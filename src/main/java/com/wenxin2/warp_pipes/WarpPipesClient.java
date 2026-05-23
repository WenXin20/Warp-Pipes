package com.wenxin2.warp_pipes;

import com.wenxin2.warp_pipes.blocks.client.WarpPipeScreen;
import com.wenxin2.warp_pipes.client.renderers.blocks.WarpPipeBlockEntityRenderer;
import com.wenxin2.warp_pipes.registries.ModRegistry;
import java.util.Optional;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackSelectionConfig;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.KnownPack;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.event.AddPackFindersEvent;

@EventBusSubscriber(modid = WarpPipes.MOD_ID, value = Dist.CLIENT)
public class WarpPipesClient {
    @SubscribeEvent
    public static void registerBlockColors(final RegisterColorHandlersEvent.Block event) {
        event.register((state, world, pos, tintIndex) -> {
            return world != null && pos != null
                    ? BiomeColors.getAverageWaterColor(world, pos) | 0xFF0000cc : 0xFFFFFFFF;
        }, ModRegistry.WATER_SPOUT.get());
    }

    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(ModRegistry.WARP_PIPE_MENU.get(), WarpPipeScreen::new);
    }

    public static void registerBlockEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModRegistry.WARP_PIPE_BLOCK_ENTITY.get(), WarpPipeBlockEntityRenderer::new);
    }

    public static void addPackFinder(final AddPackFindersEvent event) {
        if (event.getPackType() == PackType.CLIENT_RESOURCES) {
            ResourceLocation packLocation = ResourceLocation.fromNamespaceAndPath(WarpPipes.MOD_ID, "resourcepacks/warp_pipes/classic_pipes");
            Component packNameDisplay = Component.translatable("resource_pack.warp_pipes.classic_pipes");

            event.addPackFinders(packLocation, PackType.CLIENT_RESOURCES, packNameDisplay,
                    PackSource.BUILT_IN, false, Pack.Position.TOP);

            if (ModList.get().isLoaded("fusion")) {
                packLocation = ResourceLocation.fromNamespaceAndPath(WarpPipes.MOD_ID, "resourcepacks/warp_pipes/classic_pipes_fusion");
                packNameDisplay = Component.translatable("resource_pack.warp_pipes.classic_pipes_fusion");
                event.addPackFinders(packLocation, PackType.CLIENT_RESOURCES, packNameDisplay,
                        PackSource.BUILT_IN, false, Pack.Position.TOP);
            }
        }
    }
}
