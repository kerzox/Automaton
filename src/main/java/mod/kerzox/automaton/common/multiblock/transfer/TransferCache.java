package mod.kerzox.automaton.common.multiblock.transfer;

import net.minecraft.nbt.CompoundNBT;

public abstract class TransferCache implements ICache {

    private CompoundNBT storedNBT;

    /**
     * Override to store new data into nbt, must return store(nbt);
     * @param nbt
     * @return nbt
     */

    public CompoundNBT save(CompoundNBT nbt) {
        return nbt;
    }

}
