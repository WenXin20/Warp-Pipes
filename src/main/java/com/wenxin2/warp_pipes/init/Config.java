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
    public static ForgeConfigSpec.BooleanValue TELEPORT_MOBS;
    public static ForgeConfigSpec.BooleanValue TELEPORT_NON_MOBS;
    public static ForgeConfigSpec.BooleanValue TELEPORT_PLAYERS;
    public static ForgeConfigSpec.IntValue WARP_COOLDOWN;
    public static ForgeConfigSpec.BooleanValue WARP_COOLDOWN_MESSAGE;
    public static ForgeConfigSpec.BooleanValue WARP_COOLDOWN_MESSAGE_TICKS;

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
        TELEPORT_MOBS = BUILDER.comment("Allow mobs to teleport. " + "[Default: true]")
                .define("teleport_mobs", true);
        TELEPORT_NON_MOBS = BUILDER.comment("Allow non living entities to teleport. " + "[Default: true]")
                .define("teleport_non_mobs", true);
        TELEPORT_PLAYERS = BUILDER.comment("Allow players to teleport. " + "[Default: true]")
                .define("teleport_players", true);
        WARP_COOLDOWN = BUILDER.comment("Cooldown between teleports in ticks. " + "[Default: 30]")
                .defineInRange("warp_cooldown", 50, 0, 8000);
        WARP_COOLDOWN_MESSAGE = BUILDER.comment("Display a warp cooldown message. " + "[Default: false]")
                .define("warp_cooldown_message", false);
        WARP_COOLDOWN_MESSAGE_TICKS = BUILDER.comment("Display a warp cooldown message with ticks. Requires \"warp_cooldown_message\". " + "[Default: false]")
                .define("warp_cooldown_message_with_ticks", false);
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
