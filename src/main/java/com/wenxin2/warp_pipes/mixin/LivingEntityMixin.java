package com.wenxin2.warp_pipes.mixin;

import com.wenxin2.warp_pipes.blocks.ClearWarpPipeBlock;
import com.wenxin2.warp_pipes.blocks.WarpPipeBlock;
import com.wenxin2.warp_pipes.blocks.entities.WarpPipeBlockEntity;
import com.wenxin2.warp_pipes.init.Config;
import com.wenxin2.warp_pipes.init.ModTags;
import java.util.Collection;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    private static final int MAX_PARTICLE_AMOUNT = 100;
    private int warpCooldown;

    public LivingEntityMixin(EntityType<?> entityType, Level world) {
        super(entityType, world);
    }

    @Inject(at = @At("TAIL"), method = "baseTick")
    public void baseTick(CallbackInfo ci) {
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

        if (state.getBlock() instanceof ClearWarpPipeBlock)
            this.moveAlongPipe(pos, state);
    }

    protected void moveAlongPipe(BlockPos pos, BlockState state) {
        this.resetFallDistance();
        double d0 = this.getX();
        double d1 = this.getY();
        double d2 = this.getZ();
        Vec3 vec3 = this.getPos(d0, d1, d2);
        d1 = pos.getY();
        boolean flag = false;
        boolean flag1 = false;
        if (state.getBlock() instanceof ClearWarpPipeBlock) {
            flag = !state.getValue(ClearWarpPipeBlock.CLOSED);
            flag1 = !flag;
        }

        Vec3 vec31 = this.getDeltaMovement();
        Vec3i facingNormal;

        // Determine facing normal based on the direction of movement
        if (Math.abs(vec31.x) > Math.abs(vec31.z)) {
            // Moving mainly in the x direction
            facingNormal = vec31.x > 0 ? Direction.EAST.getNormal() : Direction.WEST.getNormal();
        } else {
            // Moving mainly in the z direction
            facingNormal = vec31.z > 0 ? Direction.SOUTH.getNormal() : Direction.NORTH.getNormal();
        }

        Vec3i oppositeNormal = new Vec3i(-facingNormal.getX(), -facingNormal.getY(), -facingNormal.getZ());
        double d4 = oppositeNormal.getX() - facingNormal.getX();
        double d5 = oppositeNormal.getZ() - facingNormal.getZ();
        double d6 = Math.sqrt(d4 * d4 + d5 * d5);
        double d7 = vec31.x * d4 + vec31.z * d5;
        if (d7 < 0.0D) {
            d4 = -d4;
            d5 = -d5;
        }

        double d8 = Math.min(2.0D, vec31.horizontalDistance());
        vec31 = new Vec3(d8 * d4 / d6, vec31.y, d8 * d5 / d6);
        this.setDeltaMovement(vec31);
        Entity entity2 = this.getFirstPassenger();
        if (entity2 instanceof Player) {
            Vec3 vec32 = entity2.getDeltaMovement();
            double d9 = vec32.horizontalDistanceSqr();
            double d11 = this.getDeltaMovement().horizontalDistanceSqr();
            if (d9 > 1.0E-4D && d11 < 0.01D) {
                this.setDeltaMovement(this.getDeltaMovement().add(vec32.x * 0.1D, 0.5D, vec32.z * 0.1D));
                flag1 = false;
            }
        }

        if (flag1) {
            double d22 = this.getDeltaMovement().horizontalDistance();
            if (d22 < 0.03D) {
                this.setDeltaMovement(Vec3.ZERO);
            } else {
                this.setDeltaMovement(this.getDeltaMovement().multiply(0.5D, 0.0D, 0.5D));
            }
        }

        // Update entity's position along the pipe
        double d23 = (double) pos.getX() + 0.5D + (double) facingNormal.getX() * 0.5D;
        double d10 = (double) pos.getZ() + 0.5D + (double) facingNormal.getZ() * 0.5D;
        double d12 = (double) pos.getX() + 0.5D + (double) oppositeNormal.getX() * 0.5D;
        double d13 = (double) pos.getZ() + 0.5D + (double) oppositeNormal.getZ() * 0.5D;
        d4 = d12 - d23;
        d5 = d13 - d10;
        double d14;
        if (d4 == 0.0D) {
            d14 = d2 - (double) pos.getZ();
        } else if (d5 == 0.0D) {
            d14 = d0 - (double) pos.getX();
        } else {
            double d15 = d0 - d23;
            double d16 = d2 - d10;
            d14 = (d15 * d4 + d16 * d5) * 2.0D;
        }

        d0 = d23 + d4 * d14;
        d2 = d10 + d5 * d14;
//        this.setPos(d0, d1, d2);

        // Perform collision check
        VoxelShape collisionShape = state.getCollisionShape(this.level(), pos).move(pos.getX(), pos.getY(), pos.getZ());
        if (!collisionShape.isEmpty()) {
            AABB entityBoundingBox = this.getBoundingBox();
            AABB blockBoundingBox = collisionShape.bounds();

            facingNormal = getFacingNormalForMovement();
            oppositeNormal = new Vec3i(-facingNormal.getX(), -facingNormal.getY(), -facingNormal.getZ());

            // Check if the entity's bounding box intersects with the pipe's bounding box
            boolean intersects = entityBoundingBox.intersects(blockBoundingBox);

            if (intersects) {
                // Calculate the direction towards the center of the hitbox
                Vec3 centerOfHitbox = blockBoundingBox.getCenter();
                Vec3 entityPosition = this.position();
                Vec3 direction = centerOfHitbox.subtract(entityPosition).normalize();

                // Move the entity towards the center of the hitbox
                double distanceToMove = entityBoundingBox.distanceToSqr(centerOfHitbox);
                double maxMoveDistance = Math.sqrt(distanceToMove); // Move at most the distance to the center
                double moveX = direction.x * maxMoveDistance;
                double moveY = direction.y * maxMoveDistance;
                double moveZ = direction.z * maxMoveDistance;

                // Adjust the entity's position
                this.setPos(this.getX() + moveX, this.getY() + moveY, this.getZ() + moveZ);
            }
        }

        this.setPos(d0, d1, d2);
        this.moveEntityOnPipe(pos);
        if (facingNormal.getY() != 0 && Mth.floor(this.getX()) - pos.getX() == facingNormal.getX() && Mth.floor(this.getZ()) - pos.getZ() == facingNormal.getZ()) {
            this.setPos(this.getX(), this.getY() + (double)facingNormal.getY(), this.getZ());
        } else if (oppositeNormal.getY() != 0 && Mth.floor(this.getX()) - pos.getX() == oppositeNormal.getX() && Mth.floor(this.getZ()) - pos.getZ() == oppositeNormal.getZ()) {
            this.setPos(this.getX(), this.getY() + (double)oppositeNormal.getY(), this.getZ());
        }

        this.applyNaturalSlowdown();
        Vec3 vec33 = this.getPos(this.getX(), this.getY(), this.getZ());
        if (vec33 != null && vec3 != null) {
            double d17 = (vec3.y - vec33.y) * 0.05D;
            Vec3 vec34 = this.getDeltaMovement();
            double d18 = vec34.horizontalDistance();
            if (d18 > 0.0D) {
                this.setDeltaMovement(vec34.multiply((d18 + d17) / d18, 1.0D, (d18 + d17) / d18));
            }

            this.setPos(this.getX(), vec33.y, this.getZ());
        }

        int j = Mth.floor(this.getX());
        int i = Mth.floor(this.getZ());
        if (j != pos.getX() || i != pos.getZ()) {
            Vec3 vec35 = this.getDeltaMovement();
            double d26 = vec35.horizontalDistance();
            this.setDeltaMovement(d26 * (double)(j - pos.getX()), vec35.y, d26 * (double)(i - pos.getZ()));
        }

        if (flag) {
            Vec3 vec36 = this.getDeltaMovement();
            double d27 = vec36.horizontalDistance();
            if (d27 > 0.01D) {
                double d19 = 0.06D;
                this.setDeltaMovement(vec36.add(vec36.x / d27 * 0.06D, 0.5D, vec36.z / d27 * 0.06D));
            } else {
                Vec3 vec37 = this.getDeltaMovement();
                double d20 = vec37.x;
                double d21 = vec37.z;
                this.setDeltaMovement(d20, vec37.y, d21);
            }
        }

    }

    private Vec3i getFacingNormalForMovement() {
        Vec3 vec3 = this.getDeltaMovement();
        return Math.abs(vec3.x) > Math.abs(vec3.z) ? (vec3.x > 0 ? Direction.EAST.getNormal() : Direction.WEST.getNormal()) :
                (vec3.z > 0 ? Direction.SOUTH.getNormal() : Direction.NORTH.getNormal());
    }

    @Nullable
    public Vec3 getPos(double x, double y, double z) {
        int i = Mth.floor(x);
        int j = Mth.floor(y);
        int k = Mth.floor(z);

        BlockPos pos = new BlockPos(i, j, k);
        if (this.level().getBlockState(pos).getBlock() instanceof ClearWarpPipeBlock) {
            Vec3i facingNormal;
            Vec3 vec3 = this.getDeltaMovement();
            if (Math.abs(vec3.x) > Math.abs(vec3.z)) {
                // Moving mainly in the x direction
                facingNormal = vec3.x > 0 ? Direction.EAST.getNormal() : Direction.WEST.getNormal();
            } else {
                // Moving mainly in the z direction
                facingNormal = vec3.z > 0 ? Direction.SOUTH.getNormal() : Direction.NORTH.getNormal();
            }
            double d0 = (double)i + 0.5D + (double)facingNormal.getX() * 0.5D;
            double d1 = (double)j + 0.0625D + (double)facingNormal.getY() * 0.5D;
            double d2 = (double)k + 0.5D + (double)facingNormal.getZ() * 0.5D;

            // Adjust position if moving vertically
            if (facingNormal.getY() != 0 && Mth.floor(this.getX()) - i == facingNormal.getX() &&
                    Mth.floor(this.getZ()) - k == facingNormal.getZ()) {
                d1 += facingNormal.getY();
            }

            return new Vec3(d0, d1, d2);
        } else {
            return null;
        }
    }

    public void moveEntityOnPipe(BlockPos pos) {
        Level world = this.level();
        double d24 = this.isVehicle() ? 0.75D : 1.0D;
        double d25 = this.getMaxSpeedWithPipe(world);
        Vec3 vec3d1 = this.getDeltaMovement();
        if (Double.isNaN(vec3d1.x) || Double.isNaN(vec3d1.z)) {
            return;
        }
        double clampedX = Mth.clamp(d24 * vec3d1.x, -d25, d25);
        double clampedZ = Mth.clamp(d24 * vec3d1.z, -d25, d25);
        this.move(MoverType.SELF, new Vec3(clampedX, 0.5D, clampedZ));
    }

    public double getMaxSpeedWithPipe(Level world) { //Non-default because getMaximumSpeed is protected
        BlockPos pos = this.getCurrentPipePos();
        BlockState state = world.getBlockState(pos);
        if (!(state.getBlock() instanceof ClearWarpPipeBlock)) return (this.isInWater() ? 4.0D : 8.0D) / 20.0D;

        //        return Math.min(pipeMaxSpeed, 1.2f);
        return this.isInWater() ? 0.2f : 0.4f;
    }

    public BlockPos getCurrentPipePos()
    {
        int x = Mth.floor(this.getX());
        int y = Mth.floor(this.getY());
        int z = Mth.floor(this.getZ());
        BlockPos pos = new BlockPos(x, y, z);
        if (this.level().getBlockState(pos.below()).getBlock() instanceof ClearWarpPipeBlock) pos = pos.below();
        return pos;
    }

    protected void applyNaturalSlowdown() {
        double d0 = this.isVehicle() ? 0.997D : 0.96D;
        Vec3 vec3 = this.getDeltaMovement();
        vec3 = vec3.multiply(d0, 0.0D, d0);
        if (this.isInWater()) {
            vec3 = vec3.scale(0.95F);
        }

        this.setDeltaMovement(vec3);
    }

    public void spawnParticles(Level world) {
        RandomSource random = world.getRandom();

        // Calculate a scaling factor based on entity dimensions
        float scaleFactor = this.getBbHeight() * this.getBbWidth();
        // Calculate the particle count based on the scaling factor
        int particleCount = (int) (scaleFactor * 40);
        // Ensure particle count does not exceed the maximum limit
        particleCount = Math.min(particleCount, MAX_PARTICLE_AMOUNT);

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
                && this.getPersistentData().getBoolean("warp_pipes:can_warp")) {
            warpPos = warpPipeBE.destinationPos;
            int entityId = this.getId();

            if (!world.isClientSide() && WarpPipeBlock.teleportedEntities.getOrDefault(entityId, false)) {
                this.spawnParticles(world);

                // Reset the teleport status for the entity
                WarpPipeBlock.teleportedEntities.put(entityId, false);
            }

            if (this.getWarpCooldown() == 0 && warpPipeBE.hasDestinationPos()) {
                if (stateAboveEntity.getValue(WarpPipeBlock.FACING) == Direction.DOWN
                        && (entityX < blockX + 1 && entityX > blockX) && (entityZ < blockZ + 1 && entityZ > blockZ)) {
                    if (warpPos != null && world.getBlockState(warpPos).getBlock() instanceof WarpPipeBlock)
                        WarpPipeBlock.warp(this, warpPos, world, stateAboveEntity);
                    else if (warpPipeBE.getUuid() != null && WarpPipeBlock.findMatchingUUID(warpPipeBE.getUuid(), world, pos) != null)
                        WarpPipeBlock.warp(this, WarpPipeBlock.findMatchingUUID(warpPipeBE.getUuid(), world, pos), world, stateAboveEntity);
                    this.setWarpCooldown(Config.WARP_COOLDOWN.get());
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

        if (!state.getValue(WarpPipeBlock.CLOSED) && blockEntity instanceof WarpPipeBlockEntity warpPipeBE
                && !warpPipeBE.preventWarp && Config.TELEPORT_MOBS.get() && !this.getType().is(ModTags.WARP_BlACKLIST)
                && this.getPersistentData().getBoolean("warp_pipes:can_warp")) {
            warpPos = warpPipeBE.destinationPos;
            int entityId = this.getId();

            if (!world.isClientSide() && WarpPipeBlock.teleportedEntities.getOrDefault(entityId, false)) {
                this.spawnParticles(world);

                // Reset the teleport status for the entity
                WarpPipeBlock.teleportedEntities.put(entityId, false);
            }

            if (this.getWarpCooldown() == 0 && warpPipeBE.hasDestinationPos()) {
                if (state.getValue(WarpPipeBlock.FACING) == Direction.UP && (entityY > blockY - 1)
                        && (entityX < blockX + 1 && entityX > blockX) && (entityZ < blockZ + 1 && entityZ > blockZ)) {
                    if (warpPos != null && world.getBlockState(warpPos).getBlock() instanceof WarpPipeBlock)
                        WarpPipeBlock.warp(this, warpPos, world, state);
                    else if (warpPipeBE.getUuid() != null && WarpPipeBlock.findMatchingUUID(warpPipeBE.getUuid(), world, pos) != null)
                        WarpPipeBlock.warp(this, WarpPipeBlock.findMatchingUUID(warpPipeBE.getUuid(), world, pos), world, state);
                    this.setWarpCooldown(Config.WARP_COOLDOWN.get());
                }
                if (state.getValue(WarpPipeBlock.FACING) == Direction.NORTH
                        && (entityX < blockX + 1 && entityX > blockX) && (entityY >= blockY && entityY < blockY + 0.75) && (entityZ < blockZ)) {
                    if (warpPos != null && world.getBlockState(warpPos).getBlock() instanceof WarpPipeBlock)
                        WarpPipeBlock.warp(this, warpPos, world, state);
                    else if (warpPipeBE.getUuid() != null && WarpPipeBlock.findMatchingUUID(warpPipeBE.getUuid(), world, pos) != null)
                        WarpPipeBlock.warp(this, WarpPipeBlock.findMatchingUUID(warpPipeBE.getUuid(), world, pos), world, state);
                    this.setWarpCooldown(Config.WARP_COOLDOWN.get());
                }
                if (state.getValue(WarpPipeBlock.FACING) == Direction.SOUTH
                        && (entityX < blockX + 1 && entityX > blockX) && (entityY >= blockY && entityY < blockY + 0.75) && (entityZ > blockZ)) {
                    if (warpPos != null && world.getBlockState(warpPos).getBlock() instanceof WarpPipeBlock)
                        WarpPipeBlock.warp(this, warpPos, world, state);
                    else if (warpPipeBE.getUuid() != null && WarpPipeBlock.findMatchingUUID(warpPipeBE.getUuid(), world, pos) != null)
                        WarpPipeBlock.warp(this, WarpPipeBlock.findMatchingUUID(warpPipeBE.getUuid(), world, pos), world, state);
                    this.setWarpCooldown(Config.WARP_COOLDOWN.get());
                }
                if (state.getValue(WarpPipeBlock.FACING) == Direction.EAST
                        && (entityX > blockX) && (entityY >= blockY && entityY < blockY + 0.75) && (entityZ < blockZ + 1 && entityZ > blockZ)) {
                    if (warpPos != null && world.getBlockState(warpPos).getBlock() instanceof WarpPipeBlock)
                        WarpPipeBlock.warp(this, warpPos, world, state);
                    else if (warpPipeBE.getUuid() != null && WarpPipeBlock.findMatchingUUID(warpPipeBE.getUuid(), world, pos) != null)
                        WarpPipeBlock.warp(this, WarpPipeBlock.findMatchingUUID(warpPipeBE.getUuid(), world, pos), world, state);
                    this.setWarpCooldown(Config.WARP_COOLDOWN.get());
                }
                if (state.getValue(WarpPipeBlock.FACING) == Direction.WEST
                        && (entityX < blockX) && (entityY >= blockY && entityY < blockY + 0.75) && (entityZ < blockZ + 1 && entityZ > blockZ)) {
                    if (warpPos != null && world.getBlockState(warpPos).getBlock() instanceof WarpPipeBlock)
                        WarpPipeBlock.warp(this, warpPos, world, state);
                    else if (warpPipeBE.getUuid() != null && WarpPipeBlock.findMatchingUUID(warpPipeBE.getUuid(), world, pos) != null)
                        WarpPipeBlock.warp(this, WarpPipeBlock.findMatchingUUID(warpPipeBE.getUuid(), world, pos), world, state);
                    this.setWarpCooldown(Config.WARP_COOLDOWN.get());
                }
            }
        }
    }
}
