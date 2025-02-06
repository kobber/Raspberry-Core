package cc.cassian.raspberry.registry;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;

import static cc.cassian.raspberry.RaspberryMod.MOD_ID;

public class RaspberryTags {
    public static final TagKey<Item> DISABLED = createItemTag("disabled");
    public static final TagKey<Item> BAIT = createItemTag("bait");
    public static final TagKey<Item> BAD_BAIT = createItemTag("bad_bait");
    public static final TagKey<Item> MID_BAIT = createItemTag("mid_bait");
    public static final TagKey<Item> GOOD_BAIT = createItemTag("good_bait");
    public static final TagKey<Item> HAS_KINETIC_DAMAGE = createItemTag( "has_kinetic_damage");
    public static final TagKey<Item> WORM_SEEKER_ITEMS = createItemTag( "worm_seeker_items");
    public static final TagKey<EntityType<?>> WORM_SEEKERS = createEntityTypeTag( "worm_seekers");


    public static TagKey<Item> createItemTag(String id) {
        return TagKey.create(ForgeRegistries.ITEMS.getRegistryKey(), new ResourceLocation(MOD_ID, id));
    }

    public static TagKey<EntityType<?>> createEntityTypeTag(String id) {
        return TagKey.create(ForgeRegistries.ENTITY_TYPES.getRegistryKey(), new ResourceLocation(MOD_ID, id));
    }
}
