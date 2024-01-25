package com.wenxin2.warp_pipes.blocks;

import com.wenxin2.warp_pipes.init.Config;
import com.wenxin2.warp_pipes.init.ModRegistry;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class WaterSpoutBlock extends Block implements BucketPickup {
    public static final BooleanProperty TOP = BooleanProperty.create("top");

    public WaterSpoutBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(TOP, Boolean.FALSE));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateBuilder) {
        stateBuilder.add(TOP);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter blockGetter, BlockPos pos, CollisionContext context) {
        if (context instanceof EntityCollisionContext && ((EntityCollisionContext)context).getEntity() instanceof Player player
                && player.hasPermissions(1) && player.isCreative() && Config.DEBUG_WATER_SPOUT_SELECTION_BOX.get()) {
            return Block.box(5, 0, 5, 11, 16, 11).optimize();
        }
        // Shapes.empty() causes a crash, use a tiny bounding box instead
        return Shapes.box(8, 8, 8, 8.00001, 8.00001, 8.00001);
    }

    @Override
    public void tick(BlockState state, ServerLevel serverWorld, BlockPos pos, RandomSource random) {
        WaterSpoutBlock.repeatColumnUp(serverWorld, pos, state, serverWorld.getBlockState(pos.below()));
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState,
                                  LevelAccessor worldAccessor, BlockPos pos, BlockPos neighborPos) {
        super.updateShape(state, direction, neighborState, worldAccessor, pos, neighborPos);
        Block blockAbove = worldAccessor.getBlockState(pos.above()).getBlock();

        if (!state.canSurvive(worldAccessor, pos) && !neighborState.is(ModRegistry.WATER_SPOUT.get())
                 && canExistIn(neighborState)) {
            worldAccessor.scheduleTick(pos, this, 3);
        }

        worldAccessor.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(worldAccessor));

        if (blockAbove == this) {
            return state.setValue(TOP, Boolean.FALSE);
        }
        else return state.setValue(TOP, Boolean.TRUE);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader worldReader, BlockPos pos) {
        BlockState stateBelow = worldReader.getBlockState(pos.below());

        return (stateBelow.is(ModRegistry.WATER_SPOUT.get())
                || (stateBelow.getBlock() instanceof WarpPipeBlock && !stateBelow.getValue(WarpPipeBlock.CLOSED)
                && !(stateBelow.getBlock() instanceof ClearWarpPipeBlock))
                || (stateBelow.getBlock() instanceof ClearWarpPipeBlock && !stateBelow.getValue(WarpPipeBlock.CLOSED)
                && stateBelow.getValue(ClearWarpPipeBlock.WATERLOGGED)));
    }

    public static boolean canExistIn(BlockState state) {
        return state.is(ModRegistry.WATER_SPOUT.get()) || state.isAir();
    }

    public static BlockState setBlockState(BlockState state, LevelAccessor worldAccessor, BlockPos pos) {
        if (state.is(ModRegistry.WATER_SPOUT.get())) {
            if (worldAccessor.getBlockState(pos.above()).is(ModRegistry.WATER_SPOUT.get()))
                return state.setValue(TOP, Boolean.FALSE);
            else return state.setValue(TOP, Boolean.TRUE);
        } else if (state.getBlock() instanceof WarpPipeBlock && !state.getValue(WarpPipeBlock.CLOSED) && state.getValue(WarpPipeBlock.WATER_SPOUT)) {
            if (worldAccessor.getBlockState(pos.above()).is(ModRegistry.WATER_SPOUT.get()))
                return ModRegistry.WATER_SPOUT.get().defaultBlockState().setValue(TOP, Boolean.FALSE);
            else return ModRegistry.WATER_SPOUT.get().defaultBlockState().setValue(TOP, Boolean.TRUE);
        }
        return Blocks.AIR.defaultBlockState();
    }

    public void addParticles(Level world, ParticleOptions particleOptions, double xPos, double yPos, double zPos,
                             int amt, double xMotion, double yMotion, double zMotion, double speed) {
        if (!world.isClientSide) {
            ServerLevel serverWorld = (ServerLevel)world;
            serverWorld.sendParticles(particleOptions, xPos, yPos, zPos, amt, xMotion, yMotion, zMotion, speed);
        }
    }

    public void addAlwaysVisibleParticles(Level world, ParticleOptions particleOptions, double xPos, double yPos, double zPos,
                                          double xMotion, double yMotion, double zMotion) {
        if (world.isClientSide) {
            world.addAlwaysVisibleParticle(particleOptions, xPos, yPos, zPos, xMotion, yMotion, zMotion);
        }
    }

    @Override
    public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource random) {
        double randomNum = -1 + random.nextDouble();
        double x = pos.getX();
        double y = pos.getY();
        double z = pos.getZ();


        this.addAlwaysVisibleParticles(world, ParticleTypes.BUBBLE, x + 0.5D, y, z + 0.5D, 0.0D, 1.0D, 0.0D);
        this.addAlwaysVisibleParticles(world, ParticleTypes.BUBBLE, x + random.nextFloat(),
                y + random.nextFloat(), z + random.nextFloat(), 0.0D, 1.0D, 0.0D);

        if (world.getBlockState(pos.above()).isAir()) {
            for (int i = 0; i < 75; ++i)
                this.addAlwaysVisibleParticles(world, ParticleTypes.SPLASH, x + random.nextDouble(),
                        y + 1, z + random.nextDouble(), randomNum, 0.04D, randomNum);
            for (int i = 0; i < 10; ++i)
                this.addAlwaysVisibleParticles(world, ParticleTypes.BUBBLE, x + random.nextDouble(),
                        y + 1, z + random.nextDouble(), 0.0D, 0.01D, 0.0D);
        }

        if (random.nextInt(20) == 0) {
            world.playLocalSound(x, y, z, SoundEvents.BUBBLE_COLUMN_UPWARDS_AMBIENT, SoundSource.BLOCKS,
                    0.2F + random.nextFloat() * 0.2F, 0.9F + random.nextFloat() * 0.15F, false);
        }
    }

    @Override
    public void entityInside(BlockState state, Level world, BlockPos pos, Entity entity) {
        BlockState stateAbove = world.getBlockState(pos.above());

        if (stateAbove.isAir()) {
            if (entity instanceof Boat boat) {
                boat.onAboveBubbleCol(Boolean.FALSE);
            } else this.onAboveUpBubbleCol(entity);
            if (!world.isClientSide) {
                ServerLevel serverWorld = (ServerLevel)world;

                for(int i = 0; i < 2; ++i) {
                    this.addParticles(serverWorld, ParticleTypes.SPLASH, pos.getX() + world.random.nextDouble(),
                            (pos.getY() + 1), pos.getZ() + world.random.nextDouble(), 1, 0.0D, 0.0D, 0.0D, 1.0D);
                    this.addParticles(serverWorld, ParticleTypes.BUBBLE, pos.getX() + world.random.nextDouble(),
                            (pos.getY() + 1), pos.getZ() + world.random.nextDouble(), 1, 0.0D, 0.01D, 0.0D, 0.2D);
                }
            }
        } else {
            this.onInsideUpBubbleColumn(entity);
            if (!world.isClientSide) {
                ServerLevel serverWorld = (ServerLevel)world;

                for(int i = 0; i < 2; ++i) {
                    this.addParticles(serverWorld, ParticleTypes.SPLASH, pos.getX() + world.random.nextDouble(),
                            (pos.getY() + 1), pos.getZ() + world.random.nextDouble(), 1, 0.0D, 0.0D, 0.0D, 1.0D);
                    this.addParticles(serverWorld, ParticleTypes.BUBBLE, pos.getX() + world.random.nextDouble(),
                            (pos.getY() + 1), pos.getZ() + world.random.nextDouble(), 1, 0.0D, 0.01D, 0.0D, 0.2D);
                }
            }
        }

       if (entity instanceof LivingEntity livingEntity) {
           if (!world.isClientSide && livingEntity.canDrownInFluidType(Fluids.WATER.getFluidType())) {
               int refillAmount = 1;
               int newAir = Math.min(livingEntity.getAirSupply() + refillAmount, livingEntity.getMaxAirSupply());
               livingEntity.setAirSupply(newAir);
           }
       }
    }

    public void onAboveUpBubbleCol(Entity entity) {
        Vec3 vec3 = entity.getDeltaMovement();
        double d0 = Math.min(1.8D, vec3.y + 0.1D);

        entity.setDeltaMovement(vec3.x, d0, vec3.z);
    }

    public void onInsideUpBubbleColumn(Entity entity) {
        Vec3 vec3 = entity.getDeltaMovement();
        double d0 = Math.min(0.5D, vec3.y + 0.04D);

        entity.setDeltaMovement(vec3.x, d0 + 0.05D, vec3.z);
        entity.resetFallDistance();
    }

    public static void repeatColumnUp(LevelAccessor worldAccessor, BlockPos pos, BlockState state) {
        repeatColumnUp(worldAccessor, pos, worldAccessor.getBlockState(pos), state);
    }

    public static void repeatColumnUp(LevelAccessor worldAccessor, BlockPos pos, BlockState state, BlockState neighborState) {
        if (canExistIn(state)) {
            int initialDistance = 0;
            BlockPos.MutableBlockPos mutablePos = pos.mutable().move(Direction.UP);

            BlockState pipeColumnState = WaterSpoutBlock.setBlockState(neighborState, worldAccessor, pos);
            worldAccessor.setBlock(pos, pipeColumnState, 2);

            while (canExistIn(worldAccessor.getBlockState(mutablePos)) && initialDistance < 4 - 1) {
                if (!worldAccessor.setBlock(mutablePos, pipeColumnState, 2)) {
                    return;
                }
                mutablePos.move(Direction.UP);
                initialDistance++;
                pipeColumnState = WaterSpoutBlock.setBlockState(pipeColumnState, worldAccessor, mutablePos);
            }
        }
    }

    public ItemStack pickupBlock(LevelAccessor worldAccessor, BlockPos pos, BlockState state) {
        worldAccessor.setBlock(pos, Blocks.AIR.defaultBlockState(), 11);
        return new ItemStack(Items.WATER_BUCKET);
    }

    public Optional<SoundEvent> getPickupSound() {
        return Fluids.WATER.getPickupSound();
    }
}