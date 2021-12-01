package mod.kerzox.automaton.common.multiblock.transfer.fluid;

import mod.kerzox.automaton.common.capabilities.gas.Gas;
import mod.kerzox.automaton.common.capabilities.gas.GasStorage;
import mod.kerzox.automaton.common.capabilities.gas.flow.GasFlow;
import mod.kerzox.automaton.common.multiblock.transfer.ICache;
import mod.kerzox.automaton.common.tile.transfer.TransferTile;
import mod.kerzox.automaton.registry.AutomatonMaterials;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.LazyOptional;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class PipeFluidCache implements ICache {

    private boolean active;
    private final GasStorage handler = gasHandler();
    private final LazyOptional<GasStorage> gasLazy = LazyOptional.of(() -> handler);
    private final GasFlow flow = new GasFlow(null, 150);

    @Override
    public CompoundNBT save(CompoundNBT nbt) {
        CompoundNBT cache = new CompoundNBT();
        cache.putBoolean("active", active);
        cache.put("handler", handler.serialize());
        nbt.put("cache", cache);
        return nbt;
    }

    @Override
    public void read(CompoundNBT nbt) {
        if (nbt.contains("cache")) {
            this.active = nbt.getBoolean("active");
            this.handler.deserialize(nbt.getCompound("cache").getCompound("handler"));
        }
    }

    @Override
    public void updateSpecified(String var, Object data) {

    }

    private GasStorage gasHandler() {
        return new GasStorage(AutomatonMaterials.Gases.STEAM.get(), 1000) {
            @Override
            public void onFill(TileEntity te) {
                super.onFill(te);
                flow.addProvider(te);
            }

            @Override
            public void onDrain(TileEntity te) {
                super.onDrain(te);
            }

        };
    }

    public GasFlow getFlow() {
//        if (!flow.getProviders().isEmpty()) {
//            this.flow.getProviders().removeIf(TileEntity::isRemoved);
//        }
        return this.flow;
    }

    public LazyOptional<GasStorage> getGasLazy() {
        return gasLazy;
    }

    public GasStorage getHandler() {
        return handler;
    }


}
