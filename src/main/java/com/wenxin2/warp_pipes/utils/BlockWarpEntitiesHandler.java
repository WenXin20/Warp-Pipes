package com.wenxin2.warp_pipes.utils;

import com.wenxin2.warp_pipes.blocks.WarpPipeBlock;
import com.wenxin2.warp_pipes.blocks.entities.BaseWarpBlockEntity;
import com.wenxin2.warp_pipes.blocks.entities.WarpPipeBlockEntity;
import com.wenxin2.warp_pipes.registries.ConfigRegistry;
import com.wenxin2.warp_pipes.registries.SoundRegistry;
import com.wenxin2.warp_pipes.registries.TagRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public interface BlockWarpEntityHandler {
    boolean wp$getBlockWarpTeleportConfig();

    private static boolean getShiftKeyForEntity(Entity entity) {
        return (!entity.isShiftKeyDown() && !(entity instanceof Player))
                || (entity.isShiftKeyDown() && entity instanceof Player);
    }

    boolean wp$doPreventWarp();
    void wp$setPreventWarp(boolean preventWarp);

    int wp$getPreventWarpCooldown();
    void wp$setPreventWarpCooldown(int preventWarpCooldown);

    int wp$getWarpCooldown();
    void wp$setWarpCooldown(int warpCooldown);

    default void enterWarp(Entity entity, Level world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        BlockState stateAboveEntity = world.getBlockState(pos.above(Math.round(entity.getBbHeight())));
        BlockEntity blockEntity = world.getBlockEntity(pos);
        BlockEntity blockEntityAbove = world.getBlockEntity(pos.above(Math.round(entity.getBbHeight())));
        BlockPos warpPos;

        if (blockEntity instanceof BaseWarpBlockEntity warpBE && warpBE.getLevel() != null) {
            warpPos = warpBE.destinationPos;
            int entityId = entity.getId();

            if (BaseWarpBlockEntity.WARPED_ENTITIES.getOrDefault(entityId, true))
                // Reset the teleport status for the entity
                BaseWarpBlockEntity.WARPED_ENTITIES.put(entityId, false);

            if (state.getBlock() instanceof WarpPipeBlock)
                this.enterWarpPipe(entity, world, pos, warpPos, warpBE);
        }

        if (blockEntityAbove instanceof BaseWarpBlockEntity warpBE && warpBE.getLevel() != null) {
            warpPos = warpBE.destinationPos;
            int entityId = entity.getId();

            if (BaseWarpBlockEntity.WARPED_ENTITIES.getOrDefault(entityId, true))
                BaseWarpBlockEntity.WARPED_ENTITIES.put(entityId, false);

            if (stateAboveEntity.getBlock() instanceof WarpPipeBlock)
                this.enterWarpPipeAbove(entity, world, pos, warpPos, warpBE);
        }
    }

    default void enterWarpPipe(Entity entity, Level world, BlockPos pos, BlockPos warpPos, BaseWarpBlockEntity warpBE) {
        BlockState state = world.getBlockState(pos);

        double entityX = entity.getX();
        double entityY = entity.getY();
        double entityZ = entity.getZ();
        int blockX = pos.getX();
        int blockY = pos.getY();
        int blockZ = pos.getZ();

        if (!this.wp$doPreventWarp()) {
            if (this.wp$getBlockWarpTeleportConfig() && !entity.getType().is(TagRegistry.CANNOT_WARP)) {
                if (state.getValue(WarpPipeBlock.FACING) == Direction.UP && getShiftKeyForEntity(entity) && (entityY + entity.getBbHeight() >= blockY - 1)
                        && (entityX < blockX + 1 && entityX > blockX) && (entityZ < blockZ + 1 && entityZ > blockZ)) {
                    if (!warpBE.preventWarp && this.wp$getWarpCooldown() == 0)
                        this.warp(entity, world, pos, state, warpPos, warpBE);
                    else if (entity instanceof Player player) {
                        if (warpBE.preventWarp)
                            this.displayWarpDisruptedMessage(player, state);
                        else if (warpBE.hasDestinationPos())
                            this.displayCooldownMessage(player);
                    }
                } else
                if (state.getValue(WarpPipeBlock.FACING) == Direction.NORTH && !entity.isShiftKeyDown()
                        && (entity.onGround() || entity.isInWaterOrBubble()
                        || (entity instanceof LivingEntity livingEntity && livingEntity.isFallFlying())
                        || (entity instanceof Player player && player.getAbilities().flying))
                        && (entityX < blockX + 1 && entityX > blockX) && (entityY >= blockY && entityY < blockY + 0.75) && (entityZ < blockZ)) {
                    if (!warpBE.preventWarp && this.wp$getWarpCooldown() == 0)
                        this.warp(entity, world, pos, state, warpPos, warpBE);
                    else if (entity instanceof Player player) {
                        if (warpBE.preventWarp)
                            this.displayWarpDisruptedMessage(player, state);
                        else if (warpBE.hasDestinationPos())
                            this.displayCooldownMessage(player);
                    }
                }
                if (state.getValue(WarpPipeBlock.FACING) == Direction.SOUTH && !entity.isShiftKeyDown()
                        && (entity.onGround() || entity.isInWaterOrBubble()
                        || (entity instanceof LivingEntity livingEntity && livingEntity.isFallFlying())
                        || (entity instanceof Player player && player.getAbilities().flying))
                        && (entityX < blockX + 1 && entityX > blockX) && (entityY >= blockY && entityY < blockY + 0.75) && (entityZ > blockZ + 0.25)) {
                    if (!warpBE.preventWarp && this.wp$getWarpCooldown() == 0)
                        this.warp(entity, world, pos, state, warpPos, warpBE);
                    else if (entity instanceof Player player) {
                        if (warpBE.preventWarp)
                            this.displayWarpDisruptedMessage(player, state);
                        else if (warpBE.hasDestinationPos())
                            this.displayCooldownMessage(player);
                    }
                }
                if (state.getValue(WarpPipeBlock.FACING) == Direction.EAST && !entity.isShiftKeyDown()
                        && (entity.onGround() || entity.isInWaterOrBubble()
                        || (entity instanceof LivingEntity livingEntity && livingEntity.isFallFlying())
                        || (entity instanceof Player player && player.getAbilities().flying))
                        && (entityX > blockX) && (entityY >= blockY && entityY < blockY + 0.75) && (entityZ < blockZ + 1 && entityZ > blockZ)) {
                    if (!warpBE.preventWarp && this.wp$getWarpCooldown() == 0)
                        this.warp(entity, world, pos, state, warpPos, warpBE);
                    else if (entity instanceof Player player) {
                        if (warpBE.preventWarp)
                            this.displayWarpDisruptedMessage(player, state);
                        else if (warpBE.hasDestinationPos())
                            this.displayCooldownMessage(player);
                    }
                }
                if (state.getValue(WarpPipeBlock.FACING) == Direction.WEST && !entity.isShiftKeyDown()
                        && (entity.onGround() || entity.isInWaterOrBubble()
                        || (entity instanceof LivingEntity livingEntity && livingEntity.isFallFlying())
                        || (entity instanceof Player player && player.getAbilities().flying))
                        && (entityX < blockX) && (entityY >= blockY && entityY < blockY + 0.75) && (entityZ < blockZ + 1 && entityZ > blockZ)) {
                    if (!warpBE.preventWarp && this.wp$getWarpCooldown() == 0)
                        this.warp(entity, world, pos, state, warpPos, warpBE);
                    else if (entity instanceof Player player) {
                        if (warpBE.preventWarp)
                            this.displayWarpDisruptedMessage(player, state);
                        else if (warpBE.hasDestinationPos())
                            this.displayCooldownMessage(player);
                    }
                }
            }
        }
    }

    default void enterWarpPipeAbove(Entity entity, Level world, BlockPos pos, BlockPos warpPos, BaseWarpBlockEntity warpBE) {
        BlockState stateAboveEntity = world.getBlockState(pos.above(Math.round(entity.getBbHeight())));

        double entityX = entity.getX();
        double entityZ = entity.getZ();
        int blockX = pos.getX();
        int blockZ = pos.getZ();

        if (!this.wp$doPreventWarp()) {
            if (this.wp$getBlockWarpTeleportConfig() && !entity.getType().is(TagRegistry.CANNOT_WARP)) {
                if (stateAboveEntity.getValue(WarpPipeBlock.FACING) == Direction.DOWN
                        && (entityX < blockX + 1 && entityX > blockX) && (entityZ < blockZ + 1 && entityZ > blockZ)) {
                    if (!warpBE.preventWarp && this.wp$getWarpCooldown() == 0)
                        this.warp(entity, world, pos, stateAboveEntity, warpPos, warpBE);
                    else if (entity instanceof Player player) {
                        if (warpBE.preventWarp)
                            this.displayWarpDisruptedMessage(player, stateAboveEntity);
                        else if (warpBE.hasDestinationPos())
                            this.displayCooldownMessage(player);
                    }
                }
            }
        }
    }

    default void warp(Entity entity, Level world, BlockPos pos, BlockState state, BlockPos warpPos, BaseWarpBlockEntity warpBE) {
        if (warpPos != null && !(world.getBlockEntity(warpPos) instanceof BaseWarpBlockEntity)
                && entity instanceof Player player)
            this.displayDestinationMissingMessage(player);

        if (warpPos != null && world.getBlockEntity(warpPos) instanceof BaseWarpBlockEntity) {
            BlockState warpState = world.getBlockState(warpPos);

            if (warpState.getBlock() instanceof WarpPipeBlock)
                WarpPipeBlockEntity.warp(entity, warpPos, world, warpState);
            if (state.getBlock() instanceof WarpPipeBlock)
                world.playSound(null, pos, SoundRegistry.PIPE_WARPS.get(), SoundSource.BLOCKS);
        } else if (warpBE.getUUID() != null && warpBE.getWarpUuid() != null
                && BaseWarpBlockEntity.findMatchingUUID(warpBE.getUUID()) != null) {
            warpPos = BaseWarpBlockEntity.findMatchingUUID(warpBE.getUUID());
            BlockState warpState = world.getBlockState(warpPos);

            if (warpState.getBlock() instanceof WarpPipeBlock)
                WarpPipeBlockEntity.warp(entity, warpPos, world, warpState);
            if (state.getBlock() instanceof WarpPipeBlock)
                world.playSound(null, pos, SoundRegistry.PIPE_WARPS.get(), SoundSource.BLOCKS);

            warpBE.setDestinationPos(warpPos);
            if (world.getBlockEntity(warpPos) instanceof BaseWarpBlockEntity destBE)
                destBE.setDestinationPos(pos);
        }
    }

    default void displayCooldownMessage(Player player) {
        if (this.wp$getWarpCooldown() >= 10) {
            if (ConfigRegistry.WARP_COOLDOWN_MESSAGE.get()) {
                if (ConfigRegistry.WARP_COOLDOWN_MESSAGE_TICKS.get())
                    player.displayClientMessage(Component.translatable("display.warp_pipes.warp_pipe_cooldown.ticks",
                            this.wp$getWarpCooldown()), true);
                else player.displayClientMessage(Component.translatable("display.warp_pipes.warp_pipe_cooldown"), true);
            }
        }
    }

    default void displayWarpDisruptedMessage(Player player, BlockState state) {
        player.displayClientMessage(Component.translatable("display.warp_pipes.warp_disrupted",
                state.getBlock().getName()).withStyle(ChatFormatting.RED), true);
    }

    default void displayDestinationMissingMessage(Player player) {
        player.displayClientMessage(Component.translatable("display.warp_pipes.warp_destination_missing"), true);
    }
}