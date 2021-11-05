package mod.kerzox.automaton.common.capabilities.gas;

import mod.kerzox.automaton.Automaton;
import net.minecraft.block.AirBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.function.Predicate;

public class GasConsumer extends GasTank {

    enum MachineState {
        ACTIVE,
        DISABLED,
        BLOCKED
    }

    private MachineState state = MachineState.ACTIVE;
    private TileEntity tile;
    private Fluid lockedFluid;

    public GasConsumer(int capacity, @Nonnull  TileEntity tile, Fluid fluid) {
        super(capacity);
        this.tile = tile;
        this.lockedFluid = fluid;
    }

    @Override
    public boolean isFluidValid(int tank, @Nonnull FluidStack stack) {
        if (stack.getFluid() != lockedFluid) {
            return false;
        }
        return super.isFluidValid(tank, stack);
    }

    public void consumeGas(int amount, boolean simulate) {
        if (state != MachineState.ACTIVE) return;
        int current = this.getFluidAmount();
        FluidStack newStack = drain(amount, simulate ? FluidAction.SIMULATE : FluidAction.EXECUTE);

        if (newStack.getAmount() != current) {
            exhaust(amount);
        }

    }

    private void exhaust(int amount) {

        World level = this.tile.getLevel();

        if (level != null) {
            BlockPos position = this.tile.getBlockPos();

            if (!(level.getBlockState(position.above()).getBlock() instanceof AirBlock)) {
                this.state = MachineState.BLOCKED;
                Automaton.logger().info("Exhaust is blocked.");
                return;
            }

            List<Entity> entities = level.getEntitiesOfClass(LivingEntity.class, new AxisAlignedBB(position.above()));
            entities.forEach(e -> e.hurt(new DamageSource("voltageExplosion"), amount * 2 / 100f));

        }
    }

    /**
     * Client only method, will create a particle from the exhaust
     */

    public void exhaustParticle() {

    }
}
