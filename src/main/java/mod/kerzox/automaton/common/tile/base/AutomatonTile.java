package mod.kerzox.automaton.common.tile.base;

import mod.kerzox.automaton.common.util.IBlockIsEntity;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;

public class AutomatonTile<T extends AutomatonTile<T>> extends TileEntity {

    public AutomatonTile(Block block) {
        super(IBlockIsEntity.tileFromBlock(block));
    }

}
