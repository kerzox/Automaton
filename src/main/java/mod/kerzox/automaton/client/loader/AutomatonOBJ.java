package mod.kerzox.automaton.client.loader;

import com.mojang.datafixers.util.Pair;
import net.minecraft.client.renderer.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModelConfiguration;
import net.minecraftforge.client.model.geometry.IModelGeometry;
import net.minecraftforge.client.model.obj.OBJModel;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Set;
import java.util.function.Function;

public class AutomatonOBJ implements IModelGeometry<AutomatonOBJ> {

    private final OBJModel forgeOBJ;

    public AutomatonOBJ(OBJModel forgeOBJ) {
        this.forgeOBJ = forgeOBJ;
    }

    @Override
    public IBakedModel bake(IModelConfiguration owner, ModelBakery bakery, Function<RenderMaterial, TextureAtlasSprite> spriteGetter, IModelTransform modelTransform, ItemOverrideList overrides, ResourceLocation modelLocation) {
        IBakedModel forgeBaked = forgeOBJ.bake(owner, bakery, spriteGetter, modelTransform, overrides, modelLocation);
        try {
            AutomatonOBJLoader.addModel(new AutomatonBakedOBJ(forgeOBJ, forgeBaked, owner, spriteGetter, modelTransform));
        } catch (InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return forgeBaked;
    }

    @Override
    public Collection<RenderMaterial> getTextures(IModelConfiguration owner, Function<ResourceLocation, IUnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors) {
        return forgeOBJ.getTextures(owner, modelGetter, missingTextureErrors);
    }
}
