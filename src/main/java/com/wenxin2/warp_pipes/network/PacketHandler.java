package com.wenxin2.warp_pipes.network;

import com.wenxin2.warp_pipes.WarpPipes;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlerEvent;
import net.neoforged.neoforge.network.registration.IPayloadRegistrar;
import org.lwjgl.system.windows.MSG;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class PacketHandler {

    @SubscribeEvent
    public static void registerPackets(final RegisterPayloadHandlerEvent event) {
        final IPayloadRegistrar registrar = event.registrar("warp_pipes").versioned("1.0.0");

        // Sends to server
        registrar.play(SCloseStatePacket.CLOSE_STATE_PAYLOAD, SCloseStatePacket::new, SCloseStatePacket::handle);
        registrar.play(SPipeBubblesSliderPacket.BUBBLES_DISTANCE_PAYLOAD, SPipeBubblesSliderPacket::new, SPipeBubblesSliderPacket::handle);
        registrar.play(SPipeBubblesStatePacket.BUBBLES_STATE_PAYLOAD, SPipeBubblesStatePacket::new, SPipeBubblesStatePacket::handle);
        registrar.play(SRenamePipePacket.RENAME_PIPE_PAYLOAD, SRenamePipePacket::new, SRenamePipePacket::handle);
        registrar.play(SWaterSpoutSliderPacket.SPOUT_HEIGHT_PAYLOAD, SWaterSpoutSliderPacket::new, SWaterSpoutSliderPacket::handle);
        registrar.play(SWaterSpoutStatePacket.SPOUT_STATE_PAYLOAD, SWaterSpoutStatePacket::new, SWaterSpoutStatePacket::handle);
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
