package mod.kerzox.automaton.common.capabilities.item;

import mod.kerzox.automaton.common.capabilities.IStrictSided;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class SidedInventoryHandler extends CombinedInvWrapper {

    public SidedInventoryHandler(IItemHandlerModifiable... itemHandler)
    {
        super(itemHandler);
    }

    public Boolean[] checkSidesOfHandlers(Direction dir) {
        Boolean[] ret = new Boolean[this.itemHandler.length];
        for (int i = 0; i < this.itemHandler.length; i++) {
            ItemStackWrapper wrapper = getHandlerFromIndex(i);
            if (wrapper instanceof ItemOutputWrapper) ret[i] = wrapper.checkSide(dir, IStrictSided.SIDE.OUTPUT);
            else if (wrapper instanceof ItemInputWrapper) ret[i] = wrapper.checkSide(dir, IStrictSided.SIDE.INPUT);
        }
        return ret;
    }

    @Override
    public ItemStackWrapper getHandlerFromIndex(int index) {
        return (ItemStackWrapper) super.getHandlerFromIndex(index);
    }

    @Nonnull
    @Override
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        int index = getIndexForSlot(slot);
        return getHandlerFromIndex(index) instanceof ItemOutputWrapper ? stack : super.insertItem(slot, stack, simulate);
    }

    @Nonnull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        int index = getIndexForSlot(slot);
        return getHandlerFromIndex(index) instanceof ItemInputWrapper ? ItemStack.EMPTY : super.extractItem(slot, amount, simulate);
    }
}
