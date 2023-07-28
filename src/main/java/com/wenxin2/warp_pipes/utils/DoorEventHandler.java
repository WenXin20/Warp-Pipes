package com.wenxin2.warp_pipes.utils;

import com.wenxin2.warp_pipes.WarpPipes;
import com.wenxin2.warp_pipes.blocks.entities.WarpDoorBlockEntity;
import com.wenxin2.warp_pipes.init.ModRegistry;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.ServerLifecycleHooks;

@Mod.EventBusSubscriber(modid = WarpPipes.MODID)
public class DoorEventHandler {
    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        Player player = event.getEntity();
        BlockPos pos = event.getPos();
        BlockState state = event.getLevel().getBlockState(pos);
        Block block = state.getBlock();
        InteractionHand hand = player.getUsedItemHand();
        ItemStack itemInHand = player.getItemInHand(hand);

        if (player != null && player.isShiftKeyDown() && itemInHand.is(ModRegistry.PIPE_WRENCH.get())) {
            // If the player is right-clicking without sneaking, and the block is a door block
            if (block instanceof DoorBlock) {
                // Check if the block entity does not already exist at this position
                if (!event.getLevel().isClientSide && event.getLevel().getBlockEntity(pos) == null) {
                    // Create and attach the block entity
                    BlockEntity blockEntity = new WarpDoorBlockEntity(pos, state);

                    // Replace the existing door block with the new one containing the block entity
                    Property<DoubleBlockHalf> half = DoorBlock.HALF;
                    BlockState newState = state.cycle(half);
                    event.getLevel().setBlock(pos, newState, 3); // The last parameter 3 notifies neighbors of the block change
                    event.getLevel().setBlockEntity(blockEntity);
                }
            }
        }
    }

    // Create a map to keep track of entities inside door blocks
    private static final Map<Entity, BlockPos> entitiesInsideDoorBlocks = new HashMap<>();

    @SubscribeEvent
    public static void onWorldTick(TickEvent.LevelTickEvent event) {
        if (event.phase == TickEvent.Phase.END && event.side.isServer()) {
            for (Entity entity : event.level.getAllEntities()) {
                if (entity instanceof Player && entity.isAlive()) {
                    BlockPos pos = entity.blockPosition();
                    BlockState state = event.level.getBlockState(pos);
                    Block block = state.getBlock();

                    if (block instanceof DoorBlock) {
                        DoorBlock doorBlock = (DoorBlock) block;
                        boolean isInsideDoor = doorBlock.entityInside(state, event.level, pos, entity);
                        if (isInsideDoor) {
                            entitiesInsideDoorBlocks.put(entity, pos);

                            // Add your custom behavior here when an entity is inside the door block

                            BlockEntity blockEntity = event.level.getBlockEntity(pos);
                            if (blockEntity instanceof WarpDoorBlockEntity warpDoorBE) {
                                BlockPos destinationPos = warpDoorBE.destinationPos;
                                int entityId = entity.getId();
                                DoorEventHandler.addEntityInsideDoorBlock(entity, pos);
                                DoorEventHandler.warp(entity, destinationPos, event.level, state);
                                DoorEventHandler.removeEntityInsideDoorBlock(entity);
                                entity.setPortalCooldown();
                                entity.portalCooldown = 20;
                            }
                        } else {
                            entitiesInsideDoorBlocks.remove(entity.getId());
                        }
                    } else {
                        entitiesInsideDoorBlocks.remove(entity.getId());
                    }
                }
            }
        }
    }

    // Add entities to the tracking list
    public static void addEntityInsideDoorBlock(Entity entity, BlockPos pos) {
        entitiesInsideDoorBlocks.put(entity, pos);
    }

    // Remove entities from the tracking list
    public static void removeEntityInsideDoorBlock(Entity entity) {
        entitiesInsideDoorBlocks.remove(entity);
    }

    public static void warp(Entity entity, BlockPos pos, Level world, BlockState state) {
        if (world.getBlockState(pos).getBlock() instanceof DoorBlock) {
            Entity passengerEntity = entity.getControllingPassenger();
            if (entity instanceof Player) {
                entity.teleportTo(pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5);
            } else {
                entity.teleportTo(pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5);
                if (passengerEntity instanceof Player) {
                    entity.unRide();
                }
            }
            DoorEventHandler.markEntityTeleported(entity);
        }
        world.gameEvent(GameEvent.TELEPORT, pos, GameEvent.Context.of(entity));
        world.playSound(null, pos, SoundEvents.ENDERMAN_TELEPORT, SoundSource.BLOCKS, 1.0F, 0.1F);
    }

    // Store a map to track whether entities have teleported or not
    private static final Map<Integer, Boolean> teleportedEntities = new HashMap<>();

    // Method to mark an entity as teleported
    public static void markEntityTeleported(Entity entity) {
        if (entity != null) {
            teleportedEntities.put(entity.getId(), true);
        }
    }
}
