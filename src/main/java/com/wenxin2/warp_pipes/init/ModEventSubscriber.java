package com.wenxin2.warp_pipes.init;

import com.wenxin2.warp_pipes.WarpPipes;
import com.wenxin2.warp_pipes.network.PacketHandler;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod.EventBusSubscriber(modid = WarpPipes.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)

public class ModEventSubscriber {

    @SubscribeEvent
    public static void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(PacketHandler::register);
    }
}
