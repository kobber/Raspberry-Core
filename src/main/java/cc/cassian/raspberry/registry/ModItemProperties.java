package cc.cassian.raspberry.registry;

import cc.cassian.raspberry.RaspberryMod;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.renderer.item.ItemPropertyFunction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;

public class ModItemProperties {

    private static final ItemPropertyFunction FLOWER_GARLAND_EQUIPPED = (stack, level, entity, i) -> {
        if (entity == null) {
            return 0.0F;
        } else {
            return entity.getItemBySlot(EquipmentSlot.HEAD) == stack ? 1.0F : 0.0F;
        }
    };

    public static void register() {
        ItemProperties.register(RaspberryItems.CHEERY_WILDFLOWER_GARLAND.get(), new ResourceLocation(RaspberryMod.MOD_ID, "equipped"), FLOWER_GARLAND_EQUIPPED);
        ItemProperties.register(RaspberryItems.HOPEFUL_WILDFLOWER_GARLAND.get(), new ResourceLocation(RaspberryMod.MOD_ID, "equipped"), FLOWER_GARLAND_EQUIPPED);
        ItemProperties.register(RaspberryItems.PLAYFUL_WILDFLOWER_GARLAND.get(), new ResourceLocation(RaspberryMod.MOD_ID, "equipped"), FLOWER_GARLAND_EQUIPPED);
        ItemProperties.register(RaspberryItems.MOODY_WILDFLOWER_GARLAND.get(), new ResourceLocation(RaspberryMod.MOD_ID, "equipped"), FLOWER_GARLAND_EQUIPPED);
    }
}