package com.wenxin2.warp_pipes.mixin;

import com.wenxin2.warp_pipes.registries.ConfigRegistry;
import com.wenxin2.warp_pipes.utils.WP$BlockWarpPlayersHandler;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ServerPlayer.class)
public class ServerPlayerMixin implements WP$BlockWarpPlayersHandler {
    @Unique private boolean wp$preventWarp;
    @Unique private int wp$preventWarpCooldown;
    @Unique private int wp$warpCooldown;

    @Override
    public boolean wp$getBlockWarpTeleportConfig() {
        return ConfigRegistry.TELEPORT_PLAYERS.get();
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