package com.wenxin2.warp_pipes.mixin;

import com.wenxin2.warp_pipes.blocks.WarpPipeBlock;
import com.wenxin2.warp_pipes.blocks.entities.WarpPipeBlockEntity;
import com.wenxin2.warp_pipes.init.Config;
import com.wenxin2.warp_pipes.init.ModTags;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Player.class)
public abstract class PlayerMixin extends Entity {
    @Shadow protected abstract float getBlockSpeedFactor();

    @Shadow public abstract void displayClientMessage(Component p_36216_, boolean p_36217_);

    private static final int MAX_PARTICLE_AMOUNT = 40;
    private int warpCooldown;

    public PlayerMixin(EntityType<?> entityType, Level world) {
        super(entityType, world);
    }

    @Override
    public void baseTick() {
        Level world = this.level();
        BlockPos pos = this.blockPosition();
        BlockState state = world.getBlockState(pos);
        BlockState stateAboveEntity = world.getBlockState(pos.above(Math.round(this.getBbHeight())));

        for (Direction facing : Direction.values()) {
            BlockPos offsetPos = pos.relative(facing);
            BlockState offsetState = world.getBlockState(offsetPos);

            if (offsetState.getBlock() instanceof WarpPipeBlock) {
                this.enterPipe(offsetPos);
            }
            if (state.getBlock() instanceof WarpPipeBlock) {
                this.enterPipe(pos);
            }
        }

        if (stateAboveEntity.getBlock() instanceof WarpPipeBlock) {
            this.enterPipeBelow(pos);
        }

        if (this.warpCooldown > 0) {
            --this.warpCooldown;
        }
        super.baseTick();

//        if (stateAboveEntity.is(Blocks.BRICKS) && this.getDeltaMovement().y > 0)
//        {
//            world.destroyBlock(pos.above(Math.round(this.getBbHeight())), true);
//        }
    }

    public void spawnParticles(Entity entity, Level world) {
        RandomSource random = world.getRandom();
        for(int i = 0; i < MAX_PARTICLE_AMOUNT; ++i) {
            world.addParticle(ParticleTypes.ENCHANT,
                    entity.getRandomX(0.5D), entity.getRandomY(), entity.getRandomZ(0.5D),
                    (random.nextDouble() - 0.5D) * 2.0D, -random.nextDouble(),
                    (random.nextDouble() - 0.5D) * 2.0D);
        }
    }

    public int getWarpCooldown() {
        return warpCooldown;
    }

    public void setWarpCooldown(int cooldown) {
        this.warpCooldown = cooldown;
    }

    public void enterPipeBelow(BlockPos pos) {
        Level world = this.level();
        BlockState stateAboveEntity = world.getBlockState(pos.above(Math.round(this.getBbHeight())));
        BlockEntity blockEntity = world.getBlockEntity(pos.above(Math.round(this.getBbHeight())));
        BlockPos warpPos;

        double entityX = this.getX();
        double entityZ = this.getZ();

        int blockX = pos.getX();
        int blockZ = pos.getZ();

        if (!stateAboveEntity.getValue(WarpPipeBlock.CLOSED) && blockEntity instanceof WarpPipeBlockEntity warpPipeBE && warpPipeBE.getLevel() != null
                && !warpPipeBE.preventWarp && Config.TELEPORT_PLAYERS.get() && !this.getType().is(ModTags.WARP_BlACKLIST)
                && !this.getPersistentData().getBoolean("warp_pipes:prevent_warp")) {
            warpPos = warpPipeBE.destinationPos;
            int entityId = this.getId();

            if (world.isClientSide() && WarpPipeBlock.teleportedEntities.getOrDefault(entityId, false)) {
                this.spawnParticles(this, world);

                // Reset the teleport status for the entity
                WarpPipeBlock.teleportedEntities.put(entityId, false);
            }

            if (warpPipeBE.hasDestinationPos()) {
                if (stateAboveEntity.getValue(WarpPipeBlock.FACING) == Direction.DOWN && this.getDeltaMovement().y > 0
                        && (entityX < blockX + 1 && entityX > blockX) && (entityZ < blockZ + 1 && entityZ > blockZ)) {
                    System.out.println("Below Pipe ");
                    if (this.getWarpCooldown() == 0) {
                        if (warpPos != null && world.getBlockState(warpPos).getBlock() instanceof WarpPipeBlock)
                            WarpPipeBlock.warp(this, warpPos, world, stateAboveEntity);
                        else if (warpPipeBE.getUuid() != null && WarpPipeBlock.findMatchingUUID(warpPipeBE.getUuid(), world, pos) != null)
                            WarpPipeBlock.warp(this, WarpPipeBlock.findMatchingUUID(warpPipeBE.getUuid(), world, pos), world, stateAboveEntity);
                        this.setWarpCooldown(Config.WARP_COOLDOWN.get());
                    } else if (warpPos != null && !(world.getBlockState(warpPos).getBlock() instanceof WarpPipeBlock))
                        displayDestinationMissingMessage();
                    else this.displayCooldownMessage();
                }
            }
        }
    }

