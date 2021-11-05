package mod.kerzox.automaton.common.block;

import mod.kerzox.automaton.common.tile.PressurizedFluidTank;
import mod.kerzox.automaton.common.tile.base.AutomatonTile;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BucketItem;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fml.RegistryObject;

import static net.minecraftforge.fluids.capability.IFluidHandler.FluidAction.EXECUTE;

public class PressurizedFluidTankBlock<T extends AutomatonTile<? super T>> extends AutomatonBlockEntity<T> {

    public PressurizedFluidTankBlock(String registryName, RegistryObject<TileEntityType<T>> type, Properties properties) {
        super(registryName, type, properties);
    }

    @Override
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity playerEntity, Hand hand, BlockRayTraceResult hit) {
        if (!world.isClientSide) {
            TileEntity te = world.getBlockEntity(pos);
            if (te instanceof PressurizedFluidTank) {
                if (playerEntity.getMainHandItem().getItem() instanceof BucketItem) {
                    BucketItem bucket = (BucketItem) playerEntity.getMainHandItem().getItem();
                    PressurizedFluidTank tank = (PressurizedFluidTank) te;
                    tank.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY).ifPresent(cap -> {
                        cap.fill(new FluidStack(bucket.getFluid(), 30000), EXECUTE);
                    });
                }
            }
        }
        return super.use(state, world, pos, playerEntity, hand, hit);
    }
}
