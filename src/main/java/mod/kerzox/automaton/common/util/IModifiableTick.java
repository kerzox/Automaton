package mod.kerzox.automaton.common.util;

import java.util.HashSet;
import java.util.Set;

public interface IModifiableTick {

    static Set<IModifiableTick> tickingEntities = new HashSet<>();

    boolean tick();
    boolean destroy();
    void setDestroy(boolean destroy);

    default boolean doTick() {
        return !destroy() || tick();
    }

    default void add(IModifiableTick tile) {
        tickingEntities.add(tile);
    }

}
