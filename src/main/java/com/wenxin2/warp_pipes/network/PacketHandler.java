package com.wenxin2.warp_pipes.network;

import com.wenxin2.warp_pipes.network.server_bound.ClosePipeButtonPayload;
import com.wenxin2.warp_pipes.network.server_bound.PipeBubblesButtonPayload;
import com.wenxin2.warp_pipes.network.server_bound.PipeBubblesSliderPayload;
import com.wenxin2.warp_pipes.network.server_bound.RenamePipePayload;
import com.wenxin2.warp_pipes.network.server_bound.WaterSpoutButtonPayload;
import com.wenxin2.warp_pipes.network.server_bound.WaterSpoutSliderPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlerEvent;
import net.neoforged.neoforge.network.registration.IPayloadRegistrar;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public class PacketHandler {

    @SubscribeEvent
    public static void registerPackets(final RegisterPayloadHandlerEvent event) {
        final IPayloadRegistrar registrar = event.registrar("warp_pipes").versioned("1.0.0");

        // Sends to server
        registrar.play(ClosePipeButtonPayload.CLOSE_STATE_PAYLOAD, ClosePipeButtonPayload::new, ClosePipeButtonPayload::handle);
        registrar.play(PipeBubblesSliderPayload.BUBBLES_DISTANCE_PAYLOAD, PipeBubblesSliderPayload::new, PipeBubblesSliderPayload::handle);
        registrar.play(PipeBubblesButtonPayload.BUBBLES_STATE_PAYLOAD, PipeBubblesButtonPayload::new, PipeBubblesButtonPayload::handle);
        registrar.play(RenamePipePayload.RENAME_PIPE_PAYLOAD, RenamePipePayload::new, RenamePipePayload::handle);
        registrar.play(WaterSpoutSliderPayload.SPOUT_HEIGHT_PAYLOAD, WaterSpoutSliderPayload::new, WaterSpoutSliderPayload::handle);
        registrar.play(WaterSpoutButtonPayload.SPOUT_STATE_PAYLOAD, WaterSpoutButtonPayload::new, WaterSpoutButtonPayload::handle);
    }

    public static <MSG extends CustomPacketPayload> void sendToServer(MSG message) {
        PacketDistributor.SERVER.noArg().send(message);
    }

    public static <MSG extends CustomPacketPayload> void sentToAllClients(MSG message) {
        PacketDistributor.ALL.noArg().send(message);
    }

    public static <MSG extends CustomPacketPayload> void sendToPlayer(MSG message, ServerPlayer player) {
        PacketDistributor.PLAYER.with(player).send(message);
    }
}
