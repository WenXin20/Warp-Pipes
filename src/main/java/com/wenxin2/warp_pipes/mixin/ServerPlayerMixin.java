package com.wenxin2.warp_pipes.mixin;

import com.wenxin2.warp_pipes.registries.ConfigRegistry;
import com.wenxin2.warp_pipes.utils.WP$BlockWarpPlayerHandler;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ServerPlayer.class)
public class ServerPlayerMixin implements WP$BlockWarpPlayerHandler {
    @Override
    public boolean wp$getBlockWarpTeleportConfig(Entity entity) {
        return ConfigRegistry.TELEPORT_PLAYERS.get();
    }
}