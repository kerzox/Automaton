package mod.kerzox.automaton.client.render.ter;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import mod.kerzox.automaton.Automaton;
import mod.kerzox.automaton.common.events.ModBusEvents;
import mod.kerzox.automaton.common.tile.transfer.pipes.PressurizedPipe;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ModelManager;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.World;
import net.minecraftforge.client.model.data.EmptyModelData;

import java.util.Set;


public class PressurizedPipeRender extends TileEntityRenderer<PressurizedPipe> {

    public static final ResourceLocation PIPE_CONNECTION_RESOURCE = new ResourceLocation(Automaton.modid(), "block/pipe_model_connection");
    public static final ResourceLocation PIPE_RESOURCE = new ResourceLocation(Automaton.modid(), "block/pipe_model");
    public static final ResourceLocation PIPE_CORNER_RESOURCE = new ResourceLocation(Automaton.modid(), "block/pipe_model_inverted");
    public static final ResourceLocation PIPE_CORNER_TEST_RESOURCE = new ResourceLocation(Automaton.modid(), "block/pipe_model_corner");


    public PressurizedPipeRender(TileEntityRendererDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    public void render(PressurizedPipe tile, float pPartialTicks, MatrixStack mStack, IRenderTypeBuffer buffer, int pCombinedLight, int pCombinedOverlay) {
        BlockRendererDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();
        ModelManager modelManager = Minecraft.getInstance().getModelManager();

        World world = tile.getLevel();
        BlockPos pos = tile.getBlockPos();

        boolean isCorner = tile.getConnections().contains(Direction.WEST) && tile.getConnections().contains(Direction.SOUTH)
                || tile.getConnections().contains(Direction.EAST) && tile.getConnections().contains(Direction.NORTH)
                || tile.getConnections().contains(Direction.WEST) && tile.getConnections().contains(Direction.NORTH)
                || tile.getConnections().contains(Direction.EAST) && tile.getConnections().contains(Direction.SOUTH);

        if (!isCorner) renderMainPipe(mStack, tile, modelManager, buffer, dispatcher, pCombinedLight, pCombinedOverlay);
        else drawCorner(mStack, tile, modelManager, buffer, dispatcher, pCombinedLight, pCombinedOverlay);
        Set<Direction> connections = tile.getConnections();

        if (connections.size() >= 1) {
            renderConnection(connections, mStack, modelManager, buffer, dispatcher, pCombinedLight, pCombinedOverlay);
        }

    }

    private void renderMainPipe(MatrixStack mStack, PressurizedPipe tile, ModelManager modelManager, IRenderTypeBuffer buffer, BlockRendererDispatcher dispatcher, int pCombinedLight, int pCombinedOverlay) {
        mStack.pushPose();
        mStack.translate(0.5, 0.5,0.5);

        boolean invert = tile.getConnections().contains(Direction.EAST) || tile.getConnections().contains(Direction.WEST);
        boolean goingUP = tile.getConnections().contains(Direction.UP) || tile.getConnections().contains(Direction.DOWN);

        if (invert) mStack.mulPose(Vector3f.YP.rotationDegrees(270F));
        else if (goingUP) mStack.mulPose(Vector3f.XP.rotationDegrees(90F));
        MatrixStack.Entry currentMatrix = mStack.last();
        IBakedModel model = modelManager.getModel(PIPE_RESOURCE);
        IVertexBuilder vertexBuffer = buffer.getBuffer(RenderType.solid());

        float red = 255 / 255.0F;
        float green = 255 / 255.0F;
        float blue = 255 / 255.0F;

        drawBlock(dispatcher, pCombinedLight, pCombinedOverlay, currentMatrix, model, vertexBuffer, red, green, blue);


        mStack.popPose();
    }

    private void drawCorner(MatrixStack mStack, PressurizedPipe tile, ModelManager modelManager, IRenderTypeBuffer buffer, BlockRendererDispatcher dispatcher, int pCombinedLight, int pCombinedOverlay) {
        mStack.pushPose();
        mStack.translate(0.5, 0.5,0.5);
        MatrixStack.Entry currentMatrix = mStack.last();
        IBakedModel model = modelManager.getModel(PIPE_CORNER_TEST_RESOURCE);
        IVertexBuilder vertexBuffer = buffer.getBuffer(RenderType.solid());

        if (tile.getConnections().contains(Direction.WEST) && tile.getConnections().contains(Direction.NORTH)) {
            mStack.mulPose(Vector3f.YP.rotationDegrees(90));
        }
        if (tile.getConnections().contains(Direction.WEST) && tile.getConnections().contains(Direction.SOUTH)) {
            mStack.mulPose(Vector3f.YP.rotationDegrees(180F));
        }
        if (tile.getConnections().contains(Direction.EAST) && tile.getConnections().contains(Direction.SOUTH)) {
            mStack.mulPose(Vector3f.YN.rotationDegrees(90));
        }

        float red = 255 / 255.0F;
        float green = 255 / 255.0F;
        float blue = 255 / 255.0F;

        drawBlock(dispatcher, pCombinedLight, pCombinedOverlay, currentMatrix, model, vertexBuffer, red, green, blue);
        mStack.popPose();
    }

    private void renderConnection(Set<Direction> connectedTiles, MatrixStack mStack, ModelManager modelManager, IRenderTypeBuffer buffer, BlockRendererDispatcher dispatcher, int pCombinedLight, int pCombinedOverlay) {
        mStack.pushPose();
        //mStack.translate(0.5, 0.5,0.5);
        IBakedModel model = modelManager.getModel(PIPE_CONNECTION_RESOURCE);
        IVertexBuilder vertexBuffer = buffer.getBuffer(RenderType.solid());

        float red = 255 / 255.0F;
        float green = 255 / 255.0F;
        float blue = 255 / 255.0F;

        for (Direction dir : connectedTiles) {
            mStack.pushPose();
//            mStack.translate(.47, 0, 0);
            translateFromDirection(mStack, dir, dispatcher, pCombinedLight, pCombinedOverlay, model, vertexBuffer, red, green, blue);
            mStack.popPose();
        }

        mStack.popPose();
    }

//                mStack.mulPose(Vector3f.YN.rotationDegrees(270F));
//            mStack.translate(-1, 0, 0);

    private void translateFromDirection(MatrixStack mStack, Direction dir, BlockRendererDispatcher dispatcher, int pCombinedLight, int pCombinedOverlay, IBakedModel model, IVertexBuilder vertexBuffer, float red, float green, float blue) {
        mStack.translate(.125, 0.5, 0.5);
        if (dir == Direction.UP) {
            mStack.pushPose();
            mStack.translate(.375, .375, 0);
            mStack.mulPose(Vector3f.ZP.rotationDegrees(270F));
            mStack.mulPose(Vector3f.XP.rotationDegrees(90F));
            MatrixStack.Entry currentMatrix = mStack.last();
            drawBlock(dispatcher, pCombinedLight, pCombinedOverlay, currentMatrix, model, vertexBuffer, red, green, blue);
            mStack.popPose();
        }
        if (dir == Direction.DOWN) {
            mStack.pushPose();
            mStack.translate(.375, -.375, 0);
            mStack.mulPose(Vector3f.ZP.rotationDegrees(90F));
            MatrixStack.Entry currentMatrix = mStack.last();
            drawBlock(dispatcher, pCombinedLight, pCombinedOverlay, currentMatrix, model, vertexBuffer, red, green, blue);
            mStack.popPose();
        }
        if (dir == Direction.NORTH) {
            mStack.pushPose();
            mStack.translate(0.375, 0, -0.375);
            mStack.mulPose(Vector3f.YP.rotationDegrees(270F));
            MatrixStack.Entry currentMatrix = mStack.last();
            drawBlock(dispatcher, pCombinedLight, pCombinedOverlay, currentMatrix, model, vertexBuffer, red, green, blue);
            mStack.popPose();
        }
        if (dir == Direction.EAST) {
            mStack.pushPose();
            mStack.translate(0.5 + .250, 0, 0);
            mStack.mulPose(Vector3f.YP.rotationDegrees(180F));
            MatrixStack.Entry currentMatrix = mStack.last();
            drawBlock(dispatcher, pCombinedLight, pCombinedOverlay, currentMatrix, model, vertexBuffer, red, green, blue);
            mStack.popPose();
        }
        if (dir == Direction.SOUTH) {
            mStack.pushPose();
            mStack.translate(0.375, 0, 0.375);
            mStack.mulPose(Vector3f.YP.rotationDegrees(90F));
            MatrixStack.Entry currentMatrix = mStack.last();
            drawBlock(dispatcher, pCombinedLight, pCombinedOverlay, currentMatrix, model, vertexBuffer, red, green, blue);
            mStack.popPose();
        }
        if (dir == Direction.WEST) {
            mStack.pushPose();
            MatrixStack.Entry currentMatrix = mStack.last();
            drawBlock(dispatcher, pCombinedLight, pCombinedOverlay, currentMatrix, model, vertexBuffer, red, green, blue);
            mStack.popPose();
        }
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
