package mod.kerzox.automaton.client.loader.reflection;

import mod.kerzox.automaton.Automaton;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.TransformationMatrix;
import net.minecraft.util.math.vector.Vector4f;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.client.model.geometry.IModelGeometryPart;
import net.minecraftforge.client.model.obj.MaterialLibrary;
import net.minecraftforge.client.model.obj.OBJModel;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import org.apache.commons.lang3.tuple.Pair;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OBJModelHelper {

    private static Class<?> meshClass;
    private static Field parts;
    private static Field meshes;
    private static Field faces;
    private static Field material;

    private static Method makeQuad;

    public static void doReflection() throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException, NoSuchMethodException {
        parts = OBJModel.class.getDeclaredField("parts");
        parts.setAccessible(true);
        meshes = OBJModel.ModelObject.class.getDeclaredField("meshes");
        meshes.setAccessible(true);
        meshClass = Class.forName("net.minecraftforge.client.model.obj.OBJModel$ModelMesh");
        faces = meshClass.getDeclaredField("faces");
        faces.setAccessible(true);
        material = meshClass.getDeclaredField("mat");
        material.setAccessible(true);
        makeQuad = OBJModel.class.getDeclaredMethod("makeQuad", int[][].class, int.class, Vector4f.class, Vector4f.class, TextureAtlasSprite.class, TransformationMatrix.class);
        makeQuad.setAccessible(true);
    }

    public static Pair<BakedQuad, Direction> makeQuad(OBJModel model, int[][] indices, int tintIndex, Vector4f colorTint, Vector4f ambientColor, TextureAtlasSprite texture, TransformationMatrix transform) throws InvocationTargetException, IllegalAccessException {
        return (Pair<BakedQuad, Direction>) makeQuad.invoke(model, indices, tintIndex, colorTint, ambientColor, texture, transform);
    }

    public static OBJModel.ModelGroup getGroupByName(OBJModel model, String name)
    {
        Map<String, OBJModel.ModelGroup> groups = get(parts, model);
        return groups.get(name);
    }

    public static Map<String, OBJModel.ModelGroup> getAllGroups(OBJModel model)
    {
        return get(parts, model);
    }

    public static List<ModelMeshWrapper> getMeshes(OBJModel.ModelObject object) {
        List<?> m = get(meshes, object);
        List<ModelMeshWrapper> ret = new ArrayList<>();
        for (Object o : m) {
            ret.add(new ModelMeshWrapper(o));
        }
        return ret;
    }

    private static <T> T get(Field f, Object e)
    {
        try
        {
            return (T)f.get(e);
        } catch(IllegalAccessException ex)
        {
            throw new RuntimeException(ex);
        }
    }

    public static class ModelMeshWrapper {

        Object meshObject;

        public ModelMeshWrapper(Object meshObject) {
            this.meshObject = meshObject;
        }

        public List<int[][]> getFaces() {
            return get(faces, meshObject);
        }

        public MaterialLibrary.Material getMat() {
            return get(material, meshObject);
        }
    }
}
