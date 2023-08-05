package com.wenxin2.warp_pipes.utils;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ClearWarpPipeVoxels {
    public static final VoxelShape PIPE_CLOSED =
            Block.box(0, 13, 0, 16, 16, 16);

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

    public static final VoxelShape PIPE_ENTRANCE_NED = Shapes.or(
            Block.box(0, 0, 13, 16, 15.98, 16),
            Block.box(0, 0, 0, 3, 15.98, 16)).optimize();

    public static final VoxelShape PIPE_ENTRANCE_NWD = VoxelShapeUtils.rotateShape(PIPE_ENTRANCE_NED, Direction.NORTH, Direction.WEST);
    public static final VoxelShape PIPE_ENTRANCE_SED = VoxelShapeUtils.rotateShape(PIPE_ENTRANCE_NED, Direction.NORTH, Direction.EAST);
    public static final VoxelShape PIPE_ENTRANCE_SWD = VoxelShapeUtils.rotateShape(PIPE_ENTRANCE_NED, Direction.NORTH, Direction.SOUTH);

    public static final VoxelShape PIPE_ENTRANCE_NSD = Shapes.or(
            Block.box(13, 0, 0, 16, 15.98, 16),
            Block.box(0, 0, 0, 3, 15.98, 16)).optimize();

    public static final VoxelShape PIPE_ENTRANCE_EWD = VoxelShapeUtils.rotateShape(PIPE_ENTRANCE_NS, Direction.NORTH, Direction.EAST);


    public static final VoxelShape PIPE_ENTRANCE_NSEW =
            Block.box(0, 0, 0, 16, 3, 16);
    public static final VoxelShape PIPE_ENTRANCE_NSED =
            Block.box(0, 0, 0, 3, 15.98, 16);

    public static final VoxelShape PIPE_ENTRANCE_NSWD = VoxelShapeUtils.rotateShape(PIPE_ENTRANCE_NSED, Direction.NORTH, Direction.SOUTH);
    public static final VoxelShape PIPE_ENTRANCE_NEWD = VoxelShapeUtils.rotateShape(PIPE_ENTRANCE_NSED, Direction.NORTH, Direction.WEST);
    public static final VoxelShape PIPE_ENTRANCE_SEWD = VoxelShapeUtils.rotateShape(PIPE_ENTRANCE_NSED, Direction.NORTH, Direction.EAST);


    // Format: axis-facing-direction
    public static final VoxelShape PIPE_CLOSED_ZE = VoxelShapeUtils.rotateShapeAxis(PIPE_CLOSED, Direction.Axis.Z, 1);
    public static final VoxelShape PIPE_CLOSED_ZW = VoxelShapeUtils.rotateShapeAxis(PIPE_CLOSED, Direction.Axis.Z, 3);
    public static final VoxelShape PIPE_CLOSED_YN = VoxelShapeUtils.rotateShapeAxis(PIPE_CLOSED_ZW, Direction.Axis.Y, 1);
    public static final VoxelShape PIPE_CLOSED_YS = VoxelShapeUtils.rotateShapeAxis(PIPE_CLOSED_ZW, Direction.Axis.Y, 3);
    public static final VoxelShape PIPE_CLOSED_XD = VoxelShapeUtils.rotateShapeAxis(PIPE_CLOSED, Direction.Axis.X, 1);

    public static final VoxelShape PIPE_ENTRANCE_ZE = VoxelShapeUtils.rotateShapeAxis(PIPE_ENTRANCE, Direction.Axis.Z, 1);
    public static final VoxelShape PIPE_ENTRANCE_ZW = VoxelShapeUtils.rotateShapeAxis(PIPE_ENTRANCE, Direction.Axis.Z, 3);
    public static final VoxelShape PIPE_ENTRANCE_YN = VoxelShapeUtils.rotateShapeAxis(PIPE_ENTRANCE_ZW, Direction.Axis.Y, 1);
    public static final VoxelShape PIPE_ENTRANCE_YS = VoxelShapeUtils.rotateShapeAxis(PIPE_ENTRANCE_ZW, Direction.Axis.Y, 3);
    public static final VoxelShape PIPE_ENTRANCE_XD = VoxelShapeUtils.rotateShapeAxis(PIPE_ENTRANCE, Direction.Axis.X, 1);

    public static final VoxelShape PIPE_ENTRANCE_ZE_N = VoxelShapeUtils.rotateShapeAxis(PIPE_ENTRANCE_N, Direction.Axis.Z, 1);
    public static final VoxelShape PIPE_ENTRANCE_ZE_S = VoxelShapeUtils.rotateShapeAxis(PIPE_ENTRANCE_S, Direction.Axis.Z, 1);
    public static final VoxelShape PIPE_ENTRANCE_ZW_N = VoxelShapeUtils.rotateShapeAxis(PIPE_ENTRANCE_N, Direction.Axis.Z, 3);
    public static final VoxelShape PIPE_ENTRANCE_ZW_S = VoxelShapeUtils.rotateShapeAxis(PIPE_ENTRANCE_S, Direction.Axis.Z, 3);
    public static final VoxelShape PIPE_ENTRANCE_XD_N = VoxelShapeUtils.rotateShapeAxis(PIPE_ENTRANCE_N, Direction.Axis.X, 1);
    public static final VoxelShape PIPE_ENTRANCE_XD_S = VoxelShapeUtils.rotateShapeAxis(PIPE_ENTRANCE_S, Direction.Axis.X, 1);

    public static final VoxelShape PIPE_ENTRANCE_YN_E = VoxelShapeUtils.rotateShapeAxis(PIPE_ENTRANCE_ZE_S, Direction.Axis.Y, 3);
    public static final VoxelShape PIPE_ENTRANCE_YN_W = VoxelShapeUtils.rotateShapeAxis(PIPE_ENTRANCE_ZE_N, Direction.Axis.Y, 3);
    public static final VoxelShape PIPE_ENTRANCE_YS_E = VoxelShapeUtils.rotateShapeAxis(PIPE_ENTRANCE_ZE_N, Direction.Axis.Y, 1);
    public static final VoxelShape PIPE_ENTRANCE_YS_W = VoxelShapeUtils.rotateShapeAxis(PIPE_ENTRANCE_ZE_S, Direction.Axis.Y, 1);
    public static final VoxelShape PIPE_ENTRANCE_XD_E = VoxelShapeUtils.rotateShapeAxis(PIPE_ENTRANCE_E, Direction.Axis.X, 1);
    public static final VoxelShape PIPE_ENTRANCE_XD_W = VoxelShapeUtils.rotateShapeAxis(PIPE_ENTRANCE_W, Direction.Axis.X, 1);

    public static final VoxelShape PIPE_ENTRANCE_ZE_U = VoxelShapeUtils.rotateShapeAxis(PIPE_ENTRANCE_W, Direction.Axis.Z, 1);
    public static final VoxelShape PIPE_ENTRANCE_ZE_D = VoxelShapeUtils.rotateShapeAxis(PIPE_ENTRANCE_E, Direction.Axis.Z, 1);
    public static final VoxelShape PIPE_ENTRANCE_ZW_U = VoxelShapeUtils.rotateShapeAxis(PIPE_ENTRANCE_E, Direction.Axis.Z, 3);
    public static final VoxelShape PIPE_ENTRANCE_ZW_D = VoxelShapeUtils.rotateShapeAxis(PIPE_ENTRANCE_W, Direction.Axis.Z, 3);
    public static final VoxelShape PIPE_ENTRANCE_YN_U = VoxelShapeUtils.rotateShapeAxis(PIPE_ENTRANCE_ZE_U, Direction.Axis.Y, 3);
    public static final VoxelShape PIPE_ENTRANCE_YN_D = VoxelShapeUtils.rotateShapeAxis(PIPE_ENTRANCE_ZE_D, Direction.Axis.Y, 3);
    public static final VoxelShape PIPE_ENTRANCE_YS_U = VoxelShapeUtils.rotateShapeAxis(PIPE_ENTRANCE_ZE_U, Direction.Axis.Y, 1);
    public static final VoxelShape PIPE_ENTRANCE_YS_D = VoxelShapeUtils.rotateShapeAxis(PIPE_ENTRANCE_ZE_D, Direction.Axis.Y, 1);


    public static final VoxelShape PIPE_ENTRANCE_CLOSED_N = Shapes.or(PIPE_CLOSED, PIPE_ENTRANCE_N).optimize();
    public static final VoxelShape PIPE_ENTRANCE_CLOSED_S = Shapes.or(PIPE_CLOSED, PIPE_ENTRANCE_S).optimize();
    public static final VoxelShape PIPE_ENTRANCE_CLOSED_E = Shapes.or(PIPE_CLOSED, PIPE_ENTRANCE_E).optimize();
    public static final VoxelShape PIPE_ENTRANCE_CLOSED_W = Shapes.or(PIPE_CLOSED, PIPE_ENTRANCE_W).optimize();
    public static final VoxelShape PIPE_ENTRANCE_CLOSED_D = Shapes.or(PIPE_CLOSED, PIPE_ENTRANCE_D).optimize();
    public static final VoxelShape PIPE_ENTRANCE_CLOSED_NE = Shapes.or(PIPE_CLOSED, PIPE_ENTRANCE_NE).optimize();
    public static final VoxelShape PIPE_ENTRANCE_CLOSED_NW = Shapes.or(PIPE_CLOSED, PIPE_ENTRANCE_NW).optimize();
    public static final VoxelShape PIPE_ENTRANCE_CLOSED_SE = Shapes.or(PIPE_CLOSED, PIPE_ENTRANCE_SE).optimize();
    public static final VoxelShape PIPE_ENTRANCE_CLOSED_SW = Shapes.or(PIPE_CLOSED, PIPE_ENTRANCE_SW).optimize();
    public static final VoxelShape PIPE_ENTRANCE_CLOSED_ND = Shapes.or(PIPE_CLOSED, PIPE_ENTRANCE_ND).optimize();
    public static final VoxelShape PIPE_ENTRANCE_CLOSED_SD = Shapes.or(PIPE_CLOSED, PIPE_ENTRANCE_SD).optimize();
    public static final VoxelShape PIPE_ENTRANCE_CLOSED_ED = Shapes.or(PIPE_CLOSED, PIPE_ENTRANCE_ED).optimize();
    public static final VoxelShape PIPE_ENTRANCE_CLOSED_WD = Shapes.or(PIPE_CLOSED, PIPE_ENTRANCE_WD).optimize();
    public static final VoxelShape PIPE_ENTRANCE_CLOSED_NS = Shapes.or(PIPE_CLOSED, PIPE_ENTRANCE_NS).optimize();
    public static final VoxelShape PIPE_ENTRANCE_CLOSED_EW = Shapes.or(PIPE_CLOSED, PIPE_ENTRANCE_EW).optimize();
    public static final VoxelShape PIPE_ENTRANCE_CLOSED_NSE = Shapes.or(PIPE_CLOSED, PIPE_ENTRANCE_NSE).optimize();
    public static final VoxelShape PIPE_ENTRANCE_CLOSED_NSW = Shapes.or(PIPE_CLOSED, PIPE_ENTRANCE_NSW).optimize();
    public static final VoxelShape PIPE_ENTRANCE_CLOSED_NEW = Shapes.or(PIPE_CLOSED, PIPE_ENTRANCE_NEW).optimize();
    public static final VoxelShape PIPE_ENTRANCE_CLOSED_SEW = Shapes.or(PIPE_CLOSED, PIPE_ENTRANCE_SEW).optimize();
    public static final VoxelShape PIPE_ENTRANCE_CLOSED_NED = Shapes.or(PIPE_CLOSED, PIPE_ENTRANCE_NED).optimize();
    public static final VoxelShape PIPE_ENTRANCE_CLOSED_NWD = Shapes.or(PIPE_CLOSED, PIPE_ENTRANCE_NWD).optimize();
    public static final VoxelShape PIPE_ENTRANCE_CLOSED_SED = Shapes.or(PIPE_CLOSED, PIPE_ENTRANCE_SED).optimize();
    public static final VoxelShape PIPE_ENTRANCE_CLOSED_SWD = Shapes.or(PIPE_CLOSED, PIPE_ENTRANCE_SWD).optimize();
    public static final VoxelShape PIPE_ENTRANCE_CLOSED_NSD = Shapes.or(PIPE_CLOSED, PIPE_ENTRANCE_NSD).optimize();
    public static final VoxelShape PIPE_ENTRANCE_CLOSED_EWD = Shapes.or(PIPE_CLOSED, PIPE_ENTRANCE_EWD).optimize();
    public static final VoxelShape PIPE_ENTRANCE_CLOSED_NSEW = Shapes.or(PIPE_CLOSED, PIPE_ENTRANCE_NSEW).optimize();
    public static final VoxelShape PIPE_ENTRANCE_CLOSED_NSED = Shapes.or(PIPE_CLOSED, PIPE_ENTRANCE_NSED).optimize();
    public static final VoxelShape PIPE_ENTRANCE_CLOSED_NSWD = Shapes.or(PIPE_CLOSED, PIPE_ENTRANCE_NSWD).optimize();
    public static final VoxelShape PIPE_ENTRANCE_CLOSED_NEWD = Shapes.or(PIPE_CLOSED, PIPE_ENTRANCE_NEWD).optimize();
    public static final VoxelShape PIPE_ENTRANCE_CLOSED_SEWD = Shapes.or(PIPE_CLOSED, PIPE_ENTRANCE_SEWD).optimize();


    public static final VoxelShape PIPE_ENTRANCE_CLOSED_ZE = Shapes.or(PIPE_CLOSED_ZE, PIPE_ENTRANCE_ZE).optimize();
    public static final VoxelShape PIPE_ENTRANCE_CLOSED_ZW = Shapes.or(PIPE_CLOSED_ZW, PIPE_ENTRANCE_ZW).optimize();

    public static final VoxelShape PIPE_ENTRANCE_CLOSED_XN = Shapes.or(PIPE_CLOSED_YN, PIPE_ENTRANCE_YN).optimize();
    public static final VoxelShape PIPE_ENTRANCE_CLOSED_XS = Shapes.or(PIPE_CLOSED_YS, PIPE_ENTRANCE_YS).optimize();

    public static final VoxelShape PIPE_ENTRANCE_CLOSED_XD = Shapes.or(PIPE_CLOSED_XD, PIPE_ENTRANCE_XD).optimize();

    public static final VoxelShape PIPE_ENTRANCE_CLOSED_ZE_N = Shapes.or(PIPE_CLOSED_ZE, PIPE_ENTRANCE_ZE_N).optimize();
    public static final VoxelShape PIPE_ENTRANCE_CLOSED_ZE_S = Shapes.or(PIPE_CLOSED_ZE, PIPE_ENTRANCE_ZE_S).optimize();
    public static final VoxelShape PIPE_ENTRANCE_CLOSED_ZW_N = Shapes.or(PIPE_CLOSED_ZW, PIPE_ENTRANCE_ZW_N).optimize();
    public static final VoxelShape PIPE_ENTRANCE_CLOSED_ZW_S = Shapes.or(PIPE_CLOSED_ZW, PIPE_ENTRANCE_ZW_S).optimize();
    public static final VoxelShape PIPE_ENTRANCE_CLOSED_XD_N = Shapes.or(PIPE_CLOSED_XD, PIPE_ENTRANCE_XD_N).optimize();
    public static final VoxelShape PIPE_ENTRANCE_CLOSED_XD_S = Shapes.or(PIPE_CLOSED_XD, PIPE_ENTRANCE_XD_S).optimize();

    public static final VoxelShape PIPE_ENTRANCE_CLOSED_YN_E = Shapes.or(PIPE_CLOSED_YN, PIPE_ENTRANCE_YN_E).optimize();
    public static final VoxelShape PIPE_ENTRANCE_CLOSED_YN_W = Shapes.or(PIPE_CLOSED_YN, PIPE_ENTRANCE_YN_W).optimize();
    public static final VoxelShape PIPE_ENTRANCE_CLOSED_YS_E = Shapes.or(PIPE_CLOSED_YS, PIPE_ENTRANCE_YS_E).optimize();
    public static final VoxelShape PIPE_ENTRANCE_CLOSED_YS_W = Shapes.or(PIPE_CLOSED_YS, PIPE_ENTRANCE_YS_W).optimize();
    public static final VoxelShape PIPE_ENTRANCE_CLOSED_XD_E = Shapes.or(PIPE_CLOSED_XD, PIPE_ENTRANCE_XD_E).optimize();
    public static final VoxelShape PIPE_ENTRANCE_CLOSED_XD_W = Shapes.or(PIPE_CLOSED_XD, PIPE_ENTRANCE_XD_W).optimize();

    public static final VoxelShape PIPE_ENTRANCE_CLOSED_ZE_U = Shapes.or(PIPE_CLOSED_ZE, PIPE_ENTRANCE_ZE_U).optimize();
    public static final VoxelShape PIPE_ENTRANCE_CLOSED_ZE_D = Shapes.or(PIPE_CLOSED_ZE, PIPE_ENTRANCE_ZE_D).optimize();
    public static final VoxelShape PIPE_ENTRANCE_CLOSED_ZW_U = Shapes.or(PIPE_CLOSED_ZW, PIPE_ENTRANCE_ZW_U).optimize();
    public static final VoxelShape PIPE_ENTRANCE_CLOSED_ZW_D = Shapes.or(PIPE_CLOSED_ZW, PIPE_ENTRANCE_ZW_D).optimize();
    public static final VoxelShape PIPE_ENTRANCE_CLOSED_YN_U = Shapes.or(PIPE_CLOSED_YN, PIPE_ENTRANCE_YN_U).optimize();
    public static final VoxelShape PIPE_ENTRANCE_CLOSED_YN_D = Shapes.or(PIPE_CLOSED_YN, PIPE_ENTRANCE_YN_D).optimize();
    public static final VoxelShape PIPE_ENTRANCE_CLOSED_YS_U = Shapes.or(PIPE_CLOSED_YS, PIPE_ENTRANCE_YS_U).optimize();
    public static final VoxelShape PIPE_ENTRANCE_CLOSED_YS_D = Shapes.or(PIPE_CLOSED_YS, PIPE_ENTRANCE_YS_D).optimize();
}
