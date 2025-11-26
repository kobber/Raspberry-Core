package cc.cassian.raspberry.registry;

import cc.cassian.raspberry.blocks.state.CabinetType;
import net.minecraft.world.level.block.state.properties.EnumProperty;

public class RaspberryBlockStateProperties {
    public static final EnumProperty<CabinetType> CABINET_TYPE;
    static {
        CABINET_TYPE = EnumProperty.create("type", CabinetType.class);
    }
}
