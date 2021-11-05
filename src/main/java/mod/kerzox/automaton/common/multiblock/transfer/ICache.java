package mod.kerzox.automaton.common.multiblock.transfer;

import net.minecraft.nbt.CompoundNBT;

public interface ICache {

    CompoundNBT save(CompoundNBT nbt);
    void read(CompoundNBT nbt);
    void updateSpecified(String var, Object data);
}
