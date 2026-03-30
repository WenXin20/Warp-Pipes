package com.wenxin2.warp_pipes.datagen;

import com.wenxin2.warp_pipes.WarpPipes;
import com.wenxin2.warp_pipes.registries.ModRegistry;
import com.wenxin2.warp_pipes.registries.TagRegistry;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class BlockTagsGen extends BlockTagsProvider {
    public BlockTagsGen(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, WarpPipes.MOD_ID, existingFileHelper);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void addTags(HolderLookup.Provider lookupProvider) {
        ModRegistry.PIPE_JUNCTION.values().forEach(block -> tag(TagRegistry.PIPE_JUNCTION_BLOCKS).add(block.get()));
        ModRegistry.WARP_PIPES.values().forEach(block -> tag(TagRegistry.DYEABLE_WARP_PIPE_BLOCKS).add(block.get()));

        for (DyeColor color : DyeColor.values()) {
            tag(TagRegistry.blockTags("c", "dyed/" + color))
                    .add(ModRegistry.PIPE_JUNCTION.get(color).get())
                    .add(ModRegistry.WARP_PIPES.get(color).get());
        }

        tag(TagRegistry.WARP_PIPE_BLOCKS)
                .addTag(TagRegistry.DYEABLE_WARP_PIPE_BLOCKS)
                .add(ModRegistry.CLEAR_WARP_PIPE.get());

        tag(TagRegistry.WRENCH_EFFICIENT)
                .addTag(TagRegistry.WARP_PIPE_BLOCKS);

        tag(BlockTags.IMPERMEABLE)
                .add(ModRegistry.CLEAR_WARP_PIPE.get());

        tag(BlockTags.NEEDS_STONE_TOOL)
                .addTag(TagRegistry.DYEABLE_WARP_PIPE_BLOCKS)
                .addTag(TagRegistry.PIPE_JUNCTION_BLOCKS);

        tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .addTag(TagRegistry.PIPE_JUNCTION_BLOCKS)
                .addTag(TagRegistry.WARP_PIPE_BLOCKS);
    }
}