package com.wenxin2.warp_pipes.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.wenxin2.warp_pipes.blocks.WarpPipeBlock;
import com.wenxin2.warp_pipes.integration.sable_compat.SableProvider;
import com.wenxin2.warp_pipes.registries.ConfigRegistry;
import com.wenxin2.warp_pipes.registries.DataAttachmentRegistry;
import com.wenxin2.warp_pipes.registries.ModRegistry;
import com.wenxin2.warp_pipes.utils.WP$BlockWarpEntityHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.fml.ModList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityMixin implements WP$BlockWarpEntityHandler {
    @Shadow public abstract Level level();
    @Shadow public abstract double getX();
    @Shadow public abstract double getY();
    @Shadow public abstract double getZ();
    @Shadow public abstract int getId();
    @Shadow public abstract BlockPos blockPosition();
    @Shadow public abstract EntityType<?> getType();
    @Shadow public abstract void setPos(Vec3 vec3);

    @Override
    public boolean wp$getBlockWarpTeleportConfig(Entity entity) {
        return ConfigRegistry.TELEPORT_NON_MOBS.get();
    }

    @Inject(at = @At("TAIL"), method = "tick")
    public void tick(CallbackInfo ci) {
        Entity entity = (Entity) (Object) this;
        Level level = entity.level();
        BlockPos pos = entity.blockPosition();
        BlockPos posAboveEntity = pos.above(Math.round(entity.getBbHeight()));
        BlockState state = level.getBlockState(pos);
        BlockState stateAboveEntity = level.getBlockState(posAboveEntity);
        Vec3 motion = entity.getDeltaMovement();
        AABB aboveBox = entity.getBoundingBox()
                .deflate(0.2, 0.0, 0.2)
                .expandTowards(0, motion.y + 0.2, 0);
        BlockPos min = BlockPos.containing(aboveBox.minX, aboveBox.minY, aboveBox.minZ);
        BlockPos max = BlockPos.containing(aboveBox.maxX, aboveBox.maxY, aboveBox.maxZ);

        for (Direction facing : Direction.values()) {
            BlockPos posOffset = pos.relative(facing);
            BlockState stateOffset = level.getBlockState(posOffset);

            if (!entity.getData(DataAttachmentRegistry.PREVENT_WARP) || entity instanceof Player) {
                if (stateOffset.getBlock() instanceof WarpPipeBlock && !stateOffset.getValue(WarpPipeBlock.CLOSED))
                    this.enterWarp(entity, level, posOffset, posOffset, stateOffset, null);
                if (state.getBlock() instanceof WarpPipeBlock && !state.getValue(WarpPipeBlock.CLOSED))
                    this.enterWarp(entity, level, pos, pos, state, null);
            }
        }

        if (stateAboveEntity.getBlock() instanceof WarpPipeBlock && !stateAboveEntity.getValue(WarpPipeBlock.CLOSED)
                && !entity.getData(DataAttachmentRegistry.PREVENT_WARP))
            this.enterWarp(entity, level, pos, pos, state, null);

        if (ModList.get().isLoaded("sable")) {
            SableProvider.SableContext context = SableProvider.getContext(level, entity);
            BlockPos posEmbedded;
            BlockPos posWorld;

            if (context != null) {
                posEmbedded = context.posEmbedded;
                posWorld = context.toWorld(posEmbedded);
            } else {
                posEmbedded = pos;
                posWorld = posEmbedded;
            }

            for (Direction facing : Direction.values()) {
                BlockState stateOffset;
                BlockPos worldOffset;
                BlockPos embeddedOffset;

                if (context != null) {
                    embeddedOffset = context.posEmbedded.relative(facing);
                    worldOffset = context.toWorld(embeddedOffset);
                    stateOffset = context.accessor.getBlockState(embeddedOffset);
                    if (level instanceof ServerLevel) {
                        embeddedOffset = context.posWorld.relative(facing);
                        stateOffset = context.accessor.getServerBlockState(embeddedOffset);
                    }
                } else {
                    embeddedOffset = entity.blockPosition().relative(facing);
                    worldOffset = entity.blockPosition().relative(facing);
                    stateOffset = level.getBlockState(worldOffset);
                }

                if (!entity.getData(DataAttachmentRegistry.PREVENT_WARP) || entity instanceof Player) {
                    if (stateOffset.getBlock() instanceof WarpPipeBlock && !stateOffset.getValue(WarpPipeBlock.CLOSED))
                        this.enterWarp(entity, level, worldOffset, embeddedOffset, stateOffset, context);
                    if (state.getBlock() instanceof WarpPipeBlock && !state.getValue(WarpPipeBlock.CLOSED))
                        this.enterWarp(entity, level, posWorld, posWorld, state, context);
                }
            }
        }

        Object object = null;
        if (ModList.get().isLoaded("sable"))
            object = SableProvider.getContext(level, entity);

        for (BlockPos posAbove : BlockPos.betweenClosed(min, max)) {
            BlockState stateAbove = level.getBlockState(posAbove);
            BlockPos entityPos = entity.blockPosition();
            BlockPos posEmbedded = entity.blockPosition();

            if (object instanceof SableProvider.SableContext context) {
                BlockPos delta = posAbove.subtract(entityPos);

                posEmbedded = context.posEmbedded.offset(delta);
                stateAbove = context.accessor.getBlockState(posEmbedded);

                if (level instanceof ServerLevel) {
                    posEmbedded = context.posWorld.offset(delta);
                    stateAbove = context.accessor.getServerBlockState(posEmbedded);
                }
            }

            if (stateAbove.getBlock() instanceof WarpPipeBlock && !stateAbove.getValue(WarpPipeBlock.CLOSED)
                    && !entity.getData(DataAttachmentRegistry.PREVENT_WARP))
                this.enterWarp(entity, level, posAbove, posEmbedded, stateAbove, object);
        }
    }

    @ModifyReturnValue(method = "isInWaterOrBubble", at = @At("RETURN"))
    private boolean isInWaterOrBubble(boolean original) {
        BlockState state = this.level().getBlockState(this.blockPosition());
        if (!original) {
            if (state.is(ModRegistry.PIPE_BUBBLES.get()))
                return true;
        }
        return original;
    }

    @ModifyReturnValue(method = "isInWaterRainOrBubble", at = @At("RETURN"))
    private boolean isInWaterRainOrBubble(boolean original) {
        BlockState state = this.level().getBlockState(this.blockPosition());
        if (!original) {
            if (state.is(ModRegistry.PIPE_BUBBLES.get()))
                return true;
        }
        return original;
    }

    @Inject(method = "handleEntityEvent", at = @At("HEAD"))
    private void handleEntityEvent(byte id, CallbackInfo ci) {
        Entity entity = (Entity) (Object) this;
        RandomSource random = entity.getRandom();

        if (id == 120) {
            for(int i = 0; i < 100; ++i) {
                this.level().addParticle(ParticleTypes.ENCHANT,
                        entity.getRandomX(0.5D), entity.getRandomY(), entity.getRandomZ(0.5D),
                        (random.nextDouble() - 0.5D) * 2.0D, -random.nextDouble(),
                        (random.nextDouble() - 0.5D) * 2.0D);
            }
        }
    }
}