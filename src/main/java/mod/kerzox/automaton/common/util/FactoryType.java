package mod.kerzox.automaton.common.util;

import net.minecraft.util.IStringSerializable;

public enum FactoryType implements IStringSerializable {

    COAL,
    STEAM,
    ELECTRIC,
    HEAT,
    NUCLEAR;

    @Override
    public String getSerializedName() {
        return toString().toLowerCase();
    }
}
