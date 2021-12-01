package mod.kerzox.automaton.common.capabilities;

import net.minecraft.util.Direction;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public interface IStrictSided {

    enum SIDE {
        INPUT, OUTPUT
    }

    boolean checkSide(Direction dir, SIDE sideIn);
    void addInput(Direction... dir);
    void addOutput(Direction... dir);
    void removeInput(Direction... dir);
    void removeOutput(Direction... dir);

}
