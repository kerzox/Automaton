package mod.kerzox.automaton.common.multiblock.transfer.fluid;

import mod.kerzox.automaton.Automaton;
import mod.kerzox.automaton.common.capabilities.gas.GasCapability;
import mod.kerzox.automaton.common.capabilities.gas.GasConsumer;
import mod.kerzox.automaton.common.capabilities.gas.IGasHandler;
import mod.kerzox.automaton.common.capabilities.gas.flow.GasFlow;
import mod.kerzox.automaton.common.multiblock.transfer.ICache;
import mod.kerzox.automaton.common.multiblock.transfer.PipeNetwork;
import mod.kerzox.automaton.common.tile.transfer.TransferTile;
import mod.kerzox.automaton.common.tile.transfer.pipes.PressurizedPipe;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public class FluidPipeNetwork extends PipeNetwork {

    private boolean roundRobin = false;
    private final Map<TileEntity, Set<Direction>> consumers = new HashMap<>();

    public FluidPipeNetwork(PressurizedPipe pipe) {
        super(pipe);
        this.networkCache = new PipeFluidCache();
        findGasTiles(pipe.getConnectedTiles());
    }

    public void findGasTiles(Map<Direction, TileEntity> tiles) {
        if (tiles != null && !tiles.isEmpty()) {
            for (Map.Entry<Direction, TileEntity> entry : tiles.entrySet()) {
                if (entry.getValue() != null && !entry.getValue().isRemoved()) {
                    entry.getValue().getCapability(GasCapability.GAS).ifPresent(cap -> {
                        if (!consumers.containsKey(entry.getValue())) {
                            consumers.put(entry.getValue(), new HashSet<Direction>(Collections.singleton(entry.getKey())));
                        } else {
                            consumers.get(entry.getValue()).add(entry.getKey());
                        }
                    });
                }
            }
        }
    }

    @Override
    public void update() {
        GasFlow flow = getCache().getFlow();
        for (Iterator<Map.Entry<TileEntity, Set<Direction>>> iterator = consumers.entrySet().iterator(); iterator.hasNext();) {
            Map.Entry<TileEntity, Set<Direction>> entry = iterator.next();
            TileEntity consumer = entry.getKey();
            Set<Direction> directions = entry.getValue();
            if (consumer.isRemoved()) {
                iterator.remove();
                continue;
            }
            if (flow.getProviders().contains(consumer)) {
                continue;
            }
            pushFluidToConsumer(consumer, directions);
        }
    }

    private void pushFluidToConsumer(TileEntity consumer, Set<Direction> directions) {
        for (Direction direction : directions) {
            Optional<IGasHandler> cap = consumer.getCapability(GasCapability.GAS, direction.getOpposite()).resolve();
            if (cap.isPresent()) {
                if (roundRobin) fillRoundRobin(cap.get(), this.consumers.size());
                else fillOrderly(cap.get());
            }
        }
    }

    private void fillRoundRobin(IGasHandler destination, float totalConsumers) {
        int drainSplit = Math.round(getCache().getFlow().getFlowInMB() / totalConsumers);
        int amount = getCache().getHandler().drain(this.getController(), drainSplit);
        if (amount != 0) {
            int fill = destination.fill(this.getController(), amount);
            getCache().getHandler().fill(this.getController(), amount - fill);
            Automaton.logger().info(fill);
        }
    }

    private void fillOrderly(IGasHandler destination) {
        int amount = getCache().getHandler().drain(this.getController(), getCache().getFlow().getFlowInMB());
        if (amount != 0) {
            int fill = destination.fill(this.getController(), amount);
            getCache().getHandler().fill(this.getController(), amount - fill);
            Automaton.logger().info(fill);
        }
    }

    public void setRoundRobin(boolean roundRobin) {
        this.roundRobin = roundRobin;
    }

    @Override
    protected void onNetworkAddFinish(TileEntity tileAdded) {
        if (tileAdded instanceof PressurizedPipe) {
            findGasTiles(((PressurizedPipe) tileAdded).getConnectedTiles());
        }
    }

    @Override
    public boolean isValid(TileEntity tile) {
        return tile instanceof PressurizedPipe;
    }

    @Override
    public PipeFluidCache getCache() {
        return (PipeFluidCache) super.getCache();
    }

    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (this.networkCache != null) {
            if (cap == GasCapability.GAS) {
                return getCache().getGasLazy().cast();
            }
        }
        return super.getCapability(cap, side);
    }



}
