package com.wenxin2.warp_pipes.items;

import com.mojang.logging.LogUtils;
import com.wenxin2.warp_pipes.blocks.ClearWarpPipeBlock;
import com.wenxin2.warp_pipes.blocks.WarpPipeBlock;
import com.wenxin2.warp_pipes.blocks.entities.WarpPipeBlockEntity;
import com.wenxin2.warp_pipes.init.Config;
import com.wenxin2.warp_pipes.init.SoundRegistry;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.slf4j.Logger;

public class LinkerItem extends TieredItem {
    public static final String WARP_POS = "WarpPos";
    public static final String WARP_DIMENSION = "Dimension";
    public static final String POS_X = "X";
    public static final String POS_Y = "Y";
    public static final String POS_Z = "Z";

    private static final Logger LOGGER = LogUtils.getLogger();
    public LinkerItem(final Properties properties, Tier tier) {
        super(tier, properties);
    }
    public boolean isBound;

    public boolean setBound(boolean isBound) {
        return this.isBound = isBound;
    }

    public boolean getBound() {
        return this.isBound;
    }

    @Override
    public InteractionResult useOn(UseOnContext useOnContext) {
        Player player = useOnContext.getPlayer();
        Level world = useOnContext.getLevel();
        BlockPos pos = useOnContext.getClickedPos();
        BlockState state = world.getBlockState(pos);
        BlockEntity blockEntity = world.getBlockEntity(pos);
        ItemStack item = useOnContext.getItemInHand();
        CompoundTag wrenchTag = item.getTag();
        String dimension = world.dimension().location().toString();

        if (wrenchTag != null && wrenchTag.contains("Bound")) {
            isBound = wrenchTag.getBoolean("Bound");
        }

        if (player != null && !player.isCreative() && Config.CREATIVE_WRENCH_PIPE_LINKING.get()) {
            player.displayClientMessage(Component.translatable("display.warp_pipes.linker.requires_creative")
                    .withStyle(ChatFormatting.RED), true);
            return InteractionResult.sidedSuccess(world.isClientSide);
        } else if (player != null) {
            if ((state.getBlock() instanceof ClearWarpPipeBlock || ((state.getBlock() instanceof WarpPipeBlock) && state.getValue(WarpPipeBlock.ENTRANCE))) && player.isShiftKeyDown()) {

                if (getBound() == Boolean.FALSE) {
                    if (wrenchTag == null) {
                        wrenchTag = new CompoundTag();
                    }

                    BlockPos warpPos = new BlockPos(pos.getX(), pos.getY(), pos.getZ());

                    wrenchTag.putBoolean("Bound", Boolean.TRUE);
                    wrenchTag.putInt(POS_X, pos.getX());
                    wrenchTag.putInt(POS_Y, pos.getY());
                    wrenchTag.putInt(POS_Z, pos.getZ());
                    wrenchTag.put(WARP_POS, NbtUtils.writeBlockPos(warpPos));
                    wrenchTag.putString(WARP_DIMENSION, dimension);
                    this.setBound(Boolean.TRUE);

                    player.displayClientMessage(Component.translatable("display.warp_pipes.linker.bound",
                            wrenchTag.getInt(POS_X), wrenchTag.getInt(POS_Y), wrenchTag.getInt(POS_Z), wrenchTag.getString(WARP_DIMENSION))
                            .withStyle(ChatFormatting.DARK_GREEN), true);
                    this.spawnParticles(world, pos, ParticleTypes.ENCHANT);
                    this.playSound(world, pos, SoundRegistry.WRENCH_BOUND.get(), SoundSource.PLAYERS, 1.0F, 0.1F);
                } else if (getBound()) {
                    Player player1 = useOnContext.getPlayer();
                    if (wrenchTag == null) {
                        wrenchTag = new CompoundTag();
                    }
                    wrenchTag.putBoolean("Bound", Boolean.FALSE);
                    this.setBound(Boolean.FALSE);
                    this.writeTag(world.dimension(), pos, item.getOrCreateTag());

                    BlockPos warpPos = new BlockPos(pos.getX(), pos.getY(), pos.getZ());

                    if (player1 != null) {
                        item.hurtAndBreak(1, player1, p -> p.broadcastBreakEvent(useOnContext.getHand()));
                        player1.displayClientMessage(Component.translatable("display.warp_pipes.linker.linked",
                                        wrenchTag.getInt(POS_X), wrenchTag.getInt(POS_Y), wrenchTag.getInt(POS_Z), wrenchTag.getString(WARP_DIMENSION))
                                .withStyle(ChatFormatting.GOLD), true);
                    }

                    GlobalPos globalPos = LinkerItem.createWarpPos(wrenchTag);
                    if (globalPos == null)
                        return super.useOn(useOnContext);
                    BlockEntity blockEntity1 = world.getBlockEntity(globalPos.pos());
                    
                    if (blockEntity instanceof WarpPipeBlockEntity warpPipeBE
                            && blockEntity1 instanceof WarpPipeBlockEntity warpPipeBEGlobal
                            && LinkerItem.isLinked(item)) {

                        wrenchTag.put(WarpPipeBlockEntity.WARP_POS, NbtUtils.writeBlockPos(warpPos));
                        this.link(pos, world, wrenchTag, warpPipeBE, warpPipeBEGlobal);
                    } else {
                        if (player1 != null) {
                            player1.displayClientMessage(Component.translatable("display.warp_pipes.linker.dimension_fail",
                                            wrenchTag.getInt(POS_X), wrenchTag.getInt(POS_Y), wrenchTag.getInt(POS_Z), wrenchTag.getString(WARP_DIMENSION))
                                    .withStyle(ChatFormatting.RED), true);
                        }
                    }

                    this.spawnParticles(world, pos, ParticleTypes.ENCHANT);
                    this.playSound(world, pos, SoundRegistry.PIPES_LINKED.get(), SoundSource.BLOCKS, 1.0F, 0.1F);
                }
                return InteractionResult.sidedSuccess(world.isClientSide);
            }
        }
        return super.useOn(useOnContext);
    }

