package mod.kerzox.automaton.registry;

import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import mod.kerzox.automaton.common.block.AutomatonBlockBase;
import mod.kerzox.automaton.common.block.AutomatonBlockEntity;
import mod.kerzox.automaton.common.block.PressurizedFluidTankBlock;
import mod.kerzox.automaton.common.block.PressurizedPipeBlock;
import mod.kerzox.automaton.common.tile.PressurizedFluidTank;
import mod.kerzox.automaton.common.tile.base.AutomatonTile;
import mod.kerzox.automaton.common.tile.machines.Sawmill;
import mod.kerzox.automaton.common.tile.machines.TestGroupTile;
import mod.kerzox.automaton.common.tile.misc.CreativeGasProvider;
import mod.kerzox.automaton.common.tile.transfer.pipes.PressurizedPipe;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.block.material.Material;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.Items;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import static mod.kerzox.automaton.registry.AutomatonRegistry.*;

public class AutomatonMaterials {

    public static class Tiles {
        public static Block creativeGasProvider;
        public static Block gasTank;
        public static Block sawMill;
        public static Block testGroup;
    }

    public static class Pipes {
        public static Block lowPressurePipe;
    }

    public static void register() {
        fluids();
        blocks();
        items();
    }

    private static void blocks() {
        Tiles.creativeGasProvider = new AutomatonBlockEntity<>("creative_gas_provider_block",
                createTileEntity("creative_gas_provider",
                        buildType(() -> new CreativeGasProvider(Tiles.creativeGasProvider), () -> Tiles.creativeGasProvider)), AbstractBlock.Properties.of(Material.METAL));
        Tiles.gasTank = new PressurizedFluidTankBlock<>("pressurized_tank",
                TILE_ENTITIES.register("pressurized_tank_tile",
                buildType(() -> new PressurizedFluidTank(Tiles.gasTank), () -> Tiles.gasTank)),
                AbstractBlock.Properties.of(Material.METAL));
        Tiles.sawMill = new AutomatonBlockEntity<>("sawmill",
                TILE_ENTITIES.register("sawmill_tile",
                        buildType(() -> new Sawmill(Tiles.sawMill), () -> Tiles.sawMill)),
                AbstractBlock.Properties.of(Material.METAL));
        Pipes.lowPressurePipe = new PressurizedPipeBlock("low_pressure_pipe", createTileEntity("low_pressure_pipe", buildType(
                () -> new PressurizedPipe(Pipes.lowPressurePipe), ()-> Pipes.lowPressurePipe)), AbstractBlock.Properties.of(Material.METAL));
        Tiles.testGroup = new AutomatonBlockEntity<>("test_tile", createTileEntity("test_tile", buildType(
                () -> new TestGroupTile(Tiles.testGroup), ()-> Tiles.testGroup)), AbstractBlock.Properties.of(Material.METAL));
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
