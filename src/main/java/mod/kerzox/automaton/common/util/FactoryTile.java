package mod.kerzox.automaton.common.util;

import net.minecraft.block.Block;

public class FactoryTile {

    private Block block;
    private FactoryTile type;

    public FactoryTile(Block block, FactoryTile type) {
        this.block = block;
        this.type = type;
    }

    public Block getBlock() {
        return block;
    }

    public FactoryTile getType() {
        return type;
    }
}
