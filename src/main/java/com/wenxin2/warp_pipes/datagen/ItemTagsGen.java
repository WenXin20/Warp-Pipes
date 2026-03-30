package com.wenxin2.warp_pipes.datagen;

import com.wenxin2.warp_pipes.WarpPipes;
import com.wenxin2.warp_pipes.registries.ModRegistry;
import com.wenxin2.warp_pipes.registries.TagRegistry;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class ItemTagsGen extends ItemTagsProvider {
    private static final ResourceLocation CREATE_SUPER_GLUE = ResourceLocation.fromNamespaceAndPath("create", "super_glue");

    public  ItemTagsGen(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider,
                        CompletableFuture<TagLookup<Block>> blockTagProvider, ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, blockTagProvider, WarpPipes.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider lookupProvider) {
        copy(TagRegistry.DYEABLE_WARP_PIPE_BLOCKS, TagRegistry.DYEABLE_WARP_PIPE_ITEMS);
        copy(TagRegistry.PIPE_JUNCTION_BLOCKS, TagRegistry.PIPE_JUNCTION_BLOCK_ITEMS);
        copy(TagRegistry.WARP_PIPE_BLOCKS, TagRegistry.WARP_PIPE_ITEMS);

        for (DyeColor color : DyeColor.values())
            copy(TagRegistry.blockTags("c", "dyed/" + color), TagRegistry.itemTags("c", "dyed/" + color));

        tag(ItemTags.DURABILITY_ENCHANTABLE)
                .add(ModRegistry.WRENCH.get())
                .add(ModRegistry.WARP_DISRUPTOR.get());

        tag(ItemTags.FIRE_ASPECT_ENCHANTABLE)
                .add(ModRegistry.WRENCH.get());

        tag(ItemTags.WEAPON_ENCHANTABLE)
                .add(ModRegistry.WRENCH.get());

        tag(Tags.Items.MELEE_WEAPON_TOOLS)
                .add(ModRegistry.WRENCH.get());

        tag(Tags.Items.TOOLS_WRENCH)
                .add(ModRegistry.WRENCH.get());

        tag(TagRegistry.CAN_SELECT_CLEAR_WARP_PIPES)
                .addTag(ItemTags.AXES)
                .addTag(ItemTags.HOES)
                .addTag(ItemTags.PICKAXES)
                .addTag(ItemTags.SHOVELS)
                .add(ModRegistry.WRENCH.get())
                .add(Items.DEBUG_STICK)
                .addOptional(CREATE_SUPER_GLUE);

        tag(TagRegistry.CAN_SELECT_WATER_SPOUTS)
                .add(Items.DEBUG_STICK)
                .addOptional(CREATE_SUPER_GLUE);

        tag(TagRegistry.WARP_PIPE_CANNOT_SPAWN_ITEMS)
                .addTag(Tags.Items.DYES)
                .addTag(Tags.Items.TOOLS_WRENCH)
                .addTag(TagRegistry.WARP_PIPE_ITEMS)
                .add(ModRegistry.WRENCH.get())
                .add(ModRegistry.WARP_DISRUPTOR.get())
                .add(Items.BRUSH)
                .add(Items.DEBUG_STICK)
                .add(Items.GLOW_INK_SAC)
                .add(Items.INK_SAC);

        tag(TagRegistry.WRENCHES)
                .add(ModRegistry.WRENCH.get());
    }
}