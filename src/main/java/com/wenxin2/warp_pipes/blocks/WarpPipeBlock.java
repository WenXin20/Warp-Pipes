package com.wenxin2.warp_pipes.blocks;

import com.wenxin2.warp_pipes.blocks.entities.WarpPipeBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BubbleColumnBlock;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class WarpPipeBlock extends DirectionalBlock implements EntityBlock {
    public static final BooleanProperty ENTRANCE = BooleanProperty.create("entrance");
    public static final BooleanProperty CLOSED = BooleanProperty.create("closed");

    public WarpPipeBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.UP).setValue(ENTRANCE, Boolean.TRUE).setValue(CLOSED, Boolean.FALSE));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateBuilder) {
        stateBuilder.add(CLOSED, ENTRANCE, FACING);
    }

    @Override
    public BlockEntity newBlockEntity(final BlockPos pos, final BlockState state)
    {
        return new WarpPipeBlockEntity(pos, state);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter blockGetter, BlockPos pos, CollisionContext collisionContext) {
        return Block.box(0.25D, 0.25D, 0.25D, 15.50D, 15.50D, 15.50D);
    }

    @Override
    public VoxelShape getBlockSupportShape(BlockState state, BlockGetter blockGetter, BlockPos pos) {
        return Shapes.block();
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext placeContext) {
        Direction direction = placeContext.getClickedFace();
        return this.defaultBlockState().setValue(FACING, direction);
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.setValue(FACING, mirror.mirror(state.getValue(FACING)));
    }

    @Override
    public boolean isPathfindable(BlockState state, BlockGetter blockGetter, BlockPos pos, PathComputationType pathType) {
        return false;
    }

    @Override
    public void neighborChanged(BlockState state, Level world, BlockPos pos, Block block, BlockPos posNeighbor, boolean b) {
        if (!world.isClientSide) {
            boolean isClosed = state.getValue(CLOSED);
            if (isClosed != world.hasNeighborSignal(pos)) {
                if (isClosed) {
                    world.scheduleTick(pos, this, 4);
                } else {
                    world.setBlock(pos, state.cycle(CLOSED), 2);
                    this.playAnvilSound(world, pos, SoundEvents.ANVIL_PLACE);
                }
            }

        }
    }

    @Override
    public void tick(BlockState state, ServerLevel serverWorld, BlockPos pos, RandomSource random) {
        if (state.getValue(CLOSED) && !serverWorld.hasNeighborSignal(pos)) {
            serverWorld.setBlock(pos, state.cycle(CLOSED), 2);
            this.playAnvilSound(serverWorld, pos, SoundEvents.ANVIL_PLACE);
        }
        BubbleColumnBlock.updateColumn(serverWorld, pos.above(), state);
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
                return state.setValue(ENTRANCE, Boolean.FALSE);
            }
            return state.setValue(ENTRANCE, Boolean.TRUE);
        }

        if (facingDown) {
            if (blockBelow instanceof WarpPipeBlock) {
                return state.setValue(ENTRANCE, Boolean.FALSE);
            }
            return state.setValue(ENTRANCE, Boolean.TRUE);
        }

        if (facingNorth) {
            if (blockNorth instanceof WarpPipeBlock) {
                return state.setValue(ENTRANCE, Boolean.FALSE);
            }
            return state.setValue(ENTRANCE, Boolean.TRUE);
        }

        if (facingSouth) {
            if (blockSouth instanceof WarpPipeBlock) {
                return state.setValue(ENTRANCE, Boolean.FALSE);
            }
            return state.setValue(ENTRANCE, Boolean.TRUE);
        }

        if (facingEast) {
            if (blockEast instanceof WarpPipeBlock) {
                return state.setValue(ENTRANCE, Boolean.FALSE);
            }
            return state.setValue(ENTRANCE, Boolean.TRUE);
        }

        if (facingWest) {
            if (blockWest instanceof WarpPipeBlock) {
                return state.setValue(ENTRANCE, Boolean.FALSE);
            }
        }
        return state.setValue(ENTRANCE, Boolean.TRUE);
    }

    public static void spawnParticles(Entity entity, Level world, BlockPos pos) {
        RandomSource random = world.getRandom();

        if (world.isClientSide() && entity instanceof Player) {
            for(int i = 0; i < 40; ++i) {
                world.addParticle(ParticleTypes.ENCHANT,
                        entity.getRandomX(0.5D), entity.getRandomY(), entity.getRandomZ(0.5D),
                        (random.nextDouble() - 0.5D) * 2.0D, -random.nextDouble(),
                        (random.nextDouble() - 0.5D) * 2.0D);
            }
        }
    }

    private void playAnvilSound(Level world, BlockPos pos, SoundEvent soundEvent) {
        world.playSound(null, pos, soundEvent, SoundSource.PLAYERS, 0.5f, 1.0f);
    }

    public static void warp(Entity entity, BlockPos pos, Level world, BlockState state) {
        if (world.getBlockState(pos).getBlock() instanceof WarpPipeBlock && state.getValue(ENTRANCE) && !state.getValue(CLOSED)) {
            Entity passengerEntity = entity.getControllingPassenger();
            if (world.getBlockState(pos).getValue(FACING) == Direction.UP) {
                if (entity instanceof Player) {
                    entity.teleportTo(pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5);
                } else {
                    entity.teleportTo(pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5);
                    if (passengerEntity instanceof Player && !(entity instanceof Boat)) {
                        entity.unRide();
                    }
                }
            }
            if (world.getBlockState(pos).getValue(FACING) == Direction.DOWN) {
                if (entity instanceof Player) {
                    entity.teleportTo(pos.getX() + 0.5, pos.getY() - entity.getBbHeight(), pos.getZ() + 0.5);
                } else {
                    entity.teleportTo(pos.getX() + 0.5, pos.getY() - entity.getBbHeight(), pos.getZ() + 0.5);
                    if (passengerEntity instanceof Player && !(entity instanceof Boat)) {
                        entity.unRide();
                    }
                }
            }
            if (world.getBlockState(pos).getValue(FACING) == Direction.NORTH) {
                if (entity instanceof Player) {
                    entity.teleportTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + entity.getBbWidth() - 1.0);
                } else {
                    entity.teleportTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + entity.getBbWidth() - 1.0);
                    if (passengerEntity instanceof Player && !(entity instanceof Boat)) {
                        entity.unRide();
                    }
                }
            }
            if (world.getBlockState(pos).getValue(FACING) == Direction.SOUTH) {
                if (entity instanceof Player) {
                    entity.teleportTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + entity.getBbWidth() + 1.0);
                } else {
                    entity.teleportTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + entity.getBbWidth() + 1.0);
                    if (passengerEntity instanceof Player && !(entity instanceof Boat)) {
                        entity.unRide();
                    }
                }
            }
            if (world.getBlockState(pos).getValue(FACING) == Direction.EAST) {
                if (entity instanceof Player) {
                    entity.teleportTo(pos.getX() + entity.getBbWidth() + 1.0, pos.getY(), pos.getZ() + 0.5);
                } else {
                    entity.teleportTo(pos.getX() + entity.getBbWidth() + 1.0, pos.getY(), pos.getZ() + 0.5);
                    if (passengerEntity instanceof Player && !(entity instanceof Boat)) {
                        entity.unRide();
                    }
                }
            }
            if (world.getBlockState(pos).getValue(FACING) == Direction.WEST) {
                if (entity instanceof Player) {
                    entity.teleportTo(pos.getX() + entity.getBbWidth() - 1.0, pos.getY(), pos.getZ() + 0.5);
                } else {
                    entity.teleportTo(pos.getX() + entity.getBbWidth() - 1.0, pos.getY(), pos.getZ() + 0.5);
                    if (passengerEntity instanceof Player) {
                        entity.unRide();
                    }
                }
            }
        }
        world.gameEvent(GameEvent.TELEPORT, pos, GameEvent.Context.of(entity));
        world.playSound(null, pos, SoundEvents.ENDERMAN_TELEPORT, SoundSource.BLOCKS, 200.0F, 0.5F);
    }

    @Override
    public void entityInside(BlockState state, Level world, BlockPos pos, Entity entity) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        BlockPos destinationPos = null;

        double entityX = entity.getX();
        double entityY = entity.getY();
        double entityZ = entity.getZ();

        int blockX = pos.getX();
        int blockY = pos.getY();
        int blockZ = pos.getZ();

        if (state.getValue(ENTRANCE) && !state.getValue(CLOSED) && blockEntity instanceof WarpPipeBlockEntity warpPipeBE) {
            destinationPos = warpPipeBE.destinationPos;
            if (entity instanceof Player && entity.portalCooldown == 0 && destinationPos != null) {
                if (state.getValue(FACING) == Direction.UP && entity.isShiftKeyDown() && (entityY > blockY)
                        && (entityX < blockX + 1 && entityX > blockX) && (entityZ < blockZ + 1 && entityZ > blockZ) ) {
                    WarpPipeBlock.warp(entity, destinationPos, world, state);
                    WarpPipeBlock.spawnParticles(entity, world, pos);
                    WarpPipeBlock.spawnParticles(entity, world, destinationPos);
                    entity.setPortalCooldown();
                    entity.portalCooldown = 20;
                }
                if (state.getValue(FACING) == Direction.DOWN && (entityY + entity.getBbHeight() < blockY + 1.0)
                        && (entityX < blockX + 1 && entityX > blockX) && (entityZ < blockZ + 1 && entityZ > blockZ)) {
                    WarpPipeBlock.warp(entity, destinationPos, world, state);
                    WarpPipeBlock.spawnParticles(entity, world, pos);
                    WarpPipeBlock.spawnParticles(entity, world, destinationPos);
                    entity.setPortalCooldown();
                    entity.portalCooldown = 20;
                }
                if (state.getValue(FACING) == Direction.NORTH && entity.getMotionDirection() == Direction.SOUTH
                        && (entityX < blockX + 1 && entityX > blockX) && (entityY >= blockY && entityY < blockY + 0.75) && (entityZ < blockZ)) {
                    WarpPipeBlock.warp(entity, destinationPos, world, state);
                    WarpPipeBlock.spawnParticles(entity, world, pos);
                    WarpPipeBlock.spawnParticles(entity, world, destinationPos);
                    entity.setPortalCooldown();
                    entity.portalCooldown = 20;
                }
                if (state.getValue(FACING) == Direction.SOUTH && entity.getMotionDirection() == Direction.NORTH
                        && (entityX < blockX + 1 && entityX > blockX) && (entityY >= blockY && entityY < blockY + 0.75) && (entityZ > blockZ)) {
                    WarpPipeBlock.warp(entity, destinationPos, world, state);
                    WarpPipeBlock.spawnParticles(entity, world, pos);
                    WarpPipeBlock.spawnParticles(entity, world, destinationPos);
                    entity.setPortalCooldown();
                    entity.portalCooldown = 20;
                }
                if (state.getValue(FACING) == Direction.EAST && entity.getMotionDirection() == Direction.WEST
                        && (entityX > blockX) && (entityY >= blockY && entityY < blockY + 0.75) && (entityZ < blockZ + 1 && entityZ > blockZ)) {
                    WarpPipeBlock.warp(entity, destinationPos, world, state);
                    WarpPipeBlock.spawnParticles(entity, world, pos);
                    WarpPipeBlock.spawnParticles(entity, world, destinationPos);
                    entity.setPortalCooldown();
                    entity.portalCooldown = 20;
                }
                if (state.getValue(FACING) == Direction.WEST && entity.getMotionDirection() == Direction.EAST
                        && (entityX < blockX) && (entityY >= blockY && entityY < blockY + 0.75) && (entityZ < blockZ + 1 && entityZ > blockZ)) {
                    WarpPipeBlock.warp(entity, destinationPos, world, state);
                    WarpPipeBlock.spawnParticles(entity, world, pos);
                    WarpPipeBlock.spawnParticles(entity, world, destinationPos);
                    entity.setPortalCooldown();
                    entity.portalCooldown = 20;
                }
            }
            if (!(entity instanceof Player) && entity.portalCooldown == 0 && destinationPos != null) {
                if (state.getValue(FACING) == Direction.UP && (entityY > blockY)
                        && (entityX < blockX + 1 && entityX > blockX) && (entityZ < blockZ + 1 && entityZ > blockZ)) {
                    WarpPipeBlock.warp(entity, destinationPos, world, state);
                    WarpPipeBlock.spawnParticles(entity, world, pos);
                    WarpPipeBlock.spawnParticles(entity, world, destinationPos);
                    entity.setPortalCooldown();
                    entity.portalCooldown = 20;
                }
                if (state.getValue(FACING) == Direction.DOWN && (entityY + entity.getBbHeight() < blockY + 1.5)
                        && (entityX < blockX + 1 && entityX > blockX) && (entityZ < blockZ + 1 && entityZ > blockZ)) {
                    WarpPipeBlock.warp(entity, destinationPos, world, state);
                    WarpPipeBlock.spawnParticles(entity, world, pos);
                    WarpPipeBlock.spawnParticles(entity, world, destinationPos);
                    entity.setPortalCooldown();
                    entity.portalCooldown = 20;
                }
                if (state.getValue(FACING) == Direction.NORTH
                        && (entityX < blockX + 1 && entityX > blockX) && (entityY >= blockY && entityY < blockY + 0.75) && (entityZ < blockZ)) {
                    WarpPipeBlock.warp(entity, destinationPos, world, state);
                    WarpPipeBlock.spawnParticles(entity, world, pos);
                    WarpPipeBlock.spawnParticles(entity, world, destinationPos);
                    entity.setPortalCooldown();
                    entity.portalCooldown = 20;
                }
                if (state.getValue(FACING) == Direction.SOUTH
                        && (entityX < blockX + 1 && entityX > blockX) && (entityY >= blockY && entityY < blockY + 0.75) && (entityZ > blockZ)) {
                    WarpPipeBlock.warp(entity, destinationPos, world, state);
                    WarpPipeBlock.spawnParticles(entity, world, pos);
                    WarpPipeBlock.spawnParticles(entity, world, destinationPos);
                    entity.setPortalCooldown();
                    entity.portalCooldown = 20;
                }
                if (state.getValue(FACING) == Direction.EAST
                        && (entityX > blockX) && (entityY >= blockY && entityY < blockY + 0.75) && (entityZ < blockZ + 1 && entityZ > blockZ)) {
                    WarpPipeBlock.warp(entity, destinationPos, world, state);
                    WarpPipeBlock.spawnParticles(entity, world, pos);
                    WarpPipeBlock.spawnParticles(entity, world, destinationPos);
                    entity.setPortalCooldown();
                    entity.portalCooldown = 20;
                }
                if (state.getValue(FACING) == Direction.WEST
                        && (entityX < blockX) && (entityY >= blockY && entityY < blockY + 0.75) && (entityZ < blockZ + 1 && entityZ > blockZ)) {
                    WarpPipeBlock.warp(entity, destinationPos, world, state);
                    WarpPipeBlock.spawnParticles(entity, world, pos);
                    WarpPipeBlock.spawnParticles(entity, world, destinationPos);
                    entity.setPortalCooldown();
                    entity.portalCooldown = 20;
                }
            }
        }
    }
}
