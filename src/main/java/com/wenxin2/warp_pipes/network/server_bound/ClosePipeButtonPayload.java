package com.wenxin2.warp_pipes.network.server_bound;

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

public record ClosePipeButtonPayload(BlockPos pos, Boolean closePipe) implements CustomPacketPayload {
    public static final ResourceLocation CLOSE_STATE_PAYLOAD = new ResourceLocation(WarpPipes.MODID, "close_state_payload");

    @Override
    public ResourceLocation id() {
        return CLOSE_STATE_PAYLOAD;
    }

    // Read and write in the same order!
    public ClosePipeButtonPayload(FriendlyByteBuf buffer) {
        this(buffer.readBlockPos(), buffer.readBoolean());
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        if (this.pos != null) {
            buffer.writeBlockPos(this.pos);
            buffer.writeBoolean(closePipe);
        }
    }

    public void handle(IPayloadContext context) {
        if (context.flow().isServerbound()) {
            context.workHandler().execute(() -> {
                if (context.player().isEmpty() && this.pos == null || context.level().isEmpty())
                    return;
                ServerPlayer player = (ServerPlayer) context.player().get();
                Level world = player.level();
                BlockEntity blockEntity = world.getBlockEntity(this.pos.immutable());
                if (blockEntity instanceof WarpPipeBlockEntity) {
                    changeState(player, (WarpPipeBlockEntity) blockEntity);
                    ((WarpPipeBlockEntity) blockEntity).sendData();
                    blockEntity.setChanged();
                }
            });
        }
    }

    public void changeState(ServerPlayer player, WarpPipeBlockEntity pipeBlockEntity) {
        Level world = pipeBlockEntity.getLevel();
        if (world == null)
            return;
        BlockPos pos = pipeBlockEntity.getBlockPos();
        BlockState state = world.getBlockState(pos);

        if (!(state.getBlock() instanceof WarpPipeBlock))
            return;

        pipeBlockEntity.closePipe(player);
    }

    public static ClosePipeButtonPayload openPipe(BlockPos pos, Boolean closePipe) {
        ClosePipeButtonPayload packet = new ClosePipeButtonPayload(pos, closePipe);
        closePipe = false;
        return packet;
    }

    public static ClosePipeButtonPayload closePipe(BlockPos pos, Boolean closePipe) {
        ClosePipeButtonPayload packet = new ClosePipeButtonPayload(pos, closePipe);
        closePipe = true;
        return packet;
    }
}
