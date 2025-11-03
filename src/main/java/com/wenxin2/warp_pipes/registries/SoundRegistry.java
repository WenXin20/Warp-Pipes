package com.wenxin2.warp_pipes.registries;

import com.wenxin2.warp_pipes.WarpPipes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.SoundType;
import net.neoforged.neoforge.registries.DeferredHolder;

public class SoundRegistry {
    public static final DeferredHolder<SoundEvent, SoundEvent> CLEAR_PIPE_ENTER;
    public static final DeferredHolder<SoundEvent, SoundEvent> CLEAR_PIPE_EXIT;
    public static final DeferredHolder<SoundEvent, SoundEvent> CLEAR_PIPE_INSIDE;
    public static final DeferredHolder<SoundEvent, SoundEvent> GLASS_HIT;
    public static final DeferredHolder<SoundEvent, SoundEvent> GLASS_FALL;
    public static final DeferredHolder<SoundEvent, SoundEvent> GLASS_STEP;
    public static final DeferredHolder<SoundEvent, SoundEvent> ITEM_SPAWNS;
    public static final DeferredHolder<SoundEvent, SoundEvent> PIPES_LINKED;
    public static final DeferredHolder<SoundEvent, SoundEvent> PIPE_CLOSES;
    public static final DeferredHolder<SoundEvent, SoundEvent> PIPE_OPENS;
    public static final DeferredHolder<SoundEvent, SoundEvent> PIPE_WARPS;
    public static final DeferredHolder<SoundEvent, SoundEvent> WATER_SPOUT_BREAK;
    public static final DeferredHolder<SoundEvent, SoundEvent> WATER_SPOUT_FALL;
    public static final DeferredHolder<SoundEvent, SoundEvent> WATER_SPOUT_HIT;
    public static final DeferredHolder<SoundEvent, SoundEvent> WATER_SPOUT_PLACE;
    public static final DeferredHolder<SoundEvent, SoundEvent> WATER_SPOUT_STEP;
    public static final DeferredHolder<SoundEvent, SoundEvent> WRENCH_BOUND;

    static {
        CLEAR_PIPE_ENTER = WarpPipes.SOUNDS.register("block.clear_pipe_enter",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(WarpPipes.MOD_ID, "block.clear_pipe_enter")));
        CLEAR_PIPE_EXIT = WarpPipes.SOUNDS.register("block.clear_pipe_exit",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(WarpPipes.MOD_ID, "block.clear_pipe_exit")));
        CLEAR_PIPE_INSIDE = WarpPipes.SOUNDS.register("block.clear_pipe_inside",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(WarpPipes.MOD_ID, "block.clear_pipe_inside")));

        GLASS_HIT = WarpPipes.SOUNDS.register("block.glass_hit",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(WarpPipes.MOD_ID, "block.glass_hit")));
        GLASS_FALL = WarpPipes.SOUNDS.register("block.glass_fall",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(WarpPipes.MOD_ID, "block.glass_fall")));
        GLASS_STEP = WarpPipes.SOUNDS.register("block.glass_step",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(WarpPipes.MOD_ID, "block.glass_step")));

        ITEM_SPAWNS = WarpPipes.SOUNDS.register("block.item_spawns",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(WarpPipes.MOD_ID, "block.item_spawns")));
        
        PIPES_LINKED = WarpPipes.SOUNDS.register("block.pipes_linked",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(WarpPipes.MOD_ID, "block.pipes_linked")));
        PIPE_CLOSES = WarpPipes.SOUNDS.register("block.pipe_closes",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(WarpPipes.MOD_ID, "block.pipe_closes")));
        PIPE_OPENS = WarpPipes.SOUNDS.register("block.pipe_opens",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(WarpPipes.MOD_ID, "block.pipe_opens")));
        PIPE_WARPS = WarpPipes.SOUNDS.register("block.pipe_warps",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(WarpPipes.MOD_ID, "block.pipe_warps")));
        
        WATER_SPOUT_BREAK = WarpPipes.SOUNDS.register("block.water_spout.break",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(WarpPipes.MOD_ID, "block.water_spout.break")));
        WATER_SPOUT_FALL = WarpPipes.SOUNDS.register("block.water_spout.fall",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(WarpPipes.MOD_ID, "block.water_spout.fall")));
        WATER_SPOUT_HIT = WarpPipes.SOUNDS.register("block.water_spout.hit",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(WarpPipes.MOD_ID, "block.water_spout.hit")));
        WATER_SPOUT_PLACE = WarpPipes.SOUNDS.register("block.water_spout.place",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(WarpPipes.MOD_ID, "block.water_spout.place")));
        WATER_SPOUT_STEP = WarpPipes.SOUNDS.register("block.water_spout.step",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(WarpPipes.MOD_ID, "block.water_spout.step")));
        
        WRENCH_BOUND = WarpPipes.SOUNDS.register("item.wrench_bound",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(WarpPipes.MOD_ID, "item.wrench_bound")));
    }

    public static void init()
    {}
}
