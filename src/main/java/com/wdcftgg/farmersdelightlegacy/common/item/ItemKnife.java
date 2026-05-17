package com.wdcftgg.farmersdelightlegacy.common.item;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.wdcftgg.farmersdelightlegacy.FarmersDelightLegacy;
import com.wdcftgg.farmersdelightlegacy.common.recipe.HarvestDropRecipeManager;
import com.wdcftgg.farmersdelightlegacy.common.recipe.HuntingDropRecipeManager;
import com.wdcftgg.farmersdelightlegacy.common.registry.ModItems;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.oredict.OreDictionary;

import java.util.Set;

public class ItemKnife extends ItemSword {

    private static final float WEB_SPEED = 15.0F;
    private static final float KNIFE_SPEED = 8.0F;
    private static final float KNOCKBACK_REDUCTION = 0.1F;
    private static final Set<Enchantment> ALLOWED_ENCHANTMENTS = Sets.newHashSet(
            Enchantments.SHARPNESS,
            Enchantments.SMITE,
            Enchantments.BANE_OF_ARTHROPODS,
            Enchantments.KNOCKBACK,
            Enchantments.FIRE_ASPECT,
            Enchantments.LOOTING
    );
    private static final Set<Enchantment> DENIED_ENCHANTMENTS = Sets.newHashSet(Enchantments.FORTUNE);
    private final double attackDamage;
    private final double attackSpeed;

    public ItemKnife(Item.ToolMaterial material, double attackDamage) {
        super(material);
        this.attackDamage = attackDamage;
        this.attackSpeed = -2.0D;
    }

    @Override
    public boolean canHarvestBlock(IBlockState blockIn) {
        return blockIn.getBlock() == Blocks.WEB;
    }

    @Override
    public float getDestroySpeed(ItemStack stack, IBlockState state) {
        Block block = state.getBlock();
        Material material = state.getMaterial();
        if (block == Blocks.WEB) {
            return WEB_SPEED;
        }

        if (block instanceof BlockCrops
                || block instanceof BlockBush
                || block == Blocks.MELON_BLOCK
                || block == Blocks.PUMPKIN
                || block == Blocks.LIT_PUMPKIN
                || material == Material.PLANTS
                || material == Material.VINE
                || material == Material.LEAVES
                || material == Material.GOURD
                || material == Material.CACTUS
                || material == Material.CLOTH
                || material == Material.CARPET) {
            return KNIFE_SPEED;
        }

        return super.getDestroySpeed(stack, state);
    }

    @Override
    public boolean onBlockDestroyed(ItemStack stack, World worldIn, IBlockState state, BlockPos pos, EntityLivingBase entityLiving) {
        if (state.getBlockHardness(worldIn, pos) != 0.0F) {
            stack.damageItem(1, entityLiving);
        }
        return true;
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        if (ALLOWED_ENCHANTMENTS.contains(enchantment)) {
            return true;
        }
        if (DENIED_ENCHANTMENTS.contains(enchantment)) {
            return false;
        }
        return enchantment.type != null && enchantment.type.canEnchantItem(this);
    }

    @Override
    public Multimap<String, AttributeModifier> getItemAttributeModifiers(EntityEquipmentSlot equipmentSlot) {
        Multimap<String, AttributeModifier> attributes = HashMultimap.create(super.getItemAttributeModifiers(equipmentSlot));
        if (equipmentSlot == EntityEquipmentSlot.MAINHAND) {
            attributes.removeAll(SharedMonsterAttributes.ATTACK_DAMAGE.getName());
            attributes.removeAll(SharedMonsterAttributes.ATTACK_SPEED.getName());
            attributes.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(),
                    new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier", this.attackDamage, 0));
            attributes.put(SharedMonsterAttributes.ATTACK_SPEED.getName(),
                    new AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon modifier", this.attackSpeed, 0));
        }
        return attributes;
    }

    public static boolean isKnife(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }

        if (stack.getItem() instanceof ItemKnife) {
            return true;
        }

        for (ItemStack oreStack : OreDictionary.getOres("toolKnife")) {
            if (oreStack.isEmpty() || oreStack.getItem() != stack.getItem()) {
                continue;
            }

            int oreMetadata = oreStack.getMetadata();
            if (oreMetadata == OreDictionary.WILDCARD_VALUE || oreMetadata == stack.getMetadata()) {
                return true;
            }
        }

        ResourceLocation itemId = ForgeRegistries.ITEMS.getKey(stack.getItem());
        return itemId != null && itemId.getPath().endsWith("_knife");
    }

