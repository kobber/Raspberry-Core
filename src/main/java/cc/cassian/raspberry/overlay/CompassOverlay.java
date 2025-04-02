package cc.cassian.raspberry.overlay;

import cc.cassian.raspberry.ModCompat;
import cc.cassian.raspberry.ModHelpers;
import cc.cassian.raspberry.RaspberryMod;
import cc.cassian.raspberry.compat.MapAtlasesCompat;
import cc.cassian.raspberry.config.ModConfig;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.core.BlockPos;
import net.minecraftforge.client.event.CustomizeGuiOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

public class CompassOverlay {
    public static boolean hasCompass = false;
    public static boolean hasDepthGauge = false;

    @SubscribeEvent
    public static void renderGameOverlayEvent(CustomizeGuiOverlayEvent.DebugText event) {
        if (!hasCompass && !hasDepthGauge)
            return;
        if (!ModConfig.get().overlay_compass_enable)
            return;
        if (ModCompat.MAP_ATLASES && MapAtlasesCompat.showingCoords())
            return;
        var mc = Minecraft.getInstance();
        if (mc.options.renderDebug && !mc.options.reducedDebugInfo().get())
            return;

        ArrayList<String> coords = new ArrayList<>();

        BlockPos pos;
        if (mc.player != null) pos = mc.player.blockPosition();
        else return;

        String x = String.format("%d", pos.getX());
        String y = String.format("%d", pos.getY());
        String z = String.format("%d", pos.getZ());
        var width = Integer.max(x.length(), z.length());
        width = Integer.max(width, y.length());
        x = StringUtils.leftPad(x, width);
        y = StringUtils.leftPad(y, width);
        z = StringUtils.leftPad(z, width);
        int offset = 3;
        int top = ModConfig.get().overlay_position_compass_vertical;
        int textureSize = 256;
        int fontWidth = mc.font.width(StringUtils.repeat("a", width+2));

        if (hasCompass) {
            coords.add("§%sX:§f %s".formatted(ModHelpers.getColour(ModConfig.get().overlay_x_colour), x));
            if (hasDepthGauge) {
                coords.add("§%sY:§f %s".formatted(ModHelpers.getColour(ModConfig.get().overlay_y_colour), y));
            }
            coords.add("§%sZ:§f %s".formatted(ModHelpers.getColour(ModConfig.get().overlay_z_colour), z));
        }
        else if (hasDepthGauge) {
            coords.add("§%sY:§f %s".formatted(ModHelpers.getColour(ModConfig.get().overlay_y_colour), y));
        }
        if (!ClockOverlay.hasClock || !ModConfig.get().overlay_clock_enable) {
            top = top - 15;
        }

        int textureOffset = 9;  // only depth gauge
        int tooltipSize = 14;  // only depth gauge
        if (hasCompass & hasDepthGauge) { // depth gauge and compass
            textureOffset = 51;
            tooltipSize = 33;
        }
        else if (hasCompass) { // only compass
            textureOffset = 27;
            tooltipSize = 23;
        }

        int windowWidth = mc.getWindow().getGuiScaledWidth();
        int placement = OverlayHelpers.getPlacement(windowWidth, fontWidth);
        int endCapPlacement = OverlayHelpers.getEndCapPlacement(windowWidth, fontWidth);
        int endCapOffset = 197;
        var poseStack = event.getPoseStack();
        RenderSystem.setShaderTexture(0, RaspberryMod.locate("textures/gui/tooltip.png"));
        // render background
        GuiComponent.blit(poseStack,
                placement-offset-4, top-3,
                0, 0,
                textureOffset, fontWidth+offset+4, tooltipSize,
                textureSize, textureSize);
        // render endcap
        GuiComponent.blit(poseStack,
                endCapPlacement, top-3,
                0, endCapOffset,
                textureOffset, 3, tooltipSize,
                textureSize, textureSize);
        // render text
        for (String text : coords) {
            GuiComponent.drawString(poseStack, mc.font, text, placement-offset, top, 14737632);
            top += mc.font.lineHeight;
        }
    }
}