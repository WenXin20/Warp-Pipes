package com.wenxin2.warp_pipes.blocks;

import com.wenxin2.warp_pipes.blocks.entities.WarpPipeBlockEntity;
import java.util.HashMap;
import java.util.Map;
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
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
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
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.LavaFluid;
import net.minecraft.world.level.material.WaterFluid;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class WarpPipeBlock extends DirectionalBlock implements EntityBlock {
    public static final BooleanProperty ENTRANCE = BooleanProperty.create("entrance");
    public static final BooleanProperty CLOSED = BooleanProperty.create("closed");
    public static final BooleanProperty BUBBLES = BooleanProperty.create("bubbles");

    public WarpPipeBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.UP).setValue(BUBBLES, Boolean.TRUE).setValue(ENTRANCE, Boolean.TRUE).setValue(CLOSED, Boolean.FALSE));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateBuilder) {
        stateBuilder.add(BUBBLES, CLOSED, ENTRANCE, FACING);
    }

    @Override
    public BlockEntity newBlockEntity(final BlockPos pos, final BlockState state)
    {
        return new WarpPipeBlockEntity(pos, state);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter blockGetter, BlockPos pos, CollisionContext collisionContext) {
        if (state.getValue(FACING) == Direction.DOWN) {
            return Block.box(0.0, 0.25, 0, 16, 16, 16);
        } else return Shapes.block();
    }

    @Override
    public VoxelShape getBlockSupportShape(BlockState state, BlockGetter blockGetter, BlockPos pos) {
        return Shapes.block();
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext placeContext) {
        Direction direction = placeContext.getClickedFace();
        return this.defaultBlockState().setValue(FACING, direction).setValue(CLOSED, placeContext.getLevel().hasNeighborSignal(placeContext.getClickedPos()));
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
                    world.setBlock(pos, state.cycle(CLOSED).cycle(BUBBLES), 2);
                    this.playAnvilSound(world, pos, SoundEvents.ANVIL_PLACE);
                }
            }

        }
    }

    @Override
    public void tick(BlockState state, ServerLevel serverWorld, BlockPos pos, RandomSource random) {

        if (state.getValue(FACING) == Direction.UP) {
            PipeBubblesBlock.repeatColumnUp(serverWorld, pos.above(), state);
        } else if (state.getValue(FACING) == Direction.DOWN) {
            PipeBubblesBlock.repeatColumnDown(serverWorld, pos.below(), state);
        } else if (state.getValue(FACING) == Direction.NORTH) {
            PipeBubblesBlock.repeatColumnNorth(serverWorld, pos.north(), state);
        } else if (state.getValue(FACING) == Direction.SOUTH) {
            PipeBubblesBlock.repeatColumnSouth(serverWorld, pos.south(), state);
        } else if (state.getValue(FACING) == Direction.EAST) {
            PipeBubblesBlock.repeatColumnEast(serverWorld, pos.east(), state);
        } else if (state.getValue(FACING) == Direction.WEST) {
            PipeBubblesBlock.repeatColumnWest(serverWorld, pos.west(), state);
        }

        if (state.getValue(CLOSED) && !serverWorld.hasNeighborSignal(pos)) {
            serverWorld.setBlock(pos, state.cycle(CLOSED), 2);
            this.playAnvilSound(serverWorld, pos, SoundEvents.ANVIL_PLACE);
        }
    }

    @Override
    public void onPlace(BlockState state, Level world, BlockPos pos, BlockState neighborState, boolean b) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        BlockPos destinationPos = null;

        if (!state.getValue(CLOSED) && blockEntity instanceof WarpPipeBlockEntity warpPipeBE) {
            destinationPos = warpPipeBE.destinationPos;

            if (destinationPos == null) {
                world.scheduleTick(pos, this, 3);
            }
        }
    }

    @Override
    public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource random) {
        BlockEntity blockEntity = world.getBlockEntity(pos);

        double dx = pos.getX();
        double dy = pos.getY();
        double dz = pos.getZ();

        Fluid fluidAbove = world.getFluidState(pos.above()).getType();
        Fluid fluidBelow = world.getFluidState(pos.below()).getType();
        Fluid fluidNorth = world.getFluidState(pos.north()).getType();
        Fluid fluidSouth = world.getFluidState(pos.south()).getType();
        Fluid fluidEast = world.getFluidState(pos.east()).getType();
        Fluid fluidWest = world.getFluidState(pos.west()).getType();

        Block blockAbove = world.getBlockState(pos.above()).getBlock();
        Block blockBelow = world.getBlockState(pos.below()).getBlock();
        Block blockNorth = world.getBlockState(pos.north()).getBlock();
        Block blockSouth = world.getBlockState(pos.south()).getBlock();
        Block blockEast = world.getBlockState(pos.east()).getBlock();
        Block blockWest = world.getBlockState(pos.west()).getBlock();

        if (!state.getValue(CLOSED) && state.getValue(ENTRANCE) && blockEntity instanceof WarpPipeBlockEntity warpPipeBE) {

            if (warpPipeBE.getPersistentData().isEmpty()) {

                if (state.getValue(FACING) == Direction.UP) {
                    if (fluidAbove instanceof LavaFluid) {
                        if (random.nextInt(10) == 0) {
                            world.addParticle(ParticleTypes.LAVA, dx + 0.5D, dy + 1.0D, dz + 0.5D, 0.0D, 0.0D, 0.0D);
                        }
                    } else if (fluidAbove instanceof WaterFluid || blockAbove instanceof PipeBubblesBlock) {
                        world.addParticle(ParticleTypes.BUBBLE_COLUMN_UP, dx + 0.5D, dy + 1.15D, dz + 0.5D, 0.0D, 0.4D, 0.0D);
                        world.addParticle(ParticleTypes.BUBBLE_COLUMN_UP, dx + (double) random.nextFloat(),
                                dy + (double) random.nextFloat() + 1.15D, dz + (double) random.nextFloat(), 0.0D, 0.4D, 0.0D);
                    }
                }
                if (state.getValue(FACING) == Direction.DOWN) {
                    if (fluidBelow instanceof LavaFluid) {
                        if (random.nextInt(10) == 0) {
                            world.addParticle(ParticleTypes.LAVA, dx + 0.5D, dy - 0.5D, dz + 0.05D, 0.0D, 0.0D, 0.0D);
                        }
                    } else if (fluidBelow instanceof WaterFluid || blockBelow instanceof PipeBubblesBlock) {
                        world.addParticle(ParticleTypes.BUBBLE, dx + 0.5D, dy - 1.15D, dz + 0.5D, 0.0D, -0.4D, 0.0D);
                        world.addParticle(ParticleTypes.BUBBLE, dx + (double) random.nextFloat(),
                                dy - (double) random.nextFloat() - 1.15D, dz + (double) random.nextFloat(), 0.0D, -0.4D, 0.0D);
                    }
                }
                if (state.getValue(FACING) == Direction.NORTH) {
                    if (fluidNorth instanceof LavaFluid) {
                        if (random.nextInt(10) == 0) {
                            world.addParticle(ParticleTypes.LAVA, dx + 0.5D, dy + 0.5D, dz - 0.05D, 0.0D, 0.0D, 0.0D);
                        }
                    } else if (fluidNorth instanceof WaterFluid || blockNorth instanceof PipeBubblesBlock) {
                        world.addParticle(ParticleTypes.BUBBLE, dx + 0.5D, dy + 0.5D, dz - 1.15D, 0.0D, 0.4D, -1.5D);
                        world.addParticle(ParticleTypes.BUBBLE, dx + (double) random.nextFloat(),
                                dy + (double) random.nextFloat(), dz + (double) random.nextFloat() - 1.15D, 0.0D, 0.4D, -1.5D);
                    }
                }
                if (state.getValue(FACING) == Direction.SOUTH) {
                    if (fluidSouth instanceof LavaFluid) {
                        if (random.nextInt(10) == 0) {
                            world.addParticle(ParticleTypes.LAVA, dx + 0.5D, dy + 0.5D, dz + 1.05D, 0.0D, 0.0D, 0.0D);
                        }
                    } else if (fluidSouth instanceof WaterFluid || blockSouth instanceof PipeBubblesBlock) {
                        world.addParticle(ParticleTypes.BUBBLE, dx + 0.5D, dy + 0.5D, dz + 1.15D, 0.0D, 0.4D, 0.0D);
                        world.addParticle(ParticleTypes.BUBBLE, dx + (double) random.nextFloat(),
                                dy + (double) random.nextFloat(), dz + (double) random.nextFloat() + 1.15D, 0.0D, 0.4D, 0.0D);
                    }
                }
                if (state.getValue(FACING) == Direction.EAST) {
                    if (fluidEast instanceof LavaFluid) {
                        if (random.nextInt(10) == 0) {
                            world.addParticle(ParticleTypes.LAVA, dx + 1.05D, dy + 0.5D, dz + 0.5D, 0.0D, 0.0D, 0.0D);
                        }
                    } else if (fluidEast instanceof WaterFluid || blockEast instanceof PipeBubblesBlock) {
                        world.addParticle(ParticleTypes.BUBBLE, dx + 1.15D, dy + 0.5D, dz + 0.5D, 0.0D, 0.4D, 0.0D);
                        world.addParticle(ParticleTypes.BUBBLE, dx + (double) random.nextFloat() + 1.15D,
                                dy + (double) random.nextFloat(), dz + (double) random.nextFloat(), 0.0D, 0.4D, 0.0D);
                    }
                }
                if (state.getValue(FACING) == Direction.WEST) {
                    if (fluidWest instanceof LavaFluid) {
                        if (random.nextInt(10) == 0) {
                            world.addParticle(ParticleTypes.LAVA, dx - 0.05D, dy + 0.5D, dz + 0.5D, 0.0D, 0.0D, 0.0D);
                        }
                    } else if (fluidWest instanceof WaterFluid || blockWest instanceof PipeBubblesBlock) {
                        world.addParticle(ParticleTypes.BUBBLE, dx - 1.15D, dy + 0.5D, dz + 0.5D, 0.0D, 0.4D, 0.0D);
                        world.addParticle(ParticleTypes.BUBBLE, dx + (double) random.nextFloat() - 1.15D,
                                dy + (double) random.nextFloat(), dz + (double) random.nextFloat(), 0.0D, 0.4D, 0.0D);
                    }
                }
            }
        }
        super.animateTick(state, world, pos, random);
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

    private void playAnvilSound(Level world, BlockPos pos, SoundEvent soundEvent) {
        world.playSound(null, pos, soundEvent, SoundSource.PLAYERS, 0.5f, 1.0f);
    }

    // Store a map to track whether entities have teleported or not
    public static final Map<Integer, Boolean> teleportedEntities = new HashMap<>();

    // Method to mark an entity as teleported
    public static void markEntityTeleported(Entity entity) {
        if (entity != null) {
            teleportedEntities.put(entity.getId(), true);
        }
    }

    public static void warp(Entity entity, BlockPos pos, Level world, BlockState state) {
        if (world.getBlockState(pos).getBlock() instanceof WarpPipeBlock && !state.getValue(CLOSED)) {
            Entity passengerEntity = entity.getControllingPassenger();

            if (state.getBlock() instanceof ClearWarpPipeBlock && !state.getValue(ENTRANCE)) {
                if (entity instanceof Player) {
                    entity.teleportTo(pos.getX() + 0.5, pos.getY() - 1.0, pos.getZ() + 0.5);
                } else {
                    entity.teleportTo(pos.getX() + 0.5, pos.getY() - 1.0, pos.getZ() + 0.5);
                    if (passengerEntity instanceof Player) {
                        entity.unRide();
                    }
                }
            }
            if (world.getBlockState(pos).getValue(FACING) == Direction.UP && state.getValue(ENTRANCE)) {
                if (entity instanceof Player) {
                    entity.teleportTo(pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5);
                } else {
                    entity.teleportTo(pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5);
                    if (passengerEntity instanceof Player) {
                        entity.unRide();
                    }
                }
            }
            if (world.getBlockState(pos).getValue(FACING) == Direction.DOWN && state.getValue(ENTRANCE)) {
                if (entity instanceof Player) {
                    entity.teleportTo(pos.getX() + 0.5, pos.getY() - entity.getBbHeight(), pos.getZ() + 0.5);
                } else {
                    entity.teleportTo(pos.getX() + 0.5, pos.getY() - entity.getBbHeight(), pos.getZ() + 0.5);
                    if (passengerEntity instanceof Player) {
                        entity.unRide();
                    }
                }
            }
            if (world.getBlockState(pos).getValue(FACING) == Direction.NORTH && state.getValue(ENTRANCE)) {
                if (entity instanceof Player) {
                    entity.teleportTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + entity.getBbWidth() - 1.0);
                } else {
                    entity.teleportTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + entity.getBbWidth() - 1.0);
                    if (passengerEntity instanceof Player) {
                        entity.unRide();
                    }
                }
            }
            if (world.getBlockState(pos).getValue(FACING) == Direction.SOUTH && state.getValue(ENTRANCE)) {
                if (entity instanceof Player) {
                    entity.teleportTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + entity.getBbWidth() + 1.0);
                } else {
                    entity.teleportTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + entity.getBbWidth() + 1.0);
                    if (passengerEntity instanceof Player) {
                        entity.unRide();
                    }
                }
            }
            if (world.getBlockState(pos).getValue(FACING) == Direction.EAST && state.getValue(ENTRANCE)) {
                if (entity instanceof Player) {
                    entity.teleportTo(pos.getX() + entity.getBbWidth() + 1.0, pos.getY(), pos.getZ() + 0.5);
                } else {
                    entity.teleportTo(pos.getX() + entity.getBbWidth() + 1.0, pos.getY(), pos.getZ() + 0.5);
                    if (passengerEntity instanceof Player) {
                        entity.unRide();
                    }
                }
            }
            if (world.getBlockState(pos).getValue(FACING) == Direction.WEST && state.getValue(ENTRANCE)) {
                if (entity instanceof Player) {
                    entity.teleportTo(pos.getX() + entity.getBbWidth() - 1.0, pos.getY(), pos.getZ() + 0.5);
                } else {
                    entity.teleportTo(pos.getX() + entity.getBbWidth() - 1.0, pos.getY(), pos.getZ() + 0.5);
                    if (passengerEntity instanceof Player) {
                        entity.unRide();
                    }
                }
            }
            WarpPipeBlock.markEntityTeleported(entity);
        }
        world.gameEvent(GameEvent.TELEPORT, pos, GameEvent.Context.of(entity));
        world.playSound(null, pos, SoundEvents.ENDERMAN_TELEPORT, SoundSource.BLOCKS, 1.0F, 0.1F);
    }

    public static void spawnParticles(Entity entity, Level world) {
        RandomSource random = world.getRandom();
        for(int i = 0; i < 40; ++i) {
            world.addParticle(ParticleTypes.ENCHANT,
                    entity.getRandomX(0.5D), entity.getRandomY(), entity.getRandomZ(0.5D),
                    (random.nextDouble() - 0.5D) * 2.0D, -random.nextDouble(),
                    (random.nextDouble() - 0.5D) * 2.0D);
        }
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

        if (!state.getValue(CLOSED) && blockEntity instanceof WarpPipeBlockEntity warpPipeBE) {
            destinationPos = warpPipeBE.destinationPos;
            int entityId = entity.getId();

            if (world.isClientSide() && WarpPipeBlock.teleportedEntities.getOrDefault(entityId, false)) {
                WarpPipeBlock.spawnParticles(entity, world);
                if (destinationPos != null) {
                    WarpPipeBlock.spawnParticles(entity, world);
                }
                // Reset the teleport status for the entity
                WarpPipeBlock.teleportedEntities.put(entityId, false);
            }

            if (entity instanceof Player && entity.portalCooldown == 0 && destinationPos != null) {
                if (state.getValue(FACING) == Direction.DOWN && (entityY + entity.getBbHeight() < blockY + 1.0)
                        && (entityX < blockX + 1 && entityX > blockX) && (entityZ < blockZ + 1 && entityZ > blockZ)) {
                    WarpPipeBlock.warp(entity, destinationPos, world, state);
                    entity.setPortalCooldown();
                    entity.portalCooldown = 20;
                }
            }
            if (!(entity instanceof Player) && entity.portalCooldown == 0 && destinationPos != null) {
                if (state.getValue(FACING) == Direction.DOWN && (entityY + entity.getBbHeight() < blockY + 1.5)
                        && (entityX < blockX + 1 && entityX > blockX) && (entityZ < blockZ + 1 && entityZ > blockZ)) {
                    WarpPipeBlock.warp(entity, destinationPos, world, state);
                    entity.setPortalCooldown();
                    entity.portalCooldown = 20;
                }
            }
        }
    }
}
