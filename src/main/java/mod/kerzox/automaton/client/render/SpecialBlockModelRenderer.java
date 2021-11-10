package mod.kerzox.automaton.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.BlockModelRenderer;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.*;
import net.minecraftforge.client.model.data.IModelData;
import org.lwjgl.system.MemoryStack;

import javax.annotation.Nullable;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.List;
import java.util.Random;

public abstract class SpecialBlockModelRenderer {

    public static void renderModel(MatrixStack.Entry pMatrixEntry, IVertexBuilder pBuffer, @Nullable BlockState pState, IBakedModel pModel, float pRed, float pGreen, float pBlue, int pCombinedLight, int pCombinedOverlay, net.minecraftforge.client.model.data.IModelData modelData) {
        Random random = new Random();
        long i = 42L;

        for(Direction direction : Direction.values()) {
            random.setSeed(42L);
            renderQuadList(pMatrixEntry, pBuffer, pRed, pGreen, pBlue, pModel.getQuads(pState, direction, random, modelData), pCombinedLight, pCombinedOverlay);
        }

        random.setSeed(42L);
        renderQuadList(pMatrixEntry, pBuffer, pRed, pGreen, pBlue, pModel.getQuads(pState, (Direction)null, random, modelData), pCombinedLight, pCombinedOverlay);
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
