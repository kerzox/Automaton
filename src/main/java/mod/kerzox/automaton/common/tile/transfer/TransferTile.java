package mod.kerzox.automaton.common.tile.transfer;

import mod.kerzox.automaton.common.multiblock.transfer.PipeNetwork;
import mod.kerzox.automaton.common.tile.base.AutomatonTile;
import net.minecraft.block.Block;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;

import java.util.HashMap;
import java.util.Map;

public abstract class TransferTile extends AutomatonTile<TransferTile> {

    protected CompoundNBT cachedControllerData;
    protected PipeNetwork network;
    protected Map<Direction, TransferTile> connectedPipes = new HashMap<>();

    public TransferTile(Block block) {
        super(block);
    }

    public abstract PipeNetwork createNetwork();

    public abstract void setNetwork(PipeNetwork network);

    public abstract PipeNetwork getNetwork();

    public abstract Map<Direction, TransferTile> getConnectedPipes();

    public CompoundNBT getCachedControllerData() {
        return cachedControllerData;
    }
}