    public void clearTags(CompoundTag wrenchTag) {
        wrenchTag.remove(POS_X);
        wrenchTag.remove(POS_Y);
        wrenchTag.remove(POS_Z);
        wrenchTag.remove(WARP_DIMENSION);
    }

    public static boolean isLinked(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        return tag != null && (tag.contains(POS_X) || tag.contains(POS_Y) || tag.contains(POS_Z) || tag.contains(WARP_DIMENSION));
    }

    public void link(BlockPos pos, Level world, CompoundTag tag, WarpPipeBlockEntity warpPipeBE, WarpPipeBlockEntity warpPipeBEGlobal) {
        System.out.println("Current Dimension: " + world.dimension());

        warpPipeBE.setDestinationPos(warpPipeBEGlobal.getBlockPos());
        warpPipeBE.setChanged();
        if (warpPipeBEGlobal.getLevel() != null) {
            warpPipeBE.setDestinationDim(warpPipeBEGlobal.getLevel().dimension());
            System.out.println("Global Dimension: " + warpPipeBEGlobal.getLevel().dimension());
        } else System.out.println("World is null!");

        warpPipeBEGlobal.setDestinationPos(pos);
        warpPipeBEGlobal.setDestinationDim(world.dimension());
        warpPipeBEGlobal.setChanged();
        this.clearTags(tag);
    }

    @Nullable
    public static GlobalPos createWarpPos(CompoundTag tag) {
        Optional<ResourceKey<Level>> optional;
        boolean containsPipePos = tag.contains(WARP_POS);
        boolean containsPipeDim = tag.contains(WARP_DIMENSION);
        if (containsPipePos && containsPipeDim && (optional = LinkerItem.getWarpDimension(tag)).isPresent()) {
            BlockPos pos = new BlockPos(tag.getInt(POS_X), tag.getInt(POS_Y), tag.getInt(POS_Z));
            return GlobalPos.of(optional.get(), pos);
        }
        return null;
    }


    public void playSound(Level world, BlockPos pos, SoundEvent soundEvent, SoundSource source, float volume, float pitch) {
        world.playSound(null, pos, soundEvent, source, volume, pitch);
    }

    private void spawnParticles(Level world, BlockPos pos, ParticleOptions particleOptions) {
        if (world.isClientSide()) {
            RandomSource random = world.getRandom();

            for (int i = 0; i < 40; ++i) {
                world.addParticle(particleOptions,
                        pos.getX() + 0.5D + (0.5D * (random.nextBoolean() ? 1 : -1)), pos.getY() + 1.5D,
                        pos.getZ() + 0.5D + (0.5D * (random.nextBoolean() ? 1 : -1)),
                        (random.nextDouble() - 0.5D) * 2.0D, -random.nextDouble(),
                        (random.nextDouble() - 0.5D) * 2.0D);
            }
        }
    }

    private void writeTag(ResourceKey<Level> worldKey, BlockPos pos, CompoundTag tag) {
        tag.put(WARP_POS, NbtUtils.writeBlockPos(pos));
        Level.RESOURCE_KEY_CODEC.encodeStart(NbtOps.INSTANCE, worldKey)
                .resultOrPartial(LOGGER::error).ifPresent(nbtElement -> tag.put(WARP_DIMENSION, nbtElement));
    }

    public static Optional<ResourceKey<Level>> getWarpDimension(CompoundTag tag) {
        return Level.RESOURCE_KEY_CODEC.parse(NbtOps.INSTANCE, tag.get(WARP_DIMENSION)).result();
    }

    @Override
    @ParametersAreNonnullByDefault
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> list, TooltipFlag tooltip) {
        CompoundTag tag = stack.getTag();
        if (getBound() && tag != null && tag.contains(POS_X) && tag.contains(POS_Y) && tag.contains(POS_Z) && tag.contains(WARP_DIMENSION)) {
            list.add(Component.translatable("", true));
            list.add(Component.translatable("display.warp_pipes.linker.bound_tooltip", tag.getInt(POS_X), tag.getInt(POS_Y), tag.getInt(POS_Z),
                    tag.getString(WARP_DIMENSION), true).withStyle(ChatFormatting.GOLD));
        }
        else {
            list.add(Component.translatable("", true));
            list.add(Component.translatable("display.warp_pipes.linker.not_bound_tooltip", true)
                    .withStyle(ChatFormatting.GRAY).withStyle(ChatFormatting.ITALIC));
        }
    }
}
