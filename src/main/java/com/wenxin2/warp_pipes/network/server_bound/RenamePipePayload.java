package com.wenxin2.warp_pipes.network.server_bound;

import com.wenxin2.warp_pipes.WarpPipes;
import com.wenxin2.warp_pipes.blocks.entities.WarpPipeBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record RenamePipePayload(BlockPos pos, String customName) implements CustomPacketPayload {
    public static final ResourceLocation RENAME_PIPE_PAYLOAD = new ResourceLocation(WarpPipes.MODID, "rename_pipe_payload");

    @Override
    public ResourceLocation id() {
        return RENAME_PIPE_PAYLOAD;
    }

    // Read and write in the same order!
    public RenamePipePayload(FriendlyByteBuf buffer) {
        this(buffer.readBlockPos(), buffer.readUtf());
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        if (this.pos != null) {
            buffer.writeBlockPos(this.pos);
            buffer.writeUtf(customName);
        }
    }

    public void handle(IPayloadContext context) {
        if (context.flow().isServerbound()) {
            context.workHandler().execute(() -> {
                if (context.level().isEmpty())
                    return;
                Level world = context.level().get();
                BlockEntity blockEntity = world.getBlockEntity(pos);
                if (blockEntity instanceof WarpPipeBlockEntity) {
                    ((WarpPipeBlockEntity) blockEntity).sendData();
                    ((WarpPipeBlockEntity) blockEntity).setCustomName(Component.literal(customName));
                    ((WarpPipeBlockEntity) blockEntity).updateText(pipeText -> pipeText.setMessage(0, Component.literal(customName)));
                    ((WarpPipeBlockEntity) blockEntity).markUpdated();
                    ((WarpPipeBlockEntity) blockEntity).getUpdateTag();
                }
            });
        }
    }
}
