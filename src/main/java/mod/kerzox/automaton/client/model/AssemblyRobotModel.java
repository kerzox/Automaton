package mod.kerzox.automaton.client.model;

import mod.kerzox.automaton.client.loader.AutomatonOBJLoader;
import mod.kerzox.automaton.client.loader.animation.IAnimatable;
import mod.kerzox.automaton.client.loader.animation.Joint;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.client.model.IModelConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AssemblyRobotModel extends BakedOBJ {

    private List<BakedPart> children = new ArrayList<>();
    private Map<String, Joint> joints = new HashMap<>();

    public AssemblyRobotModel(Map<String, BakedPart> cachedModels, BakedPart baseModel, boolean hasAmbientOcclusion, boolean isGui3d, boolean usesBlockLight, TextureAtlasSprite particleIcon, ItemCameraTransforms transforms, ItemOverrideList overrides) {
        super(cachedModels, baseModel, hasAmbientOcclusion, isGui3d, usesBlockLight, particleIcon, transforms, overrides);
        children.add(cachedModels.get("anim_rotation_shaft"));
        createJoints();
    }

    private void createJoints() {

        // rotator plate joint
        joints.put("rotator_plate", new Joint(new Vector3f(0, 6.5f, -1), true));

        // rotator_shaft
        joints.put("rotator_shaft", new Joint(new Vector3f(-0.25f, 7f, -1), true));
    }

    public List<BakedPart> getChildren() {
        return children;
    }

    public Map<String, Joint> getJoints() {
        return joints;
    }

    public static class Builder extends BakedOBJ.Builder {

        public Builder(Map<String, BakedPart> cachedModels, IModelConfiguration model, ItemOverrideList overrides) {
            super(cachedModels, model, overrides);
        }

        @Override
        public AssemblyRobotModel build() {
            return new AssemblyRobotModel(this.cachedModels, this.baseModel, this.hasAmbientOcclusion, this.usesBlockLight, this.isGui3d, this.particleIcon, this.transforms, this.overrides);
        }
    }

}
