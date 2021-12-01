package mod.kerzox.automaton.client.model;

import mod.kerzox.automaton.client.loader.animation.IAnimatable;
import mod.kerzox.automaton.client.loader.animation.Joint;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import net.minecraftforge.client.model.data.IDynamicBakedModel;
import net.minecraftforge.client.model.data.IModelData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class BakedPart implements IDynamicBakedModel {

    protected List<BakedQuad> unculledFaces;
    protected Map<Direction, List<BakedQuad>> culledFaces;
    protected final boolean hasAmbientOcclusion;
    protected final boolean isGui3d;
    protected final boolean usesBlockLight;
    protected final TextureAtlasSprite particleIcon;
    protected final ItemCameraTransforms transforms;
    protected final ItemOverrideList overrides;

    public BakedPart(List<BakedQuad> unculledFaces, Map<Direction, List<BakedQuad>> culledFaces, boolean hasAmbientOcclusion, boolean isGui3d, boolean usesBlockLight, TextureAtlasSprite particleIcon, ItemCameraTransforms transforms, ItemOverrideList overrides) {
        this.unculledFaces = unculledFaces;
        this.culledFaces = culledFaces;
        this.hasAmbientOcclusion = hasAmbientOcclusion;
        this.isGui3d = isGui3d;
        this.usesBlockLight = usesBlockLight;
        this.particleIcon = particleIcon;
        this.transforms = transforms;
        this.overrides = overrides;
    }

    public BakedPart(boolean hasAmbientOcclusion, boolean isGui3d, boolean usesBlockLight, TextureAtlasSprite particleIcon, ItemCameraTransforms transforms, ItemOverrideList overrides) {
        this.hasAmbientOcclusion = hasAmbientOcclusion;
        this.isGui3d = isGui3d;
        this.usesBlockLight = usesBlockLight;
        this.particleIcon = particleIcon;
        this.transforms = transforms;
        this.overrides = overrides;
    }

    @Nonnull
    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData extraData) {
        return side == null ? this.unculledFaces : this.culledFaces.get(side);
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
}
