package mod.kerzox.automaton.common.capabilities.gas;

import mod.kerzox.automaton.common.capabilities.heat.IHeat;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;

import java.util.function.Predicate;

import static mod.kerzox.automaton.common.capabilities.gas.GasUtil.GAS_CONSTANT;
import static mod.kerzox.automaton.common.capabilities.gas.GasUtil.getTemperatureOfGas;

public class GasTank extends FluidTank {

    private float pressureCapacity;
    private float pressure = 0;

    public GasTank(int liquidCapacity) {
        super(liquidCapacity);
        this.pressureCapacity = calculateMaxPressure();
    }

    public GasTank(int capacity, Predicate<FluidStack> validator) {
        super(capacity, validator);
    }

    private float calculateGasPressure() {
        float kPa = 0;
        if (!getFluid().isEmpty()) {
            float mol = GasUtil.getMolFromLitres(getFluidAmount());
            // P = nRt / V
            kPa = (mol * GAS_CONSTANT * getTemperatureOfGas(getFluid())) / getCapacity() / 1000;
            this.pressure = Math.round(kPa);
        }
        return this.pressure;
    }

    private float calculateMaxPressure() {
        float kPa = 0;
        float mol = GasUtil.getMolFromLitres(getCapacity());
        kPa = (mol * GAS_CONSTANT * GasUtil.convertCelsiusToKelvin(IHeat.BASE_STEAM_TEMPERATURE)) / getCapacity() / 1000;
        return Math.round(kPa);
    }

    public float getPressureCapacity() {
        return pressureCapacity;
    }

    public float getPressure() {
        this.calculateGasPressure();
        return pressure;
    }

    @Override
    public FluidTank readFromNBT(CompoundNBT nbt) {
        FluidTank tank = super.readFromNBT(nbt);
        if (nbt.contains("gasNBT")) {
            this.pressureCapacity = nbt.getCompound("gasNBT").getFloat("capacity");
            this.pressure = nbt.getCompound("gasNBT").getFloat("pressure");
        }
        return tank;
    }

    @Override
    public CompoundNBT writeToNBT(CompoundNBT nbt) {
        CompoundNBT compoundNBT = new CompoundNBT();
        compoundNBT.putFloat("capacity", this.pressureCapacity);
        compoundNBT.putFloat("pressure", this.pressure);
        nbt.put("gasNBT", compoundNBT);
        return super.writeToNBT(nbt);
    }
}
