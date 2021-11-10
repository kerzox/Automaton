package mod.kerzox.automaton.client.render.ter;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import mod.kerzox.automaton.client.loader.reflection.OBJModelHelper;
import mod.kerzox.automaton.client.loader.AutomatonBakedOBJ;
import mod.kerzox.automaton.client.loader.AutomatonOBJLoader;
import mod.kerzox.automaton.common.tile.machines.TestGroupTile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ModelManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector4f;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.obj.MaterialLibrary;
import org.apache.commons.lang3.tuple.Pair;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class TestGroupTileRender extends TileEntityRenderer<TestGroupTile> {

    public TestGroupTileRender(TileEntityRendererDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    public void render(TestGroupTile pBlockEntity, float pPartialTicks, MatrixStack pMatrixStack, IRenderTypeBuffer pBuffer, int pCombinedLight, int pCombinedOverlay) {
        BlockRendererDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();
        ModelManager modelManager = Minecraft.getInstance().getModelManager();

        World world = pBlockEntity.getLevel();
        BlockPos pos = pBlockEntity.getBlockPos();
        AutomatonBakedOBJ model = AutomatonOBJLoader.getModelByName("test");
        IBakedModel baked = model.getSplitModelByName("right_cube");
        pMatrixStack.pushPose();
        pMatrixStack.translate(0, 2, 0);
        MatrixStack.Entry currentMatrix = pMatrixStack.last();
        IVertexBuilder vertexBuffer = pBuffer.getBuffer(RenderType.solid());

        float red = 255 / 255.0F;
        float green = 255 / 255.0F;
        float blue = 255 / 255.0F;

        drawBlock(dispatcher, pCombinedLight, pCombinedOverlay, currentMatrix, baked, vertexBuffer, red, green, blue);

        pMatrixStack.popPose();
    }

    private void drawBlock(BlockRendererDispatcher dispatcher, int pCombinedLight, int pCombinedOverlay, MatrixStack.Entry currentMatrix, IBakedModel model, IVertexBuilder vertexBuffer, float red, float green, float blue) {
        dispatcher.getModelRenderer().renderModel(currentMatrix,
                vertexBuffer,
                null,
                model,
                red,
                green,
                blue,
                getLighting(pCombinedLight),
                pCombinedOverlay,
                EmptyModelData.INSTANCE);
    }


    public static int getLighting(int combinedLight) {
        int blockLight = LightTexture.block(combinedLight);
        int skyLight = LightTexture.sky(combinedLight);
        return LightTexture.pack(blockLight, skyLight);
    }

}
