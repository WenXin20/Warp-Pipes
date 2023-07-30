package com.wenxin2.warp_pipes.mixin;

import com.wenxin2.warp_pipes.blocks.WarpPipeBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BubbleColumnBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BubbleColumnBlock.class)
public class BubbleColumnMixin {

    @Inject(at = @At("HEAD"), method = "getColumnState", cancellable = true)
    private static void getColumnState(BlockState state, CallbackInfoReturnable<BlockState> cir) {
        if (state.getBlock() instanceof WarpPipeBlock && state.getValue(BlockStateProperties.FACING) == Direction.UP && !state.getValue(WarpPipeBlock.CLOSED)) {
            cir.setReturnValue(Blocks.BUBBLE_COLUMN.defaultBlockState().setValue(BubbleColumnBlock.DRAG_DOWN, false));
        }
    }

    @Inject(at = @At("HEAD"), method = "canSurvive", cancellable = true)
    public void canSurvive(BlockState state, LevelReader world, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        BlockState stateBelow = world.getBlockState(pos.below());
        if (state.getBlock() instanceof WarpPipeBlock && stateBelow.getValue(BlockStateProperties.FACING) == Direction.UP && !stateBelow.getValue(WarpPipeBlock.CLOSED)) {
            cir.setReturnValue(true);
        }
    }
}
