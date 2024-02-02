package com.wenxin2.warp_pipes.items;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.wenxin2.warp_pipes.blocks.WarpPipeBlock;
import com.wenxin2.warp_pipes.blocks.entities.WarpPipeBlockEntity;
import com.wenxin2.warp_pipes.init.Config;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraftforge.common.Tags;

public class WrenchItem extends LinkerItem {
    private final Tier tier;
    private final float attackDamage;
    private final Multimap<Attribute, AttributeModifier> defaultModifiers;
    public WrenchItem(final Item.Properties properties, Tier tier) {
        super(properties, tier);
        this.tier = tier;
        this.attackDamage = 2.0F + tier.getAttackDamageBonus();
        float attackSpeedModifier = -1f;
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Tool modifier", this.attackDamage, AttributeModifier.Operation.ADDITION));
        builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Tool modifier", attackSpeedModifier, AttributeModifier.Operation.ADDITION));
        this.defaultModifiers = builder.build();
    }

    @Override
    public void appendHoverText(ItemStack stack, @org.jetbrains.annotations.Nullable Level world, List<Component> list, TooltipFlag tooltip) {

        list.add(Component.literal(""));

        if (Screen.hasShiftDown()) {
            list.add(Component.translatable(this.getDescriptionId() + ".tooltip.right_click").withStyle(ChatFormatting.DARK_GREEN));
            list.add(Component.translatable(this.getDescriptionId() + ".tooltip.shift_right_click").withStyle(ChatFormatting.BLUE));

        } else {
            list.add(Component.translatable(this.getDescriptionId() + ".tooltip").withStyle(ChatFormatting.GRAY).withStyle(ChatFormatting.ITALIC));
        }

        super.appendHoverText(stack, world, list, tooltip);
    }

    @Override
    public int getEnchantmentValue() {
        return this.tier.getEnchantmentValue();
    }

    @Override
    public boolean isValidRepairItem(ItemStack stack, ItemStack repairStack) {
        return repairStack.is(Tags.Items.INGOTS_COPPER) || super.isValidRepairItem(stack, repairStack);
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity livingEntity, LivingEntity hurtEntity) {
        stack.hurtAndBreak(2, hurtEntity, (entity) -> {
            entity.broadcastBreakEvent(EquipmentSlot.MAINHAND);
        });
        return true;
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot slot) {
        return slot == EquipmentSlot.MAINHAND ? this.defaultModifiers : super.getDefaultAttributeModifiers(slot);
    }

    @Override
    public boolean canPerformAction(ItemStack stack, net.minecraftforge.common.ToolAction toolAction) {
        return net.minecraftforge.common.ToolActions.DEFAULT_SWORD_ACTIONS.contains(toolAction);
    }

    private void playAnvilSound(Level world, BlockPos pos, SoundEvent soundEvent) {
        world.playSound(null, pos, soundEvent, SoundSource.PLAYERS, 0.5f, 1.0f);
    }

    private void spawnParticles(LevelAccessor worldAccessor, BlockPos pos, ParticleOptions particleOptions) {
        if (worldAccessor.isClientSide()) {
            RandomSource random = worldAccessor.getRandom();

            for (int i = 0; i < 40; ++i) {
                worldAccessor.addParticle(particleOptions,
                        pos.getX() + 0.5D + (0.5D * (random.nextBoolean() ? 1 : -1)), pos.getY() + 1.1D,
                        pos.getZ() + 0.5D + (0.5D * (random.nextBoolean() ? 1 : -1)),
                        (random.nextDouble() - 0.5D) * 2.0D, -random.nextDouble(),
                        (random.nextDouble() - 0.5D) * 2.0D);
            }
        }
    }

    @Override
    public boolean canAttackBlock(BlockState state, Level world, BlockPos pos, Player player) {
        if (!player.isCreative() && Config.CREATIVE_WRENCH.get() && !world.isClientSide) {
            message(player, Component.translatable(this.getDescriptionId() + ".requires_creative").withStyle(ChatFormatting.RED));
            return !player.isCreative();
        } else if (!world.isClientSide) {
            this.handleInteraction(player, state, world, pos, false, player.getItemInHand(InteractionHand.MAIN_HAND));
        }
        return !player.isCreative();
    }

