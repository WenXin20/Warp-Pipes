package com.wenxin2.warp_pipes.mixin;

import com.wenxin2.warp_pipes.blocks.WarpPipeBlock;
import com.wenxin2.warp_pipes.blocks.entities.WarpPipeBlockEntity;
import com.wenxin2.warp_pipes.init.Config;
import com.wenxin2.warp_pipes.init.ModTags;
import java.util.Collection;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    public LivingEntityMixin(EntityType<?> entityType, Level world) {
        super(entityType, world);
    }
    private static final int MAX_PARTICLE_COUNT = 100;

    @Inject(at = @At("TAIL"), method = "baseTick")
    public void baseTick(CallbackInfo ci) {
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

        // Calculate a scaling factor based on entity dimensions
        float scaleFactor = this.getBbHeight() * this.getBbWidth(); // You can adjust this formula as needed

        // Calculate the particle count based on the scaling factor
        int particleCount = (int) (scaleFactor * 40); // You can adjust the multiplier to control particle density

        // Ensure particle count does not exceed the maximum limit
        particleCount = Math.min(particleCount, MAX_PARTICLE_COUNT);

        if (!state.getValue(WarpPipeBlock.CLOSED) && blockEntity instanceof WarpPipeBlockEntity warpPipeBE
                && Config.TELEPORT_MOBS.get() && !this.getType().is(ModTags.WARP_BlACKLIST)) {
            warpPos = warpPipeBE.destinationPos;
            int entityId = this.getId();

            if (!world.isClientSide() && WarpPipeBlock.teleportedEntities.getOrDefault(entityId, false)) {
                Collection<ServerPlayer> players = ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers();
                for (ServerPlayer player : players) {
                    for (int i = 0; i < particleCount; ++i) {
                        player.connection.send(new ClientboundLevelParticlesPacket(
                                ParticleTypes.ENCHANT,      // Particle type
                                false,                       // Long distance
                                this.getRandomX(0.5D), this.getRandomY(), this.getRandomZ(0.5D), // Position
                                (random.nextFloat() - 0.5F) * 2.0F, -random.nextFloat(),
                                (random.nextFloat() - 0.5F) * 2.0F, // Motion
                                0,                          // Particle data
                                2                           // Particle count
                        ));
                    }
                }
                // Reset the teleport status for the entity
                WarpPipeBlock.teleportedEntities.put(entityId, false);
            }

            if (this.portalCooldown == 0 && warpPipeBE.hasDestinationPos()) {
                if (state.getValue(WarpPipeBlock.FACING) == Direction.UP && (entityY > blockY - 1)
                        && (entityX < blockX + 1 && entityX > blockX) && (entityZ < blockZ + 1 && entityZ > blockZ)) {
                    if (warpPipeBE.getUuid() != null && WarpPipeBlock.findMatchingUUID(warpPipeBE.getUuid(), world, pos) != null)
                        WarpPipeBlock.warp(this, WarpPipeBlock.findMatchingUUID(warpPipeBE.getUuid(), world, pos), world, state);
                    else WarpPipeBlock.warp(this, warpPos, world, state);
                    this.setPortalCooldown();
                    this.portalCooldown = Config.WARP_COOLDOWN.get();
                }
                if (state.getValue(WarpPipeBlock.FACING) == Direction.DOWN && (this.getBlockY() < blockY)
                        && (entityX < blockX + 1 && entityX > blockX) && (entityZ < blockZ + 1 && entityZ > blockZ)) {
                    if (warpPipeBE.getUuid() != null && WarpPipeBlock.findMatchingUUID(warpPipeBE.getUuid(), world, pos) != null)
                        WarpPipeBlock.warp(this, WarpPipeBlock.findMatchingUUID(warpPipeBE.getUuid(), world, pos), world, state);
                    else WarpPipeBlock.warp(this, warpPos, world, state);
                    this.setPortalCooldown();
                    this.portalCooldown = Config.WARP_COOLDOWN.get();
                }
                if (state.getValue(WarpPipeBlock.FACING) == Direction.NORTH
                        && (entityX < blockX + 1 && entityX > blockX) && (entityY >= blockY && entityY < blockY + 0.75) && (entityZ < blockZ)) {
                    if (warpPipeBE.getUuid() != null && WarpPipeBlock.findMatchingUUID(warpPipeBE.getUuid(), world, pos) != null)
                        WarpPipeBlock.warp(this, WarpPipeBlock.findMatchingUUID(warpPipeBE.getUuid(), world, pos), world, state);
                    else WarpPipeBlock.warp(this, warpPos, world, state);
                    this.setPortalCooldown();
                    this.portalCooldown = Config.WARP_COOLDOWN.get();
                }
                if (state.getValue(WarpPipeBlock.FACING) == Direction.SOUTH
                        && (entityX < blockX + 1 && entityX > blockX) && (entityY >= blockY && entityY < blockY + 0.75) && (entityZ > blockZ)) {
                    if (warpPipeBE.getUuid() != null && WarpPipeBlock.findMatchingUUID(warpPipeBE.getUuid(), world, pos) != null)
                        WarpPipeBlock.warp(this, WarpPipeBlock.findMatchingUUID(warpPipeBE.getUuid(), world, pos), world, state);
                    else WarpPipeBlock.warp(this, warpPos, world, state);
                    this.setPortalCooldown();
                    this.portalCooldown = Config.WARP_COOLDOWN.get();
                }
                if (state.getValue(WarpPipeBlock.FACING) == Direction.EAST
                        && (entityX > blockX) && (entityY >= blockY && entityY < blockY + 0.75) && (entityZ < blockZ + 1 && entityZ > blockZ)) {
                    if (warpPipeBE.getUuid() != null && WarpPipeBlock.findMatchingUUID(warpPipeBE.getUuid(), world, pos) != null)
                        WarpPipeBlock.warp(this, WarpPipeBlock.findMatchingUUID(warpPipeBE.getUuid(), world, pos), world, state);
                    else WarpPipeBlock.warp(this, warpPos, world, state);
                    this.setPortalCooldown();
                    this.portalCooldown = Config.WARP_COOLDOWN.get();
                }
                if (state.getValue(WarpPipeBlock.FACING) == Direction.WEST
                        && (entityX < blockX) && (entityY >= blockY && entityY < blockY + 0.75) && (entityZ < blockZ + 1 && entityZ > blockZ)) {
                    if (warpPipeBE.getUuid() != null && WarpPipeBlock.findMatchingUUID(warpPipeBE.getUuid(), world, pos) != null)
                        WarpPipeBlock.warp(this, WarpPipeBlock.findMatchingUUID(warpPipeBE.getUuid(), world, pos), world, state);
                    else WarpPipeBlock.warp(this, warpPos, world, state);
                    this.setPortalCooldown();
                    this.portalCooldown = Config.WARP_COOLDOWN.get();
                }
            }
        }
    }
}
