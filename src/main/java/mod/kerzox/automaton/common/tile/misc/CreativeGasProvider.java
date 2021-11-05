package mod.kerzox.automaton.common.tile.misc;

import mod.kerzox.automaton.common.capabilities.gas.GasTank;
import mod.kerzox.automaton.common.tile.base.AutomatonTickableTile;
import net.minecraft.block.Block;
import net.minecraft.fluid.Fluid;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nonnull;

import static mod.kerzox.automaton.registry.FluidRegistrar.ALL_FLUIDS;

public class CreativeGasProvider extends AutomatonTickableTile<CreativeGasProvider> {

    private final GasTank tank = new GasTank(Integer.MAX_VALUE);
    private final LazyOptional<GasTank> tankHandler = LazyOptional.of(() -> tank);
    private FluidStack fluidStack = new FluidStack(ALL_FLUIDS.get(0).getStill().get(), 100);

    public CreativeGasProvider(Block block) {
        super(block);
    }

    @Override
    public void onServerTick() {
        pushGas();
    }

    public void pushGas() {
        if (getLevel() != null) {
            for (Direction dir : Direction.values()) {
                BlockPos pos = this.getBlockPos();
                TileEntity te = getLevel().getBlockEntity(pos.relative(dir));
                if (te != null) {
                    te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY).ifPresent(cap -> {
                        cap.fill(this.fluidStack, IFluidHandler.FluidAction.EXECUTE);
                    });
                }
            }
        }
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap) {
        if (cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            return tankHandler.cast();
        }
        return super.getCapability(cap);
    }
}
