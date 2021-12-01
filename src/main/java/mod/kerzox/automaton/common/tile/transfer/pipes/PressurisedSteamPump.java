package mod.kerzox.automaton.common.tile.transfer.pipes;

import mod.kerzox.automaton.Automaton;
import mod.kerzox.automaton.common.capabilities.gas.*;
import mod.kerzox.automaton.common.tile.base.AutomatonTickableTile;
import mod.kerzox.automaton.registry.AutomatonMaterials;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;
import java.util.Set;

public class PressurisedSteamPump extends AutomatonTickableTile<PressurisedSteamPump> {

    private GasConsumer gasTank = createPumpTank();
    private final LazyOptional<GasConsumer> handler = LazyOptional.of(() -> gasTank);
    private final Direction[] points = new Direction[2];

    public PressurisedSteamPump(Block block) {
        super(block);
    }

    @Override
    public void onServerTick() {

    }

    private int attemptToPushFluid(int amount) {
        if (gasTank.hasEnoughToProcess(null)) {
            this.gasTank.useFuel(null);
            TileEntity consumer = level.getBlockEntity(getBlockPos().relative(points[1]));
            if (consumer != null) {
                Optional<IGasHandler> cap = consumer.getCapability(GasCapability.GAS, points[1].getOpposite()).resolve();
                if (cap.isPresent()) {
                    return cap.get().fill(this, amount);
                }
            }
        }
        return amount;
    }

    private GasConsumer createPumpTank() {
        return new GasConsumer(AutomatonMaterials.Gases.EMPTY.get(), 25, 25) {

            private TileEntity cached;

            @Override
            public int fill(TileEntity te, int amount) {
                if (te == null) return 0;
                if (points[0] == null) {
                    points[0] = getDirectionOfTile(te);
                    points[1] = points[0].getOpposite();
                }
                if (cached != null && !cached.equals(te)) {
                    points[0] = getDirectionOfTile(te);
                    points[1] = points[0].getOpposite();
                }
                cached = te;
                return attemptToPushFluid(super.fill(te, amount));
            }
        };
    }

    private Direction getDirectionOfTile(TileEntity te) {
        for (Direction dir : Direction.values()) {
            TileEntity tile = level.getBlockEntity(getBlockPos().relative(dir));
            if (tile != null && tile.equals(te)) return dir;
        }
        return null;
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == GasCapability.GAS) {
            return handler.cast();
        }
        return super.getCapability(cap, side);
    }
}