    @Override
    public InteractionResult useOn(UseOnContext useOnContext) {
        Player player = useOnContext.getPlayer();
        Level world = useOnContext.getLevel();
        BlockPos pos = useOnContext.getClickedPos();
        BlockState state = world.getBlockState(pos);
        ItemStack item = useOnContext.getItemInHand();

        if (player != null) {
            if (state.getBlock() instanceof WarpPipeBlock && state.getValue(WarpPipeBlock.ENTRANCE) && !player.isShiftKeyDown()) {
//                if (!this.handleInteraction(player, world.getBlockState(pos), world, pos, true, useOnContext.getItemInHand())) {
//                    return InteractionResult.FAIL;
//                }
//                else {
                    item.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(useOnContext.getHand()));
                    this.playAnvilSound(world, pos, SoundEvents.ANVIL_PLACE);
                    return InteractionResult.SUCCESS;
//                }
            }
        }
        return super.useOn(useOnContext);
    }

    public boolean handleInteraction(Player player, BlockState state, LevelAccessor worldAccessor, BlockPos pos, boolean isPlayerRightClicking, ItemStack stack) {
        if (!player.isCreative() && Config.CREATIVE_WRENCH.get() && !worldAccessor.isClientSide()) {
            message(player, Component.translatable(this.getDescriptionId() + ".requires_creative").withStyle(ChatFormatting.RED));
            return false;
        } else {
            Block block = state.getBlock();
            StateDefinition<Block, BlockState> statedefinition = block.getStateDefinition();
            String registryName = BuiltInRegistries.BLOCK.getKey(block).toString();

            if (block instanceof WarpPipeBlock) {
                CompoundTag compoundtag = stack.getOrCreateTagElement("DebugProperty");
                String propertyName = compoundtag.getString(registryName);
                statedefinition.getProperty(propertyName);

                if (!propertyName.equals("closed") && !propertyName.equals("bubbles")) {
                    propertyName = "closed";
                    compoundtag.putString(registryName, propertyName);
                }

//                if (isPlayerRightClicking && state.getValue(WarpPipeBlock.ENTRANCE) && !(!player.isCreative() && Config.CREATIVE_WRENCH.get())) {
//                    if (!worldAccessor.isClientSide() && player.isShiftKeyDown()) {
//                        if (propertyName.equals("closed")) {
//                            worldAccessor.setBlock(pos, state.cycle(WarpPipeBlock.CLOSED), 8);
//                            if (state.getValue(WarpPipeBlock.CLOSED)) {
//                                message(player, Component.translatable(this.getDescriptionId() + ".closed.false")
//                                        .withStyle(ChatFormatting.GOLD));
//                            } else {
//                                message(player, Component.translatable(this.getDescriptionId() + ".closed.true")
//                                        .withStyle(ChatFormatting.GOLD));
//                            }
//                        }
//                        if (propertyName.equals("bubbles")) {
//                            worldAccessor.setBlock(pos, state.cycle(WarpPipeBlock.BUBBLES), 20);
//                            if (state.getValue(WarpPipeBlock.BUBBLES)) {
//                                message(player, Component.translatable(this.getDescriptionId() + ".bubbles.false")
//                                        .withStyle(ChatFormatting.GOLD));
//                            } else {
//                                message(player, Component.translatable(this.getDescriptionId() + ".bubbles.true")
//                                        .withStyle(ChatFormatting.GOLD));
//                            }
//                        }
//                    }
//
//                    if (propertyName.equals("closed"))
//                        this.spawnParticles(worldAccessor, pos, ParticleTypes.ENCHANTED_HIT);
//                    if (propertyName.equals("bubbles")) {
//                        this.spawnParticles(worldAccessor, pos, ParticleTypes.BUBBLE);
//                        this.spawnParticles(worldAccessor, pos, ParticleTypes.SPLASH);
//                    }
//
//                } else if (state.getValue(WarpPipeBlock.ENTRANCE)) {
//
//                    String nextProperty = "closed".equals(propertyName) ? "bubbles" : "closed";
//                    compoundtag.putString(registryName, nextProperty);
//                    Property<?> property = statedefinition.getProperty(nextProperty);
//                    if (!propertyName.equals("closed") && !worldAccessor.isClientSide()) {
//                        message(player, Component.translatable(this.getDescriptionId() + ".select.closed", property.getName()).withStyle(ChatFormatting.DARK_GREEN));
//                    }
//                    if (!propertyName.equals("bubbles") && !worldAccessor.isClientSide()) {
//                        message(player, Component.translatable(this.getDescriptionId() + ".select.bubbles", property.getName()).withStyle(ChatFormatting.DARK_GREEN));
//                    }
//                }

                return true;
            }
            return false;
        }
    }

    public static void message(Player player, Component component) {
        ((ServerPlayer)player).sendSystemMessage(component, true);
    }

    private static <T extends Comparable<T>> BlockState cycleState(BlockState state, Property<T> property, boolean b) {
        return state.setValue(property, getRelative(property.getPossibleValues(), state.getValue(property), b));
    }

    private static <T> T getRelative(Iterable<T> iterable, @Nullable T t, boolean b) {
        return (T)(b ? Util.findPreviousInIterable(iterable, t) : Util.findNextInIterable(iterable, t));
    }
}
