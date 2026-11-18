package com.wenxin2.warp_pipes.entities.ai;

import net.minecraft.world.level.pathfinder.Node;

public interface PipeLinkNodeEvaluator {
    float wp$getPipeLinkCost(Node from, Node to);
}
