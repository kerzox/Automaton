package mod.kerzox.automaton.common.util;

import net.minecraft.tileentity.TileEntity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public interface IRemovableTick {

    Set<TileEntity> toRemove = new HashSet<>();
    Set<TileEntity> toTick = new HashSet<>();

    static void remove(TileEntity te) {
        toRemove.add(te);
    }
    static void add(TileEntity te) {
        toTick.add(te);
    }

    /**
     * Tick function
     * @return false when you want to stop ticking that block
     */

    boolean tick();
}
