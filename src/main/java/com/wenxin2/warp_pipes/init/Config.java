package com.wenxin2.warp_pipes.init;

import net.minecraftforge.common.ForgeConfigSpec;

public class Config
{
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static ForgeConfigSpec CONFIG;

    public static final String CATEGORY_DEBUG = "Debug";
    public static final String CATEGORY_COMMON = "Common";

    public static ForgeConfigSpec.BooleanValue CREATIVE_WRENCH;
    public static ForgeConfigSpec.BooleanValue CREATIVE_WRENCH_PIPE_LINKING;
    public static ForgeConfigSpec.BooleanValue DEBUG_PIPE_BUBBLES_SELECTION_BOX;
    public static ForgeConfigSpec.BooleanValue DEBUG_WATER_SPOUT_SELECTION_BOX;
    public static ForgeConfigSpec.BooleanValue DEBUG_SELECTION_BOX;
    public static ForgeConfigSpec.BooleanValue DEBUG_SELECTION_BOX_CREATIVE;
    public static ForgeConfigSpec.IntValue WARP_COOLDOWN;

    static
    {
        initializeConfig();
        CONFIG = BUILDER.build();
    }

    public static void initializeConfig()
    {
        BUILDER.push(CATEGORY_COMMON);
        CREATIVE_WRENCH = BUILDER.comment("Require creative to turn bubbles on/off and open/close pipes. " + "[Default: false]")
                .define("creative_wrench", false);
        CREATIVE_WRENCH_PIPE_LINKING = BUILDER.comment("Require creative to link pipes. " + "[Default: false]")
                .define("creative_wrench_pipe_linking", false);
        WARP_COOLDOWN = BUILDER.comment("Cooldown between teleports in ticks. " + "[Default: 30]")
                .defineInRange("warp_cooldown", 50, 0, 9000);
        BUILDER.pop();

        BUILDER.comment("Warp Pipes Config").push(CATEGORY_DEBUG);
        DEBUG_SELECTION_BOX = BUILDER.comment("Enable debug selection box for Clear Warp Pipes. " + "[Default: false]")
                .define("debug_selection_box", false);
        DEBUG_SELECTION_BOX_CREATIVE = BUILDER.comment("Enable debug selection box for Clear Warp Pipes in Creative. " + "[Default: true]")
                .define("debug_selection_box_creative", true);
        DEBUG_PIPE_BUBBLES_SELECTION_BOX = BUILDER.comment("Enable debug selection box for Pipe Bubbles. Creative Only. " + "[Default: false]")
                .define("debug_pipe_bubbles_selection_box", false);
        DEBUG_WATER_SPOUT_SELECTION_BOX = BUILDER.comment("Enable debug selection box for Water Spouts. Creative Only. " + "[Default: false]")
                .define("debug_water_spout_selection_box", false);
        BUILDER.pop();
    }
}
