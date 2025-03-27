package cc.cassian.raspberry;

import cc.cassian.raspberry.compat.MapAtlasesCompat;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.client.event.CustomizeGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import pepjebs.mapatlases.MapAtlasesMod;
import pepjebs.mapatlases.client.MapAtlasesClient;
import pepjebs.mapatlases.config.MapAtlasesClientConfig;
import vazkii.quark.base.module.config.Config;

import java.util.ArrayList;

public class CompassTracker {
    public static boolean hasCompass = false;
    public static boolean hasDepthGauge = true;


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
            coords.add(String.format("Z: %d", z));
        }
        if (hasDepthGauge) {
            coords.add(1, String.format("Y: %d", y));
        }

        if (MapAtlasesClientConfig.miniMapAnchoring.get().isUp && !MapAtlasesClientConfig.miniMapAnchoring.get().isLeft)
            top = 90;
        int placement = windowWidth-2-mc.font.width(coords.get(0));
        var poseStack = event.getPoseStack();
        RenderSystem.setShaderTexture(0, RaspberryMod.locate("textures/gui/tooltip.png"));
        GuiComponent.blit(poseStack,
                placement-offset-4, top-3,
                0, 0,
                2, mc.font.width(coords.get(0))+offset+4, tooltipSize,
                textureSize, textureSize);
        GuiComponent.blit(poseStack,
                windowWidth-4, top-3,
                0, 197,
                2, offset, tooltipSize,
                textureSize, textureSize);
        for (String text : coords) {
            GuiComponent.drawString(poseStack, mc.font, text, placement-offset, top, 14737632);
            top += mc.font.lineHeight;
        }
    }
}
