package mod.kerzox.automaton.common.tile.base;

import mod.kerzox.automaton.Automaton;
import mod.kerzox.automaton.common.util.FactoryType;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;

public class AutomatonFactory<T extends AutomatonTickableTile<T>> extends AutomatonTickableTile<T> {

    private FactoryType factory;

    public AutomatonFactory(Block block, FactoryType type) {
        super(block);
        this.factory = type;
    }

    public FactoryType getFactoryType() {
        return factory;
    }

    public boolean isFactoryType(FactoryType type) {
        return this.factory == type;
    }

}
