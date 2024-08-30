//package com.wenxin2.warp_pipes.server;
//
//import java.util.HashMap;
//import java.util.Map;
//import java.util.UUID;
//import net.minecraft.nbt.CompoundTag;
//
//public class WarpProxy {
//    private static final WarpProxy INSTANCE = new WarpProxy();
//    private final Map<UUID, WarpData> warpPositions = new HashMap<>();
//    private WarpData warpPipe;
//
//    private WarpProxy() {
//    }
//
//    public static WarpProxy getInstance() {
//        return INSTANCE;
//    }
//
//    public void registerWarp(UUID warpUUID, WarpData data) {
//        warpPositions.put(warpUUID, data);
//    }
//
//    public WarpData getWarp(UUID warpUUID) {
//        return warpPositions.get(warpUUID);
//    }
//
//    public void removeWarp(UUID warpUUID) {
//        warpPositions.remove(warpUUID);
//    }
//
//    // Save and load methods to persist data to the server
//    public CompoundTag save(CompoundTag nbt) {
//        // Serialize warpPositions to NBT
//        return nbt;
//    }
//
//    public void load(CompoundTag nbt) {
//        // Deserialize warpPositions from NBT
//    }
//}
