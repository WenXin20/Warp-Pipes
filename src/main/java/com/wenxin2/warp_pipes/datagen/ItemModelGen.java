package com.wenxin2.warp_pipes.datagen;

import com.wenxin2.warp_pipes.WarpPipes;
import com.wenxin2.warp_pipes.registries.ModRegistry;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class ItemModelGen extends ItemModelProvider {
    public ItemModelGen(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, WarpPipes.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        this.handheldItem(ModRegistry.WRENCH.get());
        this.handheldItem(ModRegistry.WARP_DISRUPTOR.get());
    }
}
