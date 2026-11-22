package com.wenxin2.warp_pipes.utils;

import com.wenxin2.warp_pipes.blocks.WarpPipeBlock;
import com.wenxin2.warp_pipes.blocks.entities.BaseWarpBlockEntity;
import com.wenxin2.warp_pipes.blocks.entities.WarpPipeBlockEntity;
import com.wenxin2.warp_pipes.integration.sable_compat.SableProvider;
import com.wenxin2.warp_pipes.blocks.ClearWarpPipeBlock;
import com.wenxin2.warp_pipes.registries.ConfigRegistry;
import com.wenxin2.warp_pipes.registries.DataAttachmentRegistry;
import com.wenxin2.warp_pipes.registries.SoundRegistry;
import com.wenxin2.warp_pipes.registries.TagRegistry;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
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
import org.jetbrains.annotations.Nullable;
import org.joml.Quaterniondc;
import org.joml.Vector3d;

public interface WP$BlockWarpEntityHandler {
    Map<Entity, Long> AWAITING_RELEASE = Collections.synchronizedMap(new WeakHashMap<>());
    int RELEASE_GRACE_TICKS = 10;
    double LOWERED_FLOOR_TOLERANCE = 0.25;

    boolean wp$getBlockWarpTeleportConfig(Entity entity);

    static boolean getShiftKeyForEntity(Entity entity) {
        return (!entity.isShiftKeyDown() && !(entity instanceof Player))
                || (entity.isShiftKeyDown() && entity instanceof Player);
    }

