package cc.cassian.raspberry;

import cc.cassian.raspberry.compat.CavernsAndChasmsCompat;
import cc.cassian.raspberry.compat.MapAtlasesCompat;
import cc.cassian.raspberry.compat.SpelunkeryCompat;
import cc.cassian.raspberry.config.ModConfig;
import com.mojang.blaze3d.systems.RenderSystem;
import net.mehvahdjukaar.supplementaries.configs.ClientConfigs;
import net.mehvahdjukaar.supplementaries.reg.ModRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.client.event.CustomizeGuiOverlayEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

import static cc.cassian.raspberry.CompassOverlay.checkInventoryForItem;

public class ClockOverlay {
    public static boolean hasClock = false;
    public static boolean hasBarometer = false;

    public static void checkInventoryForItems(Player player) {
        var clock = Items.CLOCK;
        var barometer = Items.CLOCK;
        if (ModCompat.CAVERNS_AND_CHASMS)
            barometer = CavernsAndChasmsCompat.getBarometer();
        if (ModConfig.get().overlay_enable) {
            var inventory = player.getInventory();
            if (ModConfig.get().overlay_requireItemInHand) {
                var main = player.getMainHandItem();
                var offhand = player.getOffhandItem();
                hasClock = main.is(clock) || offhand.is(clock);
                hasBarometer = main.is(barometer) || offhand.is(barometer);
                if (ModCompat.CAVERNS_AND_CHASMS) {
                    hasBarometer = checkInventoryForItem(inventory, barometer, "caverns_and_chasms:barometer");
                }
                else {
                    hasBarometer = hasClock;
                }
            }
            else {
                hasClock = checkInventoryForItem(inventory, clock, "minecraft:clock");
                if (ModCompat.CAVERNS_AND_CHASMS) {
                    hasBarometer = checkInventoryForItem(inventory, barometer, "caverns_and_chasms:barometer");
                }
                else {
                    hasBarometer = hasClock;
                }
            }
        }
    }

    @SubscribeEvent
    public static void renderGameOverlayEvent(CustomizeGuiOverlayEvent.DebugText event) {
        if (!hasBarometer && !hasClock)
            return;
        var mc = Minecraft.getInstance();

        var time = getTime(mc.level.getDayTime());
        if (time.length() == 4) {
            time = " " + time;
        }

        int offset = 3;
        int top = ModConfig.get().overlay_position_vertical;
        int textureSize = 256;
        int fontWidth = mc.font.width(time);


        int textureOffset = 9;  // only clock
        int tooltipSize = 14;  // only clock


        int windowWidth = mc.getWindow().getGuiScaledWidth();
        int placement = windowWidth-2-fontWidth;
        var poseStack = event.getPoseStack();
        RenderSystem.setShaderTexture(0, RaspberryMod.locate("textures/gui/tooltip.png"));
        if (hasClock) {
            GuiComponent.blit(poseStack,
                    placement-offset-4, top-23,
                    0, 0,
                    textureOffset, fontWidth+offset+4, tooltipSize,
                    textureSize, textureSize);
            GuiComponent.blit(poseStack,
                    windowWidth-4, top-23,
                    0, 197,
                    textureOffset, offset, tooltipSize,
                    textureSize, textureSize);
            GuiComponent.drawString(poseStack, mc.font, time, placement-offset, top-20, 14737632);
        }
    }

    // CODE COPY - Supplementaries ClockBlock
    public static String getTime(float dayTime) {
        int time = (int)(dayTime + 6000L) % 24000;
        int m = (int)((float)time % 1000.0F / 1000.0F * 60.0F);
        int h = time / 1000;
        String a = "";
        if (!(Boolean) ClientConfigs.Blocks.CLOCK_24H.get()) {
            a = time < 12000 ? " AM" : " PM";
            h %= 12;
            if (h == 0) {
                h = 12;
            }
        }
        return (h + ":" + (m < 10 ? "0" : "") + m + a);
    }
}
