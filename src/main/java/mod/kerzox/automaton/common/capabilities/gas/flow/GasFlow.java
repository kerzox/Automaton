package mod.kerzox.automaton.common.capabilities.gas.flow;

import net.minecraft.tileentity.TileEntity;

import java.util.HashSet;
import java.util.Set;

public class GasFlow {

    private int mbFlow;
    private Set<TileEntity> providers = new HashSet<>();

    public GasFlow(TileEntity provider, int flow) {
        this.providers.add(provider);
        this.mbFlow = flow;
    }

    public void addProvider(TileEntity provider) {
        this.providers.add(provider);
    }

    public void removeProvider(TileEntity provider) {
        this.providers.remove(provider);
    }

    public Set<TileEntity> getProviders() {
        return providers;
    }

    public int getFlowInMB() {
        return mbFlow;
    }

    public void setFlowInMb(int mb) {
        this.mbFlow = mbFlow;
    }
}
