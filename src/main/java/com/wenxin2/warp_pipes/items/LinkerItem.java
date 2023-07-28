package com.wenxin2.warp_pipes.items;

import com.mojang.logging.LogUtils;
import com.wenxin2.warp_pipes.blocks.WarpPipeBlock;
import com.wenxin2.warp_pipes.blocks.entities.WarpPipeBlockEntity;
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
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
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
    public static final String WARP_PIPE_POS = "WarpPos";
    public static final String WARP_PIPE_DIMENSION = "WarpDimension";
    private static final Logger LOGGER = LogUtils.getLogger();
    public LinkerItem(final Item.Properties properties, Tier tier) {
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
        InteractionResult interactionResult = super.useOn(useOnContext);
        Player player = useOnContext.getPlayer();
        Level world = useOnContext.getLevel();
        BlockPos pos = useOnContext.getClickedPos();
        BlockState state = world.getBlockState(pos);
        BlockEntity blockEntity = world.getBlockEntity(pos);
        ItemStack item = useOnContext.getItemInHand();
        CompoundTag tag = item.getTag();

        if ((state.getBlock() instanceof WarpPipeBlock) && state.getValue(WarpPipeBlock.ENTRANCE) && player.isShiftKeyDown())
        {
            world.setBlock(pos, state.cycle(WarpPipeBlock.CLOSED), 4);
            item.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(useOnContext.getHand()));
            this.playAnvilSound(world, pos, SoundEvents.ANVIL_PLACE);
            this.spawnParticles(world, pos, ParticleTypes.ENCHANTED_HIT);
            return InteractionResult.SUCCESS;
        }

        if (tag != null && tag.contains("Bound")) {
            isBound = tag.getBoolean("Bound");
        }

        if ((state.getBlock() instanceof WarpPipeBlock) && state.getValue(WarpPipeBlock.ENTRANCE)) {
            if (getBound() == Boolean.FALSE) {
                if (tag == null) {
                    tag = new CompoundTag();
                }

                tag.putBoolean("Bound", Boolean.TRUE);
                tag.putDouble("X", (int)pos.getX());
                tag.putDouble("Y", (int)pos.getY());
                tag.putDouble("Z", (int)pos.getZ());
                tag.put(WARP_PIPE_POS, NbtUtils.writeBlockPos(pos));
                tag.putString(WARP_PIPE_DIMENSION, world.dimension().location().toString());
                this.setBound(Boolean.TRUE);

                if (player != null) {
                    player.displayClientMessage(Component.translatable("display.warp_pipes.linker.bound",
                            tag.getInt("X"), tag.getInt("Y"), tag.getInt("Z")).withStyle(ChatFormatting.DARK_GREEN), true);
                }
                this.spawnParticles(world, pos, ParticleTypes.ENCHANT);
                this.playSound(world, pos, SoundEvents.AMETHYST_BLOCK_CHIME);
            } else if (getBound()) {
                Player player1 = useOnContext.getPlayer();
                if (tag == null) {
                    tag = new CompoundTag();
                }
                tag.putBoolean("Bound", Boolean.FALSE);
                this.setBound(Boolean.FALSE);
                this.writeTag(world.dimension(), pos, item.getOrCreateTag());

                if (player1 != null) {
                    item.hurtAndBreak(1, player1, p -> p.broadcastBreakEvent(useOnContext.getHand()));
                    player1.displayClientMessage(Component.translatable("display.warp_pipes.linker.linked",
                            tag.getInt("X"), tag.getInt("Y"), tag.getInt("Z")).withStyle(ChatFormatting.GOLD), true);
                }

                GlobalPos globalPos = LinkerItem.createWarpPos(tag);
                if (globalPos == null)
                    return interactionResult;
                BlockEntity blockEntity1 = world.getBlockEntity(globalPos.pos());
                if (blockEntity instanceof WarpPipeBlockEntity warpPipeBE && blockEntity1 instanceof WarpPipeBlockEntity warpPipeBEGlobal &&  LinkerItem.isLinked(item)) {
                    this.link(pos, world, tag, warpPipeBE, warpPipeBEGlobal);
                }
                this.spawnParticles(world, pos, ParticleTypes.ENCHANT);
                this.playSound(world, pos, SoundEvents.AMETHYST_CLUSTER_BREAK);
            }
            return InteractionResult.sidedSuccess(world.isClientSide);
        }
        return interactionResult;
    }

    public static boolean isLinked(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        return tag != null && (tag.contains(WARP_PIPE_DIMENSION) || tag.contains(WARP_PIPE_POS));
    }

    private void link(BlockPos pos, Level world, CompoundTag tag, WarpPipeBlockEntity warpPipeBE, WarpPipeBlockEntity warpPipeBEGlobal) {
        warpPipeBE.setDestinationPos(warpPipeBEGlobal.getBlockPos());
        warpPipeBE.setDestinationDim(warpPipeBEGlobal.getLevel());
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
            BlockPos pos = new BlockPos(tag.getInt("X"), tag.getInt("Y"), tag.getInt("Z"));
            return GlobalPos.of(optional.get(), pos);
        }
        return null;
    }

    private void playSound(Level world, BlockPos pos, SoundEvent soundEvent) {
        world.playSound(null, pos, soundEvent, SoundSource.PLAYERS, 100.0f, 1.0f);
    }

    private void playAnvilSound(Level world, BlockPos pos, SoundEvent soundEvent) {
        world.playSound(null, pos, soundEvent, SoundSource.PLAYERS, 0.5f, 1.0f);
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
        tag.put(WARP_PIPE_POS, NbtUtils.writeBlockPos(pos));
        Level.RESOURCE_KEY_CODEC.encodeStart(NbtOps.INSTANCE, worldKey).resultOrPartial(LOGGER::error).ifPresent(nbtElement -> tag.put(WARP_PIPE_DIMENSION, nbtElement));
    }

    public static Optional<ResourceKey<Level>> getWarpDimension(CompoundTag tag) {
        return Level.RESOURCE_KEY_CODEC.parse(NbtOps.INSTANCE, tag.get(WARP_PIPE_DIMENSION)).result();
    }

    @Override
    @ParametersAreNonnullByDefault
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> list, TooltipFlag tooltip) {
        CompoundTag tag = stack.getTag();
        if (getBound()) {
            assert tag != null;
            if (tag.contains(WARP_PIPE_POS)) {
                list.add(Component.translatable("display.warp_pipes.linker.bound_tooltip", tag.getInt("X"), tag.getInt("Y"), tag.getInt("Z"),
                        tag.getString(WARP_PIPE_DIMENSION), true).withStyle(ChatFormatting.GOLD));
            }
        }
        else {
            list.add(Component.translatable("display.warp_pipes.linker.not_bound_tooltip", true).withStyle(ChatFormatting.GRAY));
        }
    }
}
