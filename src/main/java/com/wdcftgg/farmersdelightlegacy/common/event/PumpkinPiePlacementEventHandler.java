package com.wdcftgg.farmersdelightlegacy.common.event;

import com.wdcftgg.farmersdelightlegacy.FarmersDelightLegacy;
import com.wdcftgg.farmersdelightlegacy.common.Configuration;
import com.wdcftgg.farmersdelightlegacy.common.block.BlockPie;
import com.wdcftgg.farmersdelightlegacy.common.registry.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.lang.reflect.Method;

@Mod.EventBusSubscriber(modid = FarmersDelightLegacy.MOD_ID)
public final class PumpkinPiePlacementEventHandler {

    private static final String PUMPKIN_PIE_PLACEABLE_TOOLTIP = "farmersdelight.tooltip.pumpkin_pie.placeable";
    private static final String BLOCK_ACTIVATION_METHOD_NAME = "onBlockActivated";
    private static final String BLOCK_ACTIVATION_SRG_METHOD_NAME = "func_180639_a";

    private PumpkinPiePlacementEventHandler() {
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void onPumpkinPieTooltip(ItemTooltipEvent event) {
        ItemStack itemStack = event.getItemStack();
        if (itemStack.isEmpty() || itemStack.getItem() != Items.PUMPKIN_PIE) {
            return;
        }

        event.getToolTip().add(TextFormatting.GRAY
                + new TextComponentTranslation(PUMPKIN_PIE_PLACEABLE_TOOLTIP).getFormattedText());
    }

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (event.getHand() != EnumHand.MAIN_HAND) {
            return;
        }

        EntityPlayer player = event.getEntityPlayer();
        if (player == null) {
            return;
        }

        ItemStack heldStack = player.getHeldItem(event.getHand());
        if (heldStack.isEmpty() || heldStack.getItem() != Items.PUMPKIN_PIE) {
            return;
        }

        World world = event.getWorld();
        IBlockState clickedState = world.getBlockState(event.getPos());
        if (!player.isSneaking() && (Configuration.enablePumpkinPieSneakToPlace || hasBlockActivation(clickedState.getBlock()))) {
            return;
        }

        BlockPos placePos = getPumpkinPiePlacePos(world, event.getPos(), event.getFace());
        if (placePos == null || !player.canPlayerEdit(placePos, event.getFace(), heldStack)) {
            return;
        }

        IBlockState placedState = ModBlocks.pumpkinPie.getDefaultState()
                .withProperty(BlockPie.FACING, player.getHorizontalFacing());
        if (!world.mayPlace(ModBlocks.pumpkinPie, placePos, false, event.getFace(), player)
                || !ModBlocks.pumpkinPie.canPlaceBlockAt(world, placePos)) {
            return;
        }

        if (!world.isRemote) {
            world.setBlockState(placePos, placedState, 11);
            SoundType soundType = placedState.getBlock().getSoundType(placedState, world, placePos, player);
            world.playSound(null, placePos, soundType.getPlaceSound(), SoundCategory.BLOCKS,
                    (soundType.getVolume() + 1.0F) / 2.0F, soundType.getPitch() * 0.8F);
            if (!player.capabilities.isCreativeMode) {
                heldStack.shrink(1);
            }
        }

        event.setCanceled(true);
        event.setCancellationResult(EnumActionResult.SUCCESS);
    }

    @SubscribeEvent
    public static void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
        if (Configuration.enablePumpkinPieDirectEating || event.getHand() != EnumHand.MAIN_HAND) {
            return;
        }

        EntityPlayer player = event.getEntityPlayer();
        if (player == null) {
            return;
        }

        ItemStack heldStack = player.getHeldItem(event.getHand());
        if (heldStack.isEmpty() || heldStack.getItem() != Items.PUMPKIN_PIE) {
            return;
        }

        event.setCanceled(true);
        event.setCancellationResult(EnumActionResult.FAIL);
    }

    private static BlockPos getPumpkinPiePlacePos(World world, BlockPos clickedPos, EnumFacing clickedFace) {
        if (clickedFace == null) {
            return null;
        }
        IBlockState clickedState = world.getBlockState(clickedPos);
        Block clickedBlock = clickedState.getBlock();
        return clickedBlock.isReplaceable(world, clickedPos) ? clickedPos : clickedPos.offset(clickedFace);
    }

    private static boolean hasBlockActivation(Block block) {
        Class<?> blockClass = block.getClass();
        while (blockClass != Block.class) {
            if (declaresBlockActivation(blockClass, BLOCK_ACTIVATION_METHOD_NAME)
                    || declaresBlockActivation(blockClass, BLOCK_ACTIVATION_SRG_METHOD_NAME)) {
                return true;
            }
            blockClass = blockClass.getSuperclass();
        }
        return false;
    }

    private static boolean declaresBlockActivation(Class<?> blockClass, String methodName) {
        try {
            Method activationMethod = blockClass.getDeclaredMethod(methodName, World.class, BlockPos.class,
                    IBlockState.class, EntityPlayer.class, EnumHand.class, EnumFacing.class,
                    float.class, float.class, float.class);
            return activationMethod != null;
        } catch (NoSuchMethodException exception) {
            return false;
        }
    }
}
