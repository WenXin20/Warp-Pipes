package com.wenxin2.warp_pipes.items;

import com.mojang.logging.LogUtils;
import com.wenxin2.warp_pipes.blocks.WarpPipeBlock;
import com.wenxin2.warp_pipes.blocks.entities.WarpPipeBlockEntity;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.slf4j.Logger;

public class LinkerItem extends Item {
    public static final String WARP_PIPE_POS = "WarpPos";
    public static final String WARP_PIPE_DIMENSION = "WarpDimension";
    private static final Logger LOGGER = LogUtils.getLogger();
    public LinkerItem(final Item.Properties properties) {
        super(properties);
    }
    boolean Bound = Boolean.FALSE;
    @Override
    public InteractionResult useOn(UseOnContext useOnContext) {
        InteractionResult interactionResult = super.useOn(useOnContext);
        Player player = useOnContext.getPlayer();
        InteractionHand hand = Objects.requireNonNull(useOnContext.getPlayer()).getUsedItemHand();
        Level world = useOnContext.getLevel();
        BlockPos pos = useOnContext.getClickedPos();
        BlockState state = world.getBlockState(pos);
        BlockEntity blockEntity = world.getBlockEntity(pos);
        ItemStack stack = useOnContext.getItemInHand();
        CompoundTag tag = stack.getTag();

        if (tag != null && tag.contains("Bound")) {
            Bound = tag.getBoolean("Bound");
        }

        if ((state.getBlock() instanceof WarpPipeBlock) && state.getValue(WarpPipeBlock.ENTRANCE)) {
            if (Bound == Boolean.FALSE) {
                if (tag == null) {
                    tag = new CompoundTag();
                }
                tag.putBoolean("Bound", Boolean.TRUE);
                tag.putDouble("X", (int)pos.getX());
                tag.putDouble("Y", (int)pos.getY());
                tag.putDouble("Z", (int)pos.getZ());
                tag.put(WARP_PIPE_POS, NbtUtils.writeBlockPos(pos));
                tag.putString(WARP_PIPE_DIMENSION, world.dimension().location().toString());

                assert player != null;
                player.displayClientMessage(Component.translatable("display.warp_pipes.linker.bound",
                        pos.getX(), pos.getY(), pos.getZ()), true);

                return InteractionResult.sidedSuccess(world.isClientSide);
            } else {
                Player player1 = useOnContext.getPlayer();
                this.writeTag(world.dimension(), pos, stack.getOrCreateTag());
                tag.putBoolean("Bound", Boolean.FALSE);

                GlobalPos globalPos = LinkerItem.createWarpPos(tag);
                if (globalPos == null)
                    return interactionResult;
                BlockEntity blockEntity1 = world.getBlockEntity(globalPos.pos());
                if (blockEntity instanceof WarpPipeBlockEntity warpPipeBE && blockEntity1 instanceof WarpPipeBlockEntity warpPipeBEGlobal &&  LinkerItem.isLinked(stack)) {
                    this.link(pos, world, tag, warpPipeBE, warpPipeBEGlobal);
//                    createWarpPos(tag);
                }

                if (player1 == null)
                    return interactionResult;
                stack.hurtAndBreak(1, player1, p -> p.broadcastBreakEvent(useOnContext.getHand()));
                player1.displayClientMessage(Component.translatable("display.warp_pipes.linker.linked",
                        pos.getX(), pos.getY(), pos.getZ()), true);
                this.playSound(world, pos, SoundEvents.AMETHYST_CLUSTER_BREAK);

//                if (globalPos.pos().equals(pos)) {
//                    return this.reset(world, pos, tag);
//                }
                return InteractionResult.sidedSuccess(world.isClientSide);
            }
        }
        return interactionResult;
    }

    public static boolean isLinked(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        return tag != null && (tag.contains(WARP_PIPE_DIMENSION) || tag.contains(WARP_PIPE_POS));
    }

//    private InteractionResult reset(Level world, BlockPos pos, CompoundTag tag) {
//        this.playSound(world, pos, SoundEvents.AMETHYST_CLUSTER_BREAK);
//        tag.remove(WARP_PIPE_POS);
//        tag.remove(WARP_PIPE_DIMENSION);
//        tag.remove("X");
//        tag.remove("Y");
//        tag.remove("Z");
//        return InteractionResult.sidedSuccess(world.isClientSide);
//    }

