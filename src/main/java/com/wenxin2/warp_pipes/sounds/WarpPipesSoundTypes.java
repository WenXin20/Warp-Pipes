package com.wenxin2.warp_pipes.sounds;

import com.wenxin2.warp_pipes.integration.CompatRegistry;
import com.wenxin2.warp_pipes.registries.SoundRegistry;
import com.wenxin2.warp_pipes.registries.TagRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ArmorStandItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BoatItem;
import net.minecraft.world.item.EggItem;
import net.minecraft.world.item.ExperienceBottleItem;
import net.minecraft.world.item.FireChargeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.LingeringPotionItem;
import net.minecraft.world.item.MinecartItem;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.WindChargeItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.TntBlock;
import net.neoforged.neoforge.common.util.DeferredSoundType;

public class WarpPipesSoundTypes {
    public static final SoundType WATER_SPOUT_TYPE = new DeferredSoundType(1.0F, 1.0F, () -> SoundEvents.BUCKET_FILL,
            () -> SoundEvents.BUCKET_FILL, () -> SoundEvents.BUCKET_EMPTY, () -> SoundEvents.BUCKET_FILL, () -> SoundEvents.BUCKET_FILL);

    public static void playSounds(Level world, BlockPos pos, ItemStack stack) {
        if (stack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() == CompatRegistry.STAR_COIN.get())
            world.playSound(null, pos, CompatRegistry.STAR_COIN_PICKUP_SOUND.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
        else if (stack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() == CompatRegistry.COIN.get())
            world.playSound(null, pos, CompatRegistry.COIN_PICKUP_SOUND.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
        else if (stack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof TntBlock)
            world.playSound(null, pos, SoundEvents.TNT_PRIMED, SoundSource.BLOCKS, 1.0F, 1.0F);
        else if (stack.getItem() instanceof ArmorStandItem)
            world.playSound(null, pos, SoundEvents.ARMOR_STAND_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F);
        else if (CompatRegistry.POWER_UP_SPAWNS.get() != null && stack.is(TagRegistry.POWER_UPS_ITEMS) || stack.getItem() == CompatRegistry.DASH_MUSHROOM.get())
            world.playSound(null, pos, CompatRegistry.POWER_UP_SPAWNS.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
        else if (stack.getItem() instanceof BoatItem)
            world.playSound(null, pos, SoundEvents.BOAT_PADDLE_WATER, SoundSource.BLOCKS, 1.0F, 1.0F);
        else if (stack.getItem() instanceof EggItem)
            world.playSound(null, pos, SoundEvents.EGG_THROW, SoundSource.BLOCKS, 1.0F, 1.0F);
        else if (stack.getItem() instanceof ExperienceBottleItem)
            world.playSound(null, pos, SoundEvents.EXPERIENCE_BOTTLE_THROW, SoundSource.BLOCKS, 1.0F, 1.0F);
        else if (stack.getItem() instanceof FireChargeItem)
            world.playSound(null, pos, SoundEvents.FIRECHARGE_USE, SoundSource.BLOCKS, 1.0F, 1.0F);
        else if (stack.getItem() instanceof LingeringPotionItem)
            world.playSound(null, pos, SoundEvents.LINGERING_POTION_THROW, SoundSource.BLOCKS, 1.0F, 1.0F);
        else if (stack.getItem() instanceof MinecartItem)
            world.playSound(null, pos, SoundEvents.MINECART_RIDING, SoundSource.BLOCKS, 1.0F, 1.0F);
        else if (stack.getItem() instanceof PotionItem)
            world.playSound(null, pos, SoundEvents.SPLASH_POTION_THROW, SoundSource.BLOCKS, 1.0F, 1.0F);
        else if (CompatRegistry.POWER_UP_SPAWNS.get() != null && stack.getItem() instanceof SpawnEggItem)
            world.playSound(null, pos, CompatRegistry.MOB_SPAWNS.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
        else if (stack.getItem() instanceof WindChargeItem)
            world.playSound(null, pos, SoundEvents.WIND_CHARGE_THROW, SoundSource.BLOCKS, 1.0F, 1.0F);
        else if (stack.getItem() == CompatRegistry.BOMB_ITEM.get()
                || stack.getItem() == CompatRegistry.BOMB_BLUE_ITEM.get()
                || stack.getItem() == CompatRegistry.BOMB_SPIKY_ITEM.get())
            world.playSound(null, pos, CompatRegistry.BOMB_SOUND.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
        else if (stack.getItem() == CompatRegistry.CANNONBALL_ITEM.get())
            world.playSound(null, pos, CompatRegistry.CANNON_SOUND.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
        else if (stack.getItem() == CompatRegistry.CONFETTI_POPPER_ITEM.get())
            world.playSound(null, pos, CompatRegistry.CONFETTI_POPPER_SOUND.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
        else if (stack.getItem() == CompatRegistry.HAT_STAND_ITEM.get())
            world.playSound(null, pos, SoundEvents.ARMOR_STAND_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F);
        else if (stack.getItem() == CompatRegistry.ICE_BOMB_ITEM.get())
            world.playSound(null, pos, CompatRegistry.ICE_BOMB_SOUND.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
        else world.playSound(null, pos, SoundRegistry.ITEM_SPAWNS.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
    }
}
