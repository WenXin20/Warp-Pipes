package com.wenxin2.warp_pipes.init;

import com.wenxin2.warp_pipes.WarpPipes;
import com.wenxin2.warp_pipes.blocks.WarpPipeBlock;
import com.wenxin2.warp_pipes.blocks.entities.WarpPipeBlockEntity;
import com.wenxin2.warp_pipes.items.WrenchItem;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = WarpPipes.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModRegistry {
    public static final RegistryObject<Item> PIPE_WRENCH;

    public static final RegistryObject<Block> GREEN_WARP_PIPE;
    public static final RegistryObject<Block> LIGHT_BLUE_WARP_PIPE;
    public static final RegistryObject<Block> LIGHT_GRAY_WARP_PIPE;
    public static final RegistryObject<Block> LIME_WARP_PIPE;
    public static final RegistryObject<Block> MAGENTA_WARP_PIPE;
    public static final RegistryObject<Block> ORANGE_WARP_PIPE;
    public static final RegistryObject<Block> PINK_WARP_PIPE;
    public static final RegistryObject<Block> WHITE_WARP_PIPE;
    public static final RegistryObject<Block> YELLOW_WARP_PIPE;

    public static final RegistryObject<BlockEntityType<WarpPipeBlockEntity>> WARP_PIPES;

    static
    {
        PIPE_WRENCH = registerItem("pipe_wrench",
                () -> new WrenchItem(new Item.Properties().durability(128).tab(WarpPipes.CREATIVE_TAB), Tiers.IRON));


        WHITE_WARP_PIPE = registerBlock("white_warp_pipe",
                () -> new WarpPipeBlock(BlockBehaviour.Properties.of(Material.METAL, MaterialColor.SNOW)
                        .sound(SoundType.NETHERITE_BLOCK).strength(3.5F, 1000.0F).isViewBlocking(ModRegistry::always)
                        .requiresCorrectToolForDrops()), WarpPipes.CREATIVE_TAB);

        ORANGE_WARP_PIPE = registerBlock("orange_warp_pipe",
                () -> new WarpPipeBlock(BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_ORANGE)
                        .sound(SoundType.NETHERITE_BLOCK).strength(3.5F, 1000.0F).isViewBlocking(ModRegistry::always)
                        .requiresCorrectToolForDrops()), WarpPipes.CREATIVE_TAB);

        MAGENTA_WARP_PIPE = registerBlock("magenta_warp_pipe",
                () -> new WarpPipeBlock(BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_MAGENTA)
                        .sound(SoundType.NETHERITE_BLOCK).strength(3.5F, 1000.0F).isViewBlocking(ModRegistry::always)
                        .requiresCorrectToolForDrops()), WarpPipes.CREATIVE_TAB);

        LIGHT_BLUE_WARP_PIPE = registerBlock("light_blue_warp_pipe",
                () -> new WarpPipeBlock(BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_LIGHT_BLUE)
                        .sound(SoundType.NETHERITE_BLOCK).strength(3.5F, 1000.0F).isViewBlocking(ModRegistry::always)
                        .requiresCorrectToolForDrops()), WarpPipes.CREATIVE_TAB);

        YELLOW_WARP_PIPE = registerBlock("yellow_warp_pipe",
                () -> new WarpPipeBlock(BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_YELLOW)
                        .sound(SoundType.NETHERITE_BLOCK).strength(3.5F, 1000.0F).isViewBlocking(ModRegistry::always)
                        .requiresCorrectToolForDrops()), WarpPipes.CREATIVE_TAB);

        LIME_WARP_PIPE = registerBlock("lime_warp_pipe",
                () -> new WarpPipeBlock(BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_LIGHT_GREEN)
                        .sound(SoundType.NETHERITE_BLOCK).strength(3.5F, 1000.0F).isViewBlocking(ModRegistry::always)
                        .requiresCorrectToolForDrops()), WarpPipes.CREATIVE_TAB);

        PINK_WARP_PIPE = registerBlock("pink_warp_pipe",
                () -> new WarpPipeBlock(BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_PINK)
                        .sound(SoundType.NETHERITE_BLOCK).strength(3.5F, 1000.0F).isViewBlocking(ModRegistry::always)
                        .requiresCorrectToolForDrops()), WarpPipes.CREATIVE_TAB);

        LIGHT_GRAY_WARP_PIPE = registerBlock("light_gray_warp_pipe",
                () -> new WarpPipeBlock(BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_LIGHT_GRAY)
                        .sound(SoundType.NETHERITE_BLOCK).strength(3.5F, 1000.0F).isViewBlocking(ModRegistry::always)
                        .requiresCorrectToolForDrops()), WarpPipes.CREATIVE_TAB);

        GREEN_WARP_PIPE = registerBlock("green_warp_pipe",
                () -> new WarpPipeBlock(BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_GREEN)
                        .sound(SoundType.NETHERITE_BLOCK).strength(3.5F, 1000.0F).isViewBlocking(ModRegistry::always)
                        .requiresCorrectToolForDrops()), WarpPipes.CREATIVE_TAB);


        WARP_PIPES = WarpPipes.BLOCK_ENTITIES.register("warp_pipe",
                () -> BlockEntityType.Builder.of(WarpPipeBlockEntity::new, ModRegistry.GREEN_WARP_PIPE.get()).build(null));
    }

    public static RegistryObject<Block> registerBlock(String name, Supplier<? extends Block> block, CreativeModeTab tab)
    {
        RegistryObject<Block> blocks = WarpPipes.BLOCKS.register(name, block);
        WarpPipes.ITEMS.register(name, () -> new BlockItem(blocks.get(), new Item.Properties().tab(tab)));
        return blocks;
    }

    public static RegistryObject<Block> registerFoodBlock(String name, Supplier<? extends Block> block, FoodProperties foodProperties, CreativeModeTab tab)
    {
        RegistryObject<Block> blocks = WarpPipes.BLOCKS.register(name, block);
        WarpPipes.ITEMS.register(name, () -> new BlockItem(blocks.get(), new Item.Properties().tab(tab).food(foodProperties)));
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
}
