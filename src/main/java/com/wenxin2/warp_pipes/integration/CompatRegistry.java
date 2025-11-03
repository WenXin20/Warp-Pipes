package com.wenxin2.warp_pipes.integration;

import com.google.common.base.Suppliers;
import java.util.function.Supplier;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

public class CompatRegistry {
    public static final Supplier<Block> COIN = make("marioverse:coin", BuiltInRegistries.BLOCK);
    public static final Supplier<Block> STAR_COIN = make("marioverse:star_coin", BuiltInRegistries.BLOCK);

    public static final Supplier<Item> ANTIQUE_INK = make("supplementaries:antique_ink", BuiltInRegistries.ITEM);
    public static final Supplier<Item> BOMB_BLUE_ITEM = make("supplementaries:bomb_blue", BuiltInRegistries.ITEM);
    public static final Supplier<Item> BOMB_ITEM = make("supplementaries:bomb", BuiltInRegistries.ITEM);
    public static final Supplier<Item> BOMB_SPIKY_ITEM = make("supplementaries:bomb_spiky", BuiltInRegistries.ITEM);
    public static final Supplier<Item> BUBBLE_BLOWER = make("supplementaries:bubble_blower", BuiltInRegistries.ITEM);
    public static final Supplier<Item> CANNONBALL_ITEM = make("supplementaries:cannonball", BuiltInRegistries.ITEM);
    public static final Supplier<Item> CONFETTI_POPPER_ITEM = make("supplementaries:confetti_popper", BuiltInRegistries.ITEM);
    public static final Supplier<Item> DASH_MUSHROOM = make("supplementaries:dash_mushroom", BuiltInRegistries.ITEM);
    public static final Supplier<Item> HAT_STAND_ITEM = make("supplementaries:hat_stand", BuiltInRegistries.ITEM);
    public static final Supplier<Item> ICE_BOMB_ITEM = make("twilightforest:ice_bomb", BuiltInRegistries.ITEM);
    public static final Supplier<Item> SOAP = make("supplementaries:soap", BuiltInRegistries.ITEM);

    public static final Supplier<EntityType<?>> TEST_DUMMY = make("dummmmmmy:test_dummy", BuiltInRegistries.ENTITY_TYPE);
    public static final Supplier<EntityType<?>> BOMB = make("supplementaries:bomb", BuiltInRegistries.ENTITY_TYPE);
    public static final Supplier<EntityType<?>> CANNONBALL = make("supplementaries:cannonball", BuiltInRegistries.ENTITY_TYPE);
    public static final Supplier<EntityType<?>> HAT_STAND = make("supplementaries:hat_stand", BuiltInRegistries.ENTITY_TYPE);
    public static final Supplier<EntityType<?>> ICE_BOMB = make("twilightforest:thrown_ice", BuiltInRegistries.ENTITY_TYPE);
    public static final Supplier<EntityType<?>> PIRANHA_PLANT = make("marioverse:piranha_plant", BuiltInRegistries.ENTITY_TYPE);

    public static final Supplier<SoundEvent> BOMB_SOUND = make("supplementaries:item.bomb", BuiltInRegistries.SOUND_EVENT);
    public static final Supplier<SoundEvent> BUBBLE_BLOWER_SOUND = make("supplementaries:item.bubble_blower", BuiltInRegistries.SOUND_EVENT);
    public static final Supplier<SoundEvent> CANNON_SOUND = make("supplementaries:block.cannon.fire", BuiltInRegistries.SOUND_EVENT);
    public static final Supplier<SoundEvent> COIN_PICKUP_SOUND = make("marioverse:block.coin_pickup", BuiltInRegistries.SOUND_EVENT);
    public static final Supplier<SoundEvent> CONFETTI_POPPER_SOUND = make("supplementaries:item.confetti_popper", BuiltInRegistries.SOUND_EVENT);
    public static final Supplier<SoundEvent> ICE_BOMB_SOUND = make("twilightforest:item.twilightforest.ice_bomb.fired", BuiltInRegistries.SOUND_EVENT);
    public static final Supplier<SoundEvent> ITEM_SPAWNS = make("marioverse:block.item_spawns", BuiltInRegistries.SOUND_EVENT);
    public static final Supplier<SoundEvent> MOB_SPAWNS = make("marioverse:block.mob_spawns", BuiltInRegistries.SOUND_EVENT);
    public static final Supplier<SoundEvent> POWER_UP_SPAWNS = make("marioverse:block.power_up_spawns", BuiltInRegistries.SOUND_EVENT);
    public static final Supplier<SoundEvent> STAR_COIN_PICKUP_SOUND = make("marioverse:block.star_coin_pickup", BuiltInRegistries.SOUND_EVENT);

    public static final Supplier<ParticleType<?>> SUDS_PARTICLE = make("supplementaries:suds", BuiltInRegistries.PARTICLE_TYPE);

    private static <T> Supplier<@Nullable T> make(String name, Registry<T> registry) {
        return Suppliers.memoize(() -> registry.getOptional(ResourceLocation.parse(name)).orElse(null));
    }
}
