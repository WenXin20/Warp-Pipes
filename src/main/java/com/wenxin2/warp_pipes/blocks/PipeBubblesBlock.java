package com.wenxin2.warp_pipes.blocks;

import com.wenxin2.warp_pipes.init.ModRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BubbleColumnBlock;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;

public class PipeBubblesBlock extends BubbleColumnBlock implements BucketPickup {
    public static final BooleanProperty DRAG_DOWN = BlockStateProperties.DRAG;
    public static final DirectionProperty FACING = BlockStateProperties.FACING;
    public static final IntegerProperty DISTANCE = IntegerProperty.create("distance", 0, 6);

    public PipeBubblesBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.UP).setValue(DRAG_DOWN, Boolean.TRUE).setValue(DISTANCE, 3));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateBuilder) {
        stateBuilder.add(DISTANCE, DRAG_DOWN, FACING);
    }

    public static boolean canExistIn(BlockState state) {
        return state.is(ModRegistry.PIPE_BUBBLES.get()) || state.is(Blocks.WATER) && state.getFluidState().getAmount() >= 8 && state.getFluidState().isSource();
    }

    @Override
    public void tick(BlockState state, ServerLevel serverWorld, BlockPos pos, RandomSource random) {
        Direction facing = state.getValue(FACING);

        if (facing == Direction.UP) {
            PipeBubblesBlock.repeatColumnUp(serverWorld, pos, state, serverWorld.getBlockState(pos.below()));
        } else if (facing == Direction.DOWN) {
            PipeBubblesBlock.repeatColumnDown(serverWorld, pos, state, serverWorld.getBlockState(pos.above()));
        } else if (facing == Direction.NORTH) {
            PipeBubblesBlock.repeatColumnNorth(serverWorld, pos, state, serverWorld.getBlockState(pos.south()));
        } else if (facing == Direction.SOUTH) {
            PipeBubblesBlock.repeatColumnSouth(serverWorld, pos, state, serverWorld.getBlockState(pos.north()));
        } else if (facing == Direction.EAST) {
            PipeBubblesBlock.repeatColumnEast(serverWorld, pos, state, serverWorld.getBlockState(pos.west()));
        } else if (facing == Direction.WEST) {
            PipeBubblesBlock.repeatColumnWest(serverWorld, pos, state, serverWorld.getBlockState(pos.east()));
        }
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor worldAccessor, BlockPos pos, BlockPos neighborPos) {
        if (!state.canSurvive(worldAccessor, pos) && !neighborState.is(ModRegistry.PIPE_BUBBLES.get())
                && canExistIn(neighborState)) {
            worldAccessor.scheduleTick(pos, this, 3);
        }
        worldAccessor.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(worldAccessor));
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
            return (stateBelow.is(ModRegistry.PIPE_BUBBLES.get()) || stateBelow.getBlock() instanceof WarpPipeBlock);
        } else if (state.getValue(FACING) == Direction.DOWN) {
            return (stateAbove.is(ModRegistry.PIPE_BUBBLES.get()) || stateAbove.getBlock() instanceof WarpPipeBlock);
        } else if (state.getValue(FACING) == Direction.NORTH) {
            return (stateSouth.is(ModRegistry.PIPE_BUBBLES.get()) || stateSouth.getBlock() instanceof WarpPipeBlock);
        } else if (state.getValue(FACING) == Direction.SOUTH) {
            return (stateNorth.is(ModRegistry.PIPE_BUBBLES.get()) || stateNorth.getBlock() instanceof WarpPipeBlock);
        } else if (state.getValue(FACING) == Direction.EAST) {
            return (stateWest.is(ModRegistry.PIPE_BUBBLES.get()) || stateWest.getBlock() instanceof WarpPipeBlock);
        } else {
            return (stateEast.is(ModRegistry.PIPE_BUBBLES.get()) || stateEast.getBlock() instanceof WarpPipeBlock);
        }
    }

    public static BlockState setBlockState(BlockState state, LevelAccessor worldAccessor, BlockPos pos, int distance) {
        BlockPos.MutableBlockPos posMutable = new BlockPos.MutableBlockPos();
        for (Direction direction : Direction.values()) {
            posMutable.setWithOffset(pos, direction);
            distance = Math.min(distance, getDistance(worldAccessor.getBlockState(posMutable)) + 1);
            if (distance == 1) {
                break;
            }
        }

        if (state.is(ModRegistry.PIPE_BUBBLES.get())) {
            // Clamp the distance value to be within the allowed range (0 to 3)
            distance = Math.min(Math.max(distance, 0), 3);
            return state.setValue(DISTANCE, distance);
        } else if (state.getBlock() instanceof WarpPipeBlock && !state.getValue(WarpPipeBlock.CLOSED)) {
            return ModRegistry.PIPE_BUBBLES.get().defaultBlockState().setValue(DRAG_DOWN, Boolean.FALSE)
                    .setValue(FACING, state.getValue(FACING));
        }
        return Blocks.WATER.defaultBlockState();
    }

    private static int getDistance(BlockState state) {
        if (state.getBlock() instanceof WarpPipeBlock && !state.getValue(WarpPipeBlock.CLOSED)) {
            return 0;
        }
        if (state.getBlock() instanceof PipeBubblesBlock) {
            return state.getValue(DISTANCE);
        }
        return 3;
    }

    public void addParticles(Level world, ParticleOptions particleOptions, double xPos, double yPos, double zPos,
                             int amt, double xMotion, double yMotion, double zMotion, double speed) {
        if (!world.isClientSide) {
            ServerLevel serverWorld = (ServerLevel)world;
            serverWorld.sendParticles(particleOptions, xPos, yPos, zPos, amt, xMotion, yMotion, zMotion, speed);
        }
    }

    public void addAlwaysVisibleParticles(Level world, ParticleOptions particleOptions, double xPos, double yPos, double zPos,
                                          double xMotion, double yMotion, double zMotion) {
        if (world.isClientSide) {
            world.addAlwaysVisibleParticle(particleOptions, xPos, yPos, zPos, xMotion, yMotion, zMotion);
        }
    }

    @Override
    public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource random) {
        double x = pos.getX();
        double y = pos.getY();
        double z = pos.getZ();
        if (state.getValue(DRAG_DOWN) || state.getValue(FACING) == Direction.DOWN) {
            this.addAlwaysVisibleParticles(world, ParticleTypes.CURRENT_DOWN, x + 0.5D, y + 0.8D, z, 0.0D, -1.0D, 0.0D);
            this.addAlwaysVisibleParticles(world, ParticleTypes.BUBBLE, x + random.nextFloat(),
                    y + random.nextFloat(), z + random.nextFloat(), 0.0D, -1.5D, 0.0D);
            if (random.nextInt(200) == 0) {
                world.playLocalSound(x, y, z, SoundEvents.BUBBLE_COLUMN_WHIRLPOOL_AMBIENT, SoundSource.BLOCKS,
                        0.2F + random.nextFloat() * 0.2F, 0.9F + random.nextFloat() * 0.15F, false);
            }
        } else {
            if (state.getValue(FACING) == Direction.UP) {
                this.addAlwaysVisibleParticles(world, ParticleTypes.BUBBLE_COLUMN_UP, x + 0.5D, y, z + 0.5D, 0.0D, 1.0D, 0.0D);
                this.addAlwaysVisibleParticles(world, ParticleTypes.BUBBLE_COLUMN_UP, x + random.nextFloat(),
                        y + random.nextFloat(), z + random.nextFloat(), 0.0D, 1.0D, 0.0D);
            } else if (state.getValue(FACING) == Direction.NORTH) {
                this.addAlwaysVisibleParticles(world, ParticleTypes.BUBBLE, x + 0.5D, y, z + 0.5D, 0.0D, 0.04D, -1.5D);
                this.addAlwaysVisibleParticles(world, ParticleTypes.BUBBLE, x + 0.5D, y, z + 0.5D, 0.0D, 0.04D, -1.5D);
                this.addAlwaysVisibleParticles(world, ParticleTypes.BUBBLE, x + random.nextFloat(),
                        y + random.nextFloat(), z + random.nextFloat(), 0.0D, 0.04D, -1.5D);
                this.addAlwaysVisibleParticles(world, ParticleTypes.BUBBLE, x + random.nextFloat(),
                        y + random.nextFloat(), z + random.nextFloat(), 0.0D, 0.04D, -1.5D);
            } else if (state.getValue(FACING) == Direction.SOUTH) {
                this.addAlwaysVisibleParticles(world, ParticleTypes.BUBBLE, x + 0.5D, y, z + 0.5D, 0.0D, 0.04D, 1.5D);
                this.addAlwaysVisibleParticles(world, ParticleTypes.BUBBLE, x + 0.5D, y, z + 0.5D, 0.0D, 0.04D, 1.5D);
                this.addAlwaysVisibleParticles(world, ParticleTypes.BUBBLE, x + random.nextFloat(),
                        y + random.nextFloat(), z + random.nextFloat(), 0.0D, 0.04D, 1.5D);
                this.addAlwaysVisibleParticles(world, ParticleTypes.BUBBLE, x + random.nextFloat(),
                        y + random.nextFloat(), z + random.nextFloat(), 0.0D, 0.04D, 1.5D);
            } else if (state.getValue(FACING) == Direction.EAST) {
                this.addAlwaysVisibleParticles(world, ParticleTypes.BUBBLE, x + 0.5D, y, z + 0.5D, 1.5D, 0.04D, 0.0D);
                this.addAlwaysVisibleParticles(world, ParticleTypes.BUBBLE, x + 0.5D, y, z + 0.5D, 1.5D, 0.04D, 0.0D);
                this.addAlwaysVisibleParticles(world, ParticleTypes.BUBBLE, x + random.nextFloat(),
                        y + random.nextFloat(), z + random.nextFloat(), 1.5D, 0.04D, 0.0D);
                this.addAlwaysVisibleParticles(world, ParticleTypes.BUBBLE, x + random.nextFloat(),
                        y + random.nextFloat(), z + random.nextFloat(), 1.5D, 0.04D, 0.0D);
            } else if (state.getValue(FACING) == Direction.WEST) {
                this.addAlwaysVisibleParticles(world, ParticleTypes.BUBBLE, x + 0.5D, y, z + 0.5D, -1.5D, 0.04D, 0.0D);
                this.addAlwaysVisibleParticles(world, ParticleTypes.BUBBLE, x + 0.5D, y, z + 0.5D, -1.5D, 0.04D, 0.0D);
                this.addAlwaysVisibleParticles(world, ParticleTypes.BUBBLE, x + random.nextFloat(),
                        y + random.nextFloat(), z + random.nextFloat(), -1.5D, 0.04D, 0.0D);
                this.addAlwaysVisibleParticles(world, ParticleTypes.BUBBLE, x + random.nextFloat(),
                        y + random.nextFloat(), z + random.nextFloat(), -1.5D, 0.04D, 0.0D);
            }
        }

        if (random.nextInt(200) == 0) {
            world.playLocalSound(x, y, z, SoundEvents.BUBBLE_COLUMN_UPWARDS_AMBIENT, SoundSource.BLOCKS,
                    0.2F + random.nextFloat() * 0.2F, 0.9F + random.nextFloat() * 0.15F, false);
        }
    }

    @Override
    public void entityInside(BlockState state, Level world, BlockPos pos, Entity entity) {
        BlockState stateAbove = world.getBlockState(pos.above());

        if (stateAbove.isAir()) {
            this.onAboveUpBubbleCol(state.getValue(DRAG_DOWN), entity);
            if (!world.isClientSide) {
                ServerLevel serverWorld = (ServerLevel)world;

                for(int i = 0; i < 2; ++i) {
                    this.addParticles(serverWorld, ParticleTypes.SPLASH, pos.getX() + world.random.nextDouble(),
                            (pos.getY() + 1), pos.getZ() + world.random.nextDouble(), 1, 0.0D, 0.0D, 0.0D, 1.0D);
                    this.addParticles(serverWorld, ParticleTypes.BUBBLE, pos.getX() + world.random.nextDouble(),
                            (pos.getY() + 1), pos.getZ() + world.random.nextDouble(), 1, 0.0D, 0.01D, 0.0D, 0.2D);
                }
            }
        } else if (state.getValue(FACING) == Direction.UP) {
            this.onInsideUpBubbleColumn(state.getValue(DRAG_DOWN), entity);
            if (!world.isClientSide) {
                ServerLevel serverWorld = (ServerLevel)world;

                for(int i = 0; i < 2; ++i) {
                    this.addParticles(serverWorld, ParticleTypes.SPLASH, pos.getX() + world.random.nextDouble(),
                            (pos.getY() + 1), pos.getZ() + world.random.nextDouble(), 1, 0.0D, 0.0D, 0.0D, 1.0D);
                    this.addParticles(serverWorld, ParticleTypes.BUBBLE, pos.getX() + world.random.nextDouble(),
                            (pos.getY() + 1), pos.getZ() + world.random.nextDouble(), 1, 0.0D, 0.01D, 0.0D, 0.2D);
                }
            }
        } else if (state.getValue(FACING) == Direction.DOWN) {
            this.onInsideDownBubbleColumn(state.getValue(DRAG_DOWN), entity);
        } else if (state.getValue(FACING) == Direction.NORTH) {
            this.onInsideNorthBubbleColumn(state.getValue(DRAG_DOWN), entity);
        } else if (state.getValue(FACING) == Direction.SOUTH) {
            this.onInsideSouthBubbleColumn(state.getValue(DRAG_DOWN), entity);
        } else if (state.getValue(FACING) == Direction.EAST) {
            this.onInsideEastBubbleColumn(state.getValue(DRAG_DOWN), entity);
        } else if (state.getValue(FACING) == Direction.WEST) {
            this.onInsideWestBubbleColumn(state.getValue(DRAG_DOWN), entity);
        }
    }

    public void onAboveUpBubbleCol(boolean isDragDown, Entity entity) {
        Vec3 vec3 = entity.getDeltaMovement();
        double d0;
        if (isDragDown) {
            d0 = Math.max(-0.9D, vec3.y - 0.03D);
        } else {
            d0 = Math.min(1.8D, vec3.y + 0.1D);
        }
        entity.setDeltaMovement(vec3.x, d0, vec3.z);
    }

    public void onInsideUpBubbleColumn(boolean isDragDown, Entity entity) {
        Vec3 vec3 = entity.getDeltaMovement();
        double d0;
        if (isDragDown) {
            d0 = Math.max(-0.3D, vec3.y - 0.03D);
        } else {
            d0 = Math.min(0.5D, vec3.y + 0.04D);
        }

        entity.setDeltaMovement(vec3.x, d0, vec3.z);
        entity.resetFallDistance();
    }

    public void onInsideDownBubbleColumn(boolean isDragDown, Entity entity) {
        Vec3 vec3 = entity.getDeltaMovement();
        double d0;
        if (isDragDown) {
            d0 = Math.max(0.3D, vec3.y + 0.03D);
        } else {
            d0 = Math.min(-0.5D, vec3.y - 0.04D);
        }

        entity.setDeltaMovement(vec3.x, d0, vec3.z);
        entity.resetFallDistance();
    }

    public void onInsideNorthBubbleColumn(boolean isDragDown, Entity entity) {
        Vec3 vec3 = entity.getDeltaMovement();
        double d0;
        if (isDragDown) {
            d0 = Math.max(0.3D, vec3.z + 0.03D);
        } else {
            d0 = Math.min(-0.5D, vec3.z - 0.04D);
        }

        entity.setDeltaMovement(vec3.x, vec3.y, d0);
        entity.resetFallDistance();
    }

    public void onInsideSouthBubbleColumn(boolean isDragDown, Entity entity) {
        Vec3 vec3 = entity.getDeltaMovement();
        double d0;
        if (isDragDown) {
            d0 = Math.max(-0.3D, vec3.z - 0.03D);
        } else {
            d0 = Math.min(0.5D, vec3.z + 0.04D);
        }

        entity.setDeltaMovement(vec3.x, vec3.y, d0);
        entity.resetFallDistance();
    }

    public void onInsideEastBubbleColumn(boolean isDragDown, Entity entity) {
        Vec3 vec3 = entity.getDeltaMovement();
        double d0;
        if (isDragDown) {
            d0 = Math.max(-0.3D, vec3.x - 0.03D);
        } else {
            d0 = Math.min(0.5D, vec3.x + 0.04D);
        }

        entity.setDeltaMovement(d0, vec3.y, vec3.z);
        entity.resetFallDistance();
    }

    public void onInsideWestBubbleColumn(boolean isDragDown, Entity entity) {
        Vec3 vec3 = entity.getDeltaMovement();
        double d0;
        if (isDragDown) {
            d0 = Math.max(0.3D, vec3.x + 0.03D);
        } else {
            d0 = Math.min(-0.5D, vec3.x - 0.04D);
        }

        entity.setDeltaMovement(d0, vec3.y, vec3.z);
        entity.resetFallDistance();
    }

    public static void repeatColumnUp(LevelAccessor worldAccessor, BlockPos pos, BlockState state) {
        repeatColumnUp(worldAccessor, pos, worldAccessor.getBlockState(pos), state);
    }

    public static void repeatColumnDown(LevelAccessor worldAccessor, BlockPos pos, BlockState state) {
        repeatColumnDown(worldAccessor, pos, worldAccessor.getBlockState(pos), state);
    }

    public static void repeatColumnNorth(LevelAccessor worldAccessor, BlockPos pos, BlockState state) {
        repeatColumnNorth(worldAccessor, pos, worldAccessor.getBlockState(pos), state);
    }

    public static void repeatColumnSouth(LevelAccessor worldAccessor, BlockPos pos, BlockState state) {
        repeatColumnSouth(worldAccessor, pos, worldAccessor.getBlockState(pos), state);
    }

    public static void repeatColumnEast(LevelAccessor worldAccessor, BlockPos pos, BlockState state) {
        repeatColumnEast(worldAccessor, pos, worldAccessor.getBlockState(pos), state);
    }

    public static void repeatColumnWest(LevelAccessor worldAccessor, BlockPos pos, BlockState state) {
        repeatColumnWest(worldAccessor, pos, worldAccessor.getBlockState(pos), state);
    }

    public static void repeatColumnUp(LevelAccessor worldAccessor, BlockPos pos, BlockState state, BlockState neighborState) {
        if (PipeBubblesBlock.canExistIn(state)) {
            int initialDistance = getDistance(neighborState);
            BlockPos.MutableBlockPos mutablePos = pos.mutable().move(Direction.UP);

            BlockState pipeColumnState = PipeBubblesBlock.setBlockState(neighborState, worldAccessor, pos, initialDistance);
            worldAccessor.setBlock(pos, pipeColumnState, 2);

            while (PipeBubblesBlock.canExistIn(worldAccessor.getBlockState(mutablePos)) && initialDistance < 2) {
                if (!worldAccessor.setBlock(mutablePos, pipeColumnState, 2)) {
                    return;
                }
                mutablePos.move(Direction.UP);
                initialDistance++;
                pipeColumnState = PipeBubblesBlock.setBlockState(pipeColumnState, worldAccessor, mutablePos, initialDistance);
            }
        }
    }

    public static void repeatColumnDown(LevelAccessor worldAccessor, BlockPos pos, BlockState state, BlockState neighborState) {
        if (PipeBubblesBlock.canExistIn(state)) {
            int initialDistance = getDistance(neighborState);
            BlockPos.MutableBlockPos mutablePos = pos.mutable().move(Direction.DOWN);

            BlockState pipeColumnState = PipeBubblesBlock.setBlockState(neighborState, worldAccessor, pos, initialDistance);
            worldAccessor.setBlock(pos, pipeColumnState, 2);

            while (PipeBubblesBlock.canExistIn(worldAccessor.getBlockState(mutablePos)) && initialDistance < 2) {
                if (!worldAccessor.setBlock(mutablePos, pipeColumnState, 2)) {
                    return;
                }
                mutablePos.move(Direction.DOWN);
                initialDistance++;
                pipeColumnState = PipeBubblesBlock.setBlockState(pipeColumnState, worldAccessor, mutablePos, initialDistance);
            }
        }
    }

    public static void repeatColumnNorth(LevelAccessor worldAccessor, BlockPos pos, BlockState state, BlockState neighborState) {
        if (PipeBubblesBlock.canExistIn(state)) {
            int initialDistance = getDistance(neighborState);
            BlockPos.MutableBlockPos mutablePos = pos.mutable().move(Direction.NORTH);

            BlockState pipeColumnState = PipeBubblesBlock.setBlockState(neighborState, worldAccessor, pos, initialDistance);
            worldAccessor.setBlock(pos, pipeColumnState, 2);

            while (PipeBubblesBlock.canExistIn(worldAccessor.getBlockState(mutablePos)) && initialDistance < 2) {
                if (!worldAccessor.setBlock(mutablePos, pipeColumnState, 2)) {
                    return;
                }
                mutablePos.move(Direction.NORTH);
                initialDistance++;
                pipeColumnState = PipeBubblesBlock.setBlockState(pipeColumnState, worldAccessor, mutablePos, initialDistance);
            }
        }
    }

    public static void repeatColumnSouth(LevelAccessor worldAccessor, BlockPos pos, BlockState state, BlockState neighborState) {
        if (PipeBubblesBlock.canExistIn(state)) {
            int initialDistance = getDistance(neighborState);
            BlockPos.MutableBlockPos mutablePos = pos.mutable().move(Direction.SOUTH);

            BlockState pipeColumnState = PipeBubblesBlock.setBlockState(neighborState, worldAccessor, pos, initialDistance);
            worldAccessor.setBlock(pos, pipeColumnState, 2);

            while (PipeBubblesBlock.canExistIn(worldAccessor.getBlockState(mutablePos)) && initialDistance < 2) {
                if (!worldAccessor.setBlock(mutablePos, pipeColumnState, 2)) {
                    return;
                }
                mutablePos.move(Direction.SOUTH);
                initialDistance++;
                pipeColumnState = PipeBubblesBlock.setBlockState(pipeColumnState, worldAccessor, mutablePos, initialDistance);
            }
        }
    }

    public static void repeatColumnEast(LevelAccessor worldAccessor, BlockPos pos, BlockState state, BlockState neighborState) {
        if (PipeBubblesBlock.canExistIn(state)) {
            int initialDistance = getDistance(neighborState);
            BlockPos.MutableBlockPos mutablePos = pos.mutable().move(Direction.EAST);

            BlockState pipeColumnState = PipeBubblesBlock.setBlockState(neighborState, worldAccessor, pos, initialDistance);
            worldAccessor.setBlock(pos, pipeColumnState, 2);

            while (PipeBubblesBlock.canExistIn(worldAccessor.getBlockState(mutablePos)) && initialDistance < 2) {
                if (!worldAccessor.setBlock(mutablePos, pipeColumnState, 2)) {
                    return;
                }
                mutablePos.move(Direction.EAST);
                initialDistance++;
                pipeColumnState = PipeBubblesBlock.setBlockState(pipeColumnState, worldAccessor, mutablePos, initialDistance);
            }
        }
    }

    public static void repeatColumnWest(LevelAccessor worldAccessor, BlockPos pos, BlockState state, BlockState neighborState) {
        if (PipeBubblesBlock.canExistIn(state)) {
            int initialDistance = getDistance(neighborState);
            BlockPos.MutableBlockPos mutablePos = pos.mutable().move(Direction.WEST);

            BlockState pipeColumnState = PipeBubblesBlock.setBlockState(neighborState, worldAccessor, pos, initialDistance);
            worldAccessor.setBlock(pos, pipeColumnState, 2);

            while (PipeBubblesBlock.canExistIn(worldAccessor.getBlockState(mutablePos)) && initialDistance < 2) {
                if (!worldAccessor.setBlock(mutablePos, pipeColumnState, 2)) {
                    return;
                }
                mutablePos.move(Direction.WEST);
                initialDistance++;
                pipeColumnState = PipeBubblesBlock.setBlockState(pipeColumnState, worldAccessor, mutablePos, initialDistance);
            }
        }
    }
}
