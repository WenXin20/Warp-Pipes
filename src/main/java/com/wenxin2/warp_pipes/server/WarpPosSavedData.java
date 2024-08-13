//package com.wenxin2.warp_pipes.server;
//
//import java.util.HashSet;
//import java.util.Optional;
//import java.util.Set;
//import net.minecraft.core.BlockPos;
//import net.minecraft.core.HolderLookup;
//import net.minecraft.nbt.CompoundTag;
//import net.minecraft.nbt.ListTag;
//import net.minecraft.nbt.NbtUtils;
//import net.minecraft.nbt.Tag;
//import net.minecraft.server.level.ServerLevel;
//import net.minecraft.world.level.saveddata.SavedData;
//
//public class WarpPosSavedData extends SavedData {
//    private final Set<BlockPos> warpPositions = new HashSet<>();
//    private static final String WARP_POS_SAVED_DATA = "warp_pos_saved_data";
//
//    public WarpPosSavedData create() {
//        return new WarpPosSavedData();
//    }
//
//    public WarpPosSavedData get(ServerLevel world) {
//        return world.getDataStorage().computeIfAbsent(new Factory<>(this::create, this::load), WARP_POS_SAVED_DATA);
//    }
//
//    @Override
//    public CompoundTag save(CompoundTag nbt, HolderLookup.Provider lookupProvider) {
//        ListTag warpList = new ListTag();
//        for (BlockPos pos : warpPositions) {
//            CompoundTag posTag = new CompoundTag();
//            posTag.put("Position", NbtUtils.writeBlockPos(pos));
//            warpList.add(posTag);
//        }
//        nbt.put("WarpPositions", warpList);
//        return nbt;
//    }
//
//    public WarpPosSavedData load(CompoundTag nbt, HolderLookup.Provider lookupProvider) {
//        WarpPosSavedData data = new WarpPosSavedData();
//        ListTag warpList = nbt.getList("WarpPositions", Tag.TAG_COMPOUND);
//        for (int i = 0; i < warpList.size(); i++) {
//            CompoundTag posTag = warpList.getCompound(i);
//            Optional<BlockPos> pos = NbtUtils.readBlockPos(posTag, "WarpPositions");
//            pos.ifPresent(data.warpPositions::add);
//        }
//        return data;
//    }
//
//    public void addWarpPosition(BlockPos pos) {
//        warpPositions.add(pos);
//        setDirty(); // Mark the data as needing to be saved
//    }
//
//    public boolean removeWarpPosition(BlockPos pos) {
//        boolean removed = warpPositions.remove(pos);
//        if (removed) {
//            setDirty(); // Mark the data as needing to be saved
//        }
//        return removed;
//    }
//
//    public boolean containsWarpPosition(BlockPos pos) {
//        return warpPositions.contains(pos);
//    }
//
//    public Set<BlockPos> getWarpPositions() {
//        return warpPositions;
//    }
//}
