package mod.kerzox.automaton.common.capabilities.gas;

import mod.kerzox.automaton.common.capabilities.heat.HeatStorage;
import mod.kerzox.automaton.common.capabilities.heat.IHeat;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

import javax.annotation.Nullable;

public class GasCapability {

    @CapabilityInject(IGasHandler.class)
    public static Capability<IGasHandler> GAS = null;

    public static void register() {
        CapabilityManager.INSTANCE.register(IGasHandler.class, new Capability.IStorage<IGasHandler>() {
            @Nullable
            @Override
            public INBT writeNBT(Capability<IGasHandler> capability, IGasHandler instance, Direction side) {
                CompoundNBT nbt = new CompoundNBT();
                instance.serialize();
                return nbt;
            }

            @Override
            public void readNBT(Capability<IGasHandler> capability, IGasHandler instance, Direction side, INBT nbt) {
                CompoundNBT compoundNBT = (CompoundNBT) nbt;
                instance.deserialize(compoundNBT);
            }
        }, GasStorage::new);
    }

}
