package mod.kerzox.automaton.common.capabilities.gas;

import mod.kerzox.automaton.common.capabilities.heat.IHeat;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;

public class GasUtil {

    public static float MOL = 0.04462f;
    public static float GAS_CONSTANT = 8.31446261815324f;

    /**
     * Heat the intended fluid stack
     * @param stack fluid
     * @param amount to heat by
     */

    public static void heatGas(FluidStack stack, float amount) {
        CompoundNBT nbt = new CompoundNBT();
        float currentTemp = getTemperatureOfGas(stack);
        nbt.putFloat("temperature", Math.max(currentTemp + amount, IHeat.ABSOLUTE_ZERO));
        stack.writeToNBT(nbt);
    }

    /**
     * Cool the intended fluid stack
     * @param stack fluid
     * @param amount to cool by
     */

    public static void coolGas(FluidStack stack, float amount) {
        CompoundNBT nbt = new CompoundNBT();
        float currentTemp = getTemperatureOfGas(stack);
        nbt.putFloat("temperature", Math.max(currentTemp - amount, IHeat.ABSOLUTE_ZERO));
        stack.writeToNBT(nbt);
    }

    /**
     * Gets temperature of a fluid stack
     * @param stack fluid stack to get temp from
     * @return current temperature from nbt (will return fluid attribute if nbt is not set yet)
     */

    public static float getTemperatureOfGas(FluidStack stack) {
        CompoundNBT nbt = stack.getOrCreateTag();
        if (!nbt.contains("temperature")) {
            return stack.getFluid().getAttributes().getTemperature();
        }
        return nbt.getFloat("temperature");
    }

    public static float getMolFromLitres(float litre) {
        return litre / MOL;
    }

    public static float convertCelsiusToKelvin(float celsius) {
        return celsius + -IHeat.ABSOLUTE_ZERO;
    }

    public static float convertKelvinToCelsius(float kelvin) {
        return kelvin - IHeat.ABSOLUTE_ZERO;
    }

}
