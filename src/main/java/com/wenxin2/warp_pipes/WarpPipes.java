package com.wenxin2.warp_pipes;

import com.mojang.logging.LogUtils;
import com.wenxin2.warp_pipes.event_handlers.RegistryEventHandlers;
import com.wenxin2.warp_pipes.event_handlers.WarpPipesEventHandlers;
import com.wenxin2.warp_pipes.registries.ConfigRegistry;
import com.wenxin2.warp_pipes.registries.DataAttachmentRegistry;
import com.wenxin2.warp_pipes.registries.DataComponentRegistry;
import com.wenxin2.warp_pipes.registries.ModRegistry;
import com.wenxin2.warp_pipes.registries.SoundRegistry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.slf4j.Logger;

@Mod(WarpPipes.MOD_ID)
public class WarpPipes
{
    public static final String MOD_ID = "warp_pipes";
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MOD_ID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MOD_ID);

    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, WarpPipes.MOD_ID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, WarpPipes.MOD_ID);
    public static final DeferredRegister<DataComponentType<?>> COMPONENTS = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, WarpPipes.MOD_ID);
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, WarpPipes.MOD_ID);
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(Registries.SOUND_EVENT, WarpPipes.MOD_ID);

    public WarpPipes(IEventBus bus, Dist dist, ModContainer container)
    {
        ATTACHMENT_TYPES.register(bus);
        BLOCKS.register(bus);
        ITEMS.register(bus);
        BLOCK_ENTITIES.register(bus);
        MENUS.register(bus);
        SOUNDS.register(bus);
        COMPONENTS.register(bus);
        WarpPipesCreativeTabs.TABS.register(bus);

        ModRegistry.init();
        DataAttachmentRegistry.init();
        DataComponentRegistry.init();
        SoundRegistry.init();
        ConfigRegistry.register(container);

        if (dist.isClient()) {
            bus.addListener(WarpPipesClient::registerBlockEntityRenderers);
            ConfigRegistry.registerClient(container);
        }

        NeoForge.EVENT_BUS.addListener(WarpPipesEventHandlers::onRightClickBlock);
        bus.addListener(RegistryEventHandlers::gatherData);
    }
}
