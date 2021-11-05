package mod.kerzox.automaton.common.multiblock.transfer.pipe;

import mod.kerzox.automaton.common.capabilities.gas.GasTank;
import mod.kerzox.automaton.common.multiblock.transfer.TransferCache;
import net.minecraft.nbt.CompoundNBT;

public class PipeCache extends TransferCache {

    private final GasTank tank;
    private int totalStorage;
    private float pressure;

    public PipeCache(int capacity) {
        this.totalStorage = 0;
        this.pressure = 0;
        this.tank = new GasTank(capacity);
    }

    @Override
    public CompoundNBT save(CompoundNBT nbt) {
        CompoundNBT cache = new CompoundNBT();
        cache.putInt("totalStorage", this.totalStorage);
        cache.putFloat("pressure", this.pressure);
        nbt.put("cache", cache);
        return nbt;
    }

    @Override
    public void read(CompoundNBT nbt) {
        if (nbt.contains("cache")) {
            this.totalStorage = nbt.getInt("totalStorage");
            this.pressure = nbt.getFloat("pressure");
        }
    }

    @Override
    public void updateSpecified(String var, Object data) {
        switch (var) {
            case "totalStorage": this.totalStorage = (int) data; break;
            case "pressure": this.pressure = (float) data; break;
        }
    }

    public GasTank getTank() {
        return tank;
    }

    public int getTotalStorage() {
        return totalStorage;
    }

    public float getPressure() {
        return pressure;
    }
}
