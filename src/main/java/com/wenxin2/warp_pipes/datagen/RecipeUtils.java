package com.wenxin2.warp_pipes.datagen;

import com.wenxin2.warp_pipes.WarpPipes;
import java.util.concurrent.CompletableFuture;
import net.minecraft.advancements.Criterion;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;

public class RecipeUtils extends RecipeProvider {
    public RecipeUtils(PackOutput output, CompletableFuture<HolderLookup.Provider> provider) {
        super(output, provider);
    }

    public void warpDisruptorRecipe(int outputAmt, ItemLike outputItem, ItemLike inputItem, TagKey<Item> inputItemTag, TagKey<Item> inputItemTag2, RecipeOutput output) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, outputItem, outputAmt)
                .define('E', inputItem)
                .define('G', inputItemTag)
                .define('S', inputItemTag2)
                .pattern("  E")
                .pattern(" G ")
                .pattern("S  ")
                .unlockedBy(getHasName(inputItem), has(inputItem))
                .unlockedBy("has_gold_ingot", has(inputItemTag))
                .unlockedBy("has_stick", has(inputItemTag2))
                .group(WarpPipes.MOD_ID + ":" + getSimpleRecipeName(outputItem))
                .save(output);
    }

    public void warpPipeRecipe(int outputAmt, ItemLike outputItem, TagKey<Item> inputItemTag, TagKey<Item> inputItemTag2,
                               TagKey<Item> inputItemTag3, TagKey<Item> inputItemTag4, RecipeOutput output) {
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, outputItem, outputAmt)
                .define('I', inputItemTag)
                .define('D', inputItemTag2)
                .define('G', inputItemTag3)
                .define('E', inputItemTag4)
                .pattern("IDI")
                .pattern("IGI")
                .pattern("IEI")
                .unlockedBy("has_copper_ingot", has(inputItemTag))
                .unlockedBy("has_dye", has(inputItemTag2))
                .unlockedBy("has_diamond", has(inputItemTag3))
                .unlockedBy("has_ender_pearl", has(inputItemTag4))
                .group(WarpPipes.MOD_ID + ":warp_pipes")
                .save(output);
    }

    public void wrenchRecipe(int outputAmt, ItemLike outputItem, TagKey<Item> inputItemTag, RecipeOutput output) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, outputItem, outputAmt)
                .define('I', inputItemTag)
                .pattern("I I")
                .pattern(" I ")
                .pattern(" I ")
                .unlockedBy("has_iron_ingot", has(inputItemTag))
                .group(WarpPipes.MOD_ID + ":" + getSimpleRecipeName(outputItem))
                .save(output);
    }

    public void dyeItemRecipe(int outputAmt, String groupName, ItemLike outputItem, RecipeCategory category,
                              Object input1, Object input2, RecipeOutput output) {
        ShapelessRecipeBuilder builder = ShapelessRecipeBuilder.shapeless(category, outputItem, outputAmt)
                .group(WarpPipes.MOD_ID + ":" + groupName);

        builder.unlockedBy(getUnlockName(input1), unlockCriterion(input1));
        builder.unlockedBy(getUnlockName(input2), unlockCriterion(input2));

        if (input1 instanceof ItemLike itemLike)
            builder.requires(itemLike);
        else if (input1 instanceof TagKey<?> itemLike && itemLike.registry() == Registries.ITEM) {
            TagKey<Item> tag = (TagKey<Item>) itemLike;
            builder.requires(tag);
        }

        if (input2 instanceof ItemLike itemLike)
            builder.requires(itemLike);
        else if (input2 instanceof TagKey<?> itemLike && itemLike.registry() == Registries.ITEM) {
            TagKey<Item> tag = (TagKey<Item>) itemLike;
            builder.requires(tag);
        }

        builder.save(output, WarpPipes.MOD_ID + ":" + getItemName(outputItem) + "_from_dye");
    }

    public void twoItemTagRecipe(int outputAmt, String groupName, String recipeName, ItemLike outputItem, RecipeCategory category,
                                 TagKey<Item> itemTag, TagKey<Item> itemTag2, RecipeOutput output) {
        ShapelessRecipeBuilder.shapeless(category, outputItem, outputAmt)
                .requires(itemTag)
                .requires(itemTag2)
                .unlockedBy("has_tag_item", has(itemTag))
                .unlockedBy("has_tag_item2", has(itemTag2))
                .group(WarpPipes.MOD_ID + ":" + groupName)
                .save(output, WarpPipes.MOD_ID + ":" + getSimpleRecipeName(outputItem) + recipeName);
    }


    @SuppressWarnings("unchecked")
    private void defineIngredient(ShapedRecipeBuilder builder, char symbol, Object ingredient) {
        if (ingredient instanceof ItemLike item)
            builder.define(symbol, item);
        else if (ingredient instanceof TagKey<?> tag && tag.registry() == Registries.ITEM)
            builder.define(symbol, (TagKey<Item>) tag);
        else throw new IllegalArgumentException("Unsupported ingredient type: " + ingredient);
    }

    private String getUnlockName(Object ingredient) {
        if (ingredient instanceof ItemLike item)
            return getHasName(item);
        else if (ingredient instanceof TagKey<?> tag)
            return "has_" + tag.location().getPath();
        throw new IllegalArgumentException("Unsupported ingredient type: " + ingredient);
    }

    @SuppressWarnings("unchecked")
    private Criterion<?> unlockCriterion(Object ingredient) {
        if (ingredient instanceof ItemLike item)
            return RecipeProvider.has(item);
        else if (ingredient instanceof TagKey<?> raw && raw.registry() == Registries.ITEM) {
            TagKey<Item> tag = (TagKey<Item>) raw;
            return RecipeProvider.has(tag);
        }
        throw new IllegalArgumentException("Unsupported ingredient: " + ingredient);
    }
}
