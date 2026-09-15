package com.wdcftgg.farmersdelightlegacy.client.jei;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionUtils;
import net.minecraft.profiler.Profiler;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.VillageCollection;
import net.minecraft.world.*;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.SaveHandlerMP;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;

import java.lang.reflect.Field;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

final class JeiPreviewWorld extends World {

    private static final int PREVIEW_ENTITY_ID = -2000000000;
    private static final net.minecraft.util.ResourceLocation PARTICLE_TEXTURES = new net.minecraft.util.ResourceLocation("textures/particle/particles.png");
    private static final Field PARTICLE_LAYERS_FIELD = findParticleLayersField();
    private static final Field POTION_EFFECTS_FIELD = findEntityLivingBaseDataParameterField("POTION_EFFECTS", "field_184633_f");
    private static final Field HIDE_PARTICLES_FIELD = findEntityLivingBaseDataParameterField("HIDE_PARTICLES", "field_184634_g");

    private final Map<Long, Chunk> chunks = new HashMap<>();
    private IChunkProvider previewChunkProvider;
    private final ParticleManager particleManager;

    private JeiPreviewWorld(Minecraft minecraft) {
        super(new SaveHandlerMP(), createWorldInfo(minecraft), new WorldProviderSurface(), new Profiler(), true);
        this.mapStorage = new MapStorage(this.saveHandler);
        this.perWorldStorage = new MapStorage(this.saveHandler);
        this.worldScoreboard = new Scoreboard();
        this.villageCollection = new VillageCollection(this);
        this.lootTable = minecraft.world == null ? null : minecraft.world.getLootTableManager();
        this.particleManager = new ParticleManager(this, minecraft.getTextureManager());
        this.provider.setWorld(this);
        this.provider.setDimension(minecraft.world == null ? 0 : minecraft.world.provider.getDimension());
        this.chunkProvider = createChunkProvider();
        this.calculateInitialSkylight();
        this.calculateInitialWeather();
        this.getWorldBorder().setSize(1024);
    }

    static JeiPreviewWorld create(Minecraft minecraft) {
        return new JeiPreviewWorld(minecraft);
    }

    void resetTo(Minecraft minecraft) {
        long totalTime = minecraft.world == null ? Minecraft.getSystemTime() / 50L : minecraft.world.getTotalWorldTime();
        long worldTime = minecraft.world == null ? totalTime : minecraft.world.getWorldTime();
        this.setTotalWorldTime(totalTime);
        this.setWorldTime(worldTime);
        this.loadedEntityList.clear();
        this.unloadedEntityList.clear();
        this.weatherEffects.clear();
        this.playerEntities.clear();
        this.particleManager.clearEffects(this);
    }

    void addPreviewEntity(Entity entity) {
        entity.setWorld(this);
        entity.setEntityId(PREVIEW_ENTITY_ID);
        this.loadedEntityList.clear();
        this.entitiesById.removeObject(PREVIEW_ENTITY_ID);
        this.loadedEntityList.add(entity);
        this.entitiesById.addKey(PREVIEW_ENTITY_ID, entity);
        entity.onAddedToWorld();
        MinecraftForge.EVENT_BUS.post(new EntityJoinWorldEvent(entity, this));
    }

    void updatePreviewEntity(Entity entity) {
        syncPotionMetadata(entity);
        this.updateEntityWithOptionalForce(entity, true);
        syncPotionMetadata(entity);
        this.particleManager.updateEffects();
        this.setTotalWorldTime(this.getTotalWorldTime() + 1L);
        this.setWorldTime(this.getWorldTime() + 1L);
    }

    void renderParticles(Entity cameraEntity, float partialTicks) {
        ArrayDeque<Particle>[][] particleLayers = getParticleLayers();
        if (particleLayers == null) {
            this.particleManager.renderParticles(cameraEntity, partialTicks);
            this.particleManager.renderLitParticles(cameraEntity, partialTicks);
            return;
        }

        Particle.interpPosX = cameraEntity.lastTickPosX + (cameraEntity.posX - cameraEntity.lastTickPosX) * partialTicks;
        Particle.interpPosY = cameraEntity.lastTickPosY + (cameraEntity.posY - cameraEntity.lastTickPosY) * partialTicks;
        Particle.interpPosZ = cameraEntity.lastTickPosZ + (cameraEntity.posZ - cameraEntity.lastTickPosZ) * partialTicks;

        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.alphaFunc(516, 0.003921569F);
        GlStateManager.disableDepth();
        GlStateManager.disableCull();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        renderParticleLayers(particleLayers, cameraEntity, partialTicks, tessellator, buffer, false);
        renderParticleLayers(particleLayers, cameraEntity, partialTicks, tessellator, buffer, true);

        GlStateManager.depthMask(true);
        GlStateManager.enableDepth();
        GlStateManager.enableCull();
        GlStateManager.disableBlend();
        GlStateManager.alphaFunc(516, 0.1F);
    }

