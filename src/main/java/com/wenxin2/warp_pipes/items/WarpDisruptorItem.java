package com.wenxin2.warp_pipes.items;

import com.wenxin2.warp_pipes.blocks.WarpPipeBlock;
import com.wenxin2.warp_pipes.blocks.entities.BaseWarpBlockEntity;
import com.wenxin2.warp_pipes.registries.ConfigRegistry;
import com.wenxin2.warp_pipes.registries.DataAttachmentRegistry;
import com.wenxin2.warp_pipes.utils.ServerParticleUtils;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.common.Tags;
import org.jetbrains.annotations.NotNull;

public class WarpDisruptorItem extends Item {
    public WarpDisruptorItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean isValidRepairItem(ItemStack stack, ItemStack repairStack) {
        return repairStack.is(Tags.Items.INGOTS_GOLD) || super.isValidRepairItem(stack, repairStack);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext tooltipContext, List<Component> list, TooltipFlag tooltip) {
        MutableComponent rCText = Component.translatable(this.getDescriptionId() + ".tooltip.right_click.selected");
        MutableComponent shiftRCText = Component.translatable(this.getDescriptionId() + ".tooltip.shift_right_click.prevents");

        if (Screen.hasShiftDown()) {
            list.add(Component.literal(""));
            list.add(Component.translatable(this.getDescriptionId() + ".tooltip.right_click"));

            rCText = rCText.append(Component.translatable(this.getDescriptionId() + ".tooltip.right_click.mob"));
            if (!ConfigRegistry.DISABLE_PLAYER_WARP_DISRUPTING.get())
                rCText = rCText.append(Component.translatable(this.getDescriptionId() + ".tooltip.right_click.player"));
            rCText = rCText.append(Component.translatable(this.getDescriptionId() + ".tooltip.right_click.warping"));
            list.add(rCText);

            list.add(Component.translatable(this.getDescriptionId() + ".tooltip.shift_right_click"));

            shiftRCText = shiftRCText.append(Component.translatable(this.getDescriptionId() + ".tooltip.shift_right_click.pipe"));
            shiftRCText = shiftRCText.append(Component.translatable(this.getDescriptionId() + ".tooltip.shift_right_click.warping"));
            list.add(shiftRCText);
        } else
            list.add(Component.translatable(this.getDescriptionId() + ".tooltip"));

        super.appendHoverText(stack, tooltipContext, list, tooltip);
    }

    @NotNull
    @Override
    public InteractionResult useOn(UseOnContext useOnContext) {
        Player player = useOnContext.getPlayer();
        Level world = useOnContext.getLevel();
        BlockPos pos = useOnContext.getClickedPos();
        BlockState state = world.getBlockState(pos);
        ItemStack stack = useOnContext.getItemInHand();
        BlockEntity blockEntity = world.getBlockEntity(pos);
        
        if (blockEntity instanceof BaseWarpBlockEntity warpBE && !warpBE.preventWarp
                && world instanceof ServerLevel serverWorld) {
            warpBE.setPreventWarp(Boolean.TRUE);
            ServerParticleUtils.spawnThreeLayerBlockParticles(ParticleTypes.CRIMSON_SPORE, serverWorld, pos, 16);

            if (player != null) {
                if (warpBE.isWaxed() && ConfigRegistry.WAX_DISABLES_WARP_LINKING.get()) {
                    player.displayClientMessage(Component.translatable(this.getDescriptionId() + ".message.waxed",
                            state.getBlock().getName()).withStyle(ChatFormatting.GOLD), true);
                    return InteractionResult.sidedSuccess(Boolean.TRUE);
                } else if (state.getBlock() instanceof WarpPipeBlock)
                    player.displayClientMessage(Component.translatable(this.getDescriptionId() + ".message.prevent_pipe_warp"), true);
                if (!player.isCreative())
                    stack.hurtAndBreak(1, player, Player.getSlotForHand(player.getUsedItemHand()));
            }
            warpBE.markUpdated();
            world.sendBlockUpdated(pos, state, state, Block.UPDATE_ALL);
            return InteractionResult.SUCCESS;
        }
        return super.useOn(useOnContext);
    }

