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
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Shadow public abstract Level level();

    @Shadow public abstract double getX();

    @Shadow public abstract double getY();
    @Shadow public abstract double getZ();

    @Shadow public abstract float getBbHeight();

    @Shadow public abstract float getBbWidth();

    @Shadow public abstract int getId();

    @Shadow public abstract BlockPos blockPosition();
    @Shadow @Final protected RandomSource random;

    @Shadow public abstract int getBlockY();

    @Shadow public abstract double getRandomX(double p_20209_);

    @Shadow public abstract double getRandomY();

    @Shadow public abstract double getRandomZ(double p_20263_);

    @Shadow public abstract EntityType<?> getType();

    @Shadow public abstract void setDeltaMovement(Vec3 p_20257_);

    @Shadow public abstract void resetFallDistance();

    @Shadow public abstract Vec3 getDeltaMovement();

    @Shadow @Nullable public abstract Entity getFirstPassenger();

    @Shadow public abstract void setPos(Vec3 p_146885_);

    @Shadow public abstract boolean isVehicle();

    @Shadow public abstract void move(MoverType p_19973_, Vec3 p_19974_);

    @Shadow public abstract boolean isInWater();

    private static final int MAX_PARTICLE_AMOUNT = 100;
    private int warpCooldown;

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

//        this.moveAlongPipe(pos, state);
    }

