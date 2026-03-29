package com.wenxin2.warp_pipes.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.wenxin2.warp_pipes.blocks.WarpPipeBlock;
import com.wenxin2.warp_pipes.registries.ConfigRegistry;
import com.wenxin2.warp_pipes.registries.DataAttachmentRegistry;
import com.wenxin2.warp_pipes.registries.ModRegistry;
import com.wenxin2.warp_pipes.registries.TagRegistry;
import com.wenxin2.warp_pipes.utils.WP$BlockWarpEntitiesHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin implements WP$BlockWarpEntitiesHandler {
    @Shadow public abstract Level level();
    @Shadow public abstract double getX();
    @Shadow public abstract double getY();
    @Shadow public abstract double getZ();
    @Shadow public abstract int getId();
    @Shadow public abstract BlockPos blockPosition();
    @Shadow public abstract EntityType<?> getType();
    @Shadow public abstract void setPos(Vec3 vec3);

    @Override
    public boolean wp$getBlockWarpTeleportConfig() {
        return ConfigRegistry.TELEPORT_NON_MOBS.get();
    }

    @Inject(at = @At("TAIL"), method = "tick")
    public void tick(CallbackInfo ci) {
        Entity entity = (Entity) (Object) this;
        Level world = entity.level();
        BlockPos pos = entity.blockPosition();
        BlockPos posAboveEntity = pos.above(Math.round(entity.getBbHeight()));
        BlockState state = world.getBlockState(pos);
        BlockState stateAboveEntity = world.getBlockState(posAboveEntity);

        for (Direction facing : Direction.values()) {
            BlockPos offsetPos = pos.relative(facing);
            BlockState offsetState = world.getBlockState(offsetPos);

            if (!entity.getData(DataAttachmentRegistry.PREVENT_WARP) || entity instanceof Player) {
                if (offsetState.getBlock() instanceof WarpPipeBlock && !offsetState.getValue(WarpPipeBlock.CLOSED))
                    this.enterWarp(entity, world, offsetPos);
                if (state.getBlock() instanceof WarpPipeBlock && !state.getValue(WarpPipeBlock.CLOSED))
                    this.enterWarp(entity, world, pos);
            }
        }

        if (stateAboveEntity.getBlock() instanceof WarpPipeBlock && !stateAboveEntity.getValue(WarpPipeBlock.CLOSED)
                && !entity.getData(DataAttachmentRegistry.PREVENT_WARP))
            this.enterWarp(entity, world, pos);
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