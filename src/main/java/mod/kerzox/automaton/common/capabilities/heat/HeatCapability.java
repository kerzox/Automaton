package mod.kerzox.automaton.common.capabilities.heat;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

import javax.annotation.Nullable;

public class HeatCapability {

    @CapabilityInject(IHeat.class)
    public static Capability<IHeat> HEAT = null;

    public static void register() {
        CapabilityManager.INSTANCE.register(IHeat.class, new Capability.IStorage<IHeat>() {
            @Nullable
            @Override
            public INBT writeNBT(Capability<IHeat> capability, IHeat instance, Direction side) {
                CompoundNBT nbt = new CompoundNBT();
                nbt.putFloat("temperature", instance.getTemperature());
                return nbt;
            }

            @Override
            public void readNBT(Capability<IHeat> capability, IHeat instance, Direction side, INBT nbt) {
                CompoundNBT compoundNBT = (CompoundNBT) nbt;
                instance.setTemperature(compoundNBT.getFloat("temperature"));
            }
        }, HeatStorage::new);
    }

}
