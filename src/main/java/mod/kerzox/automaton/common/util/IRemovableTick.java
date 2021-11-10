package mod.kerzox.automaton.common.util;

import net.minecraft.tileentity.TileEntity;

import java.util.HashSet;
import java.util.Set;

import static mod.kerzox.automaton.Automaton.toTick;

public interface IRemovableTick {

    Set<IRemovableTick> disabled = new HashSet<>();


    static void remove(IRemovableTick te) {
        te.kill();
    }

    void kill();


    static void add(TileEntity te) {
        if (te.getLevel() != null && !te.getLevel().isClientSide) {
            toTick.add(te);
        }
        ((IRemovableTick)te).createInstance();
    }

    boolean canTick();

    void createInstance();


    /**
     * Tick function
     * @return false when you want to stop ticking that block
     */

    boolean tick();
}
