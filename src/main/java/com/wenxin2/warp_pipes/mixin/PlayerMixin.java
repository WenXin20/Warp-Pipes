package com.wenxin2.warp_pipes.mixin;

import com.wenxin2.warp_pipes.blocks.WarpPipeBlock;
import com.wenxin2.warp_pipes.blocks.entities.WarpPipeBlockEntity;
import java.util.Collection;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Player.class)
public abstract class PlayerMixin extends Entity {
    public PlayerMixin(EntityType<?> entityType, Level world) {
        super(entityType, world);
    }

    private static final int MAX_PARTICLE_COUNT = 100;

    @Override
    public void baseTick() {
        Level world = this.getLevel();
        BlockPos pos = this.blockPosition();
        BlockState state = world.getBlockState(pos);

        for (Direction facing : Direction.values()) {
            BlockPos offsetPos = pos.relative(facing);
            BlockState offsetState = world.getBlockState(offsetPos);

            if (offsetState.getBlock() instanceof WarpPipeBlock) {
                this.entityInside(facing, offsetPos);
            }
            if (state.getBlock() instanceof WarpPipeBlock) {
                this.entityInside(facing, pos);
            }
        }

        super.baseTick();
    }
    
    public void entityInside(Direction pipeDirection, BlockPos pos) {
        Level world = this.getLevel();
        BlockState state = world.getBlockState(pos);
        BlockEntity blockEntity = world.getBlockEntity(pos);
        BlockPos destinationPos = null;

        double entityX = this.getX();
        double entityY = this.getY();
        double entityZ = this.getZ();

        int blockX = pos.getX();
        int blockY = pos.getY();
        int blockZ = pos.getZ();

        // Calculate random motion values within the desired range
        float entityHeight = this.getBbHeight();
        float entityWidth = this.getBbWidth();
        float motionRangeMin = 0.1F;
        float motionX = random.nextFloat() * (entityWidth - motionRangeMin) + motionRangeMin;
        float motionY = random.nextFloat() * (entityHeight - motionRangeMin) + motionRangeMin;
        float motionZ = random.nextFloat() * (entityWidth - motionRangeMin) + motionRangeMin;

        // Calculate a scaling factor based on entity dimensions
        float scaleFactor = entityHeight * entityWidth; // You can adjust this formula as needed

        // Calculate the particle count based on the scaling factor
        int particleCount = (int) (scaleFactor * 40); // You can adjust the multiplier to control particle density

        // Ensure particle count does not exceed the maximum limit
        particleCount = Math.min(particleCount, MAX_PARTICLE_COUNT);

        // Restrict motionY to the entity's height
        motionY = Math.max(-entityHeight, Math.min(entityHeight, motionY));

        // Calculate the center point at the bottom of the entity
        double centerX = entityX;
        double centerY = entityY - entityHeight / 2;
        double centerZ = entityZ;

        // Calculate the motion towards the center point
        double motionToCenterX = (centerX - entityX) / particleCount;
        double motionToCenterY = (centerY - entityY) / particleCount;
        double motionToCenterZ = (centerZ - entityZ) / particleCount;

        if (!state.getValue(WarpPipeBlock.CLOSED) && blockEntity instanceof WarpPipeBlockEntity warpPipeBE) {
            destinationPos = warpPipeBE.destinationPos;
            int entityId = this.getId();

            if (!world.isClientSide() && WarpPipeBlock.teleportedEntities.getOrDefault(entityId, false)) {
                Collection<ServerPlayer> players = ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers();
                for (ServerPlayer player : players) {
                    for (int i = 0; i < particleCount; ++i) {
                        double posX = entityX + motionToCenterX * i;
                        double posY = entityY + entityHeight + motionToCenterY * i;
                        double posZ = entityZ + motionToCenterZ * i;
                        player.connection.send(new ClientboundLevelParticlesPacket(
                                ParticleTypes.ENCHANT,      // Particle type
                                true,                       // Long distance
                                posX, posY, posZ,           // Position
                                motionX, -motionY, motionZ, // Motion
                                0,                          // Particle data
                                2                           // Particle count
                        ));
                    }
                }
                // Reset the teleport status for the entity
                WarpPipeBlock.teleportedEntities.put(entityId, false);
            }

            if (this.portalCooldown == 0 && destinationPos != null) {

                if (state.getValue(WarpPipeBlock.FACING) == Direction.UP && this.isShiftKeyDown() && (entityY + this.getBbHeight() >= blockY - 1)
                        && (entityX < blockX + 1 && entityX > blockX) && (entityZ < blockZ + 1 && entityZ > blockZ)) {
                    WarpPipeBlock.warp(this, destinationPos, world, state);
                    this.setPortalCooldown();
                    this.portalCooldown = 20;
                }
                if (state.getValue(WarpPipeBlock.FACING) == Direction.DOWN && (this.getBlockY() < blockY)
                        && (entityX < blockX + 1 && entityX > blockX) && (entityZ < blockZ + 1 && entityZ > blockZ)) {
                    WarpPipeBlock.warp(this, destinationPos, world, state);
                    System.out.println("EntityY: " + (this.getBlockY()) + ", BlockY: " + (blockY));
                    this.setPortalCooldown();
                    this.portalCooldown = 20;
                }
                if (state.getValue(WarpPipeBlock.FACING) == Direction.NORTH && !this.isShiftKeyDown() && this.getMotionDirection() == Direction.SOUTH
                        && (entityX < blockX + 1 && entityX > blockX) && (entityY >= blockY && entityY < blockY + 0.75) && (entityZ < blockZ)) {
                    WarpPipeBlock.warp(this, destinationPos, world, state);
                    this.setPortalCooldown();
                    this.portalCooldown = 20;
                }
                if (state.getValue(WarpPipeBlock.FACING) == Direction.SOUTH && !this.isShiftKeyDown() && this.getMotionDirection() == Direction.NORTH
                        && (entityX < blockX + 1 && entityX > blockX) && (entityY >= blockY && entityY < blockY + 0.75) && (entityZ > blockZ + 0.25)) {
                    WarpPipeBlock.warp(this, destinationPos, world, state);
                    this.setPortalCooldown();
                    this.portalCooldown = 20;
                }
                if (state.getValue(WarpPipeBlock.FACING) == Direction.EAST && !this.isShiftKeyDown() && this.getMotionDirection() == Direction.WEST
                        && (entityX > blockX) && (entityY >= blockY && entityY < blockY + 0.75) && (entityZ < blockZ + 1 && entityZ > blockZ)) {
                    WarpPipeBlock.warp(this, destinationPos, world, state);
                    this.setPortalCooldown();
                    this.portalCooldown = 20;
                }
                if (state.getValue(WarpPipeBlock.FACING) == Direction.WEST && !this.isShiftKeyDown() && this.getMotionDirection() == Direction.EAST
                        && (entityX < blockX) && (entityY >= blockY && entityY < blockY + 0.75) && (entityZ < blockZ + 1 && entityZ > blockZ)) {
                    WarpPipeBlock.warp(this, destinationPos, world, state);
                    this.setPortalCooldown();
                    this.portalCooldown = 20;
                }
            }
        }
    }
}
