package mod.kerzox.automaton.client.loader;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import mod.kerzox.automaton.client.model.BakedPart;
import net.minecraft.client.renderer.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import net.minecraftforge.client.model.IModelBuilder;

import java.util.List;
import java.util.Map;

public class PartBuilder implements IModelBuilder<PartBuilder> {

    private final List<BakedQuad> unculledFaces = Lists.newArrayList();
    private final Map<Direction, List<BakedQuad>> culledFaces = Maps.newEnumMap(Direction.class);
    private final ItemOverrideList overrides;
    private final boolean hasAmbientOcclusion;
    private TextureAtlasSprite particleIcon;
    private final boolean usesBlockLight;
    private final boolean isGui3d;
    private final ItemCameraTransforms transforms;

    public PartBuilder(net.minecraftforge.client.model.IModelConfiguration model, ItemOverrideList overrides) {
        this(model.useSmoothLighting(), model.isSideLit(), model.isShadedInGui(), model.getCameraTransforms(), overrides);
    }

    public PartBuilder(BlockModel p_i230060_1_, ItemOverrideList p_i230060_2_, boolean p_i230060_3_) {
        this(p_i230060_1_.hasAmbientOcclusion(), p_i230060_1_.getGuiLight().lightLikeBlock(), p_i230060_3_, p_i230060_1_.getTransforms(), p_i230060_2_);
    }

    private PartBuilder(boolean p_i230061_1_, boolean p_i230061_2_, boolean p_i230061_3_, ItemCameraTransforms p_i230061_4_, ItemOverrideList p_i230061_5_) {
        for(Direction direction : Direction.values()) {
            this.culledFaces.put(direction, Lists.newArrayList());
        }

        this.overrides = p_i230061_5_;
        this.hasAmbientOcclusion = p_i230061_1_;
        this.usesBlockLight = p_i230061_2_;
        this.isGui3d = p_i230061_3_;
        this.transforms = p_i230061_4_;
    }

    @Override
    public PartBuilder addFaceQuad(Direction facing, BakedQuad quad) {
        culledFaces.get(facing).add(quad);
        return this;
    }

    @Override
    public PartBuilder addGeneralQuad(BakedQuad quad) {
        unculledFaces.add(quad);
        return this;
    }

    public PartBuilder particle(TextureAtlasSprite pTexture) {
        this.particleIcon = pTexture;
        return this;
    }

    public List<BakedQuad> getUnculledFaces() {
        return unculledFaces;
    }

    public Map<Direction, List<BakedQuad>> getCulledFaces() {
        return culledFaces;
    }

    @Override
    public BakedPart build() {
        if (this.particleIcon == null) {
            throw new RuntimeException("Missing particle!");
        } else {
            return new BakedPart(this.unculledFaces, this.culledFaces, this.hasAmbientOcclusion, this.usesBlockLight, this.isGui3d, this.particleIcon, this.transforms, this.overrides);
        }
    }
}
