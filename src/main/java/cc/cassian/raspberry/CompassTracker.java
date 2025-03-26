package cc.cassian.raspberry;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class CompassTracker {
    public static boolean hasCompass = false;

    @SubscribeEvent
    public static void pickup(PlayerEvent.ItemPickupEvent event) {
        checkInventoryForCompasses(event.getEntity().getInventory());
    }

    @SubscribeEvent
    public static void join(PlayerEvent.PlayerLoggedInEvent event) {
        checkInventoryForCompasses(event.getEntity().getInventory());
    }

    @SubscribeEvent
    public static void toss(ItemTossEvent event) {
        checkInventoryForCompasses(event.getPlayer().getInventory());
    }

    @SubscribeEvent
    public static void closeInventory(PlayerContainerEvent.Close event) {
        checkInventoryForCompasses(event.getEntity().getInventory());
    }

    public static void checkInventoryForCompasses(Inventory inventory) {
        if (inventory.contains(Items.COMPASS.getDefaultInstance())) {
            hasCompass = true;
            return;
        }
        if (inventory.contains(Items.BUNDLE.getDefaultInstance())) {
            for (ItemStack item : inventory.items) {
                if (item.getTag() != null) {
                    var items = item.getTag().get("Items");
                    if (items != null) {
                        if (items.toString().contains("minecraft:compass")) {
                            hasCompass = true;
                            return;
                        }
                    }
                }
            }
        }
        hasCompass = false;
    }
}
