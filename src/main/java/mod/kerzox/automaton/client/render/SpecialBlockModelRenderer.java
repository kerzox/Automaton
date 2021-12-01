package mod.kerzox.automaton.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.client.model.data.EmptyModelData;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public abstract class SpecialBlockModelRenderer {

    public static void renderModelCorrectLighting(MatrixStack pMatrixStack, IRenderTypeBuffer pBuffer, IBakedModel model, TileEntity te, BlockPos pos, int overlay) {
        World world = te.getLevel();
        Minecraft.getInstance().getBlockRenderer().getModelRenderer().renderModel(world,
                model,
                te.getBlockState(),
                pos,
                pMatrixStack,
                pBuffer.getBuffer(RenderType.solid()),
                false,
                world.getRandom(),
                0,
                overlay,
                EmptyModelData.INSTANCE);
        }

    public static void renderModelFlat(MatrixStack.Entry pMatrixEntry, IVertexBuilder pBuffer, @Nullable BlockState pState, IBakedModel pModel, float pRed, float pGreen, float pBlue, int pCombinedLight, int pCombinedOverlay, net.minecraftforge.client.model.data.IModelData modelData) {
        Random random = new Random();
        long i = 42L;

        for(Direction direction : Direction.values()) {
            random.setSeed(42L);
            renderQuadList(pMatrixEntry, pBuffer, pRed, pGreen, pBlue, pModel.getQuads(pState, direction, random, modelData), pCombinedLight, pCombinedOverlay);
        }

        random.setSeed(42L);
        renderQuadList(pMatrixEntry, pBuffer, pRed, pGreen, pBlue, pModel.getQuads(pState, (Direction)null, random, modelData), pCombinedLight, pCombinedOverlay);
    }

    public static int getLighting(int combinedLight) {
        int blockLight = LightTexture.block(combinedLight);
        int skyLight = LightTexture.sky(combinedLight);
        return LightTexture.pack(blockLight, skyLight);
    }

    public static void renderQuadList(MatrixStack.Entry pMatrixEntry, IVertexBuilder pBuffer, float pRed, float pGreen, float pBlue, List<BakedQuad> pListQuads, int pCombinedLight, int pCombinedOverlay) {
        for(BakedQuad bakedquad : pListQuads) {
            float f;
            float f1;
            float f2;
            if (bakedquad.isTinted()) {
                f = MathHelper.clamp(pRed, 0.0F, 1.0F);
                f1 = MathHelper.clamp(pGreen, 0.0F, 1.0F);
                f2 = MathHelper.clamp(pBlue, 0.0F, 1.0F);
            } else {
                f = 1.0F;
                f1 = 1.0F;
                f2 = 1.0F;
            }

            pBuffer.putBulkData(pMatrixEntry, bakedquad, f, f1, f2, pCombinedLight, pCombinedOverlay);
        }

    }
}
