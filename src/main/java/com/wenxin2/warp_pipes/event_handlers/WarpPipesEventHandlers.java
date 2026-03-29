package com.wenxin2.warp_pipes.event_handlers;

import com.wenxin2.warp_pipes.WarpPipes;
import com.wenxin2.warp_pipes.blocks.WarpPipeBlock;
import com.wenxin2.warp_pipes.blocks.client.WarpPipeScreen;
import com.wenxin2.warp_pipes.blocks.entities.BaseWarpBlockEntity;
import com.wenxin2.warp_pipes.blocks.entities.WarpPipeBlockEntity;
import com.wenxin2.warp_pipes.registries.ConfigRegistry;
import com.wenxin2.warp_pipes.registries.DataAttachmentRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.ParticleUtils;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.HoneycombItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

@EventBusSubscriber(modid = WarpPipes.MOD_ID)
public class WarpPipesEventHandlers {
    @SubscribeEvent
    public static void onJoinWorld(EntityJoinLevelEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof LivingEntity)) return;
        CompoundTag tag = entity.getPersistentData();

        if (tag.contains("warp_pipes:prevent_warp")) {
            entity.setData(DataAttachmentRegistry.PREVENT_WARP, tag.getBoolean("warp_pipes:prevent_warp"));
            tag.remove("warp_pipes:prevent_warp");
        }

        if (tag.contains("warp_pipes:warp_cooldown")) {
            entity.setData(DataAttachmentRegistry.WARP_COOLDOWN, tag.getInt("warp_pipes:warp_cooldown"));
            tag.remove("warp_pipes:warp_cooldown");
        }
    }

    @SubscribeEvent
    public static void preEntityTick(EntityTickEvent.Pre event) {
        Entity entity = event.getEntity();

        if (entity.hasData(DataAttachmentRegistry.WARP_COOLDOWN) &&
                entity.getData(DataAttachmentRegistry.WARP_COOLDOWN) > 0)
            entity.setData(DataAttachmentRegistry.WARP_COOLDOWN, entity.getData(DataAttachmentRegistry.WARP_COOLDOWN) - 1);

        if (entity.hasData(DataAttachmentRegistry.PREVENT_WARP_COOLDOWN)) {
            int preventWarpCooldown = entity.getData(DataAttachmentRegistry.PREVENT_WARP_COOLDOWN);

            if (preventWarpCooldown > 0)
                entity.setData(DataAttachmentRegistry.PREVENT_WARP_COOLDOWN, preventWarpCooldown - 1);

            if (preventWarpCooldown == 0
                    && entity.getData(DataAttachmentRegistry.PREVENT_WARP))
                entity.setData(DataAttachmentRegistry.PREVENT_WARP, false);
        }
    }

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        Level world = event.getLevel();
        BlockPos pos = event.getPos();
        BlockState state = world.getBlockState(pos);
        ItemStack heldItem = event.getItemStack();
        Player player = event.getEntity();

        if (world.isClientSide()) {
            BlockPos clickedPos = event.getPos();
            BlockEntity blockEntity = event.getLevel().getBlockEntity(clickedPos);
            if (blockEntity instanceof WarpPipeBlockEntity) {
                // Update the last clicked position
                WarpPipeScreen.lastClickedPos = clickedPos;
            }
        }

        if (heldItem.getItem() instanceof SpawnEggItem && state.getBlock() instanceof WarpPipeBlock
                && world.getBlockEntity(pos) instanceof BaseWarpBlockEntity warpBE) {
            if (warpBE.isWaxed() || !ConfigRegistry.WARP_PIPE_SPAWNS_MOBS.get() || !player.isCreative()) {
                event.setCancellationResult(InteractionResult.FAIL);
                event.setCanceled(true);
            }
        }

        if (heldItem.getItem() instanceof HoneycombItem && world.getBlockEntity(pos) instanceof BaseWarpBlockEntity warpBE
                && (ConfigRegistry.WAX_DISABLES_BUBBLES.get() || ConfigRegistry.WAX_DISABLES_CLOSING.get()
                || ConfigRegistry.WAX_DISABLES_RENAMING.get() || ConfigRegistry.WAX_DISABLES_WATER_SPOUTS.get()
                || ConfigRegistry.WAX_DISABLES_WARP_LINKING.get())) {
            if (!warpBE.isWaxed()) {
                warpBE.setWaxed(true);
                warpBE.markUpdated();
                heldItem.consume(1, player);

                ParticleUtils.spawnParticlesOnBlockFaces(world, pos, ParticleTypes.WAX_ON, UniformInt.of(3, 5));
                world.playSound(player, pos, SoundEvents.HONEYCOMB_WAX_ON, SoundSource.BLOCKS, 1.0F, 1.0F);
                world.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
                world.sendBlockUpdated(pos, state, state, Block.UPDATE_ALL);

                event.setCancellationResult(InteractionResult.SUCCESS);
                event.setCanceled(true);
            }
        }

        if (heldItem.getItem() instanceof AxeItem && world.getBlockEntity(pos) instanceof BaseWarpBlockEntity warpBE
                && ConfigRegistry.ALLOW_PIPE_UNWAXING.get()) {
            if (warpBE.isWaxed()) {
                warpBE.setWaxed(false);
                warpBE.markUpdated();
                heldItem.hurtAndBreak(1, player, Player.getSlotForHand(player.getUsedItemHand()));

                ParticleUtils.spawnParticlesOnBlockFaces(world, pos, ParticleTypes.WAX_OFF, UniformInt.of(3, 5));
                world.playSound(null, pos, SoundEvents.AXE_WAX_OFF, SoundSource.BLOCKS, 1.0F, 1.0F);
                world.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
                world.sendBlockUpdated(pos, state, state, Block.UPDATE_ALL);

                event.setCancellationResult(InteractionResult.SUCCESS);
                event.setCanceled(true);
            }
        }
    }
}