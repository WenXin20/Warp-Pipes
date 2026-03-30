package com.wenxin2.warp_pipes.registries;

import com.mojang.serialization.Codec;
import com.wenxin2.warp_pipes.WarpPipes;
import java.util.UUID;
import java.util.function.Supplier;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.attachment.AttachmentType;

public class DataAttachmentRegistry {
    public static final Supplier<AttachmentType<Boolean>> PLAYED_ENTER_PIPE_SOUND = WarpPipes.ATTACHMENT_TYPES
            .register("played_enter_pipe_sound", () -> AttachmentType.builder(() -> false).serialize(Codec.BOOL)
                    .sync(StreamCodec.of(FriendlyByteBuf::writeBoolean, FriendlyByteBuf::readBoolean)).build());
    public static final Supplier<AttachmentType<Boolean>> PLAYED_EXIT_PIPE_SOUND = WarpPipes.ATTACHMENT_TYPES
            .register("played_exit_pipe_sound", () -> AttachmentType.builder(() -> true).serialize(Codec.BOOL)
                    .sync(StreamCodec.of(FriendlyByteBuf::writeBoolean, FriendlyByteBuf::readBoolean)).build());
    public static final Supplier<AttachmentType<Boolean>> PLAYED_INSIDE_PIPE_SOUND = WarpPipes.ATTACHMENT_TYPES
            .register("played_inside_pipe_sound", () -> AttachmentType.builder(() -> true).serialize(Codec.BOOL)
                    .sync(StreamCodec.of(FriendlyByteBuf::writeBoolean, FriendlyByteBuf::readBoolean)).build());
    public static final Supplier<AttachmentType<Boolean>> PREVENT_WARP = WarpPipes.ATTACHMENT_TYPES
            .register("prevent_warp", () -> AttachmentType.builder(() -> false).serialize(Codec.BOOL)
                    .sync(StreamCodec.of(FriendlyByteBuf::writeBoolean, FriendlyByteBuf::readBoolean)).build());

    public static final Supplier<AttachmentType<Integer>> PREVENT_WARP_COOLDOWN = WarpPipes.ATTACHMENT_TYPES
            .register("prevent_warp_cooldown", () -> AttachmentType.builder(() -> 0).serialize(Codec.INT)
                    .sync(StreamCodec.of(FriendlyByteBuf::writeInt, FriendlyByteBuf::readInt)).build());
    public static final Supplier<AttachmentType<Integer>> WARP_COOLDOWN = WarpPipes.ATTACHMENT_TYPES
            .register("warp_cooldown", () -> AttachmentType.builder(() -> 0).serialize(Codec.INT)
                    .sync(StreamCodec.of(FriendlyByteBuf::writeInt, FriendlyByteBuf::readInt)).build());
    public static final Supplier<AttachmentType<Integer>> RIDE_VEHICLE_COUNTDOWN = WarpPipes.ATTACHMENT_TYPES
            .register("ride_vehicle_countdown", () -> AttachmentType.builder(() -> 0).serialize(Codec.INT)
                    .sync(StreamCodec.of(FriendlyByteBuf::writeInt, FriendlyByteBuf::readInt)).build());

    public static final Supplier<AttachmentType<UUID>> VEHICLE_UUID = WarpPipes.ATTACHMENT_TYPES
            .register("vehicle_uuid", () -> AttachmentType.<UUID>builder(() -> null).serialize(UUIDUtil.CODEC)
                    .sync(UUIDUtil.STREAM_CODEC).build());

    public static void init() {}
}