    default void enterWarp(Entity entity, Level world, BlockPos pos, BlockPos posEmbedded, BlockState state,
                           @Nullable Object object) {
        if (entity.isSpectator())
            return;

        BlockEntity blockEntity = world.getBlockEntity(pos);
        BlockPos warpPos;

        if (SableProvider.isLoaded() && object instanceof SableProvider.SableContext context) {
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

        if (SableProvider.isLoaded() && object instanceof SableProvider.SableContext context) {
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

                boolean withinX = entityX >= blockX && entityX < blockX + 1;
                boolean withinY = entityY + LOWERED_FLOOR_TOLERANCE >= blockY && entityY < blockY + 1;
                boolean withinZ = entityZ >= blockZ && entityZ < blockZ + 1;
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
                    if (!warpBE.preventWarp && entity.getData(DataAttachmentRegistry.WARP_COOLDOWN) == 0) {
                        this.warp(entity, world, pos, state, warpPos, warpBE);
                        AWAITING_RELEASE.put(entity, entity.level().getGameTime());
                    } else if (entity instanceof Player player) {
                        if (WP$BlockWarpEntityHandler.isStillEnteringAfterWarp(entity))
                            return;

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

            if (SableProvider.isLoaded() && object instanceof SableProvider.SableContext context) {
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
                if (this.wp$getBlockWarpTeleportConfig(entity) && !entity.getType().is(TagRegistry.CANNOT_WARP)) {
                    if (stateAbove.hasProperty(WarpPipeBlock.FACING) && stateAbove.getValue(WarpPipeBlock.FACING) == Direction.DOWN) {
                        if (!warpBE.preventWarp && entity.getData(DataAttachmentRegistry.WARP_COOLDOWN) == 0) {
                            this.warp(entity, level, pos, stateAbove, warpPos, warpBE);
                            AWAITING_RELEASE.put(entity, entity.level().getGameTime());
                        } else if (entity instanceof Player player) {
                            if (WP$BlockWarpEntityHandler.isStillEnteringAfterWarp(entity))
                                continue;

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
        if (ClearWarpPipeBlock.isTeleportDisabled(state))
            return;

        if (SableProvider.isLoaded()) {
            if (warpBE.destinationSubLevel != null && warpBE.destinationSubLevelPos != null) {
                if (!(level instanceof ServerLevel serverLevel))
                    return;

                BlockPos resolvedPos = SableProvider.resolveSubLevelPos(serverLevel, warpBE.destinationSubLevel, warpBE.destinationSubLevelPos);

                if (resolvedPos == null) {
                    if (SableProvider.queueSubLevelWarp(serverLevel, warpBE.destinationSubLevel, warpBE.destinationSubLevelPos, entity,
                            loadedPos -> serverLevel.getBlockEntity(loadedPos) instanceof BaseWarpBlockEntity,
                            () -> this.warp(entity, level, pos, state, warpBE.destinationPos, warpBE))) {
                        if (entity instanceof Player player)
                            player.displayClientMessage(Component.translatable("display.warp_pipes.warp_destination_loading"), true);
                    } else if (entity instanceof Player player)
                        WP$BlockWarpEntityHandler.displayDestinationMissingMessage(player);
                    return;
                }

                if (!resolvedPos.equals(warpPos)) {
                    warpBE.destinationPos = resolvedPos;
                    warpBE.setChanged();
                    warpPos = resolvedPos;
                }
            } else if (warpPos != null && level instanceof ServerLevel) {
                warpBE.updateDestinationSubLevel(warpPos);
                if (warpBE.destinationSubLevel != null)
                    warpBE.setChanged();
            }
        }

        if (!level.isClientSide && warpPos != null && !(level.getBlockEntity(warpPos) instanceof BaseWarpBlockEntity)
                && entity instanceof Player player)
            WP$BlockWarpEntityHandler.displayDestinationMissingMessage(player);

        if (warpPos != null && level.getBlockEntity(warpPos) instanceof BaseWarpBlockEntity) {
            this.warpToBlock(entity, level, pos, state, warpPos);
        } else if (warpBE.getUUID() != null && warpBE.getWarpUuid() != null
                && BaseWarpBlockEntity.findMatchingUUID(warpBE.getUUID(), level, pos) != null) {
            warpPos = BaseWarpBlockEntity.findMatchingUUID(warpBE.getUUID(), level, pos);
            this.warpToBlock(entity, level, pos, state, warpPos);

            warpBE.setDestinationPos(warpPos);
            if (level.getBlockEntity(warpPos) instanceof BaseWarpBlockEntity destBE)
                destBE.setDestinationPos(pos);
        }
    }

    private void warpToBlock(Entity entity, Level level, BlockPos pos, BlockState state, BlockPos warpPos) {
        if (WarpLinkPathing.isAccidentalWarp(entity, pos, warpPos))
            return;

        BlockState warpState = level.getBlockState(warpPos);
        WarpCost.Cost warpCost = WarpCost.getBlockCost(level, pos, warpPos, state);
        Vec3 startPos = entity.position();

        if (!WarpCost.canAfford(entity, warpCost))
            return;

        if (warpState.getBlock() instanceof WarpPipeBlock)
            WarpPipeBlockEntity.warp(entity, warpPos, level, warpState);
        if (state.getBlock() instanceof WarpPipeBlock)
            level.playSound(null, pos, SoundRegistry.PIPE_WARPS.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
        WarpCost.chargeIfWarped(entity, startPos, warpCost);
        WarpReturn.onWarped(entity, warpPos, startPos);

        if (level instanceof ServerLevel serverLevel && !entity.position().equals(startPos))
            WarpLinkParticles.spawnWarpTransition(serverLevel, pos, state, warpPos, warpState);
    }

    default void displayCooldownMessage(Player player, BlockState state) {
        if (!player.level().isClientSide && player.getData(DataAttachmentRegistry.WARP_COOLDOWN) >= 10) {
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

    default boolean spectatorWarp(Entity entity, Level level, BlockPos pos, BlockState state, BaseWarpBlockEntity warpBE) {
        if (!(state.getBlock() instanceof WarpPipeBlock) || warpBE.preventWarp || !warpBE.hasDestinationPos()
                || entity.getData(DataAttachmentRegistry.WARP_COOLDOWN) > 0 || ClearWarpPipeBlock.isTeleportDisabled(state))
            return false;
        if (!state.getValue(WarpPipeBlock.ENTRANCE) || state.getValue(WarpPipeBlock.CLOSED))
            return false;

        this.warp(entity, level, pos, state, warpBE.destinationPos, warpBE);
        return true;
    }

    static void displayDestinationMissingMessage(Player player) {
        player.displayClientMessage(Component.translatable("display.warp_pipes.warp_destination_missing"), true);
    }

    static boolean isOnLoweredFloor(double feetY) {
        double fraction = feetY - Math.floor(feetY);
        return fraction > 0.0 && fraction >= 1.0 - LOWERED_FLOOR_TOLERANCE;
    }

    static boolean isStillEnteringAfterWarp(Entity entity) {
        Long lastTick = AWAITING_RELEASE.get(entity);
        long gameTime = entity.level().getGameTime();

        if (lastTick != null && gameTime - lastTick <= RELEASE_GRACE_TICKS) {
            AWAITING_RELEASE.put(entity, gameTime);
            return true;
        }

        AWAITING_RELEASE.remove(entity);
        return false;
    }

    static void warpIntoBlock(Level level, BlockPos warpPos, Entity entity) {
        if (!(level.getBlockEntity(warpPos) instanceof BaseWarpBlockEntity))
            return;

        BlockState warpState = level.getBlockState(warpPos);

        if (warpState.getBlock() instanceof WarpPipeBlock)
            WarpPipeBlockEntity.warp(entity, warpPos, level, warpState);
    }
}
