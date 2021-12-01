package mod.kerzox.automaton.common.capabilities.gas;

import mod.kerzox.automaton.common.capabilities.IStrictSided;
import net.minecraft.util.Direction;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class GasTank extends GasStorage implements IStrictSided {

    protected Set<Direction> inputSides = new HashSet<>();
    protected Set<Direction> outputSides = new HashSet<>();

    public GasTank(Gas gas, int capacity, int inital) {
        super(gas, capacity, inital);
    }

    public GasTank(Gas gas, int capacity) {
        super(gas, capacity, 0);
    }

    @Override
    public boolean checkSide(Direction dir, SIDE sideIn) {
        return sideIn == SIDE.OUTPUT ? outputSides.contains(dir) : inputSides.contains(dir);
    }

    @Override
    public void addInput(Direction... dir) {
        this.inputSides.addAll(Arrays.asList(dir));
    }

    @Override
    public void addOutput(Direction... dir) {
        this.outputSides.addAll(Arrays.asList(dir));
    }

    @Override
    public void removeInput(Direction... dir) {
        Arrays.asList(dir).forEach(this.inputSides::remove);
    }

    @Override
    public void removeOutput(Direction... dir) {
        Arrays.asList(dir).forEach(this.outputSides::remove);
    }
}
