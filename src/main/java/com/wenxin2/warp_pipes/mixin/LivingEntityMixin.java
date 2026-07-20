package com.wenxin2.warp_pipes.mixin;

import com.wenxin2.warp_pipes.registries.ConfigRegistry;
import com.wenxin2.warp_pipes.utils.WP$BlockWarpEntityHandler;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements WP$BlockWarpEntityHandler {
    public LivingEntityMixin(EntityType<?> entityType, Level world) {
        super(entityType, world);
    }

    @Override
    public boolean wp$getBlockWarpTeleportConfig(Entity entity) {
        return ConfigRegistry.TELEPORT_MOBS.get();
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    public void readAdditionalSaveData(CompoundTag tag, CallbackInfo ci) {
        LivingEntity entity = (LivingEntity) (Object) this;

        if (tag.contains("warp_pipes:prevent_warp"))
            entity.getPersistentData().putBoolean("warp_pipes:prevent_warp",
                    tag.getBoolean("warp_pipes:prevent_warp"));

        if (tag.contains("warp_pipes:warp_cooldown"))
            entity.getPersistentData().putInt("warp_pipes:warp_cooldown",
                    tag.getInt("warp_pipes:warp_cooldown"));
    }
}
