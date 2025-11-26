package cc.cassian.raspberry.blocks.state;

import net.minecraft.util.StringRepresentable;

public enum CabinetType implements StringRepresentable {
    SINGLE("single", 0),
    TOP("top", 2),
    BOTTOM("bottom", 1);

    public static final CabinetType[] BY_ID = values();
    private final String name;
    private final int opposite;

    private CabinetType(String name, int opposite) {
        this.name = name;
        this.opposite = opposite;
    }

    public String getSerializedName() {
        return this.name;
    }

    public CabinetType getOpposite() {
        return BY_ID[this.opposite];
    }
}