    @Mod.EventBusSubscriber(modid = FarmersDelightLegacy.MOD_ID)
    public static final class KnifeEvents {

        private KnifeEvents() {
        }

        @SubscribeEvent
        public static void onKnifeKnockback(LivingKnockBackEvent event) {
            Entity attacker = event.getEntityLiving().getAttackingEntity();
            if (!(attacker instanceof EntityLivingBase)) {
                attacker = event.getEntityLiving().getRevengeTarget();
            }
            if (!(attacker instanceof EntityLivingBase)) {
                return;
            }

            ItemStack toolStack = ((EntityLivingBase) attacker).getHeldItemMainhand();
            if (!ItemKnife.isKnife(toolStack)) {
                return;
            }

            event.setStrength(Math.max(0.0F, event.getOriginalStrength() - KNOCKBACK_REDUCTION));
        }

        @SubscribeEvent
        public static void onCakeInteraction(PlayerInteractEvent.RightClickBlock event) {
            EntityPlayer player = event.getEntityPlayer();
            ItemStack toolStack = player.getHeldItem(event.getHand());
            if (!ItemKnife.isKnife(toolStack)) {
                return;
            }

            World world = event.getWorld();
            BlockPos pos = event.getPos();
            IBlockState state = world.getBlockState(pos);
            if (state.getBlock() != Blocks.CAKE) {
                return;
            }

            if (!world.isRemote) {
                int bites = state.getValue(BlockCake.BITES);
                if (bites < 6) {
                    world.setBlockState(pos, state.withProperty(BlockCake.BITES, bites + 1), 3);
                } else {
                    world.setBlockToAir(pos);
                }

                Item sliceItem = ModItems.get("cake_slice");
                if (sliceItem != null) {
                    double offset = bites * 0.1D;
                    EntityItem drop = new EntityItem(world, pos.getX() + 0.5D + offset, pos.getY() + 0.2D, pos.getZ() + 0.5D,
                            new ItemStack(sliceItem));
                    drop.motionX = -0.05D;
                    drop.motionY = 0.0D;
                    drop.motionZ = 0.0D;
                    world.spawnEntity(drop);
                }

                SoundType soundType = Blocks.WOOL.getSoundType(state, world, pos, player);
                world.playSound(null, pos, soundType.getBreakSound(), SoundCategory.PLAYERS, 0.8F, 0.8F);
                player.getCooldownTracker().setCooldown(toolStack.getItem(), 4);
            }

            player.swingArm(event.getHand());
            event.setCanceled(true);
            event.setCancellationResult(EnumActionResult.SUCCESS);
        }

        @SubscribeEvent
        public static void onLivingDrops(LivingDropsEvent event) {
            Entity source = event.getSource().getTrueSource();
            if (!(source instanceof EntityLivingBase)) {
                return;
            }

            EntityLivingBase attacker = (EntityLivingBase) source;
            ItemStack toolStack = attacker.getHeldItemMainhand();
            if (!ItemKnife.isKnife(toolStack)) {
                return;
            }

            HuntingDropRecipeManager.addDrops(event, attacker, toolStack);
        }

        @SubscribeEvent
        public static void onHarvestDrops(BlockEvent.HarvestDropsEvent event) {
            EntityPlayer player = event.getHarvester();
            if (player == null || event.isSilkTouching()) {
                return;
            }

            ItemStack toolStack = player.getHeldItemMainhand();
            if (!ItemKnife.isKnife(toolStack)) {
                return;
            }

            HarvestDropRecipeManager.addDrops(event, toolStack);
        }

    }
}
