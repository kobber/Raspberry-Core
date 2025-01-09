package cc.cassian.raspberry;

import cc.cassian.raspberry.client.config.ModConfigFactory;
import cc.cassian.raspberry.config.ModConfig;
import cc.cassian.raspberry.registry.RaspberryBlocks;
import cc.cassian.raspberry.registry.RaspberryItems;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(RaspberryMod.MOD_ID)
public final class RaspberryMod {
    public static final String MOD_ID = "raspberry";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public RaspberryMod() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like registries and resources) may still be uninitialized.
        // Proceed with mild caution.
        ModConfig.load();
        FMLJavaModLoadingContext context = FMLJavaModLoadingContext.get(); //replace this with initializer once RF updates
        IEventBus modEventBus = context.getModEventBus();
        RaspberryBlocks.BLOCKS.register(modEventBus);
        RaspberryItems.ITEMS.register(modEventBus);
        registerModsPage(context);
    }

    //Integrate Cloth Config screen (if mod present) with Forge mod menu.
    public static void registerModsPage(FMLJavaModLoadingContext context) {
        if (ModList.get().isLoaded("cloth_config"))
            context.registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class, () -> new ConfigScreenHandler.ConfigScreenFactory(ModConfigFactory::createScreen));
    }
}
