package mod.kerzox.automaton.registry;

import mod.kerzox.automaton.Automaton;
import mod.kerzox.automaton.common.capabilities.gas.Gas;
import net.minecraft.block.Block;
import net.minecraft.fluid.Fluid;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;

import java.util.function.Supplier;

import static org.apache.commons.lang3.ClassUtils.getName;

public class AutomatonRegistry {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Automaton.modid());
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Automaton.modid());
    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(ForgeRegistries.FLUIDS, Automaton.modid());
    public static final DeferredRegister<TileEntityType<?>> TILE_ENTITIES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, Automaton.modid());
    public static final DeferredRegister<ContainerType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.CONTAINERS, Automaton.modid());
    public static final DeferredRegister<IRecipeSerializer<?>> RECIPES = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, Automaton.modid());

    public static final DeferredRegister<Gas> GAS = DeferredRegister.create(Gas.class, Automaton.modid());
    public static final Supplier<IForgeRegistry<Gas>> GAS_REGISTRY = GAS.makeRegistry("gas_registry",
            () -> new RegistryBuilder<Gas>().setMaxID(Integer.MAX_VALUE - 1).onAdd(((owner, stage, id, obj, oldObj) -> {
                Automaton.logger().info("New gas added: " + obj.getName() + " ");
            })).setDefaultKey(new ResourceLocation(Automaton.modid(), "null")));

    public static void init() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        BLOCKS.register(bus);
        ITEMS.register(bus);
        TILE_ENTITIES.register(bus);
        FLUIDS.register(bus);
        CONTAINERS.register(bus);
        RECIPES.register(bus);
        GAS.register(bus);
        Automaton.logger().info("Automaton: Registry events");

        AutomatonMaterials.register();
    }

}
