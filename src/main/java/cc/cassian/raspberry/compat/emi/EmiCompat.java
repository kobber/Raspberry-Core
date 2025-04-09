package cc.cassian.raspberry.compat.emi;

import cc.cassian.raspberry.ModCompat;
import cc.cassian.raspberry.RaspberryMod;
import cofh.ensorcellation.init.EnsorcEnchantments;
import com.brokenkeyboard.usefulspyglass.UsefulSpyglass;
import com.github.alexthe668.domesticationinnovation.server.enchantment.DIEnchantmentRegistry;
import com.github.alexthe668.domesticationinnovation.server.item.DIItemRegistry;
import com.simibubi.create.AllItems;
import com.teamabnormals.allurement.core.registry.AllurementEnchantments;
import de.cadentem.additional_enchantments.registry.AEEnchantments;
import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import net.mehvahdjukaar.supplementaries.reg.ModRegistry;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.common.Tags;
import net.minecraftforge.registries.ForgeRegistries;
import org.infernalstudios.miningmaster.init.MMEnchantments;
import vectorwing.farmersdelight.common.registry.ModEnchantments;
import vectorwing.farmersdelight.common.registry.ModItems;
import vectorwing.farmersdelight.common.tag.ForgeTags;

@EmiEntrypoint
public class EmiCompat implements EmiPlugin {
    public static EmiRecipeCategory ANVIL = new EmiRecipeCategory(RaspberryMod.locate("anvil"), EmiStack.of(Items.ANVIL));

    @Override
    public void register(EmiRegistry emiRegistry) {
        if (ModCompat.CREATE && ModCompat.DOMESTICATION_INNOVATION && ModCompat.ENSORCELLATION && ModCompat.SUPPLEMENTARIES && ModCompat.ALLUREMENT) {
            EmiSmithingRecipe.addEnchantments(emiRegistry);
        }
        if (ModCompat.QUARK) {
            emiRegistry.addWorkstation(EmiCompat.ANVIL, EmiStack.of(Items.ANVIL));
            emiRegistry.addWorkstation(EmiCompat.ANVIL, EmiStack.of(Items.CHIPPED_ANVIL));
            emiRegistry.addWorkstation(EmiCompat.ANVIL, EmiStack.of(Items.DAMAGED_ANVIL));
            emiRegistry.addCategory(ANVIL);
            EmiEtchingRecipe.addRunes(emiRegistry);
        }
    }

}
