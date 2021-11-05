package mod.kerzox.automaton.common.tile.machines;

import mod.kerzox.automaton.common.capabilities.gas.GasConsumer;
import mod.kerzox.automaton.common.tile.base.AutomatonTickableTile;
import net.minecraft.block.Block;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static mod.kerzox.automaton.registry.FluidRegistrar.ALL_FLUIDS;

public class Sawmill extends AutomatonTickableTile<Sawmill> {

    private final GasConsumer tank = new GasConsumer(10000, this, ALL_FLUIDS.get("steam").getFluid());
    private final LazyOptional<GasConsumer> gasHandler = LazyOptional.of(() -> tank);

    public Sawmill(Block block) {
        super(block);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            return gasHandler.cast();
        }
        return super.getCapability(cap, side);
    }
}
