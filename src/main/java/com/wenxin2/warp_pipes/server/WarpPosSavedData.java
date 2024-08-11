package com.wenxin2.warp_pipes.server;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.saveddata.SavedData;

public class WarpPosSavedData extends SavedData {
    public WarpPosSavedData create() {
        return new WarpPosSavedData();
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider lookupProvider) {
        return null;
    }


    public WarpPosSavedData load(CompoundTag tag, HolderLookup.Provider lookupProvider) {
        WarpPosSavedData data = this.create();
        // Load saved data
        return data;
    }
}
