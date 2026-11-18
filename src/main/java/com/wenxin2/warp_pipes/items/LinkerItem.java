package com.wenxin2.warp_pipes.items;

import com.wenxin2.warp_pipes.blocks.ClearWarpPipeBlock;
import com.wenxin2.warp_pipes.blocks.WarpPipeBlock;
import com.wenxin2.warp_pipes.blocks.entities.BaseWarpBlockEntity;
import com.wenxin2.warp_pipes.registries.ConfigRegistry;
import com.wenxin2.warp_pipes.registries.SoundRegistry;
import com.wenxin2.warp_pipes.registries.DataComponentRegistry;
import com.wenxin2.warp_pipes.utils.WarpLinkParticles;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.List;
import java.util.UUID;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
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
import net.minecraft.world.phys.Vec3;

public class LinkerItem extends TieredItem {
    private record PipePair(BlockPos first, BlockPos second, double distance) {}

    public LinkerItem(final Properties properties, Tier tier) {
        super(tier, properties);
    }

    private static boolean getLinkableBlock(BlockState state) {
        if (state.getBlock() instanceof WarpPipeBlock && state.getValue(WarpPipeBlock.ENTRANCE))
            return true;
        else if (state.getBlock() instanceof WarpPipeBlock && !state.getValue(WarpPipeBlock.ENTRANCE))
            return false;
        else if (state.getBlock() instanceof ClearWarpPipeBlock)
            return true;
        else return true;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext tooltipContext, List<Component> list, TooltipFlag tooltip) {
        if (getIsBound(stack) && stack.has(DataComponentRegistry.WARP_POS)) {
            list.add(Component.literal(""));

            list.add(Component.translatable(this.getDescriptionId() + ".tooltip.bound", true)
                    .withStyle(ChatFormatting.GOLD));

            if (stack.has(DataComponentRegistry.WARP_BLOCK.get()))
                list.add(Component.translatable(this.getDescriptionId() + ".tooltip.bound.block",
                        getWarpBlock(stack).name(), true).withStyle(ChatFormatting.GRAY));

            if (stack.has(DataComponentRegistry.WARP_POS)) {
                list.add(Component.translatable(this.getDescriptionId() + ".tooltip.bound.x",
                        getWarpPos(stack).getX(), true).withStyle(ChatFormatting.GRAY));
                list.add(Component.translatable(this.getDescriptionId() + ".tooltip.bound.y",
                        getWarpPos(stack).getY(), true).withStyle(ChatFormatting.GRAY));
                list.add(Component.translatable(this.getDescriptionId() + ".tooltip.bound.z",
                        getWarpPos(stack).getZ(), true).withStyle(ChatFormatting.GRAY));
            }
        }
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
        float pitch = 0.9F + world.random.nextFloat() * 0.2F;

        if (player != null && !player.isCreative() && ConfigRegistry.CREATIVE_WRENCH_LINKING.get()) {
            player.displayClientMessage(Component.translatable(this.getDescriptionId() + ".message.requires_creative"), true);
            return InteractionResult.SUCCESS;
        } else if (player != null) {
            if (player.isShiftKeyDown() && blockEntity instanceof BaseWarpBlockEntity warpBE
                    && getLinkableBlock(state)) {
                UUID uuid = warpBE.getUUID();

                if (warpBE.isWaxed() && ConfigRegistry.WAX_DISABLES_WARP_LINKING.get()) {
                    player.displayClientMessage(Component.translatable(this.getDescriptionId() + ".message.waxed",
                            state.getBlock().getName()).withStyle(ChatFormatting.GOLD), true);
                    return InteractionResult.SUCCESS;
                } else if (!getIsBound(stack) || !stack.has(DataComponentRegistry.WARP_POS)) {

                    if (!world.isClientSide && uuid == null) {
                        uuid = UUID.randomUUID();
                        warpBE.setUUID(uuid);
                        warpBE.setChanged();
                    }
                    // First interaction: Bind the first block
                    setWarpBlock(stack, pos, state);
                    setWarpPos(stack, pos);
                    setWarpDimension(stack, dimension);
                    setWarpUUID(stack, uuid);
                    setIsBound(stack, true);  // Mark the item as bound

                    player.displayClientMessage(Component.translatable(this.getDescriptionId() + ".message.bound",
                            state.getBlock().getName()).withStyle(ChatFormatting.GREEN), true);

                    if (world instanceof ServerLevel serverLevel)
                        WarpLinkParticles.outlineWarp(serverLevel, pos, null);
                    this.playSound(world, pos, SoundRegistry.WRENCH_WARP_LINKED.get(), SoundSource.BLOCKS, 1.0F, pitch);
                } else {

                    if (!world.isClientSide && uuid == null) {
                        uuid = UUID.randomUUID();
                        warpBE.setUUID(uuid);
                        warpBE.setChanged();
                    }

                    // Second interaction: Link the blocks
                    BlockPos firstPos = getWarpPos(stack);
                    BlockState firstState = world.getBlockState(firstPos);
                    String firstDim = getWarpDimension(stack);

                    //  if (dimension.equals(getWarpDimension(stack))) {
                    BlockEntity firstBE = world.getBlockEntity(firstPos);
                    if (firstBE instanceof BaseWarpBlockEntity firstWarpBE) {

                        // Perform the linking logic
                        if (!LinkerItem.linkPipeGroups(this, stack, world, firstPos, firstState, pos, state))
                            this.link(stack, firstWarpBE, warpBE);

                        player.displayClientMessage(Component.translatable(this.getDescriptionId() + ".message.linked_warp_block",
                                state.getBlock().getName(), firstState.getBlock().getName()).withStyle(ChatFormatting.GOLD), true);

                        if (world instanceof ServerLevel serverLevel) {
                            WarpLinkParticles.outlineWarp(serverLevel, firstPos, null);
                            WarpLinkParticles.outlineWarp(serverLevel, pos, null);
                        }
                        this.playSound(world, pos, SoundRegistry.WRENCH_WARP_CREATED.get(), SoundSource.BLOCKS, 1.0F, pitch);

                        if (!world.isClientSide)
                            stack.hurtAndBreak(2, player, LivingEntity.getSlotForHand(useOnContext.getHand()));
                    }
                    //  }
                    setIsBound(stack, false);  // Reset binding
                }
                return InteractionResult.sidedSuccess(true);
            }
        }
        return super.useOn(useOnContext);
    }

