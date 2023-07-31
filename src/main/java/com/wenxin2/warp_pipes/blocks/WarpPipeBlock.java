package com.wenxin2.warp_pipes.blocks;

import com.wenxin2.warp_pipes.blocks.entities.WarpPipeBlockEntity;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
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
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.server.ServerLifecycleHooks;

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

        if (!state.getValue(CLOSED)) {
            if (state.getValue(FACING) == Direction.UP) {
                PipeBubblesBlock.updateColumnUp(serverWorld, pos.above(), state);
            } else if (state.getValue(FACING) == Direction.DOWN) {
                PipeBubblesBlock.updateColumnDown(serverWorld, pos.below(), state);
            } else if (state.getValue(FACING) == Direction.NORTH) {
                PipeBubblesBlock.updateColumnNorth(serverWorld, pos.north(), state);
            } else if (state.getValue(FACING) == Direction.SOUTH) {
                PipeBubblesBlock.updateColumnSouth(serverWorld, pos.south(), state);
            } else if (state.getValue(FACING) == Direction.EAST) {
                PipeBubblesBlock.updateColumnEast(serverWorld, pos.east(), state);
            } else if (state.getValue(FACING) == Direction.WEST) {
                PipeBubblesBlock.updateColumnWest(serverWorld, pos.west(), state);
            }
        }
    }

    @Override
    public void onPlace(BlockState state, Level world, BlockPos pos, BlockState neighborState, boolean b) {
        if (!state.getValue(CLOSED)) {
            world.scheduleTick(pos, this, 20);
        }
    }

    @Override
    public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource random) {
        BlockEntity blockEntity = world.getBlockEntity(pos);

        double dx = pos.getX();
        double dy = pos.getY();
        double dz = pos.getZ();

        Block blockAbove = world.getBlockState(pos.above()).getBlock();
        Block blockBelow = world.getBlockState(pos.below()).getBlock();
        Block blockNorth = world.getBlockState(pos.north()).getBlock();
        Block blockSouth = world.getBlockState(pos.south()).getBlock();
        Block blockEast = world.getBlockState(pos.east()).getBlock();
        Block blockWest = world.getBlockState(pos.west()).getBlock();

        if (!state.getValue(CLOSED) && state.getValue(ENTRANCE) && blockEntity instanceof WarpPipeBlockEntity warpPipeBE) {

            if (!warpPipeBE.getPersistentData().isEmpty()) {
                if (state.getValue(FACING) == Direction.UP && blockAbove instanceof LiquidBlock) {
                    if (blockAbove == Blocks.LAVA) {
                        if (random.nextInt(10) == 0) {
                            world.addParticle(ParticleTypes.LAVA, dx + 0.5D, dy + 1.0D, dz + 0.5D, 0.0D, 0.0D, 0.0D);
                        }
                    } else {
                        world.addParticle(ParticleTypes.BUBBLE_COLUMN_UP, dx + 0.5D, dy + 1.15D, dz + 0.5D, 0.0D, 0.4D, 0.0D);
                        world.addParticle(ParticleTypes.BUBBLE_COLUMN_UP, dx + (double) random.nextFloat(),
                                dy + (double) random.nextFloat() + 1.15D, dz + (double) random.nextFloat(), 0.0D, 0.4D, 0.0D);
                    }
                }
                if (state.getValue(FACING) == Direction.DOWN && blockBelow instanceof LiquidBlock) {
                    if (blockBelow == Blocks.LAVA) {
                        if (random.nextInt(10) == 0) {
                            world.addParticle(ParticleTypes.LAVA, dx + 0.5D, dy - 0.5D, dz + 0.05D, 0.0D, 0.0D, 0.0D);
                        }
                    } else {
                        world.addParticle(ParticleTypes.BUBBLE_COLUMN_UP, dx + 0.5D, dy - 1.15D, dz + 0.5D, 0.0D, 0.4D, 0.0D);
                        world.addParticle(ParticleTypes.BUBBLE_COLUMN_UP, dx + (double) random.nextFloat(),
                                dy - (double) random.nextFloat() - 1.15D, dz + (double) random.nextFloat(), 0.0D, 0.4D, 0.0D);
                    }
                }
                if (state.getValue(FACING) == Direction.NORTH && blockNorth instanceof LiquidBlock) {
                    if (blockNorth == Blocks.LAVA) {
                        if (random.nextInt(10) == 0) {
                            world.addParticle(ParticleTypes.LAVA, dx + 0.5D, dy + 0.5D, dz - 0.05D, 0.0D, 0.0D, 0.0D);
                        }
                    } else {
                        world.addParticle(ParticleTypes.BUBBLE_COLUMN_UP, dx + 0.5D, dy + 0.5D, dz - 1.15D, 0.0D, 0.4D, 0.0D);
                        world.addParticle(ParticleTypes.BUBBLE_COLUMN_UP, dx + (double) random.nextFloat(),
                                dy + (double) random.nextFloat(), dz + (double) random.nextFloat() - 1.15D, 0.0D, 0.4D, 0.0D);
                    }
                }
                if (state.getValue(FACING) == Direction.SOUTH && blockSouth instanceof LiquidBlock) {
                    if (blockSouth == Blocks.LAVA) {
                        if (random.nextInt(10) == 0) {
                            world.addParticle(ParticleTypes.LAVA, dx + 0.5D, dy + 0.5D, dz + 1.05D, 0.0D, 0.0D, 0.0D);
                        }
                    } else {
                        world.addParticle(ParticleTypes.BUBBLE_COLUMN_UP, dx + 0.5D, dy + 0.5D, dz + 1.15D, 0.0D, 0.4D, 0.0D);
                        world.addParticle(ParticleTypes.BUBBLE_COLUMN_UP, dx + (double) random.nextFloat(),
                                dy + (double) random.nextFloat(), dz + (double) random.nextFloat() + 1.15D, 0.0D, 0.4D, 0.0D);
                    }
                }
                if (state.getValue(FACING) == Direction.EAST && blockEast instanceof LiquidBlock) {
                    if (blockEast == Blocks.LAVA) {
                        if (random.nextInt(10) == 0) {
                            world.addParticle(ParticleTypes.LAVA, dx + 1.05D, dy + 0.5D, dz + 0.5D, 0.0D, 0.0D, 0.0D);
                        }
                    } else {
                        world.addParticle(ParticleTypes.BUBBLE_COLUMN_UP, dx + 1.15D, dy + 0.5D, dz + 0.5D, 0.0D, 0.4D, 0.0D);
                        world.addParticle(ParticleTypes.BUBBLE_COLUMN_UP, dx + (double) random.nextFloat() + 1.15D,
                                dy + (double) random.nextFloat(), dz + (double) random.nextFloat(), 0.0D, 0.4D, 0.0D);
                    }
                }
                if (state.getValue(FACING) == Direction.WEST && blockWest instanceof LiquidBlock) {
                    if (blockWest == Blocks.LAVA) {
                        if (random.nextInt(10) == 0) {
                            world.addParticle(ParticleTypes.LAVA, dx - 0.05D, dy + 0.5D, dz + 0.5D, 0.0D, 0.0D, 0.0D);
                        }
                    } else {
                        world.addParticle(ParticleTypes.BUBBLE_COLUMN_UP, dx - 1.15D, dy + 0.5D, dz + 0.5D, 0.0D, 0.4D, 0.0D);
                        world.addParticle(ParticleTypes.BUBBLE_COLUMN_UP, dx + (double) random.nextFloat() - 1.15D,
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
    private static final Map<Integer, Boolean> teleportedEntities = new HashMap<>();

    // Method to mark an entity as teleported
    public static void markEntityTeleported(Entity entity) {
        if (entity != null) {
            teleportedEntities.put(entity.getId(), true);
        }
    }

    public static void warp(Entity entity, BlockPos pos, Level world, BlockState state) {
        if (world.getBlockState(pos).getBlock() instanceof WarpPipeBlock && state.getValue(ENTRANCE) && !state.getValue(CLOSED)) {
            Entity passengerEntity = entity.getControllingPassenger();
            if (world.getBlockState(pos).getValue(FACING) == Direction.UP) {
                if (entity instanceof Player) {
                    entity.teleportTo(pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5);
                } else {
                    entity.teleportTo(pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5);
                    if (passengerEntity instanceof Player) {
                        entity.unRide();
                    }
                }
            }
            if (world.getBlockState(pos).getValue(FACING) == Direction.DOWN) {
                if (entity instanceof Player) {
                    entity.teleportTo(pos.getX() + 0.5, pos.getY() - entity.getBbHeight(), pos.getZ() + 0.5);
                } else {
                    entity.teleportTo(pos.getX() + 0.5, pos.getY() - entity.getBbHeight(), pos.getZ() + 0.5);
                    if (passengerEntity instanceof Player) {
                        entity.unRide();
                    }
                }
            }
            if (world.getBlockState(pos).getValue(FACING) == Direction.NORTH) {
                if (entity instanceof Player) {
                    entity.teleportTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + entity.getBbWidth() - 1.0);
                } else {
                    entity.teleportTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + entity.getBbWidth() - 1.0);
                    if (passengerEntity instanceof Player) {
                        entity.unRide();
                    }
                }
            }
            if (world.getBlockState(pos).getValue(FACING) == Direction.SOUTH) {
                if (entity instanceof Player) {
                    entity.teleportTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + entity.getBbWidth() + 1.0);
                } else {
                    entity.teleportTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + entity.getBbWidth() + 1.0);
                    if (passengerEntity instanceof Player) {
                        entity.unRide();
                    }
                }
            }
            if (world.getBlockState(pos).getValue(FACING) == Direction.EAST) {
                if (entity instanceof Player) {
                    entity.teleportTo(pos.getX() + entity.getBbWidth() + 1.0, pos.getY(), pos.getZ() + 0.5);
                } else {
                    entity.teleportTo(pos.getX() + entity.getBbWidth() + 1.0, pos.getY(), pos.getZ() + 0.5);
                    if (passengerEntity instanceof Player) {
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
            WarpPipeBlock.markEntityTeleported(entity);
        }
        world.gameEvent(GameEvent.TELEPORT, pos, GameEvent.Context.of(entity));
        world.playSound(null, pos, SoundEvents.ENDERMAN_TELEPORT, SoundSource.BLOCKS, 1.0F, 0.1F);
    }

    private static final int MAX_PARTICLE_COUNT = 100;

    @Override
    public void entityInside(BlockState state, Level world, BlockPos pos, Entity entity) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        RandomSource random = world.getRandom();
        BlockPos destinationPos = null;

        double entityX = entity.getX();
        double entityY = entity.getY();
        double entityZ = entity.getZ();

        int blockX = pos.getX();
        int blockY = pos.getY();
        int blockZ = pos.getZ();

        // Calculate random motion values within the desired range
        float entityHeight = entity.getBbHeight();
        float entityWidth = entity.getBbWidth();
        float motionRangeMin = 0.1F;
        float motionX = random.nextFloat() * (entityWidth - motionRangeMin) + motionRangeMin;
        float motionY = random.nextFloat() * (entityHeight - motionRangeMin) + motionRangeMin;
        float motionZ = random.nextFloat() * (entityWidth - motionRangeMin) + motionRangeMin;

        // Calculate a scaling factor based on entity dimensions
        float scaleFactor = entityHeight * entityWidth; // You can adjust this formula as needed

        // Calculate the particle count based on the scaling factor
        int particleCount = (int) (scaleFactor * 40); // You can adjust the multiplier to control particle density

        // Ensure particle count does not exceed the maximum limit
        particleCount = Math.min(particleCount, MAX_PARTICLE_COUNT);

        // Restrict motionY to the entity's height
        motionY = Math.max(-entityHeight, Math.min(entityHeight, motionY));

        // Calculate the center point at the bottom of the entity
        double centerX = entityX;
        double centerY = entityY - entityHeight / 2;
        double centerZ = entityZ;

        // Calculate the motion towards the center point
        double motionToCenterX = (centerX - entityX) / particleCount;
        double motionToCenterY = (centerY - entityY) / particleCount;
        double motionToCenterZ = (centerZ - entityZ) / particleCount;

        if (state.getValue(ENTRANCE) && !state.getValue(CLOSED) && blockEntity instanceof WarpPipeBlockEntity warpPipeBE) {
            destinationPos = warpPipeBE.destinationPos;
            int entityId = entity.getId();

            if (!world.isClientSide() && teleportedEntities.getOrDefault(entityId, false)) {
                Collection<ServerPlayer> players = ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers();
                for (ServerPlayer player : players) {
                    for (int i = 0; i < particleCount; ++i) {
                        double posX = entityX + motionToCenterX * i;
                        double posY = entityY + entityHeight + motionToCenterY * i;
                        double posZ = entityZ + motionToCenterZ * i;
                        player.connection.send(new ClientboundLevelParticlesPacket (
                            ParticleTypes.ENCHANT,      // Particle type
                            true,                       // Long distance
                            posX, posY, posZ,           // Position
                            motionX, -motionY, motionZ, // Motion
                            0,                          // Particle data
                            2                           // Particle count
                        ));
                    }
                }
                // Reset the teleport status for the entity
                teleportedEntities.put(entityId, false);
            }

            if (entity instanceof Player && entity.portalCooldown == 0 && destinationPos != null) {
                if (state.getValue(FACING) == Direction.UP && entity.isShiftKeyDown() && (entityY > blockY)
                        && (entityX < blockX + 1 && entityX > blockX) && (entityZ < blockZ + 1 && entityZ > blockZ)) {
                    WarpPipeBlock.warp(entity, destinationPos, world, state);
                    entity.setPortalCooldown();
                    entity.portalCooldown = 20;
                }
                if (state.getValue(FACING) == Direction.DOWN && (entityY + entity.getBbHeight() < blockY + 1.0)
                        && (entityX < blockX + 1 && entityX > blockX) && (entityZ < blockZ + 1 && entityZ > blockZ)) {
                    WarpPipeBlock.warp(entity, destinationPos, world, state);
                    entity.setPortalCooldown();
                    entity.portalCooldown = 20;
                }
                if (state.getValue(FACING) == Direction.NORTH && entity.getMotionDirection() == Direction.SOUTH
                        && (entityX < blockX + 1 && entityX > blockX) && (entityY >= blockY && entityY < blockY + 0.75) && (entityZ < blockZ)) {
                    WarpPipeBlock.warp(entity, destinationPos, world, state);
                    entity.setPortalCooldown();
                    entity.portalCooldown = 20;
                }
                if (state.getValue(FACING) == Direction.SOUTH && entity.getMotionDirection() == Direction.NORTH
                        && (entityX < blockX + 1 && entityX > blockX) && (entityY >= blockY && entityY < blockY + 0.75) && (entityZ > blockZ)) {
                    WarpPipeBlock.warp(entity, destinationPos, world, state);
                    entity.setPortalCooldown();
                    entity.portalCooldown = 20;
                }
                if (state.getValue(FACING) == Direction.EAST && entity.getMotionDirection() == Direction.WEST
                        && (entityX > blockX) && (entityY >= blockY && entityY < blockY + 0.75) && (entityZ < blockZ + 1 && entityZ > blockZ)) {
                    WarpPipeBlock.warp(entity, destinationPos, world, state);
                    entity.setPortalCooldown();
                    entity.portalCooldown = 20;
                }
                if (state.getValue(FACING) == Direction.WEST && entity.getMotionDirection() == Direction.EAST
                        && (entityX < blockX) && (entityY >= blockY && entityY < blockY + 0.75) && (entityZ < blockZ + 1 && entityZ > blockZ)) {
                    WarpPipeBlock.warp(entity, destinationPos, world, state);
                    entity.setPortalCooldown();
                    entity.portalCooldown = 20;
                }
            }
            if (!(entity instanceof Player) && entity.portalCooldown == 0 && destinationPos != null) {
                if (state.getValue(FACING) == Direction.UP && (entityY > blockY)
                        && (entityX < blockX + 1 && entityX > blockX) && (entityZ < blockZ + 1 && entityZ > blockZ)) {
                    WarpPipeBlock.warp(entity, destinationPos, world, state);
                    entity.setPortalCooldown();
                    entity.portalCooldown = 20;
                }
                if (state.getValue(FACING) == Direction.DOWN && (entityY + entity.getBbHeight() < blockY + 1.5)
                        && (entityX < blockX + 1 && entityX > blockX) && (entityZ < blockZ + 1 && entityZ > blockZ)) {
                    WarpPipeBlock.warp(entity, destinationPos, world, state);
                    entity.setPortalCooldown();
                    entity.portalCooldown = 20;
                }
                if (state.getValue(FACING) == Direction.NORTH
                        && (entityX < blockX + 1 && entityX > blockX) && (entityY >= blockY && entityY < blockY + 0.75) && (entityZ < blockZ)) {
                    WarpPipeBlock.warp(entity, destinationPos, world, state);
                    entity.setPortalCooldown();
                    entity.portalCooldown = 20;
                }
                if (state.getValue(FACING) == Direction.SOUTH
                        && (entityX < blockX + 1 && entityX > blockX) && (entityY >= blockY && entityY < blockY + 0.75) && (entityZ > blockZ)) {
                    WarpPipeBlock.warp(entity, destinationPos, world, state);
                    entity.setPortalCooldown();
                    entity.portalCooldown = 20;
                }
                if (state.getValue(FACING) == Direction.EAST
                        && (entityX > blockX) && (entityY >= blockY && entityY < blockY + 0.75) && (entityZ < blockZ + 1 && entityZ > blockZ)) {
                    WarpPipeBlock.warp(entity, destinationPos, world, state);
                    entity.setPortalCooldown();
                    entity.portalCooldown = 20;
                }
                if (state.getValue(FACING) == Direction.WEST
                        && (entityX < blockX) && (entityY >= blockY && entityY < blockY + 0.75) && (entityZ < blockZ + 1 && entityZ > blockZ)) {
                    WarpPipeBlock.warp(entity, destinationPos, world, state);
                    entity.setPortalCooldown();
                    entity.portalCooldown = 20;
                }
            }
        }
    }
}
