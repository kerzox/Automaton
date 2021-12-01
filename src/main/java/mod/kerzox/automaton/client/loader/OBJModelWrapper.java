package mod.kerzox.automaton.client.loader;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import mod.kerzox.automaton.client.model.AssemblyRobotModel;
import mod.kerzox.automaton.client.model.BakedOBJ;
import mod.kerzox.automaton.client.model.BakedPart;
import net.minecraft.client.renderer.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModelBuilder;
import net.minecraftforge.client.model.IModelConfiguration;
import net.minecraftforge.client.model.geometry.IModelGeometryPart;
import net.minecraftforge.client.model.geometry.ISimpleModelGeometry;
import net.minecraftforge.client.model.obj.OBJModel;

import java.util.*;
import java.util.function.Function;

public class OBJModelWrapper implements ISimpleModelGeometry<OBJModelWrapper> {

    private Map<String, List<IModelGeometryPart>> groups = new HashMap<>();
    private Map<IModelGeometryPart, Boolean> visible = new HashMap<>();
    private BakedOBJ automatonBaked;
    private String baseModelName;
    private String name;

    private final OBJModel objModel;

    public OBJModelWrapper(String name, OBJModel objModel, JsonObject modelContents) {
        this.name = name;
        this.objModel = objModel;
        this.objModel.getParts().forEach(part -> visible.put(part, true));
        deserialize(modelContents);
    }

    private void deserialize(JsonObject modelContents) {

        baseModelName = JSONUtils.getAsString(modelContents, "block_model", "");
        JsonArray array = JSONUtils.getAsJsonArray(modelContents, "combinedGroups", null);
        if (array != null) {
            for (JsonElement group : modelContents.get("combinedGroups").getAsJsonArray()) {
                List<IModelGeometryPart> modelParts = new ArrayList<>();
                String groupName = "";
                for (JsonElement arr : group.getAsJsonArray()) {
                    String[] split = arr.getAsString().split(":");
                    if (this.objModel.getPart(split[1]).isPresent()) {
                        modelParts.add(this.objModel.getPart(split[1]).get());
                    }
                    groupName = split[0];
                }
                groups.put(groupName, modelParts);
            }

        }
        List<IModelGeometryPart> nogroup = new ArrayList<>(this.objModel.getParts());

        for (List<IModelGeometryPart> partList : groups.values()) {
            partList.forEach(nogroup::remove);
        }

        nogroup.forEach(part -> groups.put(part.name(), Lists.newArrayList(part)));
    }

    @Override
    public IBakedModel bake(IModelConfiguration owner, ModelBakery bakery, Function<RenderMaterial, TextureAtlasSprite> spriteGetter, IModelTransform modelTransform, ItemOverrideList overrides, ResourceLocation modelLocation) {
        TextureAtlasSprite particle = spriteGetter.apply(owner.resolveTexture("particle"));

        Map<String, BakedPart> baked = new HashMap<>();

        for (Map.Entry<String, List<IModelGeometryPart>> partList : groups.entrySet()) {
            PartBuilder partBuilder = new PartBuilder(owner, overrides);
            partList.getValue().stream().filter(part -> visible.get(part))
                    .forEach(part -> part.addQuads(owner, partBuilder, bakery, spriteGetter, modelTransform, modelLocation));
            partBuilder.particle(particle);
            baked.put(partList.getKey(), partBuilder.build());
        }

        BakedOBJ.Builder builder = new BakedOBJ.Builder(baked, owner, overrides);

        if (name.contains("assembly_robot")) {
            builder = new AssemblyRobotModel.Builder(baked, owner, overrides);
        }

        builder.particle(particle);
        if (!baseModelName.isEmpty()) {
            builder.createBaseModel(baked.get(baseModelName));
        }
        automatonBaked = builder.build();
        return automatonBaked;
    }

    @Override
    public void addQuads(IModelConfiguration owner, IModelBuilder<?> modelBuilder, ModelBakery bakery, Function<RenderMaterial, TextureAtlasSprite> spriteGetter, IModelTransform modelTransform, ResourceLocation modelLocation) {

    }

    @Override
    public Collection<RenderMaterial> getTextures(IModelConfiguration owner, Function<ResourceLocation, IUnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors) {
        return this.objModel.getTextures(owner, modelGetter, missingTextureErrors);
    }

    public BakedOBJ getAutomatonBaked() {
        return automatonBaked;
    }

    public String getName() {
        return this.objModel.modelLocation.getPath().split("/")[1];
    }
}
