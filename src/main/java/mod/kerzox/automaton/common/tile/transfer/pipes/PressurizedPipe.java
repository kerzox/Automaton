package mod.kerzox.automaton.common.tile.transfer.pipes;

import mod.kerzox.automaton.common.capabilities.gas.GasTank;
import mod.kerzox.automaton.common.multiblock.transfer.IMultiblockAttachable;
import mod.kerzox.automaton.common.multiblock.transfer.TransferController;
import mod.kerzox.automaton.common.multiblock.transfer.pipe.PipeController;
import mod.kerzox.automaton.common.tile.base.AutomatonTile;
import mod.kerzox.automaton.common.tile.misc.CreativeGasProvider;
import mod.kerzox.automaton.common.util.IRemovableTick;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class PressurizedPipe extends AutomatonTile<PressurizedPipe> implements IRemovableTick, IMultiblockAttachable<PressurizedPipe> {

    private PipeController controller;
    private final Set<TileEntity> consumingTiles = new HashSet<>();

    public PressurizedPipe(Block block) {
        super(block);
        IRemovableTick.add(this);
    }

    private void createController() {
        this.controller = new PipeController(this);
    }

    @Override
    public boolean tick() {
        if (getLevel() != null) { // should never be null
            if (!getLevel().isClientSide) {
                for (Direction dir : Direction.values()) {
                    BlockPos newPos = getBlockPos().relative(dir);
                    TileEntity foundTile = getLevel().getBlockEntity(newPos);
                    if (foundTile != null) {
                        if (foundTile instanceof PressurizedPipe) {
                            PressurizedPipe pipe = (PressurizedPipe) foundTile;
                            if (pipe.hasController()) {
                                if (!pipe.getController().getNetwork().contains(this)) {
                                    // connect to that controller, remove ourself from special tick by returning true
                                    pipe.getController().attach(this);
                                    return true;
                                }
                            }
                        }
                    }
                }
                if (!hasController()) createController();
            }
            if (hasController()) {
                if (this.controller.getControllerBlock() == this) {
                    this.controller.controllerTick();
                }
            }
        }
        return false;
    }

    public boolean hasController() {
        return controller != null;
    }

    @Override
    public void load(BlockState state, CompoundNBT nbt) {
        if (nbt.contains("controllerData")) {
            if (nbt.getBoolean("controller")) {
                createController();
                this.controller.getCache().read(nbt.getCompound("ControllerData"));
            }
        }
        super.load(state, nbt);
    }

    @Override
    public CompoundNBT save(CompoundNBT nbt) {
        if (hasController()) {
            if (this.controller.getControllerBlock() != this) {
                nbt.putBoolean("controller", false);
            }
            nbt.putBoolean("controller", true);
            this.controller.getCache().save(nbt);
        }
        return super.save(nbt);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            return hasController() ? this.controller.getCapability(cap, side) : super.getCapability(cap, side);
        }
        return super.getCapability(cap, side);
    }

    @Override
    public PipeController getController() {
        return controller;
    }

    @Override
    public void setController(TransferController<PressurizedPipe> controller) {
        this.controller = (PipeController) controller;
    }

    @Override
    public void setRemoved() {
        if (this.controller != null && this.controller.getControllerBlock() == this) {
            IRemovableTick.remove(this);
            this.controller = null;
        }
        super.setRemoved();
    }

    public Set<TileEntity> getConsumingTiles() {
        return consumingTiles;
    }

    public void doNeighbourConnection() {
        consumingTiles.clear();
        Set<TileEntity> newConnections = new HashSet<>();
        for (Direction dir : Direction.values()) {
            TileEntity te = null;
            if (this.level != null) {
                te = this.level.getBlockEntity(this.getBlockPos().relative(dir));
                if (te != null) {
                    findConsumers(newConnections, te);
                    pipeConnections(te);
                }
            }
        }

        // replace old consuming with the new connections
        consumingTiles.addAll(newConnections);

        if (hasController()) this.controller.needsUpdate();
    }

    /**
     * Function to create new connections for the pipes blockstates
     * @param te tileentity
     */

    private void pipeConnections(TileEntity te) {
        if (te instanceof PressurizedPipe) {

        }
    }

    /**
     * Find consuming blocks to add to the network
     * @param newConnections
     * @param te
     */

    private void findConsumers(Set<TileEntity> newConnections, TileEntity te) {
        if (!(te instanceof PressurizedPipe || te instanceof CreativeGasProvider)) {
            Optional<IFluidHandler> capability = te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY).resolve();
            if (capability.isPresent()) {
                if (capability.get() instanceof GasTank) {
                    newConnections.add(te);
                }
            }
        }
    }
}
