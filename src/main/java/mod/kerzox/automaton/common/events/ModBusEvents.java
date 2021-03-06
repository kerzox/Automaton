package mod.kerzox.automaton.common.events;

import mod.kerzox.automaton.client.loader.AutomatonOBJLoader;
import mod.kerzox.automaton.client.loader.reflection.OBJModelHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import static mod.kerzox.automaton.client.render.ter.PressurizedPipeRender.*;

public class ModBusEvents {

    @SubscribeEvent
    public void newRegistry(RegistryEvent.NewRegistry e) {

    }

    @SubscribeEvent
    public void modelBake(ModelBakeEvent event) {
        ModelLoader.addSpecialModel(new ResourceLocation("automaton", "block/assembly_robot_model"));
    }

    @SubscribeEvent
    public void modelRegister(ModelRegistryEvent event) {
        try {
            OBJModelHelper.doReflection();
        } catch (NoSuchFieldException | ClassNotFoundException | IllegalAccessException | NoSuchMethodException e) {
            e.printStackTrace();
        }

        //ModelLoaderRegistry.registerLoader(new ResourceLocation("automaton","obj"), AutomatonOBJLoader.instance);
        ModelLoaderRegistry.registerLoader(new ResourceLocation("automaton","obj"), AutomatonOBJLoader.instance);

        ModelLoader.addSpecialModel(new ResourceLocation("automaton", "block/assembly_robot_model"));
        ModelLoader.addSpecialModel(new ResourceLocation("automaton", "block/conveyor_belt_model"));
        ModelLoader.addSpecialModel(PIPE_CONNECTION_RESOURCE);
        ModelLoader.addSpecialModel(PIPE_RESOURCE);
        ModelLoader.addSpecialModel(PIPE_CORNER_RESOURCE);
        ModelLoader.addSpecialModel(PIPE_CORNER_TEST_RESOURCE);
    }

}
