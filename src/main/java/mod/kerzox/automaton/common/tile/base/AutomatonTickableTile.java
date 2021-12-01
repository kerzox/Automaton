package mod.kerzox.automaton.common.tile.base;

import net.minecraft.block.Block;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraftforge.common.extensions.IForgeTileEntity;

public class AutomatonTickableTile<T extends AutomatonTile<T>> extends AutomatonTile<T> implements ITickableTileEntity, IForgeTileEntity {

    protected long currTick;
    protected long nextTick;
    public AutomatonTickableTile(Block block) {
        super(block);
    }

    @Override
    public void tick() {
        if (getLevel() != null) {
            commonTick();
            if (!getLevel().isClientSide) {
                currTick++;
                onServerTick();
            } else {
                onClientTick();
            }
        }
    }

    public void commonTick() {

    }

    public void onServerTick() {

    }

    public void onClientTick() {

    }

    public void updateNextTick() {
        this.nextTick += this.currTick++;
    }

    public long currentTick() {
        return currTick;
    }

    public long nextTick() {
        return nextTick;
    }
}
