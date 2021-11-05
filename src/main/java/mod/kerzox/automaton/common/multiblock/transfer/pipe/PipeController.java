package mod.kerzox.automaton.common.multiblock.transfer.pipe;

import mod.kerzox.automaton.Automaton;
import mod.kerzox.automaton.common.capabilities.gas.GasTank;
import mod.kerzox.automaton.common.multiblock.transfer.ICache;
import mod.kerzox.automaton.common.multiblock.transfer.TransferController;
import mod.kerzox.automaton.common.tile.transfer.pipes.PressurizedPipe;
import mod.kerzox.automaton.common.util.IRemovableTick;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashSet;
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
            for (PressurizedPipe pipe : this.getNetwork()) {
                consumers.addAll(pipe.getConsumingTiles());
            }
            this.doUpdate = false;
        }

        if (getCache().getTank().getFluidAmount() > 0) {
            pushFluidToConsumers();
        }

        Automaton.logger().info("Current pipe length: " + getNetwork().size() + "\nCurrent fluid storage: " + getCache().getTank().getCapacity() + "\nFluid amount: " + getCache().getTank().getFluidAmount());
    }

    private void pushFluidToConsumers() {
        for (TileEntity consumer : consumers) { // loop through all consumers
            consumer.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY).ifPresent(cap -> {

                // if consumer has capability for fluid (they all should but just in case)

                FluidStack stack = getCache().getTank().drain(150, IFluidHandler.FluidAction.EXECUTE); // drain our tank
                cap.fill(stack, IFluidHandler.FluidAction.EXECUTE); // fill the consumers tank with return fluidstack
            });
        }
    }


    public int calculateCapacity(int pipeType) {
        return 200 * this.getNetwork().size();
    }

    @Override
    public void afterAttach() {
        //getCache().getTank().setCapacity(calculateCapacity(1));
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
