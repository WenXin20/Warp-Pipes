package com.wenxin2.warp_pipes.blocks.behaviors;

import com.wenxin2.warp_pipes.registries.ModRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.world.item.DispensibleContainerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.NotNull;

public class DispenserBehaviors {
    public static void register() {
        DispenseItemBehavior dispenseBucketBehavior = new DefaultDispenseItemBehavior() {
            private final DefaultDispenseItemBehavior defaultDispenseItemBehavior = new DefaultDispenseItemBehavior();

            @NotNull
            @Override
            public ItemStack execute(BlockSource blockSource, ItemStack stack) {
                DispensibleContainerItem dispensibleContainerItem = (DispensibleContainerItem) stack.getItem();
                BlockPos pos = blockSource.pos().relative(blockSource.state().getValue(DispenserBlock.FACING));
                Level level = blockSource.level();

                if (dispensibleContainerItem.emptyContents(null, level, pos, null, stack)) {
                    dispensibleContainerItem.checkExtraContent(null, level, stack, pos);
                    return this.consumeWithRemainder(blockSource, stack, new ItemStack(Items.BUCKET));
                } else return this.defaultDispenseItemBehavior.dispense(blockSource, stack);
            }
        };

        DispenseItemBehavior bucketBehavior = DispenserBlock.DISPENSER_REGISTRY.get(Items.BUCKET);
        DispenserBlock.registerBehavior(Items.BUCKET, new DefaultDispenseItemBehavior() {
            @NotNull
            @Override
            public ItemStack execute(BlockSource blockSource, ItemStack stack) {
                LevelAccessor level = blockSource.level();
                BlockPos pos = blockSource.pos().relative(
                        blockSource.state().getValue(DispenserBlock.FACING));
                BlockState state = level.getBlockState(pos);

                if (!(state.getBlock() instanceof BucketPickup bucketPickup))
                    return bucketBehavior.dispense(blockSource, stack);

                ItemStack newStack;
                if (state.is(ModRegistry.PIPE_BUBBLES))
                    newStack = new ItemStack(Items.WATER_BUCKET);
                else if (state.is(ModRegistry.WATER_SPOUT))
                    newStack = new ItemStack(Items.WATER_BUCKET);
                else return super.execute(blockSource, stack);

                newStack.applyComponents(stack.getComponents());

                ItemStack vanillaResult =
                        bucketPickup.pickupBlock(null, level, pos, state);

                if (vanillaResult.isEmpty())
                    return bucketBehavior.dispense(blockSource, stack);

                level.gameEvent(null, GameEvent.FLUID_PICKUP, pos);

                return this.consumeWithRemainder(blockSource, stack, newStack);
            }
        });
    }
}