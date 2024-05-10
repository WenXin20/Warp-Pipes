package com.wenxin2.warp_pipes.blocks.client.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.wenxin2.warp_pipes.blocks.WarpPipeBlock;
import com.wenxin2.warp_pipes.blocks.entities.WarpPipeBlockEntity;
import com.wenxin2.warp_pipes.init.ModRegistry;
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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class WarpPipeBlockEntityRenderer implements BlockEntityRenderer<WarpPipeBlockEntity> {
    private static final float TEXT_RENDER_SCALE = 0.6666667F;
    private static final int OUTLINE_RENDER_DISTANCE = Mth.square(16);
    private final Font font;

    public WarpPipeBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.font = context.getFont();
    }

    @Override
    public void render(WarpPipeBlockEntity blockEntity, float partialTick, PoseStack stack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        BlockState state = blockEntity.getBlockState();
        BlockPos pos = blockEntity.getBlockPos();
        Level world = blockEntity.getLevel();
        WarpPipeBlock warpPipeBlock = (WarpPipeBlock)state.getBlock();
        int darkTextColor = getDarkColor(blockEntity);
        if (blockEntity.getDisplayName() != null) {
            stack.pushPose(); //Push

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

            stack.translate(0.5, 0.85, -0.001);

            int rotation = state.getValue(WarpPipeBlock.FACING).get2DDataValue();
            stack.mulPose(Axis.YP.rotationDegrees(0F * rotation + 180F));
            stack.mulPose(Axis.XP.rotationDegrees(-0F));

            stack.scale(1.0F, -1.0F, -1.0F);
            stack.scale(0.0125F, 0.0125F, 0.0125F);

            List<FormattedCharSequence> lines = this.font.split(FormattedText.of(blockEntity.getDisplayName().getString()), 60);
            stack.translate(0.0, -(lines.size() * this.font.lineHeight - 1.0) / 2.0, 0);

            for (int j = 0; j < lines.size(); j++) {
                stack.pushPose();
                stack.translate(-this.font.width(lines.get(j)) / 2.0, (j * this.font.lineHeight), 0.0);
                if (world != null) {
                    BlockState stateNorth = world.getBlockState(pos.north());
                    if (!(stateNorth.isSolid() && stateNorth.isSolidRender(world, pos.north())) && !(state.is(ModRegistry.CLEAR_WARP_PIPE.get()) && stateNorth.is(ModRegistry.CLEAR_WARP_PIPE.get()))) {
                        if (flag) {
                            this.font.drawInBatch8xOutline(lines.get(j), 0, 0, textColor, darkTextColor, stack.last().pose(), buffer, packedLightL);
                        } else {
                            this.font.drawInBatch(lines.get(j), 0, 0, textColor, false, stack.last().pose(), buffer, Font.DisplayMode.POLYGON_OFFSET, 0, packedLightL);
                        }
                    }
                }
                stack.popPose();
            }
            stack.popPose(); //Pop
        }
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
