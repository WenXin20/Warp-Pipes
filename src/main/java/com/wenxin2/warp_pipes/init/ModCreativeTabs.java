package com.wenxin2.warp_pipes.init;

import com.wenxin2.warp_pipes.WarpPipes;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, WarpPipes.MODID);
    public static final RegistryObject<CreativeModeTab> WARP_PIPES_TAB = TABS.register("warp_pipes_tab", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.warp_pipes"))
            .icon(() -> new ItemStack(ModRegistry.GREEN_WARP_PIPE.get()))
            .displayItems((parameters, output) -> {
                output.accept(ModRegistry.PIPE_WRENCH.get());

                output.accept(ModRegistry.WHITE_WARP_PIPE.get());
                output.accept(ModRegistry.ORANGE_WARP_PIPE.get());
                output.accept(ModRegistry.MAGENTA_WARP_PIPE.get());
                output.accept(ModRegistry.LIGHT_BLUE_WARP_PIPE.get());
                output.accept(ModRegistry.LIGHT_GRAY_WARP_PIPE.get());
                output.accept(ModRegistry.YELLOW_WARP_PIPE.get());
                output.accept(ModRegistry.LIME_WARP_PIPE.get());
                output.accept(ModRegistry.PINK_WARP_PIPE.get());
                output.accept(ModRegistry.GRAY_WARP_PIPE.get());
                output.accept(ModRegistry.CYAN_WARP_PIPE.get());
                output.accept(ModRegistry.PURPLE_WARP_PIPE.get());
                output.accept(ModRegistry.BLACK_WARP_PIPE.get());
                output.accept(ModRegistry.BLUE_WARP_PIPE.get());
                output.accept(ModRegistry.BROWN_WARP_PIPE.get());
                output.accept(ModRegistry.GREEN_WARP_PIPE.get());
                output.accept(ModRegistry.RED_WARP_PIPE.get());
                output.accept(ModRegistry.CLEAR_WARP_PIPE.get());

            }).build());

}
