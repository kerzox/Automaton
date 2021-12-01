package mod.kerzox.automaton.common.block;

import mod.kerzox.automaton.common.tile.machines.assembly.AssemblyRobot;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.fml.RegistryObject;

public class AssemblyRobotBlock extends AutomatonInvisibleBlockEntity<AssemblyRobot> {

    public AssemblyRobotBlock(String registryName, RegistryObject<TileEntityType<AssemblyRobot>> type, Properties properties) {
        super(registryName, type, properties);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {

        Direction facing = state.getValue(FACING);

        return VoxelShapes.block();
    }
}
