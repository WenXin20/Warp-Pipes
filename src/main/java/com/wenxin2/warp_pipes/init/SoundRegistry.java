package com.wenxin2.warp_pipes.init;

import com.wenxin2.warp_pipes.WarpPipes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.RegistryObject;

public class SoundRegistry {
    public static final RegistryObject<SoundEvent> PIPES_LINKED;
    public static final RegistryObject<SoundEvent> PIPE_CLOSES;
    public static final RegistryObject<SoundEvent> PIPE_OPENS;
    public static final RegistryObject<SoundEvent> PIPE_WARPS;
    public static final RegistryObject<SoundEvent> WRENCH_BOUND;

    static {
        PIPES_LINKED = WarpPipes.SOUNDS.register("block.pipes_linked",
                () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(WarpPipes.MODID + ":block.pipes_linked")));
        PIPE_CLOSES = WarpPipes.SOUNDS.register("block.pipe_closes",
                () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(WarpPipes.MODID + ":block.pipe_closes")));
        PIPE_OPENS = WarpPipes.SOUNDS.register("block.pipe_opens",
                () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(WarpPipes.MODID + ":block.pipe_opens")));
        PIPE_WARPS = WarpPipes.SOUNDS.register("block.pipe_warps",
                () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(WarpPipes.MODID + ":block.pipe_warps")));
        WRENCH_BOUND = WarpPipes.SOUNDS.register("item.wrench_bound",
                () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(WarpPipes.MODID + ":item.wrench_bound")));
    }

    public static void init()
    {}
}
