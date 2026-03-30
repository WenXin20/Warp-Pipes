package com.wenxin2.warp_pipes.datagen;

import com.wenxin2.warp_pipes.WarpPipes;
import com.wenxin2.warp_pipes.blocks.ClearWarpPipeBlock;
import com.wenxin2.warp_pipes.blocks.WarpPipeBlock;
import com.wenxin2.warp_pipes.blocks.WaterSpoutBlock;
import com.wenxin2.warp_pipes.registries.ModRegistry;
import java.util.Map;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.client.model.generators.VariantBlockStateBuilder;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredBlock;

public class BlockStateGen extends BlockStateProvider {
    public BlockStateGen(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, WarpPipes.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        String waterSpoutName = BuiltInRegistries.BLOCK.getKey(ModRegistry.WATER_SPOUT.get()).getPath();

        this.pipeBubblesModel(ModRegistry.PIPE_BUBBLES.get());
        this.waterSpoutModel(ModRegistry.WATER_SPOUT.get(), modLoc("block/" + waterSpoutName + "_flow"),
                modLoc("block/" + waterSpoutName + "_still"), modLoc("block/" + waterSpoutName + "_splash"));

        for (Map.Entry<DyeColor, DeferredBlock<Block>> entry : ModRegistry.PIPE_JUNCTION.entrySet()) {
            String blockName = BuiltInRegistries.BLOCK.getKey(entry.getValue().get()).getPath();
            ResourceLocation texture = modLoc("block/" + blockName);

            this.cubeAllModel(entry.getValue().get(), texture);
        }

        for (Map.Entry<DyeColor, DeferredBlock<Block>> entry : ModRegistry.WARP_PIPES.entrySet()) {
            String blockName = BuiltInRegistries.BLOCK.getKey(entry.getValue().get()).getPath();
            ResourceLocation entranceTexture = modLoc("block/" + blockName + "_entrance_side");
            ResourceLocation sideTexture = modLoc("block/" + blockName + "_side");
            ResourceLocation bottomTexture = modLoc("block/" + blockName + "_bottom");
            ResourceLocation topTexture = modLoc("block/" + blockName + "_top");
            ResourceLocation topClosedTexture = modLoc("block/" + blockName + "_top_closed");

            this.warpPipeModel(entry.getValue().get(), entranceTexture, bottomTexture, sideTexture, topTexture, topClosedTexture);
        }
    }

    private void cubeAllModel(Block block, ResourceLocation mainTexture) {
        String modelName = BuiltInRegistries.BLOCK.getKey(block).getPath();

        ModelFile model = models()
                .withExistingParent(modelName, mcLoc("minecraft:block/cube_all"))
                .texture("all", mainTexture);

        this.simpleBlockWithItem(block, model);
    }

    private void pipeBubblesModel(Block block) {
        ModelFile model = models().getExistingFile(ResourceLocation
                .fromNamespaceAndPath("minecraft", "block/water"));

        VariantBlockStateBuilder variantBuilder = this.getVariantBuilder(block);
        variantBuilder.partialState().addModels(new ConfiguredModel(model));
    }

