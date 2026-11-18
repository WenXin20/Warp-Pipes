package com.wenxin2.warp_pipes.event_handlers;

import com.wenxin2.warp_pipes.blocks.ClearWarpPipeBlock;
import com.wenxin2.warp_pipes.blocks.WarpPipeBlock;
import com.wenxin2.warp_pipes.blocks.entities.BaseWarpBlockEntity;
import com.wenxin2.warp_pipes.client.PipeTravelRotations;
import com.wenxin2.warp_pipes.registries.ConfigRegistry;
import com.wenxin2.warp_pipes.registries.DataAttachmentRegistry;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import com.wenxin2.warp_pipes.registries.SoundRegistry;
import com.wenxin2.warp_pipes.sounds.FadeInAndOutSoundInstance;
import com.wenxin2.warp_pipes.WarpPipes;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityLeaveLevelEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

@EventBusSubscriber(modid = WarpPipes.MOD_ID, value = Dist.CLIENT)
public class ClientEventHandlers {
    public static final Map<UUID, FadeInAndOutSoundInstance> ACTIVE_PIPE_SOUNDS = new HashMap<>();
    private static final List<CooldownSmoke> COOLDOWN_PIPE_ENTRANCES = new ArrayList<>();
    private static BlockPos cooldownScanCenter;
    private static long cooldownScanTime;
    private static final ResourceLocation OVERLAY = ResourceLocation.fromNamespaceAndPath(WarpPipes.MOD_ID, "textures/misc/splunkin_pumpkin_blur.png");

    @SubscribeEvent
    public static void onEntityRemoved(EntityLeaveLevelEvent event) {
        Entity entity = event.getEntity();
        UUID uuid = entity.getUUID();
        if (!(event.getLevel() instanceof ClientLevel))
            return;

        if (ACTIVE_PIPE_SOUNDS.get(uuid) != null) {
            ACTIVE_PIPE_SOUNDS.get(uuid).startFadeOut();
            entity.setData(DataAttachmentRegistry.PLAYED_INSIDE_PIPE_SOUND, false);
        }
    }

    @SubscribeEvent
    public static void postEntityTick(EntityTickEvent.Post event) {
        Entity entity = event.getEntity();

        UUID uuid = entity.getUUID();
        Level world = entity.level();

        if (!(entity.level() instanceof ClientLevel))
            return;

        BlockState state = PipeTravelRotations.getClearPipeState(entity);
        boolean inClearPipe = state.getBlock() instanceof ClearWarpPipeBlock;
        boolean isEntrance = state.hasProperty(ClearWarpPipeBlock.ENTRANCE) && state.getValue(ClearWarpPipeBlock.ENTRANCE);

        if (entity instanceof LivingEntity livingEntity)
            PipeTravelRotations.tick(livingEntity, inClearPipe);

        if (entity instanceof Player player && player == Minecraft.getInstance().player
                && !ConfigRegistry.DISABLE_WARP_COOLDOWN_PARTICLES.get() && player.getData(DataAttachmentRegistry.WARP_COOLDOWN) > 0
                && world.getGameTime() % 4 == 0)
            ClientEventHandlers.spawnWarpCooldownParticles(world, player);

        if (entity.getData(DataAttachmentRegistry.PLAYED_EXIT_PIPE_SOUND)
                && !entity.getData(DataAttachmentRegistry.PLAYED_ENTER_PIPE_SOUND) && inClearPipe && isEntrance) {
            entity.playSound(SoundRegistry.CLEAR_PIPE_ENTER.get(), 1.0F, 1.0F);
            entity.setData(DataAttachmentRegistry.PLAYED_ENTER_PIPE_SOUND, true);
            entity.setData(DataAttachmentRegistry.PLAYED_EXIT_PIPE_SOUND, false);
        }

        if (entity.getData(DataAttachmentRegistry.PLAYED_ENTER_PIPE_SOUND)
                && !entity.getData(DataAttachmentRegistry.PLAYED_EXIT_PIPE_SOUND) && !inClearPipe) {
            entity.playSound(SoundRegistry.CLEAR_PIPE_EXIT.get(), 1.0F, 1.0F);
            entity.setData(DataAttachmentRegistry.PLAYED_EXIT_PIPE_SOUND, true);
            entity.setData(DataAttachmentRegistry.PLAYED_ENTER_PIPE_SOUND, false);
        }

        if (entity.getData(DataAttachmentRegistry.PLAYED_ENTER_PIPE_SOUND)
                && !entity.getData(DataAttachmentRegistry.PLAYED_EXIT_PIPE_SOUND)
                && !entity.getData(DataAttachmentRegistry.PLAYED_INSIDE_PIPE_SOUND) && inClearPipe) {
            FadeInAndOutSoundInstance insideSound = new FadeInAndOutSoundInstance(entity, SoundRegistry.CLEAR_PIPE_INSIDE.get(),
                    SoundSource.BLOCKS, 20, 10);

            ACTIVE_PIPE_SOUNDS.put(uuid, insideSound);
            Minecraft.getInstance().getSoundManager().play(insideSound);
            entity.setData(DataAttachmentRegistry.PLAYED_INSIDE_PIPE_SOUND, true);
        }

        if (entity.getData(DataAttachmentRegistry.PLAYED_INSIDE_PIPE_SOUND) && !inClearPipe) {
            FadeInAndOutSoundInstance active = ACTIVE_PIPE_SOUNDS.get(uuid);

            if (active != null)
                active.startFadeOut();
            ACTIVE_PIPE_SOUNDS.remove(uuid);
            entity.setData(DataAttachmentRegistry.PLAYED_INSIDE_PIPE_SOUND, false);
        }
    }

    private record CooldownSmoke(Vec3 pos, Vec3 speed) {}

    private static void spawnWarpCooldownParticles(Level level, Player player) {
        BlockPos playerPos = player.blockPosition();

        if (!playerPos.equals(cooldownScanCenter) || level.getGameTime() - cooldownScanTime >= 20) {
            cooldownScanCenter = playerPos;
            cooldownScanTime = level.getGameTime();
            COOLDOWN_PIPE_ENTRANCES.clear();

            for (BlockPos pos : BlockPos.betweenClosed(playerPos.offset(-3, -3, -3), playerPos.offset(3, 3, 3))) {
                BlockState state = level.getBlockState(pos);

                if (state.getBlock() instanceof WarpPipeBlock && state.getValue(WarpPipeBlock.ENTRANCE) && !state.getValue(WarpPipeBlock.CLOSED)
                        && level.getBlockEntity(pos) instanceof BaseWarpBlockEntity warpBE && warpBE.hasDestinationPos()) {
                    Direction facing = state.getValue(WarpPipeBlock.FACING);
                    COOLDOWN_PIPE_ENTRANCES.add(new CooldownSmoke(
                            Vec3.atCenterOf(pos).add(facing.getStepX() * 0.5, facing.getStepY() * 0.5, facing.getStepZ() * 0.5),
                            new Vec3(facing.getStepX() * 0.02, facing.getStepY() * 0.02 + 0.01, facing.getStepZ() * 0.02)));
                }
            }
        }

        for (CooldownSmoke smoke : COOLDOWN_PIPE_ENTRANCES)
            level.addParticle(ParticleTypes.SMOKE, smoke.pos().x, smoke.pos().y, smoke.pos().z,
                    smoke.speed().x, smoke.speed().y, smoke.speed().z);
    }
}
