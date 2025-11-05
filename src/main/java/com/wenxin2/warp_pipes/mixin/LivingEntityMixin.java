package com.wenxin2.warp_pipes.mixin;

import com.wenxin2.warp_pipes.registries.ConfigRegistry;
import com.wenxin2.warp_pipes.registries.TagRegistry;
import com.wenxin2.warp_pipes.utils.BlockWarpEntitiesHandler;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements BlockWarpEntitiesHandler {
    @Unique private boolean wp$preventWarp;
    @Unique private int wp$preventWarpCooldown;
    @Unique private int wp$warpCooldown;

    public LivingEntityMixin(EntityType<?> entityType, Level world) {
        super(entityType, world);
    }

    @Override
    public boolean wp$getBlockWarpTeleportConfig() {
        return ConfigRegistry.TELEPORT_MOBS.get();
    }
    
    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    public void addAdditionalSaveData(CompoundTag tag, CallbackInfo ci) {
        LivingEntity entity = (LivingEntity) (Object) this;
        
        if (!entity.getType().is(TagRegistry.CANNOT_WARP)) {
            if (entity instanceof Player && ConfigRegistry.TELEPORT_PLAYERS.get()) {
                tag.putBoolean("warp_pipes:prevent_warp", this.wp$doPreventWarp());
                tag.putInt("warp_pipes:warp_cooldown", this.wp$getWarpCooldown());
            } else if (ConfigRegistry.TELEPORT_MOBS.get()) {
                tag.putBoolean("warp_pipes:prevent_warp", this.wp$doPreventWarp());
                tag.putInt("warp_pipes:warp_cooldown", this.wp$getWarpCooldown());
            }

            if (entity instanceof Player)
                tag.putInt("warp_pipes:prevent_warp_cooldown", this.wp$getPreventWarpCooldown());
        }
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    public void readAdditionalSaveData(CompoundTag tag, CallbackInfo ci) {
        LivingEntity entity = (LivingEntity) (Object) this;

        if (!entity.getType().is(TagRegistry.CANNOT_WARP)) {
            if (entity instanceof Player && ConfigRegistry.TELEPORT_PLAYERS.get()) {
                this.wp$setPreventWarp(tag.getBoolean("warp_pipes:prevent_warp"));
                this.wp$setWarpCooldown(tag.getInt("warp_pipes:warp_cooldown"));
            } else if (ConfigRegistry.TELEPORT_MOBS.get()) {
                this.wp$setPreventWarp(tag.getBoolean("warp_pipes:prevent_warp"));
                this.wp$setWarpCooldown(tag.getInt("warp_pipes:warp_cooldown"));
            }

            if (entity instanceof Player)
                this.wp$setPreventWarpCooldown(tag.getInt("warp_pipes:prevent_warp_cooldown"));
        }
    }

    @Override
    public boolean wp$doPreventWarp() {
        return this.wp$preventWarp;
    }

    @Override
    public void wp$setPreventWarp(boolean preventWarp) {
        this.wp$preventWarp = preventWarp;
    }

    @Override
    public int wp$getPreventWarpCooldown() {
        return this.wp$preventWarpCooldown;
    }

    @Override
    public void wp$setPreventWarpCooldown(int preventWarpCooldown) {
        this.wp$preventWarpCooldown = preventWarpCooldown;
    }

    @Override
    public int wp$getWarpCooldown() {
        return this.wp$warpCooldown;
    }

    @Override
    public void wp$setWarpCooldown(int warpCooldown) {
        this.wp$warpCooldown = warpCooldown;
    }
}
