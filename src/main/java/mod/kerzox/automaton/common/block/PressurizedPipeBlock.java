package mod.kerzox.automaton.common.block;

import mod.kerzox.automaton.common.tile.base.AutomatonTile;
import mod.kerzox.automaton.common.tile.transfer.pipes.PressurizedPipe;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.RegistryObject;

public class PressurizedPipeBlock extends AutomatonBlockEntity<PressurizedPipe> {

    public PressurizedPipeBlock(String registryName, RegistryObject<TileEntityType<PressurizedPipe>> type, Properties properties) {
        super(registryName, type, properties);
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

}
