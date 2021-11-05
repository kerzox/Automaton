package mod.kerzox.automaton.registry;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.block.material.Material;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.fml.RegistryObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class FluidRegistrar {

    /**
     * Honestly this was done terribly lmao, defiantly don't think this is how you are suppose to do it
     */

    public static class FluidObject {
        private String name;
        private RegistryObject<ForgeFlowingFluid.Flowing> flowing;
        private RegistryObject<ForgeFlowingFluid.Source> still;
        private RegistryObject<FlowingFluidBlock> block;
        private RegistryObject<Item> item;

        public FluidObject(String name,
                           RegistryObject<FlowingFluidBlock> block,
                           RegistryObject<Item> item,
                           RegistryObject<ForgeFlowingFluid.Flowing> flowing,
                           RegistryObject<ForgeFlowingFluid.Source> still) {
            this.name = name;
            this.flowing = flowing;
            this.still = still;
            this.block = block;
            this.item = item;
        }

        public String getName() {
            return name;
        }

        public RegistryObject<?> getBlock() {
            return block;
        }

        public RegistryObject<?> getItem() {
            return item;
        }

        public RegistryObject<ForgeFlowingFluid.Flowing> getFlowing() {
            return this.flowing;
        }

        public RegistryObject<ForgeFlowingFluid.Source> getStill() {
            return this.still;
        }

        public ForgeFlowingFluid.Source getFluid() {
            return getStill().get();
        }

    }

    public static Map<String, FluidObject> ALL_FLUIDS = new HashMap<>();

    private static RegistryObject<ForgeFlowingFluid.Flowing> FLOWING;
    private static RegistryObject<ForgeFlowingFluid.Source> STILL;

    public static void CreateFluid(String name, int tint, int temp, int viscosity, int density, boolean gas) {

        RegistryObject<FlowingFluidBlock> block = AutomatonRegistry.BLOCKS.register(name, () -> new FlowingFluidBlock(STILL, AbstractBlock.Properties.of(Material.WATER).strength(100.0F).noDrops()));

        RegistryObject<Item> bucket = AutomatonRegistry.ITEMS.register(name, () ->
                new BucketItem(STILL, new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1).tab(ItemGroup.TAB_MISC)));

        ForgeFlowingFluid.Properties properties = properties(
                name, () -> STILL.get(), () -> FLOWING.get(),
                tint,
                temp,
                viscosity,
                density,
                gas)
                .block(block)
                .bucket(bucket);
        FLOWING = fluid(name+"flowing", () -> new ForgeFlowingFluid.Flowing(properties));
        STILL = fluid(name, () -> new ForgeFlowingFluid.Source(properties));

        ALL_FLUIDS.put(name, new FluidObject(name, block, bucket, FLOWING, STILL));
    }

    private static ForgeFlowingFluid.Properties properties(String name, Supplier<Fluid> still, Supplier<Fluid> flowing,
                                                           int tint, int temp, int viscosity, int density, boolean gas) {
        String block = "block/" + name;
        return gas ? new ForgeFlowingFluid.Properties(still, flowing, FluidAttributes.builder(new ResourceLocation("minecraft:block/water_still"),
                new ResourceLocation("minecraft:block/water_flow"))
                .gaseous()
                .color(tint)
                .temperature(temp)
                .viscosity(viscosity)
                .density(density))
                : new ForgeFlowingFluid.Properties(still, flowing, FluidAttributes.builder(new ResourceLocation("minecraft:block/water_still"),
                new ResourceLocation("minecraft:block/water_flow"))
                .color(tint)
                .temperature(temp)
                .viscosity(viscosity)
                .density(density));
    }


    private static <T extends Fluid> RegistryObject<T> fluid(String name, Supplier<T> fluid) {
        return AutomatonRegistry.FLUIDS.register(name, fluid);
    }

}
