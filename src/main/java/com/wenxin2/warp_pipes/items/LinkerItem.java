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
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
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
        ItemStack stack = useOnContext.getItemInHand();
//        CompoundTag wrenchTag = stack.getOrDefault(LinkerDataComponents.COMPONENTS);
        String dimension = world.dimension().location().toString();

//        if (stack.get(LinkerDataComponents.IS_BOUND) != null)
            isBound = getIsBound(stack);

        if (player != null && !player.isCreative() && Config.CREATIVE_WRENCH_PIPE_LINKING.get()) {
            player.displayClientMessage(Component.translatable("display.warp_pipes.linker.requires_creative")
                    .withStyle(), true);
            return InteractionResult.sidedSuccess(world.isClientSide);
        } else if (player != null) {
            if ((state.getBlock() instanceof ClearWarpPipeBlock || ((state.getBlock() instanceof WarpPipeBlock)
                    && state.getValue(WarpPipeBlock.ENTRANCE))) && player.isShiftKeyDown() && blockEntity instanceof WarpPipeBlockEntity pipeBlockEntity) {
                UUID uuid = pipeBlockEntity.getUuid();
                if (getBound() == Boolean.FALSE) {
//                    if (wrenchTag == null) {
//                        wrenchTag = new CompoundTag();
//                    }

                    BlockPos warpPos = new BlockPos(pos.getX(), pos.getY(), pos.getZ());

                    setIsBound(stack, Boolean.TRUE);
//                    setPosX(stack, pos.getX());
//                    setPosY(stack, pos.getY());
//                    setPosZ(stack, pos.getZ());
                    setWarpPos(stack, warpPos);
                    setWarpDimension(stack, dimension);

                    if (uuid != null)
                        setWarpUUID(stack, uuid);
                    this.setBound(Boolean.TRUE);

                    player.displayClientMessage(Component.translatable("display.warp_pipes.linker.bound",
                                    getWarpPos(stack).getX(), getWarpPos(stack).getY(), getWarpPos(stack).getZ(), getWarpDimension(stack))
                            .withStyle(ChatFormatting.DARK_GREEN), true);
                    this.spawnParticles(world, pos, ParticleTypes.ENCHANT);
                    this.playSound(world, pos, SoundRegistry.WRENCH_BOUND.get(), SoundSource.PLAYERS, 1.0F, 0.1F);
                } else if (getBound()) {
                    Player player1 = useOnContext.getPlayer();
//                    if (wrenchTag == null) {
//                        wrenchTag = new CompoundTag();
//                    }
                    setIsBound(stack, Boolean.FALSE);
                    this.setBound(Boolean.FALSE);
                    setGlobalWarpPos(stack, GlobalPos.of(world.dimension(), pos));

                    BlockPos warpPos = new BlockPos(pos.getX(), pos.getY(), pos.getZ());

                    if (player1 != null) {
                        stack.hurtAndBreak(1, player1, LivingEntity.getSlotForHand(useOnContext.getHand()));
                        player1.displayClientMessage(Component.translatable("display.warp_pipes.linker.linked",
                                        getWarpPos(stack).getX(), getWarpPos(stack).getY(), getWarpPos(stack).getZ(), getWarpDimension(stack))
                                .withStyle(ChatFormatting.GOLD), true);
                    }

                    GlobalPos globalPos = LinkerItem.getGlobalWarpPos(stack);
//                    if (globalPos == null)
//                        return super.useOn(useOnContext);
                    BlockEntity blockEntity1 = world.getBlockEntity(globalPos.pos());

                    WarpPipeBlockEntity warpPipeBE = (WarpPipeBlockEntity) blockEntity;
                    if (blockEntity1 instanceof WarpPipeBlockEntity warpPipeBEGlobal && LinkerItem.isLinked(stack)) {

                        setWarpPos(stack, warpPos);
//                        wrenchTag.put(WarpPipeBlockEntity.WARP_POS, NbtUtils.writeBlockPos(warpPos));
                        if (uuid != null)
                            setWarpUUID(stack, getWarpUUID(stack));
//                            wrenchTag.putUUID(WarpPipeBlockEntity.WARP_UUID, wrenchTag.getUUID(WARP_UUID));
                        this.link(pos, world, stack, warpPipeBE, warpPipeBEGlobal);
                    } else {
                        if (player1 != null) {
                            player1.displayClientMessage(Component.translatable("display.warp_pipes.linker.dimension_fail",
                                            getWarpPos(stack).getX(), getWarpPos(stack).getY(), getWarpPos(stack).getZ(), getWarpDimension(stack))
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

    public static boolean getIsBound(ItemStack stack) {
        return stack.getOrDefault(LinkerDataComponents.IS_BOUND, Boolean.FALSE);
    }

    public static void setIsBound(ItemStack stack, boolean isBound) {
        stack.set(LinkerDataComponents.IS_BOUND, isBound);
    }

//    public static int getPosX(ItemStack stack) {
//        return stack.getOrDefault(LinkerDataComponents.POS_X, 0);
//    }
//
//    public static void setPosX(ItemStack stack, int posX) {
//        stack.set(LinkerDataComponents.POS_X, posX);
//    }
//
//    public static int getPosY(ItemStack stack) {
//        return stack.getOrDefault(LinkerDataComponents.POS_Y, 0);
//    }
//
//    public static void setPosY(ItemStack stack, int posY) {
//        stack.set(LinkerDataComponents.POS_Y, posY);
//    }
//
//    public static int getPosZ(ItemStack stack) {
//        return stack.getOrDefault(LinkerDataComponents.POS_Z, 0);
//    }
//
//    public static void setPosZ(ItemStack stack, int posZ) {
//        stack.set(LinkerDataComponents.POS_Z, posZ);
//    }

    public static BlockPos getWarpPos(ItemStack stack) {
        return stack.getOrDefault(LinkerDataComponents.WARP_POS, null);
    }

    public static void setWarpPos(ItemStack stack, BlockPos warpPos) {
        stack.set(LinkerDataComponents.WARP_POS, warpPos);
    }

    public static String getWarpDimension(ItemStack stack) {
        return stack.getOrDefault(LinkerDataComponents.WARP_DIMENSION, "");
    }

    public static void setWarpDimension(ItemStack stack, String dimension) {
        stack.set(LinkerDataComponents.WARP_DIMENSION, dimension);
    }

    public static UUID getWarpUUID(ItemStack stack) {
        UUID uuid = UUID.randomUUID();
        return stack.getOrDefault(LinkerDataComponents.WARP_UUID, null);
//        if (!stack.has(LinkerDataComponents.WARP_UUID))
//            return setWarpUUID(stack, uuid);
//        return stack.get(LinkerDataComponents.WARP_UUID);
    }

    public static UUID setWarpUUID(ItemStack stack, UUID warpUUID) {
        stack.set(LinkerDataComponents.WARP_UUID, warpUUID);
        return warpUUID;
    }

    public static GlobalPos getGlobalWarpPos(ItemStack stack) {
        return stack.getOrDefault(LinkerDataComponents.GLOBAL_WARP_POS, null);
    }

    public static void setGlobalWarpPos(ItemStack stack, GlobalPos globalPos) {
        stack.set(LinkerDataComponents.GLOBAL_WARP_POS, globalPos);
    }

    public void clearTags(ItemStack stack) {
        setWarpPos(stack, null);
        setWarpDimension(stack, "");
        setWarpUUID(stack, null);
//        wrenchTag.remove(POS_X);
//        wrenchTag.remove(POS_Y);
//        wrenchTag.remove(POS_Z);
//        wrenchTag.remove(WARP_DIMENSION);
//        wrenchTag.remove(WARP_UUID);
    }

    public static boolean isLinked(ItemStack stack) {
//        CompoundTag tag = stack.getTag();
        return getWarpPos(stack) != null;
    }

    public void link(BlockPos pos, Level world, ItemStack stack, WarpPipeBlockEntity warpPipeBE, WarpPipeBlockEntity warpPipeBEGlobal) {
        // System.out.println("Current Dimension: " + world.dimension());
        UUID uuid = warpPipeBE.getUuid();
        UUID uuidGlobal = warpPipeBEGlobal.getUuid();

        warpPipeBE.setDestinationPos(warpPipeBEGlobal.getBlockPos());
        if (uuid != null)
            warpPipeBE.setWarpUuid(getWarpUUID(stack));
        warpPipeBE.setChanged();
        if (warpPipeBEGlobal.getLevel() != null) {
            warpPipeBE.setDestinationDim(warpPipeBEGlobal.getLevel().dimension());
            // System.out.println("Global Dimension: " + warpPipeBEGlobal.getLevel().dimension());
        } /*else System.out.println("World is null!");*/

        warpPipeBEGlobal.setDestinationPos(pos);
        warpPipeBEGlobal.setDestinationDim(world.dimension());
        if (uuidGlobal != null)
            warpPipeBEGlobal.setWarpUuid(warpPipeBE.getUuid());
        warpPipeBEGlobal.setChanged();
        this.clearTags(stack);
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
        if (getBound() && getWarpPos(stack) != null) {
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