    private void warpPipeModel(Block block, ResourceLocation entranceTexture, ResourceLocation bottomTexture,
                               ResourceLocation sideTexture, ResourceLocation topTexture, ResourceLocation topClosedTexture) {
        String modelName = BuiltInRegistries.BLOCK.getKey(block).getPath();

        ModelFile model = models()
                .withExistingParent(modelName, mcLoc("minecraft:block/cube_bottom_top"))
                .texture("bottom", bottomTexture).texture("side", sideTexture).texture("top", bottomTexture);
        ModelFile modelEntrance = models()
                .withExistingParent(modelName + "_entrance", mcLoc("minecraft:block/cube_bottom_top"))
                .texture("bottom", bottomTexture).texture("side", entranceTexture).texture("top", topTexture);
        ModelFile modelClosed = models()
                .withExistingParent(modelName + "_entrance_closed", mcLoc("minecraft:block/cube_bottom_top"))
                .texture("bottom", bottomTexture).texture("side", entranceTexture).texture("top", topClosedTexture);

        simpleBlockItem(block, modelEntrance);

        VariantBlockStateBuilder variantBuilder = this.getVariantBuilder(block);

        for (Direction direction : Direction.values()) {
            int xRot = getXRotation(direction);
            int yRot = getYRotation(direction);

            for (boolean entrance : new boolean[]{false, true}) {
                for (boolean closed : new boolean[]{false, true}) {
                    for (boolean bubbles : new boolean[]{false, true}) {
                        for (boolean waterSpout : new boolean[]{false, true}) {
                            ModelFile selectedModel = getModelForState(model, modelEntrance, modelClosed, entrance, closed);

                            variantBuilder.partialState()
                                    .with(WarpPipeBlock.FACING, direction)
                                    .with(WarpPipeBlock.ENTRANCE, entrance)
                                    .with(WarpPipeBlock.CLOSED, closed)
                                    .with(WarpPipeBlock.BUBBLES, bubbles)
                                    .with(WarpPipeBlock.WATER_SPOUT, waterSpout)
                                    .addModels(new ConfiguredModel(selectedModel, xRot, yRot, false));
                        }
                    }
                }
            }
        }
    }

    private void waterSpoutModel(Block block, ResourceLocation sideTexture, ResourceLocation topTexture, ResourceLocation splashTexture) {
        String modelName = BuiltInRegistries.BLOCK.getKey(block).getPath();

        ModelFile model = models()
                .withExistingParent(modelName, modLoc("block/template_water_spout"))
                .texture("side", sideTexture);
        ModelFile modelTop = models()
                .withExistingParent(modelName + "_top", modLoc("block/template_water_spout_top"))
                .texture("splash", splashTexture).texture("side", sideTexture).texture("top", topTexture);

        VariantBlockStateBuilder variantBuilder = this.getVariantBuilder(block);
        variantBuilder.partialState().with(WaterSpoutBlock.TOP, false).addModels(new ConfiguredModel(model));
        variantBuilder.partialState().with(WaterSpoutBlock.TOP, true).addModels(new ConfiguredModel(modelTop));
    }

