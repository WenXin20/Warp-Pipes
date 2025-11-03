package com.wenxin2.warp_pipes.registries;

import com.wenxin2.warp_pipes.WarpPipes;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class TagRegistry {
    public static final TagKey<Block> DYEABLE_WARP_PIPE_BLOCKS = blockTags(WarpPipes.MOD_ID, "dyeable_warp_pipes");
    public static final TagKey<Block> WARP_PIPE_BLOCKS = blockTags(WarpPipes.MOD_ID, "warp_pipes");
    public static final TagKey<Block> WRENCH_EFFICIENT = blockTags(WarpPipes.MOD_ID, "wrench_efficient");

    public static final TagKey<Item> CAN_SELECT_CLEAR_WARP_PIPES = itemTags(WarpPipes.MOD_ID, "can_select_clear_warp_pipes");
    public static final TagKey<Item> CAN_SELECT_WATER_SPOUTS = itemTags(WarpPipes.MOD_ID, "can_select_water_spouts");
    public static final TagKey<Item> DYEABLE_WARP_PIPE_ITEMS = itemTags(WarpPipes.MOD_ID, "dyeable_warp_pipes");
    public static final TagKey<Item> POWER_UPS_ITEMS = itemTags("marioverse", "power_ups");
    public static final TagKey<Item> WARP_PIPE_CANNOT_SPAWN_ITEMS = itemTags(WarpPipes.MOD_ID, "warp_pipe_cannot_spawn");
    public static final TagKey<Item> WARP_PIPE_ITEMS = itemTags(WarpPipes.MOD_ID, "warp_pipes");

    public static final TagKey<EntityType<?>> CANNOT_QUICK_TRAVEL = entityTypeTags(WarpPipes.MOD_ID, "cannot_quick_travel");
    public static final TagKey<EntityType<?>> CANNOT_WARP = entityTypeTags(WarpPipes.MOD_ID, "cannot_warp");
    public static final TagKey<EntityType<?>> WARP_PIPE_CANNOT_SPAWN = entityTypeTags(WarpPipes.MOD_ID, "warp_pipe_cannot_spawn");

    public static TagKey<Block> blockTags(String id, String name) {
        return TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(id, name));
    }

    public static TagKey<Item> itemTags(String id, String name) {
        return TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(id, name));
    }

    public static TagKey<EntityType<?>> entityTypeTags(String id, String name) {
        return TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(id, name));
    }
}