    private void link(BlockPos pos, Level world, CompoundTag tag, WarpPipeBlockEntity warpPipeBE, WarpPipeBlockEntity warpPipeBEGlobal) {
        this.spawnParticles(world, pos);
        this.playSound(world, pos, SoundEvents.AMETHYST_CLUSTER_BREAK);
        warpPipeBE.setDestinationPos(warpPipeBEGlobal.getBlockPos());
        warpPipeBE.setDestinationDim(warpPipeBEGlobal.getLevel());
//        warpPipeBE.setDestinationPos(warpPipeBEGlobal.getBlockPos());
        warpPipeBEGlobal.setDestinationPos(pos);
        warpPipeBEGlobal.setDestinationDim(world);
        tag.remove(WARP_PIPE_POS);
        tag.remove(WARP_PIPE_DIMENSION);
        tag.remove("X");
        tag.remove("Y");
        tag.remove("Z");
    }

    @Nullable
    public static GlobalPos createWarpPos(CompoundTag tag) {
        Optional<ResourceKey<Level>> optional;
        boolean containsPipePos = tag.contains(WARP_PIPE_POS);
        boolean containsPipeDim = tag.contains(WARP_PIPE_DIMENSION);
        if (containsPipePos && containsPipeDim && (optional = LinkerItem.getWarpDimension(tag)).isPresent()) {
//            BlockPos pos = NbtUtils.readBlockPos(tag.getCompound(WARP_PIPE_POS));
            BlockPos pos = new BlockPos(tag.getInt("X"), tag.getInt("Y"), tag.getInt("Z"));
            return GlobalPos.of(optional.get(), pos);
        }
        return null;
    }

    private void playSound(Level world, BlockPos pos, SoundEvent soundEvent) {
        world.playSound(null, pos, soundEvent, SoundSource.PLAYERS, 1.0f, 1.0f);
    }

    private void spawnParticles(Level world, BlockPos pos) {
        if (world instanceof ServerLevel) {
            RandomSource random = world.getRandom();

            for (int i = 0; i < 4; ++i) {
                ((ServerLevel) world).sendParticles(ParticleTypes.POOF, pos.getX() + 0.5D + (0.5D * (random.nextBoolean() ? 1 : -1)),
                        pos.getY() + 1.5D, pos.getZ() + 0.5D + (0.5D * (random.nextBoolean() ? 1 : -1)),
                        1, 0.0D, 0.0D, 0.0D, 0.0D);
            }
        }
    }

    private void writeTag(ResourceKey<Level> worldKey, BlockPos pos, CompoundTag tag) {
        tag.put(WARP_PIPE_POS, NbtUtils.writeBlockPos(pos));
//        tag.putDouble("X", pos.getX());
//        tag.putDouble("Y", pos.getY());
//        tag.putDouble("Z", pos.getZ());
        Level.RESOURCE_KEY_CODEC.encodeStart(NbtOps.INSTANCE, worldKey).resultOrPartial(LOGGER::error).ifPresent(nbtElement -> tag.put(WARP_PIPE_DIMENSION, nbtElement));
    }

    public static Optional<ResourceKey<Level>> getWarpDimension(CompoundTag tag) {
        return Level.RESOURCE_KEY_CODEC.parse(NbtOps.INSTANCE, tag.get(WARP_PIPE_DIMENSION)).result();
    }

    @Override
    @ParametersAreNonnullByDefault
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> list, TooltipFlag tooltip) {
        CompoundTag tag = stack.getTag();
        if (!Bound) {
            assert tag != null;
            list.add(Component.translatable("display.warp_pipes.linker.bound_tooltip",/*tag.get(WARP_PIPE_POS)*/tag.get("X"), tag.get("Y"), tag.get("Z"),
                    tag.getString(WARP_PIPE_DIMENSION), true).withStyle(ChatFormatting.GOLD));
        }
    }
}
