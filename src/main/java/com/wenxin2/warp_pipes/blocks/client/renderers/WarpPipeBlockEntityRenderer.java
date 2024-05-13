package com.wenxin2.warp_pipes.blocks.client.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.wenxin2.warp_pipes.blocks.WarpPipeBlock;
import com.wenxin2.warp_pipes.blocks.entities.PipeText;
import com.wenxin2.warp_pipes.blocks.entities.WarpPipeBlockEntity;
import com.wenxin2.warp_pipes.init.Config;
import com.wenxin2.warp_pipes.init.ModRegistry;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.FastColor;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class WarpPipeBlockEntityRenderer implements BlockEntityRenderer<WarpPipeBlockEntity> {
    private static final float TEXT_RENDER_SCALE = 0.0125F;
    private static final int OUTLINE_RENDER_DISTANCE = Mth.square(16);
    private final Font font;

    public WarpPipeBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.font = context.getFont();
    }

    @Override
    public void render(WarpPipeBlockEntity blockEntity, float partialTick, PoseStack stack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        if (!Config.DISABLE_TEXT.get()) this.renderPipeWithText(blockEntity, stack, buffer, packedLight);
    }

    void renderPipeWithText(WarpPipeBlockEntity pipeBlockEntity, PoseStack stack, MultiBufferSource buffer, int packedLight) {
        stack.pushPose();
        this.renderPipeText(pipeBlockEntity, pipeBlockEntity.getBlockPos(), pipeBlockEntity.getPipeName(), stack, buffer, packedLight,
                pipeBlockEntity.getTextLineHeight(), pipeBlockEntity.getMaxTextLineWidth());
        stack.popPose();
    }

    void renderPipeText(WarpPipeBlockEntity pipeBlockEntity, BlockPos pos, PipeText pipeText, PoseStack stack, MultiBufferSource buffer, int packedLight, int lineHeight, int maxWidth) {
        BlockState state = pipeBlockEntity.getBlockState();
        Level world = pipeBlockEntity.getLevel();

        FormattedCharSequence[] pipeNameArray = pipeText.getRenderMessages(Minecraft.getInstance().isTextFilteringEnabled(), (text) -> {
            List<FormattedCharSequence> list = this.font.split(text, maxWidth);
            return list.isEmpty() ? FormattedCharSequence.EMPTY : list.get(0);
        });

        stack.pushPose();

        int textColor;
        int packedLightL;
        boolean hasGlowingText;
        if (pipeText.hasGlowingText()) {
            textColor = pipeText.getColor().getTextColor();
            hasGlowingText = isOutlineVisible(pos, textColor);
            packedLightL = 15728880;
        } else {
            textColor = getDarkColor(pipeText);;
            packedLightL = packedLight;
            hasGlowingText = false;
        }

        if (world != null && state.getValue(WarpPipeBlock.ENTRANCE)) {
            stack.pushPose();
            if (state.getValue(WarpPipeBlock.FACING) == Direction.UP) {
                stack.translate(0.5, 0.8, 1.001);
                stack.mulPose(Axis.YP.rotationDegrees(0F));
            } else if (state.getValue(WarpPipeBlock.FACING) == Direction.DOWN){
                stack.translate(0.5, 0.2, 1.001);
                stack.mulPose(Axis.YP.rotationDegrees(0F));
                stack.mulPose(Axis.ZP.rotationDegrees(180F));
            } else if (state.getValue(WarpPipeBlock.FACING) == Direction.EAST){
                stack.translate(0.8, 0.5, 1.001);
                stack.mulPose(Axis.YP.rotationDegrees(0F));
                stack.mulPose(Axis.ZP.rotationDegrees(270F));
            } else if (state.getValue(WarpPipeBlock.FACING) == Direction.WEST){
                stack.translate(0.2, 0.5, 1.001);
                stack.mulPose(Axis.YP.rotationDegrees(0F));
                stack.mulPose(Axis.ZP.rotationDegrees(90F));
            }

            stack.scale(1.0F, -1.0F, -1.0F);
            stack.scale(TEXT_RENDER_SCALE, TEXT_RENDER_SCALE, TEXT_RENDER_SCALE);
            stack.translate(0.0, -(this.font.lineHeight - 1.0) / 2.0, 0);

            for (int j = 0; j < 1; j++) {
                BlockState stateSouth = world.getBlockState(pos.south());
                FormattedCharSequence pipeName = pipeNameArray[j];
                stack.translate((-this.font.width(pipeName) / 2.0) + 0.5, 2, 0.0);

                if (!(stateSouth.isSolid() && stateSouth.isSolidRender(world, pos.south())) && !(state.is(ModRegistry.CLEAR_WARP_PIPE.get()) && stateSouth.is(ModRegistry.CLEAR_WARP_PIPE.get()))) {
                    if (hasGlowingText) {
                        this.font.drawInBatch8xOutline(pipeName, 0, 0, textColor, getDarkColor(pipeText),
                                stack.last().pose(), buffer, packedLightL);
                    } else {
                        this.font.drawInBatch(pipeName, 0, 0, textColor, false,
                                stack.last().pose(), buffer, Font.DisplayMode.POLYGON_OFFSET, 0, packedLightL);
                    }
                }
            }
            stack.popPose();
        }
        stack.popPose();
    }

    static boolean isOutlineVisible(BlockPos pos, int textColor) {
        if (textColor == DyeColor.BLACK.getTextColor()) {
            return true;
        } else {
            Minecraft minecraft = Minecraft.getInstance();
            LocalPlayer localplayer = minecraft.player;
            if (localplayer != null && minecraft.options.getCameraType().isFirstPerson() && localplayer.isScoping()) {
                return true;
            } else {
                Entity entity = minecraft.getCameraEntity();
                return entity != null && entity.distanceToSqr(Vec3.atCenterOf(pos)) < (double) OUTLINE_RENDER_DISTANCE;
            }
        }
    }

    static int getDarkColor(PipeText pipeText) {
        int textColor = pipeText.getColor().getTextColor();
        if (textColor == DyeColor.BLACK.getTextColor() && pipeText.hasGlowingText()) {
            return -988212;
        } else {
            double d0 = 0.4D;
            int j = (int)((double) FastColor.ARGB32.red(textColor) * 0.4D);
            int k = (int)((double) FastColor.ARGB32.green(textColor) * 0.4D);
            int l = (int)((double) FastColor.ARGB32.blue(textColor) * 0.4D);
            return FastColor.ARGB32.color(0, j, k, l);
        }
    }
}
