package mod.kerzox.automaton.common.capabilities.gas;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;

public interface IGasHandler {

    Gas getGas();
    int getCapacity();
    int getStorage();
    boolean isEmpty();
    void heat(int amount);
    void cool(int amount);
    int fill(TileEntity te, int amount, boolean simulate);
    int drain(TileEntity te, int amount, boolean simulate);
    int fill(TileEntity te, int amount);
    int drain(TileEntity te, int amount);
    CompoundNBT serialize();
    void deserialize(CompoundNBT nbt);
    void changeGas(Gas gas);
}
