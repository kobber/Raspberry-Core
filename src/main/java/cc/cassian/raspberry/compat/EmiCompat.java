package cc.cassian.raspberry.compat;

import cc.cassian.raspberry.RaspberryMod;
import cofh.ensorcellation.init.EnsorcEnchantments;
import com.brokenkeyboard.usefulspyglass.UsefulSpyglass;
import com.simibubi.create.AllItems;
import com.teamabnormals.allurement.core.registry.AllurementEnchantments;
import de.cadentem.additional_enchantments.data.AEEffectTags;
import de.cadentem.additional_enchantments.registry.AEEnchantments;
import dev.emi.emi.EmiUtil;
import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.recipe.VanillaEmiRecipeCategories;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.common.Tags;

import java.util.List;

@EmiEntrypoint
public class EmiCompat implements EmiPlugin {
    public static class EmiSmithingRecipe implements EmiRecipe {
        private final EmiIngredient input1;
        private final EmiStack input2;
        private final EmiStack output;
        private final ResourceLocation id;
        private final int uniq;

        public EmiSmithingRecipe(EmiIngredient input1, EmiStack input2, EmiStack output, ResourceLocation id) {
            this.uniq = EmiUtil.RANDOM.nextInt();
            this.input1 = input1;
            this.input2 = input2;
            this.output = output;
            this.id = id;
        }

        public EmiRecipeCategory getCategory() {
            return VanillaEmiRecipeCategories.SMITHING;
        }

        public ResourceLocation getId() {
            return this.id;
        }

        public List<EmiIngredient> getInputs() {
            return List.of(this.input1, this.input2);
        }

        public List<EmiStack> getOutputs() {
            return List.of(this.output);
        }

        public boolean supportsRecipeTree() {
            return false;
        }

        public int getDisplayWidth() {
            return 125;
        }

        public int getDisplayHeight() {
            return 18;
        }

        public void addWidgets(WidgetHolder widgets) {
            widgets.addTexture(EmiTexture.PLUS, 27, 3);
            widgets.addTexture(EmiTexture.EMPTY_ARROW, 75, 1);
            widgets.addGeneratedSlot((r) -> this.input1, this.uniq, 0, 0);
            widgets.addSlot(this.input2, 49, 0);
            widgets.addGeneratedSlot((r) -> this.output, this.uniq, 107, 0).recipeContext(this);
        }
    }

    public static ResourceLocation tablet(String id) {
        return new ResourceLocation("kubejs", id+"_tablet");
    }

    public static Item get(String id) {
        return Registry.ITEM.get(tablet(id));
    }

    private static Item getLeggings() {
        return Items.IRON_LEGGINGS;
    }

    private static Item getBoots() {
        return Items.IRON_BOOTS;
    }

    private static Item getSword() {
        return Items.IRON_SWORD;
    }

    private static Item getArmour() {
        return Items.IRON_CHESTPLATE;
    }

    private static Item getTools() {
        return Items.IRON_PICKAXE;
    }

    public static void addRecipe(EmiRegistry emiRegistry, Item item, TagKey<Item> tag, Enchantment enchantment, Item tablet, String id) {
        var enchantedGear = new ItemStack(item);
        enchantedGear.enchant(enchantment, 1);
        emiRegistry.addRecipe(new EmiSmithingRecipe(
                EmiIngredient.of(tag),
                EmiStack.of(new ItemStack(tablet)),
                EmiStack.of(enchantedGear),
                RaspberryMod.locate("/smithing/"+id)
        ));
    }

    public static void addRecipe(EmiRegistry emiRegistry, Item item, Item tag, Enchantment enchantment, Item tablet, String id) {
        var enchantedGear = new ItemStack(item);
        enchantedGear.enchant(enchantment, 1);
        emiRegistry.addRecipe(new EmiSmithingRecipe(
                EmiIngredient.of(Ingredient.of(tag)),
                EmiStack.of(new ItemStack(tablet)),
                EmiStack.of(enchantedGear),
                RaspberryMod.locate("/smithing/"+id)
        ));
    }

