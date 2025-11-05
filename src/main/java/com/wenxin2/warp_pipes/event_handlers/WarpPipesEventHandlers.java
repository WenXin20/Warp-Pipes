package com.wenxin2.warp_pipes.event_handlers;

import com.wenxin2.warp_pipes.WarpPipes;
import com.wenxin2.warp_pipes.blocks.client.WarpPipeScreen;
import com.wenxin2.warp_pipes.blocks.entities.BaseWarpBlockEntity;
import com.wenxin2.warp_pipes.blocks.entities.WarpPipeBlockEntity;
import com.wenxin2.warp_pipes.registries.ConfigRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.ParticleUtils;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.HoneycombItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

@EventBusSubscriber(modid = WarpPipes.MOD_ID)
public class WarpPipesEventHandlers {
    @SubscribeEvent
    public static void onPlayerRightClick(PlayerInteractEvent.RightClickBlock event) {
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