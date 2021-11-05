package mod.kerzox.automaton.common.multiblock.transfer;

import mod.kerzox.automaton.common.util.IRemovableTick;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;

public class TransferController<T extends TileEntity> {

    protected ICache cache;
    protected World world;
    private final T controllerBlock;
    private final Set<T> network = new HashSet<>();
    private final Set<T> merge = new HashSet<>();
    protected boolean doUpdate = false;

    public TransferController(T te) {
        this.controllerBlock = te;
        this.world = te.getLevel();
        this.network.add(te);
    }

    public T getControllerBlock() {
        return controllerBlock;
    }

    public void attach(T toAttach) {
        if (toAttach instanceof IMultiblockAttachable) {
            IMultiblockAttachable<T> te = (IMultiblockAttachable<T>) toAttach;
            if (te.getController() != this) {
                merge(te);
            }
            this.network.add(toAttach);
        }
        afterAttach();
    }

    public void remove(T toRemove) {
        this.network.forEach(b -> {
            if (b != this.controllerBlock) ((IMultiblockAttachable<T>) b).setController(null);
        });
        this.network.clear();
        if (toRemove != this.controllerBlock) {
            IRemovableTick.add(this.controllerBlock);
            return;
        }
        this.network.add(this.controllerBlock);
    }

    public void merge(IMultiblockAttachable<T> toMerge) {
        for (T t : toMerge.getController().network) {
            ((IMultiblockAttachable<T>) t).setController(this);
        }
        toMerge.setController(this);
    }

    public void afterAttach() {

    }

    public void controllerTick() {
        if (this.world.getBlockEntity(this.getControllerBlock().getBlockPos()) != this.getControllerBlock()) {
            IRemovableTick.remove(this.getControllerBlock());
        }
    }

    public Set<T> getNetwork() {
        return this.network;
    }

    public ICache getCache() {
        return cache;
    }

    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        return LazyOptional.empty();
    }

    public void needsUpdate() {
        this.doUpdate = true;
    }
}
