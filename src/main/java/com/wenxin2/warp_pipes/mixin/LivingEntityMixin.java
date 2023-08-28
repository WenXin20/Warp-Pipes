package com.wenxin2.warp_pipes.mixin;

import com.wenxin2.warp_pipes.init.ModRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    @Shadow public abstract boolean isAlive();

    public LivingEntityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }
    
    @Inject(at = @At("TAIL"), method = "baseTick")
    public void baseTick(CallbackInfo ci) {
        LivingEntity livingEntity = (LivingEntity) (Object) this;

        Level world = this.level();

        if (this.isAlive()) {
            boolean flag = livingEntity instanceof Player;

            if (!this.getEyeInFluidType().isAir()
                    && !world.getBlockState(new BlockPos((int) this.getX(), (int) this.getEyeY(), (int) this.getZ())).is(Blocks.BUBBLE_COLUMN)
                    && !world.getBlockState(new BlockPos((int) this.getX(), (int) this.getEyeY(), (int) this.getZ())).is(ModRegistry.PIPE_BUBBLES.get())) {

                boolean flag1 = livingEntity.canDrownInFluidType(this.getEyeInFluidType())
                        && !MobEffectUtil.hasWaterBreathing(livingEntity)
                        && (!flag || !((Player) livingEntity).getAbilities().invulnerable);

                if (flag1) {
                    this.setAirSupply(livingEntity.decreaseAirSupply(this.getAirSupply()));

                    if (this.getAirSupply() == -20) {
                        this.setAirSupply(0);
                        Vec3 vec3 = this.getDeltaMovement();

                        for (int i = 0; i < 8; ++i) {
                            double d2 = this.random.nextDouble() - this.random.nextDouble();
                            double d3 = this.random.nextDouble() - this.random.nextDouble();
                            double d4 = this.random.nextDouble() - this.random.nextDouble();
                            world.addParticle(ParticleTypes.BUBBLE, this.getX() + d2, this.getY() + d3, this.getZ() + d4, vec3.x, vec3.y, vec3.z);
                        }

                        this.hurt(this.damageSources().drown(), 2.0F);
                    }
                }
            }

            // Forge: Move to if statement since we need to increase the air supply if in bubble column or can't drown
            if (this.getAirSupply() < this.getMaxAirSupply()
                    && (!livingEntity.canDrownInFluidType(this.getEyeInFluidType())
                    || world.getBlockState(new BlockPos((int) this.getX(), (int) this.getEyeY(), (int) this.getZ())).is(Blocks.BUBBLE_COLUMN)
                    || world.getBlockState(new BlockPos((int) this.getX(), (int) this.getEyeY(), (int) this.getZ())).is(ModRegistry.PIPE_BUBBLES.get()))) {
                this.setAirSupply(livingEntity.increaseAirSupply(this.getAirSupply()));
            }
        }
    }
}
