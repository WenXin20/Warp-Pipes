package com.wenxin2.warp_pipes.init;

import com.wenxin2.warp_pipes.WarpPipes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class ModTags {
    public static final TagKey<Block> WARP_PIPE_BLOCKS = blockTags(WarpPipes.MODID, "warp_pipes");
    public static final TagKey<Item> WARP_PIPE_ITEMS = itemTags(WarpPipes.MODID, "warp_pipes");

    public static TagKey<Item> itemTags(String id, String name) {
        return ItemTags.create(new ResourceLocation(id, name));
    }

    public static TagKey<Block> blockTags(String id, String name) {
        return BlockTags.create(new ResourceLocation(id, name));
    }
}
