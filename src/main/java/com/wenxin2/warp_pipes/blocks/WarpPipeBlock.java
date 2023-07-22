package com.wenxin2.warp_pipes.blocks;

import com.wenxin2.warp_pipes.blocks.entities.WarpPipeBlockEntity;
import com.wenxin2.warp_pipes.init.ModRegistry;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.TheEndGatewayBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraftforge.registries.RegistryObject;

import static net.minecraft.world.level.block.BaseEntityBlock.createTickerHelper;

public class WarpPipeBlock extends DirectionalBlock implements EntityBlock {
    public static final BooleanProperty ENTRANCE = BooleanProperty.create("entrance");
    public int warpCooldown;

    public WarpPipeBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.UP).setValue(ENTRANCE, Boolean.TRUE));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateBuilder) {
        stateBuilder.add(ENTRANCE, FACING);
    }

    @Override
    public BlockEntity newBlockEntity(final BlockPos pos, final BlockState state)
    {
        return new WarpPipeBlockEntity(pos, state);
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

    @Override
    public void stepOn(Level world, BlockPos pos, BlockState state, Entity entity) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        BlockPos destinationPos = null;
        if (state.getValue(ENTRANCE) && blockEntity instanceof WarpPipeBlockEntity warpPipeBE) {
            destinationPos = warpPipeBE.destinationPos;
            if (!(entity instanceof Player) && warpPipeBE.getWarpCooldown() == 0 && !entity.isOnPortalCooldown() && destinationPos != null){
                WarpPipeBlock.warp(entity, destinationPos, world, state);
                entity.setPortalCooldown();
//                entity.getPortalWaitTime();
                warpPipeBE.setWarpCooldown(100);
            }
            if (entity instanceof Player && !entity.isOnPortalCooldown() && destinationPos != null && entity.isShiftKeyDown()) {
                WarpPipeBlock.warp(entity, destinationPos, world, state);
                entity.setPortalCooldown();
                warpPipeBE.setWarpCooldown(100);
            }
//            if (this.getWarpCooldown() > 0) {
//                --this.warpCooldown;
//            }
//            if (entity.isOnPortalCooldown()) {
//                --entity.portalCooldown();
//            }
        }
        super.stepOn(world, pos, state, entity);
    }

    protected static final RandomSource random = RandomSource.create();
    public static void warp(Entity entity, BlockPos pos, Level world, BlockState state) {
        entity.teleportTo(pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5);
        if (entity instanceof Player) {
            entity.unRide();
        }
        world.gameEvent(GameEvent.TELEPORT, pos, GameEvent.Context.of(entity));
        world.playSound(null, pos, SoundEvents.FOX_TELEPORT, SoundSource.BLOCKS, 2.0F, 1.0F);

        if (world.isClientSide) {
            for(int i = 0; i < 10; ++i) {
                world.addParticle(ParticleTypes.PORTAL,
                        entity.getRandomX(0.5D), entity.getRandomY() - 0.25D, entity.getRandomZ(0.5D),
                        (random.nextDouble() - 0.5D) * 2.0D, -random.nextDouble(),
                        (random.nextDouble() - 0.5D) * 2.0D);
            }
        }
    }

//    public int getWarpCooldown() {
//        return warpCooldown;
//    }
//
//    public void setWarpCooldown(int cooldown) {
//        this.warpCooldown = cooldown;
//    }

//    @Override
//    public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource source) {
//        BlockEntity blockEntity = world.getBlockEntity(pos);
//        if (blockEntity instanceof WarpPipeBlockEntity warpPipeBE) {
//            if (warpPipeBE.getWarpCooldown() > 0) {
//                warpPipeBE.zeroWarpCooldown();
//            }
//        }
//        super.animateTick(state, world, pos, source);
//    }
    @org.jetbrains.annotations.Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> blockEntityType) {
        return createTickerHelper(blockEntityType, ModRegistry.WARP_PIPES.get(), WarpPipeBlockEntity::warpCooldownTick);
    }
//    @Nullable
//    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> blockEntityType) {
//        BlockEntity blockEntity = world.getBlockEntity(pos);
//        if (blockEntity instanceof WarpPipeBlockEntity warpPipeBE) {
//            return warpPipeBE.warpCooldownTick();
//        }
//        return createTickerHelper(world, ModRegistry.WARP_PIPES, world.isClientSide ? WarpPipeBlockEntity::beamAnimationTick : WarpPipeBlockEntity::warpCooldownTick);
//    }

//    @Nullable
//    protected static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> createTickerHelper(BlockEntityType<A> blockEntityTypeA, BlockEntityType<E> blockEntityTypeE, BlockEntityTicker<? super E> blockEntityTicker) {
//        return blockEntityTypeE == blockEntityTypeA ? (BlockEntityTicker<A>)blockEntityTicker : null;
//    }
}
