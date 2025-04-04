package cc.cassian.raspberry.registry;

import cc.cassian.raspberry.ModCompat;
import cc.cassian.raspberry.blocks.FlowerBedBlock;
import cc.cassian.raspberry.blocks.RaspberryCakeBlock;
import cc.cassian.raspberry.blocks.RaspberryGravelBlock;
import cc.cassian.raspberry.compat.CopperBackportCompat;
import cc.cassian.raspberry.compat.EnvironmentalCompat;
import com.soytutta.mynethersdelight.common.registry.MNDItems;
import com.teamabnormals.abnormals_delight.core.registry.ADItems;
import net.mehvahdjukaar.supplementaries.common.block.blocks.RakedGravelBlock;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;
import oshi.util.tuples.Pair;
import plus.dragons.respiteful.entries.RespitefulItems;
import umpaz.farmersrespite.common.registry.FRItems;
import vectorwing.farmersdelight.FarmersDelight;
import vectorwing.farmersdelight.common.block.StoveBlock;
import vectorwing.farmersdelight.common.registry.ModBlocks;
import vectorwing.farmersdelight.common.registry.ModItems;

import java.util.ArrayList;
import java.util.function.Supplier;

import static cc.cassian.raspberry.RaspberryMod.MOD_ID;

public class RaspberryBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MOD_ID);

    public static Pair<RegistryObject<Block>, RegistryObject<BlockItem>>
            SILT_STOVE = registerBlock("silt_stove",
            ()-> new StoveBlock(BlockBehaviour.Properties.copy(ModBlocks.STOVE.get())), FarmersDelight.CREATIVE_TAB);

    public static Pair<RegistryObject<Block>, RegistryObject<BlockItem>>
            ASH_STOVE = registerBlock("ash_stove",
            ()-> new StoveBlock(BlockBehaviour.Properties.copy(ModBlocks.STOVE.get())), FarmersDelight.CREATIVE_TAB);

    public static Pair<RegistryObject<Block>, RegistryObject<BlockItem>>
            LEAD_GRATE = registerLeadGrate();

    public static Pair<RegistryObject<Block>, RegistryObject<BlockItem>>
            WORMY_DIRT = registerBlock("wormy_dirt",
            ()-> new Block(getTruffleProperties()), CreativeModeTab.TAB_BUILDING_BLOCKS);

    public static Pair<RegistryObject<Block>, RegistryObject<BlockItem>>
            RAKED_BLACKSTONE_GRAVEL = registerBlock("raked_blackstone_gravel",
            ()-> new RakedGravelBlock(BlockBehaviour.Properties.copy(Blocks.GRAVEL)), CreativeModeTab.TAB_BUILDING_BLOCKS);

    public static Pair<RegistryObject<Block>, RegistryObject<BlockItem>>
            RAKED_DEEPSLATE_GRAVEL = registerBlock("raked_deepslate_gravel",
            ()-> new RakedGravelBlock(BlockBehaviour.Properties.copy(Blocks.GRAVEL)), CreativeModeTab.TAB_BUILDING_BLOCKS);

    public static Pair<RegistryObject<Block>, RegistryObject<BlockItem>>
            BLACKSTONE_GRAVEL = registerBlock("blackstone_gravel",
            ()-> new RaspberryGravelBlock(BlockBehaviour.Properties.copy(Blocks.GRAVEL), 986379), CreativeModeTab.TAB_BUILDING_BLOCKS);

    public static Pair<RegistryObject<Block>, RegistryObject<BlockItem>>
            DEEPSLATE_GRAVEL = registerBlock("deepslate_gravel",
            ()-> new RaspberryGravelBlock(BlockBehaviour.Properties.copy(Blocks.GRAVEL), 2039584), CreativeModeTab.TAB_BUILDING_BLOCKS);

    public static Pair<RegistryObject<Block>, RegistryObject<BlockItem>>
            CHEERFUL_WILDFLOWERS = registerBlock("cheery_wildflowers",
            ()-> new FlowerBedBlock(flowerBedProperties()), CreativeModeTab.TAB_DECORATIONS);

    public static Pair<RegistryObject<Block>, RegistryObject<BlockItem>>
            PINK_PETALS = registerBlock("pink_petals",
            ()-> new FlowerBedBlock(flowerBedProperties()), CreativeModeTab.TAB_DECORATIONS);

    public static Pair<RegistryObject<Block>, RegistryObject<BlockItem>>
            CLOVERS = registerBlock("clovers",
            ()-> new FlowerBedBlock(flowerBedProperties()), CreativeModeTab.TAB_DECORATIONS);

    public static Pair<RegistryObject<Block>, RegistryObject<BlockItem>>
            MOODY_WILDFLOWERS = registerBlock("moody_wildflowers",
            ()-> new FlowerBedBlock(flowerBedProperties()), CreativeModeTab.TAB_DECORATIONS);

    public static Pair<RegistryObject<Block>, RegistryObject<BlockItem>>
            CAKE = registerBlock("cake",
            ()-> new RaspberryCakeBlock(BlockBehaviour.Properties.copy(Blocks.CAKE), ModItems.CAKE_SLICE), CreativeModeTab.TAB_DECORATIONS);

    public static Pair<RegistryObject<Block>, RegistryObject<BlockItem>>
            CHOCOLATE_CAKE = registerBlock("chocolate_cake",
            ()-> new RaspberryCakeBlock(BlockBehaviour.Properties.copy(Blocks.CAKE), ADItems.CHOCOLATE_CAKE_SLICE), CreativeModeTab.TAB_DECORATIONS);

    public static Pair<RegistryObject<Block>, RegistryObject<BlockItem>>
            STRAWBERRY_CAKE = registerBlock("strawberry_cake",
            ()-> new RaspberryCakeBlock(BlockBehaviour.Properties.copy(Blocks.CAKE), ADItems.STRAWBERRY_CAKE_SLICE), CreativeModeTab.TAB_DECORATIONS);

    public static Pair<RegistryObject<Block>, RegistryObject<BlockItem>>
            BANANA_CAKE = registerBlock("banana_cake",
            ()-> new RaspberryCakeBlock(BlockBehaviour.Properties.copy(Blocks.CAKE), ADItems.BANANA_CAKE_SLICE), CreativeModeTab.TAB_DECORATIONS);

    public static Pair<RegistryObject<Block>, RegistryObject<BlockItem>>
            MINT_CAKE = registerBlock("mint_cake",
            ()-> new RaspberryCakeBlock(BlockBehaviour.Properties.copy(Blocks.CAKE), ADItems.MINT_CAKE_SLICE), CreativeModeTab.TAB_DECORATIONS);

    public static Pair<RegistryObject<Block>, RegistryObject<BlockItem>>
            VANILLA_CAKE = registerBlock("vanilla_cake",
            ()-> new RaspberryCakeBlock(BlockBehaviour.Properties.copy(Blocks.CAKE), ADItems.VANILLA_CAKE_SLICE), CreativeModeTab.TAB_DECORATIONS);

    public static Pair<RegistryObject<Block>, RegistryObject<BlockItem>>
            CHERRY_CAKE = registerBlock("cherry_cake",
            ()-> new RaspberryCakeBlock(BlockBehaviour.Properties.copy(Blocks.CAKE), ADItems.ADZUKI_CAKE_SLICE), CreativeModeTab.TAB_DECORATIONS);

    public static Pair<RegistryObject<Block>, RegistryObject<BlockItem>>
            MAGMA_CAKE = registerBlock("magma_cake",
            ()-> new RaspberryCakeBlock(BlockBehaviour.Properties.copy(Blocks.CAKE), MNDItems.MAGMA_CAKE_SLICE), CreativeModeTab.TAB_DECORATIONS);

    public static Pair<RegistryObject<Block>, RegistryObject<BlockItem>>
            GREEN_TEA_CAKE = registerBlock("green_tea_cake",
            ()-> new RaspberryCakeBlock(BlockBehaviour.Properties.copy(Blocks.CAKE), RespitefulItems.GREEN_TEA_CAKE_SLICE), CreativeModeTab.TAB_DECORATIONS);

    public static Pair<RegistryObject<Block>, RegistryObject<BlockItem>>
            YELLOW_TEA_CAKE = registerBlock("yellow_tea_cake",
            ()-> new RaspberryCakeBlock(BlockBehaviour.Properties.copy(Blocks.CAKE), RespitefulItems.YELLOW_TEA_CAKE_SLICE), CreativeModeTab.TAB_DECORATIONS);

    public static Pair<RegistryObject<Block>, RegistryObject<BlockItem>>
            BLACK_TEA_CAKE = registerBlock("black_tea_cake",
            ()-> new RaspberryCakeBlock(BlockBehaviour.Properties.copy(Blocks.CAKE), RespitefulItems.BLACK_TEA_CAKE_SLICE), CreativeModeTab.TAB_DECORATIONS);

    public static Pair<RegistryObject<Block>, RegistryObject<BlockItem>>
            COFFEE_CAKE = registerBlock("coffee_cake",
            ()-> new RaspberryCakeBlock(BlockBehaviour.Properties.copy(Blocks.CAKE), FRItems.COFFEE_CAKE_SLICE), CreativeModeTab.TAB_DECORATIONS);

    private static BlockBehaviour.Properties flowerBedProperties() {
        return BlockBehaviour.Properties.of(Material.PLANT).noCollission().sound(SoundType.FLOWERING_AZALEA);
    }

    public static final ArrayList<Pair<RegistryObject<Block>, RegistryObject<BlockItem>>> FOLIAGE_BLOCKS = new ArrayList<Pair<RegistryObject<Block>, RegistryObject<BlockItem>>>();

    public static void register(IEventBus eventBus) {
        RaspberryBlocks.BLOCKS.register(eventBus);
        FOLIAGE_BLOCKS.add(CHEERFUL_WILDFLOWERS);
        FOLIAGE_BLOCKS.add(PINK_PETALS);
        FOLIAGE_BLOCKS.add(MOODY_WILDFLOWERS);
    }

    public static BlockBehaviour.Properties getTruffleProperties() {
        if (ModCompat.ENVIRONMENTAL)
            return EnvironmentalCompat.getTruffleProperties();
        else return BlockBehaviour.Properties.copy(Blocks.DIRT);
    }

    public static Pair<RegistryObject<Block>, RegistryObject<BlockItem>> registerLeadGrate() {
        BlockBehaviour.Properties properties = BlockBehaviour.Properties.of(Material.HEAVY_METAL).noOcclusion().strength(5.0F, 6.0F).requiresCorrectToolForDrops().sound(SoundType.METAL);
        if (ModCompat.COPPER_BACKPORT) {
            return CopperBackportCompat.registerGrateBlock(properties);
        }
        else return registerBlock("lead_grate",
                ()-> new Block(properties), CreativeModeTab.TAB_BUILDING_BLOCKS);
    }

    public static Pair<RegistryObject<Block>, RegistryObject<BlockItem>> registerBlock(String blockID, Supplier<Block> blockSupplier, CreativeModeTab tab) {
        final var block = BLOCKS.register(blockID, blockSupplier);
        final var item = RaspberryItems.ITEMS.register(blockID, () -> new BlockItem(block.get(), new Item.Properties().tab(tab)));
        return new Pair<>(block, item);
    }

    /**
	 * Get a block from the registry
	 */
    public static @NotNull Block getBlock(Pair<RegistryObject<Block>, RegistryObject<BlockItem>> block) {
        return block.getA().get();
    }

    /**
     * Get a blockitem from the registry
     */
    public static @NotNull Item getItem(Pair<RegistryObject<Block>, RegistryObject<BlockItem>> block) {
        return block.getB().get();
    }
}
