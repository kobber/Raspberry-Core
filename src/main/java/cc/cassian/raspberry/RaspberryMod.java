package cc.cassian.raspberry;

import cc.cassian.raspberry.client.config.ModConfigFactory;
import cc.cassian.raspberry.compat.*;
import cc.cassian.raspberry.compat.oreganized.OreganizedEvents;
import cc.cassian.raspberry.compat.oreganized.network.RaspberryOreganizedNetwork;
import cc.cassian.raspberry.config.ModConfig;
import cc.cassian.raspberry.registry.RaspberryAttributes;
import cc.cassian.raspberry.registry.RaspberryBlocks;
import cc.cassian.raspberry.registry.RaspberryItems;
import cc.cassian.raspberry.registry.RasperryMobEffects;
import com.teamabnormals.blueprint.common.world.storage.tracking.DataProcessors;
import com.teamabnormals.blueprint.common.world.storage.tracking.TrackedData;
import com.teamabnormals.blueprint.common.world.storage.tracking.TrackedDataManager;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.EntityStruckByLightningEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(RaspberryMod.MOD_ID)
public final class RaspberryMod {
    public static final String MOD_ID = "raspberry";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static final TrackedData<Integer> WORM_HUNTING_TIME = TrackedData.Builder.create(DataProcessors.INT, () -> 0).enableSaving().build();
    public static final TrackedData<Integer> SNIFF_SOUND_TIME = TrackedData.Builder.create(DataProcessors.INT, () -> 0).build();
    public static final TrackedData<BlockPos> WORM_POS = TrackedData.Builder.create(DataProcessors.POS, () -> BlockPos.ZERO).enableSaving().build();
    public static final TrackedData<Boolean> HAS_WORM_TARGET = TrackedData.Builder.create(DataProcessors.BOOLEAN, () -> false).enableSaving().build();
    public static final TrackedData<Boolean> LOOKING_FOR_WORM = TrackedData.Builder.create(DataProcessors.BOOLEAN, () -> false).build();

    public RaspberryMod(FMLJavaModLoadingContext context) {
        var eventBus = context.getModEventBus();
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like registries and resources) may still be uninitialized.
        // Proceed with mild caution.
        ModConfig.load();
        // Register deferred registers.
        RaspberryBlocks.BLOCKS.register(eventBus);
        RaspberryItems.ITEMS.register(eventBus);
        RasperryMobEffects.MOB_EFFECTS.register(eventBus);
        // Register config
        registerModsPage(context);
        // Register event bus listeners.
        MinecraftForge.EVENT_BUS.addListener(this::onItemTooltipEvent);
        MinecraftForge.EVENT_BUS.addListener(this::onEntityInteract);
        MinecraftForge.EVENT_BUS.addListener(this::onEntityJoinLevel);
        MinecraftForge.EVENT_BUS.addListener(this::onLivingUpdate);
        eventBus.addListener(RaspberryMod::commonSetup);
        MinecraftForge.EVENT_BUS.addListener(RaspberryMod::copperTick);
        MinecraftForge.EVENT_BUS.addListener(RaspberryMod::lightningTick);
        if (ModCompat.OREGANIZED) {
            RaspberryAttributes.ATTRIBUTES.register(eventBus);
            RaspberryOreganizedNetwork.register();
            MinecraftForge.EVENT_BUS.addListener(OreganizedEvents::onItemAttributes);
        }

        TrackedDataManager.INSTANCE.registerData(new ResourceLocation(MOD_ID, "truffle_hunting_time"), WORM_HUNTING_TIME);
        TrackedDataManager.INSTANCE.registerData(new ResourceLocation(MOD_ID, "sniff_sound_time"), SNIFF_SOUND_TIME);
        TrackedDataManager.INSTANCE.registerData(new ResourceLocation(MOD_ID, "truffle_pos"), WORM_POS);
        TrackedDataManager.INSTANCE.registerData(new ResourceLocation(MOD_ID, "has_truffle_target"), HAS_WORM_TARGET);
        TrackedDataManager.INSTANCE.registerData(new ResourceLocation(MOD_ID, "looking_for_truffle"), LOOKING_FOR_WORM);
    }

    @SubscribeEvent
    public static void commonSetup(FMLCommonSetupEvent event) {
        if (ModCompat.NEAPOLITAN)
            NeapolitanCompat.boostAgility();
        if (ModCompat.QUARK) {
            QuarkCompat.register();
        }
    }

    @SubscribeEvent
    public static void lightningTick(EntityStruckByLightningEvent event) {
        if (ModCompat.COPPERIZED && !ModCompat.COFH_CORE && ModConfig.get().aftershock)
            CopperizedCompat.electrify(event);
    }

    @SubscribeEvent
    public static void copperTick(TickEvent.PlayerTickEvent event) {
        if (ModCompat.COPPERIZED && ModCompat.COFH_CORE)
            CopperizedCompat.resist(event);
    }

    /**
	 * Integrate Cloth Config screen (if mod present) with Forge mod menu.
	 */
    public static void registerModsPage(FMLJavaModLoadingContext context) {
        if (ModCompat.CLOTH_CONFIG)
            context.registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class, () -> new ConfigScreenHandler.ConfigScreenFactory(ModConfigFactory::createScreen));
    }

    @SubscribeEvent
    public void onItemTooltipEvent(ItemTooltipEvent event) {
        if (ModCompat.AQUACULTURE)
            AquacultureCompat.checkAndAddTooltip(event);
    }

    @SubscribeEvent
    public void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        if (ModCompat.ENVIRONMENTAL)
            EnvironmentalCompat.onEntityInteract(event);
    }

    @SubscribeEvent
    public void onEntityJoinLevel(EntityJoinLevelEvent event) {
        if (ModCompat.ENVIRONMENTAL)
            EnvironmentalCompat.onEntityJoinWorld(event);
    }

    @SubscribeEvent
    public void onLivingUpdate(LivingEvent.LivingTickEvent event) {
        if (ModCompat.ENVIRONMENTAL)
            EnvironmentalCompat.onLivingUpdate(event);
    }
}
