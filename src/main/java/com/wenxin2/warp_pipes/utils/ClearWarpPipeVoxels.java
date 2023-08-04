package com.wenxin2.warp_pipes.utils;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ClearWarpPipeVoxels {
    public static final VoxelShape PIPE_ENTRANCE = Shapes.or(
            Block.box(0, 0, 0, 16, 15.98, 3),
            Block.box(0, 0, 13, 16, 15.98, 16),
            Block.box(13, 0, 0, 16, 15.98, 16),
            Block.box(0, 0, 0, 3, 15.98, 16),
            Block.box(0, 0, 0, 16, 3, 16)).optimize();

    public static final VoxelShape PIPE_ENTRANCE_CLOSED = Shapes.or(
            Block.box(0, 13, 0, 16, 16, 16),
            Block.box(0, 0, 0, 16, 16, 3),
            Block.box(0, 0, 13, 16, 16, 16),
            Block.box(13, 0, 0, 16, 16, 16),
            Block.box(0, 0, 0, 3, 16, 16),
            Block.box(0, 0, 0, 16, 3, 16)).optimize();

    public static final VoxelShape PIPE_ENTRANCE_N = Shapes.or(
            Block.box(0, 0, 13, 16, 15.98, 16),
            Block.box(13, 0, 0, 16, 15.98, 16),
            Block.box(0, 0, 0, 3, 15.98, 16),
            Block.box(0, 0, 0, 16, 3, 16)).optimize();

    public static final VoxelShape PIPE_ENTRANCE_S = VoxelShapeUtils.rotateShape(PIPE_ENTRANCE_N, Direction.NORTH, Direction.SOUTH);
    public static final VoxelShape PIPE_ENTRANCE_E = VoxelShapeUtils.rotateShape(PIPE_ENTRANCE_N, Direction.NORTH, Direction.EAST);
    public static final VoxelShape PIPE_ENTRANCE_W = VoxelShapeUtils.rotateShape(PIPE_ENTRANCE_N, Direction.NORTH, Direction.WEST);

    public static final VoxelShape PIPE_ENTRANCE_D = Shapes.or(
            Block.box(0, 0, 0, 16, 15.98, 3),
            Block.box(0, 0, 13, 16, 15.98, 16),
            Block.box(13, 0, 0, 16, 15.98, 16),
            Block.box(0, 0, 0, 3, 15.98, 16)).optimize();



    public static final VoxelShape PIPE_ENTRANCE_NE = Shapes.or(
            Block.box(0, 0, 13, 16, 15.98, 16),
            Block.box(0, 0, 0, 3, 15.98, 16),
            Block.box(0, 0, 0, 16, 3, 16)).optimize();

    public static final VoxelShape PIPE_ENTRANCE_NW = VoxelShapeUtils.rotateShape(PIPE_ENTRANCE_NE, Direction.NORTH, Direction.WEST);
    public static final VoxelShape PIPE_ENTRANCE_SE = VoxelShapeUtils.rotateShape(PIPE_ENTRANCE_NE, Direction.NORTH, Direction.EAST);
    public static final VoxelShape PIPE_ENTRANCE_SW = VoxelShapeUtils.rotateShape(PIPE_ENTRANCE_NE, Direction.NORTH, Direction.SOUTH);

    public static final VoxelShape PIPE_ENTRANCE_ND = Shapes.or(
            Block.box(0, 0, 13, 16, 15.98, 16),
            Block.box(13, 0, 0, 16, 15.98, 16),
            Block.box(0, 0, 0, 3, 15.98, 16)).optimize();

    public static final VoxelShape PIPE_ENTRANCE_SD = VoxelShapeUtils.rotateShape(PIPE_ENTRANCE_ND, Direction.NORTH, Direction.SOUTH);
    public static final VoxelShape PIPE_ENTRANCE_ED = VoxelShapeUtils.rotateShape(PIPE_ENTRANCE_ND, Direction.NORTH, Direction.EAST);
    public static final VoxelShape PIPE_ENTRANCE_WD = VoxelShapeUtils.rotateShape(PIPE_ENTRANCE_ND, Direction.NORTH, Direction.WEST);

    public static final VoxelShape PIPE_ENTRANCE_NS = Shapes.or(
            Block.box(13, 0, 0, 16, 15.98, 16),
            Block.box(0, 0, 0, 3, 15.98, 16),
            Block.box(0, 0, 0, 16, 3, 16)).optimize();

    public static final VoxelShape PIPE_ENTRANCE_EW = VoxelShapeUtils.rotateShape(PIPE_ENTRANCE_NS, Direction.NORTH, Direction.EAST);



    public static final VoxelShape PIPE_ENTRANCE_NSE = Shapes.or(
            Block.box(0, 0, 0, 3, 15.98, 16),
            Block.box(0, 0, 0, 16, 3, 16)).optimize();

    public static final VoxelShape PIPE_ENTRANCE_NSW = VoxelShapeUtils.rotateShape(PIPE_ENTRANCE_NSE, Direction.NORTH, Direction.SOUTH);
    public static final VoxelShape PIPE_ENTRANCE_NEW = VoxelShapeUtils.rotateShape(PIPE_ENTRANCE_NSE, Direction.NORTH, Direction.WEST);
    public static final VoxelShape PIPE_ENTRANCE_SEW = VoxelShapeUtils.rotateShape(PIPE_ENTRANCE_NSE, Direction.NORTH, Direction.EAST);



    public static final VoxelShape PIPE_ENTRANCE_NSEW = Shapes.or(
            Block.box(0, 0, 0, 16, 3, 16)).optimize();



    public static final VoxelShape PIPE_ENTRANCE_ZE_N = VoxelShapeUtils.rotateShapeAxis(PIPE_ENTRANCE_N, Direction.Axis.Z, 1);
    public static final VoxelShape PIPE_ENTRANCE_ZE_S = VoxelShapeUtils.rotateShapeAxis(PIPE_ENTRANCE_S, Direction.Axis.Z, 1);
    public static final VoxelShape PIPE_ENTRANCE_ZW_N = VoxelShapeUtils.rotateShapeAxis(PIPE_ENTRANCE_N, Direction.Axis.Z, 3);
    public static final VoxelShape PIPE_ENTRANCE_ZW_S = VoxelShapeUtils.rotateShapeAxis(PIPE_ENTRANCE_S, Direction.Axis.Z, 3);


    public static final VoxelShape PIPE_ENTRANCE_XN_E = VoxelShapeUtils.rotateShapeAxis(PIPE_ENTRANCE_ZE_N, Direction.Axis.X, 1);
    public static final VoxelShape PIPE_ENTRANCE_XN_W = VoxelShapeUtils.rotateShapeAxis(PIPE_ENTRANCE_ZW_N, Direction.Axis.X, 1);
    public static final VoxelShape PIPE_ENTRANCE_XS_E = VoxelShapeUtils.rotateShapeAxis(PIPE_ENTRANCE_ZE_S, Direction.Axis.X, 3);
    public static final VoxelShape PIPE_ENTRANCE_XS_W = VoxelShapeUtils.rotateShapeAxis(PIPE_ENTRANCE_ZW_S, Direction.Axis.X, 3);
}
