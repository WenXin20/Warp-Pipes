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
import net.minecraft.world.level.block.Blocks;
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

    public static final VoxelShape PIPE_ENTRANCE = Shapes.or(
            Block.box(0, 0, 0, 16, 15.98, 3),
            Block.box(0, 0, 13, 16, 15.98, 16),
            Block.box(13, 0, 0, 16, 15.98, 16),
            Block.box(0, 0, 0, 3, 15.98, 16),
            Block.box(0, 0, 0, 16, 3, 16)).optimize();

    public static final VoxelShape PIPE_ENTRANCE_N = Shapes.or(
            Block.box(0, 0, 13, 16, 15.98, 16),
            Block.box(13, 0, 0, 16, 15.98, 16),
            Block.box(0, 0, 0, 3, 15.98, 16),
            Block.box(0, 0, 0, 16, 3, 16)).optimize();

    public static final VoxelShape PIPE_ENTRANCE_S = rotateShape(PIPE_ENTRANCE_N, Direction.NORTH, Direction.SOUTH);
    public static final VoxelShape PIPE_ENTRANCE_E = rotateShape(PIPE_ENTRANCE_N, Direction.NORTH, Direction.EAST);
    public static final VoxelShape PIPE_ENTRANCE_W = rotateShape(PIPE_ENTRANCE_N, Direction.NORTH, Direction.WEST);

    public static final VoxelShape PIPE_ENTRANCE_NS = Shapes.or(
            Block.box(13, 0, 0, 16, 15.98, 16),
            Block.box(0, 0, 0, 3, 15.98, 16),
            Block.box(0, 0, 0, 16, 3, 16)).optimize();

    public static final VoxelShape PIPE_ENTRANCE_EW = rotateShape(PIPE_ENTRANCE_NS, Direction.NORTH, Direction.EAST);

    public static final VoxelShape PIPE_ENTRANCE_NSE = Shapes.or(
            Block.box(0, 0, 0, 3, 15.98, 16),
            Block.box(0, 0, 0, 16, 3, 16)).optimize();

    public static final VoxelShape PIPE_ENTRANCE_NSW = rotateShape(PIPE_ENTRANCE_NSE, Direction.NORTH, Direction.SOUTH);
    public static final VoxelShape PIPE_ENTRANCE_NEW = rotateShape(PIPE_ENTRANCE_NSE, Direction.NORTH, Direction.WEST);
    public static final VoxelShape PIPE_ENTRANCE_SEW = rotateShape(PIPE_ENTRANCE_NSE, Direction.NORTH, Direction.EAST);

    public static final VoxelShape PIPE_ENTRANCE_NSEW = Shapes.or(
            Block.box(0, 0, 0, 16, 3, 16)).optimize();

    public static final VoxelShape PIPE_ENTRANCE_ZE_N = rotateShapeAxis(PIPE_ENTRANCE_N, Direction.Axis.Z, 1);
    public static final VoxelShape PIPE_ENTRANCE_ZE_S = rotateShapeAxis(PIPE_ENTRANCE_S, Direction.Axis.Z, 1);
    public static final VoxelShape PIPE_ENTRANCE_ZW_N = rotateShapeAxis(PIPE_ENTRANCE_N, Direction.Axis.Z, 3);
    public static final VoxelShape PIPE_ENTRANCE_ZW_S = rotateShapeAxis(PIPE_ENTRANCE_S, Direction.Axis.Z, 3);
    public static final VoxelShape PIPE_ENTRANCE_XN_E = rotateShapeAxis(PIPE_ENTRANCE_ZE_N, Direction.Axis.X, 1);
    public static final VoxelShape PIPE_ENTRANCE_XN_W = rotateShapeAxis(PIPE_ENTRANCE_ZW_N, Direction.Axis.X, 1);
    public static final VoxelShape PIPE_ENTRANCE_XS_E = rotateShapeAxis(PIPE_ENTRANCE_ZE_S, Direction.Axis.X, 3);
    public static final VoxelShape PIPE_ENTRANCE_XS_W = rotateShapeAxis(PIPE_ENTRANCE_ZW_S, Direction.Axis.X, 3);

    public static final VoxelShape PIPE_ENTRANCE_D = Shapes.or(
            Block.box(0, 0, 0, 16, 15.98, 3),
            Block.box(0, 0, 13, 16, 15.98, 16),
            Block.box(13, 0, 0, 16, 15.98, 16),
            Block.box(0, 0, 0, 3, 15.98, 16)).optimize();

    public static final VoxelShape PIPE_ENTRANCE_NE = Shapes.or(
            Block.box(0, 0, 13, 16, 15.98, 16),
            Block.box(0, 0, 0, 3, 15.98, 16),
            Block.box(0, 0, 0, 16, 3, 16)).optimize();

    public static final VoxelShape PIPE_ENTRANCE_NW = rotateShape(PIPE_ENTRANCE_NE, Direction.NORTH, Direction.WEST);
    public static final VoxelShape PIPE_ENTRANCE_SE = rotateShape(PIPE_ENTRANCE_NE, Direction.NORTH, Direction.EAST);
    public static final VoxelShape PIPE_ENTRANCE_SW = rotateShape(PIPE_ENTRANCE_NE, Direction.NORTH, Direction.SOUTH);

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
    public VoxelShape getShape(BlockState state, BlockGetter blockGetter, BlockPos pos, CollisionContext context) {
        if (state.getValue(ENTRANCE)) {
            if (state.getValue(FACING) == Direction.UP) {
                if (state.getValue(NORTH)) {
                    if (state.getValue(SOUTH)) {
                        if (state.getValue(EAST)) {
                            if (state.getValue(WEST)) {
                                if (state.getValue(CLOSED)) {
                                    return PIPE_ENTRANCE_NSEW;
                                } else {
                                    return PIPE_ENTRANCE_NSEW;
                                }
                            }
                            if (state.getValue(CLOSED)) {
                                return PIPE_ENTRANCE_NSE;
                            } else {
                                return PIPE_ENTRANCE_NSE;
                            }
                        }
                        if (state.getValue(WEST)) {
                            if (state.getValue(CLOSED)) {
                                return PIPE_ENTRANCE_NSW;
                            } else {
                                return PIPE_ENTRANCE_NSW;
                            }
                        }
                        if (state.getValue(CLOSED)) {
                            return PIPE_ENTRANCE_NS;
                        } else {
                            return PIPE_ENTRANCE_NS;
                        }
                    }
                    if (state.getValue(EAST)) {
                        if (state.getValue(WEST)) {
                            if (state.getValue(CLOSED)) {
                                return PIPE_ENTRANCE_NEW;
                            } else {
                                return PIPE_ENTRANCE_NEW;
                            }
                        }
                        if (state.getValue(CLOSED)) {
                            return PIPE_ENTRANCE_NE;
                        } else {
                            return PIPE_ENTRANCE_NE;
                        }
                    }
                    if (state.getValue(WEST)) {
                        if (state.getValue(CLOSED)) {
                            return PIPE_ENTRANCE_NW;
                        } else {
                            return PIPE_ENTRANCE_NW;
                        }
                    }
                    if (state.getValue(CLOSED)) {
                        return PIPE_ENTRANCE_N;
                    } else {
                        return PIPE_ENTRANCE_N;
                    }
                }
                if (state.getValue(SOUTH)) {
                    if (state.getValue(EAST)) {
                        if (state.getValue(WEST)) {
                            if (state.getValue(CLOSED)) {
                                return PIPE_ENTRANCE_SEW;
                            } else {
                                return PIPE_ENTRANCE_SEW;
                            }
                        }
                        if (state.getValue(CLOSED)) {
                            return PIPE_ENTRANCE_SE;
                        } else {
                            return PIPE_ENTRANCE_SE;
                        }
                    }
                    if (state.getValue(WEST)) {
                        if (state.getValue(CLOSED)) {
                            return PIPE_ENTRANCE_SW;
                        } else {
                            return PIPE_ENTRANCE_SW;
                        }
                    }
                    if (state.getValue(CLOSED)) {
                        return PIPE_ENTRANCE_S;
                    } else {
                        return PIPE_ENTRANCE_S;
                    }
                }
                if (state.getValue(EAST)) {
                    if (state.getValue(WEST)) {
                        if (state.getValue(CLOSED)) {
                            return PIPE_ENTRANCE_EW;
                        } else {
                            return PIPE_ENTRANCE_EW;
                        }
                    }
                    if (state.getValue(CLOSED)) {
                        return PIPE_ENTRANCE_E;
                    } else {
                        return PIPE_ENTRANCE_E;
                    }
                }
                if (state.getValue(WEST)) {
                    if (state.getValue(CLOSED)) {
                        return PIPE_ENTRANCE_W;
                    } else {
                        return PIPE_ENTRANCE_W;
                    }
                }
                if (state.getValue(DOWN)) {
                    if (state.getValue(CLOSED)) {
                        return PIPE_ENTRANCE_D;
                    } else {
                        return PIPE_ENTRANCE_D;
                    }
                }
                return PIPE_ENTRANCE;
            }
            if (state.getValue(FACING) == Direction.NORTH) {
                if (state.getValue(EAST)) {
                    if (state.getValue(CLOSED)) {
                        return PIPE_ENTRANCE_XN_E;
                    } else {
                        return PIPE_ENTRANCE_XN_E;
                    }
                }
                if (state.getValue(WEST)) {
                    if (state.getValue(CLOSED)) {
                        return PIPE_ENTRANCE_XN_W;
                    } else {
                        return PIPE_ENTRANCE_XN_W;
                    }
                }
            }
            if (state.getValue(FACING) == Direction.SOUTH) {
                if (state.getValue(EAST)) {
                    if (state.getValue(CLOSED)) {
                        return PIPE_ENTRANCE_XS_E;
                    } else {
                        return PIPE_ENTRANCE_XS_E;
                    }
                }
                if (state.getValue(WEST)) {
                    if (state.getValue(CLOSED)) {
                        return PIPE_ENTRANCE_XS_W;
                    } else {
                        return PIPE_ENTRANCE_XS_W;
                    }
                }
            }
            if (state.getValue(FACING) == Direction.EAST) {
                if (state.getValue(NORTH)) {
                    if (state.getValue(CLOSED)) {
                        return PIPE_ENTRANCE_ZE_N;
                    } else {
                        return PIPE_ENTRANCE_ZE_N;
                    }
                }
                if (state.getValue(SOUTH)) {
                    if (state.getValue(CLOSED)) {
                        return PIPE_ENTRANCE_ZE_S;
                    } else {
                        return PIPE_ENTRANCE_ZE_S;
                    }
                }
            }
            if (state.getValue(FACING) == Direction.WEST) {
                if (state.getValue(NORTH)) {
                    if (state.getValue(CLOSED)) {
                        return PIPE_ENTRANCE_ZW_N;
                    } else {
                        return PIPE_ENTRANCE_ZW_N;
                    }
                }
                if (state.getValue(SOUTH)) {
                    if (state.getValue(CLOSED)) {
                        return PIPE_ENTRANCE_ZW_S;
                    } else {
                        return PIPE_ENTRANCE_ZW_S;
                    }
                }
            }
            return PIPE_ENTRANCE;
        }
        return Shapes.block();
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter blockGetter, BlockPos pos, CollisionContext collisionContext) {
        return this.getShape(state, blockGetter, pos, collisionContext);
    }

    @Override
    public VoxelShape getBlockSupportShape(BlockState state, BlockGetter blockGetter, BlockPos pos) {
        return this.getShape(state, blockGetter, pos, CollisionContext.empty());
    }

    public static VoxelShape rotateShape(VoxelShape shape, Direction fromDirection, Direction toDirection) {
        VoxelShape[] buffer = new VoxelShape[]{ shape, Shapes.empty() };

        // Calculate the number of 90-degree rotations needed around the specified axis
        int times = (toDirection.get2DDataValue() - fromDirection.get2DDataValue() + 4) % 4;

        for (int i = 0; i < times; i++) {
            buffer[0].forAllBoxes((minX, minY, minZ, maxX, maxY, maxZ)
                    -> buffer[1] = Shapes.or(buffer[1], Shapes.create(1-maxZ, minY, minX, 1-minZ, maxY, maxX)));
            buffer[0] = buffer[1];
            buffer[1] = Shapes.empty();
        }

        return buffer[0];
    }

    public static VoxelShape rotateShapeAxis(VoxelShape shape, Direction.Axis rotationAxis, int numRotations) {
        VoxelShape[] buffer = new VoxelShape[]{shape, Shapes.empty()};

        // Calculate the number of 90-degree rotations needed around the specified axis
        int times = numRotations % 4;
        if (times < 0) times += 4; // Ensure positive value for times

        for (int i = 0; i < times; i++) {
            buffer[0].forAllBoxes((minX, minY, minZ, maxX, maxY, maxZ) -> {
                // Rotate the VoxelShape around the specified axis
                if (rotationAxis == Direction.Axis.X) {
                    // Rotate around the X-axis
                    buffer[1] = Shapes.or(buffer[1], Shapes.create(minX, 1 - maxY, minZ, maxX, 1 - minY, maxZ));
                } else if (rotationAxis == Direction.Axis.Y) {
                    // Rotate around the Y-axis
                    buffer[1] = Shapes.or(buffer[1], Shapes.create(1 - maxZ, minY, minX, 1 - minZ, maxY, maxX));
                } else if (rotationAxis == Direction.Axis.Z) {
                    // Rotate around the Z-axis
                    buffer[1] = Shapes.or(buffer[1], Shapes.create(minY, 1 - maxX, minZ, maxY, 1 - minX, maxZ));
                }
            });
            buffer[0] = buffer[1];
            buffer[1] = Shapes.empty();
        }

        return buffer[0];
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

    public boolean connectsTo(BlockState state) {
        Block block = state.getBlock();
        return block instanceof ClearWarpPipeBlock;
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
        return neighborState.is(this) ? true : super.skipRendering(state, neighborState, direction);
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

        if (facingUp && direction == Direction.UP && neighborState.is(Blocks.WATER) && !state.getValue(CLOSED)) {
            worldAccessor.scheduleTick(pos, this, 20);
        }

        if (facingUp) {
            if (blockAbove instanceof WarpPipeBlock) {
                return state.setValue(ENTRANCE, Boolean.FALSE).setValue(PROPERTY_BY_DIRECTION.get(direction), this.connectsTo(neighborState));
            }
            return state.setValue(ENTRANCE, Boolean.TRUE).setValue(PROPERTY_BY_DIRECTION.get(direction), this.connectsTo(neighborState));
        }

        if (facingDown) {
            if (blockBelow instanceof WarpPipeBlock) {
                return state.setValue(ENTRANCE, Boolean.FALSE).setValue(PROPERTY_BY_DIRECTION.get(direction), this.connectsTo(neighborState));
            }
            return state.setValue(ENTRANCE, Boolean.TRUE).setValue(PROPERTY_BY_DIRECTION.get(direction), this.connectsTo(neighborState));
        }

        if (facingNorth) {
            if (blockNorth instanceof WarpPipeBlock) {
                return state.setValue(ENTRANCE, Boolean.FALSE).setValue(PROPERTY_BY_DIRECTION.get(direction), this.connectsTo(neighborState));
            }
            return state.setValue(ENTRANCE, Boolean.TRUE).setValue(PROPERTY_BY_DIRECTION.get(direction), this.connectsTo(neighborState));
        }

        if (facingSouth) {
            if (blockSouth instanceof WarpPipeBlock) {
                return state.setValue(ENTRANCE, Boolean.FALSE).setValue(PROPERTY_BY_DIRECTION.get(direction), this.connectsTo(neighborState));
            }
            return state.setValue(ENTRANCE, Boolean.TRUE).setValue(PROPERTY_BY_DIRECTION.get(direction), this.connectsTo(neighborState));
        }

        if (facingEast) {
            if (blockEast instanceof WarpPipeBlock) {
                return state.setValue(ENTRANCE, Boolean.FALSE).setValue(PROPERTY_BY_DIRECTION.get(direction), this.connectsTo(neighborState));
            }
            return state.setValue(ENTRANCE, Boolean.TRUE).setValue(PROPERTY_BY_DIRECTION.get(direction), this.connectsTo(neighborState));
        }

        if (facingWest) {
            if (blockWest instanceof WarpPipeBlock) {
                return state.setValue(ENTRANCE, Boolean.FALSE).setValue(PROPERTY_BY_DIRECTION.get(direction), this.connectsTo(neighborState));
            }
        }
        return state.setValue(ENTRANCE, Boolean.TRUE).setValue(PROPERTY_BY_DIRECTION.get(direction), this.connectsTo(neighborState));
    }
}
