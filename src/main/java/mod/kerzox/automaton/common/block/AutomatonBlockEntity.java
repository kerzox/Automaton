package mod.kerzox.automaton.common.block;

import mod.kerzox.automaton.common.capabilities.gas.GasCapability;
import mod.kerzox.automaton.common.capabilities.gas.IGasHandler;
import mod.kerzox.automaton.common.tile.base.AutomatonTile;
import mod.kerzox.automaton.common.tile.machines.Sawmill;
import mod.kerzox.automaton.common.util.IBlockIsEntity;
import mod.kerzox.automaton.common.util.INeighbourUpdatable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Items;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
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

    public static final DirectionProperty FACING = HorizontalBlock.FACING;
    private RegistryObject<TileEntityType<T>> tileType;

    public AutomatonBlockEntity(String registryName, RegistryObject<TileEntityType<T>> type, Properties properties) {
        super(registryName, true, properties);
        this.tileType = type;
    }

    @Override
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity playerEntity, Hand hand, BlockRayTraceResult hit) {
        if (!world.isClientSide) {
            TileEntity te = world.getBlockEntity(pos);
            if (hand == Hand.MAIN_HAND) {
                if (te != null) {
                 playerEntity.sendMessage(new StringTextComponent("Fluid amount: " + te.getCapability(GasCapability.GAS).map(IGasHandler::getStorage).orElse(0)), playerEntity.getUUID());
                }
            }
        }
        return super.use(state, world, pos, playerEntity, hand, hit);
    }

    @Override
    public void neighborChanged(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        super.neighborChanged(state, world, pos, block, fromPos, isMoving);
        TileEntity te = world.getBlockEntity(pos);
        if (te instanceof INeighbourUpdatable) {
            ((INeighbourUpdatable) te).updateByNeighbours(state, world.getBlockState(fromPos), pos, fromPos);
        }
    }


    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection());
    }

    @Override
    public BlockState rotate(BlockState state, Rotation p_185499_2_) {
        return state.setValue(FACING, p_185499_2_.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState p_185471_1_, Mirror p_185471_2_) {
        return p_185471_1_.rotate(p_185471_2_.getRotation(p_185471_1_.getValue(FACING)));
    }
    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> p_206840_1_) {
        super.createBlockStateDefinition(p_206840_1_);
        p_206840_1_.add(FACING);
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
