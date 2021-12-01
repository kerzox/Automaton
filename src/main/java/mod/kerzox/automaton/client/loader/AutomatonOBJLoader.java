package mod.kerzox.automaton.client.loader;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.resources.IResourceManager;
import net.minecraftforge.client.model.IModelLoader;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.client.model.obj.OBJModel;

import java.util.HashMap;
import java.util.Map;

public class AutomatonOBJLoader implements IModelLoader<OBJModelWrapper> {

    public static AutomatonOBJLoader instance = new AutomatonOBJLoader();
    private static Map<String, OBJModelWrapper> cachedOBJWrappers = new HashMap<>();

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) {
        cachedOBJWrappers.clear();
    }

    @Override
    public OBJModelWrapper read(JsonDeserializationContext deserializationContext, JsonObject modelContents) {
        OBJModel model = OBJLoader.INSTANCE.read(deserializationContext, modelContents);

        String[] name = model.modelLocation.getPath().split("/");

        return cachedOBJWrappers.computeIfAbsent(name[2], (objWrapped) -> new OBJModelWrapper(name[2], model, modelContents));
    }
    public static OBJModelWrapper getModelByName(String name) {
        return cachedOBJWrappers.get(name);
    }
}
