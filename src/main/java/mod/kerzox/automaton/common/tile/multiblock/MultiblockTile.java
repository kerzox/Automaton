package mod.kerzox.automaton.common.tile.multiblock;

import mod.kerzox.automaton.common.tile.base.AutomatonTile;
import net.minecraft.block.Block;

public class MultiblockTile<T extends AutomatonTile<T>> extends AutomatonTile<T> {

    public MultiblockTile(Block block) {
        super(block);
    }

}