    public void link(ItemStack stack, BaseWarpBlockEntity firstPipeBlockEntity, BaseWarpBlockEntity secondPipeBlockEntity) {
        UUID firstUuid = firstPipeBlockEntity.getUUID();
        UUID secondUuid = secondPipeBlockEntity.getUUID();

        BlockPos firstPos = firstPipeBlockEntity.getBlockPos();
        BlockPos secondPos = secondPipeBlockEntity.getBlockPos();
        ResourceKey<Level> firstDim = firstPipeBlockEntity.getDestinationDim();
        ResourceKey<Level> secondDim = secondPipeBlockEntity.getDestinationDim();

        // Linking logic
        firstPipeBlockEntity.setDestinationPos(secondPos);
        secondPipeBlockEntity.setDestinationPos(firstPos);

        if (secondDim != null)
            firstPipeBlockEntity.setDestinationDim(secondDim);
        if (firstDim != null)
            secondPipeBlockEntity.setDestinationDim(firstDim);

        if (firstUuid != null)
            secondPipeBlockEntity.setWarpUuid(firstUuid);
        if (secondUuid != null)
            firstPipeBlockEntity.setWarpUuid(secondUuid);

        firstPipeBlockEntity.markUpdated();
        secondPipeBlockEntity.markUpdated();

        if (firstPipeBlockEntity.getLevel() instanceof ServerLevel serverLevel)
            WarpLinkParticles.sendLinkVibration(serverLevel, Vec3.atCenterOf(firstPos), Vec3.atCenterOf(secondPos));
        clearItemComponents(stack);
    }

    public void clearItemComponents(ItemStack stack) {
        setWarpPos(stack, null);
        setWarpDimension(stack, "");
        setWarpUUID(stack, null);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return LinkerItem.getIsBound(stack) || super.isFoil(stack);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, level, entity, slot, selected);

