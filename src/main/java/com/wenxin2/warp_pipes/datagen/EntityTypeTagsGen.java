package com.wenxin2.warp_pipes.datagen;

import com.wenxin2.warp_pipes.WarpPipes;
import com.wenxin2.warp_pipes.integration.CompatRegistry;
import com.wenxin2.warp_pipes.registries.TagRegistry;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class EntityTypeTagsGen extends EntityTypeTagsProvider {
    private static ResourceLocation PIRANHA_PLANT = ResourceLocation.fromNamespaceAndPath("marioverse", "piranha_plant");

    public EntityTypeTagsGen(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, WarpPipes.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider lookupProvider) {
        tag(TagRegistry.CANNOT_QUICK_TRAVEL)
                .addOptional(PIRANHA_PLANT);

        tag(TagRegistry.CANNOT_WARP)
                .addTag(Tags.EntityTypes.BOSSES)
                .add(EntityType.ELDER_GUARDIAN)
                .add(EntityType.ENDER_DRAGON)
                .add(EntityType.WITHER)
                .addOptionalTag(TagRegistry.TWILIGHT_FOREST_BOSSES);

        tag(TagRegistry.WARP_PIPE_CANNOT_SPAWN);
    }
}