package mod.kerzox.automaton.client.loader;

import mod.kerzox.automaton.client.loader.reflection.OBJModelHelper;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.IModelTransform;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Vector4f;
import net.minecraftforge.client.model.IModelBuilder;
import net.minecraftforge.client.model.IModelConfiguration;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.client.model.obj.MaterialLibrary;
import net.minecraftforge.client.model.obj.OBJModel;
import org.apache.commons.lang3.tuple.Pair;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class AutomatonBakedOBJ {

    private final OBJModel objModel;
    private final IBakedModel baseModel;

    private final IModelConfiguration owner;
    private final Function<RenderMaterial, TextureAtlasSprite> spriteGetter;
    private final IModelTransform transform;

    private final Map<String, IBakedModel> cachedBakedModels = new HashMap<>();

    public AutomatonBakedOBJ(OBJModel objModel, IBakedModel baseModel, IModelConfiguration owner, Function<RenderMaterial, TextureAtlasSprite> sprite, IModelTransform transform) throws InvocationTargetException, IllegalAccessException {
        this.objModel = objModel;
        this.baseModel = baseModel;
        this.owner = owner;
        this.spriteGetter = sprite;
        this.transform = transform;
        splitModel();
    }

    /*TODO
        Add texture remapping, allowing to change uv based on a model state;
     */

    /**
     * Creates IBakedModels from each group specified in an OBJ model. (Uses reflection so resource intensive)
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */

    private void splitModel() throws InvocationTargetException, IllegalAccessException {
        Map<String, OBJModel.ModelGroup> groups = OBJModelHelper.getAllGroups(this.objModel);
        for (Map.Entry<String, OBJModel.ModelGroup> group : groups.entrySet()) {
            List<OBJModelHelper.ModelMeshWrapper> meshes = OBJModelHelper.getMeshes(group.getValue());
            for(OBJModelHelper.ModelMeshWrapper mesh : meshes)
            {
                MaterialLibrary.Material mat = mesh.getMat();
                if (mat == null)
                    continue;
                TextureAtlasSprite texture = spriteGetter.apply(ModelLoaderRegistry.resolveTexture(mat.diffuseColorMap, owner));
                int tintIndex = mat.diffuseTintIndex;
                Vector4f colorTint = mat.diffuseColor;

                IModelBuilder<?> builder = IModelBuilder.of(owner, this.baseModel.getOverrides(), texture);

                for (int[][] face : mesh.getFaces())
                {
                    Pair<BakedQuad, Direction> quad = OBJModelHelper.makeQuad(objModel, face, tintIndex, colorTint, mat.ambientColor, texture, transform.getRotation());
                    if (quad.getRight() == null)
                        builder.addGeneralQuad(quad.getLeft());
                    else
                        builder.addFaceQuad(quad.getRight(), quad.getLeft());
                }
                cachedBakedModels.put(group.getKey(), builder.build());
            }
        }
    }

    public OBJModel getObjModel() {
        return objModel;
    }

    public IBakedModel getBaseModel() {
        return baseModel;
    }

    public Function<RenderMaterial, TextureAtlasSprite> getSpriteGetter() {
        return spriteGetter;
    }

    public IModelConfiguration getOwner() {
        return owner;
    }

    public IModelTransform getTransform() {
        return transform;
    }

    public void addBakedModel(String name, IBakedModel model) {
        this.cachedBakedModels.put(name, model);
    }

    public Map<String, IBakedModel> getCachedBakedModels() {
        return cachedBakedModels;
    }

    public IBakedModel getSplitModelByName(String name) {
        return cachedBakedModels.get(name);
    }
}
