package com.wenxin2.warp_pipes.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.wenxin2.warp_pipes.integration.sable_compat.SableProvider;
import com.wenxin2.warp_pipes.registries.ConfigRegistry;
import com.wenxin2.warp_pipes.utils.SubLevelCollision;
import com.wenxin2.warp_pipes.utils.WP$BlockWarpPlayerHandler;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Player.class)
public abstract class PlayerMixin extends Entity implements WP$BlockWarpPlayerHandler {
    public PlayerMixin(EntityType<?> entityType, Level world) {
        super(entityType, world);
    }

    @Override
    public boolean wp$getBlockWarpTeleportConfig(Entity entity) {
        return ConfigRegistry.TELEPORT_PLAYERS.get();
    }

    @WrapMethod(method = "updatePlayerPose")
    private void wp$trackPoseCollidingEntity(Operation<Void> original) {
        if (!SableProvider.isLoaded()) {
            original.call();
            return;
        }

        Entity previous = SubLevelCollision.pushCollidingEntity(this);
        try {
            original.call();
        } finally {
            SubLevelCollision.popCollidingEntity(previous);
        }
    }
}
