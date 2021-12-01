package mod.kerzox.automaton.common.block;

import mod.kerzox.automaton.common.tile.transfer.conveyor.ConveyorBelt;
import mod.kerzox.automaton.common.tile.transfer.pipes.PressurizedPipe;
import mod.kerzox.automaton.common.util.INeighbourUpdatable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.RegistryObject;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.minecraftforge.common.util.Constants.BlockFlags.BLOCK_UPDATE;
import static net.minecraftforge.common.util.Constants.BlockFlags.NOTIFY_NEIGHBORS;

public class ConveyorBeltBlock extends AutomatonInvisibleBlockEntity<ConveyorBelt>{

    public static final BooleanProperty LEG_RENDER = BooleanProperty.create("leg_render");

    public static final EnumProperty<ConnectiveSides> WEST = EnumProperty.create("west", ConnectiveSides.class);
    public static final EnumProperty<ConnectiveSides> EAST = EnumProperty.create("east", ConnectiveSides.class);
    public static final EnumProperty<ConnectiveSides> NORTH = EnumProperty.create("north", ConnectiveSides.class);
    public static final EnumProperty<ConnectiveSides> SOUTH = EnumProperty.create("south", ConnectiveSides.class);

    public enum ConnectiveSides implements IStringSerializable {
        NONE("none"),
        CONNECTED("connected");


        private final String name;

        ConnectiveSides(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }


        @Override
        public String getSerializedName() {
            return getName();
        }
    }


    public ConveyorBeltBlock(String registryName, RegistryObject<TileEntityType<ConveyorBelt>> type, Properties properties) {
        super(registryName, type, properties);
        this.registerDefaultState(stateDefinition.any()
                .setValue(NORTH, ConnectiveSides.NONE)
                .setValue(EAST, ConnectiveSides.NONE)
                .setValue(SOUTH, ConnectiveSides.NONE)
                .setValue(WEST, ConnectiveSides.NONE));
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> p_206840_1_) {
        super.createBlockStateDefinition(p_206840_1_);
        p_206840_1_.add(NORTH, SOUTH, WEST, EAST, LEG_RENDER);
    }

    @Override
    public void setPlacedBy(World worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(worldIn, pos, state, placer, stack);
        TileEntity te = worldIn.getBlockEntity(pos);
        if (te instanceof INeighbourUpdatable) {
            ((INeighbourUpdatable) te).updateByNeighbours(state, worldIn.getBlockState(pos), pos, null);
        }
    }

    @Override
    public void neighborChanged(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        super.neighborChanged(state, world, pos, block, fromPos, isMoving);
        TileEntity te = world.getBlockEntity(pos);
        if (te instanceof INeighbourUpdatable) {
            ((INeighbourUpdatable) te).updateByNeighbours(state, world.getBlockState(fromPos), pos, fromPos);
        }
    }

}
