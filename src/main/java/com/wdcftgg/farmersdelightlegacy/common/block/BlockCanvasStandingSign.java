package com.wdcftgg.farmersdelightlegacy.common.block;

import com.wdcftgg.farmersdelightlegacy.common.tile.TileEntityCanvasSign;
import net.minecraft.block.BlockStandingSign;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockCanvasStandingSign extends BlockStandingSign {

    private final ResourceLocation textureLocation;

    public BlockCanvasStandingSign(ResourceLocation textureLocation) {
        this.textureLocation = textureLocation;
    }

    public ResourceLocation getTextureLocation() {
        return textureLocation;
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityCanvasSign();
    }

    @Override
    public ItemStack getItem(World worldIn, BlockPos pos, net.minecraft.block.state.IBlockState state) {
        return new ItemStack(Item.getItemFromBlock(this));
    }

    @Override
    public Item getItemDropped(net.minecraft.block.state.IBlockState state, java.util.Random rand, int fortune) {
        return Item.getItemFromBlock(this);
    }
}
