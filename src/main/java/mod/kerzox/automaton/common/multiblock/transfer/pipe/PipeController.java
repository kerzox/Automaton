package mod.kerzox.automaton.common.multiblock.transfer.pipe;

import mod.kerzox.automaton.Automaton;
import mod.kerzox.automaton.common.capabilities.gas.GasConsumer;
import mod.kerzox.automaton.common.capabilities.gas.GasTank;
import mod.kerzox.automaton.common.multiblock.transfer.ICache;
import mod.kerzox.automaton.common.multiblock.transfer.TransferController;
import mod.kerzox.automaton.common.tile.transfer.pipes.PressurizedPipe;
import mod.kerzox.automaton.common.util.IRemovableTick;
import net.minecraft.fluid.Fluid;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class PipeController extends TransferController<PressurizedPipe> {

    private final LazyOptional<GasTank> gasHandler;
    private final Set<TileEntity> consumers = new HashSet<>();

    public PipeController(PressurizedPipe te) {
        super(te);
        this.cache = new PipeCache(calculateCapacity(1));
        this.gasHandler = LazyOptional.of(() -> getCache().getTank());
    }

    @Override
    public PipeCache getCache() {
        return (PipeCache) super.getCache();
    }

    @Override
    public void controllerTick() {
        super.controllerTick();

        if (this.doUpdate) {
            this.consumers.clear();
            for (PressurizedPipe pipe : this.getNetwork()) {
                consumers.addAll(pipe.getConsumingTiles());
            }
            this.doUpdate = false;
        }

        if (getCache().getTank().getFluidAmount() > 0) {
            pushFluidToConsumers();
        }
//        Automaton.logger().info(this.id);
   }

    private void pushFluidToConsumers() {

        for (TileEntity consumer : consumers) { //// oop through all consumers

            Optional<IFluidHandler> fluidHandler = consumer.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY).resolve();
            if (fluidHandler.isPresent()) {
                if (fluidHandler.get() instanceof GasConsumer) {
                    GasConsumer handler = (GasConsumer) fluidHandler.get();
                    if (!getCache().isRoundRobin()) fillOrderly(handler);
                    fillRoundRobin(consumer, handler, consumers.size());
                }
            }
        }
    }

    private void fillRoundRobin(TileEntity consumer, GasConsumer destination, float totalConsumers) {

        int drainSplit = Math.round(150f / totalConsumers);

        FluidStack simulateDrain = getCache().getTank().drain(drainSplit, IFluidHandler.FluidAction.SIMULATE);
        if (simulateDrain.isEmpty()) return;
        FluidStack retStack = FluidUtil.tryFluidTransfer(destination, getCache().getTank(), simulateDrain, true);
    }

    private void fillOrderly(GasConsumer destination) {
        FluidStack simulateDrain = getCache().getTank().drain(150, IFluidHandler.FluidAction.SIMULATE);
        if (simulateDrain.isEmpty()) return;
        FluidStack retStack = FluidUtil.tryFluidTransfer(destination, getCache().getTank(), simulateDrain, true);
    }

    public int calculateCapacity(int pipeType) {
        return 200 * this.getNetwork().size();
    }

    @Override
    public void afterAttach() {
        this.doUpdate = true;
    }

    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            return gasHandler.cast();
        }
        return super.getCapability(cap, side);
    }
}
