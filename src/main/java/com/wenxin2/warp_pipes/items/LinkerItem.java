package com.wenxin2.warp_pipes.items;

import com.mojang.logging.LogUtils;
import com.wenxin2.warp_pipes.blocks.ClearWarpPipeBlock;
import com.wenxin2.warp_pipes.blocks.WarpPipeBlock;
import com.wenxin2.warp_pipes.blocks.entities.WarpPipeBlockEntity;
import com.wenxin2.warp_pipes.init.Config;
import com.wenxin2.warp_pipes.init.SoundRegistry;
import com.wenxin2.warp_pipes.items.data_components.LinkerDataComponents;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
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
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class LinkerItem extends TieredItem {
//    public static final String WARP_POS = "WarpPos";
//    public static final String WARP_DIMENSION = "Dimension";
//    public static final String WARP_UUID = "WarpUUID";
//    public static final String POS_X = "X";
//    public static final String POS_Y = "Y";
//    public static final String POS_Z = "Z";

//    private static final Logger LOGGER = LogUtils.getLogger();
    public LinkerItem(final Properties properties, Tier tier) {
        super(tier, properties);
    }
    public boolean isBound;

    public void setBound(boolean isBound) {
        this.isBound = isBound;
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
        ItemStack stack = useOnContext.getItemInHand();
        String dimension = world.dimension().location().toString();

        // Check if the item is currently bound to a block
        boolean isBound = getIsBound(stack);

        if (player != null && !player.isCreative() && Config.CREATIVE_WRENCH_PIPE_LINKING.get()) {
            player.displayClientMessage(Component.translatable("display.warp_pipes.linker.requires_creative")
                    .withStyle(), true);
            return InteractionResult.sidedSuccess(world.isClientSide);
        } else if (player != null) {
            if ((state.getBlock() instanceof ClearWarpPipeBlock || ((state.getBlock() instanceof WarpPipeBlock)
                    && state.getValue(WarpPipeBlock.ENTRANCE))) && player.isShiftKeyDown() && blockEntity instanceof WarpPipeBlockEntity pipeBlockEntity) {

                UUID uuid = pipeBlockEntity.getUuid();

                if (!isBound) {
                    // First interaction: Bind the first block
                    setWarpPos(stack, pos);
                    setWarpDimension(stack, dimension);
                    setWarpUUID(stack, uuid);
                    setIsBound(stack, true);  // Mark the item as bound
                    this.setBound(Boolean.TRUE);

                    player.displayClientMessage(Component.translatable("display.warp_pipes.linker.bound",
                                    pos.getX(), pos.getY(), pos.getZ(), dimension)
                            .withStyle(ChatFormatting.DARK_GREEN), true);

                    this.spawnParticles(world, pos, ParticleTypes.ENCHANT);
                    this.playSound(world, pos, SoundRegistry.WRENCH_BOUND.get(), SoundSource.PLAYERS, 1.0F, 0.1F);
                } else {
                    // Second interaction: Link the blocks
                    BlockPos firstPos = getWarpPos(stack);
                    if (firstPos != null && dimension.equals(getWarpDimension(stack))) {
                        BlockEntity firstBlockEntity = world.getBlockEntity(firstPos);
                        if (firstBlockEntity instanceof WarpPipeBlockEntity firstPipeBlockEntity) {

                            // Perform the linking logic
                            this.link(firstPos, pos, stack, firstPipeBlockEntity, pipeBlockEntity);

                            player.displayClientMessage(Component.translatable("display.warp_pipes.linker.linked",
                                            pos.getX(), pos.getY(), pos.getZ(), dimension)
                                    .withStyle(ChatFormatting.GOLD), true);

                            this.spawnParticles(world, pos, ParticleTypes.ENCHANT);
                            this.playSound(world, pos, SoundRegistry.PIPES_LINKED.get(), SoundSource.BLOCKS, 1.0F, 0.1F);
                        }
                    }
                    setIsBound(stack, false);  // Reset binding
                    this.setBound(Boolean.FALSE);
                }
                return InteractionResult.sidedSuccess(world.isClientSide);
            }
        }
        return super.useOn(useOnContext);
    }

    public static boolean isLinked(ItemStack stack) {
//        CompoundTag tag = stack.getTag();
        return getWarpPos(stack) != null;
    }

    public void link(BlockPos firstPos, BlockPos secondPos, ItemStack stack, WarpPipeBlockEntity firstPipeBlockEntity, WarpPipeBlockEntity secondPipeBlockEntity) {
        UUID firstUuid = firstPipeBlockEntity.getUuid();
        UUID secondUuid = secondPipeBlockEntity.getUuid();

        // Linking logic
        firstPipeBlockEntity.setDestinationPos(Optional.of(secondPos));
        secondPipeBlockEntity.setDestinationPos(Optional.of(firstPos));

        if (firstUuid != null) {
            secondPipeBlockEntity.setWarpUuid(firstUuid);
        }
        if (secondUuid != null) {
            firstPipeBlockEntity.setWarpUuid(secondUuid);
        }

        firstPipeBlockEntity.setChanged();
        secondPipeBlockEntity.setChanged();

        clearItemComponents(stack);  // Clear tags after linking
    }

    private void writeTag(ResourceKey<Level> worldKey, BlockPos pos, CompoundTag tag, ItemStack stack) {
        tag.put(getWarpPos(stack).toString(), NbtUtils.writeBlockPos(pos));
        Level.RESOURCE_KEY_CODEC.encodeStart(NbtOps.INSTANCE, worldKey)
                .resultOrPartial(LogUtils.getLogger()::error).ifPresent(nbtElement -> tag.put(getWarpDimension(stack), nbtElement));
    }

    public void clearItemComponents(ItemStack stack) {
        setWarpPos(stack, null);
        setWarpDimension(stack, "");
        setWarpUUID(stack, null);
//        wrenchTag.remove(POS_X);
//        wrenchTag.remove(POS_Y);
//        wrenchTag.remove(POS_Z);
//        wrenchTag.remove(WARP_DIMENSION);
//        wrenchTag.remove(WARP_UUID);
    }

    public static boolean getIsBound(ItemStack stack) {
        return stack.getOrDefault(LinkerDataComponents.IS_BOUND.get(), Boolean.FALSE);
    }

    public static void setIsBound(ItemStack stack, boolean isBound) {
        stack.set(LinkerDataComponents.IS_BOUND.get(), isBound);
    }

//    public static int getPosX(ItemStack stack) {
//        return stack.getOrDefault(LinkerDataComponents.POS_X.get(), 0);
//    }
//
//    public static void setPosX(ItemStack stack, int posX) {
//        stack.set(LinkerDataComponents.POS_X.get(), posX);
//    }
//
//    public static int getPosY(ItemStack stack) {
//        return stack.getOrDefault(LinkerDataComponents.POS_Y.get(), 0);
//    }
//
//    public static void setPosY(ItemStack stack, int posY) {
//        stack.set(LinkerDataComponents.POS_Y.get(), posY);
//    }
//
//    public static int getPosZ(ItemStack stack) {
//        return stack.getOrDefault(LinkerDataComponents.POS_Z.get(), 0);
//    }
//
//    public static void setPosZ(ItemStack stack, int posZ) {
//        stack.set(LinkerDataComponents.POS_Z.get(), posZ);
//    }

    public static BlockPos getWarpPos(ItemStack stack) {
        return stack.getOrDefault(LinkerDataComponents.WARP_POS.get(), new BlockPos(0, 0, 0));
    }

    public static void setWarpPos(ItemStack stack, BlockPos warpPos) {
        stack.set(LinkerDataComponents.WARP_POS.get(), warpPos);
    }

    public static String getWarpDimension(ItemStack stack) {
        return stack.getOrDefault(LinkerDataComponents.WARP_DIMENSION.get(), "");
    }

    public static void setWarpDimension(ItemStack stack, String dimension) {
        stack.set(LinkerDataComponents.WARP_DIMENSION.get(), dimension);
    }

    public static UUID getWarpUUID(ItemStack stack) {
        UUID uuid = UUID.randomUUID();
        return stack.getOrDefault(LinkerDataComponents.WARP_UUID.get(), null);
//        if (!stack.has(LinkerDataComponents.WARP_UUID.get()))
//            return setWarpUUID(stack, uuid);
//        return stack.get(LinkerDataComponents.WARP_UUID.get());
    }

    public static UUID setWarpUUID(ItemStack stack, UUID warpUUID) {
        stack.set(LinkerDataComponents.WARP_UUID.get(), warpUUID);
        return warpUUID;
    }

    public static GlobalPos getGlobalWarpPos(ItemStack stack) {
        return stack.getOrDefault(LinkerDataComponents.GLOBAL_WARP_POS.get(), null);
    }

    public static void setGlobalWarpPos(ItemStack stack, GlobalPos globalPos) {
        stack.set(LinkerDataComponents.GLOBAL_WARP_POS.get(), globalPos);
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

//    public static Optional<ResourceKey<Level>> getWarpDimension(CompoundTag tag) {
//        return Level.RESOURCE_KEY_CODEC.parse(NbtOps.INSTANCE, tag.get(WARP_DIMENSION)).result();
//    }

    @Override
    @ParametersAreNonnullByDefault
    public void appendHoverText(ItemStack stack, Item.TooltipContext tooltipContext, List<Component> list, TooltipFlag tooltip) {
//        CompoundTag tag = stack.getTag();
        if (getBound()) {
            list.add(Component.translatable("", true));
            list.add(Component.translatable("display.warp_pipes.linker.bound_tooltip",
                    getWarpPos(stack).getX(), getWarpPos(stack).getY(), getWarpPos(stack).getZ(), getWarpDimension(stack), true)
                    .withStyle(ChatFormatting.GOLD));
        }
        else {
            list.add(Component.translatable("", true));
            list.add(Component.translatable("display.warp_pipes.linker.not_bound_tooltip", true)
                    .withStyle(ChatFormatting.GRAY).withStyle(ChatFormatting.ITALIC));
        }
    }
}
