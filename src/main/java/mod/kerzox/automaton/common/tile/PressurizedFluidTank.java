package mod.kerzox.automaton.common.tile;

import mod.kerzox.automaton.common.capabilities.gas.GasTank;
import mod.kerzox.automaton.common.tile.base.AutomatonTile;
import mod.kerzox.automaton.common.util.IBlockIsEntity;
import net.minecraft.block.Block;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.extensions.IForgeTileEntity;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class PressurizedFluidTank extends AutomatonTile<PressurizedFluidTank> implements ITickableTileEntity, IForgeTileEntity {

    private final GasTank tank = new GasTank(30000);
    private final LazyOptional<GasTank> tankHandler = LazyOptional.of(() -> tank);

    public PressurizedFluidTank(Block block) {
        super(block);
    }

    @Override
    public void tick() {
        if (getLevel() != null) {
            if (!level.isClientSide) System.out.println("Current fluid amount: " + tank.getFluidAmount() + "\nCurrent pressure: " + tank.getPressure() + "\nMaximum pressure: " + tank.getPressureCapacity());
        }
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            return tankHandler.cast();
        }
        return LazyOptional.empty();
    }
}
