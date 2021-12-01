package mod.kerzox.automaton.common.capabilities.gas;

import net.minecraft.tileentity.TileEntity;

public class GasConsumer extends GasTank {

    private int consumeAmount;

    public GasConsumer(Gas gas, int capacity, int consumeAmount) {
        this(gas, capacity, 0, consumeAmount);
    }

    public GasConsumer(Gas gas, int capacity, int inital, int consumeAmount) {
        super(gas, capacity, inital);
    }

    public boolean hasEnoughToProcess(TileEntity te) {
        int ret = drain(null, consumeAmount, true);
        if (ret == 0) return false;
        return true;
    }

    public void useFuel(TileEntity te) {
        int ret = drain(te, consumeAmount);
    }

    public void exhaust() {

    }

}