        if (level instanceof ServerLevel serverLevel && entity instanceof ServerPlayer player && level.getGameTime() % 10 == 0
                && (selected || player.getOffhandItem() == stack) && LinkerItem.getIsBound(stack)) {
            BlockPos warpPos = LinkerItem.getWarpPos(stack);

            if (warpPos != null)
                WarpLinkParticles.outlineWarp(serverLevel, warpPos, player);
        }
    }

    public static boolean linkPipeGroups(LinkerItem linker, ItemStack stack, Level level, BlockPos firstPos, BlockState firstState,
                                         BlockPos secondPos, BlockState secondState) {
        List<BlockPos> firstGroup = WarpPipeBlock.getEntranceGroup(level, firstPos, firstState);
        List<BlockPos> secondGroup = WarpPipeBlock.getEntranceGroup(level, secondPos, secondState);

        if (firstGroup.isEmpty() != secondGroup.isEmpty() && Math.max(firstGroup.size(), secondGroup.size()) > 1)
            return LinkerItem.linkPipeGroupToWarp(linker, stack, level, firstGroup.isEmpty() ? secondPos : firstPos,
                    firstGroup.isEmpty() ? secondGroup : firstGroup, firstGroup.isEmpty() ? firstPos : secondPos);

        if (firstGroup.isEmpty() || secondGroup.isEmpty() || (firstGroup.size() == 1 && secondGroup.size() == 1))
            return false;

        Direction firstFacing = firstState.getValue(WarpPipeBlock.FACING);
        Direction secondFacing = secondState.getValue(WarpPipeBlock.FACING);
        List<PipePair> pairs = new ArrayList<>();

        for (BlockPos first : firstGroup) {
            double[] firstOffset = LinkerItem.getLocalPipeOffset(first.subtract(firstPos), firstFacing, true);

            for (BlockPos second : secondGroup) {
                double[] secondOffset = LinkerItem.getLocalPipeOffset(second.subtract(secondPos), secondFacing, false);
                pairs.add(new PipePair(first, second, Math.hypot(firstOffset[0] - secondOffset[0], firstOffset[1] - secondOffset[1])));
            }
        }

        pairs.sort(Comparator.comparingDouble(PipePair::distance));
        Set<BlockPos> linkedFirst = new HashSet<>();
        Set<BlockPos> linkedSecond = new HashSet<>();

        for (PipePair pair : pairs) {
            if (linkedFirst.contains(pair.first()) || linkedSecond.contains(pair.second())
                    || !(level.getBlockEntity(pair.first()) instanceof BaseWarpBlockEntity firstBE)
                    || !(level.getBlockEntity(pair.second()) instanceof BaseWarpBlockEntity secondBE))
                continue;

            if (!level.isClientSide) {
                if (firstBE.getUUID() == null)
                    firstBE.setUUID(UUID.randomUUID());
                if (secondBE.getUUID() == null)
                    secondBE.setUUID(UUID.randomUUID());
            }

            linker.link(stack, firstBE, secondBE);
            linkedFirst.add(pair.first());
            linkedSecond.add(pair.second());
        }

        for (PipePair pair : pairs) {
            if (linkedFirst.contains(pair.first()) || !linkedSecond.contains(pair.second())
                    || !(level.getBlockEntity(pair.first()) instanceof BaseWarpBlockEntity firstBE)
                    || !(level.getBlockEntity(pair.second()) instanceof BaseWarpBlockEntity secondBE))
                continue;

            if (!level.isClientSide && firstBE.getUUID() == null)
                firstBE.setUUID(UUID.randomUUID());

            firstBE.setDestinationPos(pair.second());
            if (secondBE.getDestinationDim() != null)
                firstBE.setDestinationDim(secondBE.getDestinationDim());
            if (secondBE.getUUID() != null)
                firstBE.setWarpUuid(secondBE.getUUID());
            firstBE.markUpdated();

            if (level instanceof ServerLevel serverLevel)
                WarpLinkParticles.sendLinkVibration(serverLevel, Vec3.atCenterOf(pair.first()), Vec3.atCenterOf(pair.second()));
            linkedFirst.add(pair.first());
        }
        return true;
    }

    private static boolean linkPipeGroupToWarp(LinkerItem linker, ItemStack stack, Level level, BlockPos pipePos,
                                               List<BlockPos> group, BlockPos warpPos) {
        if (!(level.getBlockEntity(pipePos) instanceof BaseWarpBlockEntity pipeBE)
                || !(level.getBlockEntity(warpPos) instanceof BaseWarpBlockEntity warpBE))
            return false;

        if (!level.isClientSide) {
            if (pipeBE.getUUID() == null)
                pipeBE.setUUID(UUID.randomUUID());
            if (warpBE.getUUID() == null)
                warpBE.setUUID(UUID.randomUUID());
        }

        linker.link(stack, pipeBE, warpBE);

        for (BlockPos pos : group) {
            if (pos.equals(pipePos) || !(level.getBlockEntity(pos) instanceof BaseWarpBlockEntity groupBE))
                continue;

            if (!level.isClientSide && groupBE.getUUID() == null)
                groupBE.setUUID(UUID.randomUUID());

            groupBE.setDestinationPos(warpPos);
            if (pipeBE.getDestinationDim() != null)
                groupBE.setDestinationDim(pipeBE.getDestinationDim());
            if (warpBE.getUUID() != null)
                groupBE.setWarpUuid(warpBE.getUUID());
            groupBE.markUpdated();

            if (level instanceof ServerLevel serverLevel)
                WarpLinkParticles.sendLinkVibration(serverLevel, Vec3.atCenterOf(pos), Vec3.atCenterOf(warpPos));
        }
        return true;
    }

    private static double[] getLocalPipeOffset(BlockPos offset, Direction facing, boolean entering) {
        Direction right;
        Direction up;

        if (facing.getAxis().isHorizontal()) {
            right = entering ? facing.getClockWise() : facing.getCounterClockWise();
            up = Direction.UP;
        } else {
            right = entering ? Direction.EAST : Direction.WEST;
            up = Direction.SOUTH;
        }

        return new double[] {
                offset.getX() * right.getStepX() + offset.getY() * right.getStepY() + offset.getZ() * right.getStepZ(),
                offset.getX() * up.getStepX() + offset.getY() * up.getStepY() + offset.getZ() * up.getStepZ()};
    }

    public static boolean getIsBound(ItemStack stack) {
        return stack.getOrDefault(DataComponentRegistry.IS_BOUND.get(), Boolean.FALSE);
    }

    public static void setIsBound(ItemStack stack, boolean isBound) {
        stack.set(DataComponentRegistry.IS_BOUND.get(), isBound);
    }

    public static BlockPos getWarpPos(ItemStack stack) {
        return stack.getOrDefault(DataComponentRegistry.WARP_POS, null);
    }

    public static void setWarpPos(ItemStack stack, BlockPos warpPos) {
        stack.set(DataComponentRegistry.WARP_POS, warpPos);
    }

    public static DataComponentRegistry.WarpTarget getWarpBlock(ItemStack stack) {
        return stack.getOrDefault(DataComponentRegistry.WARP_BLOCK, new DataComponentRegistry.WarpTarget(null, null));
    }

    public static void setWarpBlock(ItemStack stack, BlockPos warpPos, BlockState blockState) {
        String blockName = blockState.getBlock().getName().getString();
        stack.set(DataComponentRegistry.WARP_BLOCK.get(), new DataComponentRegistry.WarpTarget(warpPos, blockName));
    }

    public static String getWarpDimension(ItemStack stack) {
        return stack.getOrDefault(DataComponentRegistry.WARP_DIMENSION.get(), "");
    }

    public static void setWarpDimension(ItemStack stack, String dimension) {
        stack.set(DataComponentRegistry.WARP_DIMENSION.get(), dimension);
    }

    public static UUID getWarpUUID(ItemStack stack) {
        UUID uuid = UUID.randomUUID();
        return stack.getOrDefault(DataComponentRegistry.WARP_UUID.get(), null);
    }

    public static UUID setWarpUUID(ItemStack stack, UUID warpUUID) {
        stack.set(DataComponentRegistry.WARP_UUID.get(), warpUUID);
        return warpUUID;
    }

    public static GlobalPos getGlobalWarpPos(ItemStack stack) {
        return stack.getOrDefault(DataComponentRegistry.GLOBAL_WARP_POS.get(), null);
    }

    public static void setGlobalWarpPos(ItemStack stack, GlobalPos globalPos) {
        stack.set(DataComponentRegistry.GLOBAL_WARP_POS.get(), globalPos);
    }


    public void playSound(Level world, BlockPos pos, SoundEvent soundEvent, SoundSource source, float volume, float pitch) {
        world.playSound(null, pos, soundEvent, source, volume, pitch);
    }

    public void spawnParticles(Level world, BlockPos pos, ParticleOptions particleOptions) {
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
}