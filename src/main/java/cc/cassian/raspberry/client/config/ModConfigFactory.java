package cc.cassian.raspberry.client.config;

import cc.cassian.raspberry.config.ClothConfigFactory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import org.jetbrains.annotations.NotNull;

public class ModConfigFactory {
    public static @NotNull Screen createScreen(@NotNull Minecraft arg, @NotNull Screen parent) {
        return ClothConfigFactory.create(parent);
    }
}
