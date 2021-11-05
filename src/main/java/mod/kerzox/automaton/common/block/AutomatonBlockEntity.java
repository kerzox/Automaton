package mod.kerzox.automaton.common.block;

import mod.kerzox.automaton.Automaton;
import mod.kerzox.automaton.common.capabilities.gas.GasConsumer;
import mod.kerzox.automaton.common.tile.PressurizedFluidTank;
import mod.kerzox.automaton.common.tile.base.AutomatonTile;
import mod.kerzox.automaton.common.tile.machines.Sawmill;
import mod.kerzox.automaton.common.util.IBlockIsEntity;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Items;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fml.RegistryObject;

import javax.annotation.Nullable;

import static mod.kerzox.automaton.registry.FluidRegistrar.ALL_FLUIDS;
import static net.minecraftforge.fluids.capability.IFluidHandler.FluidAction.EXECUTE;

public class AutomatonBlockEntity<T extends AutomatonTile<? super T>> extends AutomatonBlockBase implements IBlockIsEntity<T> {

    private RegistryObject<TileEntityType<T>> tileType;

    public AutomatonBlockEntity(String registryName, RegistryObject<TileEntityType<T>> type, Properties properties) {
        super(registryName, true, properties);
        this.tileType = type;
    }

    @Override
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity playerEntity, Hand hand, BlockRayTraceResult hit) {
        if (!world.isClientSide) {
            TileEntity te = world.getBlockEntity(pos);
            if (te instanceof Sawmill) {
                if (hand == Hand.MAIN_HAND) {
                    Sawmill sm = (Sawmill) te;
                    if (playerEntity.getMainHandItem().getItem() == Items.STICK) {
                        te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY).ifPresent(cap -> {
                            GasConsumer consumer = (GasConsumer) cap;
                            playerEntity.sendMessage(new StringTextComponent(String.format("Current fluid: %s amount: %d", consumer.getFluid().getFluid().getRegistryName(), consumer.getFluidAmount())), playerEntity.getUUID());
                        });
                        return super.use(state, world, pos, playerEntity, hand, hit);
                    }
                    te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY).ifPresent(cap -> {
                        cap.fill(new FluidStack(ALL_FLUIDS.get(0).getStill().get(), 1000), EXECUTE);
                    });
                }
            }
        }
        return super.use(state, world, pos, playerEntity, hand, hit);
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return tileType.get().create();
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Override
    public TileEntityType<? extends T> getEntityType() {
        return tileType.get();
    }
}
