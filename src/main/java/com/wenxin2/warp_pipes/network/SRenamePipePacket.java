package com.wenxin2.warp_pipes.network;

import com.wenxin2.warp_pipes.blocks.WarpPipeBlock;
import com.wenxin2.warp_pipes.blocks.entities.WarpPipeBlockEntity;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.NetworkEvent;

public class SRenamePipePacket {
    public final BlockPos pos;
    public static String customName;

    public SRenamePipePacket(BlockPos pos, String customName) {
        this.pos = pos;
        SRenamePipePacket.customName = customName;
    }

    // Read and write in the same order!
    public SRenamePipePacket(FriendlyByteBuf buffer) {
        this(buffer.readBlockPos(), buffer.readUtf());
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(this.pos);
        buffer.writeUtf(customName);
    }

    public void handle(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            ServerPlayer player = context.get().getSender();
            if (player == null)
                return;
            Level world = player.level();
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof WarpPipeBlockEntity) {
//                changeState(player, (WarpPipeBlockEntity) blockEntity);
                ((WarpPipeBlockEntity) blockEntity).sendData();
                ((WarpPipeBlockEntity) blockEntity).setCustomName(Component.literal(customName));
                blockEntity.setChanged();
            }
        });
    }

//    public void changeState(ServerPlayer player, WarpPipeBlockEntity pipeBlockEntity) {
//        Level world = pipeBlockEntity.getLevel();
//        if (world == null)
//            return;
//        BlockPos pos = pipeBlockEntity.getBlockPos();
//        BlockState state = world.getBlockState(pos);
//
//        if (!(state.getBlock() instanceof WarpPipeBlock))
//            return;
//
//        pipeBlockEntity.closePipe(player);
//    }
}
