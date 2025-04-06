package cc.cassian.raspberry.overlay;

import cc.cassian.raspberry.RaspberryMod;
import cc.cassian.raspberry.config.ModConfig;
import com.mojang.blaze3d.systems.RenderSystem;
import net.mehvahdjukaar.supplementaries.configs.ClientConfigs;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraftforge.client.event.CustomizeGuiOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ClockOverlay {
    public static boolean hasClock = false;
    public static boolean hasBarometer = false;

    @SubscribeEvent
    public static void renderGameOverlayEvent(CustomizeGuiOverlayEvent.DebugText event) {
        if (!hasBarometer && !hasClock)
            return;
        if (!ModConfig.get().overlay_clock_enable)
            return;
        var mc = Minecraft.getInstance();

        var time = getTime(mc.level.getDayTime());
        if (time.length() == 4) {
            time = " " + time;
        }

        int xOffset = 3;
        int yPlacement = ModConfig.get().overlay_position_clock_vertical;
        int fontWidth = mc.font.width(time);

        if (mc.player == null) return;
        if (OverlayHelpers.playerHasPotions(mc.player)) {
            yPlacement = yPlacement + 16;
        }

        int windowWidth = mc.getWindow().getGuiScaledWidth();
        int xPlacement = OverlayHelpers.getPlacement(windowWidth, fontWidth);
        var poseStack = event.getPoseStack();
        RenderSystem.setShaderTexture(0, RaspberryMod.locate("textures/gui/tooltip.png"));
        if (hasClock) {
            OverlayHelpers.renderBackground(poseStack, windowWidth, fontWidth, xPlacement, xOffset, yPlacement, 7, 16);
            // render text
            GuiComponent.drawString(poseStack, mc.font, time, xPlacement-xOffset, yPlacement, 14737632);
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