    @NotNull
    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity livingEntity, InteractionHand hand) {
        if (!livingEntity.getData(DataAttachmentRegistry.PREVENT_WARP)) {
            if (livingEntity instanceof Player && !ConfigRegistry.DISABLE_PLAYER_WARP_DISRUPTING.get()) {
                livingEntity.setData(DataAttachmentRegistry.PREVENT_WARP, true);
                livingEntity.setData(DataAttachmentRegistry.PREVENT_WARP_COOLDOWN, ConfigRegistry.WARP_DISRUPTING_COOLDOWN.get());
                player.displayClientMessage(Component.translatable(this.getDescriptionId() + ".message.prevent_player_warp",
                        player.getDisplayName(), ConfigRegistry.WARP_DISRUPTING_COOLDOWN.get()).withStyle(ChatFormatting.RED), true);
                this.spawnEntityParticles(ParticleTypes.CRIMSON_SPORE, player, livingEntity.level(), 16);

                if (!player.isCreative())
                    stack.hurtAndBreak(1, player, Player.getSlotForHand(player.getUsedItemHand()));
                return InteractionResult.SUCCESS;
            } else if (!(livingEntity instanceof Player)) {
                livingEntity.setData(DataAttachmentRegistry.PREVENT_WARP, true);
                player.displayClientMessage(Component.translatable(this.getDescriptionId() + ".message.prevent_entity_warp",
                        livingEntity.getDisplayName()).withStyle(ChatFormatting.RED), true);
                this.spawnEntityParticles(ParticleTypes.CRIMSON_SPORE, livingEntity, livingEntity.level(), 16);

                if (!player.isCreative())
                    stack.hurtAndBreak(1, player, Player.getSlotForHand(player.getUsedItemHand()));
                return InteractionResult.SUCCESS;
            }
        } return InteractionResult.PASS;
    }

    @NotNull
    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        AttributeInstance reachAttribute = player.getAttribute(Attributes.BLOCK_INTERACTION_RANGE);
        double reachDistance = player.isCreative() ? 5.0D : 4.5D;
        if (reachAttribute != null)
            reachDistance = reachAttribute.getValue();
        HitResult hitResult = player.pick(reachDistance, 0.0F, false);

        if (hitResult.getType() == HitResult.Type.MISS) {
            if (!ConfigRegistry.DISABLE_PLAYER_WARP_DISRUPTING.get()) {
                if (!player.getData(DataAttachmentRegistry.PREVENT_WARP)) {
                    player.setData(DataAttachmentRegistry.PREVENT_WARP, true);
                    player.setData(DataAttachmentRegistry.PREVENT_WARP_COOLDOWN, ConfigRegistry.WARP_DISRUPTING_COOLDOWN.get());
                    player.displayClientMessage(Component.translatable(this.getDescriptionId() + ".message.prevent_player_warp",
                            player.getDisplayName(), ConfigRegistry.WARP_DISRUPTING_COOLDOWN.get()).withStyle(ChatFormatting.RED), true);
                    this.spawnEntityParticles(ParticleTypes.CRIMSON_SPORE, player, world, 16);

                    if (!player.isCreative())
                        stack.hurtAndBreak(1, player, Player.getSlotForHand(player.getUsedItemHand()));
                    return InteractionResultHolder.success(stack);
                }
            }
        } else return InteractionResultHolder.pass(stack);
        return super.use(world, player, hand);
    }

    public void spawnEntityParticles(ParticleOptions particleType, Entity entity, Level world, int avgAmount) {
        float scaleFactor = entity.getBbWidth();
        int numParticles = (int) (scaleFactor * avgAmount);
        double radius = entity.getBbWidth() / 2;

        for (int i = 0; i < numParticles; i++) {
            // Calculate angle for each particle
            double angle = 2 * Math.PI * i / numParticles;
            // Calculate the X and Z offset using sine and cosine to spread in an ellipse
            double offsetX = Math.cos(angle) * radius;
            double offsetY = entity.getBbHeight();
            double offsetZ = Math.sin(angle) * radius;

            double x = entity.getX() + offsetX;
            double y = entity.getY();
            double z = entity.getZ() + offsetZ;

            world.addParticle(particleType, x, y + 0.2, z, 0, 1.0, 0);
            world.addParticle(particleType, x, y + offsetY / 2, z, 0, 1.0, 0);
            world.addParticle(particleType, x, y + offsetY - 0.2, z, 0, 1.0, 0);
        }
    }
}
