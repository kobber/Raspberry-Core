package cc.cassian.raspberry.overlay;

import cc.cassian.raspberry.RaspberryMod;
import cc.cassian.raspberry.config.ModConfig;
import com.mojang.blaze3d.systems.RenderSystem;
import net.mehvahdjukaar.supplementaries.configs.ClientConfigs;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
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
        // The amount of offset needed to display the barometer icons, if visible.
        int iconOffset = 0;
        int textureOffset = 7;
        int tooltipSize = 16;
        int yPlacement = ModConfig.get().overlay_position_clock_vertical;
        int textYPlacement = yPlacement;
        if (hasBarometer) {
            iconOffset = 20;
            textureOffset = 27;
            tooltipSize = 23;
            textYPlacement += 3;
        }

        int fontWidth = mc.font.width(time)+iconOffset;

        if (mc.player == null) return;
        if (OverlayHelpers.playerHasPotions(mc.player)) {
            yPlacement = yPlacement + 16;
        }

        int windowWidth = mc.getWindow().getGuiScaledWidth();
        int xPlacement = OverlayHelpers.getPlacement(windowWidth, fontWidth);
        var poseStack = event.getPoseStack();
        RenderSystem.setShaderTexture(0, OverlayHelpers.TEXTURE);
        if (hasClock) {
            OverlayHelpers.renderBackground(poseStack, windowWidth, fontWidth, xPlacement, xOffset, yPlacement, textureOffset, tooltipSize);
            // render text
            GuiComponent.drawString(poseStack, mc.font, time, xPlacement-xOffset+iconOffset, textYPlacement, 14737632);
        }
        if (hasBarometer) {
            var spriteOffset = getWeather(mc.player);
            RenderSystem.setShaderTexture(0, OverlayHelpers.TEXTURE);
            GuiComponent.blit(poseStack,
                    xPlacement-xOffset-2, yPlacement,
                    0, spriteOffset,
                    95, 16, 16,
                    OverlayHelpers.textureSize, OverlayHelpers.textureSize);
        }
    }

    public static int getWeather(Player player) {
        var level = player.level;
        var biome = level.getBiome(player.blockPosition()).get();
        if (!level.dimensionType().natural()) return 124; // Netherlike
        else if (level.getLevelData().isThundering()) {
            if (biome.coldEnoughToSnow(player.blockPosition())) return 96; // Snowing
            if (biome.isHumid()) return 104; // Sandstorming
            return 80; // Thundering
        } else if (level.isRaining()) {
            if (biome.coldEnoughToSnow(player.blockPosition())) return 96; // Snowing
            if (biome.isHumid()) return 104; // Sandstorming
            return 60; // Raining
        }
        return 0; // SUNNY
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
