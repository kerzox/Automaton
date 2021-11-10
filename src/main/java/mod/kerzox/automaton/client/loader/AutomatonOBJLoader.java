package mod.kerzox.automaton.client.loader;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.IResourceManager;
import net.minecraftforge.client.model.IModelLoader;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.client.model.obj.OBJModel;

import java.util.HashMap;
import java.util.Map;

public class AutomatonOBJLoader implements IModelLoader<AutomatonOBJ> {

    private static Map<String, AutomatonBakedOBJ> CACHED_TEST_BAKED = new HashMap<>();
    public static final AutomatonOBJLoader instance = new AutomatonOBJLoader();
    private IResourceManager manager = Minecraft.getInstance().getResourceManager();

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) {
        CACHED_TEST_BAKED.clear();
    }

    @Override
    public AutomatonOBJ read(JsonDeserializationContext deserializationContext, JsonObject modelContents) {
        OBJModel model = OBJLoader.INSTANCE.read(deserializationContext, modelContents);
        return new AutomatonOBJ(
                model
        );
    }

    public static void addModel(AutomatonBakedOBJ model) {
        CACHED_TEST_BAKED.put("test", model);
    }

    public static AutomatonBakedOBJ getModelByName(String name) {
        return CACHED_TEST_BAKED.get(name);
    }
}
