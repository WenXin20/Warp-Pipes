package com.wenxin2.warp_pipes.registries;

import com.mojang.serialization.Codec;
import com.wenxin2.warp_pipes.WarpPipes;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.attachment.AttachmentType;

public class DataAttachmentRegistry {
    public static final Supplier<AttachmentType<Boolean>> PLAYED_ENTER_PIPE_SOUND = WarpPipes.ATTACHMENT_TYPES.register(
            "played_enter_pipe_sound", () -> AttachmentType.builder(() -> false).serialize(Codec.BOOL)
                    .sync(StreamCodec.of(FriendlyByteBuf::writeBoolean, FriendlyByteBuf::readBoolean)).build());
    public static final Supplier<AttachmentType<Boolean>> PLAYED_EXIT_PIPE_SOUND = WarpPipes.ATTACHMENT_TYPES.register(
            "played_exit_pipe_sound", () -> AttachmentType.builder(() -> true).serialize(Codec.BOOL)
                    .sync(StreamCodec.of(FriendlyByteBuf::writeBoolean, FriendlyByteBuf::readBoolean)).build());
    public static final Supplier<AttachmentType<Boolean>> PLAYED_INSIDE_PIPE_SOUND = WarpPipes.ATTACHMENT_TYPES.register(
            "played_inside_pipe_sound", () -> AttachmentType.builder(() -> true).serialize(Codec.BOOL)
                    .sync(StreamCodec.of(FriendlyByteBuf::writeBoolean, FriendlyByteBuf::readBoolean)).build());

    public static void init() {}
}
