package com.wenxin2.warp_pipes.init;

import com.wenxin2.warp_pipes.WarpPipes;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
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
            .icon(() -> new ItemStack(ModRegistry.GREEN_WARP_PIPE.get())).build());

    @SubscribeEvent
    public static void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == WARP_PIPES_TAB.getKey()) {
            add(event, ModRegistry.PIPE_WRENCH.get());

            add(event, ModRegistry.CLEAR_WARP_PIPE.get());
            add(event, ModRegistry.WHITE_WARP_PIPE.get());
            add(event, ModRegistry.LIGHT_GRAY_WARP_PIPE.get());
            add(event, ModRegistry.GRAY_WARP_PIPE.get());
            add(event, ModRegistry.BLACK_WARP_PIPE.get());
            add(event, ModRegistry.BROWN_WARP_PIPE.get());
            add(event, ModRegistry.RED_WARP_PIPE.get());
            add(event, ModRegistry.ORANGE_WARP_PIPE.get());
            add(event, ModRegistry.YELLOW_WARP_PIPE.get());
            add(event, ModRegistry.LIME_WARP_PIPE.get());
            add(event, ModRegistry.GREEN_WARP_PIPE.get());
            add(event, ModRegistry.CYAN_WARP_PIPE.get());
            add(event, ModRegistry.LIGHT_BLUE_WARP_PIPE.get());
            add(event, ModRegistry.BLUE_WARP_PIPE.get());
            add(event, ModRegistry.PURPLE_WARP_PIPE.get());
            add(event, ModRegistry.MAGENTA_WARP_PIPE.get());
            add(event, ModRegistry.PINK_WARP_PIPE.get());
        }
        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            addAfter(event, Items.FISHING_ROD, ModRegistry.PIPE_WRENCH.get());
        }

        if (event.getTabKey() == CreativeModeTabs.COMBAT) {
            addBefore(event, Items.SHIELD, ModRegistry.PIPE_WRENCH.get());
        }

        if (event.getTabKey() == CreativeModeTabs.REDSTONE_BLOCKS) {
            addAfter(event, Items.REDSTONE_LAMP, ModRegistry.CLEAR_WARP_PIPE.get());
            addAfter(event, Items.REDSTONE_LAMP, ModRegistry.GREEN_WARP_PIPE.get());
        }

        if (event.getTabKey() == CreativeModeTabs.COLORED_BLOCKS) {
            add(event, ModRegistry.CLEAR_WARP_PIPE.get());
            add(event, ModRegistry.WHITE_WARP_PIPE.get());
            add(event, ModRegistry.LIGHT_GRAY_WARP_PIPE.get());
            add(event, ModRegistry.GRAY_WARP_PIPE.get());
            add(event, ModRegistry.BLACK_WARP_PIPE.get());
            add(event, ModRegistry.BROWN_WARP_PIPE.get());
            add(event, ModRegistry.RED_WARP_PIPE.get());
            add(event, ModRegistry.ORANGE_WARP_PIPE.get());
            add(event, ModRegistry.YELLOW_WARP_PIPE.get());
            add(event, ModRegistry.LIME_WARP_PIPE.get());
            add(event, ModRegistry.GREEN_WARP_PIPE.get());
            add(event, ModRegistry.CYAN_WARP_PIPE.get());
            add(event, ModRegistry.LIGHT_BLUE_WARP_PIPE.get());
            add(event, ModRegistry.BLUE_WARP_PIPE.get());
            add(event, ModRegistry.PURPLE_WARP_PIPE.get());
            add(event, ModRegistry.MAGENTA_WARP_PIPE.get());
            add(event, ModRegistry.PINK_WARP_PIPE.get());
        }

        if (event.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
            addAfter(event, Items.RESPAWN_ANCHOR, ModRegistry.CLEAR_WARP_PIPE.get());
            addAfter(event, ModRegistry.CLEAR_WARP_PIPE.get(), ModRegistry.WHITE_WARP_PIPE.get());
            addAfter(event, ModRegistry.WHITE_WARP_PIPE.get(), ModRegistry.LIGHT_GRAY_WARP_PIPE.get());
            addAfter(event, ModRegistry.LIGHT_GRAY_WARP_PIPE.get(), ModRegistry.GRAY_WARP_PIPE.get());
            addAfter(event, ModRegistry.GRAY_WARP_PIPE.get(), ModRegistry.BLACK_WARP_PIPE.get());
            addAfter(event, ModRegistry.BLACK_WARP_PIPE.get(), ModRegistry.BROWN_WARP_PIPE.get());
            addAfter(event, ModRegistry.BROWN_WARP_PIPE.get(), ModRegistry.RED_WARP_PIPE.get());
            addAfter(event, ModRegistry.RED_WARP_PIPE.get(), ModRegistry.ORANGE_WARP_PIPE.get());
            addAfter(event, ModRegistry.ORANGE_WARP_PIPE.get(), ModRegistry.YELLOW_WARP_PIPE.get());
            addAfter(event, ModRegistry.YELLOW_WARP_PIPE.get(), ModRegistry.LIME_WARP_PIPE.get());
            addAfter(event, ModRegistry.LIME_WARP_PIPE.get(), ModRegistry.GREEN_WARP_PIPE.get());
            addAfter(event, ModRegistry.GREEN_WARP_PIPE.get(), ModRegistry.CYAN_WARP_PIPE.get());
            addAfter(event, ModRegistry.CYAN_WARP_PIPE.get(), ModRegistry.LIGHT_BLUE_WARP_PIPE.get());
            addAfter(event, ModRegistry.LIGHT_BLUE_WARP_PIPE.get(), ModRegistry.BLUE_WARP_PIPE.get());
            addAfter(event, ModRegistry.BLUE_WARP_PIPE.get(), ModRegistry.PURPLE_WARP_PIPE.get());
            addAfter(event, ModRegistry.PURPLE_WARP_PIPE.get(), ModRegistry.MAGENTA_WARP_PIPE.get());
            addAfter(event, ModRegistry.MAGENTA_WARP_PIPE.get(), ModRegistry.PINK_WARP_PIPE.get());
        }
    }

    public static void add(BuildCreativeModeTabContentsEvent event, ItemLike item)
    {
        ItemStack stack = new ItemStack(item);
        add(event, stack);
    }

    public static void add(BuildCreativeModeTabContentsEvent event, ItemStack stack)
    {
        if (stack.isEmpty())
        {
            System.out.println("Warning, attempting to register an empty stack to tab!");
            return;
        }
        event.accept(stack);
    }

    public static void addAfter(BuildCreativeModeTabContentsEvent event, ItemLike afterItem, ItemLike item) {
        event.getEntries().putAfter(new ItemStack(afterItem), new ItemStack(item), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
    }

    public static void addBefore(BuildCreativeModeTabContentsEvent event, ItemLike beforeItem, ItemLike item) {
        event.getEntries().putBefore(new ItemStack(beforeItem), new ItemStack(item), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
    }
}
