package mod.kerzox.automaton.common.block;

import mod.kerzox.automaton.Automaton;
import mod.kerzox.automaton.common.multiblock.transfer.TransferController;
import mod.kerzox.automaton.common.tile.PressurizedFluidTank;
import mod.kerzox.automaton.common.tile.base.AutomatonTile;
import mod.kerzox.automaton.common.tile.transfer.pipes.PressurizedPipe;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.BucketItem;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fml.RegistryObject;

import javax.annotation.Nullable;
import java.util.Set;
import java.util.stream.Stream;

import static net.minecraftforge.fluids.capability.IFluidHandler.FluidAction.EXECUTE;

public class PressurizedPipeBlock extends AutomatonBlockEntity<PressurizedPipe> {

    public static final VoxelShape PIPE = Stream.of(
            Block.box(1, 8, 5, 15, 11, 11),
            Block.box(15, 7, 4, 16, 10, 12),
            Block.box(15, 14, 4, 16, 17, 12),
            Block.box(15, 8, 4, 16, 11, 5),
            Block.box(15, 8, 11, 16, 11, 12),
            Block.box(0, 7, 4, 1, 10, 12),
            Block.box(0, 14, 4, 1, 17, 12),
            Block.box(0, 8, 4, 1, 11, 5),
            Block.box(0, 8, 11, 1, 11, 12)
    ).reduce((v1, v2) -> VoxelShapes.join(v1, v2, IBooleanFunction.OR)).get();

    public PressurizedPipeBlock(String registryName, RegistryObject<TileEntityType<PressurizedPipe>> type, Properties properties) {
        super(registryName, type, properties.isViewBlocking(PressurizedPipeBlock::never).noOcclusion());
    }

    static boolean never(BlockState state, IBlockReader iBlockReader, BlockPos pos) {
        return false;
    }

    @Override
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity playerEntity, Hand hand, BlockRayTraceResult hit) {
        if (!world.isClientSide) {
            TileEntity te = world.getBlockEntity(pos);
            if (te instanceof PressurizedPipe) {
                PressurizedPipe pipe = (PressurizedPipe) te;
                if (hand == Hand.MAIN_HAND) {
                    if (pipe.hasController()) {
                        playerEntity.sendMessage(new StringTextComponent("Controller ID: " + pipe.getController().getId() + "\nPipe length: "  +pipe.getController().getNetwork().size() +"\nPos: " + pipe.getBlockPos().toShortString()), playerEntity.getUUID());
                    }
                }
            }
        }
        return super.use(state, world, pos, playerEntity, hand, hit);
    }

    @Override
    public void setPlacedBy(World worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(worldIn, pos, state, placer, stack);
        TileEntity te = worldIn.getBlockEntity(pos);
        if (te instanceof PressurizedPipe) {
            PressurizedPipe pipe = (PressurizedPipe) te;
            pipe.doNeighbourConnection();
        }
    }

    @Override
    public void neighborChanged(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        super.neighborChanged(state, world, pos, block, fromPos, isMoving);
        TileEntity te = world.getBlockEntity(pos);
        if (te instanceof PressurizedPipe) {
            PressurizedPipe pipe = (PressurizedPipe) te;
            pipe.doNeighbourConnection();
        }

    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> p_206840_1_) {
        p_206840_1_.add(BlockStateProperties.FACING);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return defaultBlockState().setValue(BlockStateProperties.FACING, context.getNearestLookingDirection().getOpposite());
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader reader, BlockPos pos, ISelectionContext context) {

        VoxelShape base = Block.box(5, 5, 5, 11, 11, 11);

        TileEntity te = reader.getBlockEntity(pos);
        if (te instanceof PressurizedPipe) {
            Set<Direction> directions = ((PressurizedPipe) te).getConnections();
            if (!directions.isEmpty()) {
                VoxelShape connector = Block.box(5, 5, 0, 11, 11, 11);
                VoxelShape connection = Block.box(12, 12, 0, 10, 12, 1);
                connector = VoxelShapes.join(connector, connection, IBooleanFunction.OR);
                return VoxelShapes.join(base, connector, IBooleanFunction.OR);
            }
        }
        return base;
    }

    @OnlyIn(Dist.CLIENT)
    public float getShadeBrightness(BlockState p_220080_1_, IBlockReader p_220080_2_, BlockPos p_220080_3_) {
        return 1.0F;
    }

    public boolean propagatesSkylightDown(BlockState p_200123_1_, IBlockReader p_200123_2_, BlockPos p_200123_3_) {
        return true;
    }
}
