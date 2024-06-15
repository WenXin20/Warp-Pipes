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

public record SPipeBubblesStatePacket(BlockPos pos, Boolean hasPipeBubbles) implements CustomPacketPayload {
    public static final ResourceLocation BUBBLES_STATE_PAYLOAD = new ResourceLocation(WarpPipes.MODID, "bubbles_state_payload");

    @Override
    public ResourceLocation id() {
        return BUBBLES_STATE_PAYLOAD;
    }

    // Read and write in the same order!
    public SPipeBubblesStatePacket(FriendlyByteBuf buffer) {
        this(buffer.readBlockPos(), buffer.readBoolean());
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        if (this.pos != null) {
            buffer.writeBlockPos(this.pos);
            buffer.writeBoolean(hasPipeBubbles);
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

        pipeBlockEntity.togglePipeBubbles(player);
    }

    public static SPipeBubblesStatePacket pipeBubblesOn(BlockPos pos, Boolean hasPipeBubbles) {
        SPipeBubblesStatePacket packet = new SPipeBubblesStatePacket(pos, hasPipeBubbles);
        hasPipeBubbles = false;
        return packet;
    }

    public static SPipeBubblesStatePacket pipeBubblesOff(BlockPos pos, Boolean hasPipeBubbles) {
        SPipeBubblesStatePacket packet = new SPipeBubblesStatePacket(pos, hasPipeBubbles);
        hasPipeBubbles = true;
        return packet;
    }
}
