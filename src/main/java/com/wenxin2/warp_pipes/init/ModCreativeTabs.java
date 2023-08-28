package com.wenxin2.warp_pipes.init;

import com.wenxin2.warp_pipes.WarpPipes;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = WarpPipes.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, WarpPipes.MODID);
    public static final RegistryObject<CreativeModeTab> WARP_PIPES_TAB = TABS.register("warp_pipes_tab", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.warp_pipes"))
            .icon(() -> new ItemStack(ModRegistry.GREEN_WARP_PIPE.get()))
            .displayItems((parameters, output) -> {
                output.accept(ModRegistry.PIPE_WRENCH.get());

                output.accept(ModRegistry.CLEAR_WARP_PIPE.get());
                output.accept(ModRegistry.WHITE_WARP_PIPE.get());
                output.accept(ModRegistry.LIGHT_GRAY_WARP_PIPE.get());
                output.accept(ModRegistry.GRAY_WARP_PIPE.get());
                output.accept(ModRegistry.BLACK_WARP_PIPE.get());
                output.accept(ModRegistry.BROWN_WARP_PIPE.get());
                output.accept(ModRegistry.RED_WARP_PIPE.get());
                output.accept(ModRegistry.ORANGE_WARP_PIPE.get());
                output.accept(ModRegistry.YELLOW_WARP_PIPE.get());
                output.accept(ModRegistry.LIME_WARP_PIPE.get());
                output.accept(ModRegistry.GREEN_WARP_PIPE.get());
                output.accept(ModRegistry.CYAN_WARP_PIPE.get());
                output.accept(ModRegistry.LIGHT_BLUE_WARP_PIPE.get());
                output.accept(ModRegistry.BLUE_WARP_PIPE.get());
                output.accept(ModRegistry.PURPLE_WARP_PIPE.get());
                output.accept(ModRegistry.MAGENTA_WARP_PIPE.get());
                output.accept(ModRegistry.PINK_WARP_PIPE.get());

            }).build());


    @SubscribeEvent
    public static void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            event.accept(ModRegistry.PIPE_WRENCH.get());
        }

        if (event.getTabKey() == CreativeModeTabs.COMBAT) {
            event.accept(ModRegistry.PIPE_WRENCH.get());
        }

        if (event.getTabKey() == CreativeModeTabs.REDSTONE_BLOCKS) {
            event.accept(ModRegistry.CLEAR_WARP_PIPE.get());
            event.accept(ModRegistry.GREEN_WARP_PIPE.get());
        }

        if (event.getTabKey() == CreativeModeTabs.COLORED_BLOCKS) {
            event.accept(ModRegistry.CLEAR_WARP_PIPE.get());
            event.accept(ModRegistry.WHITE_WARP_PIPE.get());
            event.accept(ModRegistry.LIGHT_GRAY_WARP_PIPE.get());
            event.accept(ModRegistry.GRAY_WARP_PIPE.get());
            event.accept(ModRegistry.BLACK_WARP_PIPE.get());
            event.accept(ModRegistry.BROWN_WARP_PIPE.get());
            event.accept(ModRegistry.RED_WARP_PIPE.get());
            event.accept(ModRegistry.ORANGE_WARP_PIPE.get());
            event.accept(ModRegistry.YELLOW_WARP_PIPE.get());
            event.accept(ModRegistry.LIME_WARP_PIPE.get());
            event.accept(ModRegistry.GREEN_WARP_PIPE.get());
            event.accept(ModRegistry.CYAN_WARP_PIPE.get());
            event.accept(ModRegistry.LIGHT_BLUE_WARP_PIPE.get());
            event.accept(ModRegistry.BLUE_WARP_PIPE.get());
            event.accept(ModRegistry.PURPLE_WARP_PIPE.get());
            event.accept(ModRegistry.MAGENTA_WARP_PIPE.get());
            event.accept(ModRegistry.PINK_WARP_PIPE.get());
        }

        if (event.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
            event.accept(ModRegistry.CLEAR_WARP_PIPE.get());
            event.accept(ModRegistry.WHITE_WARP_PIPE.get());
            event.accept(ModRegistry.LIGHT_GRAY_WARP_PIPE.get());
            event.accept(ModRegistry.GRAY_WARP_PIPE.get());
            event.accept(ModRegistry.BLACK_WARP_PIPE.get());
            event.accept(ModRegistry.BROWN_WARP_PIPE.get());
            event.accept(ModRegistry.RED_WARP_PIPE.get());
            event.accept(ModRegistry.ORANGE_WARP_PIPE.get());
            event.accept(ModRegistry.YELLOW_WARP_PIPE.get());
            event.accept(ModRegistry.LIME_WARP_PIPE.get());
            event.accept(ModRegistry.GREEN_WARP_PIPE.get());
            event.accept(ModRegistry.CYAN_WARP_PIPE.get());
            event.accept(ModRegistry.LIGHT_BLUE_WARP_PIPE.get());
            event.accept(ModRegistry.BLUE_WARP_PIPE.get());
            event.accept(ModRegistry.PURPLE_WARP_PIPE.get());
            event.accept(ModRegistry.MAGENTA_WARP_PIPE.get());
            event.accept(ModRegistry.PINK_WARP_PIPE.get());
        }
    }
}
