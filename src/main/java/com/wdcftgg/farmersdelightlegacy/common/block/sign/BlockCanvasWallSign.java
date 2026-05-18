package com.wdcftgg.farmersdelightlegacy.common.block.sign;

import com.wdcftgg.farmersdelightlegacy.FarmersDelightLegacy;
import com.wdcftgg.farmersdelightlegacy.common.tile.TileEntityCanvasSign;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.block.BlockWallSign;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.Random;

public class BlockCanvasWallSign extends BlockWallSign {

    private final ResourceLocation textureLocation;
    private final String standingSignPath;

    public BlockCanvasWallSign(ResourceLocation textureLocation, String standingSignPath) {
        this.textureLocation = textureLocation;
        this.standingSignPath = standingSignPath;
    }

    public ResourceLocation getTextureLocation() {
        return textureLocation;
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityCanvasSign();
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        return CanvasSignInteractionHelper.openEditor(worldIn, pos, playerIn);
    }

    @Override
    public ItemStack getItem(World worldIn, BlockPos pos, net.minecraft.block.state.IBlockState state) {
        Item standingSignItem = ForgeRegistries.ITEMS.getValue(new ResourceLocation(FarmersDelightLegacy.MOD_ID, standingSignPath));
        return standingSignItem == null ? ItemStack.EMPTY : new ItemStack(standingSignItem);
    }

    @Override
    public Item getItemDropped(net.minecraft.block.state.IBlockState state, Random rand, int fortune) {
        Item standingSignItem = ForgeRegistries.ITEMS.getValue(new ResourceLocation(FarmersDelightLegacy.MOD_ID, standingSignPath));
        return standingSignItem == null ? Item.getItemFromBlock(this) : standingSignItem;
    }
}
