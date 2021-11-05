package mod.kerzox.automaton.common.capabilities.heat;

public class HeatStorage implements IHeat {

    private float temperature;

    public HeatStorage() {
        this.temperature = 20;
    }

    /**
     * Set a heat storage to a custom temperature
     * @param temperature cannot be below absolute zero
     */

    public HeatStorage(float temperature) {
        this.temperature = Math.max(temperature, IHeat.ABSOLUTE_ZERO);
    }

    @Override
    public void heat(float amount) {
        this.temperature += amount;
    }

    @Override
    public void cool(float amount) {
        this.temperature -= Math.max(temperature - amount, IHeat.ABSOLUTE_ZERO);
    }

    @Override
    public void setTemperature(float temperature) {
        this.temperature = temperature;
    }

    @Override
    public float getTemperature() {
        return this.temperature;
    }
}
