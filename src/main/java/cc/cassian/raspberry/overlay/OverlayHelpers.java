package cc.cassian.raspberry.overlay;

import cc.cassian.raspberry.ModCompat;
import cc.cassian.raspberry.compat.CavernsAndChasmsCompat;
import cc.cassian.raspberry.compat.SpelunkeryCompat;
import cc.cassian.raspberry.config.ModConfig;
import net.mehvahdjukaar.supplementaries.reg.ModRegistry;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class OverlayHelpers {
    public static boolean playerHasPotions(Player player) {
        // Technically, we should check whether these are ambient, but Map Atlases doesn't and still covers our overlay.
        return !player.getActiveEffects().isEmpty();
    }

    public static void checkInventoryForItems(Player player) {
        var xz = Items.COMPASS;
        var recoveryCompass = Items.RECOVERY_COMPASS;
        Item magneticCompass = Items.COMPASS;
        var y = Items.COMPASS;
        var clock = Items.CLOCK;
        var barometer = Items.CLOCK;

        if (ModCompat.SPELUNKERY) {
            y = SpelunkeryCompat.getDepthGauge();
            magneticCompass = SpelunkeryCompat.getMagneticCompass();
        } else if (ModCompat.CAVERNS_AND_CHASMS)
            y = CavernsAndChasmsCompat.getDepthGauge();
        if (ModCompat.CAVERNS_AND_CHASMS)
            barometer = CavernsAndChasmsCompat.getBarometer();
        if (ModConfig.get().overlay_compass_enable || ModConfig.get().overlay_clock_enable) {
            var inventory = player.getInventory();
            if (ModConfig.get().overlay_requireItemInHand) {
                var main = player.getMainHandItem();
                var offhand = player.getOffhandItem();
                CompassOverlay.hasCompass = main.is(xz) || offhand.is(xz);
                if (!CompassOverlay.hasCompass)
                    CompassOverlay.hasCompass = main.is(recoveryCompass) || offhand.is(recoveryCompass);
                if (!CompassOverlay.hasCompass && ModCompat.SPELUNKERY)
                    CompassOverlay.hasCompass = checkInventoryForItem(inventory, xz, "spelunkery:magentic_compass");
                CompassOverlay.hasDepthGauge = main.is(y) || offhand.is(y);
                ClockOverlay.hasClock = main.is(clock) || offhand.is(clock);
                ClockOverlay.hasBarometer = main.is(barometer) || offhand.is(barometer);
                if (ModCompat.CAVERNS_AND_CHASMS) {
                    ClockOverlay.hasBarometer = checkInventoryForItem(inventory, barometer, "caverns_and_chasms:barometer");
                    if (!CompassOverlay.hasDepthGauge) {
                        y = CavernsAndChasmsCompat.getDepthGauge();
                        CompassOverlay.hasDepthGauge = main.is(y) || offhand.is(y);
                    }
                } else {
                    ClockOverlay.hasBarometer = ClockOverlay.hasClock;
                }

            } else {
                CompassOverlay.hasCompass = checkInventoryForItem(inventory, xz, "minecraft:compass");
                if (!CompassOverlay.hasCompass)
                    CompassOverlay.hasCompass = checkInventoryForItem(inventory, recoveryCompass, "minecraft:recovery_compass");
                if (!CompassOverlay.hasCompass && ModCompat.SPELUNKERY)
                    CompassOverlay.hasCompass = checkInventoryForItem(inventory, magneticCompass, "spelunkery:magentic_compass");
                CompassOverlay.hasDepthGauge = checkInventoryForItem(inventory, y, "spelunkery:depth_gauge");
                ClockOverlay.hasClock = checkInventoryForItem(inventory, clock, "minecraft:clock");
                if (ModCompat.CAVERNS_AND_CHASMS) {
                    ClockOverlay.hasBarometer = checkInventoryForItem(inventory, y, "caverns_and_chasms:barometer");
                    if (!CompassOverlay.hasDepthGauge) {
                        y = CavernsAndChasmsCompat.getDepthGauge();
                        CompassOverlay.hasDepthGauge = checkInventoryForItem(inventory, y, "caverns_and_chasms:depth_gauge");
                    }
                } else {
                    ClockOverlay.hasBarometer = ClockOverlay.hasClock;
                }
            }
        } else {
            CompassOverlay.hasCompass = false;
            CompassOverlay.hasDepthGauge = false;
        }
    }

    public static boolean checkInventoryForItem(Inventory inventory, Item item, String name) {
        if (inventory.contains(item.getDefaultInstance())) {
            return true;
        }
        if (ModConfig.get().overlay_searchContainers && hasContainer(inventory)) {
            for (ItemStack stack : inventory.items) {
                if (stack.getTag() != null) {
                    var items = stack.getTag().get("Items");
                    if (items != null) {
                        if (items.toString().contains(name)) {
                            return true;
                        }
                    }
                    var be = stack.getTag().get("BlockEntityTag");
                    if (be != null) {
                        if (be.toString().contains(name)) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    private static boolean hasContainer(Inventory inventory) {
        if (inventory.contains(Items.BUNDLE.getDefaultInstance()))
            return true;
        else if (inventory.contains(ModRegistry.SACK.get().asItem().getDefaultInstance()))
            return true;
        else if (inventory.contains(ModRegistry.SAFE.get().asItem().getDefaultInstance()))
            return true;
        else if (inventory.contains(Items.SHULKER_BOX.getDefaultInstance()))
            return true;
        return false;
    }

    @SubscribeEvent
    public static void pickup(PlayerEvent.ItemPickupEvent event) {
        checkInventoryForItems(event.getEntity());
    }

    @SubscribeEvent
    public static void join(PlayerEvent.PlayerLoggedInEvent event) {
        checkInventoryForItems(event.getEntity());
    }

    @SubscribeEvent
    public static void toss(ItemTossEvent event) {
        checkInventoryForItems(event.getPlayer());
    }

    @SubscribeEvent
    public static void closeInventory(PlayerContainerEvent.Close event) {
        checkInventoryForItems(event.getEntity());
    }

    public static int getPlacement(int windowWidth, int fontWidth) {
        if (ModConfig.get().overlay_leftalign) {
            return 9;
        } else {
            return windowWidth-2-fontWidth;
        }
    }

    public static int getEndCapPlacement(int windowWidth, int fontWidth) {
        if (ModConfig.get().overlay_leftalign) {
            return fontWidth+8;
        } else {
            return windowWidth-4;
        }
    }
}
