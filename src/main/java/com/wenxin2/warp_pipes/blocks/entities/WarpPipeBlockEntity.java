package com.wenxin2.warp_pipes.blocks.entities;

import com.wenxin2.warp_pipes.blocks.WarpPipeBlock;
import com.wenxin2.warp_pipes.init.ModRegistry;
import com.wenxin2.warp_pipes.items.LinkerItem;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.TheEndGatewayBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class WarpPipeBlockEntity extends BlockEntity {
    public static final String DESTINATION_POS = "DestinationPos";
    public static final String WARP_COOLDOWN = "WarpCooldown";
    @Nullable
    public BlockPos destinationPos;
    public int warpCooldown;

    public WarpPipeBlockEntity(final BlockPos pos, final BlockState state)
    {
        this(ModRegistry.WARP_PIPES.get(), pos, state);
    }

    public WarpPipeBlockEntity(final BlockEntityType<?> tileEntity, BlockPos pos, BlockState state) {
        super(tileEntity, pos, state);
    }

    private boolean hasDestinationPos() {
        return this.destinationPos != null;
    }

    public void setDestinationPos(@Nullable BlockPos pos) {
        this.destinationPos = pos;
        if (this.level != null) {
            BlockState blockState = this.getBlockState();
            this.level.setBlock(this.getBlockPos(), blockState.setValue(WarpPipeBlock.ENTRANCE, Boolean.TRUE), 4);
        }
    }

    public void setDestinationDim(@Nullable Level world) {
        if (this.level != null) {
            BlockState blockState = this.getBlockState();
            this.level.setBlock(this.getBlockPos(), blockState.setValue(WarpPipeBlock.ENTRANCE, Boolean.TRUE), 4);
        }
    }

    private boolean hasWarpCooldown() {
        return this.warpCooldown != 0;
    }

    public int getWarpCooldown() {
        return warpCooldown;
    }

    public void setWarpCooldown(int cooldown) {
        this.warpCooldown = cooldown;
    }

    public static void warpCooldownTick(Level world, BlockPos pos, BlockState state, WarpPipeBlockEntity blockEntity) {
        if (blockEntity.getWarpCooldown() > 0) {
            --blockEntity.warpCooldown;
        }
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        this.destinationPos = null;
        if (tag.contains(DESTINATION_POS)) {
            this.setDestinationPos(NbtUtils.readBlockPos(tag.getCompound(DESTINATION_POS)));
        }
        if (tag.contains(WARP_COOLDOWN)) {
            this.setWarpCooldown(getWarpCooldown());
        }
    }

    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        if (this.hasDestinationPos()) {
            tag.put(DESTINATION_POS, NbtUtils.writeBlockPos(this.destinationPos));
        }
        if (this.hasWarpCooldown()) {
            tag.putInt(WARP_COOLDOWN, getWarpCooldown());
        }
    }
}
