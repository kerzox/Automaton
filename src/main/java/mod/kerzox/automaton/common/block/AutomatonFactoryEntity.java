package mod.kerzox.automaton.common.block;

import com.google.common.collect.ImmutableSet;
import mod.kerzox.automaton.common.tile.base.AutomatonTile;
import mod.kerzox.automaton.common.tile.machines.assembly.AssemblyRobot;
import mod.kerzox.automaton.common.util.FactoryTile;
import mod.kerzox.automaton.common.util.FactoryType;
import mod.kerzox.automaton.registry.AutomatonMaterials;
import mod.kerzox.automaton.registry.AutomatonRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.RegistryObject;

import java.util.function.Supplier;

public class AutomatonFactoryEntity<T extends AutomatonTile<T>> extends AutomatonBlockEntity<T> {

    private AutomatonFactoryEntity(String name, RegistryObject<TileEntityType<T>> type, Properties properties) {
        super(name, type, properties);
    }

    public static AutomatonFactoryEntity[] builder(String[] registryName, Properties properties, RegistryObject<TileEntityType<?>>[] types) {
        AutomatonFactoryEntity[] ret = new AutomatonFactoryEntity[types.length];
        for (int i = 0; i < types.length; i++) {
            ret[i] = new AutomatonFactoryEntity(registryName[i], types[i], properties);
        }
        return ret;
    }

}
