package com.wenxin2.warp_pipes.utils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;

public class ServerParticleUtils {

    public static void spawnThreeLayerBlockParticles(ParticleOptions particleOptions, ServerLevel serverWorld, BlockPos pos, int avgAmount) {
        float scaleFactor = 1;
        int numParticles = (int) (scaleFactor * avgAmount);
        double radius = 0.65;

        for (int i = 0; i < numParticles; i++) {
            double angle = 2 * Math.PI * i / numParticles;
            double offsetX = Math.cos(angle) * radius;
            double offsetZ = Math.sin(angle) * radius;

            double x = pos.getX() + 0.5 + offsetX;
            double y = pos.getY();
            double z = pos.getZ() + 0.5 + offsetZ;

            serverWorld.sendParticles(particleOptions, x, y, z, 1, 0, 0, 0, 0.0);
            serverWorld.sendParticles(particleOptions, x, y + 0.5, z, 1, 0, 0, 0, 0.0);
            serverWorld.sendParticles(particleOptions, x, y + 1.0, z, 1, 0, 0, 0, 0.0);
        }
    }
}
