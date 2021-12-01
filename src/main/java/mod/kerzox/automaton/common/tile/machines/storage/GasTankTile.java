package mod.kerzox.automaton.common.tile.machines.storage;

import mod.kerzox.automaton.Automaton;
import mod.kerzox.automaton.common.capabilities.gas.GasCapability;
import mod.kerzox.automaton.common.capabilities.gas.GasTank;
import mod.kerzox.automaton.common.capabilities.gas.IGasHandler;
import mod.kerzox.automaton.common.tile.base.AutomatonTickableTile;
import mod.kerzox.automaton.registry.AutomatonMaterials;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import java.util.Optional;

public class GasTankTile extends AutomatonTickableTile<GasTankTile> {

    private final GasTank storage = new mod.kerzox.automaton.common.capabilities.gas.GasTank(AutomatonMaterials.Gases.STEAM.get(), 15000, 15000);
    private final LazyOptional<GasTank> lazyOptional = LazyOptional.of(() -> storage);

    public GasTankTile(Block block) {
        super(block);
        this.storage.addInput(Direction.values());
        this.storage.addOutput(Direction.UP, Direction.DOWN);

    }

    private void pushFluidToConsumers() {
        if (!storage.isEmpty()) {
            for (Direction dir : Direction.values()) {
                TileEntity te = getLevel().getBlockEntity(worldPosition.relative(dir));
                if (te != null) {
                    Optional<IGasHandler> cap = te.getCapability(GasCapability.GAS, dir.getOpposite()).resolve();
                    if (cap.isPresent()) {
                        int amount = storage.drain(this, 100);
                        if (amount != 0) {
                            int fill = cap.get().fill(this, amount);
                            storage.fill(this, amount - fill);
                            Automaton.logger().info(fill);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onServerTick() {
        if (this.storage.getGas() == AutomatonMaterials.Gases.EMPTY.get()) return;
        pushFluidToConsumers();
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap) {
        if (cap == GasCapability.GAS) {
            return lazyOptional.cast();
        }
        return super.getCapability(cap);
    }
}
