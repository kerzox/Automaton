package mod.kerzox.automaton.common.capabilities.gas;

import mod.kerzox.automaton.registry.AutomatonMaterials;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;

import javax.annotation.Nullable;

public class GasStorage implements IGasHandler {

    private Gas gas;
    private int storage;
    private int capacity;
    private int maxPressure;

    public GasStorage() {
        this.gas = AutomatonMaterials.Gases.EMPTY.get();
        this.capacity = 0;
        this.storage = 0;
    }

    public GasStorage(Gas gas, int capacity, int inital) {
        this.gas = gas;
        this.capacity = capacity;
        this.storage = inital;
    }

    public GasStorage(Gas gas, int capacity) {
        this.gas = gas;
        this.capacity = capacity;
        this.storage = 0;
    }

    @Override
    public int fill(TileEntity te, int amount, boolean simulate) {
        int ret = Math.min(capacity - storage, amount);
        if (simulate) return ret;
        this.storage += ret;
        onFill(te);
        return ret;
    }

    @Override
    public int drain(TileEntity te, int amount, boolean simulate) {
        int ret = Math.min(storage, amount);
        if (simulate) return ret;
        this.storage -= ret;
        onDrain(te);
        return ret;
    }

    @Override
    public int fill(TileEntity te, int amount) {
        return fill(te, amount, false);
    }

    @Override
    public int drain(TileEntity te, int amount) {
        return drain(te, amount, false);
    }

    public void onFill(TileEntity te) {
        onContentChanged();
    }

    public void onDrain(TileEntity te) {
        onContentChanged();
    }

    public void onContentChanged() {

    }

    @Override
    public Gas getGas() {
        return this.gas;
    }

    @Override
    public int getCapacity() {
        return this.capacity;
    }

    @Override
    public int getStorage() {
        return this.storage;
    }

    @Override
    public boolean isEmpty() {
        return this.storage == 0;
    }

    @Override
    public void heat(int amount) {
        if (this.gas != AutomatonMaterials.Gases.EMPTY.get()) {
            this.gas.heat(amount);
        }
    }

    @Override
    public void cool(int amount) {
        if (this.gas != AutomatonMaterials.Gases.EMPTY.get()) {
            this.gas.cool(amount);
        }
    }

    @Override
    public CompoundNBT serialize() {
        CompoundNBT nbt = new CompoundNBT();
        if (this.gas != null) {
            this.gas.serialize(nbt);
        }
        nbt.putInt("capacity", this.capacity);
        nbt.putInt("storage", this.storage);
        nbt.putInt("pressure", this.maxPressure);
        return nbt;
    }

    @Override
    public void deserialize(CompoundNBT nbt) {
        this.capacity = nbt.getInt("capacity");
        this.storage = nbt.getInt("storage");
        this.maxPressure = nbt.getInt("maxPressure");
        if (this.gas != null) {
            this.gas.deserialize(nbt);
        }
    }

    @Override
    public void changeGas(Gas gas) {
        this.gas = gas;
    }
}
