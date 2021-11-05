package mod.kerzox.automaton.common.tile.base;

import net.minecraft.block.Block;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraftforge.common.extensions.IForgeTileEntity;

public class AutomatonTickableTile<T extends AutomatonTile<T>> extends AutomatonTile<T> implements ITickableTileEntity, IForgeTileEntity {

    public AutomatonTickableTile(Block block) {
        super(block);
    }

    @Override
    public void tick() {
        if (getLevel() != null) {
            if (!getLevel().isClientSide) {
                onServerTick();
            } else {
                onClientTick();
            }
        }
    }

    public void onServerTick() {

    }

    public void onClientTick() {

    }
}
