package com.wenxin2.warp_pipes.event_handlers;

import com.wenxin2.warp_pipes.WarpPipes;
import com.wenxin2.warp_pipes.datagen.BiomeTagsGen;
import com.wenxin2.warp_pipes.datagen.BlockLootTableGen;
import com.wenxin2.warp_pipes.datagen.BlockStateGen;
import com.wenxin2.warp_pipes.datagen.BlockTagsGen;
import com.wenxin2.warp_pipes.datagen.EntityTypeTagsGen;
import com.wenxin2.warp_pipes.datagen.ItemModelGen;
import com.wenxin2.warp_pipes.datagen.ItemTagsGen;
import com.wenxin2.warp_pipes.datagen.RecipeGen;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

@EventBusSubscriber(modid = WarpPipes.MOD_ID)
public class RegistryEventHandlers {

    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        BlockTagsGen blockTags = new BlockTagsGen(output, lookupProvider, existingFileHelper);

        generator.addProvider(event.includeClient(), new BlockStateGen(output, existingFileHelper));
        generator.addProvider(event.includeClient(), new ItemModelGen(output, existingFileHelper));

        generator.addProvider(event.includeServer(), blockTags);
        generator.addProvider(event.includeServer(), new BiomeTagsGen(output, lookupProvider, existingFileHelper));
        generator.addProvider(event.includeServer(), new BlockLootTableGen(output, lookupProvider));
        generator.addProvider(event.includeServer(), new EntityTypeTagsGen(output, lookupProvider, existingFileHelper));
        generator.addProvider(event.includeServer(), new ItemTagsGen(output, lookupProvider, blockTags.contentsGetter(), existingFileHelper));
        generator.addProvider(event.includeServer(), new RecipeGen(output, lookupProvider));
    }
}
