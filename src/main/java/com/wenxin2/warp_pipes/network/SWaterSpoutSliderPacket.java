package com.wenxin2.warp_pipes.network;

import com.wenxin2.warp_pipes.WarpPipes;
import com.wenxin2.warp_pipes.blocks.WarpPipeBlock;
import com.wenxin2.warp_pipes.blocks.entities.WarpPipeBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SWaterSpoutSliderPacket(BlockPos pos, int waterSpoutHeight) implements CustomPacketPayload {
    public static final ResourceLocation SPOUT_HEIGHT_PAYLOAD = new ResourceLocation(WarpPipes.MODID, "spout_height_payload");

    @Override
    public ResourceLocation id() {
        return SPOUT_HEIGHT_PAYLOAD;
    }

    // Read and write in the same order!
    public SWaterSpoutSliderPacket(FriendlyByteBuf buffer) {
        this(buffer.readBlockPos(), buffer.readInt());
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        if (this.pos != null) {
            buffer.writeBlockPos(this.pos);
            buffer.writeInt(waterSpoutHeight);
        }
    }

    public void handle(IPayloadContext context) {
        if (context.flow().isServerbound()) {
            context.workHandler().execute(() -> {
                if (context.player().isEmpty() || context.level().isEmpty())
                    return;
                ServerPlayer player = (ServerPlayer) context.player().get();
                Level world = context.level().get();
                BlockEntity blockEntity = world.getBlockEntity(pos);
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
        pipeBlockEntity.waterSpoutHeight(player, waterSpoutHeight);
    }
}
