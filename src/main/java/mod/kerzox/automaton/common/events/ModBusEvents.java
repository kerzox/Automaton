package mod.kerzox.automaton.common.events;

import mod.kerzox.automaton.client.loader.AutomatonOBJ;
import mod.kerzox.automaton.client.loader.AutomatonOBJLoader;
import mod.kerzox.automaton.client.loader.reflection.OBJModelHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import static mod.kerzox.automaton.client.render.ter.PressurizedPipeRender.*;

public class ModBusEvents {

    @SubscribeEvent
    public void modelBake(ModelBakeEvent event) {

    }

    @SubscribeEvent
    public void modelRegister(ModelRegistryEvent event) {
        //ModelLoaderRegistry.registerLoader(new ResourceLocation("automaton","obj"), SplitOBJLoader.instance);
        ModelLoaderRegistry.registerLoader(new ResourceLocation("automaton","obj"), AutomatonOBJLoader.instance);

        ModelLoader.addSpecialModel(PIPE_CONNECTION_RESOURCE);
        ModelLoader.addSpecialModel(PIPE_RESOURCE);
        ModelLoader.addSpecialModel(PIPE_CORNER_RESOURCE);
        ModelLoader.addSpecialModel(PIPE_CORNER_TEST_RESOURCE);
        try {
            OBJModelHelper.doReflection();
        } catch (NoSuchFieldException | ClassNotFoundException | IllegalAccessException | NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

}
