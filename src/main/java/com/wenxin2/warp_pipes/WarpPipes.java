package com.wenxin2.warp_pipes;

import com.mojang.logging.LogUtils;
import com.wenxin2.warp_pipes.event_handlers.WarpEventHandlers;
import com.wenxin2.warp_pipes.init.ClientSetupHandler;
import com.wenxin2.warp_pipes.init.Config;
import com.wenxin2.warp_pipes.init.ModCreativeTabs;
import com.wenxin2.warp_pipes.init.ModRegistry;
import com.wenxin2.warp_pipes.init.SoundRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(WarpPipes.MODID)
public class WarpPipes
{
    // Define mod id in a common place for everything to reference
    public static final String MODID = "warp_pipes";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();

    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(Registries.SOUND_EVENT, WarpPipes.MODID);

    // Bus for Forge Events
    public static final IEventBus FORGE_BUS = NeoForge.EVENT_BUS;

    public WarpPipes(IEventBus bus, Dist dist)
    {
        // Register the Deferred Register to the mod event bus so blocks/items get registered
        SOUNDS.register(bus);
        ModCreativeTabs.TABS.register(bus);

        ModRegistry.init(bus);
        SoundRegistry.init();
        Config.register();

        if (dist.isClient())
            bus.addListener(ClientSetupHandler::registerBlockEntityRenderers);

//        WarpEventHandlers.register();
        // PipeBubblesSoundHandler.init();

        // ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.CONFIG);

        // Register ourselves for server and other game events we are interested in
        NeoForge.EVENT_BUS.addListener(WarpEventHandlers::onJoinWorld);
        NeoForge.EVENT_BUS.addListener(WarpEventHandlers::onPlayerRightClick);
    }
}
