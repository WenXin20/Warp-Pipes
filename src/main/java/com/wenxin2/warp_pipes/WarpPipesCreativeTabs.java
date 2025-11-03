package com.wenxin2.warp_pipes;

import com.wenxin2.warp_pipes.registries.ConfigRegistry;
import com.wenxin2.warp_pipes.registries.ModRegistry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@EventBusSubscriber(modid = WarpPipes.MOD_ID)
public class WarpPipesCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, WarpPipes.MOD_ID);
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> WARP_PIPES_TAB = TABS.register("warp_pipes_tab", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.warp_pipes"))
            .icon(() -> new ItemStack(ModRegistry.WARP_PIPES.get(DyeColor.GREEN).get())).build());

    @SubscribeEvent
    public static void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == WARP_PIPES_TAB.getKey() && !ConfigRegistry.DISABLE_WARP_PIPES_TABS.get()) {
            add(event, ModRegistry.WRENCH.get());
            add(event, ModRegistry.WARP_DISRUPTOR);

            add(event, ModRegistry.CLEAR_WARP_PIPE.get());
            addDyedBlocks(event, ModRegistry.CLEAR_WARP_PIPE, ModRegistry.WARP_PIPES, true, true);
        }

        if (!ConfigRegistry.DISABLE_VANILLA_TABS.get()) {
            if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
                addAfter(event, Items.FISHING_ROD, ModRegistry.WRENCH.get());
                addBefore(event, ModRegistry.WRENCH, ModRegistry.WARP_DISRUPTOR);
            }

            if (event.getTabKey() == CreativeModeTabs.COMBAT) {
                addBefore(event, Items.SHIELD, ModRegistry.WRENCH.get());
            }

            if (event.getTabKey() == CreativeModeTabs.REDSTONE_BLOCKS) {
                addAfter(event, Items.REDSTONE_LAMP, ModRegistry.CLEAR_WARP_PIPE.get());
                addAfter(event, Items.REDSTONE_LAMP, ModRegistry.WARP_PIPES.get(DyeColor.GREEN).get());
            }

            if (event.getTabKey() == CreativeModeTabs.COLORED_BLOCKS) {
                addAfter(event, Blocks.PINK_SHULKER_BOX, ModRegistry.CLEAR_WARP_PIPE);
                addDyedBlocks(event, ModRegistry.CLEAR_WARP_PIPE, ModRegistry.WARP_PIPES, true, true);
            }

            if (event.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
                addAfter(event, Items.RESPAWN_ANCHOR, ModRegistry.CLEAR_WARP_PIPE);
                addDyedBlocks(event, ModRegistry.CLEAR_WARP_PIPE, ModRegistry.WARP_PIPES, true, true);
            }
        }
    }

    public static void add(BuildCreativeModeTabContentsEvent event, ItemLike item) {
        ItemStack stack = new ItemStack(item);
        add(event, stack);
    }

    public static void add(BuildCreativeModeTabContentsEvent event, ItemStack stack) {
        if (stack.isEmpty()) {
            System.out.println("Warning, attempting to register an empty stack to tab!");
            return;
        }
        event.accept(stack);
    }

    public static void addAfter(BuildCreativeModeTabContentsEvent event, ItemLike afterItem, ItemLike item) {
        event.insertAfter(new ItemStack(afterItem), new ItemStack(item), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
    }

    public static void addBefore(BuildCreativeModeTabContentsEvent event, ItemLike beforeItem, ItemLike item) {
        event.insertBefore(new ItemStack(beforeItem), new ItemStack(item), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
    }

    private static void addDyedBlocks(BuildCreativeModeTabContentsEvent event, ItemLike existingItem,
                                      EnumMap<DyeColor, DeferredBlock<Block>> dyedBlock, boolean isReversed, boolean addAfter) {
        List<DyeColor> rainbowOrder = Arrays.asList(DyeColor.WHITE, DyeColor.LIGHT_GRAY, DyeColor.GRAY, DyeColor.BLACK,
                DyeColor.BROWN, DyeColor.RED, DyeColor.ORANGE, DyeColor.YELLOW, DyeColor.LIME, DyeColor.GREEN, DyeColor.CYAN,
                DyeColor.LIGHT_BLUE, DyeColor.BLUE, DyeColor.PURPLE, DyeColor.MAGENTA, DyeColor.PINK);
        List<DeferredHolder<Block, Block>> dyedBlocks = new ArrayList<>();
        Set<DyeColor> processedColors = new HashSet<>();

        if (isReversed)
            Collections.reverse(rainbowOrder);

        for (DyeColor color : rainbowOrder) {
            DeferredBlock<Block> coloredBlock = dyedBlock.get(color);
            if (coloredBlock != null) {
                dyedBlocks.add(coloredBlock);
                processedColors.add(color);
            }
        }

        // Track blocks not in the rainbow order
        Set<Block> additionalBlocks = new HashSet<>();
        for (Map.Entry<DyeColor, DeferredBlock<Block>> entry : dyedBlock.entrySet()) {
            DyeColor color = entry.getKey();
            if (!processedColors.contains(color))
                additionalBlocks.add(entry.getValue().get());
        }

        Set<Block> listedBlocks = new HashSet<>();

        // Adds all dyed blocks
        Block lastRainbowBlock = null;
        for (DeferredHolder<Block, Block> block : dyedBlocks) {
            Block coloredBlock = block.get();
            if (!listedBlocks.contains(coloredBlock)) {
                if (addAfter)
                    addAfter(event, existingItem, coloredBlock);
                else addBefore(event, existingItem, coloredBlock);

                listedBlocks.add(coloredBlock);
                lastRainbowBlock = dyedBlock.get(DyeColor.PINK).get();
            }
        }

        // Adds any additional blocks that were not in the dyed blocks
        for (Block additionalBlock : additionalBlocks) {
            if (!listedBlocks.contains(additionalBlock)) {
                if (lastRainbowBlock != null && addAfter)
                    addAfter(event, lastRainbowBlock, additionalBlock);
                else addBefore(event, existingItem, additionalBlock);
                listedBlocks.add(additionalBlock);
            }
        }
    }
}
