//package com.wenxin2.warp_pipes.server;
//
//import java.util.UUID;
//import net.minecraft.core.BlockPos;
//import net.minecraft.nbt.CompoundTag;
//import net.minecraft.resources.ResourceKey;
//import net.minecraft.world.level.Level;
//import net.minecraft.world.level.dimension.DimensionType;
//
//public class WarpData {
//    private final BlockPos position;
//    private final ResourceKey<Level> dimension;
//    private final UUID warpUuid;
//
//    public WarpData(BlockPos position, ResourceKey<Level> dimension, UUID warpUuid) {
//        this.position = position;
//        this.dimension = dimension;
//        this.warpUuid = warpUuid;
//    }
//
//    public BlockPos getPosition() {
//        return position;
//    }
//
//    public ResourceKey<Level> getDimension() {
//        return dimension;
//    }
//
//    public UUID getWarpUuid() {
//        return warpUuid;
//    }
//
//    // Method to save the WarpData to NBT
//    public CompoundTag writeToTag(CompoundTag tag) {
//        tag.putLong("Position", position.asLong());
//        tag.putString("Dimension", dimension.toString());
//        tag.putUUID("WarpUUID", warpUuid);
//        return tag;
//    }
//
//    // Method to load the WarpData from NBT
//    public WarpData readFromTag(CompoundTag tag) {
//        BlockPos position = BlockPos.of(tag.getLong("Position"));
////        ResourceKey<Level> dimension = dimension;
//        UUID warpUuid = tag.getUUID("WarpUUID");
//        return new WarpData(position, dimension, warpUuid);
//    }
//}
