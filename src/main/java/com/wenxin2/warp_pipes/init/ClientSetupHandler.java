package com.wenxin2.warp_pipes.init;

import com.wenxin2.warp_pipes.WarpPipes;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, modid = WarpPipes.MODID, value = Dist.CLIENT)
public class ClientSetupHandler {
    @SubscribeEvent
    public static void registerBlockColors(final RegisterColorHandlersEvent.Block event)
    {
        event.register((state, world, pos, tintIndex) -> {
            return world != null && pos != null ? BiomeColors.getAverageWaterColor(world, pos) | 0xFF0000cc
                    : 0xFFFFFFFF;
        }, ModRegistry.WATER_SPOUT.get());
    }
}
