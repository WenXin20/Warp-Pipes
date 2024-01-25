package com.wenxin2.warp_pipes.fluids;

import com.wenxin2.warp_pipes.WarpPipes;
import com.wenxin2.warp_pipes.init.ModRegistry;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.common.SoundActions;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.registries.RegistryObject;

public class StillWaterType extends FluidType {
    public static final ResourceLocation STILL_WATER_STILL = new ResourceLocation(
            "minecraft:block/water_still");
    public static final ResourceLocation STILL_WATER_FLOWING = new ResourceLocation(
            "warp_pipes:block/still_water_flow");
    public static final ResourceLocation STILL_WATER_OVERLAY = new ResourceLocation(
            "minecraft:block/water_still");
    public static final ResourceLocation STILL_WATER_UNDERWATER = new ResourceLocation(
            "minecraft:textures/misc/underwater.png");

    public StillWaterType(Properties properties)
    {
        super(properties);
    }

    public static ForgeFlowingFluid.Properties makeProperties()
    {
        return new ForgeFlowingFluid.Properties(STILL_WATER_TYPE, ModRegistry.STILL_WATER,
                ModRegistry.STILL_WATER_FLOWING).bucket(Items.WATER_BUCKET.builtInRegistryHolder())
                .block(ModRegistry.STILL_WATER_BLOCK);
    }

    public static final RegistryObject<FluidType> STILL_WATER_TYPE = WarpPipes.FLUID_TYPES.register(
            "still_water",
            () -> new FluidType(FluidType.Properties.create().descriptionId("block.warp_pipes.water")
                    .pathType(BlockPathTypes.WATER).rarity(Rarity.COMMON)
                    .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
                    .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY).supportsBoating(true).density(0)
                    .temperature(100).viscosity(0).lightLevel(0).fallDistanceModifier(0.0F).motionScale(0.0D)
                    .adjacentPathType(null).canPushEntity(true).canSwim(true).canDrown(true).canExtinguish(true)
                    .canConvertToSource(false).canHydrate(true))
    {

        @Override
        public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer)
        {
            consumer.accept(new IClientFluidTypeExtensions()
            {
                @Override
                public ResourceLocation getStillTexture()
                {
                    return STILL_WATER_STILL;
                }

                @Override
                public ResourceLocation getFlowingTexture()
                {
                    return STILL_WATER_FLOWING;
                }

                @Nullable
                @Override
                public ResourceLocation getOverlayTexture()
                {
                    return STILL_WATER_OVERLAY;
                }

                @Override
                public ResourceLocation getRenderOverlayTexture(Minecraft mc)
                {
                    return STILL_WATER_UNDERWATER;
                }

                @Override
                public int getTintColor(FluidState state, BlockAndTintGetter getter, BlockPos pos)
                {
                    return BiomeColors.getAverageWaterColor(getter, pos) | 0xFF0000cc;
                }
            });
        }
    });
}
