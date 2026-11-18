package com.wenxin2.warp_pipes.utils;

import com.wenxin2.warp_pipes.blocks.ClearWarpPipeBlock;
import com.wenxin2.warp_pipes.blocks.WarpPipeBlock;
import com.wenxin2.warp_pipes.blocks.entities.BaseWarpBlockEntity;
import com.wenxin2.warp_pipes.registries.ConfigRegistry;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.Path;
import org.jetbrains.annotations.Nullable;

public class WarpLinkPathing {
    public static final float WARP_LINK_COST = 2.0F;

    public record WarpLink(BlockPos trigger, BlockPos destinationStand, boolean jump, @Nullable BlockPos door) {}

    public static boolean canPathThroughWarps(Mob mob) {
        return ConfigRegistry.TELEPORT_MOBS.get() && mob.getNavigation().getNodeEvaluator().canOpenDoors();
    }

    public static List<WarpLink> getWarpLinks(Level level, BlockPos trigger, Entity entity) {
        List<WarpLink> links = new ArrayList<>();
        WarpLinkPathing.addPipeLink(level, trigger, trigger.below(), Direction.UP, false, links);

        for (Direction direction : Direction.Plane.HORIZONTAL)
            WarpLinkPathing.addPipeLink(level, trigger, trigger.relative(direction), direction.getOpposite(), false, links);

        BlockPos downPipe = ClearPipeNetwork.findReachableDownPipe(level, trigger, entity, WarpLinkPathing::isOpenDownWarpPipe);
        if (downPipe != null)
            WarpLinkPathing.addPipeLink(level, trigger, downPipe, Direction.DOWN, true, links);
        return links;
    }

    @Nullable
    public static WarpLink findWarpStep(Level level, @Nullable Path path, Entity entity) {
        if (path == null || path.isDone())
            return null;

        int next = path.getNextNodeIndex();
        for (int index = Math.max(0, next - 1); index <= next && index + 1 < path.getNodeCount(); index++) {
            BlockPos trigger = path.getNode(index).asBlockPos();
            BlockPos stand = path.getNode(index + 1).asBlockPos();

            for (WarpLink link : WarpLinkPathing.getWarpLinks(level, trigger, entity)) {
                if (link.destinationStand().equals(stand))
                    return link;
            }
        }
        return null;
    }

    public static void openDoor(Level level, Mob mob, WarpLink link) {
        if (link.door() == null)
            return;

        BlockState state = level.getBlockState(link.door());
        if (state.getBlock() instanceof DoorBlock doorBlock && !state.getValue(DoorBlock.OPEN) && doorBlock.type().canOpenByHand())
            doorBlock.setOpen(mob, level, state, link.door(), true);
    }

    private static boolean isOpenDownWarpPipe(BlockState state) {
        return state.getBlock() instanceof WarpPipeBlock && !(state.getBlock() instanceof ClearWarpPipeBlock)
                && state.getValue(WarpPipeBlock.ENTRANCE) && !state.getValue(WarpPipeBlock.CLOSED)
                && state.getValue(WarpPipeBlock.FACING) == Direction.DOWN;
    }

    private static void addPipeLink(Level level, BlockPos trigger, BlockPos pipe, Direction requiredFacing, boolean jump, List<WarpLink> links) {
        BlockState state = level.getBlockState(pipe);
        if (!(state.getBlock() instanceof WarpPipeBlock) || state.getBlock() instanceof ClearWarpPipeBlock
                || !state.getValue(WarpPipeBlock.ENTRANCE) || state.getValue(WarpPipeBlock.CLOSED)
                || state.getValue(WarpPipeBlock.FACING) != requiredFacing)
            return;

        BaseWarpBlockEntity warpBE = WarpLinkPathing.getUsableWarpBlockEntity(level, pipe);
        if (warpBE == null)
            return;

        BlockState destinationState = level.getBlockState(warpBE.destinationPos);
        if (!(destinationState.getBlock() instanceof WarpPipeBlock) || !destinationState.getValue(WarpPipeBlock.ENTRANCE))
            return;

        links.add(new WarpLink(trigger.immutable(),
                warpBE.destinationPos.relative(destinationState.getValue(WarpPipeBlock.FACING)).immutable(), jump, null));
    }


    @Nullable
    private static BaseWarpBlockEntity getUsableWarpBlockEntity(Level level, BlockPos pos) {
        if (!(level.getBlockEntity(pos) instanceof BaseWarpBlockEntity warpBE) || warpBE.preventWarp
                || !warpBE.hasDestinationPos() || warpBE.destinationPos == null || !level.isLoaded(warpBE.destinationPos)
                || !(level.getBlockEntity(warpBE.destinationPos) instanceof BaseWarpBlockEntity))
            return null;
        return warpBE;
    }
}
