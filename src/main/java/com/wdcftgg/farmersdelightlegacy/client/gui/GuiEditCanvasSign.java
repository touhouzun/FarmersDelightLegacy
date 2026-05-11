package com.wdcftgg.farmersdelightlegacy.client.gui;

import com.wdcftgg.farmersdelightlegacy.FarmersDelightLegacy;
import com.wdcftgg.farmersdelightlegacy.client.render.ModelCanvasHangingSign;
import com.wdcftgg.farmersdelightlegacy.common.Configuration;
import com.wdcftgg.farmersdelightlegacy.common.block.BlockCanvasHangingSign;
import com.wdcftgg.farmersdelightlegacy.common.block.BlockCanvasStandingSign;
import com.wdcftgg.farmersdelightlegacy.common.block.BlockCanvasWallHangingSign;
import com.wdcftgg.farmersdelightlegacy.common.block.BlockCanvasWallSign;
import com.wdcftgg.farmersdelightlegacy.common.tile.TileEntityCanvasSign;
import net.minecraft.block.Block;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.model.ModelSign;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.network.play.client.CPacketUpdateSign;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;
import org.lwjgl.input.Keyboard;

import java.io.IOException;

public class GuiEditCanvasSign extends GuiScreen {

    private static final int MAX_LINES = 4;
    private static final int MAX_LINE_LENGTH = 18;
    private static final int MAX_LINE_LENGTH_HANGING = 14;
    private static final int TEXT_LINE_SPACING = 9;
    private static final int TEXT_LINE_SPACING_HANGING = 10;
    private final TileEntityCanvasSign tileSign;
    private final ModelSign signModel = new ModelSign();
    private final ModelCanvasHangingSign hangingSignModel = new ModelCanvasHangingSign();
    private int updateCounter;
    private int editLine;
    private GuiButton doneButton;

    public GuiEditCanvasSign(TileEntityCanvasSign tileSign) {
        this.tileSign = tileSign;
    }

    @Override
    public void initGui() {
        this.buttonList.clear();
        Keyboard.enableRepeatEvents(true);
        this.doneButton = this.addButton(new GuiButton(0, this.width / 2 - 100, this.height / 4 + 120, I18n.format("gui.done")));
        this.tileSign.setEditable(false);
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
        NetHandlerPlayClient netHandler = this.mc.getConnection();
        if (netHandler != null) {
            netHandler.sendPacket(new CPacketUpdateSign(this.tileSign.getPos(), this.tileSign.signText));
        }
        this.tileSign.setEditable(true);
    }

    @Override
    public void updateScreen() {
        ++this.updateCounter;
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.enabled && button.id == 0) {
            this.tileSign.markDirty();
            this.mc.displayGuiScreen(null);
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == 200) {
            this.editLine = this.editLine - 1 & 3;
        }
        if (keyCode == 208 || keyCode == 28 || keyCode == 156) {
            this.editLine = this.editLine + 1 & 3;
        }

        String lineText = this.tileSign.signText[this.editLine].getUnformattedText();
        if (keyCode == 14 && !lineText.isEmpty()) {
            lineText = lineText.substring(0, lineText.length() - 1);
        }
        if (ChatAllowedCharacters.isAllowedCharacter(typedChar) && lineText.length() < (isHangingSign() ? MAX_LINE_LENGTH_HANGING : MAX_LINE_LENGTH)) {
            lineText = lineText + typedChar;
        }
        this.tileSign.signText[this.editLine] = new TextComponentString(lineText);

        if (keyCode == 1) {
            this.actionPerformed(this.doneButton);
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRenderer, I18n.format(this.isHangingSign() ? "hanging_sign.edit" : "sign.edit"), this.width / 2, 40, 16777215);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.pushMatrix();
        if (this.isHangingSign()) {
            this.drawHangingCanvasSign();
        } else {
            this.drawCanvasSign();
        }
        GlStateManager.popMatrix();
        this.drawEditorTextOverlay();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private void drawCanvasSign() {
        GlStateManager.translate((float) this.width / 2.0F, 0.0F, 50.0F);
        GlStateManager.scale(58.0F, -58.0F, 58.0F);
        GlStateManager.translate(0.0F, -2F, 0.0F);
        GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(this.getTextureLocation());
        this.signModel.signStick.showModel = this.tileSign.getBlockType() instanceof BlockCanvasStandingSign;
        this.signModel.renderSign();
    }

