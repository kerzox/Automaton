package mod.kerzox.automaton.common.tile.transfer.pipes;

import mod.kerzox.automaton.Automaton;
import mod.kerzox.automaton.common.capabilities.gas.GasTank;
import mod.kerzox.automaton.common.multiblock.transfer.IMultiblockAttachable;
import mod.kerzox.automaton.common.multiblock.transfer.TransferController;
import mod.kerzox.automaton.common.multiblock.transfer.pipe.PipeController;
import mod.kerzox.automaton.common.network.PacketHandler;
import mod.kerzox.automaton.common.network.PipeConnectionData;
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
import java.util.*;

public class PressurizedPipe extends AutomatonTile<PressurizedPipe> implements IRemovableTick, IMultiblockAttachable<PressurizedPipe> {

    private IRemovableTick ticking;
    private PipeController controller;
    private final Set<TileEntity> consumingTiles = new HashSet<>();
    private final Set<Direction> connections = new HashSet<>();

    public PressurizedPipe(Block block) {
        super(block);
    }

    @Override
    public void createController() {
        this.controller = new PipeController(this);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        IRemovableTick.add(this);
        ticking = this;
    }

    @Override
    public void kill() {
        this.ticking = null;
    }

    @Override
    public boolean canTick() {
        return this.ticking != null;
    }

    @Override
    public void createInstance() {
        if (!canTick()) this.ticking = this;
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
                                if (hasController() && this.controller != pipe.getController()) {
                                    // we need to merge.
                                    int prev = this.controller.getNetwork().size();
                                    Automaton.logger().info(prev+" : " + pipe.getController().getNetwork().size());

                                    for (PressurizedPipe ourTiles : this.controller.getNetwork()) {
                                        ourTiles.controller = pipe.controller;
                                        pipe.getController().getNetwork().add(ourTiles);
                                        Automaton.logger().info(String.format("Merging pipe"));
                                    }

                                    Automaton.logger().info(String.format("%d merging to %d\nCurrent size: %d, new size: %d",
                                            this.controller.getId(), pipe.controller.getId(), prev, pipe.getController().getNetwork().size()));
                                    return true;
                                }
                                if (!pipe.getController().getNetwork().contains(this)) {
                                    pipe.getController().attach(this);
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
                } else {
                    return true;
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
    public void setController(TransferController<?> controller) {
        this.controller = (PipeController) controller;
    }

    @Override
    public void setRemoved() {
        if (hasController()) {
            if (this == this.controller.getControllerBlock()) {
                this.controller.needsRemoval();
                this.controller.controllerTick();
//                for (PressurizedPipe pipe : new ArrayList<>(this.controller.getNetwork())) {
//                    if (pipe != this.controller.getControllerBlock()) {
//                        pipe.controller = null;
//                        IRemovableTick.add(pipe);
//                    }
//                }
//                this.controller.getNetwork().clear();
//                this.controller = null;
            } else {
                this.controller.remove(this);
            }
        }
        super.setRemoved();
    }

    public Set<TileEntity> getConsumingTiles() {
        return consumingTiles;
    }

    public Set<Direction> getConnections() {
        return connections;
    }

    public void doNeighbourConnection() {
        consumingTiles.clear();
        connections.clear();
        Map<Direction, TileEntity> newConnections = new HashMap<>();
        for (Direction dir : Direction.values()) {
            TileEntity te = null;
            if (this.level != null) {
                te = this.level.getBlockEntity(this.getBlockPos().relative(dir));
                if (te != null) {
                    findConsumers(newConnections, dir, te);
                    pipeConnections(dir, te);
                }
            }
        }

        // replace old consuming with the new connections
        consumingTiles.addAll(newConnections.values());
        connections.addAll(newConnections.keySet());

        if (hasController()) this.controller.needsUpdate();

        List<Byte> bytes = new ArrayList<>();
        for (Direction dir : connections) {
            bytes.add((byte) dir.ordinal());
        }


        PacketHandler.sendToServer(new PipeConnectionData(getBlockPos(), bytes));
    }

    /**
     * Function to create new connections for the pipe
     * @param te tileentity
     */

    private void pipeConnections(Direction dir, TileEntity te) {
        if (te instanceof PressurizedPipe) {
            connections.add(dir);
            return;
        }
        if (te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY).isPresent()) {
            if (te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY).resolve().get() instanceof GasTank) {
                connections.add(dir);
            }
        }
    }

    /**
     * Find consuming blocks to add to the network
     * @param newConnections
     * @param te
     */

    private void findConsumers(Map<Direction, TileEntity> newConnections, Direction dir, TileEntity te) {
        if (te instanceof CreativeGasProvider) return;
        if (!(te instanceof PressurizedPipe)) {
            Optional<IFluidHandler> capability = te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY).resolve();
            if (capability.isPresent()) {
                if (capability.get() instanceof GasTank) {
                    newConnections.put(dir, te);
                }
            }
        }
    }
}
