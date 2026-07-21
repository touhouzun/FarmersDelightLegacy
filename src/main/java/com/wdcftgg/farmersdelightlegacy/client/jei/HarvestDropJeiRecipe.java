package com.wdcftgg.farmersdelightlegacy.client.jei;

import com.google.common.collect.ImmutableList;
import com.wdcftgg.farmersdelightlegacy.FarmersDelightLegacy;
import com.wdcftgg.farmersdelightlegacy.api.recipe.knife.HuntingDropOutput;
import com.wdcftgg.farmersdelightlegacy.common.recipe.manager.HarvestDropRecipeManager;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import org.lwjgl.input.Mouse;

import javax.annotation.Nonnull;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public final class HarvestDropJeiRecipe implements IRecipeWrapper {

    private static final String fluidloggedPreviewBlockAccessClassName =
            "com.wdcftgg.farmersdelightlegacy.client.compat.fluidlogged.FluidloggedPreviewBlockAccess";
    private static final int blockPreviewX = 28;
    private static final int blockPreviewY = 36;
    private static final int blockTooltipX = 18;
    private static final int blockTooltipY = 8;
    private static final int blockTooltipWidth = 36;
    private static final int blockTooltipHeight = 56;
    private static final float blockPreviewMaximumWidth = 36.0F;
    private static final float blockPreviewMaximumHeight = 42.0F;
    private static final float blockPreviewMaximumDepth = 36.0F;
    private static final float blockDefaultYawDegrees = 225.0F;
    private static final float blockDragDegreesPerPixel = 2.0F;
    private static final float blockDefaultScale = 24.0F;
    private static final int outputGridX = 98;
    private static final int outputGridY = 19;
    private static final int outputGridColumns = 3;
    private static final int outputGridMaxSlots = 9;
    private static final int outputStep = 19;
    private static final long displayTextIntervalTicks = 100L;
    private static final int outputClockX = outputGridX - 9;
    private static final int outputClockY = outputGridY - 9 - 7;
    private static final int clockFrameWidth = 10;
    private static final int clockFrameHeight = 10;
    private static final int clockFrameCount = 12;
    private static final int clockTextureHeight = clockFrameHeight * clockFrameCount;
    private static final ResourceLocation clockTexture = new ResourceLocation(FarmersDelightLegacy.MOD_ID,
            "textures/gui/jei/clock/clock.png");
    private static final List<String> clockTooltipKeys = ImmutableList.of(
            "farmersdelight.jei.clock.pause",
            "farmersdelight.jei.clock.resume",
            "farmersdelight.jei.clock.previous_page",
            "farmersdelight.jei.clock.next_page"
    );

    private final String recipeId;
    private final List<HarvestDropRecipeManager.HarvestDropDisplayBlockState> displayBlockStates;
    private final IBlockState displaySupportBlockState;
    private final List<ItemStack> toolOptions;
    private final List<HuntingDropOutput> outputs;
    private final List<ItemStack> outputStacks;
    private boolean outputClockPaused;
    private long outputClockTickOffset;
    private long outputPausedAnimationTick;
    private boolean blockPreviewDragging;
    private int previousBlockDragMouseX;
    private float blockPreviewYawDegrees = blockDefaultYawDegrees;
    private IGuiItemStackGroup outputItemStacks;
    private IDrawable outputSlotBackground;
    private IDrawable outputChanceSlotBackground;

    private HarvestDropJeiRecipe(String recipeId,
                                 List<HarvestDropRecipeManager.HarvestDropDisplayBlockState> displayBlockStates,
                                 IBlockState displaySupportBlockState,
                                 List<ItemStack> toolOptions, List<HuntingDropOutput> outputs) {
        this.recipeId = recipeId;
        this.displayBlockStates = ImmutableList.copyOf(displayBlockStates);
        this.displaySupportBlockState = displaySupportBlockState;
        this.toolOptions = ImmutableList.copyOf(toolOptions);
        this.outputs = ImmutableList.copyOf(outputs);
        this.outputStacks = buildOutputStacks(outputs);
    }

    public static HarvestDropJeiRecipe of(HarvestDropRecipeManager.HarvestDropRecipeView recipe) {
        return new HarvestDropJeiRecipe(recipe.getKey(), recipe.getDisplayBlockStates(), recipe.getDisplaySupportBlockState(),
                recipe.getToolOptions(), recipe.getOutputs());
    }

    private static List<ItemStack> buildOutputStacks(List<HuntingDropOutput> outputs) {
        List<ItemStack> stacks = new ArrayList<>();
        for (HuntingDropOutput output : outputs) {
            stacks.add(output.getOutputStack());
        }
        return ImmutableList.copyOf(stacks);
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        ingredients.setOutputs(VanillaTypes.ITEM, this.outputStacks);
    }

    @Override
    public void drawInfo(@Nonnull Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
        updateOutputSlots();
        updateBlockPreviewDragging(mouseX, mouseY);
        drawBlockPreview(minecraft);
        drawAnimatedTool(minecraft);
        drawClockOutput(minecraft);
    }

    @Override
    public List<String> getTooltipStrings(int mouseX, int mouseY) {
        if (isCursorInsideOutputClock(mouseX, mouseY)) {
            return getClockTooltip();
        }
        if (isCursorInsideBounds(blockTooltipX, blockTooltipY, blockTooltipWidth, blockTooltipHeight, mouseX, mouseY)) {
            return getBlockTooltip();
        }
        for (int outputIndex = 0; outputIndex < getOutputCount(); outputIndex++) {
            if (!isCursorInsideBounds(getOutputSlotX(outputIndex), getOutputSlotY(outputIndex), 18, 18, mouseX, mouseY)) {
                continue;
            }
            HuntingDropOutput output = getCurrentOutput(outputIndex);
            return output == null ? Collections.emptyList() : getOutputTooltip(output);
        }
        return Collections.emptyList();
    }

    @Override
    public boolean handleClick(Minecraft minecraft, int mouseX, int mouseY, int mouseButton) {
        if (isCursorInsideOutputClock(mouseX, mouseY)) {
            return handleOutputClockClick(minecraft, mouseButton);
        }
        if (mouseButton == 0 && isCursorInsideBlockPreview(mouseX, mouseY)) {
            startBlockPreviewDragging(mouseX);
            return true;
        }
        return false;
    }

    private List<String> getClockTooltip() {
        List<String> tooltip = new ArrayList<>();
        for (String tooltipKey : clockTooltipKeys) {
            tooltip.add(I18n.format(tooltipKey));
        }
        return tooltip;
    }

    private List<String> getBlockTooltip() {
        IBlockState tooltipBlockState = getPrimaryDisplayBlockState();
        ItemStack stack = getDisplayStack(tooltipBlockState);
        if (stack.isEmpty()) {
            return Collections.emptyList();
        }
        ResourceLocation blockId = tooltipBlockState.getBlock().getRegistryName();
        ModContainer mod = blockId == null ? null : Loader.instance().getIndexedModList().get(blockId.getNamespace());
        return ImmutableList.of(
                stack.getDisplayName(),
                TextFormatting.BLUE + "" + TextFormatting.ITALIC + (mod == null ? "Unknown" : mod.getName())
        );
    }

    private IBlockState getPrimaryDisplayBlockState() {
        return this.displayBlockStates.isEmpty() ? null : this.displayBlockStates.get(0).getBlockState();
    }

    public int getOutputCount() {
        return Math.min(this.outputStacks.size(), outputGridMaxSlots);
    }

    public void bindOutputSlots(IGuiItemStackGroup itemStacks, IDrawable slotBackground, IDrawable chanceSlotBackground) {
        this.outputItemStacks = itemStacks;
        this.outputSlotBackground = slotBackground;
        this.outputChanceSlotBackground = chanceSlotBackground;
        updateOutputSlots();
    }

    public int getOutputSlotX(int outputIndex) {
        int size = getOutputCount();
        int columns = Math.min(size, outputGridColumns);
        int centerX = columns <= 1 ? outputStep : columns == 2 ? 10 : 1;
        return outputGridX + centerX + ((outputIndex % outputGridColumns) * outputStep);
    }

    public int getOutputSlotY(int outputIndex) {
        int size = getOutputCount();
        int rows = (size + outputGridColumns - 1) / outputGridColumns;
        int centerY = rows <= 1 ? 10 : rows == 2 ? 1 : -8;
        return outputGridY + centerY + ((outputIndex / outputGridColumns) * outputStep);
    }

    public float getOutputChance(int outputIndex) {
        HuntingDropOutput output = getCurrentOutput(outputIndex);
        return output == null ? 1.0F : output.getChance();
    }

    public HuntingDropOutput getCurrentOutput(int outputSlotIndex) {
        int outputIndex = getCurrentOutputPage() * outputGridMaxSlots + outputSlotIndex;
        if (outputIndex < 0 || outputIndex >= this.outputs.size()) {
            return null;
        }
        return this.outputs.get(outputIndex);
    }

    public int getOutputPageCount() {
        return Math.max(1, (this.outputStacks.size() + outputGridMaxSlots - 1) / outputGridMaxSlots);
    }

    private void updateOutputSlots() {
        if (this.outputItemStacks == null) {
            return;
        }
        for (int outputSlotIndex = 0; outputSlotIndex < getOutputCount(); outputSlotIndex++) {
            HuntingDropOutput output = getCurrentOutput(outputSlotIndex);
            if (output == null) {
                this.outputItemStacks.set(outputSlotIndex, ItemStack.EMPTY);
                this.outputItemStacks.setBackground(outputSlotIndex, null);
                continue;
            }
            this.outputItemStacks.set(outputSlotIndex, output.getOutputStack());
            this.outputItemStacks.setBackground(outputSlotIndex,
                    output.getChance() < 1.0F ? this.outputChanceSlotBackground : this.outputSlotBackground);
        }
    }

    private int getCurrentOutputPage() {
        int pageCount = getOutputPageCount();
        if (pageCount <= 1) {
            return 0;
        }
        return getPageIndexFromAnimationTicks(getOutputAnimationTicks(Minecraft.getMinecraft()), pageCount);
    }

    public String getRecipeId() {
        return this.recipeId;
    }

    private List<String> getOutputTooltip(HuntingDropOutput output) {
        ItemStack outputStack = output.getOutputStack();
        if (outputStack.isEmpty()) {
            return Collections.emptyList();
        }
        Minecraft minecraft = Minecraft.getMinecraft();
        ITooltipFlag.TooltipFlags tooltipFlag = minecraft != null && minecraft.gameSettings != null
                && minecraft.gameSettings.advancedItemTooltips ? ITooltipFlag.TooltipFlags.ADVANCED : ITooltipFlag.TooltipFlags.NORMAL;
        List<String> tooltip = new ArrayList<>(outputStack.getTooltip(minecraft == null ? null : minecraft.player, tooltipFlag));
        float chance = output.getChance();
        if (chance < 1.0F) {
            String chancePercent = chance < 0.01F ? "<1" : Integer.toString((int) (chance * 100.0F));
            tooltip.add(Math.min(1, tooltip.size()), TextFormatting.GOLD + I18n.format("farmersdelight.jei.chance", chancePercent));
        }
        JeiTooltipUtil.addRecipeIdTooltip(tooltip, this.recipeId);
        return tooltip;
    }

    private void drawBlockPreview(Minecraft minecraft) {
        if (this.displayBlockStates.isEmpty()) {
            return;
        }
        List<HarvestDropRecipeManager.HarvestDropDisplayBlockState> previewBlockStates = createPreviewBlockStates();
        BlockPreviewBounds blockPreviewBounds = BlockPreviewBounds.create(previewBlockStates);
        PreviewBlockAccess previewBlockAccess = createPreviewBlockAccess(previewBlockStates);
        GlStateManager.pushMatrix();
        minecraft.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        minecraft.getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, false);
        GlStateManager.enableAlpha();
        GlStateManager.alphaFunc(516, 0.1F);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.enableDepth();
        GlStateManager.enableCull();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.translate(blockPreviewX, blockPreviewY, 100.0F);
        float blockPreviewScale = getBlockPreviewScale(blockPreviewBounds);
        GlStateManager.scale(blockPreviewScale, -blockPreviewScale, blockPreviewScale);
        GlStateManager.translate(blockPreviewBounds.getCenterX(), blockPreviewBounds.getCenterY(),
                blockPreviewBounds.getCenterZ());
        GlStateManager.rotate(30.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(this.blockPreviewYawDegrees, 0.0F, 1.0F, 0.0F);
        GlStateManager.translate(-blockPreviewBounds.getCenterX(), -blockPreviewBounds.getCenterY(),
                -blockPreviewBounds.getCenterZ());
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableLighting();
        for (HarvestDropRecipeManager.HarvestDropDisplayBlockState previewBlockState : previewBlockStates) {
            renderPreviewBlockState(minecraft, previewBlockAccess, previewBlockState);
        }
        RenderHelper.disableStandardItemLighting();
        GlStateManager.enableCull();
        GlStateManager.disableBlend();
        GlStateManager.alphaFunc(516, 0.1F);
        minecraft.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        minecraft.getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap();
        GlStateManager.popMatrix();
    }

    private List<HarvestDropRecipeManager.HarvestDropDisplayBlockState> createPreviewBlockStates() {
        List<HarvestDropRecipeManager.HarvestDropDisplayBlockState> previewBlockStates = new ArrayList<>();
        previewBlockStates.addAll(createSupportBlockStates());
        previewBlockStates.addAll(this.displayBlockStates);
        return ImmutableList.copyOf(previewBlockStates);
    }

    private static float getBlockPreviewScale(BlockPreviewBounds blockPreviewBounds) {
        float scaleByWidth = blockPreviewMaximumWidth / Math.max(1.0F, blockPreviewBounds.getWidth() - 1.0F);
        float scaleByHeight = blockPreviewMaximumHeight / Math.max(1.0F, blockPreviewBounds.getHeight() - 1.0F);
        float scaleByDepth = blockPreviewMaximumDepth / Math.max(1.0F, blockPreviewBounds.getDepth() - 1.0F);
        return Math.min(blockDefaultScale, Math.min(scaleByWidth, Math.min(scaleByHeight, scaleByDepth)));
    }

    private List<HarvestDropRecipeManager.HarvestDropDisplayBlockState> createSupportBlockStates() {
        if (this.displaySupportBlockState == null) {
            return Collections.emptyList();
        }
        Map<BlockColumnPosition, Integer> lowestBlockOffsets = new HashMap<>();
        for (HarvestDropRecipeManager.HarvestDropDisplayBlockState displayBlockState : this.displayBlockStates) {
            BlockColumnPosition blockColumnPosition = new BlockColumnPosition(displayBlockState.getOffsetX(),
                    displayBlockState.getOffsetZ());
            Integer lowestOffsetY = lowestBlockOffsets.get(blockColumnPosition);
            if (lowestOffsetY == null || displayBlockState.getOffsetY() < lowestOffsetY) {
                lowestBlockOffsets.put(blockColumnPosition, displayBlockState.getOffsetY());
            }
        }
        List<HarvestDropRecipeManager.HarvestDropDisplayBlockState> supportBlockStates = new ArrayList<>();
        for (Map.Entry<BlockColumnPosition, Integer> lowestBlockOffset : lowestBlockOffsets.entrySet()) {
            BlockColumnPosition blockColumnPosition = lowestBlockOffset.getKey();
            supportBlockStates.add(new HarvestDropRecipeManager.HarvestDropDisplayBlockState(this.displaySupportBlockState,
                    blockColumnPosition.getOffsetX(), lowestBlockOffset.getValue() - 1, blockColumnPosition.getOffsetZ()));
        }
        return supportBlockStates.isEmpty() ? Collections.emptyList() : ImmutableList.copyOf(supportBlockStates);
    }

    private static PreviewBlockAccess createPreviewBlockAccess(
            List<HarvestDropRecipeManager.HarvestDropDisplayBlockState> previewBlockStates) {
        if (Loader.isModLoaded("fluidlogged_api")) {
            try {
                Class<?> previewBlockAccessClass = Class.forName(fluidloggedPreviewBlockAccessClassName);
                Constructor<?> previewBlockAccessConstructor = previewBlockAccessClass.getConstructor(List.class);
                return (PreviewBlockAccess) previewBlockAccessConstructor.newInstance(previewBlockStates);
            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException
                     | InvocationTargetException exception) {
                FarmersDelightLegacy.LOGGER.warn(
                        "Failed to create the Fluidlogged harvest drop JEI preview block access; falling back to the default preview.",
                        exception);
            }
        }
        return new PreviewBlockAccess(previewBlockStates);
    }

    private static void renderPreviewBlockState(Minecraft minecraft, PreviewBlockAccess previewBlockAccess,
                                                 HarvestDropRecipeManager.HarvestDropDisplayBlockState previewBlockState) {
        IBlockState blockState = previewBlockState.getBlockState();
        if (blockState == null || blockState.getRenderType() == EnumBlockRenderType.INVISIBLE) {
            return;
        }
        if (blockState.getRenderType() == EnumBlockRenderType.ENTITYBLOCK_ANIMATED) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(previewBlockState.getOffsetX(), previewBlockState.getOffsetY(),
                    previewBlockState.getOffsetZ());
            minecraft.getBlockRendererDispatcher().renderBlockBrightness(blockState, 1.0F);
            GlStateManager.popMatrix();
            return;
        }
        BlockPos blockPos = new BlockPos(previewBlockState.getOffsetX(), previewBlockState.getOffsetY(),
                previewBlockState.getOffsetZ());
        Vec3d blockOffset = blockState.getOffset(previewBlockAccess, blockPos);
        BlockRendererDispatcher blockRendererDispatcher = minecraft.getBlockRendererDispatcher();
        IBlockState containedFluidState = previewBlockAccess.getContainedFluidBlockState(blockPos);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        GlStateManager.pushMatrix();
        GlStateManager.translate(-blockOffset.x, -blockOffset.y, -blockOffset.z);
        bufferBuilder.begin(7, DefaultVertexFormats.BLOCK);
        blockRendererDispatcher.renderBlock(blockState, blockPos, previewBlockAccess, bufferBuilder);
        tessellator.draw();
        if (containedFluidState != null) {
            bufferBuilder.begin(7, DefaultVertexFormats.BLOCK);
            blockRendererDispatcher.renderBlock(containedFluidState, blockPos, previewBlockAccess, bufferBuilder);
            tessellator.draw();
        }
        GlStateManager.popMatrix();
    }

    private void drawAnimatedTool(Minecraft minecraft) {
        ItemStack toolStack = getAnimatedTool(minecraft);
        if (toolStack.isEmpty()) {
            return;
        }

        HarvestToolPose toolPose = getHarvestToolPose(minecraft);
        GlStateManager.pushMatrix();
        GlStateManager.translate(toolPose.getPositionX(), toolPose.getPositionY(), 0.0F);
        GlStateManager.rotate(toolPose.getRotationDegrees(), 0.0F, 0.0F, 1.0F);
        GlStateManager.disableDepth();
        RenderHelper.enableGUIStandardItemLighting();
        minecraft.getRenderItem().renderItemAndEffectIntoGUI(toolStack, 0, -16);
        RenderHelper.disableStandardItemLighting();
        GlStateManager.enableDepth();
        GlStateManager.popMatrix();
    }

    private HarvestToolPose getHarvestToolPose(Minecraft minecraft) {
        float partialTicks = minecraft.world == null ? 0.0F : minecraft.getRenderPartialTicks();
        float cycleProgress = getAnimationCycleProgress(getAnimationTicks(minecraft) + partialTicks, 35.0F);
        float harvestToolSliceProgressThreshold = 1.0F / (1.0F + 1.2F);
        if (cycleProgress < harvestToolSliceProgressThreshold) {
            float sliceProgress = easeInOutSine(cycleProgress / harvestToolSliceProgressThreshold);
            float positionX = cubicBezier(56.0F, 53.0F, 42.0F, 36.0F, sliceProgress);
            float positionY = cubicBezier(39.0F, 36.0F, 43.0F, 45.0F, sliceProgress);
            float rotationDegrees = cubicBezier(-18.0F, -42.0F, -86.0F, -100.0F, sliceProgress);
            return new HarvestToolPose(positionX, positionY, rotationDegrees);
        }

        float returnProgress = easeInOutSine((cycleProgress - harvestToolSliceProgressThreshold)
                / (1.0F - harvestToolSliceProgressThreshold));
        float positionX = cubicBezier(36.0F, 39.0F, 52.0F, 56.0F, returnProgress);
        float positionY = cubicBezier(45.0F, 47.0F, 42.0F, 39.0F, returnProgress);
        float rotationDegrees = cubicBezier(-100.0F, -92.0F, -34.0F, -18.0F, returnProgress);
        return new HarvestToolPose(positionX, positionY, rotationDegrees);
    }

    private static float getAnimationCycleProgress(float animationTicks, float cycleTicks) {
        float progress = animationTicks % cycleTicks;
        if (progress < 0.0F) {
            progress += cycleTicks;
        }
        return progress / cycleTicks;
    }

    private static float easeInOutSine(float progress) {
        float clampedProgress = Math.max(0.0F, Math.min(1.0F, progress));
        return (float) (0.5D - Math.cos(clampedProgress * Math.PI) * 0.5D);
    }

    private static float cubicBezier(float startValue, float firstControlValue, float secondControlValue,
                                     float endValue, float progress) {
        float reverseProgress = 1.0F - progress;
        return reverseProgress * reverseProgress * reverseProgress * startValue
                + 3.0F * reverseProgress * reverseProgress * progress * firstControlValue
                + 3.0F * reverseProgress * progress * progress * secondControlValue
                + progress * progress * progress * endValue;
    }

    private void drawClockOutput(Minecraft minecraft) {
        if (getOutputPageCount() > 1) {
            drawClock(minecraft, outputClockX, outputClockY, getOutputAnimationTicks(minecraft));
        }
    }

    private void drawClock(Minecraft minecraft, int clockPositionX, int clockPositionY, long animationTicks) {
        minecraft.getTextureManager().bindTexture(clockTexture);
        int frameIndex = (int) (getPositiveRemainder(animationTicks, displayTextIntervalTicks) * clockFrameCount / displayTextIntervalTicks);
        int textureY = frameIndex * clockFrameHeight;
        Gui.drawModalRectWithCustomSizedTexture(clockPositionX, clockPositionY, 0.0F, textureY,
                clockFrameWidth, clockFrameHeight, clockFrameWidth, clockTextureHeight);
    }

    private boolean handleOutputClockClick(Minecraft minecraft, int mouseButton) {
        return handleClockClick(minecraft, mouseButton, getOutputPageCount());
    }

    private boolean handleClockClick(Minecraft minecraft, int mouseButton, int pageCount) {
        boolean controlDown = GuiScreen.isCtrlKeyDown();
        if (controlDown && mouseButton == 0) {
            moveClockPage(minecraft, pageCount, -1);
            return true;
        }
        if (controlDown && mouseButton == 1) {
            moveClockPage(minecraft, pageCount, 1);
            return true;
        }
        if (mouseButton == 0) {
            pauseClock(minecraft);
            return true;
        }
        if (mouseButton == 1) {
            resumeClock(minecraft);
            return true;
        }
        return false;
    }

    private void pauseClock(Minecraft minecraft) {
        this.outputPausedAnimationTick = getOutputAnimationTicks(minecraft);
        this.outputClockPaused = true;
    }

    private void resumeClock(Minecraft minecraft) {
        long animationTicks = getAnimationTicks(minecraft);
        this.outputClockTickOffset = this.outputPausedAnimationTick - animationTicks;
        this.outputClockPaused = false;
    }

    private void moveClockPage(Minecraft minecraft, int pageCount, int pageOffset) {
        long animationTicks = getOutputAnimationTicks(minecraft);
        long frameTicks = getPositiveRemainder(animationTicks, displayTextIntervalTicks);
        int currentPageIndex = getPageIndexFromAnimationTicks(animationTicks, pageCount);
        int pageIndex = wrapPageIndex(currentPageIndex + pageOffset, pageCount);
        this.outputPausedAnimationTick = pageIndex * displayTextIntervalTicks + frameTicks;
        this.outputClockPaused = true;
    }

    private long getOutputAnimationTicks(Minecraft minecraft) {
        return this.outputClockPaused ? this.outputPausedAnimationTick : getAnimationTicks(minecraft) + this.outputClockTickOffset;
    }

    private int getPageIndexFromAnimationTicks(long animationTicks, int pageCount) {
        if (pageCount <= 1) {
            return 0;
        }
        return (int) getPositiveRemainder(Math.floorDiv(animationTicks, displayTextIntervalTicks), pageCount);
    }

    private int wrapPageIndex(int pageIndex, int pageCount) {
        if (pageCount <= 1) {
            return 0;
        }
        return (int) getPositiveRemainder(pageIndex, pageCount);
    }

    private static long getPositiveRemainder(long value, long divisor) {
        long remainder = value % divisor;
        return remainder < 0L ? remainder + divisor : remainder;
    }

    private boolean isCursorInsideOutputClock(int mouseX, int mouseY) {
        return getOutputPageCount() > 1
                && isCursorInsideBounds(outputClockX, outputClockY, clockFrameWidth, clockFrameHeight, mouseX, mouseY);
    }

    private boolean isCursorInsideBlockPreview(int mouseX, int mouseY) {
        return isCursorInsideBounds(blockTooltipX, blockTooltipY, blockTooltipWidth, blockTooltipHeight, mouseX, mouseY);
    }

    private void startBlockPreviewDragging(int mouseX) {
        this.blockPreviewDragging = true;
        this.previousBlockDragMouseX = mouseX;
    }

    private void updateBlockPreviewDragging(int mouseX, int mouseY) {
        if (!Mouse.isButtonDown(0)) {
            this.blockPreviewDragging = false;
            return;
        }
        if (!this.blockPreviewDragging) {
            if (isCursorInsideBlockPreview(mouseX, mouseY)) {
                startBlockPreviewDragging(mouseX);
            }
            return;
        }
        int mouseDeltaX = mouseX - this.previousBlockDragMouseX;
        if (mouseDeltaX != 0) {
            this.blockPreviewYawDegrees += mouseDeltaX * blockDragDegreesPerPixel;
            this.previousBlockDragMouseX = mouseX;
        }
    }

    private static long getAnimationTicks(Minecraft minecraft) {
        return minecraft == null || minecraft.world == null ? Minecraft.getSystemTime() / 50L : minecraft.world.getTotalWorldTime();
    }

    private ItemStack getAnimatedTool(Minecraft minecraft) {
        if (this.toolOptions.isEmpty()) {
            return ItemStack.EMPTY;
        }
        long time = minecraft.world == null ? Minecraft.getSystemTime() : minecraft.world.getTotalWorldTime();
        return this.toolOptions.get((int) ((time / 20L) % this.toolOptions.size()));
    }

    private static ItemStack getDisplayStack(IBlockState state) {
        if (state == null) {
            return ItemStack.EMPTY;
        }
        Item item = Item.getItemFromBlock(state.getBlock());
        if (item == null) {
            return ItemStack.EMPTY;
        }
        if (item instanceof ItemBlock) {
            return new ItemStack(item, 1, state.getBlock().getMetaFromState(state));
        }
        return new ItemStack(item);
    }

    private static boolean isCursorInsideBounds(int boundsX, int boundsY, int width, int height, int mouseX, int mouseY) {
        return mouseX >= boundsX && mouseX < boundsX + width && mouseY >= boundsY && mouseY < boundsY + height;
    }

    private static final class HarvestToolPose {
        private final float positionX;
        private final float positionY;
        private final float rotationDegrees;

        private HarvestToolPose(float positionX, float positionY, float rotationDegrees) {
            this.positionX = positionX;
            this.positionY = positionY;
            this.rotationDegrees = rotationDegrees;
        }

        private float getPositionX() {
            return this.positionX;
        }

        private float getPositionY() {
            return this.positionY;
        }

        private float getRotationDegrees() {
            return this.rotationDegrees;
        }
    }

    private static final class BlockPreviewBounds {
        private final int minimumOffsetX;
        private final int minimumOffsetY;
        private final int minimumOffsetZ;
        private final int maximumOffsetX;
        private final int maximumOffsetY;
        private final int maximumOffsetZ;

        private BlockPreviewBounds(int minimumOffsetX, int minimumOffsetY, int minimumOffsetZ, int maximumOffsetX,
                                   int maximumOffsetY, int maximumOffsetZ) {
            this.minimumOffsetX = minimumOffsetX;
            this.minimumOffsetY = minimumOffsetY;
            this.minimumOffsetZ = minimumOffsetZ;
            this.maximumOffsetX = maximumOffsetX;
            this.maximumOffsetY = maximumOffsetY;
            this.maximumOffsetZ = maximumOffsetZ;
        }

        private static BlockPreviewBounds create(
                List<HarvestDropRecipeManager.HarvestDropDisplayBlockState> displayBlockStates) {
            HarvestDropRecipeManager.HarvestDropDisplayBlockState firstDisplayBlockState = displayBlockStates.get(0);
            int minimumOffsetX = firstDisplayBlockState.getOffsetX();
            int minimumOffsetY = firstDisplayBlockState.getOffsetY();
            int minimumOffsetZ = firstDisplayBlockState.getOffsetZ();
            int maximumOffsetX = firstDisplayBlockState.getOffsetX();
            int maximumOffsetY = firstDisplayBlockState.getOffsetY();
            int maximumOffsetZ = firstDisplayBlockState.getOffsetZ();

            for (HarvestDropRecipeManager.HarvestDropDisplayBlockState displayBlockState : displayBlockStates) {
                minimumOffsetX = Math.min(minimumOffsetX, displayBlockState.getOffsetX());
                minimumOffsetY = Math.min(minimumOffsetY, displayBlockState.getOffsetY());
                minimumOffsetZ = Math.min(minimumOffsetZ, displayBlockState.getOffsetZ());
                maximumOffsetX = Math.max(maximumOffsetX, displayBlockState.getOffsetX());
                maximumOffsetY = Math.max(maximumOffsetY, displayBlockState.getOffsetY());
                maximumOffsetZ = Math.max(maximumOffsetZ, displayBlockState.getOffsetZ());
            }
            return new BlockPreviewBounds(minimumOffsetX, minimumOffsetY, minimumOffsetZ, maximumOffsetX,
                    maximumOffsetY, maximumOffsetZ);
        }

        private int getWidth() {
            return this.maximumOffsetX - this.minimumOffsetX + 1;
        }

        private int getHeight() {
            return this.maximumOffsetY - this.minimumOffsetY + 1;
        }

        private int getDepth() {
            return this.maximumOffsetZ - this.minimumOffsetZ + 1;
        }

        private float getCenterX() {
            return (this.minimumOffsetX + this.maximumOffsetX + 1.0F) / 2.0F;
        }

        private float getCenterY() {
            return (this.minimumOffsetY + this.maximumOffsetY + 1.0F) / 2.0F;
        }

        private float getCenterZ() {
            return (this.minimumOffsetZ + this.maximumOffsetZ + 1.0F) / 2.0F;
        }
    }

    public static class PreviewBlockAccess implements IBlockAccess {
        private final Map<BlockPos, IBlockState> blockStates = new HashMap<>();

        public PreviewBlockAccess(List<HarvestDropRecipeManager.HarvestDropDisplayBlockState> displayBlockStates) {
            for (HarvestDropRecipeManager.HarvestDropDisplayBlockState displayBlockState : displayBlockStates) {
                this.blockStates.put(new BlockPos(displayBlockState.getOffsetX(), displayBlockState.getOffsetY(),
                        displayBlockState.getOffsetZ()), displayBlockState.getBlockState());
            }
        }

        @Override
        public TileEntity getTileEntity(BlockPos pos) {
            return null;
        }

        @Override
        public int getCombinedLight(BlockPos pos, int lightValue) {
            return 15728880;
        }

        @Override
        public IBlockState getBlockState(BlockPos pos) {
            IBlockState blockState = this.blockStates.get(pos);
            return blockState == null ? Blocks.AIR.getDefaultState() : blockState;
        }

        @Override
        public boolean isAirBlock(BlockPos pos) {
            return getBlockState(pos).getMaterial() == Material.AIR;
        }

        @Override
        public Biome getBiome(BlockPos pos) {
            return Biomes.PLAINS;
        }

        @Override
        public int getStrongPower(BlockPos pos, EnumFacing direction) {
            return 0;
        }

        @Override
        public WorldType getWorldType() {
            return WorldType.DEFAULT;
        }

        @Override
        public boolean isSideSolid(BlockPos pos, EnumFacing side, boolean defaultValue) {
            return getBlockState(pos).isSideSolid(this, pos, side);
        }

        public IBlockState getContainedFluidBlockState(BlockPos pos) {
            return null;
        }
    }

    private static final class BlockColumnPosition {
        private final int offsetX;
        private final int offsetZ;

        private BlockColumnPosition(int offsetX, int offsetZ) {
            this.offsetX = offsetX;
            this.offsetZ = offsetZ;
        }

        private int getOffsetX() {
            return this.offsetX;
        }

        private int getOffsetZ() {
            return this.offsetZ;
        }

        @Override
        public boolean equals(Object object) {
            if (this == object) {
                return true;
            }
            if (!(object instanceof BlockColumnPosition)) {
                return false;
            }
            BlockColumnPosition blockColumnPosition = (BlockColumnPosition) object;
            return this.offsetX == blockColumnPosition.offsetX && this.offsetZ == blockColumnPosition.offsetZ;
        }

        @Override
        public int hashCode() {
            int hashCode = this.offsetX;
            hashCode = 31 * hashCode + this.offsetZ;
            return hashCode;
        }
    }
}
