package com.wenxin2.warp_pipes.blocks;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.wenxin2.warp_pipes.blocks.entities.WarpPipeBlockEntity;
import java.util.Collection;
import java.util.Map;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.NotNull;

public class ClearWarpPipeBlock extends WarpPipeBlock implements EntityBlock, SimpleWaterloggedBlock {
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final BooleanProperty ENTRANCE = BooleanProperty.create("entrance");
    public static final BooleanProperty CLOSED = BooleanProperty.create("closed");
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
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.UP)
                .setValue(ENTRANCE, Boolean.TRUE).setValue(CLOSED, Boolean.FALSE).setValue(WATERLOGGED, Boolean.FALSE)
                .setValue(UP, Boolean.FALSE).setValue(NORTH, Boolean.FALSE).setValue(SOUTH, Boolean.FALSE)
                .setValue(EAST, Boolean.FALSE).setValue(WEST, Boolean.FALSE).setValue(DOWN, Boolean.FALSE));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateBuilder) {
        stateBuilder.add(CLOSED, ENTRANCE, FACING, WATERLOGGED, UP, DOWN, NORTH, SOUTH, EAST, WEST);
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
                .setValue(CLOSED, placeContext.getLevel().hasNeighborSignal(placeContext.getClickedPos()))
                .setValue(WATERLOGGED, fluidState.is(FluidTags.WATER) && fluidState.getAmount() == 8);
    }

    @NotNull
    @Override
    public FluidState getFluidState(final BlockState state)
    {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
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
        if (state.getValue(CLOSED) && (state.getValue(NORTH) || state.getValue(SOUTH) || state.getValue(EAST) || state.getValue(WEST))) {
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

        if (state.getValue(WATERLOGGED) && !state.getValue(CLOSED)) {
            worldAccessor.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(worldAccessor));
        }

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

    @Override
    public void entityInside(BlockState state, Level world, BlockPos pos, Entity entity) {
        RandomSource random = world.getRandom();
        Vec3 moveVec = entity.getDeltaMovement();

        double entityX = entity.getX();
        double entityY = entity.getY();
        double entityZ = entity.getZ();

        int blockX = pos.getX();
        int blockY = pos.getY();
        int blockZ = pos.getZ();

        if ((entityY < blockY + 0.98 && entityY > blockY + 0.02) && (entityX < blockX + 0.98 && entityX > blockX + 0.02)
                && (entityZ < blockZ + 0.98 && entityZ > blockZ + 0.02) && !entity.isShiftKeyDown()) {
            this.moveSidewaysInPipe(entity);

            if (moveVec.x > 0 || moveVec.x < 0 || moveVec.y > 0 || moveVec.y < 0 || moveVec.z > 0 || moveVec.z < 0) {
                if (random.nextInt(10) == 0) {
                    Collection<ServerPlayer> players = ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers();
                    for (ServerPlayer player : players) {
                        for (int i = 0; i < 2; i++) {
                            player.connection.send(new ClientboundLevelParticlesPacket(
                                    ParticleTypes.EFFECT, false,
                                    entityX, entityY, entityZ,
                                    0.25F, 0.15F, 0.25F,
                                    0, 2
                            ));
                        }
                    }
                }
            }
        }
        super.entityInside(state, world, pos, entity);
    }

    public void moveSidewaysInPipe(Entity entity) {
        Vec3 lookVec = entity.getLookAngle();
        Vec3 moveVec = entity.getDeltaMovement();
        double d0 = Math.min(1.5D, moveVec.y + 0.1D);
        double speed = 1.25D;
        double verticalSpeed = 1.15D;

        if (entity instanceof LivingEntity && !entity.isShiftKeyDown()) {
            Vec3 movement = new Vec3(lookVec.x * speed, moveVec.y * verticalSpeed, lookVec.z * speed);
            entity.setDeltaMovement(movement.x, movement.y, movement.z);

            if (moveVec.y > 0 || moveVec.y < 0) {
                entity.setDeltaMovement(moveVec.x, d0, moveVec.z);
            }
        } else if (!entity.isShiftKeyDown()) {

            Vec3 movement = new Vec3(moveVec.x * speed, moveVec.y * verticalSpeed, moveVec.z * speed);
            entity.setDeltaMovement(movement.x, movement.y, movement.z);

            if (moveVec.y > 0 || moveVec.y < 0) {
                entity.setDeltaMovement(moveVec.x, d0, moveVec.z);
            }
        }
        entity.resetFallDistance();
    }
}
