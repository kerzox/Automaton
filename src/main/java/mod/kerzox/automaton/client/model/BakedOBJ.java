package mod.kerzox.automaton.client.model;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import net.minecraftforge.client.model.data.IDynamicBakedModel;
import net.minecraftforge.client.model.data.IModelData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public class BakedOBJ implements IDynamicBakedModel {

    protected BakedPart baseModel;
    protected final Map<String, BakedPart> cachedModels;
    protected final boolean hasAmbientOcclusion;
    protected final boolean isGui3d;
    protected final boolean usesBlockLight;
    protected final TextureAtlasSprite particleIcon;
    protected final ItemCameraTransforms transforms;
    protected final ItemOverrideList overrides;

    public BakedOBJ(Map<String, BakedPart> cachedModels, BakedPart baseModel, boolean hasAmbientOcclusion, boolean isGui3d, boolean usesBlockLight, TextureAtlasSprite particleIcon, ItemCameraTransforms transforms, ItemOverrideList overrides) {
        this.baseModel = baseModel;
        this.cachedModels = cachedModels;
        this.hasAmbientOcclusion = hasAmbientOcclusion;
        this.isGui3d = isGui3d;
        this.usesBlockLight = usesBlockLight;
        this.particleIcon = particleIcon;
        this.transforms = transforms;
        this.overrides = overrides;
    }

    @Override
    public boolean useAmbientOcclusion() {
        return this.hasAmbientOcclusion;
    }

    @Override
    public boolean isGui3d() {
        return this.isGui3d;
    }

    @Override
    public boolean usesBlockLight() {
        return this.usesBlockLight;
    }

    @Override
    public boolean isCustomRenderer() {
        return false;
    }

    @Override
    public TextureAtlasSprite getParticleIcon() {
        return this.particleIcon;
    }

    @Override
    public ItemOverrideList getOverrides() {
        return this.overrides;
    }

    @Override
    public TextureAtlasSprite getParticleTexture(@Nonnull IModelData data) {
        return IDynamicBakedModel.super.getParticleTexture(data);
    }

    @Nonnull
    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData extraData) {
        return this.baseModel != null ? this.baseModel.getQuads(state, side, rand, extraData) : getDefaultQuads(state, side, rand, extraData);
    }

    private List<BakedQuad> getDefaultQuads(BlockState state, Direction side, Random rand, IModelData extraData) {
        List<BakedQuad> quads = new ArrayList<>();
        for (IBakedModel model : cachedModels.values()) {
            quads.addAll(model.getQuads(state, side, rand, extraData));
        }

        return quads;
    }

    public IBakedModel getSplitModelByName(String name) {
        return cachedModels.get(name);
    }


    public static class Builder {
        protected Map<String, BakedPart> cachedModels;
        protected final ItemOverrideList overrides;
        protected final boolean hasAmbientOcclusion;
        protected TextureAtlasSprite particleIcon;
        protected final boolean usesBlockLight;
        protected final boolean isGui3d;
        protected final ItemCameraTransforms transforms;

        protected BakedPart baseModel;

        public Builder(Map<String, BakedPart> cachedModels, net.minecraftforge.client.model.IModelConfiguration model, ItemOverrideList overrides) {
            this(cachedModels, model.useSmoothLighting(), model.isSideLit(), model.isShadedInGui(), model.getCameraTransforms(), overrides);
        }

        private Builder(Map<String, BakedPart> cachedModels, boolean hasAmbientOcclusion, boolean usesBlockLight, boolean isGui3d, ItemCameraTransforms transforms, ItemOverrideList overrides) {
            this.cachedModels = cachedModels;
            this.overrides = overrides;
            this.hasAmbientOcclusion = hasAmbientOcclusion;
            this.usesBlockLight = usesBlockLight;
            this.isGui3d = isGui3d;
            this.transforms = transforms;
        }

        public BakedOBJ.Builder particle(TextureAtlasSprite pTexture) {
            this.particleIcon = pTexture;
            return this;
        }

        public BakedOBJ.Builder createBaseModel(BakedPart baseModel) {
            this.baseModel = baseModel;
            return this;
        }

        public BakedOBJ build() {
            if (this.particleIcon == null) {
                throw new RuntimeException("Missing particle!");
            } else {
                return new BakedOBJ(this.cachedModels, this.baseModel, this.hasAmbientOcclusion, this.usesBlockLight, this.isGui3d, this.particleIcon, this.transforms, this.overrides);
            }
        }
    }
}
