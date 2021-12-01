package mod.kerzox.automaton.common.capabilities.gas;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class Gas extends ForgeRegistryEntry<Gas> {

    private String name;
    private int temperature;
    private int pressure;

    public Gas(String name, int temperature, int pressure) {
        this.name = name;
        this.temperature = temperature;
        this.pressure = pressure;
    }

    public void heat(int amount) {
        this.temperature += amount;
    }

    public void cool(int amount) {
        this.temperature -= amount;
    }

    public CompoundNBT serialize(CompoundNBT nbt) {
        CompoundNBT nbt1 = new CompoundNBT();
        nbt1.putString("name", this.name);
        nbt1.putInt("temperature", this.temperature);
        nbt1.putInt("pressure", this.pressure);
//        nbt1.putInt("density", this.density);
        nbt.put("gas", nbt1);
        return nbt;
    }

    public void deserialize(CompoundNBT nbt) {
        if (nbt.contains("gas")) {
            this.name = nbt.getString("name");
            this.temperature = nbt.getInt("temperature");
            this.pressure = nbt.getInt("pressure");
//            this.density = nbt.getInt("density");
        }
    }

    public String getName() {
        return name;
    }

    public int getPressure() {
        return pressure;
    }

    public int getTemperature() {
        return temperature;
    }

}
