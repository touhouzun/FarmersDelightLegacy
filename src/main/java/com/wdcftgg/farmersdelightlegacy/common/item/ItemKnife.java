package com.wdcftgg.farmersdelightlegacy.common.item;

import com.wdcftgg.farmersdelightlegacy.FarmersDelightLegacy;
import com.wdcftgg.farmersdelightlegacy.api.knife.ItemKnifeBase;
import com.wdcftgg.farmersdelightlegacy.common.recipe.HarvestDropRecipeManager;
import com.wdcftgg.farmersdelightlegacy.common.recipe.HuntingDropRecipeManager;
import com.wdcftgg.farmersdelightlegacy.common.recipe.KnifeItemManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ItemKnife extends ItemKnifeBase {

    private static final float knockbackReduction = 0.1F;

    public ItemKnife(Item.ToolMaterial material, double attackDamage) {
        super(material, attackDamage);
    }

    public static boolean isKnife(ItemStack stack) {
        return KnifeItemManager.isKnife(stack);
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

            event.setStrength(Math.max(0.0F, event.getOriginalStrength() - knockbackReduction));
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
