package com.wenxin2.warp_pipes.network;

import com.wenxin2.warp_pipes.blocks.WarpPipeBlock;
import com.wenxin2.warp_pipes.blocks.client.WarpPipeScreen;
import com.wenxin2.warp_pipes.blocks.entities.WarpPipeBlockEntity;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.NetworkEvent;

public class SWaterSpoutSliderPacket {
    public final BlockPos pos;
    public static int waterSpoutHeight;

    public SWaterSpoutSliderPacket(BlockPos pos, int waterSpoutHeight) {
        this.pos = pos;
        SWaterSpoutSliderPacket.waterSpoutHeight = waterSpoutHeight;
    }

    // Read and write in the same order!
    public SWaterSpoutSliderPacket(FriendlyByteBuf buffer) {
        this(buffer.readBlockPos(), buffer.readInt());
    }

    public void encode(FriendlyByteBuf buffer) {
        if (this.pos != null) {
            buffer.writeBlockPos(this.pos);
            buffer.writeInt(waterSpoutHeight);
        }
    }

    public void handle(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            ServerPlayer player = context.get().getSender();
            if (player == null)
                return;
            Level world = player.level();
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof WarpPipeBlockEntity pipeBlockEntity) {
                changeHeight(player, (WarpPipeBlockEntity) blockEntity);
                pipeBlockEntity.sendData();
            }
        });
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