    private void renderParticleLayers(ArrayDeque<Particle>[][] particleLayers, Entity cameraEntity, float partialTicks,
                                     Tessellator tessellator, BufferBuilder buffer, boolean depthMask) {
        GlStateManager.depthMask(depthMask);
        for (int layer = 0; layer < 3; layer++) {
            bindParticleTexture(layer);
            for (Particle particle : particleLayers[layer][depthMask ? 1 : 0]) {
                buffer.begin(7, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
                particle.renderParticle(buffer, cameraEntity, partialTicks,
                        1.0F, -1.0F, 0.0F, 0.0F, 0.0F);
                tessellator.draw();
            }
        }
    }

    private void bindParticleTexture(int layer) {
        if (layer == 1) {
            Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        } else {
            Minecraft.getMinecraft().getTextureManager().bindTexture(PARTICLE_TEXTURES);
        }
    }

    @SuppressWarnings("unchecked")
    private ArrayDeque<Particle>[][] getParticleLayers() {
        if (PARTICLE_LAYERS_FIELD == null) {
            return null;
        }
        try {
            return (ArrayDeque<Particle>[][]) PARTICLE_LAYERS_FIELD.get(this.particleManager);
        } catch (IllegalAccessException exception) {
            return null;
        }
    }

    private static Field findParticleLayersField() {
        try {
            Field field = ParticleManager.class.getDeclaredField("fxLayers");
            field.setAccessible(true);
            return field;
        } catch (NoSuchFieldException exception) {
            return null;
        }
    }

    private static Field findEntityLivingBaseDataParameterField(String deobfuscatedName, String obfuscatedName) {
        try {
            Field field = EntityLivingBase.class.getDeclaredField(deobfuscatedName);
            field.setAccessible(true);
            return field;
        } catch (NoSuchFieldException ignored) {
            try {
                Field field = EntityLivingBase.class.getDeclaredField(obfuscatedName);
                field.setAccessible(true);
                return field;
            } catch (NoSuchFieldException exception) {
                return null;
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static void syncPotionMetadata(Entity entity) {
        if (!(entity instanceof EntityLivingBase) || POTION_EFFECTS_FIELD == null || HIDE_PARTICLES_FIELD == null) {
            return;
        }
        EntityLivingBase livingEntity = (EntityLivingBase) entity;
        Collection<PotionEffect> potionEffects = livingEntity.getActivePotionEffects();
        try {
            DataParameter<Integer> potionEffectsParameter = (DataParameter<Integer>) POTION_EFFECTS_FIELD.get(null);
            DataParameter<Boolean> hideParticlesParameter = (DataParameter<Boolean>) HIDE_PARTICLES_FIELD.get(null);
            if (potionEffects.isEmpty()) {
                livingEntity.getDataManager().set(hideParticlesParameter, Boolean.FALSE);
                livingEntity.getDataManager().set(potionEffectsParameter, Integer.valueOf(0));
                return;
            }
            int potionColor = PotionUtils.getPotionColorFromEffectList(potionEffects);
            livingEntity.getDataManager().set(hideParticlesParameter, Boolean.valueOf(areAllPotionParticlesHidden(potionEffects)));
            livingEntity.getDataManager().set(potionEffectsParameter, Integer.valueOf(potionColor));
        } catch (IllegalAccessException exception) {
        }
    }

    private static boolean areAllPotionParticlesHidden(Collection<PotionEffect> potionEffects) {
        for (PotionEffect potionEffect : potionEffects) {
            if (potionEffect.doesShowParticles()) {
                return false;
            }
        }
        return true;
    }


    @Override
    protected IChunkProvider createChunkProvider() {
        if (this.previewChunkProvider == null) {
            this.previewChunkProvider = new PreviewChunkProvider();
        }
        return this.previewChunkProvider;
    }

    @Override
    protected boolean isChunkLoaded(int x, int z, boolean allowEmpty) {
        return true;
    }

    @Override
    public IBlockState getBlockState(BlockPos pos) {
        return Blocks.AIR.getDefaultState();
    }

    @Override
    public boolean isAirBlock(BlockPos pos) {
        return true;
    }

    @Override
    public int getCombinedLight(BlockPos pos, int lightValue) {
        return 15728880;
    }

    @Override
    public int getLight(BlockPos pos) {
        return 15;
    }

    @Override
    public int getLight(BlockPos pos, boolean checkNeighbors) {
        return 15;
    }

    @Override
    public int getLightFromNeighbors(BlockPos pos) {
        return 15;
    }

    @Override
    public void playSound(EntityPlayer player, BlockPos pos, SoundEvent soundIn, SoundCategory category, float volume, float pitch) {
    }

    @Override
    public void playSound(EntityPlayer player, double x, double y, double z, SoundEvent soundIn, SoundCategory category, float volume, float pitch) {
    }

    @Override
    public void playSound(double x, double y, double z, SoundEvent soundIn, SoundCategory category, float volume, float pitch, boolean distanceDelay) {
    }

    @Override
    public void spawnParticle(EnumParticleTypes particleType, double xCoord, double yCoord, double zCoord,
                              double xSpeed, double ySpeed, double zSpeed, int... parameters) {
        this.particleManager.spawnEffectParticle(particleType.getParticleID(), xCoord, yCoord, zCoord,
                xSpeed, ySpeed, zSpeed, parameters);
    }

    @Override
    public void spawnParticle(EnumParticleTypes particleType, boolean ignoreRange, double xCoord, double yCoord,
                              double zCoord, double xSpeed, double ySpeed, double zSpeed, int... parameters) {
        this.spawnParticle(particleType, xCoord, yCoord, zCoord, xSpeed, ySpeed, zSpeed, parameters);
    }

    @Override
    public void spawnAlwaysVisibleParticle(int particleId, double x, double y, double z,
                                           double xSpeed, double ySpeed, double zSpeed, int... parameters) {
        this.particleManager.spawnEffectParticle(particleId, x, y, z, xSpeed, ySpeed, zSpeed, parameters);
    }

    @Override
    public boolean spawnEntity(Entity entityIn) {
        entityIn.setWorld(this);
        return this.loadedEntityList.add(entityIn);
    }

    @Override
    public boolean addWeatherEffect(Entity entityIn) {
        entityIn.setWorld(this);
        return this.weatherEffects.add(entityIn);
    }

    @Override
    public void removeEntity(Entity entityIn) {
        this.loadedEntityList.remove(entityIn);
        this.unloadedEntityList.remove(entityIn);
        this.entitiesById.removeObject(entityIn.getEntityId());
        entityIn.onRemovedFromWorld();
    }

    @Override
    public void removeEntityDangerously(Entity entityIn) {
        removeEntity(entityIn);
    }

    @Override
    public void makeFireworks(double x, double y, double z, double motionX, double motionY, double motionZ, NBTTagCompound comp) {
    }

    private Chunk getPreviewChunk(int x, int z) {
        long key = (((long) x) << 32) ^ (z & 0xffffffffL);
        Chunk chunk = this.chunks.get(key);
        if (chunk == null) {
            chunk = new Chunk(this, x, z);
            chunk.markLoaded(true);
            chunk.setTerrainPopulated(true);
            chunk.setLightPopulated(true);
            this.chunks.put(key, chunk);
        }
        return chunk;
    }

    private Chunk getLoadedPreviewChunk(int x, int z) {
        long key = (((long) x) << 32) ^ (z & 0xffffffffL);
        return this.chunks.get(key);
    }

    private static WorldInfo createWorldInfo(Minecraft minecraft) {
        WorldSettings settings = new WorldSettings(0L, GameType.CREATIVE, false, false, WorldType.FLAT);
        WorldInfo worldInfo = new WorldInfo(settings, "FarmersDelightJeiPreview");
        worldInfo.setDifficulty(minecraft.world == null ? EnumDifficulty.NORMAL : minecraft.world.getDifficulty());
        worldInfo.setDifficultyLocked(false);
        return worldInfo;
    }

    private final class PreviewChunkProvider implements IChunkProvider {
        @Override
        public Chunk getLoadedChunk(int x, int z) {
            return getLoadedPreviewChunk(x, z);
        }

        @Override
        public Chunk provideChunk(int x, int z) {
            return getPreviewChunk(x, z);
        }

        @Override
        public boolean tick() {
            return false;
        }

        @Override
        public String makeString() {
            return "JeiPreviewWorld";
        }

        @Override
        public boolean isChunkGeneratedAt(int x, int z) {
            return getLoadedPreviewChunk(x, z) != null;
        }
    }

}
