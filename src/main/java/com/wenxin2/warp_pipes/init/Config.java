package com.wenxin2.warp_pipes.init;

import net.minecraftforge.common.ForgeConfigSpec;

public class Config
{
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static ForgeConfigSpec CONFIG;

    public static final String CATEGORY_DEBUG = "Debug";
    public static final String CATEGORY_COMMON = "Common";

    public static ForgeConfigSpec.BooleanValue DEBUG_PIPE_BUBBLES_SELECTION_BOX;
    public static ForgeConfigSpec.BooleanValue DEBUG_SELECTION_BOX;
    public static ForgeConfigSpec.BooleanValue DEBUG_SELECTION_BOX_CREATIVE;
    public static ForgeConfigSpec.IntValue PIPE_BUBBLES_DISTANCE;

    static
    {
        initializeConfig();
        CONFIG = BUILDER.build();
    }

    public static void initializeConfig()
    {
        BUILDER.comment("Warp Pipes Config").push(CATEGORY_DEBUG);
        DEBUG_SELECTION_BOX = BUILDER.comment("Enable debug selection box for Clear Warp Pipes. " + "[Default: false]")
                .define("debug_selection_box", false);
        DEBUG_SELECTION_BOX_CREATIVE = BUILDER.comment("Enable debug selection box for Clear Warp Pipes in Creative. " + "[Default: true]")
                .define("debug_selection_box_creative", true);
        DEBUG_PIPE_BUBBLES_SELECTION_BOX = BUILDER.comment("Enable debug selection box for Pipe Bubbles. Creative Only. " + "[Default: false]")
                .define("debug_pipe_bubbles_selection_box", false);
        BUILDER.pop();

        BUILDER.push(CATEGORY_COMMON);
        PIPE_BUBBLES_DISTANCE = BUILDER.comment("Max distance on blocks underwater pipes push you. " + "[Default: 3]")
                .defineInRange("rotation_distance", 3, 1, 8);
        BUILDER.pop();
    }
}
