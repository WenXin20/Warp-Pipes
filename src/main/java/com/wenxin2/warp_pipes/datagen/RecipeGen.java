package com.wenxin2.warp_pipes.datagen;

import com.wenxin2.warp_pipes.WarpPipes;
import com.wenxin2.warp_pipes.registries.ModRegistry;
import com.wenxin2.warp_pipes.registries.TagRegistry;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.registries.DeferredBlock;

public class RecipeGen extends RecipeUtils {
    public RecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider);
    }

    @Override
    protected void buildRecipes(RecipeOutput output) {
        oneToOneConversionRecipe(output, ModRegistry.WRENCH, ModRegistry.WRENCH, WarpPipes.MOD_ID + ":" + getSimpleRecipeName(ModRegistry.WRENCH));
        twoItemTagRecipe(1, "warp_pipes", "_from_glass", ModRegistry.CLEAR_WARP_PIPE, RecipeCategory.BUILDING_BLOCKS, TagRegistry.DYEABLE_WARP_PIPE_ITEMS, Tags.Items.GLASS_BLOCKS_COLORLESS, output);
        warpDisruptorRecipe(1, ModRegistry.WARP_DISRUPTOR, Items.ENDER_EYE, Tags.Items.INGOTS_GOLD, Tags.Items.RODS_WOODEN, output);
        warpPipeRecipe(4, ModRegistry.CLEAR_WARP_PIPE, Tags.Items.INGOTS_COPPER, Tags.Items.GLASS_BLOCKS_COLORLESS, Tags.Items.GEMS_DIAMOND, Tags.Items.ENDER_PEARLS, output);
        wrenchRecipe(1, ModRegistry.WRENCH, Tags.Items.INGOTS_IRON, output);

        for (Map.Entry<DyeColor, DeferredBlock<Block>> entry : ModRegistry.PIPE_JUNCTION.entrySet()) {
            DyeColor dyeColor = entry.getKey();
            TagKey<Item> dyeItemTag = TagRegistry.itemTags("c", "dyes/" + dyeColor.getName());

            dyeItemRecipe(1, "pipe_corners_from_dye", entry.getValue(), RecipeCategory.BUILDING_BLOCKS, dyeItemTag, TagRegistry.PIPE_JUNCTION_BLOCK_ITEMS, output);
        }

        for (Map.Entry<DyeColor, DeferredBlock<Block>> entry : ModRegistry.WARP_PIPES.entrySet()) {
            DyeColor dyeColor = entry.getKey();
            TagKey<Item> dyeItemTag = TagRegistry.itemTags("c", "dyes/" + dyeColor.getName());

            warpPipeRecipe(4, entry.getValue(), Tags.Items.INGOTS_COPPER, dyeItemTag, Tags.Items.GEMS_DIAMOND, Tags.Items.ENDER_PEARLS, output);
            dyeItemRecipe(1, "warp_pipes_from_dye", entry.getValue(), RecipeCategory.BUILDING_BLOCKS, dyeItemTag, TagRegistry.DYEABLE_WARP_PIPE_ITEMS, output);
        }
    }
}