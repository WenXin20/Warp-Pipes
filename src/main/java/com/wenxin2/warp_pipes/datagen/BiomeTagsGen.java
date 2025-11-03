package com.wenxin2.warp_pipes.datagen;

import com.wenxin2.warp_pipes.WarpPipes;
import com.wenxin2.warp_pipes.registries.TagRegistry;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.BiomeTagsProvider;
import net.minecraft.tags.BiomeTags;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class BiomeTagsGen extends BiomeTagsProvider {
    public BiomeTagsGen(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, WarpPipes.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider lookupProvider) {
        tag(TagRegistry.HAS_PIPE_TOWERS)
                .addTag(Tags.Biomes.IS_MUSHROOM);

        tag(TagRegistry.HAS_SUBMERGED_PIPES)
                .addTag(BiomeTags.IS_OVERWORLD);
    }
}