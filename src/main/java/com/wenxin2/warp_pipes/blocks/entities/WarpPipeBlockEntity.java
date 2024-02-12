package com.wenxin2.warp_pipes.blocks.entities;

import com.wenxin2.warp_pipes.blocks.WarpPipeBlock;
import com.wenxin2.warp_pipes.blocks.WaterSpoutBlock;
import com.wenxin2.warp_pipes.init.ModRegistry;
import com.wenxin2.warp_pipes.init.SoundRegistry;
import com.wenxin2.warp_pipes.inventory.WarpPipeMenu;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class WarpPipeBlockEntity extends BlockEntity {
    public static final String WARP_POS = "WarpPos";
    public static final String WARP_DIMENSION = "Dimension";
    public static final String SPOUT_HEIGHT = "SpoutHeight";

    @Nullable
    public BlockPos destinationPos;
    public static BlockPos blockPos;
    public String dimensionTag;
    public int spoutHeight = 4;

    public WarpPipeBlockEntity(final BlockPos pos, final BlockState state)
    {
        this(ModRegistry.WARP_PIPE_BLOCK_ENTITY.get(), pos, state);
    }

    public WarpPipeBlockEntity(final BlockEntityType<?> tileEntity, BlockPos pos, BlockState state) {
        super(tileEntity, pos, state);
        blockPos = this.getBlockPos();
    }

    public boolean hasDestinationPos() {
        return this.destinationPos != null;
    }

    public void setDestinationPos(@Nullable BlockPos pos) {
        this.destinationPos = pos;
        if (this.level != null && pos != null) {
            BlockState state = this.getBlockState();
            this.level.setBlock(this.getBlockPos(), state, 4);
        }
    }

    @Nullable
    public BlockPos getDestinationPos() {
        if (this.destinationPos != null) {
            return this.destinationPos;
        }
        return null;
    }

    public void setBlockPos(@Nullable BlockPos pos) {
        blockPos = pos;
        if (this.level != null && pos != null) {
            BlockState state = this.getBlockState();
            this.level.setBlock(this.getBlockPos(), state, 4);
        }
    }

    @Nullable
    public static BlockPos getPos() {
        if (blockPos != null) {
            return blockPos;
        }
        return null;
    }

    @Nullable
    public ResourceKey<Level> getDestinationDim() {
        if (dimensionTag != null) {
            ResourceLocation location = ResourceLocation.tryParse(dimensionTag);
            if (location != null) {
                return ResourceKey.create(Registries.DIMENSION, location);
            }
        }
        return null;
    }


    public void setDestinationDim(@Nullable ResourceKey<Level> dimension) {
        if (dimension != null) {
            this.dimensionTag = dimension.location().toString();
        }

        if (this.level != null) {
            this.level.setBlock(this.getBlockPos(), this.getBlockState(), 4);
        }
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        blockPos = NbtUtils.readBlockPos(tag.getCompound(WARP_POS));
//        System.out.println("SetDestPos: " +  this.destinationPos);

        if (tag.contains(WARP_POS)) {
            this.destinationPos = NbtUtils.readBlockPos(tag.getCompound(WARP_POS));
            this.setDestinationPos(this.destinationPos);
//            System.out.println("SetWarpPos: " + this.destinationPos);
        }

        if (tag.contains(WARP_DIMENSION))
            this.dimensionTag = tag.getString(WARP_DIMENSION);

        if (tag.getInt(SPOUT_HEIGHT) != this.spoutHeight)
            this.spoutHeight = tag.getInt(SPOUT_HEIGHT);
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        if (this.hasDestinationPos() && this.destinationPos != null) {
            tag.put(WARP_POS, NbtUtils.writeBlockPos(this.destinationPos));
//            System.out.println("WarpPos: " + NbtUtils.writeBlockPos(this.destinationPos));
        }

        if (this.dimensionTag != null) {
            tag.putString(WARP_DIMENSION, this.dimensionTag);
//            System.out.println("WarpDim: " + this.dimensionTag);
        }

        if (tag.getInt(SPOUT_HEIGHT) != this.spoutHeight)
            tag.putInt(SPOUT_HEIGHT, this.spoutHeight);
        System.out.println("SaveSpoutHeightTag: " + tag.get(SPOUT_HEIGHT) + " " + this.getBlockPos());
    }

    public void closePipe(ServerPlayer player) {
        if (this.level != null && player.containerMenu instanceof WarpPipeMenu) {
            BlockState state = this.level.getBlockState(((WarpPipeMenu) player.containerMenu).getBlockPos());
            BlockPos menuPos = ((WarpPipeMenu) player.containerMenu).getBlockPos();
            if (state.getValue(WarpPipeBlock.CLOSED)) {
                this.level.setBlock(menuPos, state.setValue(WarpPipeBlock.CLOSED, Boolean.FALSE), 3);
                this.playSound(this.level, menuPos, SoundRegistry.PIPE_OPENS.get(), SoundSource.BLOCKS, 1.0F, 0.15F);
            } else {
                this.level.setBlock(menuPos, state.setValue(WarpPipeBlock.CLOSED, Boolean.TRUE)
                        .setValue(WarpPipeBlock.WATER_SPOUT, Boolean.FALSE)
                        .setValue(WarpPipeBlock.BUBBLES, Boolean.FALSE), 3);
                this.playSound(this.level, menuPos, SoundRegistry.PIPE_CLOSES.get(), SoundSource.BLOCKS, 1.0F, 0.5F);
            }
        }
    }

    public void toggleWaterSpout(ServerPlayer player) {
        if (this.level != null && player.containerMenu instanceof WarpPipeMenu) {
            BlockState state = this.level.getBlockState(((WarpPipeMenu) player.containerMenu).getBlockPos());
            BlockPos menuPos = ((WarpPipeMenu) player.containerMenu).getBlockPos();
            if (state.getValue(WarpPipeBlock.WATER_SPOUT)) {
                this.level.setBlock(menuPos, state.setValue(WarpPipeBlock.WATER_SPOUT, Boolean.FALSE), 3);
                this.playSound(this.level, menuPos, SoundRegistry.WATER_SPOUT_BREAK.get(), SoundSource.BLOCKS, 1.0F, 0.15F);
            } else {
                this.level.setBlock(menuPos, state.setValue(WarpPipeBlock.WATER_SPOUT, Boolean.TRUE), 3);
                this.playSound(this.level, menuPos, SoundRegistry.WATER_SPOUT_PLACE.get(), SoundSource.BLOCKS, 1.0F, 0.5F);
            }
        }
    }

    public void waterSpoutHeight(ServerPlayer player, int spoutHeight) {
        if (this.level != null && player.containerMenu instanceof WarpPipeMenu) {
            BlockState state = this.level.getBlockState(((WarpPipeMenu) player.containerMenu).getBlockPos());
            BlockPos menuPos = ((WarpPipeMenu) player.containerMenu).getBlockPos();
            if (this.level.getBlockState(this.getBlockPos()).getBlock() instanceof WarpPipeBlock) {
                this.setSpoutHeight(spoutHeight, this.getBlockPos());
            }
        }
    }

    public void setSpoutHeight(int spoutHeight, BlockPos pos) {
        Level world = this.level;

        if (world != null) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            pos = this.getBlockPos();
            if (blockEntity instanceof WarpPipeBlockEntity pipeBlockEntity) {
                if (world.getBlockState(pos.above()).getBlock() instanceof WaterSpoutBlock)
                    world.destroyBlock(pos.above(), true);
                this.spoutHeight = spoutHeight;
                this.setChanged();
                System.out.println("ThisSpoutHeight: " + this.spoutHeight);
                System.out.println("SpoutHeight: " + spoutHeight);
//                System.out.println("SpoutHeightTag: " + pipeBlockEntity.getUpdateTag().get(SPOUT_HEIGHT));
                pipeBlockEntity.setChanged();
            }
        }
    }

    public void togglePipeBubbles(ServerPlayer player) {
        if (this.level != null && player.containerMenu instanceof WarpPipeMenu) {
            BlockState state = this.level.getBlockState(((WarpPipeMenu) player.containerMenu).getBlockPos());
            BlockPos menuPos = ((WarpPipeMenu) player.containerMenu).getBlockPos();
            if (state.getValue(WarpPipeBlock.BUBBLES)) {
                this.level.setBlock(menuPos, state.setValue(WarpPipeBlock.BUBBLES, Boolean.FALSE), 3);
                this.playSound(this.level, menuPos, SoundEvents.BUBBLE_COLUMN_BUBBLE_POP, SoundSource.BLOCKS, 1.0F, 0.15F);
            } else {
                this.level.setBlock(menuPos, state.setValue(WarpPipeBlock.BUBBLES, Boolean.TRUE), 3);
                this.playSound(this.level, menuPos, SoundEvents.BUBBLE_COLUMN_UPWARDS_AMBIENT, SoundSource.BLOCKS, 1.0F, 0.5F);
            }
        }
    }

    public void playSound(Level world, BlockPos pos, SoundEvent soundEvent, SoundSource source, float volume, float pitch) {
        world.playSound(null, pos, soundEvent, source, volume, pitch);
    }

    public void sendData() {
        if (level instanceof ServerLevel serverWorld)
            serverWorld.getChunkSource().blockChanged(getBlockPos());
    }
}
