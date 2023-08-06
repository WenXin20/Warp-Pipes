package com.wenxin2.warp_pipes.blocks;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.wenxin2.warp_pipes.blocks.entities.WarpPipeBlockEntity;
import java.util.Map;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ClearWarpPipeBlock extends WarpPipeBlock implements EntityBlock {
    public static final BooleanProperty ENTRANCE = BooleanProperty.create("entrance");
    public static final BooleanProperty CLOSED = BooleanProperty.create("closed");
    //    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final BooleanProperty UP = BlockStateProperties.UP;
    public static final BooleanProperty DOWN = BlockStateProperties.DOWN;
    public static final BooleanProperty NORTH = BlockStateProperties.NORTH;
    public static final BooleanProperty EAST = BlockStateProperties.EAST;
    public static final BooleanProperty SOUTH = BlockStateProperties.SOUTH;
    public static final BooleanProperty WEST = BlockStateProperties.WEST;
    public static final Map<Direction, BooleanProperty> PROPERTY_BY_DIRECTION = ImmutableMap.copyOf(Util.make(Maps.newEnumMap(Direction.class), (enumMap) -> {
        enumMap.put(Direction.UP, UP);
        enumMap.put(Direction.DOWN, DOWN);
        enumMap.put(Direction.NORTH, NORTH);
        enumMap.put(Direction.EAST, EAST);
        enumMap.put(Direction.SOUTH, SOUTH);
        enumMap.put(Direction.WEST, WEST);
    }));
    
    public static final VoxelShape PIPE_UP = Shapes.or(
            Block.box(0, 13, 0, 16, 16, 16)).optimize();
    public static final VoxelShape PIPE_NORTH = Shapes.or(
            Block.box(0, 0, 0, 16, 16, 3)).optimize();
    public static final VoxelShape PIPE_SOUTH = Shapes.or(
            Block.box(0, 0, 13, 16, 16, 16)).optimize();
    public static final VoxelShape PIPE_EAST = Shapes.or(
            Block.box(13, 0, 0, 16, 16, 16)).optimize();
    public static final VoxelShape PIPE_WEST = Shapes.or(
            Block.box(0, 0, 0, 3, 16, 16)).optimize();
    public static final VoxelShape PIPE_DOWN = Shapes.or(
            Block.box(0, 0, 0, 16, 3, 16)).optimize();

    public ClearWarpPipeBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.UP).setValue(ENTRANCE, Boolean.TRUE).setValue(CLOSED, Boolean.FALSE)
                .setValue(UP, Boolean.FALSE).setValue(NORTH, Boolean.FALSE).setValue(SOUTH, Boolean.FALSE)
                .setValue(EAST, Boolean.FALSE).setValue(WEST, Boolean.FALSE).setValue(DOWN, Boolean.FALSE));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateBuilder) {
        stateBuilder.add(CLOSED, ENTRANCE, FACING, UP, DOWN, NORTH, SOUTH, EAST, WEST);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter blockGetter, BlockPos pos, CollisionContext context) {// Start with a center post shape
        VoxelShape shape = Shapes.empty();

        // Combine shapes based on the directional block states
        if (!state.getValue(UP) && !(state.getValue(ENTRANCE) && (state.getValue(FACING) == Direction.UP))) {
            shape = Shapes.or(shape, PIPE_UP);
        }
        if (!state.getValue(DOWN) && !(state.getValue(ENTRANCE) && (state.getValue(FACING) == Direction.DOWN))) {
            shape = Shapes.or(shape, PIPE_DOWN);
        }
        if (!state.getValue(NORTH) && !(state.getValue(ENTRANCE) && (state.getValue(FACING) == Direction.NORTH))) {
            shape = Shapes.or(shape, PIPE_NORTH);
        }
        if (!state.getValue(EAST) && !(state.getValue(ENTRANCE) && (state.getValue(FACING) == Direction.EAST))) {
            shape = Shapes.or(shape, PIPE_EAST);
        }
        if (!state.getValue(SOUTH) && !(state.getValue(ENTRANCE) && (state.getValue(FACING) == Direction.SOUTH))) {
            shape = Shapes.or(shape, PIPE_SOUTH);
        }
        if (!state.getValue(WEST) && !(state.getValue(ENTRANCE) && (state.getValue(FACING) == Direction.WEST))) {
            shape = Shapes.or(shape, PIPE_WEST);
        }

        if (state.getValue(CLOSED) && state.getValue(FACING) == Direction.UP) {
            shape = Shapes.or(shape, PIPE_UP);
        }
        if (state.getValue(CLOSED) && state.getValue(FACING) == Direction.DOWN) {
            shape = Shapes.or(shape, PIPE_DOWN);
        }
        if (state.getValue(CLOSED) && state.getValue(FACING) == Direction.NORTH) {
            shape = Shapes.or(shape, PIPE_NORTH);
        }
        if (state.getValue(CLOSED) && state.getValue(FACING) == Direction.SOUTH) {
            shape = Shapes.or(shape, PIPE_SOUTH);
        }
        if (state.getValue(CLOSED) && state.getValue(FACING) == Direction.EAST) {
            shape = Shapes.or(shape, PIPE_EAST);
        }
        if (state.getValue(CLOSED) && state.getValue(FACING) == Direction.WEST) {
            shape = Shapes.or(shape, PIPE_WEST);
        }
        return shape.optimize();
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter blockGetter, BlockPos pos, CollisionContext collisionContext) {
        return this.getShape(state, blockGetter, pos, collisionContext);
    }

    @Override
    public VoxelShape getBlockSupportShape(BlockState state, BlockGetter blockGetter, BlockPos pos) {
        return this.getShape(state, blockGetter, pos, CollisionContext.empty());
    }

