package com.wenxin2.warp_pipes.datagen;

import com.wenxin2.warp_pipes.blocks.PipeBubblesBlock;
import com.wenxin2.warp_pipes.blocks.WarpPipeBlock;
import com.wenxin2.warp_pipes.blocks.WaterSpoutBlock;
import com.wenxin2.warp_pipes.registries.DataComponentRegistry;
import com.wenxin2.warp_pipes.WarpPipes;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.CopyComponentsFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.NotNull;

public class BlockLootTableGen extends LootTableProvider {
    public BlockLootTableGen(PackOutput output, CompletableFuture<HolderLookup.Provider> completableFuture) {
        super(output, Set.of(), List.of(new SubProviderEntry(BlockSubProvider::new, LootContextParamSets.BLOCK)), completableFuture);
    }

    public static class BlockSubProvider extends BlockLootSubProvider {
        public BlockSubProvider(HolderLookup.Provider provider) {
            super(Set.of(), FeatureFlags.DEFAULT_FLAGS, provider);
        }

        @NotNull
        @Override
        protected Iterable<Block> getKnownBlocks() {
            return WarpPipes.BLOCKS.getEntries().stream().map(DeferredHolder::value)
                    .filter(block -> block instanceof Block && !(block instanceof PipeBubblesBlock) && !(block instanceof WaterSpoutBlock))
                    .map(block -> (Block) block).toList();
        }

        @Override
        protected void generate() {
            WarpPipes.BLOCKS.getEntries().forEach(deferredHolder -> {
                Block block = deferredHolder.get();
                if (block.getLootTable() != BuiltInLootTables.EMPTY && !(block instanceof PipeBubblesBlock)
                        && !(block instanceof WaterSpoutBlock)) {
                    if (block instanceof WarpPipeBlock)
                        this.add(block, this.createNameableWarpPipeBEDrop(block));
                    else this.dropSelf(block);
                }
            });
        }

        protected LootTable.Builder createNameableWarpPipeBEDrop(Block block) {
            return LootTable.lootTable().withPool(this.applyExplosionCondition(block,
                    LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F))
                            .add(LootItem.lootTableItem(block)
                                    .apply(CopyComponentsFunction.copyComponents(CopyComponentsFunction.Source.BLOCK_ENTITY)
                                            .include(DataComponents.CUSTOM_NAME)
                                            .include(DataComponentRegistry.PIPE_NAME.get()))))
            );
        }
    }
}
