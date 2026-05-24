package com.wenxin2.warp_pipes.utils;

import com.wenxin2.warp_pipes.blocks.WarpPipeBlock;
import com.wenxin2.warp_pipes.blocks.entities.BaseWarpBlockEntity;
import com.wenxin2.warp_pipes.blocks.entities.WarpPipeBlockEntity;
import com.wenxin2.warp_pipes.integration.sable_compat.SableProvider;
import com.wenxin2.warp_pipes.registries.ConfigRegistry;
import com.wenxin2.warp_pipes.registries.DataAttachmentRegistry;
import com.wenxin2.warp_pipes.registries.SoundRegistry;
import com.wenxin2.warp_pipes.registries.TagRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.fml.ModList;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaterniondc;
import org.joml.Vector3d;

public interface WP$BlockWarpEntitiesHandler {
    boolean wp$getBlockWarpTeleportConfig(Entity entity);

    private static boolean getShiftKeyForEntity(Entity entity) {
        return (!entity.isShiftKeyDown() && !(entity instanceof Player))
                || (entity.isShiftKeyDown() && entity instanceof Player);
    }

    default void enterWarp(Entity entity, Level world, BlockPos pos, BlockPos posEmbedded, BlockState state,
                           @Nullable Object object) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        BlockPos warpPos;

        if (ModList.get().isLoaded("sable") && object instanceof SableProvider.SableContext context) {
            state = context.accessor.getBlockState(posEmbedded);
            blockEntity = context.accessor.getBlockEntity(posEmbedded);
            if (world instanceof ServerLevel) {
                state = context.accessor.getServerBlockState(posEmbedded);
                blockEntity = context.accessor.getServerBlockEntity(posEmbedded);
            }
        }

        if (blockEntity instanceof BaseWarpBlockEntity warpBE && warpBE.getLevel() != null) {
            warpPos = warpBE.destinationPos;
            int entityId = entity.getId();

            if (BaseWarpBlockEntity.WARPED_ENTITIES.getOrDefault(entityId, true))
                // Reset the teleport status for the entity
                BaseWarpBlockEntity.WARPED_ENTITIES.put(entityId, false);

            if (state.getBlock() instanceof WarpPipeBlock)
                this.enterWarpPipe(entity, world, posEmbedded, warpPos, warpBE, object);

            if (state.getBlock() instanceof WarpPipeBlock)
                this.enterWarpPipeAbove(entity, world, pos, warpPos, warpBE, object);
        }
    }

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

        if (!entity.getData(DataAttachmentRegistry.PREVENT_WARP)) {
            if (this.wp$getBlockWarpTeleportConfig(entity)
                    && !entity.getType().is(TagRegistry.CANNOT_WARP)
                    && state.hasProperty(WarpPipeBlock.FACING)) {
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
                    canEnterPipe = !entity.isShiftKeyDown() && canEnterSidePipe && (facingIntoPipe || !(entity instanceof Player));
                else canEnterPipe = getShiftKeyForEntity(entity);

                if (insideFaceBounds && canEnterPipe) {
                    if (!warpBE.preventWarp && entity.getData(DataAttachmentRegistry.WARP_COOLDOWN) == 0)
                        this.warp(entity, world, pos, state, warpPos, warpBE);
                    else if (entity instanceof Player player) {
                        if (warpBE.preventWarp)
                            this.displayWarpDisruptedMessage(player, state);
                        else if (warpBE.hasDestinationPos())
                            this.displayCooldownMessage(player, state);
                    }
                }
            }
        }
    }

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

            if (!entity.getData(DataAttachmentRegistry.PREVENT_WARP)) {
                if (this.wp$getBlockWarpTeleportConfig(entity) && !entity.getType().is(TagRegistry.CANNOT_WARP) && stateAbove.hasProperty(WarpPipeBlock.FACING)) {
                    if (stateAbove.getValue(WarpPipeBlock.FACING) == Direction.DOWN) {
                        if (!warpBE.preventWarp && entity.getData(DataAttachmentRegistry.WARP_COOLDOWN) == 0)
                            this.warp(entity, level, pos, stateAbove, warpPos, warpBE);
                        else if (entity instanceof Player player) {
                            if (warpBE.preventWarp)
                                this.displayWarpDisruptedMessage(player, stateAbove);
                            else if (warpBE.hasDestinationPos())
                                this.displayCooldownMessage(player, stateAbove);
                        }
                    }
                }
            }
        }
    }

    default void warp(Entity entity, Level level, BlockPos pos, BlockState state, BlockPos warpPos, BaseWarpBlockEntity warpBE) {
        if (warpPos != null && !(level.getBlockEntity(warpPos) instanceof BaseWarpBlockEntity)
                && entity instanceof Player player)
            WP$BlockWarpEntitiesHandler.displayDestinationMissingMessage(player);

        if (warpPos != null && level.getBlockEntity(warpPos) instanceof BaseWarpBlockEntity) {
            BlockState warpState = level.getBlockState(warpPos);

            if (warpState.getBlock() instanceof WarpPipeBlock)
                WarpPipeBlockEntity.warp(entity, warpPos, level, warpState);
            if (state.getBlock() instanceof WarpPipeBlock)
                level.playSound(null, pos, SoundRegistry.PIPE_WARPS.get(), SoundSource.BLOCKS);
        } else if (warpBE.getUUID() != null && warpBE.getWarpUuid() != null
                && BaseWarpBlockEntity.findMatchingUUID(warpBE.getUUID()) != null) {
            warpPos = BaseWarpBlockEntity.findMatchingUUID(warpBE.getUUID());
            BlockState warpState = level.getBlockState(warpPos);

            if (warpState.getBlock() instanceof WarpPipeBlock)
                WarpPipeBlockEntity.warp(entity, warpPos, level, warpState);
            if (state.getBlock() instanceof WarpPipeBlock)
                level.playSound(null, pos, SoundRegistry.PIPE_WARPS.get(), SoundSource.BLOCKS);

            warpBE.setDestinationPos(warpPos);
            if (level.getBlockEntity(warpPos) instanceof BaseWarpBlockEntity destBE)
                destBE.setDestinationPos(pos);
        }
    }

    default void displayCooldownMessage(Player player, BlockState state) {
        if (player.getData(DataAttachmentRegistry.WARP_COOLDOWN) >= 10) {
            if (state.getBlock() instanceof WarpPipeBlock) {
                if (ConfigRegistry.WARP_COOLDOWN_MESSAGE.get()) {
                    if (ConfigRegistry.WARP_COOLDOWN_MESSAGE_TICKS.get())
                        player.displayClientMessage(Component.translatable("display.warp_pipes.warp_pipe_cooldown.ticks",
                                player.getData(DataAttachmentRegistry.WARP_COOLDOWN)), true);
                    else player.displayClientMessage(Component.translatable("display.warp_pipes.warp_pipe_cooldown"), true);
                }
            }
        }
    }

    default void displayWarpDisruptedMessage(Player player, BlockState state) {
        player.displayClientMessage(Component.translatable("display.warp_pipes.warp_disrupted",
                state.getBlock().getName()).withStyle(ChatFormatting.RED), true);
    }

    static void displayDestinationMissingMessage(Player player) {
        player.displayClientMessage(Component.translatable("display.warp_pipes.warp_destination_missing"), true);
    }
}