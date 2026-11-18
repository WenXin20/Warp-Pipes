package com.wenxin2.warp_pipes.datagen;

import com.wenxin2.warp_pipes.WarpPipes;
import com.wenxin2.warp_pipes.blocks.ClearWarpPipeBlock;
import com.wenxin2.warp_pipes.blocks.WarpPipeBlock;
import com.wenxin2.warp_pipes.blocks.WaterSpoutBlock;
import com.wenxin2.warp_pipes.registries.ModRegistry;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.client.model.generators.MultiPartBlockStateBuilder;
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
        String baseName = BuiltInRegistries.BLOCK.getKey(block).getPath();

        Map<String, ModelFile> modelCache = new HashMap<>();
        VariantBlockStateBuilder variantBuilder = this.getVariantBuilder(block);

        for (Direction direction : Direction.values()) {
            int xRot = getXRotation(direction);
            int yRot = getYRotation(direction);

            for (boolean entrance : new boolean[]{false, true}) {
                for (boolean closed : new boolean[]{false, true}) {
                    String modelName = baseName + "_"
                            + direction.getName()
                            + (entrance ? "_entrance" : "")
                            + (entrance ? (closed ? "_closed" : "") : "");

                    ModelFile model = modelCache.computeIfAbsent(modelName, name -> {
                        ResourceLocation side = entrance ? entranceTexture : sideTexture;
                        ResourceLocation top = entrance ? (closed ? topClosedTexture : topTexture) : bottomTexture;
                        return this.createWarpPipeModel(name, bottomTexture, side, top, direction);
                    });

                    variantBuilder.partialState()
                            .with(WarpPipeBlock.FACING, direction)
                            .with(WarpPipeBlock.ENTRANCE, entrance)
                            .with(WarpPipeBlock.CLOSED, closed)
                            .addModels(new ConfiguredModel(model, xRot, yRot, false));
                }
            }
        }

        ModelFile itemModel = modelCache.get(baseName + "_up_entrance");
        this.simpleBlockItem(block, itemModel);
    }

    private ModelFile createWarpPipeModel(String name, ResourceLocation bottom, ResourceLocation side,
                                          ResourceLocation top, Direction blockDir) {
        return models()
                .withExistingParent(name, mcLoc("minecraft:block/block"))
                .texture("bottom", bottom)
                .texture("side", side)
                .texture("top", top)
                .texture("particle", side)

                .element()
                .from(0, 0, 0)
                .to(16, 16, 16)

                .face(Direction.UP)
                .texture("#top")
                .uvs(getU1(blockDir, Direction.UP), getV1(blockDir, Direction.UP),
                        getU2(blockDir, Direction.UP), getV2(blockDir, Direction.UP))
                .end()

                .face(Direction.DOWN)
                .texture("#bottom")
                .uvs(getU1(blockDir, Direction.DOWN), getV1(blockDir, Direction.DOWN),
                        getU2(blockDir, Direction.DOWN), getV2(blockDir, Direction.DOWN))
                .end()

                .face(Direction.NORTH)
                .texture("#side")
                .uvs(getU1(blockDir, Direction.NORTH), getV1(blockDir, Direction.NORTH),
                        getU2(blockDir, Direction.NORTH), getV2(blockDir, Direction.NORTH))
                .end()

                .face(Direction.SOUTH)
                .texture("#side")
                .uvs(getU1(blockDir, Direction.SOUTH), getV1(blockDir, Direction.SOUTH),
                        getU2(blockDir, Direction.SOUTH), getV2(blockDir, Direction.SOUTH))
                .end()

                .face(Direction.WEST)
                .texture("#side")
                .uvs(getU1(blockDir, Direction.WEST), getV1(blockDir, Direction.WEST),
                        getU2(blockDir, Direction.WEST), getV2(blockDir, Direction.WEST))
                .end()

                .face(Direction.EAST)
                .texture("#side")
                .uvs(getU1(blockDir, Direction.EAST), getV1(blockDir, Direction.EAST),
                        getU2(blockDir, Direction.EAST), getV2(blockDir, Direction.EAST))
                .end()
                .end();
    }

    private void waterSpoutModel(Block block, ResourceLocation sideTexture, ResourceLocation topTexture, ResourceLocation splashTexture) {
        String modelName = BuiltInRegistries.BLOCK.getKey(block).getPath();

        ModelFile[] side = this.waterSpoutParts(modelName, "_side", sideTexture, topTexture, splashTexture);
        ModelFile[] sideLeft = this.waterSpoutParts(modelName, "_side_left", sideTexture, topTexture, splashTexture);
        ModelFile[] sideRight = this.waterSpoutParts(modelName, "_side_right", sideTexture, topTexture, splashTexture);
        ModelFile topCenter = this.waterSpoutPart(modelName, "_top_center", sideTexture, topTexture, splashTexture);
        ModelFile[] topEdge = this.waterSpoutParts(modelName, "_top_edge", sideTexture, topTexture, splashTexture);
        ModelFile[] topCorner = this.waterSpoutParts(modelName, "_top_corner", sideTexture, topTexture, splashTexture);
        ModelFile[] splash = this.waterSpoutParts(modelName, "_splash", sideTexture, topTexture, splashTexture);
        ModelFile[] splashLeft = this.waterSpoutParts(modelName, "_splash_left", sideTexture, topTexture, splashTexture);
        ModelFile[] splashRight = this.waterSpoutParts(modelName, "_splash_right", sideTexture, topTexture, splashTexture);
        ModelFile[] splashExtendLeft = this.waterSpoutParts(modelName, "_splash_extend_left", sideTexture, topTexture, splashTexture);
        ModelFile[] splashExtendRight = this.waterSpoutParts(modelName, "_splash_extend_right", sideTexture, topTexture, splashTexture);

        MultiPartBlockStateBuilder builder = this.getMultipartBuilder(block);

        for (Direction facing : Direction.values()) {
            builder.part().modelFile(topCenter).rotationX(WaterSpoutBlock.getModelXRotation(facing))
                    .rotationY(WaterSpoutBlock.getModelYRotation(facing)).addModel()
                    .condition(WaterSpoutBlock.FACING, facing).condition(WaterSpoutBlock.TOP, true).end();

            for (Direction direction : Direction.Plane.HORIZONTAL) {
                BooleanProperty property = PipeBlock.PROPERTY_BY_DIRECTION.get(direction);
                BooleanProperty left = PipeBlock.PROPERTY_BY_DIRECTION.get(direction.getCounterClockWise());
                BooleanProperty right = PipeBlock.PROPERTY_BY_DIRECTION.get(direction.getClockWise());
                int[] rotation = this.waterSpoutRotation(facing, direction);

                this.waterSpoutPart(builder, side, rotation).condition(WaterSpoutBlock.FACING, facing)
                        .condition(property, false).end();
                this.waterSpoutPart(builder, sideLeft, rotation).condition(WaterSpoutBlock.FACING, facing)
                        .condition(property, true).condition(left, false).end();
                this.waterSpoutPart(builder, sideRight, rotation).condition(WaterSpoutBlock.FACING, facing)
                        .condition(property, true).condition(right, false).end();
                this.waterSpoutPart(builder, topEdge, rotation).condition(WaterSpoutBlock.FACING, facing)
                        .condition(WaterSpoutBlock.TOP, true).condition(property, true).end();
                this.waterSpoutPart(builder, topCorner, rotation).condition(WaterSpoutBlock.FACING, facing)
                        .condition(WaterSpoutBlock.TOP, true).condition(property, true).condition(right, true).end();
                this.waterSpoutPart(builder, splash, rotation).condition(WaterSpoutBlock.FACING, facing)
                        .condition(WaterSpoutBlock.TOP, true).condition(property, false).end();
                this.waterSpoutPart(builder, splashLeft, rotation).condition(WaterSpoutBlock.FACING, facing)
                        .condition(WaterSpoutBlock.TOP, true).condition(property, false).condition(left, false).end();
                this.waterSpoutPart(builder, splashRight, rotation).condition(WaterSpoutBlock.FACING, facing)
                        .condition(WaterSpoutBlock.TOP, true).condition(property, false).condition(right, false).end();
                this.waterSpoutPart(builder, splashExtendLeft, rotation).condition(WaterSpoutBlock.FACING, facing)
                        .condition(WaterSpoutBlock.TOP, true).condition(property, false).condition(left, true).end();
                this.waterSpoutPart(builder, splashExtendRight, rotation).condition(WaterSpoutBlock.FACING, facing)
                        .condition(WaterSpoutBlock.TOP, true).condition(property, false).condition(right, true).end();
            }
        }
    }

    private MultiPartBlockStateBuilder.PartBuilder waterSpoutPart(MultiPartBlockStateBuilder builder, ModelFile[] models, int[] rotation) {
        return builder.part().modelFile(models[rotation[0]]).rotationX(rotation[1]).rotationY(rotation[2]).addModel();
    }

    private int[] waterSpoutRotation(Direction facing, Direction local) {
        Direction target = WaterSpoutBlock.getWorldSide(facing, local);

        for (int base = 0; base < 2; base++) {
            Direction baseDirection = base == 0 ? Direction.NORTH : Direction.EAST;
            for (int x = 0; x < 360; x += 90) {
                for (int y = 0; y < 360; y += 90) {
                    if (WaterSpoutBlock.rotateDirection(Direction.UP, x, y) == facing
                            && WaterSpoutBlock.rotateDirection(baseDirection, x, y) == target)
                        return new int[]{base, x, y};
                }
            }
        }
        throw new IllegalStateException("No water spout rotation for " + facing + " " + local);
    }

    private ModelFile[] waterSpoutParts(String modelName, String suffix, ResourceLocation sideTexture,
                                        ResourceLocation topTexture, ResourceLocation splashTexture) {
        return new ModelFile[]{this.waterSpoutPart(modelName, suffix, sideTexture, topTexture, splashTexture),
                this.waterSpoutPart(modelName, suffix + "_east", sideTexture, topTexture, splashTexture)};
    }

    private ModelFile waterSpoutPart(String modelName, String suffix, ResourceLocation sideTexture,
                                     ResourceLocation topTexture, ResourceLocation splashTexture) {
        return models().withExistingParent(modelName + suffix, modLoc("block/template_water_spout" + suffix))
                .texture("side", sideTexture).texture("top", topTexture).texture("splash", splashTexture)
                .texture("particle", sideTexture);
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

    private Direction rotateFace(Direction face, Direction blockDir) {
        if (blockDir == Direction.DOWN)
            return face;
        if (blockDir == Direction.UP)
            return face;

        return switch (blockDir) {
            case NORTH -> face;
            case SOUTH -> face.getOpposite();
            case WEST  -> rotateY(face, 90);
            case EAST  -> rotateY(face, -90);
            default -> face;
        };
    }

    private Direction rotateY(Direction direction, int degrees) {
        if (direction.getAxis().isVertical())
            return direction;
        int steps = ((degrees / 90) % 4 + 4) % 4;

        Direction result = direction;
        for (int i = 0; i < steps; i++) {
            result = result.getClockWise();
        }
        return result;
    }

    private Direction.Axis getFlipAxis(Direction blockDir, Direction faceDir) {
        return switch (blockDir) {
            case UP -> faceDir == Direction.NORTH ? Direction.Axis.Z
                    : faceDir == Direction.WEST ? Direction.Axis.X : null;
            case DOWN -> faceDir == Direction.NORTH ? Direction.Axis.Z
                    : faceDir == Direction.EAST ? Direction.Axis.X : null;
            case NORTH -> faceDir == Direction.NORTH ? Direction.Axis.Z
                    : faceDir == Direction.WEST ? Direction.Axis.X : null;
            case SOUTH -> faceDir == Direction.NORTH ? Direction.Axis.Z
                    : faceDir == Direction.EAST ? Direction.Axis.X : null;
            case EAST -> faceDir == Direction.EAST ? Direction.Axis.Z
                    : faceDir == Direction.SOUTH ? Direction.Axis.Z : null;
            case WEST -> faceDir == Direction.EAST ? Direction.Axis.Z
                    : faceDir == Direction.NORTH ? Direction.Axis.Z : null;
            default -> null;
        };
    }

    private boolean flipU(Direction faceDir, Direction.Axis axis) {
        if (axis == null) return false;

        return switch (faceDir) {
            case UP, DOWN -> axis == Direction.Axis.X || axis == Direction.Axis.Z || axis == Direction.Axis.Y;
            case NORTH, SOUTH, EAST, WEST -> axis == Direction.Axis.X || axis == Direction.Axis.Z;
        };
    }

    private boolean flipV(Direction faceDir, Direction.Axis axis) {
        if (axis == null) return false;

        return switch (faceDir) {
            case UP -> axis == Direction.Axis.X || axis == Direction.Axis.Z || axis == Direction.Axis.Y;
            case NORTH, SOUTH, EAST, WEST, DOWN -> axis == Direction.Axis.Y;
        };
    }

    private float getU1(Direction blockDir, Direction faceDir) {
        Direction rotated = rotateFace(faceDir, blockDir);
        return flipU(rotated, getFlipAxis(blockDir, rotated)) ? 16f : 0f;
    }

    private float getU2(Direction blockDir, Direction faceDir) {
        Direction rotated = rotateFace(faceDir, blockDir);
        return flipU(rotated, getFlipAxis(blockDir, rotated)) ? 0f : 16f;
    }

    private float getV1(Direction blockDir, Direction faceDir) {
        Direction rotated = rotateFace(faceDir, blockDir);
        return flipV(rotated, getFlipAxis(blockDir, rotated)) ? 16f : 0f;
    }

    private float getV2(Direction blockDir, Direction faceDir) {
        Direction rotated = rotateFace(faceDir, blockDir);
        return flipV(rotated, getFlipAxis(blockDir, rotated)) ? 0f : 16f;
    }
}
