package com.wenxin2.warp_pipes.registries;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wenxin2.warp_pipes.WarpPipes;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.registries.DeferredHolder;

public class DataComponentRegistry {
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> IS_BOUND =
            WarpPipes.COMPONENTS.register("is_bound",
                    () -> DataComponentType.<Boolean>builder().persistent(Codec.BOOL)
                            .networkSynchronized(ByteBufCodecs.BOOL).build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Component>> PIPE_NAME =
            WarpPipes.COMPONENTS.register("pipe_name",
                    () -> DataComponentType.<Component>builder().persistent(ComponentSerialization.FLAT_CODEC)
                            .networkSynchronized(ComponentSerialization.STREAM_CODEC).cacheEncoding().build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> POS_X =
            WarpPipes.COMPONENTS.register("pos_x",
                    () -> DataComponentType.<Integer>builder().persistent(Codec.INT)
                            .networkSynchronized(ByteBufCodecs.INT).build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> POS_Y =
            WarpPipes.COMPONENTS.register("pos_y",
                    () -> DataComponentType.<Integer>builder().persistent(Codec.INT)
                            .networkSynchronized(ByteBufCodecs.INT).build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> POS_Z =
            WarpPipes.COMPONENTS.register("pos_z",
                    () -> DataComponentType.<Integer>builder().persistent(Codec.INT)
                            .networkSynchronized(ByteBufCodecs.INT).build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<BlockPos>> WARP_POS =
            WarpPipes.COMPONENTS.register("warp_pos",
                    () -> DataComponentType.<BlockPos>builder().persistent(BlockPos.CODEC)
                            .networkSynchronized(BlockPos.STREAM_CODEC).build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<GlobalPos>> GLOBAL_WARP_POS =
            WarpPipes.COMPONENTS.register("global_warp_pos",
                    () -> DataComponentType.<GlobalPos>builder().persistent(GlobalPos.CODEC)
                            .networkSynchronized(GlobalPos.STREAM_CODEC).build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<WarpTarget>> WARP_BLOCK =
            WarpPipes.COMPONENTS.register("warp_block", () -> DataComponentType.<WarpTarget>builder()
                    .persistent(WarpTarget.CODEC).networkSynchronized(WarpTarget.STREAM_CODEC).build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<String>> WARP_DIMENSION =
            WarpPipes.COMPONENTS.register("warp_dimension",
                    () -> DataComponentType.<String>builder().persistent(Codec.STRING)
                            .networkSynchronized(ByteBufCodecs.STRING_UTF8).build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<UUID>> WARP_UUID =
            WarpPipes.COMPONENTS.register("warp_uuid",
                    () -> DataComponentType.<UUID>builder().persistent(UUIDUtil.CODEC)
                            .networkSynchronized(UUIDUtil.STREAM_CODEC).build());

    public static void init() {}

    public record WarpTarget(BlockPos pos, String name) {
        public static final Codec<WarpTarget> CODEC = RecordCodecBuilder.create(instance ->
                instance.group(BlockPos.CODEC.fieldOf("pos").forGetter(WarpTarget::pos),
                                Codec.STRING.fieldOf("name").forGetter(WarpTarget::name))
                        .apply(instance, WarpTarget::new));

        public static final StreamCodec<FriendlyByteBuf, String> STRING_CODEC =
                StreamCodec.of(FriendlyByteBuf::writeUtf, buf -> buf.readUtf(32767));

        public static final StreamCodec<FriendlyByteBuf, WarpTarget> STREAM_CODEC =
                StreamCodec.composite(BlockPos.STREAM_CODEC, WarpTarget::pos,
                        STRING_CODEC, WarpTarget::name, WarpTarget::new);
    }
}
