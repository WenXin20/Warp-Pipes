package com.wenxin2.warp_pipes.items;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.wenxin2.warp_pipes.blocks.WarpPipeBlock;
import java.util.Collection;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
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
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
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
    public int getEnchantmentValue() {
        return this.tier.getEnchantmentValue();
    }

    private void spawnParticles(LevelAccessor worldAccessor, BlockPos pos, ParticleOptions particleOptions) {
        if (worldAccessor.isClientSide()) {
            RandomSource random = worldAccessor.getRandom();

            for (int i = 0; i < 40; ++i) {
                worldAccessor.addParticle(particleOptions,
                        pos.getX() + 0.5D + (0.5D * (random.nextBoolean() ? 1 : -1)), pos.getY() + 1.5D,
                        pos.getZ() + 0.5D + (0.5D * (random.nextBoolean() ? 1 : -1)),
                        (random.nextDouble() - 0.5D) * 2.0D, -random.nextDouble(),
                        (random.nextDouble() - 0.5D) * 2.0D);
            }
        }
    }

    @Override
    public boolean canAttackBlock(BlockState state, Level world, BlockPos pos, Player player) {
        if (!world.isClientSide) {
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
            if ((state.getBlock() instanceof WarpPipeBlock) && state.getValue(WarpPipeBlock.ENTRANCE) && player.isShiftKeyDown()) {
                if (!this.handleInteraction(player, world.getBlockState(pos), world, pos, true, useOnContext.getItemInHand())) {
                    return InteractionResult.FAIL;
                }
                else {
                    item.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(useOnContext.getHand()));
                    this.playAnvilSound(world, pos, SoundEvents.ANVIL_PLACE);
                    return InteractionResult.SUCCESS;
                }
            }
        }
        return super.useOn(useOnContext);
    }

    private boolean handleInteraction(Player player, BlockState state, LevelAccessor worldAccessor, BlockPos pos, boolean cycleProperty, ItemStack stack) {
        if (!player.canUseGameMasterBlocks()) {
            return false;
        } else {
            Block block = state.getBlock();
            StateDefinition<Block, BlockState> statedefinition = block.getStateDefinition();
            String s = Registry.BLOCK.getKey(block).toString();

            if (!(block instanceof WarpPipeBlock)) {
                message(player, Component.translatable(this.getDescriptionId() + ".empty", s));
                return false;
            } else {
                CompoundTag compoundtag = stack.getOrCreateTagElement("DebugProperty");
                String s1 = compoundtag.getString(s);
                Property<?> property = statedefinition.getProperty(s1);

                if (!s1.equals("closed") && !s1.equals("bubbles")) {
                    s1 = "closed";
                    compoundtag.putString(s, s1);
                }

                if (cycleProperty && state.getValue(WarpPipeBlock.ENTRANCE)) {
                    if (!worldAccessor.isClientSide()) {
                        BlockState stateCycle = cycleState(state, property, player.isSecondaryUseActive());
                        worldAccessor.setBlock(pos, stateCycle, 18);
                        message(player, Component.translatable(this.getDescriptionId() + ".update", property.getName(), getNameHelper(stateCycle, property)));
                    }

                    if (s1.equals("closed"))
                        this.spawnParticles(worldAccessor, pos, ParticleTypes.ENCHANTED_HIT);
                    if (s1.equals("bubbles")) {
                        this.spawnParticles(worldAccessor, pos, ParticleTypes.BUBBLE);
                        this.spawnParticles(worldAccessor, pos, ParticleTypes.SPLASH);
                    }

                } else if (state.getValue(WarpPipeBlock.ENTRANCE)) {

                    String nextProperty = "closed".equals(s1) ? "bubbles" : "closed";
                    compoundtag.putString(s, nextProperty);
                    property = statedefinition.getProperty(nextProperty);
                    message(player, Component.translatable(this.getDescriptionId() + ".select", property.getName(), getNameHelper(state, property)));
                }

                return true;
            }
        }
    }

    private static void message(Player player, Component component) {
        ((ServerPlayer)player).sendSystemMessage(component, true);
    }

    private static <T extends Comparable<T>> BlockState cycleState(BlockState state, Property<T> property, boolean b) {
        return state.setValue(property, getRelative(property.getPossibleValues(), state.getValue(property), b));
    }

    private static <T> T getRelative(Iterable<T> iterable, @Nullable T t, boolean b) {
        return (T)(b ? Util.findPreviousInIterable(iterable, t) : Util.findNextInIterable(iterable, t));
    }

    private static <T extends Comparable<T>> String getNameHelper(BlockState state, Property<T> property) {
        return property.getName(state.getValue(property));
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
}
