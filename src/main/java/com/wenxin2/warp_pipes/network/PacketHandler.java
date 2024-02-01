package com.wenxin2.warp_pipes.network;

import com.wenxin2.warp_pipes.WarpPipes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class PacketHandler {
    public static final SimpleChannel INSTANCE = NetworkRegistry.ChannelBuilder.named(
            new ResourceLocation(WarpPipes.MODID, "main"))
            .serverAcceptedVersions((status) -> true)
            .clientAcceptedVersions((status) -> true)
            .networkProtocolVersion(() -> "1")
            .simpleChannel();

    private static int ID = 0;
    public static void register() {
        INSTANCE.messageBuilder(SCloseStatePacket.class, ID++, NetworkDirection.PLAY_TO_SERVER)
                .encoder(SCloseStatePacket::encode)
                .decoder(SCloseStatePacket::new)
                .consumerMainThread(SCloseStatePacket::handle)
                .add();
        INSTANCE.messageBuilder(SWaterSpoutStatePacket.class, ID++, NetworkDirection.PLAY_TO_SERVER)
                .encoder(SWaterSpoutStatePacket::encode)
                .decoder(SWaterSpoutStatePacket::new)
                .consumerMainThread(SWaterSpoutStatePacket::handle)
                .add();
    }

    public static void sendToServer(Object msg) {
        INSTANCE.send(PacketDistributor.SERVER.noArg(), msg);
    }

    public static void sentToAllClients(Object msg) {
        INSTANCE.send(PacketDistributor.ALL.noArg(), msg);
    }

    public static void sendToPlayer(Object msg, ServerPlayer player) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), msg);
    }
}
