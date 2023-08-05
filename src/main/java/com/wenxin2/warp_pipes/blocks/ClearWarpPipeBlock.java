package com.wenxin2.warp_pipes.blocks;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.wenxin2.warp_pipes.blocks.entities.WarpPipeBlockEntity;
import com.wenxin2.warp_pipes.utils.ClearWarpPipeVoxels;
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
                                if (state.getValue(DOWN)) {
                                    if (state.getValue(CLOSED)) {
                                        return ClearWarpPipeVoxels.PIPE_CLOSED;
                                    } else {
                                        return Shapes.empty();
                                    }
                                }
                                if (state.getValue(CLOSED)) {
                                    return ClearWarpPipeVoxels.PIPE_ENTRANCE_CLOSED_NSEW;
                                } else {
                                    return ClearWarpPipeVoxels.PIPE_ENTRANCE_NSEW;
                                }
                            }
                            if (state.getValue(DOWN)) {
                                if (state.getValue(CLOSED)) {
                                    return ClearWarpPipeVoxels.PIPE_ENTRANCE_CLOSED_NSED;
                                } else {
                                    return ClearWarpPipeVoxels.PIPE_ENTRANCE_NSED;
                                }
                            }
                            if (state.getValue(CLOSED)) {
                                return ClearWarpPipeVoxels.PIPE_ENTRANCE_CLOSED_NSE;
                            } else {
                                return ClearWarpPipeVoxels.PIPE_ENTRANCE_NSE;
                            }
                        }
                        if (state.getValue(WEST)) {
                            if (state.getValue(DOWN)) {
                                if (state.getValue(CLOSED)) {
                                    return ClearWarpPipeVoxels.PIPE_ENTRANCE_CLOSED_NSWD;
                                } else {
                                    return ClearWarpPipeVoxels.PIPE_ENTRANCE_NSWD;
                                }
                            }
                            if (state.getValue(CLOSED)) {
                                return ClearWarpPipeVoxels.PIPE_ENTRANCE_CLOSED_NSW;
                            } else {
                                return ClearWarpPipeVoxels.PIPE_ENTRANCE_NSW;
                            }
                        }
                        if (state.getValue(DOWN)) {
                            if (state.getValue(CLOSED)) {
                                return ClearWarpPipeVoxels.PIPE_ENTRANCE_CLOSED_NSD;
                            } else {
                                return ClearWarpPipeVoxels.PIPE_ENTRANCE_NSD;
                            }
                        }
                        if (state.getValue(CLOSED)) {
                            return ClearWarpPipeVoxels.PIPE_ENTRANCE_CLOSED_NS;
                        } else {
                            return ClearWarpPipeVoxels.PIPE_ENTRANCE_NS;
                        }
                    }
                    if (state.getValue(EAST)) {
                        if (state.getValue(WEST)) {
                            if (state.getValue(DOWN)) {
                                if (state.getValue(CLOSED)) {
                                    return ClearWarpPipeVoxels.PIPE_ENTRANCE_CLOSED_NEWD;
                                } else {
                                    return ClearWarpPipeVoxels.PIPE_ENTRANCE_NEWD;
                                }
                            }
                            if (state.getValue(CLOSED)) {
                                return ClearWarpPipeVoxels.PIPE_ENTRANCE_CLOSED_NEW;
                            } else {
                                return ClearWarpPipeVoxels.PIPE_ENTRANCE_NEW;
                            }
                        }
                        if (state.getValue(DOWN)) {
                            if (state.getValue(CLOSED)) {
                                return ClearWarpPipeVoxels.PIPE_ENTRANCE_CLOSED_NED;
                            } else {
                                return ClearWarpPipeVoxels.PIPE_ENTRANCE_NED;
                            }
                        }
                        if (state.getValue(CLOSED)) {
                            return ClearWarpPipeVoxels.PIPE_ENTRANCE_CLOSED_NE;
                        } else {
                            return ClearWarpPipeVoxels.PIPE_ENTRANCE_NE;
                        }
                    }
                    if (state.getValue(WEST)) {
                        if (state.getValue(DOWN)) {
                            if (state.getValue(CLOSED)) {
                                return ClearWarpPipeVoxels.PIPE_ENTRANCE_CLOSED_NWD;
                            } else {
                                return ClearWarpPipeVoxels.PIPE_ENTRANCE_NWD;
                            }
                        }
                        if (state.getValue(CLOSED)) {
                            return ClearWarpPipeVoxels.PIPE_ENTRANCE_CLOSED_NW;
                        } else {
                            return ClearWarpPipeVoxels.PIPE_ENTRANCE_NW;
                        }
                    }
                    if (state.getValue(DOWN)) {
                        if (state.getValue(CLOSED)) {
                            return ClearWarpPipeVoxels.PIPE_ENTRANCE_CLOSED_ND;
                        } else {
                            return ClearWarpPipeVoxels.PIPE_ENTRANCE_ND;
                        }
                    }
                    if (state.getValue(CLOSED)) {
                        return ClearWarpPipeVoxels.PIPE_ENTRANCE_CLOSED_N;
                    } else {
                        return ClearWarpPipeVoxels.PIPE_ENTRANCE_N;
                    }
                }
                if (state.getValue(SOUTH)) {
                    if (state.getValue(EAST)) {
                        if (state.getValue(WEST)) {
                            if (state.getValue(DOWN)) {
                                if (state.getValue(CLOSED)) {
                                    return ClearWarpPipeVoxels.PIPE_ENTRANCE_CLOSED_SEWD;
                                } else {
                                    return ClearWarpPipeVoxels.PIPE_ENTRANCE_SEWD;
                                }
                            }
                            if (state.getValue(CLOSED)) {
                                return ClearWarpPipeVoxels.PIPE_ENTRANCE_CLOSED_SEW;
                            } else {
                                return ClearWarpPipeVoxels.PIPE_ENTRANCE_SEW;
                            }
                        }
                        if (state.getValue(DOWN)) {
                            if (state.getValue(CLOSED)) {
                                return ClearWarpPipeVoxels.PIPE_ENTRANCE_CLOSED_SED;
                            } else {
                                return ClearWarpPipeVoxels.PIPE_ENTRANCE_SED;
                            }
                        }
                        if (state.getValue(CLOSED)) {
                            return ClearWarpPipeVoxels.PIPE_ENTRANCE_CLOSED_SE;
                        } else {
                            return ClearWarpPipeVoxels.PIPE_ENTRANCE_SE;
                        }
                    }
                    if (state.getValue(WEST)) {
                        if (state.getValue(DOWN)) {
                            if (state.getValue(CLOSED)) {
                                return ClearWarpPipeVoxels.PIPE_ENTRANCE_CLOSED_SWD;
                            } else {
                                return ClearWarpPipeVoxels.PIPE_ENTRANCE_SWD;
                            }
                        }
                        if (state.getValue(CLOSED)) {
                            return ClearWarpPipeVoxels.PIPE_ENTRANCE_CLOSED_SW;
                        } else {
                            return ClearWarpPipeVoxels.PIPE_ENTRANCE_SW;
                        }
                    }
                    if (state.getValue(DOWN)) {
                        if (state.getValue(CLOSED)) {
                            return ClearWarpPipeVoxels.PIPE_ENTRANCE_CLOSED_SD;
                        } else {
                            return ClearWarpPipeVoxels.PIPE_ENTRANCE_SD;
                        }
                    }
                    if (state.getValue(CLOSED)) {
                        return ClearWarpPipeVoxels.PIPE_ENTRANCE_CLOSED_S;
                    } else {
                        return ClearWarpPipeVoxels.PIPE_ENTRANCE_S;
                    }
                }
                if (state.getValue(EAST)) {
                    if (state.getValue(WEST)) {
                        if (state.getValue(DOWN)) {
                            if (state.getValue(CLOSED)) {
                                return ClearWarpPipeVoxels.PIPE_ENTRANCE_CLOSED_EWD;
                            } else {
                                return ClearWarpPipeVoxels.PIPE_ENTRANCE_EWD;
                            }
                        }
                        if (state.getValue(CLOSED)) {
                            return ClearWarpPipeVoxels.PIPE_ENTRANCE_CLOSED_EW;
                        } else {
                            return ClearWarpPipeVoxels.PIPE_ENTRANCE_EW;
                        }
                    }
                    if (state.getValue(DOWN)) {
                        if (state.getValue(CLOSED)) {
                            return ClearWarpPipeVoxels.PIPE_ENTRANCE_CLOSED_ED;
                        } else {
                            return ClearWarpPipeVoxels.PIPE_ENTRANCE_ED;
                        }
                    }
                    if (state.getValue(CLOSED)) {
                        return ClearWarpPipeVoxels.PIPE_ENTRANCE_CLOSED_E;
                    } else {
                        return ClearWarpPipeVoxels.PIPE_ENTRANCE_E;
                    }
                }
                if (state.getValue(WEST)) {
                    if (state.getValue(DOWN)) {
                        if (state.getValue(CLOSED)) {
                            return ClearWarpPipeVoxels.PIPE_ENTRANCE_CLOSED_WD;
                        } else {
                            return ClearWarpPipeVoxels.PIPE_ENTRANCE_WD;
                        }
                    }
                    if (state.getValue(CLOSED)) {
                        return ClearWarpPipeVoxels.PIPE_ENTRANCE_CLOSED_W;
                    } else {
                        return ClearWarpPipeVoxels.PIPE_ENTRANCE_W;
                    }
                }
                if (state.getValue(DOWN)) {
                    if (state.getValue(CLOSED)) {
                        return ClearWarpPipeVoxels.PIPE_ENTRANCE_CLOSED_D;
                    } else {
                        return ClearWarpPipeVoxels.PIPE_ENTRANCE_D;
                    }
                }
                if (state.getValue(CLOSED)) {
                    return ClearWarpPipeVoxels.PIPE_ENTRANCE_CLOSED;
                } else return ClearWarpPipeVoxels.PIPE_ENTRANCE;
            }
            if (state.getValue(FACING) == Direction.NORTH) {
                if (state.getValue(EAST)) {
                    if (state.getValue(CLOSED)) {
                        return ClearWarpPipeVoxels.PIPE_ENTRANCE_XN_E;
                    } else {
                        return ClearWarpPipeVoxels.PIPE_ENTRANCE_XN_E;
                    }
                }
                if (state.getValue(WEST)) {
                    if (state.getValue(CLOSED)) {
                        return ClearWarpPipeVoxels.PIPE_ENTRANCE_XN_W;
                    } else {
                        return ClearWarpPipeVoxels.PIPE_ENTRANCE_XN_W;
                    }
                }
                if (state.getValue(CLOSED)) {
                    return ClearWarpPipeVoxels.PIPE_ENTRANCE_CLOSED_XN;
                } else return ClearWarpPipeVoxels.PIPE_ENTRANCE_YN;
            }
            if (state.getValue(FACING) == Direction.SOUTH) {
                if (state.getValue(EAST)) {
                    if (state.getValue(CLOSED)) {
                        return ClearWarpPipeVoxels.PIPE_ENTRANCE_XS_E;
                    } else {
                        return ClearWarpPipeVoxels.PIPE_ENTRANCE_XS_E;
                    }
                }
                if (state.getValue(WEST)) {
                    if (state.getValue(CLOSED)) {
                        return ClearWarpPipeVoxels.PIPE_ENTRANCE_XS_W;
                    } else {
                        return ClearWarpPipeVoxels.PIPE_ENTRANCE_XS_W;
                    }
                }
                if (state.getValue(CLOSED)) {
                    return ClearWarpPipeVoxels.PIPE_ENTRANCE_CLOSED_XS;
                } else return ClearWarpPipeVoxels.PIPE_ENTRANCE_YS;
            }
            if (state.getValue(FACING) == Direction.EAST) {
                if (state.getValue(NORTH)) {
                    if (state.getValue(CLOSED)) {
                        return ClearWarpPipeVoxels.PIPE_ENTRANCE_ZE_N;
                    } else {
                        return ClearWarpPipeVoxels.PIPE_ENTRANCE_ZE_N;
                    }
                }
                if (state.getValue(SOUTH)) {
                    if (state.getValue(CLOSED)) {
                        return ClearWarpPipeVoxels.PIPE_ENTRANCE_ZE_S;
                    } else {
                        return ClearWarpPipeVoxels.PIPE_ENTRANCE_ZE_S;
                    }
                }
                if (state.getValue(CLOSED)) {
                    return ClearWarpPipeVoxels.PIPE_ENTRANCE_CLOSED_ZE;
                } else return ClearWarpPipeVoxels.PIPE_ENTRANCE_ZE;
            }
            if (state.getValue(FACING) == Direction.WEST) {
                if (state.getValue(NORTH)) {
                    if (state.getValue(CLOSED)) {
                        return ClearWarpPipeVoxels.PIPE_ENTRANCE_ZW_N;
                    } else {
                        return ClearWarpPipeVoxels.PIPE_ENTRANCE_ZW_N;
                    }
                }
                if (state.getValue(SOUTH)) {
                    if (state.getValue(CLOSED)) {
                        return ClearWarpPipeVoxels.PIPE_ENTRANCE_ZW_S;
                    } else {
                        return ClearWarpPipeVoxels.PIPE_ENTRANCE_ZW_S;
                    }
                }
                if (state.getValue(CLOSED)) {
                    return ClearWarpPipeVoxels.PIPE_ENTRANCE_CLOSED_ZW;
                } else return ClearWarpPipeVoxels.PIPE_ENTRANCE_ZW;
            }
            if (state.getValue(FACING) == Direction.DOWN) {
                if (state.getValue(CLOSED)) {
                    return ClearWarpPipeVoxels.PIPE_ENTRANCE_CLOSED_XD;
                } else return ClearWarpPipeVoxels.PIPE_ENTRANCE_XD;
            }
            return ClearWarpPipeVoxels.PIPE_ENTRANCE;
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
