package mod.kerzox.automaton.common.util;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;

public interface IBlockIsEntity<T extends TileEntity> {

    TileEntityType<? extends T> getEntityType();

    static TileEntityType<? extends TileEntity> tileFromBlock(Block block) {
        return ((IBlockIsEntity<? extends TileEntity>) block).getEntityType();
    }
}
