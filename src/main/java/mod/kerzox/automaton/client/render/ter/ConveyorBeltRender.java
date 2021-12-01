package mod.kerzox.automaton.client.render.ter;

import com.mojang.blaze3d.matrix.MatrixStack;
import mod.kerzox.automaton.client.loader.OBJModelWrapper;
import mod.kerzox.automaton.client.loader.AutomatonOBJLoader;
import mod.kerzox.automaton.common.tile.transfer.conveyor.ConveyorBelt;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.data.EmptyModelData;

public class ConveyorBeltRender extends TileEntityRenderer<ConveyorBelt> {

    private OBJModelWrapper bakedHolder;

    public ConveyorBeltRender(TileEntityRendererDispatcher p_i226006_1_) {
        super(p_i226006_1_);
    }

    @Override
    public void render(ConveyorBelt pBlockEntity, float pPartialTicks, MatrixStack pMatrixStack, IRenderTypeBuffer pBuffer, int pCombinedLight, int pCombinedOverlay) {
        bakedHolder = AutomatonOBJLoader.getModelByName("conveyor_belt.obj");
        if (bakedHolder == null) return;

        World world = pBlockEntity.getLevel();
        BlockPos pos = pBlockEntity.getBlockPos();

        pMatrixStack.pushPose();
        pMatrixStack.translate(0.5, 1, 0.5);
//
//        IBakedModel model = bakedHolder.getAutomatonBaked();
//
//        float red = 255 / 255.0F;
//        float green = 255 / 255.0F;
//        float blue = 255 / 255.0F;
//        if (model != null) {
//            Minecraft.getInstance().getBlockRenderer().getModelRenderer().renderModel(world,
//                    model,
//                    pBlockEntity.getBlockState(),
//                    pos,
//                    pMatrixStack,
//                    pBuffer.getBuffer(RenderType.solid()),
//                    false,
//                    world.getRandom(),
//                    0,
//                    pCombinedOverlay,
//                    EmptyModelData.INSTANCE);
//        }

        pMatrixStack.popPose();

//        Map<Direction, ConnectiveSides> sides = new HashMap<>();
//
//        pMatrixStack.pushPose();
//        pMatrixStack.translate(0.5, 0.5, 0.5);
//
//        float red = 255 / 255.0F;
//        float green = 255 / 255.0F;
//        float blue = 255 / 255.0F;
//
//        Direction facing = pBlockEntity.getBlockState().getValue(FACING);
//        boolean renderLegs = true;
//        boolean renderLeftGuard = true;
//        boolean renderRightGuard = true;
//        boolean scaleBelt = false;
//
//        sides.put(Direction.NORTH, pBlockEntity.getBlockState().getValue(NORTH));
//        sides.put(Direction.EAST, pBlockEntity.getBlockState().getValue(EAST));
//        sides.put(Direction.SOUTH, pBlockEntity.getBlockState().getValue(SOUTH));
//        sides.put(Direction.WEST, pBlockEntity.getBlockState().getValue(WEST));
//
//        int connected = 0;
//
//        for (Map.Entry<Direction, ConnectiveSides> side : sides.entrySet()) {
//            if (side.getKey().getAxis() == facing.getAxis()) {
//                TileEntity te = world.getBlockEntity(pos.relative(side.getKey()));
//                if (te != null) {
//                    if (te.getBlockState().getValue(FACING) != facing) continue;
//                    connected++;
//                }
//            }
//            if (side.getKey().getAxis() != facing.getAxis()) {
//                if (side.getValue() == ConnectiveSides.CONNECTED) {
//                    TileEntity te = world.getBlockEntity(pos.relative(side.getKey()));
//                    if (te != null) {
//                        if (te.getBlockState().getValue(FACING) == facing) continue;
//                        if (te.getBlockState().getValue(FACING).getOpposite() == side.getKey()) {
//                            if (side.getKey().getAxisDirection() == Direction.AxisDirection.POSITIVE)
//                                renderRightGuard = false;
//                            else renderLeftGuard = false;
//                        }
//                    }
//                }
//            }
//        }
//
//        if (connected > 1) renderLegs = false;
//
//        if (facing == Direction.WEST || facing == Direction.EAST) {
//            pMatrixStack.mulPose(Vector3f.YP.rotationDegrees(270F));
//        }
//
//        MatrixStack.Entry currentMatrix = pMatrixStack.last();
//        IBakedModel main = bakedHolder.getSplitModelByName("main");
//        IBakedModel belt = bakedHolder.getSplitModelByName("belt");
//        IBakedModel legs = bakedHolder.getSplitModelByName("legs");
//        IBakedModel supports = bakedHolder.getSplitModelByName("supports");
//        IBakedModel pipe = bakedHolder.getSplitModelByName("pipe");
//        IBakedModel leftGuard = bakedHolder.getSplitModelByName("leftbaseGuard");
//        IBakedModel rightGuard = bakedHolder.getSplitModelByName("rightbaseGuard");
//
//        pMatrixStack.popPose();

        /*
        if (main != null) {
            SpecialBlockModelRenderer.renderModel(currentMatrix,
                    pBuffer.getBuffer(RenderType.solid()),
                    null, main, red, green, blue,
                    pCombinedLight, pCombinedOverlay, EmptyModelData.INSTANCE);
        }

        if (legs != null && renderLegs) {
            SpecialBlockModelRenderer.renderModel(currentMatrix,
                    pBuffer.getBuffer(RenderType.solid()),
                    null, legs, red, green, blue,
                    pCombinedLight, pCombinedOverlay, EmptyModelData.INSTANCE);
        }

        if (belt != null) {
            pMatrixStack.pushPose();

            TileEntity te = world.getBlockEntity(pos.relative(facing));
            if (te != null && te.getBlockState().getValue(FACING) != facing) {
                scaleBelt = true;
                pMatrixStack.translate(0, 0,
                        facing.getAxisDirection() == Direction.AxisDirection.NEGATIVE && facing.getAxis() == Direction.Axis.X ? .035f :
                                facing.getAxis() == Direction.Axis.Z && facing.getAxisDirection() == Direction.AxisDirection.POSITIVE ? .035f : -.035f);
                pMatrixStack.scale(1, 1, 1.06f);

            }
            SpecialBlockModelRenderer.renderModel(pMatrixStack.last(),
                    pBuffer.getBuffer(RenderType.solid()),
                    null, belt, red, green, blue,
                    pCombinedLight, pCombinedOverlay, EmptyModelData.INSTANCE);
            pMatrixStack.popPose();
        }

        if (leftGuard != null && rightGuard != null) {
            if (scaleBelt) {
                pMatrixStack.translate(0, 0,
                        facing.getAxisDirection() == Direction.AxisDirection.NEGATIVE && facing.getAxis() == Direction.Axis.X ? .035f :
                                facing.getAxis() == Direction.Axis.Z && facing.getAxisDirection() == Direction.AxisDirection.POSITIVE ? .035f : -.035f);
                pMatrixStack.scale(1, 1, 1.06f);
            }
            if (renderLeftGuard) {
                SpecialBlockModelRenderer.renderModel(currentMatrix,
                        pBuffer.getBuffer(RenderType.solid()),
                        null, leftGuard, red, green, blue,
                        pCombinedLight, pCombinedOverlay, EmptyModelData.INSTANCE);
            }
            if (renderRightGuard) {
                SpecialBlockModelRenderer.renderModel(currentMatrix,
                        pBuffer.getBuffer(RenderType.solid()),
                        null, rightGuard, red, green, blue,
                        pCombinedLight, pCombinedOverlay, EmptyModelData.INSTANCE);
            }
        }


        if (supports != null) {
            pMatrixStack.pushPose();
            pMatrixStack.scale(1, 1, 2.75f);
            currentMatrix = pMatrixStack.last();
            if (!renderLegs)
            SpecialBlockModelRenderer.renderModel(currentMatrix,
                    pBuffer.getBuffer(RenderType.solid()),
                    null, supports, red, green, blue,
                    SpecialBlockModelRenderer.getLighting(pCombinedLight), pCombinedOverlay, EmptyModelData.INSTANCE);
            pMatrixStack.popPose();
        }

        pMatrixStack.popPose();

         */
    }
}