    // Unfinished
    private void clearWarpPipeModel(Block block) {
        String modelName = BuiltInRegistries.BLOCK.getKey(block).getPath();

        ModelFile baseModel = models()
                .withExistingParent(modelName, modLoc("block/clear_warp_pipe/clear_warp_pipe"));

        ModelFile entranceModel = models()
                .withExistingParent(modelName + "_entrance", modLoc("block/clear_warp_pipe/clear_warp_pipe_entrance"));

        ModelFile closedModel = models()
                .withExistingParent(modelName + "_closed", modLoc("block/clear_warp_pipe/clear_warp_pipe_closed"));

        ModelFile entranceClosedModel = models()
                .withExistingParent(modelName + "_entrance_closed", modLoc("block/clear_warp_pipe/clear_warp_pipe_entrance_closed"));

        // Directional Models
        ModelFile northModel = models()
                .withExistingParent(modelName + "_n", modLoc("block/clear_warp_pipe/clear_warp_pipe_n"));
        ModelFile southModel = models()
                .withExistingParent(modelName + "_s", modLoc("block/clear_warp_pipe/clear_warp_pipe_s"));
        ModelFile eastModel = models()
                .withExistingParent(modelName + "_e", modLoc("block/clear_warp_pipe/clear_warp_pipe_e"));
        ModelFile westModel = models()
                .withExistingParent(modelName + "_w", modLoc("block/clear_warp_pipe/clear_warp_pipe_w"));
        ModelFile upModel = models()
                .withExistingParent(modelName + "_u", modLoc("block/clear_warp_pipe/clear_warp_pipe_u"));
        ModelFile downModel = models()
                .withExistingParent(modelName + "_d", modLoc("block/clear_warp_pipe/clear_warp_pipe_d"));

        // Multi-direction Models
        ModelFile nsModel = models()
                .withExistingParent(modelName + "_ns", modLoc("block/clear_warp_pipe/clear_warp_pipe_ns"));
        ModelFile ewModel = models()
                .withExistingParent(modelName + "_ew", modLoc("block/clear_warp_pipe/clear_warp_pipe_ew"));
        ModelFile udModel = models()
                .withExistingParent(modelName + "_ud", modLoc("block/clear_warp_pipe/clear_warp_pipe_ud"));
        ModelFile nsewModel = models()
                .withExistingParent(modelName + "_nsew", modLoc("block/clear_warp_pipe/clear_warp_pipe_nsew"));

        VariantBlockStateBuilder variantBuilder = this.getVariantBuilder(block);

        // Base Pipe (No entrance or closed)
        variantBuilder.partialState()
                .addModels(new ConfiguredModel(baseModel));

        // Entrance Open and Closed Variants
        variantBuilder.partialState().with(ClearWarpPipeBlock.ENTRANCE, true)
                .with(ClearWarpPipeBlock.CLOSED, false)
                .addModels(new ConfiguredModel(entranceModel));

        variantBuilder.partialState().with(ClearWarpPipeBlock.ENTRANCE, true)
                .with(ClearWarpPipeBlock.CLOSED, true)
                .addModels(new ConfiguredModel(entranceClosedModel));

        // Closed Pipe Variant (without "entrance")
        variantBuilder.partialState().with(ClearWarpPipeBlock.CLOSED, true)
                .addModels(new ConfiguredModel(closedModel));

        // Single Connection States
        variantBuilder.partialState().with(ClearWarpPipeBlock.NORTH, true)
                .addModels(new ConfiguredModel(northModel));
        variantBuilder.partialState().with(ClearWarpPipeBlock.SOUTH, true)
                .addModels(new ConfiguredModel(southModel));
        variantBuilder.partialState().with(ClearWarpPipeBlock.EAST, true)
                .addModels(new ConfiguredModel(eastModel));
        variantBuilder.partialState().with(ClearWarpPipeBlock.WEST, true)
                .addModels(new ConfiguredModel(westModel));
        variantBuilder.partialState().with(ClearWarpPipeBlock.UP, true)
                .addModels(new ConfiguredModel(upModel));
        variantBuilder.partialState().with(ClearWarpPipeBlock.DOWN, true)
                .addModels(new ConfiguredModel(downModel));

        // Multi-Directional Connection States
        variantBuilder.partialState().with(ClearWarpPipeBlock.NORTH, true).with(ClearWarpPipeBlock.SOUTH, true)
                .addModels(new ConfiguredModel(nsModel));
        variantBuilder.partialState().with(ClearWarpPipeBlock.EAST, true).with(ClearWarpPipeBlock.WEST, true)
                .addModels(new ConfiguredModel(ewModel));
        variantBuilder.partialState().with(ClearWarpPipeBlock.UP, true).with(ClearWarpPipeBlock.DOWN, true)
                .addModels(new ConfiguredModel(udModel));
        variantBuilder.partialState().with(ClearWarpPipeBlock.NORTH, true).with(ClearWarpPipeBlock.SOUTH, true)
                .with(ClearWarpPipeBlock.EAST, true).with(ClearWarpPipeBlock.WEST, true)
                .addModels(new ConfiguredModel(nsewModel));
    }

    private int getXRotation(Direction direction) {
        return switch (direction) {
            case UP -> 0;
            case DOWN -> 180;
            case NORTH, SOUTH, EAST, WEST -> 90;
        };
    }

    private int getYRotation(Direction direction) {
        return switch (direction) {
            case NORTH -> 0;
            case SOUTH -> 180;
            case EAST -> 90;
            case WEST -> 270;
            default -> 0;
        };
    }

    private ModelFile getModelForState(ModelFile model, ModelFile modelEntrance, ModelFile modelClosed, boolean entrance, boolean closed) {
        if (entrance)
            return closed ? modelClosed : modelEntrance;
        return model;
    }
}
