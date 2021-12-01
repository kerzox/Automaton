package mod.kerzox.automaton.common.capabilities.item;

import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class ItemOutputWrapper extends ItemStackWrapper {

    @Nonnull
    @Override
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        return stack;
    }

    @Nonnull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        return super.extractItem(slot, amount, simulate);
    }

}