    public void enterPipe(BlockPos pos) {
        Level world = this.level();
        BlockState state = world.getBlockState(pos);
        BlockEntity blockEntity = world.getBlockEntity(pos);
        BlockPos warpPos;

        double entityX = this.getX();
        double entityY = this.getY();
        double entityZ = this.getZ();

        int blockX = pos.getX();
        int blockY = pos.getY();
        int blockZ = pos.getZ();

        if (!state.getValue(WarpPipeBlock.CLOSED) && blockEntity instanceof WarpPipeBlockEntity warpPipeBE && warpPipeBE.getLevel() != null
                && !warpPipeBE.preventWarp && Config.TELEPORT_PLAYERS.get() && !this.getType().is(ModTags.WARP_BlACKLIST)
                && !this.getPersistentData().getBoolean("warp_pipes:prevent_warp")) {
            warpPos = warpPipeBE.destinationPos;
            int entityId = this.getId();

            if (world.isClientSide() && WarpPipeBlock.teleportedEntities.getOrDefault(entityId, false)) {
                this.spawnParticles(this, world);

                // Reset the teleport status for the entity
                WarpPipeBlock.teleportedEntities.put(entityId, false);
            }

            if (warpPipeBE.hasDestinationPos()) {

                if (state.getValue(WarpPipeBlock.FACING) == Direction.UP && this.isShiftKeyDown() && (entityY + this.getBbHeight() >= blockY - 1)
                        && (entityX < blockX + 1 && entityX > blockX) && (entityZ < blockZ + 1 && entityZ > blockZ)) {
                    if (this.getWarpCooldown() == 0) {
                         if (warpPos != null && world.getBlockState(warpPos).getBlock() instanceof WarpPipeBlock)
                            WarpPipeBlock.warp(this, warpPos, world, state);
                        else if (warpPipeBE.getUuid() != null && WarpPipeBlock.findMatchingUUID(warpPipeBE.getUuid(), world, pos) != null)
                            WarpPipeBlock.warp(this, WarpPipeBlock.findMatchingUUID(warpPipeBE.getUuid(), world, pos), world, state);
                        this.setWarpCooldown(Config.WARP_COOLDOWN.get());
                    } else if (warpPos != null && !(world.getBlockState(warpPos).getBlock() instanceof WarpPipeBlock))
                        displayDestinationMissingMessage();
                    else this.displayCooldownMessage();
                }
                if (state.getValue(WarpPipeBlock.FACING) == Direction.NORTH && !this.isShiftKeyDown() && this.getMotionDirection() == Direction.SOUTH
                        && (entityX < blockX + 1 && entityX > blockX) && (entityY >= blockY && entityY < blockY + 0.75) && (entityZ < blockZ)) {
                    if (this.getWarpCooldown() == 0) {
                        if (warpPos != null && world.getBlockState(warpPos).getBlock() instanceof WarpPipeBlock)
                            WarpPipeBlock.warp(this, warpPos, world, state);
                        else if (warpPipeBE.getUuid() != null && WarpPipeBlock.findMatchingUUID(warpPipeBE.getUuid(), world, pos) != null)
                            WarpPipeBlock.warp(this, WarpPipeBlock.findMatchingUUID(warpPipeBE.getUuid(), world, pos), world, state);
                        this.setWarpCooldown(Config.WARP_COOLDOWN.get());
                    } else if (warpPos != null && !(world.getBlockState(warpPos).getBlock() instanceof WarpPipeBlock))
                        displayDestinationMissingMessage();
                    else this.displayCooldownMessage();
                }
                if (state.getValue(WarpPipeBlock.FACING) == Direction.SOUTH && !this.isShiftKeyDown() && this.getMotionDirection() == Direction.NORTH
                        && (entityX < blockX + 1 && entityX > blockX) && (entityY >= blockY && entityY < blockY + 0.75) && (entityZ > blockZ + 0.25)) {
                    if (this.getWarpCooldown() == 0) {
                        if (warpPos != null && world.getBlockState(warpPos).getBlock() instanceof WarpPipeBlock)
                            WarpPipeBlock.warp(this, warpPos, world, state);
                        else if (warpPipeBE.getUuid() != null && WarpPipeBlock.findMatchingUUID(warpPipeBE.getUuid(), world, pos) != null)
                            WarpPipeBlock.warp(this, WarpPipeBlock.findMatchingUUID(warpPipeBE.getUuid(), world, pos), world, state);
                        this.setWarpCooldown(Config.WARP_COOLDOWN.get());
                    } else if (warpPos != null && !(world.getBlockState(warpPos).getBlock() instanceof WarpPipeBlock))
                        displayDestinationMissingMessage();
                    else this.displayCooldownMessage();
                }
                if (state.getValue(WarpPipeBlock.FACING) == Direction.EAST && !this.isShiftKeyDown() && this.getMotionDirection() == Direction.WEST
                        && (entityX > blockX) && (entityY >= blockY && entityY < blockY + 0.75) && (entityZ < blockZ + 1 && entityZ > blockZ)) {
                    if (this.getWarpCooldown() == 0) {
                        if (warpPos != null && world.getBlockState(warpPos).getBlock() instanceof WarpPipeBlock)
                            WarpPipeBlock.warp(this, warpPos, world, state);
                        else if (warpPipeBE.getUuid() != null && WarpPipeBlock.findMatchingUUID(warpPipeBE.getUuid(), world, pos) != null)
                            WarpPipeBlock.warp(this, WarpPipeBlock.findMatchingUUID(warpPipeBE.getUuid(), world, pos), world, state);
                        this.setWarpCooldown(Config.WARP_COOLDOWN.get());
                    } else if (warpPos != null && !(world.getBlockState(warpPos).getBlock() instanceof WarpPipeBlock))
                        displayDestinationMissingMessage();
                    else this.displayCooldownMessage();
                }
                if (state.getValue(WarpPipeBlock.FACING) == Direction.WEST && !this.isShiftKeyDown() && this.getMotionDirection() == Direction.EAST
                        && (entityX < blockX) && (entityY >= blockY && entityY < blockY + 0.75) && (entityZ < blockZ + 1 && entityZ > blockZ)) {
                    if (this.getWarpCooldown() == 0) {
                        if (warpPos != null && world.getBlockState(warpPos).getBlock() instanceof WarpPipeBlock)
                            WarpPipeBlock.warp(this, warpPos, world, state);
                        else if (warpPipeBE.getUuid() != null && WarpPipeBlock.findMatchingUUID(warpPipeBE.getUuid(), world, pos) != null)
                            WarpPipeBlock.warp(this, WarpPipeBlock.findMatchingUUID(warpPipeBE.getUuid(), world, pos), world, state);
                        this.setWarpCooldown(Config.WARP_COOLDOWN.get());
                    } else if (warpPos != null && !(world.getBlockState(warpPos).getBlock() instanceof WarpPipeBlock))
                        displayDestinationMissingMessage();
                    else this.displayCooldownMessage();
                }
            }
        } else if (!state.getValue(WarpPipeBlock.CLOSED) && (!Config.TELEPORT_PLAYERS.get() || this.getType().is(ModTags.WARP_BlACKLIST))) {
            if (state.getValue(WarpPipeBlock.FACING) == Direction.UP && this.isShiftKeyDown() && (entityY + this.getBbHeight() >= blockY - 1)
                    && (entityX < blockX + 1 && entityX > blockX) && (entityZ < blockZ + 1 && entityZ > blockZ)) {
                this.displayNoTeleportMessage();
            }
            if (state.getValue(WarpPipeBlock.FACING) == Direction.DOWN && (this.getBlockY() < blockY)
                    && (entityX < blockX + 1 && entityX > blockX) && (entityZ < blockZ + 1 && entityZ > blockZ)) {
                this.displayNoTeleportMessage();
            }
            if (state.getValue(WarpPipeBlock.FACING) == Direction.NORTH && !this.isShiftKeyDown() && this.getMotionDirection() == Direction.SOUTH
                    && (entityX < blockX + 1 && entityX > blockX) && (entityY >= blockY && entityY < blockY + 0.75) && (entityZ < blockZ)) {
                this.displayNoTeleportMessage();
            }
            if (state.getValue(WarpPipeBlock.FACING) == Direction.SOUTH && !this.isShiftKeyDown() && this.getMotionDirection() == Direction.NORTH
                    && (entityX < blockX + 1 && entityX > blockX) && (entityY >= blockY && entityY < blockY + 0.75) && (entityZ > blockZ + 0.25)) {
                this.displayNoTeleportMessage();
            }
            if (state.getValue(WarpPipeBlock.FACING) == Direction.EAST && !this.isShiftKeyDown() && this.getMotionDirection() == Direction.WEST
                    && (entityX > blockX) && (entityY >= blockY && entityY < blockY + 0.75) && (entityZ < blockZ + 1 && entityZ > blockZ)) {
                this.displayNoTeleportMessage();
            }
            if (state.getValue(WarpPipeBlock.FACING) == Direction.WEST && !this.isShiftKeyDown() && this.getMotionDirection() == Direction.EAST
                    && (entityX < blockX) && (entityY >= blockY && entityY < blockY + 0.75) && (entityZ < blockZ + 1 && entityZ > blockZ)) {
                this.displayNoTeleportMessage();
            }
        }
    }

    public void displayCooldownMessage() {
        if (this.getWarpCooldown() >= 10) {
            if (Config.WARP_COOLDOWN_MESSAGE.get()) {
                if (Config.WARP_COOLDOWN_MESSAGE_TICKS.get())
                    this.displayClientMessage(Component.translatable("display.warp_pipes.warp_cooldown.ticks",
                            this.getWarpCooldown()).withStyle(ChatFormatting.RED), true);
                else this.displayClientMessage(Component.translatable("display.warp_pipes.warp_cooldown")
                        .withStyle(ChatFormatting.RED), true);
            }
        }
    }

    public void displayNoTeleportMessage() {
        if (!Config.TELEPORT_PLAYERS.get() || this.getType().is(ModTags.WARP_BlACKLIST)) {
            this.displayClientMessage(Component.translatable("display.warp_pipes.players_cannot_teleport")
                    .withStyle(ChatFormatting.RED), true);
        }
    }

    public void displayDestinationMissingMessage() {
        this.displayClientMessage(Component.translatable("display.warp_pipes.warp_destination_missing")
                .withStyle(ChatFormatting.RED), true);
    }
}
