package com.wenxin2.warp_pipes.init;

import com.wenxin2.warp_pipes.WarpPipes;
import com.wenxin2.warp_pipes.blocks.ClearWarpPipeBlock;
import com.wenxin2.warp_pipes.blocks.PipeBubblesBlock;
import com.wenxin2.warp_pipes.blocks.WarpPipeBlock;
import com.wenxin2.warp_pipes.blocks.WaterSpoutBlock;
import com.wenxin2.warp_pipes.blocks.entities.WarpPipeBlockEntity;
import com.wenxin2.warp_pipes.items.WrenchItem;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.BlockItem;
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
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = WarpPipes.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModRegistry {
    public static final RegistryObject<Item> PIPE_WRENCH;

    public static final RegistryObject<Block> BLACK_WARP_PIPE;
    public static final RegistryObject<Block> BLUE_WARP_PIPE;
    public static final RegistryObject<Block> BROWN_WARP_PIPE;
    public static final RegistryObject<Block> CLEAR_WARP_PIPE;
    public static final RegistryObject<Block> CYAN_WARP_PIPE;
    public static final RegistryObject<Block> GRAY_WARP_PIPE;
    public static final RegistryObject<Block> GREEN_WARP_PIPE;
    public static final RegistryObject<Block> LIGHT_BLUE_WARP_PIPE;
    public static final RegistryObject<Block> LIGHT_GRAY_WARP_PIPE;
    public static final RegistryObject<Block> LIME_WARP_PIPE;
    public static final RegistryObject<Block> MAGENTA_WARP_PIPE;
    public static final RegistryObject<Block> ORANGE_WARP_PIPE;
    public static final RegistryObject<Block> PINK_WARP_PIPE;
    public static final RegistryObject<Block> PIPE_BUBBLES;
    public static final RegistryObject<Block> PURPLE_WARP_PIPE;
    public static final RegistryObject<Block> RED_WARP_PIPE;
    public static final RegistryObject<Block> WATER_SPOUT;
    public static final RegistryObject<Block> WHITE_WARP_PIPE;
    public static final RegistryObject<Block> YELLOW_WARP_PIPE;

    public static final RegistryObject<BlockEntityType<WarpPipeBlockEntity>> WARP_PIPE_BLOCK_ENTITY;

    static
    {
        PIPE_WRENCH = registerItem("pipe_wrench",
                () -> new WrenchItem(new Item.Properties().durability(128), Tiers.IRON));

        CLEAR_WARP_PIPE = registerBlock("clear_warp_pipe",
                () -> new ClearWarpPipeBlock(BlockBehaviour.Properties.of().mapColor(MapColor.NONE)
                        .sound(SoundType.GLASS).isSuffocating(ModRegistry::never).isViewBlocking(ModRegistry::never)
                        .isRedstoneConductor(ModRegistry::never).strength(3.0F, 500.0F)
                        .requiresCorrectToolForDrops().noOcclusion()));

        BLACK_WARP_PIPE = registerBlock("black_warp_pipe",
                () -> new WarpPipeBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLACK)
                        .sound(SoundType.NETHERITE_BLOCK).strength(3.5F, 1000.0F)
                        .isViewBlocking(ModRegistry::always).requiresCorrectToolForDrops()));

        BLUE_WARP_PIPE = registerBlock("blue_warp_pipe",
                () -> new WarpPipeBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLUE)
                        .sound(SoundType.NETHERITE_BLOCK).isViewBlocking(ModRegistry::always)
                        .strength(3.5F, 1000.0F).requiresCorrectToolForDrops()));

        BROWN_WARP_PIPE = registerBlock("brown_warp_pipe",
                () -> new WarpPipeBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BROWN)
                        .sound(SoundType.NETHERITE_BLOCK).isViewBlocking(ModRegistry::always)
                        .strength(3.5F, 1000.0F).requiresCorrectToolForDrops()));

        CYAN_WARP_PIPE = registerBlock("cyan_warp_pipe",
                () -> new WarpPipeBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_CYAN)
                        .sound(SoundType.NETHERITE_BLOCK).isViewBlocking(ModRegistry::always)
                        .strength(3.5F, 1000.0F).requiresCorrectToolForDrops()));

        GRAY_WARP_PIPE = registerBlock("gray_warp_pipe",
                () -> new WarpPipeBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GRAY)
                        .sound(SoundType.NETHERITE_BLOCK).isViewBlocking(ModRegistry::always)
                        .strength(3.5F, 1000.0F).requiresCorrectToolForDrops()));

        GREEN_WARP_PIPE = registerBlock("green_warp_pipe",
                () -> new WarpPipeBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GREEN)
                        .sound(SoundType.NETHERITE_BLOCK).isViewBlocking(ModRegistry::always)
                        .strength(3.5F, 1000.0F).requiresCorrectToolForDrops()));

        LIGHT_BLUE_WARP_PIPE = registerBlock("light_blue_warp_pipe",
                () -> new WarpPipeBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_BLUE)
                        .sound(SoundType.NETHERITE_BLOCK).isViewBlocking(ModRegistry::always)
                        .strength(3.5F, 1000.0F).requiresCorrectToolForDrops()));

        LIGHT_GRAY_WARP_PIPE = registerBlock("light_gray_warp_pipe",
                () -> new WarpPipeBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_GRAY)
                        .sound(SoundType.NETHERITE_BLOCK).isViewBlocking(ModRegistry::always)
                        .strength(3.5F, 1000.0F).requiresCorrectToolForDrops()));

        LIME_WARP_PIPE = registerBlock("lime_warp_pipe",
                () -> new WarpPipeBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_GREEN)
                        .sound(SoundType.NETHERITE_BLOCK).isViewBlocking(ModRegistry::always)
                        .strength(3.5F, 1000.0F).requiresCorrectToolForDrops()));

        MAGENTA_WARP_PIPE = registerBlock("magenta_warp_pipe",
                () -> new WarpPipeBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_MAGENTA)
                        .sound(SoundType.NETHERITE_BLOCK).isViewBlocking(ModRegistry::always)
                        .strength(3.5F, 1000.0F).requiresCorrectToolForDrops()));

        ORANGE_WARP_PIPE = registerBlock("orange_warp_pipe",
                () -> new WarpPipeBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_ORANGE)
                        .sound(SoundType.NETHERITE_BLOCK).isViewBlocking(ModRegistry::always)
                        .strength(3.5F, 1000.0F).requiresCorrectToolForDrops()));

        PINK_WARP_PIPE = registerBlock("pink_warp_pipe",
                () -> new WarpPipeBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_PINK)
                        .sound(SoundType.NETHERITE_BLOCK).isViewBlocking(ModRegistry::always)
                        .strength(3.5F, 1000.0F).requiresCorrectToolForDrops()));

        PURPLE_WARP_PIPE = registerBlock("purple_warp_pipe",
                () -> new WarpPipeBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_PURPLE)
                        .sound(SoundType.NETHERITE_BLOCK).isViewBlocking(ModRegistry::always)
                        .strength(3.5F, 1000.0F).requiresCorrectToolForDrops()));

        RED_WARP_PIPE = registerBlock("red_warp_pipe",
                () -> new WarpPipeBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_RED)
                        .sound(SoundType.NETHERITE_BLOCK).isViewBlocking(ModRegistry::always)
                        .strength(3.5F, 1000.0F)
                        .requiresCorrectToolForDrops()));

        WHITE_WARP_PIPE = registerBlock("white_warp_pipe",
                () -> new WarpPipeBlock(BlockBehaviour.Properties.of().mapColor(MapColor.SNOW)
                        .sound(SoundType.NETHERITE_BLOCK).isViewBlocking(ModRegistry::always)
                        .strength(3.5F, 1000.0F).requiresCorrectToolForDrops()));

        YELLOW_WARP_PIPE = registerBlock("yellow_warp_pipe",
                () -> new WarpPipeBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_YELLOW)
                        .sound(SoundType.NETHERITE_BLOCK).isViewBlocking(ModRegistry::always)
                        .strength(3.5F, 1000.0F).requiresCorrectToolForDrops()));


        PIPE_BUBBLES = registerNoItemBlock("pipe_bubbles",
                () -> new PipeBubblesBlock(BlockBehaviour.Properties.of().pushReaction(PushReaction.DESTROY)
                        .replaceable().noCollission().noLootTable().liquid()));

        WATER_SPOUT = registerNoItemBlock("water_spout",
                () -> new WaterSpoutBlock(BlockBehaviour.Properties.of().mapColor(MapColor.WATER).sound(SoundType.WET_GRASS)
                        .pushReaction(PushReaction.DESTROY).isRedstoneConductor(ModRegistry::never)
                        .isSuffocating(ModRegistry::never).isViewBlocking(ModRegistry::never)
                        .replaceable().noCollission().noLootTable()));


        WARP_PIPE_BLOCK_ENTITY = WarpPipes.BLOCK_ENTITIES.register("warp_pipe",
                () -> BlockEntityType.Builder.of(WarpPipeBlockEntity::new, ModRegistry.GREEN_WARP_PIPE.get(), ModRegistry.GREEN_WARP_PIPE.get()).build(null));
    }

    public static RegistryObject<Block> registerBlock(String name, Supplier<? extends Block> block)
    {
        RegistryObject<Block> blocks = WarpPipes.BLOCKS.register(name, block);
        WarpPipes.ITEMS.register(name, () -> new BlockItem(blocks.get(), new Item.Properties()));
        return blocks;
    }

    public static RegistryObject<Block> registerNoItemBlock(String name, Supplier<? extends Block> block)
    {
        RegistryObject<Block> blocks = WarpPipes.BLOCKS.register(name, block);
        return blocks;
    }

    public static RegistryObject<Item> registerItem(String name, Supplier<? extends Item> item)
    {
        return WarpPipes.ITEMS.register(name, item);
    }

    private static boolean always(BlockState state, BlockGetter block, BlockPos pos)
    {
        return true;
    }
    private static boolean never(BlockState state, BlockGetter block, BlockPos pos)
    {
        return false;
    }
}
