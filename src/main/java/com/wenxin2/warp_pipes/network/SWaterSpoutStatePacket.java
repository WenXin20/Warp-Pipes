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

public class SWaterSpoutStatePacket {
    public final BlockPos pos;
    public static boolean hasWaterSpout;

    public SWaterSpoutStatePacket(BlockPos pos, Boolean hasWaterSpout) {
        this.pos = pos;
        SWaterSpoutStatePacket.hasWaterSpout = hasWaterSpout;
    }

    // Read and write in the same order!
    public SWaterSpoutStatePacket(FriendlyByteBuf buffer) {
        this(buffer.readBlockPos(), buffer.readBoolean());
    }

    public void encode(FriendlyByteBuf buffer) {
        if (this.pos != null) {
            buffer.writeBlockPos(this.pos);
            buffer.writeBoolean(hasWaterSpout);
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

        pipeBlockEntity.toggleWaterSpout(player);
    }

    public static SWaterSpoutStatePacket waterSpoutOn(BlockPos pos, Boolean hasWaterSpout) {
        SWaterSpoutStatePacket packet = new SWaterSpoutStatePacket(pos, hasWaterSpout);
        SWaterSpoutStatePacket.hasWaterSpout = false;
        return packet;
    }

    public static SWaterSpoutStatePacket waterSpoutOff(BlockPos pos, Boolean hasWaterSpout) {
        SWaterSpoutStatePacket packet = new SWaterSpoutStatePacket(pos, hasWaterSpout);
        SWaterSpoutStatePacket.hasWaterSpout = true;
        return packet;
    }
}
