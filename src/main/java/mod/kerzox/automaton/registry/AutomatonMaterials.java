package mod.kerzox.automaton.registry;

import com.google.common.collect.ImmutableSet;
import mod.kerzox.automaton.common.block.*;
import mod.kerzox.automaton.common.capabilities.gas.Gas;
import mod.kerzox.automaton.common.tile.machines.assembly.AssemblyRobot;
import mod.kerzox.automaton.common.tile.machines.storage.GasTankTile;
import mod.kerzox.automaton.common.tile.machines.Sawmill;
import mod.kerzox.automaton.common.tile.machines.TestGroupTile;
import mod.kerzox.automaton.common.tile.transfer.conveyor.ConveyorBelt;
import mod.kerzox.automaton.common.tile.transfer.pipes.PressurizedPipe;
import mod.kerzox.automaton.common.util.FactoryTile;
import mod.kerzox.automaton.common.util.FactoryType;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;

import java.util.function.Consumer;
import java.util.function.Supplier;

import static mod.kerzox.automaton.registry.AutomatonRegistry.GAS;

public class AutomatonMaterials {

    public static class Tiles {
        public static Block creativeGasProvider;
        public static Block gasTank;
        public static Block conveyorBelt;
        public static Block testGroup;
        public static Block assemblyRobot;
        public static Block[] sawMill;

    }

    public static class Pipes {
        public static Block lowPressurePipe;
        public static Block lowPressurePipeTest;
    }

    public static class Gases {
        static void init() {

        }

        public static RegistryObject<Gas> EMPTY = GAS.register("empty", () -> new Gas("empty", 0, 0));
        public static RegistryObject<Gas> STEAM = GAS.register("steam", () -> new Gas("steam", 300, 300));
    }

    public static void register() {
        fluids();
        blocks();
        items();
        Gases.init();
    }

    private static void blocks() {
        Tiles.gasTank = new AutomatonBlockEntity<>("gas_tank", createTileEntity("gas_tank_tile", buildType(
                () -> new GasTankTile(Tiles.gasTank), () -> Tiles.gasTank)), AbstractBlock.Properties.of(Material.METAL));
        Pipes.lowPressurePipe = new PressurizedPipeBlock("low_pressure_pipe", createTileEntity("low_pressure_pipe", buildType(
                () -> new PressurizedPipe(Pipes.lowPressurePipe), ()-> Pipes.lowPressurePipe)), AbstractBlock.Properties.of(Material.METAL));
        Tiles.testGroup = new AutomatonBlockEntity<>("test_tile", createTileEntity("test_tile", buildType(
                () -> new TestGroupTile(Tiles.testGroup), ()-> Tiles.testGroup)), AbstractBlock.Properties.of(Material.METAL));
        Tiles.conveyorBelt = new ConveyorBeltBlock("conveyor_belt", createTileEntity("conveyor_belt", buildType(
                () -> new ConveyorBelt(Tiles.conveyorBelt), ()-> Tiles.conveyorBelt)), AbstractBlock.Properties.of(Material.METAL));
        Tiles.assemblyRobot = new AssemblyRobotBlock("assembly_robot", createTileEntity("assembly_robot", buildType(
                () -> new AssemblyRobot(Tiles.assemblyRobot), ()-> Tiles.assemblyRobot)), AbstractBlock.Properties.of(Material.METAL));

        Tiles.sawMill = AutomatonFactoryEntity.builder(
                new String[] {"sawmill_coal", "sawmill_steam", "sawmill_electric"},
                AbstractBlock.Properties.of(Material.METAL),
                new RegistryObject[] {
                        createTileEntity("sawmill_coal", buildType(() -> new Sawmill(Tiles.sawMill[0], FactoryType.COAL), () -> Tiles.sawMill[0])),
                        createTileEntity("sawmill_steam", buildType(() -> new Sawmill(Tiles.sawMill[1], FactoryType.STEAM), () -> Tiles.sawMill[1])),
                        createTileEntity("sawmill_electric", buildType(() -> new Sawmill(Tiles.sawMill[2], FactoryType.ELECTRIC), () -> Tiles.sawMill[2])),
                });

    }

    private static void items() {

    }

    private static void fluids() {
        FluidRegistrar.CreateFluid("steam", 0xFFFFFF, 150, 2250, 1000, true);
    }

    private static <T extends TileEntityType<?>> RegistryObject<T> createTileEntity(String name, Supplier<T> block) {
        return AutomatonRegistry.TILE_ENTITIES.register(name, block);
    }

    private static <T extends TileEntity> Supplier<TileEntityType<T>> buildType(Supplier<T> entity, Supplier<Block> validBlocks) {
        return () -> new TileEntityType<T>(entity, ImmutableSet.of(validBlocks.get()), null);
    }


}
