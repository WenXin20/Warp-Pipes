package com.wenxin2.warp_pipes.mixin;

import com.wenxin2.warp_pipes.registries.ConfigRegistry;
import com.wenxin2.warp_pipes.utils.WP$BlockWarpPlayerHandler;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ServerPlayer.class)
public class ServerPlayerMixin implements WP$BlockWarpPlayerHandler {
    @Override
    public boolean wp$getBlockWarpTeleportConfig() {
        return ConfigRegistry.TELEPORT_PLAYERS.get();
    }
}