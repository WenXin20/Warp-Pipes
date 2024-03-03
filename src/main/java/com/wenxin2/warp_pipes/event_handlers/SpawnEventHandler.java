package com.wenxin2.warp_pipes.event_handlers;

import com.wenxin2.warp_pipes.WarpPipes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, modid = WarpPipes.MODID)
public class SpawnEventHandler {
    public static void onJoinWorld(final EntityJoinLevelEvent event)
    {
        if (!(event.getEntity() instanceof LivingEntity) && !(event.getEntity() instanceof Player)) return;

        if (event.getEntity() != null && !event.getEntity().getPersistentData().contains("warp_pipes:can_warp"))
            event.getEntity().getPersistentData().putBoolean("warp_pipes:can_warp", true);
    }

    public static void register()
    {
        WarpPipes.FORGE_BUS.addListener(SpawnEventHandler::onJoinWorld);
    }
}
