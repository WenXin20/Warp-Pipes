package com.wenxin2.warp_pipes;

import com.mojang.logging.LogUtils;
import com.wenxin2.warp_pipes.init.ModRegistry;
import com.wenxin2.warp_pipes.utils.DoorEventHandler;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(WarpPipes.MODID)
public class WarpPipes
{
    // Define mod id in a common place for everything to reference
    public static final String MODID = "warp_pipes";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();
    // Create a Deferred Register to hold blocks/items which will all be registered under the "warp_pipes" namespace
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, WarpPipes.MODID);

    public static CreativeModeTab CREATIVE_TAB = new CreativeModeTab("warp_pipes")
    {
        @Override
        public ItemStack makeIcon()
        {
            return ModRegistry.GREEN_WARP_PIPE.get().asItem().getDefaultInstance();
        }
    };

    public WarpPipes()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Register the Deferred Register to the mod event bus so blocks/items get registered
        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        BLOCK_ENTITIES.register(modEventBus);

//        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.CONFIG);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(DoorEventHandler.class);
    }
}
