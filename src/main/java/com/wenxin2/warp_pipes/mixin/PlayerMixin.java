package com.wenxin2.warp_pipes.mixin;

import com.wenxin2.warp_pipes.registries.ConfigRegistry;
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
}
