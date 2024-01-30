package com.wenxin2.warp_pipes.mixin;

import com.wenxin2.warp_pipes.blocks.WarpPipeBlock;
import com.wenxin2.warp_pipes.blocks.entities.WarpPipeBlockEntity;
import com.wenxin2.warp_pipes.init.Config;
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

    public PlayerMixin(EntityType<?> entityType, Level world) {
        super(entityType, world);
    }

    private static final int MAX_PARTICLE_COUNT = 100;

    @Override
    public void baseTick() {
        Level world = this.level();
        BlockPos pos = this.blockPosition();
        BlockState state = world.getBlockState(pos);

        for (Direction facing : Direction.values()) {
            BlockPos offsetPos = pos.relative(facing);
            BlockState offsetState = world.getBlockState(offsetPos);

            if (offsetState.getBlock() instanceof WarpPipeBlock) {
                this.entityInside(offsetPos);
            }
            if (state.getBlock() instanceof WarpPipeBlock) {
                this.entityInside(pos);
            }
        }

        super.baseTick();
    }

    public void spawnParticles(Entity entity, Level world) {
        RandomSource random = world.getRandom();
        for(int i = 0; i < 40; ++i) {
            world.addParticle(ParticleTypes.ENCHANT,
                    entity.getRandomX(0.5D), entity.getRandomY(), entity.getRandomZ(0.5D),
                    (random.nextDouble() - 0.5D) * 2.0D, -random.nextDouble(),
                    (random.nextDouble() - 0.5D) * 2.0D);
        }
    }
    
    public void entityInside(BlockPos pos) {
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
                && Config.TELEPORT_PLAYERS.get()) {
            warpPos = warpPipeBE.destinationPos;
            int entityId = this.getId();

            if (world.isClientSide() && WarpPipeBlock.teleportedEntities.getOrDefault(entityId, false)) {
                this.spawnParticles(this, world);
                WarpPipeBlock.teleportedEntities.put(entityId, false);
            }

            if (warpPipeBE.hasDestinationPos()) {

                if (state.getValue(WarpPipeBlock.FACING) == Direction.UP && this.isShiftKeyDown() && (entityY + this.getBbHeight() >= blockY - 1)
                        && (entityX < blockX + 1 && entityX > blockX) && (entityZ < blockZ + 1 && entityZ > blockZ)) {
                    if (this.portalCooldown == 0) {
                        WarpPipeBlock.warp(this, warpPos, world, state);
                        this.setPortalCooldown();
                        this.portalCooldown = Config.WARP_COOLDOWN.get();
                    } else this.displayCooldownMessage();
                }
                if (state.getValue(WarpPipeBlock.FACING) == Direction.DOWN && (this.getBlockY() < blockY)
                        && (entityX < blockX + 1 && entityX > blockX) && (entityZ < blockZ + 1 && entityZ > blockZ)) {
                    if (this.portalCooldown == 0) {
                        WarpPipeBlock.warp(this, warpPos, world, state);
                        this.setPortalCooldown();
                        this.portalCooldown = Config.WARP_COOLDOWN.get();
                    } else this.displayCooldownMessage();
                }
                if (state.getValue(WarpPipeBlock.FACING) == Direction.NORTH && !this.isShiftKeyDown() && this.getMotionDirection() == Direction.SOUTH
                        && (entityX < blockX + 1 && entityX > blockX) && (entityY >= blockY && entityY < blockY + 0.75) && (entityZ < blockZ)) {
                    if (this.portalCooldown == 0) {
                        WarpPipeBlock.warp(this, warpPos, world, state);
                        this.setPortalCooldown();
                        this.portalCooldown = Config.WARP_COOLDOWN.get();
                    } else this.displayCooldownMessage();
                }
                if (state.getValue(WarpPipeBlock.FACING) == Direction.SOUTH && !this.isShiftKeyDown() && this.getMotionDirection() == Direction.NORTH
                        && (entityX < blockX + 1 && entityX > blockX) && (entityY >= blockY && entityY < blockY + 0.75) && (entityZ > blockZ + 0.25)) {
                    if (this.portalCooldown == 0) {
                        WarpPipeBlock.warp(this, warpPos, world, state);
                        this.setPortalCooldown();
                        this.portalCooldown = Config.WARP_COOLDOWN.get();
                    } else this.displayCooldownMessage();
                }
                if (state.getValue(WarpPipeBlock.FACING) == Direction.EAST && !this.isShiftKeyDown() && this.getMotionDirection() == Direction.WEST
                        && (entityX > blockX) && (entityY >= blockY && entityY < blockY + 0.75) && (entityZ < blockZ + 1 && entityZ > blockZ)) {
                    if (this.portalCooldown == 0) {
                        WarpPipeBlock.warp(this, warpPos, world, state);
                        this.setPortalCooldown();
                        this.portalCooldown = Config.WARP_COOLDOWN.get();
                    } else this.displayCooldownMessage();
                }
                if (state.getValue(WarpPipeBlock.FACING) == Direction.WEST && !this.isShiftKeyDown() && this.getMotionDirection() == Direction.EAST
                        && (entityX < blockX) && (entityY >= blockY && entityY < blockY + 0.75) && (entityZ < blockZ + 1 && entityZ > blockZ)) {
                    if (this.portalCooldown == 0) {
                        WarpPipeBlock.warp(this, warpPos, world, state);
                        this.setPortalCooldown();
                        this.portalCooldown = Config.WARP_COOLDOWN.get();
                    } else this.displayCooldownMessage();
                }
            }
        } else if (!Config.TELEPORT_PLAYERS.get()) {
            this.displayClientMessage(Component.translatable("display.warp_pipes.players_cannot_teleport")
                    .withStyle(ChatFormatting.RED), true);
        }
    }

    public void displayCooldownMessage() {
        if (this.portalCooldown >= 10) {
            if (Config.WARP_COOLDOWN_MESSAGE.get()) {
                if (Config.WARP_COOLDOWN_MESSAGE_TICKS.get())
                    this.displayClientMessage(Component.translatable("display.warp_pipes.warp_cooldown.ticks",
                            this.getPortalCooldown()).withStyle(ChatFormatting.RED), true);
                else this.displayClientMessage(Component.translatable("display.warp_pipes.warp_cooldown")
                        .withStyle(ChatFormatting.RED), true);
            }
        }
    }
}
