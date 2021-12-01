package mod.kerzox.automaton.common.util;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public interface INeighbourUpdatable {

    void updateByNeighbours(BlockState state, BlockState neighbourState, BlockPos pos, BlockPos neighbour);

}
