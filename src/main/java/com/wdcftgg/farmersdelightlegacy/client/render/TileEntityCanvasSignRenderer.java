package com.wdcftgg.farmersdelightlegacy.client.render;

import com.wdcftgg.farmersdelight.Tags;
import com.wdcftgg.farmersdelightlegacy.common.block.BlockCanvasStandingSign;
import com.wdcftgg.farmersdelightlegacy.common.block.BlockCanvasWallSign;
import com.wdcftgg.farmersdelightlegacy.common.tile.TileEntityCanvasSign;
import net.minecraft.block.Block;
import net.minecraft.block.BlockStandingSign;
import net.minecraft.block.BlockWallSign;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.model.ModelSign;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import org.lwjgl.opengl.GL11;

public class TileEntityCanvasSignRenderer extends TileEntitySpecialRenderer<TileEntityCanvasSign> {

    private static final ResourceLocation DEFAULT_TEXTURE = new ResourceLocation(Tags.MOD_ID, "textures/entity/signs/canvas.png");
    private final ModelSign signModel = new ModelSign();

    @Override
    public void render(TileEntityCanvasSign te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        IBlockState state = te.hasWorld() ? te.getWorld().getBlockState(te.getPos()) : null;
        Block block = state != null ? state.getBlock() : null;

        GlStateManager.pushMatrix();
        float rotation = 0.0F;
        if (block instanceof BlockCanvasStandingSign || state == null) {
            if (state != null) {
                rotation = -state.getValue(BlockStandingSign.ROTATION) * 360.0F / 16.0F;
            }
            signModel.signStick.showModel = true;
            GlStateManager.translate(x + 0.5D, y + 0.75D, z + 0.5D);
        } else {
            if (block instanceof BlockCanvasWallSign) {
                switch (state.getValue(BlockWallSign.FACING)) {
                    case NORTH:
                        rotation = 180.0F;
                        break;
                    case WEST:
                        rotation = 90.0F;
                        break;
                    case EAST:
                        rotation = -90.0F;
                        break;
                    default:
                        rotation = 0.0F;
                        break;
                }
            }
            signModel.signStick.showModel = false;
            GlStateManager.translate(x + 0.5D, y + 0.75D, z + 0.5D);
            GlStateManager.translate(0.0F, -0.3125F, -0.4375F);
        }

        GlStateManager.rotate(rotation, 0.0F, 1.0F, 0.0F);
        bindTexture(resolveTexture(block));

        GlStateManager.pushMatrix();
        GlStateManager.scale(0.6666667F, -0.6666667F, -0.6666667F);
        signModel.renderSign();
        GlStateManager.popMatrix();

        renderSignText(te);
        GlStateManager.popMatrix();
    }

    private ResourceLocation resolveTexture(Block block) {
        if (block instanceof BlockCanvasStandingSign) {
            return ((BlockCanvasStandingSign) block).getTextureLocation();
        }
        if (block instanceof BlockCanvasWallSign) {
            return ((BlockCanvasWallSign) block).getTextureLocation();
        }
        return DEFAULT_TEXTURE;
    }

    private void renderSignText(TileEntityCanvasSign te) {
        FontRenderer fontRenderer = this.getFontRenderer();
        GlStateManager.translate(0.0F, 0.33333334F, 0.046666667F);
        GlStateManager.scale(0.010416667F, -0.010416667F, 0.010416667F);
        GlStateManager.glNormal3f(0.0F, 0.0F, -0.010416667F);
        GlStateManager.depthMask(false);

        for (int line = 0; line < te.signText.length; ++line) {
            ITextComponent textComponent = te.signText[line];
            String lineText = textComponent == null ? "" : textComponent.getUnformattedText();
            if (line == te.lineBeingEdited) {
                lineText = "> " + lineText + " <";
            }
            int centeredX = -fontRenderer.getStringWidth(lineText) / 2;
            fontRenderer.drawString(lineText, centeredX, line * 10 - te.signText.length * 5, 0);
        }

        GlStateManager.depthMask(true);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glNormal3f(0.0F, 0.0F, -1.0F);
    }
}

