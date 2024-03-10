package com.wenxin2.warp_pipes.event_handlers;

import com.wenxin2.warp_pipes.WarpPipes;
import com.wenxin2.warp_pipes.init.Config;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.MushroomCow;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, modid = WarpPipes.MODID)
public class SpawnEventHandler {
    public static void onJoinWorld(final EntityJoinLevelEvent event)
    {
        if (!(event.getEntity() instanceof LivingEntity) && !(event.getEntity() instanceof Player)) return;

        Entity entity = event.getEntity();

        if (entity != null && !event.getEntity().getPersistentData().contains("warp_pipes:can_warp"))
            entity.getPersistentData().putBoolean("warp_pipes:can_warp", true);

        Animal mushroomEntity = ((MushroomCow) event.getEntity());
        if (entity != null && entity.getCustomName() != null && entity.hasCustomName() && entity instanceof Monster) {
            Monster animalEntity = ((Monster) event.getEntity());
            String customName = event.getEntity().getCustomName().getString().toLowerCase();
//            if (Config.HOSTILE_NAMES.get().contains(customName)) {
                animalEntity.targetSelector.addGoal(0, new NearestAttackableTargetGoal<>(animalEntity, MushroomCow.class, true));
            animalEntity.targetSelector.addGoal(0, new MeleeAttackGoal(mushroomEntity, 1.0D, true));
//            }
        }
    }

    public static void register()
    {
        WarpPipes.FORGE_BUS.addListener(SpawnEventHandler::onJoinWorld);
    }
}
