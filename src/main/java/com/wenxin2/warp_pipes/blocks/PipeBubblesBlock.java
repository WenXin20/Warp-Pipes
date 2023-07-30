package com.wenxin2.warp_pipes.blocks;

import com.wenxin2.warp_pipes.init.ModRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BubbleColumnBlock;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class PipeBubblesBlock extends BubbleColumnBlock implements BucketPickup {
    public static final BooleanProperty DRAG_DOWN = BlockStateProperties.DRAG;
    public static final DirectionProperty FACING = BlockStateProperties.FACING;

    public PipeBubblesBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.UP).setValue(DRAG_DOWN, Boolean.TRUE));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateBuilder) {
        stateBuilder.add(DRAG_DOWN, FACING);
    }

    public static BlockState getColumnState(BlockState state) {
        if (state.is(ModRegistry.PIPE_BUBBLES.get())) {
            return state;
        } else if (state.getBlock() instanceof WarpPipeBlock && !state.getValue(WarpPipeBlock.CLOSED)) {
            return ModRegistry.PIPE_BUBBLES.get().defaultBlockState().setValue(BubbleColumnBlock.DRAG_DOWN, Boolean.FALSE).setValue(FACING, state.getValue(FACING));
        } else if (state.is(Blocks.SOUL_SAND)) {
            return ModRegistry.PIPE_BUBBLES.get().defaultBlockState().setValue(DRAG_DOWN, Boolean.FALSE);
        }  else {
            return state.is(Blocks.MAGMA_BLOCK) ? ModRegistry.PIPE_BUBBLES.get().defaultBlockState().setValue(DRAG_DOWN, Boolean.TRUE) : Blocks.WATER.defaultBlockState();
        }
    }

    public static boolean canExistIn(BlockState state) {
        return state.is(ModRegistry.PIPE_BUBBLES.get()) || state.is(Blocks.WATER) && state.getFluidState().getAmount() >= 8 && state.getFluidState().isSource();
    }

    public void tick(BlockState state, ServerLevel serverWorld, BlockPos pos, RandomSource random) {
        if (state.getValue(FACING) == Direction.UP) {
            PipeBubblesBlock.updateColumnUp(serverWorld, pos, state, serverWorld.getBlockState(pos.below()));
        } else if (state.getValue(FACING) == Direction.DOWN) {
            PipeBubblesBlock.updateColumnDown(serverWorld, pos, state, serverWorld.getBlockState(pos.above()));
        } else if (state.getValue(FACING) == Direction.NORTH) {
            PipeBubblesBlock.updateColumnNorth(serverWorld, pos, state, serverWorld.getBlockState(pos.south()));
        } else if (state.getValue(FACING) == Direction.SOUTH) {
            PipeBubblesBlock.updateColumnSouth(serverWorld, pos, state, serverWorld.getBlockState(pos.north()));
        } else if (state.getValue(FACING) == Direction.EAST) {
            PipeBubblesBlock.updateColumnEast(serverWorld, pos, state, serverWorld.getBlockState(pos.west()));
        } else if (state.getValue(FACING) == Direction.WEST) {
            PipeBubblesBlock.updateColumnWest(serverWorld, pos, state, serverWorld.getBlockState(pos.east()));
        }
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor worldAccessor, BlockPos pos, BlockPos neighborPos) {
        worldAccessor.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(worldAccessor));
        if (!state.canSurvive(worldAccessor, pos) /*|| direction == Direction.UP || direction == Direction.DOWN
                || direction == Direction.NORTH || direction == Direction.SOUTH || direction == Direction.EAST || direction == Direction.WEST*/
                && !neighborState.is(ModRegistry.PIPE_BUBBLES.get()) && canExistIn(neighborState)) {
            worldAccessor.scheduleTick(pos, this, 5);
        }

        return super.updateShape(state, direction, neighborState, worldAccessor, pos, neighborPos);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader worldReader, BlockPos pos) {
        BlockState stateAbove = worldReader.getBlockState(pos.above());
        BlockState stateBelow = worldReader.getBlockState(pos.below());
        BlockState stateNorth = worldReader.getBlockState(pos.below());
        BlockState stateSouth = worldReader.getBlockState(pos.below());
        BlockState stateEast = worldReader.getBlockState(pos.below());
        BlockState stateWest = worldReader.getBlockState(pos.below());

        if (state.getValue(FACING) == Direction.UP) {
            return stateAbove.is(ModRegistry.PIPE_BUBBLES.get()) || stateAbove.getBlock() instanceof WarpPipeBlock;
        } else if (state.getValue(FACING) == Direction.DOWN) {
            return stateBelow.is(ModRegistry.PIPE_BUBBLES.get()) || stateBelow.getBlock() instanceof WarpPipeBlock;
        } else if (state.getValue(FACING) == Direction.NORTH) {
            return stateNorth.is(ModRegistry.PIPE_BUBBLES.get()) || stateNorth.getBlock() instanceof WarpPipeBlock;
        } else if (state.getValue(FACING) == Direction.SOUTH) {
            return stateSouth.is(ModRegistry.PIPE_BUBBLES.get()) || stateSouth.getBlock() instanceof WarpPipeBlock;
        } else if (state.getValue(FACING) == Direction.EAST) {
            return stateEast.is(ModRegistry.PIPE_BUBBLES.get()) || stateEast.getBlock() instanceof WarpPipeBlock;
        } else {
            return stateWest.is(ModRegistry.PIPE_BUBBLES.get()) || stateWest.getBlock() instanceof WarpPipeBlock;
        }
    }

    @Override
    public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource random) {
        double d0 = (double)pos.getX();
        double d1 = (double)pos.getY();
        double d2 = (double)pos.getZ();

        if (state.getValue(DRAG_DOWN) || state.getValue(FACING) == Direction.DOWN) {
            world.addAlwaysVisibleParticle(ParticleTypes.CURRENT_DOWN, d0 + 0.5D, d1 + 0.8D, d2, 0.0D, 0.0D, 0.0D);
            if (random.nextInt(200) == 0) {
                world.playLocalSound(d0, d1, d2, SoundEvents.BUBBLE_COLUMN_WHIRLPOOL_AMBIENT, SoundSource.BLOCKS, 0.2F + random.nextFloat() * 0.2F, 0.9F + random.nextFloat() * 0.15F, false);
            }
        } else {
            if (state.getValue(FACING) == Direction.UP) {
                world.addAlwaysVisibleParticle(ParticleTypes.BUBBLE_COLUMN_UP, d0 + 0.5D, d1, d2 + 0.5D, 0.0D, 0.04D, 0.0D);
                world.addAlwaysVisibleParticle(ParticleTypes.BUBBLE_COLUMN_UP, d0 + (double)random.nextFloat(),
                        d1 + (double)random.nextFloat(), d2 + (double)random.nextFloat(), 0.0D, 0.04D, 0.0D);
            } else {
                world.addAlwaysVisibleParticle(ParticleTypes.BUBBLE, d0 + 0.5D, d1, d2 + 0.5D, 0.0D, 0.04D, -0.05D);
                world.addAlwaysVisibleParticle(ParticleTypes.BUBBLE, d0 + (double) random.nextFloat(),
                        d1 + (double) random.nextFloat(), d2 + (double) random.nextFloat(), 0.0D, 0.04D, -0.05D);
            }

            if (random.nextInt(200) == 0) {
                world.playLocalSound(d0, d1, d2, SoundEvents.BUBBLE_COLUMN_UPWARDS_AMBIENT, SoundSource.BLOCKS, 0.2F + random.nextFloat() * 0.2F, 0.9F + random.nextFloat() * 0.15F, false);
            }
        }
    }

    @Override
    public void entityInside(BlockState state, Level world, BlockPos pos, Entity entity) {
        BlockState blockstate = world.getBlockState(pos.above());

        if (blockstate.isAir()) {
            this.onBelowDownBubbleCol(state.getValue(DRAG_DOWN), entity);
            if (!world.isClientSide) {
                ServerLevel serverlevel = (ServerLevel)world;

                for(int i = 0; i < 2; ++i) {
                    serverlevel.sendParticles(ParticleTypes.SPLASH, (double)pos.getX() + world.random.nextDouble(), (double)(pos.getY() + 1), (double)pos.getZ() + world.random.nextDouble(), 1, 0.0D, 0.0D, 0.0D, 1.0D);
                    serverlevel.sendParticles(ParticleTypes.BUBBLE, (double)pos.getX() + world.random.nextDouble(), (double)(pos.getY() + 1), (double)pos.getZ() + world.random.nextDouble(), 1, 0.0D, 0.01D, 0.0D, 0.2D);
                }
            }
        } else {
            this.onInsideDownBubbleColumn(state.getValue(DRAG_DOWN), entity);
        }
    }

    public void onBelowDownBubbleCol(boolean isDragDown, Entity entity) {
        Vec3 vec3 = entity.getDeltaMovement();
        double d0;
        if (isDragDown) {
            d0 = Math.max(-0.9D, vec3.y - 0.03D);
        } else {
            d0 = Math.min(1.8D, vec3.y + 0.1D);
        }
        entity.setDeltaMovement(vec3.x, d0, vec3.z);
    }

    public void onInsideDownBubbleColumn(boolean isDragDown, Entity entity) {
        Vec3 vec3 = entity.getDeltaMovement();
        double d0;
        if (isDragDown) {
            d0 = Math.max(-0.3D, vec3.y - 0.03D);
        } else {
            d0 = Math.min(0.7D, vec3.y + 0.06D);
        }

        entity.setDeltaMovement(vec3.x, d0, vec3.z);
        entity.resetFallDistance();
    }

    public static void updateColumnUp(LevelAccessor worldAccessor, BlockPos pos, BlockState state) {
        updateColumnUp(worldAccessor, pos, worldAccessor.getBlockState(pos), state);
    }

    public static void updateColumnDown(LevelAccessor worldAccessor, BlockPos pos, BlockState state) {
        updateColumnDown(worldAccessor, pos, worldAccessor.getBlockState(pos), state);
    }

    public static void updateColumnNorth(LevelAccessor worldAccessor, BlockPos pos, BlockState state) {
        updateColumnNorth(worldAccessor, pos, worldAccessor.getBlockState(pos), state);
    }

    public static void updateColumnSouth(LevelAccessor worldAccessor, BlockPos pos, BlockState state) {
        updateColumnSouth(worldAccessor, pos, worldAccessor.getBlockState(pos), state);
    }

    public static void updateColumnEast(LevelAccessor worldAccessor, BlockPos pos, BlockState state) {
        updateColumnEast(worldAccessor, pos, worldAccessor.getBlockState(pos), state);
    }

    public static void updateColumnWest(LevelAccessor worldAccessor, BlockPos pos, BlockState state) {
        updateColumnWest(worldAccessor, pos, worldAccessor.getBlockState(pos), state);
    }

    public static void updateColumnUp(LevelAccessor worldAccessor, BlockPos pos, BlockState state, BlockState neighborState) {
        if (PipeBubblesBlock.canExistIn(state)) {
            int i = 0;
            BlockPos.MutableBlockPos mutablePos = pos.mutable().move(Direction.UP);

            BlockState pipeColumnState = PipeBubblesBlock.getColumnState(neighborState);
            worldAccessor.setBlock(pos, pipeColumnState, 2);

            while (PipeBubblesBlock.canExistIn(worldAccessor.getBlockState(mutablePos)) && i < 2) {
                if (!worldAccessor.setBlock(mutablePos, pipeColumnState, 2)) {
                    return;
                }
                mutablePos.move(Direction.UP);
                ++i;
            }
        }
    }

    public static void updateColumnDown(LevelAccessor worldAccessor, BlockPos pos, BlockState state, BlockState neighborState) {
        if (PipeBubblesBlock.canExistIn(state)) {
            int i = 0;
            BlockPos.MutableBlockPos mutablePos = pos.mutable().move(Direction.DOWN);

            BlockState pipeColumnState = PipeBubblesBlock.getColumnState(neighborState);
            worldAccessor.setBlock(pos, pipeColumnState, 2);

            while (PipeBubblesBlock.canExistIn(worldAccessor.getBlockState(mutablePos)) && i < 2) {
                if (!worldAccessor.setBlock(mutablePos, pipeColumnState, 2)) {
                    return;
                }
                mutablePos.move(Direction.DOWN);
                ++i;
            }
        }
    }

    public static void updateColumnNorth(LevelAccessor worldAccessor, BlockPos pos, BlockState state, BlockState neighborState) {
        if (PipeBubblesBlock.canExistIn(state)) {
            int i = 0;
            BlockPos.MutableBlockPos mutablePos = pos.mutable().move(Direction.NORTH);

            BlockState pipeColumnState = PipeBubblesBlock.getColumnState(neighborState);
            worldAccessor.setBlock(pos, pipeColumnState, 2);

            while (PipeBubblesBlock.canExistIn(worldAccessor.getBlockState(mutablePos)) && i < 2) {
                if (!worldAccessor.setBlock(mutablePos, pipeColumnState, 2)) {
                    return;
                }
                mutablePos.move(Direction.NORTH);
                ++i;
            }
        }
    }

    public static void updateColumnSouth(LevelAccessor worldAccessor, BlockPos pos, BlockState state, BlockState neighborState) {
        if (PipeBubblesBlock.canExistIn(state)) {
            int i = 0;
            BlockPos.MutableBlockPos mutablePos = pos.mutable().move(Direction.SOUTH);

            BlockState pipeColumnState = PipeBubblesBlock.getColumnState(neighborState);
            worldAccessor.setBlock(pos, pipeColumnState, 2);

            while (PipeBubblesBlock.canExistIn(worldAccessor.getBlockState(mutablePos)) && i < 2) {
                if (!worldAccessor.setBlock(mutablePos, pipeColumnState, 2)) {
                    return;
                }
                mutablePos.move(Direction.SOUTH);
                ++i;
            }
        }
    }

    public static void updateColumnEast(LevelAccessor worldAccessor, BlockPos pos, BlockState state, BlockState neighborState) {
        if (PipeBubblesBlock.canExistIn(state)) {
            int i = 0;
            BlockPos.MutableBlockPos mutablePos = pos.mutable().move(Direction.EAST);

            BlockState pipeColumnState = PipeBubblesBlock.getColumnState(neighborState);
            worldAccessor.setBlock(pos, pipeColumnState, 2);

            while (PipeBubblesBlock.canExistIn(worldAccessor.getBlockState(mutablePos)) && i < 2) {
                if (!worldAccessor.setBlock(mutablePos, pipeColumnState, 2)) {
                    return;
                }
                mutablePos.move(Direction.EAST);
                ++i;
            }
        }
    }

    public static void updateColumnWest(LevelAccessor worldAccessor, BlockPos pos, BlockState state, BlockState neighborState) {
        if (PipeBubblesBlock.canExistIn(state)) {
            int i = 0;
            BlockPos.MutableBlockPos mutablePos = pos.mutable().move(Direction.WEST);

            BlockState pipeColumnState = PipeBubblesBlock.getColumnState(neighborState);
            worldAccessor.setBlock(pos, pipeColumnState, 2);

            while (PipeBubblesBlock.canExistIn(worldAccessor.getBlockState(mutablePos)) && i < 2) {
                if (!worldAccessor.setBlock(mutablePos, pipeColumnState, 2)) {
                    return;
                }
                mutablePos.move(Direction.WEST);
                ++i;
            }
        }
    }

    //Remove when done
    @Override
    public VoxelShape getShape(BlockState p_51005_, BlockGetter p_51006_, BlockPos p_51007_, CollisionContext p_51008_) {
        return Shapes.block();
    }

    @Override
    public RenderShape getRenderShape(BlockState p_51003_) {
        return RenderShape.INVISIBLE;
    }
}
