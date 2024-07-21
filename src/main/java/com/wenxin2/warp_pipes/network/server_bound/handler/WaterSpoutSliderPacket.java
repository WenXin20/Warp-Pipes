package com.wenxin2.warp_pipes.network.server_bound.handler;

import com.wenxin2.warp_pipes.WarpPipes;
import com.wenxin2.warp_pipes.blocks.WarpPipeBlock;
import com.wenxin2.warp_pipes.blocks.entities.WarpPipeBlockEntity;
import com.wenxin2.warp_pipes.network.server_bound.data.WaterSpoutSliderPayload;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class WaterSpoutSliderPacket {
    public static final WaterSpoutSliderPacket INSTANCE = new WaterSpoutSliderPacket();

    public static WaterSpoutSliderPacket get() {
        return INSTANCE;
    }

    public void handle(final WaterSpoutSliderPayload payload, IPayloadContext context) {
        if (context.flow().isServerbound()) {
            context.enqueueWork(() -> {
                if (payload.pos() == null)
                    return;
                ServerPlayer player = (ServerPlayer) context.player();
                Level world = player.level();
                BlockEntity blockEntity = world.getBlockEntity(payload.pos());
                if (blockEntity instanceof WarpPipeBlockEntity pipeBlockEntity) {
                    changeHeight(player, (WarpPipeBlockEntity) blockEntity);
                    pipeBlockEntity.sendData();
                }
            });
        }
    }

    public void changeHeight(ServerPlayer player, WarpPipeBlockEntity pipeBlockEntity) {
        Level world = pipeBlockEntity.getLevel();
        if (world == null)
            return;
        BlockPos pos = pipeBlockEntity.getBlockPos();
        BlockState state = world.getBlockState(pos);

        if (!(state.getBlock() instanceof WarpPipeBlock))
            return;
        pipeBlockEntity.waterSpoutHeight(player, pipeBlockEntity.spoutHeight); // Check this
    }
}
