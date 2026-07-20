package com.wenxin2.warp_pipes.utils;

import com.wenxin2.warp_pipes.blocks.WarpPipeBlock;
import com.wenxin2.warp_pipes.blocks.entities.BaseWarpBlockEntity;
import com.wenxin2.warp_pipes.integration.sable_compat.SableProvider;
import com.wenxin2.warp_pipes.registries.DataAttachmentRegistry;
import com.wenxin2.warp_pipes.registries.TagRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.fml.ModList;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaterniondc;
import org.joml.Vector3d;

public interface WP$BlockWarpPlayerHandler extends WP$BlockWarpEntityHandler {
    @Override
    default void enterWarpPipe(Entity entity, Level world, BlockPos pos, BlockPos warpPos, BaseWarpBlockEntity warpBE,
                               @Nullable Object object) {
        BlockState state = world.getBlockState(pos);
        boolean canEnterSidePipe = entity.verticalCollision || entity.onGround() || entity.isInWaterOrBubble()
                || (entity instanceof LivingEntity living && living.isFallFlying())
                || (entity instanceof Player player && player.getAbilities().flying);

        Vec3 look = entity.getViewVector(1.0F);
        double entityX = entity.getX();
        double entityY = entity.getY();
        double entityZ = entity.getZ();
        int blockX = pos.getX();
        int blockY = pos.getY();
        int blockZ = pos.getZ();

        if (ModList.get().isLoaded("sable") && object instanceof SableProvider.SableContext context) {
            Quaterniondc rotation = context.subLevel.logicalPose().orientation();
            Vector3d localLook = rotation.transformInverse(new Vector3d(look.x, look.y, look.z));
            look = new Vec3(localLook.x, localLook.y, localLook.z).normalize();
            state = context.accessor.getBlockState(pos);
            if (world instanceof ServerLevel)
                state = context.accessor.getServerBlockState(pos);
            entityX = context.posLocal.x;
            entityY = context.posLocal.y;
            entityZ = context.posLocal.z;
        }

        if (entity instanceof Player player && (!this.wp$getBlockWarpTeleportConfig(entity)
                || entity.getType().is(TagRegistry.CANNOT_WARP) || entity.getData(DataAttachmentRegistry.PREVENT_WARP))) {

            Direction facing = state.getValue(WarpPipeBlock.FACING);

            double horizontalLength = Math.sqrt(look.x * look.x + look.z * look.z);
            double flatX = horizontalLength > 0.0001 ? look.x / horizontalLength : 0;
            double flatZ = horizontalLength > 0.0001 ? look.z / horizontalLength : 0;
            double lookDot = flatX * facing.getStepX() + flatZ * facing.getStepZ();
            boolean facingIntoPipe = lookDot < -0.65;

            boolean withinX = entityX > blockX && entityX < blockX + 1;
            boolean withinY = entityY >= blockY && entityY < blockY + 1;
            boolean withinZ = entityZ > blockZ && entityZ < blockZ + 1;
            boolean insideFaceBounds;

            switch (facing.getAxis()) {
                case X -> insideFaceBounds = withinY && withinZ;
                case Y -> insideFaceBounds = withinX && withinZ;
                case Z -> insideFaceBounds = withinX && withinY;
                default -> insideFaceBounds = false;
            }
            boolean canEnterPipe;

            if (facing.getAxis().isHorizontal())
                canEnterPipe = !entity.isShiftKeyDown() && canEnterSidePipe && facingIntoPipe;
            else canEnterPipe = WP$BlockWarpEntityHandler.getShiftKeyForEntity(entity);

            if (insideFaceBounds && canEnterPipe)
                this.displayNoTeleportMessage(player, state);
        } else WP$BlockWarpEntityHandler.super.enterWarpPipe(entity, world, pos, warpPos, warpBE, object);
    }

    @Override
    default void enterWarpPipeAbove(Entity entity, Level level, BlockPos pos, BlockPos warpPos, BaseWarpBlockEntity warpBE,
                                    @Nullable Object object) {
        Vec3 motion = entity.getDeltaMovement();
        AABB aboveBox = entity.getBoundingBox()
                .deflate(0.1, 0.0, 0.1)
                .expandTowards(0, motion.y + 0.2, 0);
        BlockPos min = BlockPos.containing(aboveBox.minX, aboveBox.minY, aboveBox.minZ);
        BlockPos max = BlockPos.containing(aboveBox.maxX, aboveBox.maxY, aboveBox.maxZ);

        for (BlockPos posAbove : BlockPos.betweenClosed(min, max)) {
            BlockState stateAbove = level.getBlockState(posAbove);

            if (ModList.get().isLoaded("sable") && object instanceof SableProvider.SableContext context) {
                BlockPos posEmbedded = context.posEmbedded.above(Math.round(entity.getBbHeight()))
                        .offset(posAbove.getX() - min.getX(), posAbove.getY() - max.getY(), posAbove.getZ() - min.getZ());
                stateAbove = context.accessor.getBlockState(posEmbedded);
                if (level instanceof ServerLevel) {
                    posEmbedded = context.posWorld.above(Math.round(entity.getBbHeight()))
                            .offset(posAbove.getX() - min.getX(), posAbove.getY() - max.getY(), posAbove.getZ() - min.getZ());
                    stateAbove = context.accessor.getServerBlockState(posEmbedded);
                }
            }

            if (entity instanceof Player player && (!this.wp$getBlockWarpTeleportConfig(entity)
                    || entity.getType().is(TagRegistry.CANNOT_WARP) || entity.getData(DataAttachmentRegistry.PREVENT_WARP))) {
                if (stateAbove.hasProperty(WarpPipeBlock.FACING) && stateAbove.getValue(WarpPipeBlock.FACING) == Direction.DOWN)
                    this.displayNoTeleportMessage(player, stateAbove);
            } else WP$BlockWarpEntityHandler.super.enterWarpPipeAbove(entity, level, pos, warpPos, warpBE, object);
        }
    }

    private void displayNoTeleportMessage(Player player, BlockState state) {
        if (!this.wp$getBlockWarpTeleportConfig(player) || player.getType().is(TagRegistry.CANNOT_WARP)) {
            if (state.getBlock() instanceof WarpPipeBlock)
                player.displayClientMessage(Component.translatable("display.warp_pipes.pipes_cannot_teleport_players"), true);
        }

        if (player.getData(DataAttachmentRegistry.PREVENT_WARP))
            player.displayClientMessage(Component.translatable("display.warp_pipes.warp_disrupted_player"), true);
    }
}