@Override
    public BlockEntity newBlockEntity(final BlockPos pos, final BlockState state)
    {
        return new WarpPipeBlockEntity(pos, state);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext placeContext) {
        FluidState fluidState = placeContext.getLevel().getFluidState(placeContext.getClickedPos());
        Direction direction = placeContext.getClickedFace();
        BlockGetter blockGetter = placeContext.getLevel();
        BlockPos pos = placeContext.getClickedPos();

        BlockPos posAbove = pos.above();
        BlockPos posBelow = pos.below();
        BlockPos posNorth = pos.north();
        BlockPos posSouth = pos.south();
        BlockPos posEast = pos.east();
        BlockPos posWest = pos.west();

        BlockState stateAbove = blockGetter.getBlockState(posAbove);
        BlockState stateBelow = blockGetter.getBlockState(posBelow);
        BlockState stateNorth = blockGetter.getBlockState(posNorth);
        BlockState stateSouth = blockGetter.getBlockState(posSouth);
        BlockState stateEast = blockGetter.getBlockState(posEast);
        BlockState stateWest = blockGetter.getBlockState(posWest);

        return this.defaultBlockState().setValue(FACING, direction)
                .setValue(UP, this.connectsTo(stateAbove))
                .setValue(DOWN, this.connectsTo(stateBelow))
                .setValue(NORTH, this.connectsTo(stateNorth))
                .setValue(SOUTH, this.connectsTo(stateSouth))
                .setValue(EAST, this.connectsTo(stateEast))
                .setValue(WEST, this.connectsTo(stateWest))
                .setValue(CLOSED, placeContext.getLevel().hasNeighborSignal(placeContext.getClickedPos()));
    }

    public boolean connectsTo(BlockState neighborState) {
        return neighborState.getBlock() instanceof ClearWarpPipeBlock;
    }

    public boolean connectsToEntrance(BlockState neighborState, Direction facing) {
        return neighborState.getBlock() instanceof ClearWarpPipeBlock && neighborState.getValue(FACING) == facing;
    }

    @Override
    public float getShadeBrightness(BlockState state, BlockGetter blockGetter, BlockPos pos) {
        return 1.0F;
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter blockGetter, BlockPos pos) {
        return true;
    }

    @Override
    public boolean skipRendering(BlockState state, BlockState neighborState, Direction direction) {
        if (state.getValue(ENTRANCE) && !state.getValue(CLOSED) && state.getValue(NORTH) && state.getValue(SOUTH)
                && state.getValue(EAST) && state.getValue(WEST) && state.getValue(DOWN)) {
            return true;
        } if (state.getValue(CLOSED) && (state.getValue(NORTH) || state.getValue(SOUTH) || state.getValue(EAST) || state.getValue(WEST))) {
            return neighborState.is(this) && neighborState.getValue(CLOSED) || super.skipRendering(state, neighborState, direction);
        } else return state.getValue(UP) && state.getValue(NORTH) && state.getValue(SOUTH) && state.getValue(EAST)
                && state.getValue(WEST) && state.getValue(DOWN);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor worldAccessor, BlockPos pos, BlockPos pos2) {
        Block blockAbove = worldAccessor.getBlockState(pos.above()).getBlock();
        Block blockBelow = worldAccessor.getBlockState(pos.below()).getBlock();
        Block blockNorth = worldAccessor.getBlockState(pos.north()).getBlock();
        Block blockSouth = worldAccessor.getBlockState(pos.south()).getBlock();
        Block blockEast = worldAccessor.getBlockState(pos.east()).getBlock();
        Block blockWest = worldAccessor.getBlockState(pos.west()).getBlock();

        boolean facingUp = state.getValue(FACING) == Direction.UP;
        boolean facingDown = state.getValue(FACING) == Direction.DOWN;
        boolean facingNorth = state.getValue(FACING) == Direction.NORTH;
        boolean facingSouth = state.getValue(FACING) == Direction.SOUTH;
        boolean facingEast = state.getValue(FACING) == Direction.EAST;
        boolean facingWest = state.getValue(FACING) == Direction.WEST;

        if (facingUp) {
            if (blockAbove instanceof WarpPipeBlock) {
                return state.setValue(ENTRANCE, Boolean.FALSE).setValue(PROPERTY_BY_DIRECTION.get(direction), this.connectsTo(neighborState));
            }
            return state.setValue(ENTRANCE, Boolean.TRUE).setValue(PROPERTY_BY_DIRECTION.get(direction), this.connectsTo(state) && (neighborState.getValue(FACING) == Direction.UP));
        }

        if (facingDown) {
            if (blockBelow instanceof WarpPipeBlock) {
                return state.setValue(ENTRANCE, Boolean.FALSE).setValue(PROPERTY_BY_DIRECTION.get(direction), this.connectsTo(neighborState));
            }
            return state.setValue(ENTRANCE, Boolean.TRUE).setValue(PROPERTY_BY_DIRECTION.get(direction), this.connectsTo(neighborState) && (neighborState.getValue(FACING) == Direction.DOWN));
        }

        if (facingNorth) {
            if (blockNorth instanceof WarpPipeBlock) {
                return state.setValue(ENTRANCE, Boolean.FALSE).setValue(PROPERTY_BY_DIRECTION.get(direction), this.connectsTo(neighborState));
            }
            return state.setValue(ENTRANCE, Boolean.TRUE).setValue(PROPERTY_BY_DIRECTION.get(direction), this.connectsTo(neighborState) && (neighborState.getValue(FACING) == Direction.NORTH));
        }

        if (facingSouth) {
            if (blockSouth instanceof WarpPipeBlock) {
                return state.setValue(ENTRANCE, Boolean.FALSE).setValue(PROPERTY_BY_DIRECTION.get(direction), this.connectsTo(neighborState));
            }
            return state.setValue(ENTRANCE, Boolean.TRUE).setValue(PROPERTY_BY_DIRECTION.get(direction), this.connectsTo(neighborState) && (neighborState.getValue(FACING) == Direction.EAST));
        }

        if (facingEast) {
            if (blockEast instanceof WarpPipeBlock) {
                return state.setValue(ENTRANCE, Boolean.FALSE).setValue(PROPERTY_BY_DIRECTION.get(direction), this.connectsTo(neighborState));
            }
            return state.setValue(ENTRANCE, Boolean.TRUE).setValue(PROPERTY_BY_DIRECTION.get(direction), this.connectsTo(neighborState) && (neighborState.getValue(FACING) == Direction.SOUTH));
        }

        if (facingWest) {
            if (blockWest instanceof WarpPipeBlock) {
                return state.setValue(ENTRANCE, Boolean.FALSE).setValue(PROPERTY_BY_DIRECTION.get(direction), this.connectsTo(neighborState));
            }
            return state.setValue(ENTRANCE, Boolean.TRUE).setValue(PROPERTY_BY_DIRECTION.get(direction), this.connectsTo(neighborState) && (neighborState.getValue(FACING) == Direction.WEST));
        }
        return state.setValue(ENTRANCE, Boolean.TRUE).setValue(PROPERTY_BY_DIRECTION.get(direction), this.connectsTo(neighborState) && (neighborState.getValue(FACING) == Direction.UP));
    }
}
