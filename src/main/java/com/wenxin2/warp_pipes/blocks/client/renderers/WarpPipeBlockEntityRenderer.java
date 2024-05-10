package com.wenxin2.warp_pipes.blocks.client.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.wenxin2.warp_pipes.blocks.WarpPipeBlock;
import com.wenxin2.warp_pipes.blocks.entities.WarpPipeBlockEntity;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.FastColor;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.entity.SignText;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class WarpPipeBlockEntityRenderer /*extends SignRenderer */implements BlockEntityRenderer<WarpPipeBlockEntity> {
    private static final Vec3 TEXT_OFFSET = new Vec3(0.5D, 1.55F, 0.5F);
    private static final float TEXT_RENDER_SCALE = 0.6666667F;
    private static final int OUTLINE_RENDER_DISTANCE = Mth.square(16);
    private final Font font;

    public WarpPipeBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
//        super(context);
        this.font = context.getFont();
    }

    @Override
    public void render(WarpPipeBlockEntity blockEntity, float partialTick, PoseStack stack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        BlockState blockstate = blockEntity.getBlockState();
        BlockPos pos = blockEntity.getBlockPos();
        WarpPipeBlock warpPipeBlock = (WarpPipeBlock)blockstate.getBlock();
        int darkTextColor = getDarkColor(blockEntity);
        if (blockEntity.getDisplayName() != null) {
            stack.pushPose(); //Push

            stack.translate(0.5, 1.25, 0.5);

            int rotation = blockstate.getValue(WarpPipeBlock.FACING).get2DDataValue();
            stack.mulPose(Axis.YP.rotationDegrees(-90F * rotation + 180F));
            stack.mulPose(Axis.XP.rotationDegrees(-0F));

            stack.scale(1.0F, -1.0F, -1.0F);
            stack.scale(0.0125F, 0.0125F, 0.0125F);

            List<FormattedCharSequence> lines = this.font.split(FormattedText.of(blockEntity.getDisplayName().getString()), 60);
            stack.translate(0.0, -(lines.size() * this.font.lineHeight - 1.0) / 2.0, 0);

            int textColor;
            boolean flag;
            int packedLightL;
            if (blockEntity.hasGlowingText()) {
                textColor = blockEntity.getColor().getTextColor();
                flag = isOutlineVisible(pos, textColor);
                packedLightL = 15728880;
            } else {
                textColor = darkTextColor;
                flag = false;
                packedLightL = packedLight;
            }

            //TODO test
            for (int j = 0; j < lines.size(); j++) {
                stack.pushPose();
                stack.translate(-this.font.width(lines.get(j)) / 2.0, (j * this.font.lineHeight), 0.0);
//                this.font.drawInBatch(lines.get(j), 0, 0, packedOverlay, false, stack.last().pose(), buffer, Font.DisplayMode.NORMAL, 0, packedLight);
                if (flag) {
                    this.font.drawInBatch8xOutline(lines.get(j), 0, 0, textColor, darkTextColor, stack.last().pose(), buffer, packedLightL);
                } else {
                    this.font.drawInBatch(lines.get(j), 0, 0, textColor, false, stack.last().pose(), buffer, Font.DisplayMode.POLYGON_OFFSET, 0, packedLightL);
                }
                stack.popPose();
            }
            stack.popPose(); //Pop
        }
//        this.renderSignWithText(blockEntity, stack, buffer, packedLight);
    }

//    void renderSignWithText(WarpPipeBlockEntity blockEntity, PoseStack stack, MultiBufferSource buffer, int packedLight) {
//        stack.pushPose();
//        this.renderText(blockEntity, blockEntity.getBlockPos(), blockEntity.getSignText(), stack, buffer, packedLight, blockEntity.getTextLineHeight(), blockEntity.getMaxTextLineWidth(), true);
//        stack.popPose();
//    }

//    public void renderText(WarpPipeBlockEntity blockEntity, BlockPos pos, SignText text, PoseStack stack, MultiBufferSource buffer, int packedLight, int lineHeight, int maxWidth, boolean isFrontText) {
//        WarpPipeBlockEntity blockEntity =
//        stack.pushPose();
//        this.translateSignText(stack, isFrontText, this.getTextOffset());
//        int darkTextColor = getDarkColor(text);
//        int j = 4 * lineHeight / 2;
//        List<FormattedCharSequence> lines = this.font.split(FormattedText.of(blockEntity.getDisplayName().getString()), 60);
//        FormattedCharSequence[] lines = text.getRenderMessages(Minecraft.getInstance().isTextFilteringEnabled(), (texts) -> {
//            List<FormattedCharSequence> list = this.font.split(texts, maxWidth);
//            return list.isEmpty() ? FormattedCharSequence.EMPTY : list.get(0);
//        });
//        int textColor;
//        boolean flag;
//        int packedLightL;
//        if (text.hasGlowingText()) {
//            textColor = text.getColor().getTextColor();
//            flag = isOutlineVisible(pos, textColor);
//            packedLightL = 15728880;
//        } else {
//            textColor = darkTextColor;
//            flag = false;
//            packedLightL = packedLight;
//        }
//
//        for(int i1 = 0; i1 < 4; ++i1) {
//            FormattedCharSequence line = lines.get(i1);
//            float f = (float)(-this.font.width(lines.get(i1)) / 2);
//            if (flag) {
//                this.font.drawInBatch8xOutline(lines.get(i1), 0, 0, textColor, darkTextColor, stack.last().pose(), buffer, packedLightL);
//            } else {
//                this.font.drawInBatch(lines.get(i1), 0, 0, textColor, false, stack.last().pose(), buffer, Font.DisplayMode.POLYGON_OFFSET, 0, packedLightL);
//            }
//        }
//
//        stack.popPose();
//    }

    private void translateSignText(PoseStack stack, boolean isFrontText, Vec3 offset) {
        if (!isFrontText) {
            stack.mulPose(Axis.YP.rotationDegrees(180.0F));
        }

        float textScale = 0.015625F * this.getSignTextRenderScale();
        stack.translate(offset.x, offset.y, offset.z);
        stack.scale(textScale, -textScale, textScale);
    }

    public float getSignTextRenderScale() {
        return TEXT_RENDER_SCALE;
    }

    Vec3 getTextOffset() {
        return TEXT_OFFSET;
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
                return entity != null && entity.distanceToSqr(Vec3.atCenterOf(pos)) < (double)OUTLINE_RENDER_DISTANCE;
            }
        }
    }

    static int getDarkColor(WarpPipeBlockEntity blockEntity) {
        int textColor = blockEntity.getColor().getTextColor();
        if (textColor == DyeColor.BLACK.getTextColor() && blockEntity.hasGlowingText()) {
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
