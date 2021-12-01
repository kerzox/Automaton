package mod.kerzox.automaton.common.block;

import mod.kerzox.automaton.common.tile.base.AutomatonTile;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.RegistryObject;

import javax.annotation.Nullable;

public class AutomatonInvisibleBlockEntity<T extends AutomatonTile<T>> extends AutomatonBlockEntity<T> {

    public AutomatonInvisibleBlockEntity(String registryName, RegistryObject<TileEntityType<T>> type, Properties properties) {
        super(registryName, type, properties.isViewBlocking(PressurizedPipeBlock::never).noOcclusion());
    }

    static boolean never(BlockState state, IBlockReader iBlockReader, BlockPos pos) {
        return false;
    }

    @OnlyIn(Dist.CLIENT)
    public float getShadeBrightness(BlockState p_220080_1_, IBlockReader p_220080_2_, BlockPos p_220080_3_) {
        return 1.0F;
    }

    public boolean propagatesSkylightDown(BlockState p_200123_1_, IBlockReader p_200123_2_, BlockPos p_200123_3_) {
        return true;
    }
}
