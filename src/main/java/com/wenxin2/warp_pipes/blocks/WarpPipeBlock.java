package com.wenxin2.warp_pipes.blocks;

import com.wenxin2.warp_pipes.blocks.entities.WarpPipeBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.gameevent.GameEvent;

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
            if (entity instanceof Player && entity.isShiftKeyDown()) {
                destinationPos = warpPipeBE.destinationPos;
            }
            if (destinationPos != null /*&& this.getWarpCooldown() == 0*/ && entity.isShiftKeyDown()) {
                WarpPipeBlock.warp((Player) entity, destinationPos, world);
//                this.setWarpCooldown(20);
            }
        }
        super.stepOn(world, pos, state, entity);
    }

    public static void warp(Player player, BlockPos pos, Level world) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof WarpPipeBlockEntity warpPipeBE) {
            player.teleportTo(warpPipeBE.destinationPos.getX() + 0.5, warpPipeBE.destinationPos.getY() + 5, warpPipeBE.destinationPos.getZ() + 0.5);
        }
        world.gameEvent(GameEvent.TELEPORT, pos, GameEvent.Context.of(player));
        world.broadcastEntityEvent(player, (byte)53);
        world.playSound(null, pos, SoundEvents.FOX_TELEPORT, SoundSource.BLOCKS, 2.0F, 1.0F);
    }

//    public int getWarpCooldown() {
//        return warpCooldown;
//    }
//
//    public void setWarpCooldown(int cooldown) {
//        this.warpCooldown = cooldown;
//    }
//
//    @Override
//    public void tick(BlockState state, ServerLevel serverWorld, BlockPos pos, RandomSource source) {
//        if (this.getWarpCooldown() > 0) {
//            --this.warpCooldown;
//        }
//        super.tick(state, serverWorld, pos, source);
//    }
}
