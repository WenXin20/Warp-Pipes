package com.wenxin2.warp_pipes.init;

import com.wenxin2.warp_pipes.WarpPipes;
import com.wenxin2.warp_pipes.blocks.ClearWarpPipeBlock;
import com.wenxin2.warp_pipes.blocks.PipeBubblesBlock;
import com.wenxin2.warp_pipes.blocks.WarpPipeBlock;
import com.wenxin2.warp_pipes.blocks.WaterSpoutBlock;
import com.wenxin2.warp_pipes.blocks.entities.WarpPipeBlockEntity;
import com.wenxin2.warp_pipes.inventory.WarpPipeMenu;
import com.wenxin2.warp_pipes.items.WrenchItem;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModRegistry {

    // Create a Deferred Register to hold blocks/items which will all be registered under the "warp_pipes" namespace
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(WarpPipes.MODID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(WarpPipes.MODID);

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, WarpPipes.MODID);
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, WarpPipes.MODID);


    public static final DeferredHolder<Block, ClearWarpPipeBlock> CLEAR_WARP_PIPE = BLOCKS.registerBlock("clear_warp_pipe",
            ClearWarpPipeBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.NONE)
                    .sound(SoundType.GLASS).isSuffocating(ModRegistry::never).isViewBlocking(ModRegistry::never)
                    .strength(3.0F, 500.0F).requiresCorrectToolForDrops().noOcclusion());

    public static final DeferredItem<BlockItem> CLEAR_WARP_PIPE_ITEM = registerItem("clear_warp_pipe",
            () -> new BlockItem(CLEAR_WARP_PIPE.get(), new Item.Properties()));

    public static final EnumMap<DyeColor, DeferredBlock<Block>> WARP_PIPES = new EnumMap<>(DyeColor.class);

    public static final EnumMap<DyeColor, DeferredItem<BlockItem>> WARP_PIPE_ITEMS = new EnumMap<>(DyeColor.class);

    public static final DeferredItem<Item> PIPE_WRENCH = registerItem("pipe_wrench",
            () -> new WrenchItem(new Item.Properties()
                    .attributes(WrenchItem.createAttributes(Tiers.IRON, 3, -3.2F))
                    .durability(128), Tiers.IRON));;

    public static final DeferredBlock<Block> PIPE_BUBBLES = registerNoItemBlock("pipe_bubbles",
            () -> new PipeBubblesBlock(BlockBehaviour.Properties.of().pushReaction(PushReaction.DESTROY)
                    .replaceable().noCollission().noLootTable().liquid()));

    public static final DeferredBlock<Block> WATER_SPOUT = registerNoItemBlock("water_spout",
            () -> new WaterSpoutBlock(BlockBehaviour.Properties.of().mapColor(MapColor.WATER).sound(SoundRegistry.WATER_SPOUT)
                    .pushReaction(PushReaction.DESTROY).isRedstoneConductor(ModRegistry::never)
                    .isSuffocating(ModRegistry::never).isViewBlocking(ModRegistry::never)
                    .replaceable().noCollission().noLootTable()));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<WarpPipeBlockEntity>> WARP_PIPE_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("warp_pipe",
            () -> BlockEntityType.Builder.of(WarpPipeBlockEntity::new,
                            Stream.concat(WARP_PIPES.values().stream().map(DeferredBlock::get),
                                    Stream.of(CLEAR_WARP_PIPE.get())).toArray(Block[]::new))
                    .build(null));

    public static final DeferredHolder<MenuType<?>, MenuType<WarpPipeMenu>> WARP_PIPE_MENU =
            MENUS.register("warp_pipe", () -> new MenuType<>(WarpPipeMenu::new, FeatureFlags.REGISTRY.allFlags()));

    static
    {
        // Keep below CLEAR_WARP_PIPE to prevent crash
        Arrays.stream(DyeColor.values()).forEach(color ->
                WARP_PIPES.put(color, BLOCKS.registerBlock(color.getName() + "_warp_pipe",
                        WarpPipeBlock::new, BlockBehaviour.Properties.of().mapColor(color)
                                .sound(SoundType.NETHERITE_BLOCK).strength(3.5F, 1000.0F)
                                .isViewBlocking(ModRegistry::always).requiresCorrectToolForDrops())));

        Arrays.stream(DyeColor.values()).forEach(color ->
                WARP_PIPE_ITEMS.put(color, registerItem(color.getName() + "_warp_pipe",
                        () -> new BlockItem(WARP_PIPES.get(color).get(), new Item.Properties()))));
    }

    public static <T extends Block> DeferredBlock<T> registerBlock(String name, Function<BlockBehaviour.Properties, ? extends T> block, BlockBehaviour.Properties properties)
    {
        //        WarpPipes.ITEMS.register(name, () -> new BlockItem(blocks.get(), new Item.Properties()));
        return BLOCKS.registerBlock(name, block, properties);
    }

    public static <T extends Block> DeferredBlock<T> registerNoItemBlock(String name, Supplier<T> block)
    {
        return BLOCKS.register(name, block);
    }

    public static <T extends Item> DeferredItem<T> registerItem(String name, Supplier<T> item)
    {
        return ITEMS.register(name, item);
    }

    private static boolean always(BlockState state, BlockGetter block, BlockPos pos)
    {
        return true;
    }

    private static boolean never(BlockState state, BlockGetter block, BlockPos pos)
    {
        return false;
    }

    public static void init(IEventBus bus) {
        BLOCKS.register(bus);
        ITEMS.register(bus);
        BLOCK_ENTITIES.register(bus);
        MENUS.register(bus);
    }
}