    @Override
    public void register(EmiRegistry emiRegistry) {
        final var EVERLASTING = get("everlasting");
        final var AQUATIC = get("aquatic");
        final var BEASTLY = get("beastly");
        final var CYCLIC = get("cyclic");
        final var FLINGING = get("flinging");
        final var ENDURING = get("enduring");
        final var FROST = get("frost");
        final var HALLOWED = get("hallowed");
        final var HAUNTED = get("haunted");
        final var HEAVY = get("heavy");
        final var INFESTED = get("infested");
        final var OTHERWORLDLY = get("otherworldly");
        final var PIERCING = get("piercing");
        final var SILENT = get("silent");
        final var SWIFT = get("swift");

       // EVERLASTING - UNRBEAKING
        addRecipe(emiRegistry, // ARMOUR
                getArmour(), Tags.Items.ARMORS, Enchantments.UNBREAKING, EVERLASTING, "everlasting_armour");
        // EVERLASTING - UNBREAKING
        addRecipe(emiRegistry, // TOOLS
                getTools(), Tags.Items.TOOLS, Enchantments.UNBREAKING, EVERLASTING, "everlasting_tools");

        // AQUATIC - RESPIRATION
        addRecipe(emiRegistry, // HELMETS
                Items.IRON_HELMET, Items.IRON_HELMET,
                Enchantments.RESPIRATION, AQUATIC,
                "respiration_helmet");
        addRecipe(emiRegistry, // BACKTANKS
                AllItems.COPPER_BACKTANK.get(), AllItems.COPPER_BACKTANK.get(),
                Enchantments.RESPIRATION, AQUATIC,
                "respiration_backtank");
        // AQUATIC - DEPTH STRIDER
        addRecipe(emiRegistry,
                getBoots(), Tags.Items.ARMORS_BOOTS,
                Enchantments.DEPTH_STRIDER, AQUATIC,
                "depth_strider_boots");

        // BEASTLY - CAVALIER
        addRecipe(emiRegistry,
                getSword(), Tags.Items.TOOLS_SWORDS,
                EnsorcEnchantments.CAVALIER.get(), BEASTLY,
                "cavalier_sword");
        // BEASTLY - MULTI-LEAP
//        addRecipe(emiRegistry,
//                getLeggings(), Tags.Items.ARMORS_LEGGINGS,
//                MINING_MASTER_KNIGHT, BEASTLY,
//                "beastly_legs");

        // CYCLIC - SWEEPING EDGE
        addRecipe(emiRegistry,
                getSword(), Tags.Items.TOOLS_SWORDS,
                Enchantments.SWEEPING_EDGE, HALLOWED,
                "sweeping_edge");
        // CYCLIC - RIPTIDE
        addRecipe(emiRegistry,
                Items.TRIDENT, Tags.Items.TOOLS_TRIDENTS,
                Enchantments.RIPTIDE, HALLOWED,
                "riptide");
        // CYCLIC - VENGEANCE

        // ENDURING - VITALITY
        addRecipe(emiRegistry,
                getArmour(), Tags.Items.ARMORS,
                EnsorcEnchantments.VITALITY.get(), FROST,
                "vitality");

        // FLINGING - LAUNCH
        addRecipe(emiRegistry,
                getSword(), Tags.Items.TOOLS_SWORDS,
                AllurementEnchantments.LAUNCH.get(), FLINGING,
                "launch");
        // FLINGING - VOLLEY
        addRecipe(emiRegistry,
                Items.BOW, Tags.Items.TOOLS_BOWS,
                EnsorcEnchantments.VOLLEY.get(), FLINGING,
                "volley_bow");
        addRecipe(emiRegistry,
                Items.CROSSBOW, Tags.Items.TOOLS_CROSSBOWS,
                EnsorcEnchantments.VOLLEY.get(), FLINGING,
                "volley_crossbow");
        // FROST - FROST WALKER
        addRecipe(emiRegistry, // BOOTS
                getBoots(), Tags.Items.ARMORS_BOOTS,
                Enchantments.FROST_WALKER, FROST,
                "frost_walker_boots");
        addRecipe(emiRegistry, // HORSE ARMOUR
                Items.DIAMOND_HORSE_ARMOR, Items.DIAMOND_HORSE_ARMOR,
                Enchantments.FROST_WALKER, FROST,
                "frost_walker_horse_armour");

        // HALLOWED - SMITE
        addRecipe(emiRegistry,
                getSword(), Tags.Items.TOOLS_SWORDS,
                Enchantments.SMITE, HALLOWED,
                "smite");
        // HALLOWED - CHANNELING
        addRecipe(emiRegistry,
                Items.TRIDENT, Tags.Items.TOOLS_TRIDENTS,
                Enchantments.CHANNELING, HALLOWED,
                "channeling");
        // HALLOWED - SILVER EYE
        addRecipe(emiRegistry,
                Items.SPYGLASS, Items.SPYGLASS,
                UsefulSpyglass.MARKING.get(), HALLOWED,
                "silver_eye");

        // HAUNTED - SOUL CHASER
        addRecipe(emiRegistry,
                Items.BOW, Tags.Items.TOOLS_BOWS,
                AEEnchantments.HOMING.get(), HAUNTED,
                "soul_chaser_bow");
        addRecipe(emiRegistry,
                Items.CROSSBOW, Tags.Items.TOOLS_CROSSBOWS,
                AEEnchantments.HOMING.get(), HAUNTED,
                "soul_chaser_crossbow");
        // HAUNTED - SOUL SPEED
        addRecipe(emiRegistry,
                getBoots(), Tags.Items.ARMORS_BOOTS,
                Enchantments.SOUL_SPEED, HAUNTED,
                "soul_speed");

        // HEAVY - GUARD BREAK
        // HEAVY - BRACEWALK
        // HEAVY - SHOCKWAVE

        // INFESTED - BANE OF ARTHROPODS
        addRecipe(emiRegistry,
                getSword(), Tags.Items.TOOLS_SWORDS,
                Enchantments.BANE_OF_ARTHROPODS, INFESTED,
                "bane");
        // INFESTED - SPREAD OF AILMENTS

        // OTHERWORLDLY - STASIS
        // OTHERWORLDLY - DISPLACEMENT

        // PIERCING - TRUESHOT
        addRecipe(emiRegistry,
                Items.BOW, Tags.Items.TOOLS_BOWS,
                EnsorcEnchantments.TRUESHOT.get(), PIERCING,
                "trueshot_bow");
        addRecipe(emiRegistry,
                Items.CROSSBOW, Tags.Items.TOOLS_CROSSBOWS,
                EnsorcEnchantments.TRUESHOT.get(), PIERCING,
                "trueshot_crossbow");
        // PIERCING - IMPALING
        addRecipe(emiRegistry,
                Items.TRIDENT, Tags.Items.TOOLS_TRIDENTS,
                Enchantments.IMPALING, PIERCING,
                "impaling");

        // PULLING - REELING
        // PULLING - REACH

        // SILENT - BACKSTABBING
        // SILENT - SWIFT SNEAK
        addRecipe(emiRegistry,
                getBoots(), Tags.Items.ARMORS_BOOTS,
                Enchantments.SWIFT_SNEAK, SILENT,
                "swift_sneak");

        // SWIFT - SWIFTSTRIKE
        // SWIFT - QUICK DRAW

    }


}
