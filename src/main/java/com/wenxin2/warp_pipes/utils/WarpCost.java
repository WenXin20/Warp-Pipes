package com.wenxin2.warp_pipes.utils;

import com.wenxin2.warp_pipes.blocks.WarpPipeBlock;
import com.wenxin2.warp_pipes.integration.sable_compat.SableProvider;
import com.wenxin2.warp_pipes.registries.ConfigRegistry;
import com.wenxin2.warp_pipes.registries.TagRegistry;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class WarpCost {
    public static final Cost FREE = new Cost(0, 0);

    public record Cost(int xp, int fuel) {}

    public static Cost getBlockCost(Level level, BlockPos source, @Nullable BlockPos destination, BlockState state) {
        return WarpCost.getBlockCost(WarpCost.getDistance(level, source, destination), state);
    }

    public static Cost getBlockCost(double distance, BlockState state) {
        if (state.getBlock() instanceof WarpPipeBlock)
            return WarpCost.getCost(distance, ConfigRegistry.WARP_PIPE_XP_COST_MULTIPLIER.get(), ConfigRegistry.WARP_PIPE_FUEL_COST_MULTIPLIER.get());
        return FREE;
    }

    public static List<Player> getPayingPlayers(Entity entity) {
        Entity rootVehicle = entity.getRootVehicle();
        List<Player> players = new ArrayList<>();

        if (rootVehicle instanceof Player player)
            players.add(player);
        for (Entity passenger : rootVehicle.getIndirectPassengers()) {
            if (passenger instanceof Player player)
                players.add(player);
        }
        return players;
    }

    public static boolean canAfford(Entity entity, Cost cost) {
        return WarpCost.canAfford(WarpCost.getPayingPlayers(entity), cost);
    }

    public static boolean canAfford(List<Player> players, Cost cost) {
        if (cost.xp() <= 0 && cost.fuel() <= 0)
            return true;

        boolean missingXp = false;
        boolean missingFuel = false;

        for (Player player : players) {
            if (player.getAbilities().instabuild || player.isSpectator())
                continue;
            if (cost.xp() > 0 && WarpCost.getTotalExperience(player) < cost.xp())
                missingXp = true;
            if (cost.fuel() > 0 && WarpCost.countFuel(player) < cost.fuel())
                missingFuel = true;
        }

        if (!missingXp && !missingFuel)
            return true;

        MutableComponent message = WarpCost.getMissingMessage(cost, missingXp, missingFuel).withStyle(ChatFormatting.RED);
        for (Player player : players) {
            if (!player.level().isClientSide)
                player.displayClientMessage(message, true);
        }
        return false;
    }

    public static void chargeIfWarped(Entity entity, Vec3 startPos, Cost cost) {
        if (!entity.level().isClientSide && !entity.position().equals(startPos))
            WarpCost.charge(WarpCost.getPayingPlayers(entity), cost);
    }

    public static void charge(List<Player> players, Cost cost) {
        for (Player player : players) {
            if (player.level().isClientSide || player.getAbilities().instabuild || player.isSpectator())
                continue;
            if (cost.xp() > 0)
                player.giveExperiencePoints(-cost.xp());
            if (cost.fuel() > 0)
                WarpCost.consumeFuel(player, cost.fuel());
        }
    }

    private static double getDistance(Level level, BlockPos source, @Nullable BlockPos destination) {
        if (destination == null)
            return -1.0;

        BlockPos worldSource = SableProvider.isLoaded() ? SableProvider.toWorldPos(level, source) : source;
        BlockPos worldDestination = SableProvider.isLoaded() ? SableProvider.toWorldPos(level, destination) : destination;
        if (worldSource == null || worldDestination == null)
            return -1.0;
        return Math.sqrt(worldSource.distSqr(worldDestination));
    }

    private static Cost getCost(double distance, double xpMultiplier, double fuelMultiplier) {
        if (distance < 0.0 || (xpMultiplier <= 0.0 && fuelMultiplier <= 0.0))
            return FREE;
        return new Cost(xpMultiplier > 0.0 ? Mth.ceil(distance * xpMultiplier) : 0,
                fuelMultiplier > 0.0 ? Mth.ceil(distance * fuelMultiplier) : 0);
    }

    private static MutableComponent getMissingMessage(Cost cost, boolean missingXp, boolean missingFuel) {
        int requiredLevel = WarpCost.getLevelForExperience(cost.xp());
        Component levelPlural = WarpCost.getPlural(requiredLevel);
        Component fuelPlural = WarpCost.getPlural(cost.fuel());

        if (missingXp && missingFuel)
            return Component.translatable("display.warp_pipes.warp_not_enough_xp_and_fuel",
                    requiredLevel, levelPlural, cost.fuel(), WarpCost.getFuelName(), fuelPlural);
        if (missingXp)
            return Component.translatable("display.warp_pipes.warp_not_enough_xp", requiredLevel, levelPlural);
        return Component.translatable("display.warp_pipes.warp_not_enough_fuel", cost.fuel(), WarpCost.getFuelName(), fuelPlural);
    }

    private static Component getPlural(int amount) {
        return amount == 1 ? Component.empty() : Component.translatable("display.warp_pipes.warp_cost.plural");
    }

    private static Component getFuelName() {
        ItemStack fallback = new ItemStack(Items.ENDER_PEARL);

        if (fallback.is(TagRegistry.WARP_FUEL))
            return fallback.getHoverName();
        return BuiltInRegistries.ITEM.getTag(TagRegistry.WARP_FUEL)
                .flatMap(items -> items.stream().findFirst())
                .map(item -> new ItemStack(item).getHoverName())
                .orElse(fallback.getHoverName());
    }

    private static int countFuel(Player player) {
        Inventory inventory = player.getInventory();
        int total = 0;

        for (int slot = 0; slot < inventory.getContainerSize(); slot++) {
            ItemStack stack = inventory.getItem(slot);
            if (stack.is(TagRegistry.WARP_FUEL))
                total += stack.getCount();
        }
        return total;
    }

    private static void consumeFuel(Player player, int cost) {
        Inventory inventory = player.getInventory();
        int remaining = cost;

        for (int slot = 0; slot < inventory.getContainerSize() && remaining > 0; slot++) {
            ItemStack stack = inventory.getItem(slot);

            if (stack.is(TagRegistry.WARP_FUEL)) {
                int used = Math.min(stack.getCount(), remaining);
                stack.shrink(used);
                remaining -= used;
            }
        }
        inventory.setChanged();
    }

    private static int getTotalExperience(Player player) {
        int total = 0;

        for (int level = 0; level < player.experienceLevel; level++)
            total += WarpCost.getXpNeededForLevel(level);
        return total + Mth.floor(player.experienceProgress * player.getXpNeededForNextLevel());
    }

    private static int getLevelForExperience(int experience) {
        int level = 0;

        for (int total = 0; total < experience; level++)
            total += WarpCost.getXpNeededForLevel(level);
        return level;
    }

    private static int getXpNeededForLevel(int level) {
        if (level >= 30)
            return 112 + (level - 30) * 9;
        return level >= 15 ? 37 + (level - 15) * 5 : 7 + level * 2;
    }
}
