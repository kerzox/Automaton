package mod.kerzox.automaton.common.tile.machines;

import mod.kerzox.automaton.Automaton;
import mod.kerzox.automaton.common.tile.base.AutomatonFactory;
import mod.kerzox.automaton.common.tile.base.AutomatonTickableTile;
import mod.kerzox.automaton.common.util.FactoryTile;
import mod.kerzox.automaton.common.util.FactoryType;
import net.minecraft.block.Block;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static mod.kerzox.automaton.registry.FluidRegistrar.ALL_FLUIDS;

public class Sawmill extends AutomatonFactory<Sawmill> {

    public Sawmill(Block block, FactoryType type) {
        super(block, type);
    }

}
