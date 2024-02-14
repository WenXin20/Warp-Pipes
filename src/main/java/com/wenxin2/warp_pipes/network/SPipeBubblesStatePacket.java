package com.wenxin2.warp_pipes.network;

import com.wenxin2.warp_pipes.blocks.WarpPipeBlock;
import com.wenxin2.warp_pipes.blocks.entities.WarpPipeBlockEntity;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.NetworkEvent;

public class SPipeBubblesStatePacket {
    public final BlockPos pos;
    public static boolean hasPipeBubbles;

    public SPipeBubblesStatePacket(BlockPos pos, Boolean hasPipeBubbles) {
        this.pos = pos;
        SPipeBubblesStatePacket.hasPipeBubbles = hasPipeBubbles;
    }

    // Read and write in the same order!
    public SPipeBubblesStatePacket(FriendlyByteBuf buffer) {
        this(buffer.readBlockPos(), buffer.readBoolean());
    }

    public void encode(FriendlyByteBuf buffer) {
        if (this.pos != null) {
            buffer.writeBlockPos(this.pos);
            buffer.writeBoolean(hasPipeBubbles);
        }
    }

    public void handle(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            ServerPlayer player = context.get().getSender();
            if (player == null)
                return;
            Level world = player.level();
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof WarpPipeBlockEntity) {
                changeState(player, (WarpPipeBlockEntity) blockEntity);
                ((WarpPipeBlockEntity) blockEntity).sendData();
                blockEntity.setChanged();
            }
        });
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
        SPipeBubblesStatePacket.hasPipeBubbles = false;
        return packet;
    }

    public static SPipeBubblesStatePacket pipeBubblesOff(BlockPos pos, Boolean hasPipeBubbles) {
        SPipeBubblesStatePacket packet = new SPipeBubblesStatePacket(pos, hasPipeBubbles);
        SPipeBubblesStatePacket.hasPipeBubbles = true;
        return packet;
    }
}
