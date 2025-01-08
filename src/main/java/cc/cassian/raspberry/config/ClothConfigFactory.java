package cc.cassian.raspberry.config;


import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import static cc.cassian.raspberry.ModHelpers.*;

public class ClothConfigFactory {

    private static final ModConfig DEFAULT_VALUES = new ModConfig();

    public static Screen create(Screen parent) {
        final var builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Component.translatable("config.raspberry.title"));

        final var entryBuilder = builder.entryBuilder();

        for (var field : ModConfig.class.getFields()) {
            ConfigCategory category = builder.getOrCreateCategory(Component.translatable("config.raspberry.title"));

            if (field.getType() == boolean.class) {
                category.addEntry(entryBuilder.startBooleanToggle(fieldName(field), fieldGet(ModConfig.get(), field))
                        .setSaveConsumer(fieldSetter(ModConfig.get(), field))
                        .setDefaultValue((boolean) fieldGet(DEFAULT_VALUES, field)).build());

            }
            else if (field.getType() == String.class) {
                category.addEntry(entryBuilder.startStrField(fieldName(field), fieldGet(ModConfig.get(), field))
                        .setSaveConsumer(fieldSetter(ModConfig.get(), field))
                        .setDefaultValue((String) fieldGet(DEFAULT_VALUES, field)).build());
            }
            else if (field.getType() == int.class) {
                category.addEntry(entryBuilder.startIntField(fieldName(field), fieldGet(ModConfig.get(), field))
                        .setSaveConsumer(fieldSetter(ModConfig.get(), field))
                        .setDefaultValue((int) fieldGet(DEFAULT_VALUES, field)).build());
            }
        }
        builder.setSavingRunnable(ModConfig::save);
        return builder.build();
    }
}