    private void drawHangingCanvasSign() {
        GlStateManager.translate((float) this.width / 2.0F, 125.0F, 50.0F);
        GlStateManager.scale(78.0F, -78.0F, 78.0F);
        GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);

        if (this.tileSign.isHangingTextOnBack()) {
            GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
        }
        GlStateManager.translate(0.0F, -0.3125F, 0.0F);
        this.mc.getTextureManager().bindTexture(this.getTextureLocation());
        this.hangingSignModel.renderWall(0.0625F);
    }

    private void drawEditorTextOverlay() {
        int centerX = this.width / 2;
        int centerY = this.isHangingSign() ? 122 : 82;
        int startY = centerY - ((MAX_LINES - 1) * (this.isHangingSign() ? TEXT_LINE_SPACING_HANGING : TEXT_LINE_SPACING)) / 2;
        int textColor = this.getTextColor();
        GlStateManager.pushMatrix();
        GlStateManager.disableLighting();
        GlStateManager.disableDepth();
        for (int line = 0; line < Math.min(MAX_LINES, this.tileSign.signText.length); ++line) {
            String lineText = this.tileSign.signText[line].getUnformattedText();
            if (line == this.editLine && this.updateCounter / 6 % 2 == 0) {
                lineText = "> " + lineText + " <";
            }
            int y = startY + line * TEXT_LINE_SPACING;
            this.fontRenderer.drawString(lineText, centerX - this.fontRenderer.getStringWidth(lineText) / 2, y, textColor);
        }
        GlStateManager.enableDepth();
        GlStateManager.enableLighting();
        GlStateManager.popMatrix();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }

    private boolean isHangingSign() {
        Block block = this.tileSign.getBlockType();
        return block instanceof BlockCanvasHangingSign || block instanceof BlockCanvasWallHangingSign;
    }

    private int getTextColor() {
        return Configuration.isCanvasSignDarkBackground(this.getCanvasColorName()) ? 0xFFFFFF : 0;
    }

    private ResourceLocation getTextureLocation() {
        Block block = this.tileSign.getBlockType();
        if (block instanceof BlockCanvasStandingSign) {
            return ((BlockCanvasStandingSign) block).getTextureLocation();
        }
        if (block instanceof BlockCanvasWallSign) {
            return ((BlockCanvasWallSign) block).getTextureLocation();
        }
        if (block instanceof BlockCanvasHangingSign) {
            return ((BlockCanvasHangingSign) block).getTextureLocation();
        }
        if (block instanceof BlockCanvasWallHangingSign) {
            return ((BlockCanvasWallHangingSign) block).getTextureLocation();
        }
        return new ResourceLocation(FarmersDelightLegacy.MOD_ID, "textures/entity/signs/canvas.png");
    }

    private String getCanvasColorName() {
        Block block = this.tileSign.getBlockType();
        if (block == null || block.getRegistryName() == null) {
            return "";
        }
        String path = block.getRegistryName().getPath();
        String[] baseNames = new String[]{"canvas_sign", "canvas_wall_sign", "hanging_canvas_sign", "wall_hanging_canvas_sign"};
        for (String baseName : baseNames) {
            if (baseName.equals(path)) {
                return "";
            }
        }
        String[] suffixes = new String[]{"_canvas_sign", "_canvas_wall_sign", "_hanging_canvas_sign", "_wall_hanging_canvas_sign"};
        for (String suffix : suffixes) {
            if (path.endsWith(suffix)) {
                return path.substring(0, path.length() - suffix.length());
            }
        }
        return "";
    }
}
