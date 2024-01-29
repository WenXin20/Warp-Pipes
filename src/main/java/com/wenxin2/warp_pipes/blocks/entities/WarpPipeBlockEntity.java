package com.wenxin2.warp_pipes.blocks.entities;

import com.wenxin2.warp_pipes.init.ModRegistry;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class WarpPipeBlockEntity extends BlockEntity {
    public static final String WARP_POS = "WarpPos";
    public static final String WARP_DIMENSION = "Dimension";
    @Nullable
    public BlockPos destinationPos;
    public String dimensionTag;
    public int posX;
    public int posY;
    public int posZ;

    public WarpPipeBlockEntity(final BlockPos pos, final BlockState state)
    {
        this(ModRegistry.WARP_PIPES.get(), pos, state);
    }

    public WarpPipeBlockEntity(final BlockEntityType<?> tileEntity, BlockPos pos, BlockState state) {
        super(tileEntity, pos, state);
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
        if (this.level != null && dimension != null && this.destinationPos != null && this.level.getServer() != null) {
            this.level.setBlock(this.getBlockPos(), this.getBlockState(), 4);

            // Change dimension
            ServerLevel targetWorld = this.level.getServer().getLevel(dimension);
            if (targetWorld != null) {
                BlockEntity targetBlockEntity = targetWorld.getBlockEntity(this.destinationPos);

                if (targetBlockEntity instanceof WarpPipeBlockEntity warpPipeBETarget) {
                    warpPipeBETarget.setDestinationPos(this.getBlockPos());
                }
            }
        }
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        this.destinationPos = null;
        this.dimensionTag = tag.getString(WARP_DIMENSION);

        if (tag.contains(WARP_POS)) {
            this.setDestinationPos(NbtUtils.readBlockPos(tag.getCompound(WARP_POS)));
        }
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        if (this.hasDestinationPos()) {
            tag.put(WARP_POS, NbtUtils.writeBlockPos(this.destinationPos));
        }

        if (this.dimensionTag != null) {
            tag.putString(WARP_DIMENSION, this.dimensionTag);
        }
    }
}
