package mod.kerzox.automaton.common.tile.multiblock;

import mod.kerzox.automaton.common.tile.base.AutomatonTickableTile;
import mod.kerzox.automaton.common.tile.base.AutomatonTile;
import net.minecraft.block.Block;

public class MultiblockTerminal<T extends AutomatonTile<T>> extends AutomatonTickableTile<T> {

    public MultiblockTerminal(Block block) {
        super(block);
    }

}
