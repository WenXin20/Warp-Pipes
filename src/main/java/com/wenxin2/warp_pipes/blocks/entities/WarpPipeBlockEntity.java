package com.wenxin2.warp_pipes.blocks.entities;

import com.wenxin2.warp_pipes.init.ModRegistry;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class WarpPipeBlockEntity extends BlockEntity {
    public static final String WARP_POS = "WarpPos";
    public static final String WARP_DIMENSION = "Dimension";
    public static final String POS_X = "X";
    public static final String POS_Y = "Y";
    public static final String POS_Z = "Z";

    @Nullable
    public static BlockPos destinationPos;
    public String dimensionTag;

    public WarpPipeBlockEntity(final BlockPos pos, final BlockState state)
    {
        this(ModRegistry.WARP_PIPES.get(), pos, state);
    }

    public WarpPipeBlockEntity(final BlockEntityType<?> tileEntity, BlockPos pos, BlockState state) {
        super(tileEntity, pos, state);
    }

    public boolean hasDestinationPos() {
        return destinationPos != null;
    }

    public void setDestinationPos(@Nullable BlockPos pos) {
        destinationPos = pos;
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

        if (this.level != null) {
            this.level.setBlock(this.getBlockPos(), this.getBlockState(), 4);
        }
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);

        destinationPos = new BlockPos(tag.getInt(POS_X), tag.getInt(POS_Y), tag.getInt(POS_Z));
        this.dimensionTag = tag.getString(WARP_DIMENSION);
//        System.out.println("SetDestPos: " +  destinationPos);

        if (tag.contains(WARP_POS)) {
            this.setDestinationPos(destinationPos);
//            System.out.println("SetWarpPos: " + destinationPos);
        }
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        if (this.hasDestinationPos() && destinationPos != null) {
            tag.putInt(POS_X, destinationPos.getX());
            tag.putInt(POS_Y, destinationPos.getY());
            tag.putInt(POS_Z, destinationPos.getZ());
            tag.put(WARP_POS, NbtUtils.writeBlockPos(destinationPos));
//            System.out.println("WarpPos: " + NbtUtils.writeBlockPos(destinationPos));
        }

        if (this.dimensionTag != null) {
            tag.putString(WARP_DIMENSION, this.dimensionTag);
//            System.out.println("WarpDim: " + this.dimensionTag);
        }
    }
}
