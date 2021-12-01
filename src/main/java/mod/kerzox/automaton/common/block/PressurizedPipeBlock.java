package mod.kerzox.automaton.common.block;

import com.google.common.graph.EndpointPair;
import mod.kerzox.automaton.Automaton;
import mod.kerzox.automaton.common.multiblock.transfer.PipeNetwork;
import mod.kerzox.automaton.common.tile.transfer.TransferTile;
import mod.kerzox.automaton.common.tile.transfer.pipes.PressurizedPipe;
import mod.kerzox.automaton.common.util.INeighbourUpdatable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.RegistryObject;

import javax.annotation.Nullable;
import java.util.stream.Stream;

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
                    if (pipe.getNetwork() != null) {
                        playerEntity.sendMessage(new StringTextComponent("Controller: " + pipe.getNetwork()  + "\nLength: " + pipe.getNetwork().tiles().size()), playerEntity.getUUID());
                    }
//                        playerEntity.sendMessage(new StringTextComponent("EDGES:"), playerEntity.getUUID());
//                        for (EndpointPair<TransferTile> edge : pipe.getNetwork().getGraph().edges()) {
//                            playerEntity.sendMessage(new StringTextComponent(edge.nodeU().getBlockPos().toShortString() + " : " + edge.nodeV().getBlockPos().toShortString()), playerEntity.getUUID());
//                        }
//                        playerEntity.sendMessage(new StringTextComponent("NODES:"), playerEntity.getUUID());
//                        playerEntity.sendMessage(new StringTextComponent("size: " + pipe.getNetwork().getGraph().nodes().size()), playerEntity.getUUID());
//                        for (TransferTile node : pipe.getNetwork().getGraph().nodes()) {
//                            playerEntity.sendMessage(new StringTextComponent(node.getBlockState().getBlock().getRegistryName().toString()), playerEntity.getUUID());
//                        }
//                        playerEntity.sendMessage(new StringTextComponent(String.format("Controller: %sPipe length: %d\nMaster tile: %s",
//                                pipe.getNetwork().toString(), + pipe.getNetwork().getGraph().nodes().size(),
//                                pipe.getNetwork().getControllerTile() == pipe ? "MASTER" : pipe.getNetwork().getControllerTile().getBlockPos().toShortString())), playerEntity.getUUID());
//                    }
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
            pipe.updateByNeighbours(state, worldIn.getBlockState(pos), pos, null);
        }
    }

    @Override
    public void neighborChanged(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        super.neighborChanged(state, world, pos, block, fromPos, isMoving);
//        TileEntity te = world.getBlockEntity(pos);
//        if (te instanceof INeighbourUpdatable) {
//            ((INeighbourUpdatable) te).updateByNeighbours(state, world.getBlockState(fromPos), pos, fromPos);
//        }

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

    @OnlyIn(Dist.CLIENT)
    public float getShadeBrightness(BlockState p_220080_1_, IBlockReader p_220080_2_, BlockPos p_220080_3_) {
        return 1.0F;
    }

    public boolean propagatesSkylightDown(BlockState p_200123_1_, IBlockReader p_200123_2_, BlockPos p_200123_3_) {
        return true;
    }
}
