package cc.cassian.raspberry;

import cc.cassian.raspberry.compat.MapAtlasesCompat;
import com.mojang.blaze3d.systems.RenderSystem;
import com.ordana.spelunkery.reg.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.client.event.CustomizeGuiOverlayEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.ArrayList;

public class CompassTracker {
    public static boolean hasCompass = false;
    public static boolean hasDepthGauge = true;


    @SubscribeEvent
    public static void pickup(PlayerEvent.ItemPickupEvent event) {
        checkInventoryForItems(event.getEntity().getInventory());
    }

    @SubscribeEvent
    public static void join(PlayerEvent.PlayerLoggedInEvent event) {
        checkInventoryForItems(event.getEntity().getInventory());
    }

    @SubscribeEvent
    public static void toss(ItemTossEvent event) {
        checkInventoryForItems(event.getPlayer().getInventory());
    }

    @SubscribeEvent
    public static void closeInventory(PlayerContainerEvent.Close event) {
        checkInventoryForItems(event.getEntity().getInventory());
    }

    public static void checkInventoryForItems(Inventory inventory) {
        hasCompass = checkInventoryForItem(inventory, Items.COMPASS, "minecraft:compass");
        hasDepthGauge = checkInventoryForItem(inventory, ModItems.DEPTH_GAUGE.get(), "spelunkery:depth_gauge");
    }

    public static boolean checkInventoryForItem(Inventory inventory, Item item, String name) {
        if (inventory.contains(item.getDefaultInstance())) {
            return true;
        }
        if (inventory.contains(Items.BUNDLE.getDefaultInstance())) {
            for (ItemStack stack : inventory.items) {
                if (stack.getTag() != null) {
                    var items = stack.getTag().get("Items");
                    if (items != null) {
                        if (items.toString().contains(name)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }


    @SubscribeEvent
    public static void renderGameOverlayEvent(CustomizeGuiOverlayEvent.DebugText event) {
        var mc = Minecraft.getInstance();
        if (mc.options.renderDebug && !mc.options.reducedDebugInfo().get())
            return;

        if (ModCompat.MAP_ATLASES && MapAtlasesCompat.showingCoords())
            return;

        int windowWidth = mc.getWindow().getGuiScaledWidth();

        ArrayList<String> coords = new ArrayList<>();

        BlockPos pos = mc.player.blockPosition();
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();
        int offset = 3;
        int top = 2;
        int textureSize = 256;
        int tooltipSize = 22;

        if (hasCompass) {
            coords.add(String.format("X: %d", x));
            if (hasDepthGauge) {
                coords.add(String.format("Y: %d", y));
            }
            coords.add(String.format("Z: %d", z));
        }
        else if (hasDepthGauge) {
            coords.add(String.format("Y: %d", y));
        }

        int textureOffset = 2; // only depth gague
        if (hasCompass & hasDepthGauge) { // depth gauge and compass
            textureOffset = 51;
            tooltipSize = 33;
        }
        else if (hasCompass) { // only compass
            textureOffset = 27;
        }

        if (ModCompat.MAP_ATLASES && MapAtlasesCompat.isInCorner())
            top = 90;
        int placement = windowWidth-2-mc.font.width(coords.get(0));
        var poseStack = event.getPoseStack();
        RenderSystem.setShaderTexture(0, RaspberryMod.locate("textures/gui/tooltip.png"));
        GuiComponent.blit(poseStack,
                placement-offset-4, top-3,
                0, 0,
                textureOffset, mc.font.width(coords.get(0))+offset+4, tooltipSize,
                textureSize, textureSize);
        GuiComponent.blit(poseStack,
                windowWidth-4, top-3,
                0, 197,
                textureOffset, offset, tooltipSize,
                textureSize, textureSize);
        for (String text : coords) {
            GuiComponent.drawString(poseStack, mc.font, text, placement-offset, top, 14737632);
            top += mc.font.lineHeight;
        }
    }
}
