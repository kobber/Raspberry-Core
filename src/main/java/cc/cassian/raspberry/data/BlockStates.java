package cc.cassian.raspberry.data;

import cc.cassian.raspberry.RaspberryMod;
import cc.cassian.raspberry.blocks.TallCabinetBlock;
import cc.cassian.raspberry.blocks.state.CabinetType;
import cc.cassian.raspberry.registry.BlockSupplier;
import cc.cassian.raspberry.registry.RaspberryBlocks;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;

import static cc.cassian.raspberry.RaspberryMod.MOD_ID;

public class BlockStates extends BlockStateProvider {

    public BlockStates(DataGenerator gen, ExistingFileHelper exFileHelper) {
        super(gen, MOD_ID, exFileHelper);
    }

//    private String blockName(Block block) {
//        return ForgeRegistries.BLOCKS.getKey(block).getPath();
//    }

    public ResourceLocation resourceBlock(String path) {
        return RaspberryMod.locate("block/" + path);
    }

    @Override
    protected void registerStatesAndModels() {
        this.cabinetBlock(RaspberryBlocks.OAK_CABINET);
//        this.cabinetBlock(RaspberryBlocks.BIRCH_CABINET);
//        this.cabinetBlock(RaspberryBlocks.SPRUCE_CABINET);
//        this.cabinetBlock(RaspberryBlocks.JUNGLE_CABINET);
//        this.cabinetBlock(RaspberryBlocks.ACACIA_CABINET);
//        this.cabinetBlock(RaspberryBlocks.DARK_OAK_CABINET);
//        this.cabinetBlock(RaspberryBlocks.MANGROVE_CABINET);
//        this.cabinetBlock(RaspberryBlocks.CRIMSON_CABINET);
//        this.cabinetBlock(RaspberryBlocks.WARPED_CABINET);
    }

    public void cabinetBlock(BlockSupplier block) {
        this.horizontalBlock(block.getBlock(), state -> {
            String blockID = block.getID();
            String open = state.getValue(TallCabinetBlock.OPEN) ? "_open" : "";
            String type = "";
            if (state.getValue(TallCabinetBlock.TYPE).equals(CabinetType.TOP)) {
                type = "_top";
            } else if (state.getValue(TallCabinetBlock.TYPE).equals(CabinetType.BOTTOM)) {
                type = "_bottom";
            }
            return models().orientable(blockID + type + open,
                resourceBlock("cabinets/" + blockID + "_side" + type),
                resourceBlock("cabinets/" + blockID + "_front" + type + open),
                resourceBlock("cabinets/" + blockID + "_top"));
        });
    }

}