//    protected void moveAlongPipe(BlockPos pos, BlockState state) {
//        this.resetFallDistance();
//        double d0 = this.getX();
//        double d1 = this.getY();
//        double d2 = this.getZ();
//        Vec3 vec3 = this.getPos(d0, d1, d2);
//        d1 = pos.getY();
//        boolean flag = false;
//        boolean flag1 = false;
////        BaseRailBlock baserailblock = (BaseRailBlock) state.getBlock();
//        if (state.getBlock() instanceof ClearWarpPipeBlock) {
//            flag = !state.getValue(ClearWarpPipeBlock.CLOSED);
//            flag1 = !flag;
//        }
//
////        double d3 = getSlopeAdjustment();
////        if (this.isInWater()) {
////            d3 *= 0.2D;
////        }
//
//        Vec3 vec31 = this.getDeltaMovement();
////        Pair<Vec3i, Vec3i> pair = exits(railshape);
//        Vec3i vec3i = Direction.WEST.getNormal();
//        Vec3i vec3i1 = Direction.EAST.getNormal();
//        double d4 = vec3i1.getX() - vec3i.getX();
//        double d5 = vec3i1.getZ() - vec3i.getZ();
//        double d6 = Math.sqrt(d4 * d4 + d5 * d5);
//        double d7 = vec31.x * d4 + vec31.z * d5;
//        if (d7 < 0.0D) {
//            d4 = -d4;
//            d5 = -d5;
//        }
//
//        double d8 = Math.min(2.0D, vec31.horizontalDistance());
//        vec31 = new Vec3(d8 * d4 / d6, vec31.y, d8 * d5 / d6);
//        this.setDeltaMovement(vec31);
//        Entity entity2 = this.getFirstPassenger();
//        if (entity2 instanceof Player) {
//            Vec3 vec32 = entity2.getDeltaMovement();
//            double d9 = vec32.horizontalDistanceSqr();
//            double d11 = this.getDeltaMovement().horizontalDistanceSqr();
//            if (d9 > 1.0E-4D && d11 < 0.01D) {
//                this.setDeltaMovement(this.getDeltaMovement().add(vec32.x * 0.1D, 0.0D, vec32.z * 0.1D));
//                flag1 = false;
//            }
//        }
//
//        if (flag1) {
//            double d22 = this.getDeltaMovement().horizontalDistance();
//            if (d22 < 0.03D) {
//                this.setDeltaMovement(Vec3.ZERO);
//            } else {
//                this.setDeltaMovement(this.getDeltaMovement().multiply(0.5D, 0.0D, 0.5D));
//            }
//        }
//
//        double d23 = (double)pos.getX() + 0.5D + (double)vec3i.getX() * 0.5D;
//        double d10 = (double)pos.getZ() + 0.5D + (double)vec3i.getZ() * 0.5D;
//        double d12 = (double)pos.getX() + 0.5D + (double)vec3i1.getX() * 0.5D;
//        double d13 = (double)pos.getZ() + 0.5D + (double)vec3i1.getZ() * 0.5D;
//        d4 = d12 - d23;
//        d5 = d13 - d10;
//        double d14;
//        if (d4 == 0.0D) {
//            d14 = d2 - (double)pos.getZ();
//        } else if (d5 == 0.0D) {
//            d14 = d0 - (double)pos.getX();
//        } else {
//            double d15 = d0 - d23;
//            double d16 = d2 - d10;
//            d14 = (d15 * d4 + d16 * d5) * 2.0D;
//        }
//
//        d0 = d23 + d4 * d14;
//        d2 = d10 + d5 * d14;
//        this.setPos(d0, d1, d2);
////        this.moveEntityOnPipe(pos, entity);
//        if (vec3i.getY() != 0 && Mth.floor(this.getX()) - pos.getX() == vec3i.getX() && Mth.floor(this.getZ()) - pos.getZ() == vec3i.getZ()) {
//            this.setPos(this.getX(), this.getY() + (double)vec3i.getY(), this.getZ());
//        } else if (vec3i1.getY() != 0 && Mth.floor(this.getX()) - pos.getX() == vec3i1.getX() && Mth.floor(this.getZ()) - pos.getZ() == vec3i1.getZ()) {
//            this.setPos(this.getX(), this.getY() + (double)vec3i1.getY(), this.getZ());
//        }
//
//        this.applyNaturalSlowdown();
//        Vec3 vec33 = this.getPos(this.getX(), this.getY(), this.getZ());
//        if (vec33 != null && vec3 != null) {
//            double d17 = (vec3.y - vec33.y) * 0.05D;
//            Vec3 vec34 = this.getDeltaMovement();
//            double d18 = vec34.horizontalDistance();
//            if (d18 > 0.0D) {
//                this.setDeltaMovement(vec34.multiply((d18 + d17) / d18, 1.0D, (d18 + d17) / d18));
//            }
//
//            this.setPos(this.getX(), vec33.y, this.getZ());
//        }
//
//        int j = Mth.floor(this.getX());
//        int i = Mth.floor(this.getZ());
//        if (j != pos.getX() || i != pos.getZ()) {
//            Vec3 vec35 = this.getDeltaMovement();
//            double d26 = vec35.horizontalDistance();
//            this.setDeltaMovement(d26 * (double)(j - pos.getX()), vec35.y, d26 * (double)(i - pos.getZ()));
//        }
//
//        if (flag) {
//            Vec3 vec36 = this.getDeltaMovement();
//            double d27 = vec36.horizontalDistance();
//            if (d27 > 0.01D) {
//                double d19 = 0.06D;
//                this.setDeltaMovement(vec36.add(vec36.x / d27 * 0.06D, 0.0D, vec36.z / d27 * 0.06D));
//            } else {
//                Vec3 vec37 = this.getDeltaMovement();
//                double d20 = vec37.x;
//                double d21 = vec37.z;
////                if (railshape == RailShape.EAST_WEST) {
////                    if (this.isRedstoneConductor(pos.west())) {
////                        d20 = 0.02D;
////                    } else if (this.isRedstoneConductor(pos.east())) {
////                        d20 = -0.02D;
////                    }
////                } else {
////                    if (railshape != RailShape.NORTH_SOUTH) {
////                        return;
////                    }
////
////                    if (this.isRedstoneConductor(pos.north())) {
////                        d21 = 0.02D;
////                    } else if (this.isRedstoneConductor(pos.south())) {
////                        d21 = -0.02D;
////                    }
////                }
//
//                this.setDeltaMovement(d20, vec37.y, d21);
//            }
//        }
//
//    }

    @Nullable
    public Vec3 getPos(double x, double y, double z) {
        int i = Mth.floor(x);
        int j = Mth.floor(y);
        int k = Mth.floor(z);
        if (this.level().getBlockState(new BlockPos(i, j - 1, k)).is(BlockTags.RAILS)) {
            --j;
        }

        BlockState state = this.level().getBlockState(new BlockPos(i, j, k));
        if (state.getBlock() instanceof ClearWarpPipeBlock) {
//            RailShape railshape = ((BaseRailBlock)state.getBlock()).getRailDirection(state, this.level(), new BlockPos(i, j, k), this);
//            Pair<Vec3i, Vec3i> pair = exits(railshape);
            Vec3i vec3i = Direction.WEST.getNormal();
            Vec3i vec3i1 = Direction.EAST.getNormal();
            double d0 = (double)i + 0.5D + (double)vec3i.getX() * 0.5D;
            double d1 = (double)j + 0.0625D + (double)vec3i.getY() * 0.5D;
            double d2 = (double)k + 0.5D + (double)vec3i.getZ() * 0.5D;
            double d3 = (double)i + 0.5D + (double)vec3i1.getX() * 0.5D;
            double d4 = (double)j + 0.0625D + (double)vec3i1.getY() * 0.5D;
            double d5 = (double)k + 0.5D + (double)vec3i1.getZ() * 0.5D;
            double d6 = d3 - d0;
            double d7 = (d4 - d1) * 2.0D;
            double d8 = d5 - d2;
            double d9;
            if (d6 == 0.0D) {
                d9 = z - (double)k;
            } else if (d8 == 0.0D) {
                d9 = x - (double)i;
            } else {
                double d10 = x - d0;
                double d11 = z - d2;
                d9 = (d10 * d6 + d11 * d8) * 2.0D;
            }

            x = d0 + d6 * d9;
            y = d1 + d7 * d9;
            z = d2 + d8 * d9;
            if (d7 < 0.0D) {
                ++y;
            } else if (d7 > 0.0D) {
                y += 0.5D;
            }

            return new Vec3(x, y, z);
        } else {
            return null;
        }
    }

    public void moveEntityOnPipe(BlockPos pos, Entity entity) {
        if (entity == null) {
            return;
        }

        Level world = this.level();
        double d24 = this.isVehicle() ? 0.75D : 1.0D;
        double d25 = this.getMaxSpeedWithPipe(world);
        Vec3 vec3d1 = this.getDeltaMovement();
        if (Double.isNaN(vec3d1.x) || Double.isNaN(vec3d1.z)) {
            return;
        }
        double clampedX = Mth.clamp(d24 * vec3d1.x, -d25, d25);
        double clampedZ = Mth.clamp(d24 * vec3d1.z, -d25, d25);
        this.move(MoverType.SELF, new Vec3(clampedX, 0.0D, clampedZ));
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
                && !warpPipeBE.preventWarp && Config.TELEPORT_PLAYERS.get() && !this.getType().is(ModTags.WARP_BlACKLIST)) {
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
                        WarpPipeBlock.warp((Entity) (Object) this, warpPos, world, stateAboveEntity);
                    else if (warpPipeBE.getUuid() != null && WarpPipeBlock.findMatchingUUID(warpPipeBE.getUuid(), world, pos) != null)
                        WarpPipeBlock.warp((Entity) (Object) this, WarpPipeBlock.findMatchingUUID(warpPipeBE.getUuid(), world, pos), world, stateAboveEntity);
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
                && !warpPipeBE.preventWarp && Config.TELEPORT_NON_MOBS.get() && !this.getType().is(ModTags.WARP_BlACKLIST)) {
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
                        WarpPipeBlock.warp((Entity) (Object) this, warpPos, world, state);
                    else if (warpPipeBE.getUuid() != null && WarpPipeBlock.findMatchingUUID(warpPipeBE.getUuid(), world, pos) != null)
                        WarpPipeBlock.warp((Entity) (Object) this, WarpPipeBlock.findMatchingUUID(warpPipeBE.getUuid(), world, pos), world, state);
                    this.setWarpCooldown(Config.WARP_COOLDOWN.get());
                }
                if (state.getValue(WarpPipeBlock.FACING) == Direction.NORTH
                        && (entityX < blockX + 1 && entityX > blockX) && (entityY >= blockY && entityY < blockY + 0.75) && (entityZ < blockZ)) {
                    if (warpPos != null && world.getBlockState(warpPos).getBlock() instanceof WarpPipeBlock)
                        WarpPipeBlock.warp((Entity) (Object) this, warpPos, world, state);
                    else if (warpPipeBE.getUuid() != null && WarpPipeBlock.findMatchingUUID(warpPipeBE.getUuid(), world, pos) != null)
                        WarpPipeBlock.warp((Entity) (Object) this, WarpPipeBlock.findMatchingUUID(warpPipeBE.getUuid(), world, pos), world, state);
                    this.setWarpCooldown(Config.WARP_COOLDOWN.get());
                }
                if (state.getValue(WarpPipeBlock.FACING) == Direction.SOUTH
                        && (entityX < blockX + 1 && entityX > blockX) && (entityY >= blockY && entityY < blockY + 0.75) && (entityZ > blockZ)) {
                    if (warpPos != null && world.getBlockState(warpPos).getBlock() instanceof WarpPipeBlock)
                        WarpPipeBlock.warp((Entity) (Object) this, warpPos, world, state);
                    else if (warpPipeBE.getUuid() != null && WarpPipeBlock.findMatchingUUID(warpPipeBE.getUuid(), world, pos) != null)
                        WarpPipeBlock.warp((Entity) (Object) this, WarpPipeBlock.findMatchingUUID(warpPipeBE.getUuid(), world, pos), world, state);
                    this.setWarpCooldown(Config.WARP_COOLDOWN.get());
                }
                if (state.getValue(WarpPipeBlock.FACING) == Direction.EAST
                        && (entityX > blockX) && (entityY >= blockY && entityY < blockY + 0.75) && (entityZ < blockZ + 1 && entityZ > blockZ)) {
                    if (warpPos != null && world.getBlockState(warpPos).getBlock() instanceof WarpPipeBlock)
                        WarpPipeBlock.warp((Entity) (Object) this, warpPos, world, state);
                    else if (warpPipeBE.getUuid() != null && WarpPipeBlock.findMatchingUUID(warpPipeBE.getUuid(), world, pos) != null)
                        WarpPipeBlock.warp((Entity) (Object) this, WarpPipeBlock.findMatchingUUID(warpPipeBE.getUuid(), world, pos), world, state);
                    this.setWarpCooldown(Config.WARP_COOLDOWN.get());
                }
                if (state.getValue(WarpPipeBlock.FACING) == Direction.WEST
                        && (entityX < blockX) && (entityY >= blockY && entityY < blockY + 0.75) && (entityZ < blockZ + 1 && entityZ > blockZ)) {
                    if (warpPos != null && world.getBlockState(warpPos).getBlock() instanceof WarpPipeBlock)
                        WarpPipeBlock.warp((Entity) (Object) this, warpPos, world, state);
                    else if (warpPipeBE.getUuid() != null && WarpPipeBlock.findMatchingUUID(warpPipeBE.getUuid(), world, pos) != null)
                        WarpPipeBlock.warp((Entity) (Object) this, WarpPipeBlock.findMatchingUUID(warpPipeBE.getUuid(), world, pos), world, state);
                    this.setWarpCooldown(Config.WARP_COOLDOWN.get());
                }
            }
        }
    }
}
