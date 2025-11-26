package cc.cassian.raspberry.registry;

import cc.cassian.raspberry.blocks.entity.TallCabinetBlockEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static cc.cassian.raspberry.RaspberryMod.MOD_ID;

public class RaspberryBlockEntityTypes {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, MOD_ID);
    public static final RegistryObject<BlockEntityType<TallCabinetBlockEntity>> TALL_CABINET = BLOCK_ENTITIES.register("tall_cabinet", () -> BlockEntityType.Builder.of(TallCabinetBlockEntity::new, new Block[]{RaspberryBlocks.TALL_CABINET.getBlock()}).build(null));
}
