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
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionUtils;
import net.minecraft.profiler.Profiler;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.VillageCollection;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.GameType;
import net.minecraft.world.World;
import net.minecraft.world.WorldProviderSurface;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.SaveHandlerMP;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

import java.lang.reflect.Field;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

final class JeiPreviewWorld extends World {

    private static final int PREVIEW_ENTITY_ID = -2000000000;
    private static final net.minecraft.util.ResourceLocation PARTICLE_TEXTURES = new net.minecraft.util.ResourceLocation("textures/particle/particles.png");
    private static final Field PARTICLE_LAYERS_FIELD = findParticleLayersField();
    private static final Field PARTICLE_QUEUE_FIELD = findParticleQueueField();
    private static final Field POTION_EFFECTS_FIELD = findEntityLivingBaseDataParameterField("POTION_EFFECTS", "field_184633_f");
    private static final Field HIDE_PARTICLES_FIELD = findEntityLivingBaseDataParameterField("HIDE_PARTICLES", "field_184634_g");
    private static long nextParticleDebugTime;
    private static int spawnedParticleDebugCount;
    private static int particleUpdateDebugCount;
    private static int particleProjectionDebugCount;
    private static int particleGlStateDebugCount;
    private static int particleDrawDebugCount;

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
        int queueBeforeEntityUpdate = countParticleQueue();
        int layersBeforeEntityUpdate = countCurrentLayerTotal();
        printParticleUpdateDebug(queueBeforeEntityUpdate, layersBeforeEntityUpdate,
                "before entity update: entity=" + describeEntity(entity)
                        + ", queue=" + queueBeforeEntityUpdate
                        + ", layers=" + describeCurrentLayers());
        this.updateEntityWithOptionalForce(entity, true);
        syncPotionMetadata(entity);
        int queueAfterEntityUpdate = countParticleQueue();
        int layersAfterEntityUpdate = countCurrentLayerTotal();
        printParticleUpdateDebug(queueAfterEntityUpdate, layersAfterEntityUpdate,
                "after entity update: entityAlive=" + !entity.isDead
                        + ", queue=" + queueAfterEntityUpdate
                        + ", layers=" + describeCurrentLayers());
        this.particleManager.updateEffects();
        int queueAfterParticleUpdate = countParticleQueue();
        int layersAfterParticleUpdate = countCurrentLayerTotal();
        printParticleUpdateDebug(queueAfterParticleUpdate, layersAfterParticleUpdate,
                "after particle update: queue=" + queueAfterParticleUpdate
                        + ", layers=" + describeCurrentLayers()
                        + ", samples=" + describeParticleSamples());
        this.setTotalWorldTime(this.getTotalWorldTime() + 1L);
        this.setWorldTime(this.getWorldTime() + 1L);
    }

    void renderParticles(Entity cameraEntity, float partialTicks) {
        ArrayDeque<Particle>[][] particleLayers = getParticleLayers();
        if (particleLayers == null) {
            printParticleDebug("render fallback: fxLayers reflection unavailable, using ParticleManager vanilla render. camera="
                    + describeEntity(cameraEntity));
            this.particleManager.renderParticles(cameraEntity, partialTicks);
            this.particleManager.renderLitParticles(cameraEntity, partialTicks);
            return;
        }

        printParticleLayerDebug("render custom: camera=" + describeEntity(cameraEntity)
                + ", total=" + countParticles(particleLayers)
                + ", queue=" + countParticleQueue()
                + ", layers=" + describeParticleLayers(particleLayers)
                + ", samples=" + describeParticleSamples());
        printParticleProjectionDebug(particleLayers, cameraEntity, partialTicks);
        printParticleGlStateDebug("before setup");

        Particle.interpPosX = cameraEntity.lastTickPosX + (cameraEntity.posX - cameraEntity.lastTickPosX) * partialTicks;
        Particle.interpPosY = cameraEntity.lastTickPosY + (cameraEntity.posY - cameraEntity.lastTickPosY) * partialTicks;
        Particle.interpPosZ = cameraEntity.lastTickPosZ + (cameraEntity.posZ - cameraEntity.lastTickPosZ) * partialTicks;

        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.alphaFunc(516, 0.003921569F);
        GlStateManager.disableDepth();
        GlStateManager.disableCull();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        printParticleGlStateDebug("after setup");

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        int drawnParticles = 0;
        drawnParticles += renderParticleLayers(particleLayers, cameraEntity, partialTicks, tessellator, buffer, false);
        drawnParticles += renderParticleLayers(particleLayers, cameraEntity, partialTicks, tessellator, buffer, true);
        printParticleDrawDebug("drawnParticles=" + drawnParticles + ", total=" + countParticles(particleLayers));

        GlStateManager.depthMask(true);
        GlStateManager.enableDepth();
        GlStateManager.enableCull();
        GlStateManager.disableBlend();
        GlStateManager.alphaFunc(516, 0.1F);
    }

    private int renderParticleLayers(ArrayDeque<Particle>[][] particleLayers, Entity cameraEntity, float partialTicks,
                                     Tessellator tessellator, BufferBuilder buffer, boolean depthMask) {
        int drawnParticles = 0;
        GlStateManager.depthMask(depthMask);
        for (int layer = 0; layer < 3; layer++) {
            bindParticleTexture(layer);
            for (Particle particle : particleLayers[layer][depthMask ? 1 : 0]) {
                buffer.begin(7, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
                particle.renderParticle(buffer, cameraEntity, partialTicks,
                        1.0F, -1.0F, 0.0F, 0.0F, 0.0F);
                tessellator.draw();
                drawnParticles++;
            }
        }
        return drawnParticles;
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
            printParticleDebug("render fallback: cannot access ParticleManager.fxLayers: " + exception.getClass().getName()
                    + ": " + exception.getMessage());
            return null;
        }
    }

    private static Field findParticleLayersField() {
        try {
            Field field = ParticleManager.class.getDeclaredField("fxLayers");
            field.setAccessible(true);
            printParticleDebug("reflection ok: ParticleManager.fxLayers -> " + field.getType().getName());
            return field;
        } catch (NoSuchFieldException exception) {
            printParticleDebug("reflection failed: ParticleManager.fxLayers not found: " + exception.getMessage());
            return null;
        }
    }

    private static Field findParticleQueueField() {
        try {
            Field field = ParticleManager.class.getDeclaredField("queue");
            field.setAccessible(true);
            printParticleDebug("reflection ok: ParticleManager.queue -> " + field.getType().getName());
            return field;
        } catch (NoSuchFieldException exception) {
            printParticleDebug("reflection failed: ParticleManager.queue not found: " + exception.getMessage());
            return null;
        }
    }

    private static Field findEntityLivingBaseDataParameterField(String deobfuscatedName, String obfuscatedName) {
        try {
            Field field = EntityLivingBase.class.getDeclaredField(deobfuscatedName);
            field.setAccessible(true);
            printParticleDebug("reflection ok: EntityLivingBase." + deobfuscatedName + " -> " + field.getType().getName());
            return field;
        } catch (NoSuchFieldException ignored) {
            try {
                Field field = EntityLivingBase.class.getDeclaredField(obfuscatedName);
                field.setAccessible(true);
                printParticleDebug("reflection ok: EntityLivingBase." + obfuscatedName + " -> " + field.getType().getName());
                return field;
            } catch (NoSuchFieldException exception) {
                printParticleDebug("reflection failed: EntityLivingBase data parameter not found: "
                        + deobfuscatedName + '/' + obfuscatedName + ": " + exception.getMessage());
                return null;
            }
        }
    }

    private static int countParticles(ArrayDeque<Particle>[][] particleLayers) {
        int total = 0;
        for (ArrayDeque<Particle>[] particleLayer : particleLayers) {
            for (ArrayDeque<Particle> particles : particleLayer) {
                total += particles.size();
            }
        }
        return total;
    }

    private static String describeParticleLayers(ArrayDeque<Particle>[][] particleLayers) {
        StringBuilder builder = new StringBuilder();
        for (int layer = 0; layer < particleLayers.length; layer++) {
            if (layer > 0) {
                builder.append(" | ");
            }
            builder.append(layer).append(':');
            for (int depthIndex = 0; depthIndex < particleLayers[layer].length; depthIndex++) {
                if (depthIndex > 0) {
                    builder.append('/');
                }
                builder.append(particleLayers[layer][depthIndex].size());
            }
        }
        return builder.toString();
    }

    private static String describeEntity(Entity entity) {
        if (entity == null) {
            return "null";
        }
        return entity.getClass().getName()
                + " pos=" + entity.posX + ',' + entity.posY + ',' + entity.posZ
                + " last=" + entity.lastTickPosX + ',' + entity.lastTickPosY + ',' + entity.lastTickPosZ
                + " yaw=" + entity.rotationYaw + " pitch=" + entity.rotationPitch;
    }

    private static void printParticleLayerDebug(String message) {
        long time = Minecraft.getSystemTime();
        if (time >= nextParticleDebugTime) {
            nextParticleDebugTime = time + 1000L;
            printParticleDebug(message);
        }
    }

    private static void printSpawnParticleDebug(String message) {
        if (spawnedParticleDebugCount < 80) {
            spawnedParticleDebugCount++;
            printParticleDebug("spawn[" + spawnedParticleDebugCount + "]: " + message);
        }
    }

    private static void printParticleUpdateDebug(int queueCount, int layerTotal, String message) {
        if (particleUpdateDebugCount < 160 || queueCount > 0 || layerTotal > 0) {
            particleUpdateDebugCount++;
            printParticleDebug("update[" + particleUpdateDebugCount + "]: " + message);
        }
    }

    private static void printParticleDebug(String message) {
        System.out.println("[FarmersDelightLegacy][HuntingDropJEI][ParticleDebug] " + message);
    }

    private static void printParticleProjectionDebug(ArrayDeque<Particle>[][] particleLayers, Entity cameraEntity, float partialTicks) {
        if (particleProjectionDebugCount >= 20 || countParticles(particleLayers) <= 0) {
            return;
        }
        Particle particle = findFirstParticle(particleLayers);
        if (particle == null) {
            return;
        }
        particleProjectionDebugCount++;
        FloatBuffer modelView = BufferUtils.createFloatBuffer(16);
        FloatBuffer projection = BufferUtils.createFloatBuffer(16);
        IntBuffer viewport = BufferUtils.createIntBuffer(16);
        FloatBuffer screen = BufferUtils.createFloatBuffer(3);
        GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, modelView);
        GL11.glGetFloat(GL11.GL_PROJECTION_MATRIX, projection);
        GL11.glGetInteger(GL11.GL_VIEWPORT, viewport);
        float worldX = (float) (getParticleX(particle, partialTicks) - (cameraEntity.lastTickPosX + (cameraEntity.posX - cameraEntity.lastTickPosX) * partialTicks));
        float worldY = (float) (getParticleY(particle, partialTicks) - (cameraEntity.lastTickPosY + (cameraEntity.posY - cameraEntity.lastTickPosY) * partialTicks));
        float worldZ = (float) (getParticleZ(particle, partialTicks) - (cameraEntity.lastTickPosZ + (cameraEntity.posZ - cameraEntity.lastTickPosZ) * partialTicks));
        boolean projected = GLU.gluProject(worldX, worldY, worldZ, modelView, projection, viewport, screen);
        printParticleDebug("projection[" + particleProjectionDebugCount + "]: projected=" + projected
                + ", relative=" + worldX + ',' + worldY + ',' + worldZ
                + ", screen=" + screen.get(0) + ',' + screen.get(1) + ',' + screen.get(2)
                + ", viewport=" + viewport.get(0) + ',' + viewport.get(1) + ',' + viewport.get(2) + ',' + viewport.get(3)
                + ", particle=" + particle);
    }

    private static void printParticleGlStateDebug(String stage) {
        if (particleGlStateDebugCount >= 20) {
            return;
        }
        particleGlStateDebugCount++;
        IntBuffer scissorBox = BufferUtils.createIntBuffer(16);
        GL11.glGetInteger(GL11.GL_SCISSOR_BOX, scissorBox);
        printParticleDebug("glState[" + particleGlStateDebugCount + "]: texture2D=" + GL11.glIsEnabled(GL11.GL_TEXTURE_2D)
                + ", blend=" + GL11.glIsEnabled(GL11.GL_BLEND)
                + ", depthTest=" + GL11.glIsEnabled(GL11.GL_DEPTH_TEST)
                + ", cullFace=" + GL11.glIsEnabled(GL11.GL_CULL_FACE)
                + ", alphaTest=" + GL11.glIsEnabled(GL11.GL_ALPHA_TEST)
                + ", lighting=" + GL11.glIsEnabled(GL11.GL_LIGHTING)
                + ", scissorTest=" + GL11.glIsEnabled(GL11.GL_SCISSOR_TEST)
                + ", scissorBox=" + scissorBox.get(0) + ',' + scissorBox.get(1) + ',' + scissorBox.get(2) + ',' + scissorBox.get(3)
                + ", textureBinding=" + GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D)
                + ", colorMask=" + GL11.glGetBoolean(GL11.GL_COLOR_WRITEMASK)
                + ", stage=" + stage);
    }

    private static void printParticleDrawDebug(String message) {
        if (particleDrawDebugCount >= 40) {
            return;
        }
        particleDrawDebugCount++;
        printParticleDebug("draw[" + particleDrawDebugCount + "]: " + message);
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
            printParticleDebug("potionMetadata: effects=" + potionEffects.size()
                    + ", color=" + potionColor
                    + ", hidden=" + areAllPotionParticlesHidden(potionEffects));
        } catch (IllegalAccessException exception) {
            printParticleDebug("potionMetadata failed: " + exception.getClass().getName() + ": " + exception.getMessage());
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

    private static Particle findFirstParticle(ArrayDeque<Particle>[][] particleLayers) {
        for (ArrayDeque<Particle>[] particleLayer : particleLayers) {
            for (ArrayDeque<Particle> particles : particleLayer) {
                if (!particles.isEmpty()) {
                    return particles.peekFirst();
                }
            }
        }
        return null;
    }

    private static double getParticleX(Particle particle, float partialTicks) {
        String text = particle.toString();
        return parseParticleCoordinate(text, 0);
    }

    private static double getParticleY(Particle particle, float partialTicks) {
        String text = particle.toString();
        return parseParticleCoordinate(text, 1);
    }

    private static double getParticleZ(Particle particle, float partialTicks) {
        String text = particle.toString();
        return parseParticleCoordinate(text, 2);
    }

    private static double parseParticleCoordinate(String text, int coordinateIndex) {
        int start = text.indexOf("Pos (");
        if (start < 0) {
            return 0.0D;
        }
        int end = text.indexOf(')', start);
        if (end < 0) {
            return 0.0D;
        }
        String[] coordinates = text.substring(start + 5, end).split(",");
        if (coordinateIndex >= coordinates.length) {
            return 0.0D;
        }
        try {
            return Double.parseDouble(coordinates[coordinateIndex]);
        } catch (NumberFormatException exception) {
            return 0.0D;
        }
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
        Particle particle = this.particleManager.spawnEffectParticle(particleType.getParticleID(), xCoord, yCoord, zCoord,
                xSpeed, ySpeed, zSpeed, parameters);
        printSpawnParticleDebug("type=" + particleType + ", particle=" + describeParticle(particle)
                + ", pos=" + xCoord + ',' + yCoord + ',' + zCoord
                + ", speed=" + xSpeed + ',' + ySpeed + ',' + zSpeed
                + ", queue=" + countParticleQueue()
                + ", layerTotal=" + describeCurrentLayerTotal());
    }

    @Override
    public void spawnParticle(EnumParticleTypes particleType, boolean ignoreRange, double xCoord, double yCoord,
                              double zCoord, double xSpeed, double ySpeed, double zSpeed, int... parameters) {
        this.spawnParticle(particleType, xCoord, yCoord, zCoord, xSpeed, ySpeed, zSpeed, parameters);
    }

    @Override
    public void spawnAlwaysVisibleParticle(int particleId, double x, double y, double z,
                                           double xSpeed, double ySpeed, double zSpeed, int... parameters) {
        Particle particle = this.particleManager.spawnEffectParticle(particleId, x, y, z, xSpeed, ySpeed, zSpeed, parameters);
        printSpawnParticleDebug("alwaysVisible id=" + particleId + ", particle=" + describeParticle(particle)
                + ", pos=" + x + ',' + y + ',' + z
                + ", speed=" + xSpeed + ',' + ySpeed + ',' + zSpeed
                + ", queue=" + countParticleQueue()
                + ", layerTotal=" + describeCurrentLayerTotal());
    }

    @SuppressWarnings("unchecked")
    private int countParticleQueue() {
        if (PARTICLE_QUEUE_FIELD == null) {
            return -1;
        }
        try {
            return ((java.util.Queue<Particle>) PARTICLE_QUEUE_FIELD.get(this.particleManager)).size();
        } catch (IllegalAccessException exception) {
            printParticleDebug("queue access failed: " + exception.getClass().getName() + ": " + exception.getMessage());
            return -1;
        }
    }

    private String describeCurrentLayers() {
        ArrayDeque<Particle>[][] particleLayers = getParticleLayers();
        if (particleLayers == null) {
            return "unavailable";
        }
        return countParticles(particleLayers) + " [" + describeParticleLayers(particleLayers) + ']';
    }

    private int countCurrentLayerTotal() {
        ArrayDeque<Particle>[][] particleLayers = getParticleLayers();
        if (particleLayers == null) {
            return -1;
        }
        return countParticles(particleLayers);
    }

    private String describeParticleSamples() {
        ArrayDeque<Particle>[][] particleLayers = getParticleLayers();
        if (particleLayers == null) {
            return "unavailable";
        }
        StringBuilder builder = new StringBuilder();
        int sampleCount = 0;
        for (int layer = 0; layer < particleLayers.length && sampleCount < 6; layer++) {
            for (int depthIndex = 0; depthIndex < particleLayers[layer].length && sampleCount < 6; depthIndex++) {
                for (Particle particle : particleLayers[layer][depthIndex]) {
                    if (sampleCount > 0) {
                        builder.append(" || ");
                    }
                    builder.append("L").append(layer).append('/').append(depthIndex).append(' ')
                            .append(particle);
                    sampleCount++;
                    if (sampleCount >= 6) {
                        break;
                    }
                }
            }
        }
        return builder.length() == 0 ? "none" : builder.toString();
    }

    private String describeCurrentLayerTotal() {
        ArrayDeque<Particle>[][] particleLayers = getParticleLayers();
        if (particleLayers == null) {
            return "unavailable";
        }
        return String.valueOf(countParticles(particleLayers));
    }

    private static String describeParticle(Particle particle) {
        if (particle == null) {
            return "null";
        }
        return particle.getClass().getName();
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
