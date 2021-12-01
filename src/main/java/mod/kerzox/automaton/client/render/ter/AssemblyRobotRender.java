package mod.kerzox.automaton.client.render.ter;

import com.mojang.blaze3d.matrix.MatrixStack;
import mod.kerzox.automaton.client.loader.AutomatonOBJLoader;
import mod.kerzox.automaton.client.loader.OBJModelWrapper;
import mod.kerzox.automaton.client.model.BakedOBJ;
import mod.kerzox.automaton.client.render.MatrixStackWrapper;
import mod.kerzox.automaton.client.render.SpecialBlockModelRenderer;
import mod.kerzox.automaton.common.tile.machines.assembly.AssemblyRobot;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.World;

import static java.lang.Math.PI;
import static org.lwjgl.glfw.GLFW.glfwGetTime;

public class AssemblyRobotRender extends TileEntityRenderer<AssemblyRobot> {

    public AssemblyRobotRender(TileEntityRendererDispatcher p_i226006_1_) {
        super(p_i226006_1_);
    }

    @Override
    public void render(AssemblyRobot pBlockEntity, float pPartialTicks, MatrixStack pMatrixStack, IRenderTypeBuffer pBuffer, int pCombinedLight, int pCombinedOverlay) {
        OBJModelWrapper bakedHolder = AutomatonOBJLoader.getModelByName("assembly_robot.obj");
        if (bakedHolder == null) return;
        World world = pBlockEntity.getLevel();
        BlockPos pos = pBlockEntity.getBlockPos();

        MatrixStackWrapper ms = new MatrixStackWrapper(pMatrixStack);

        BakedOBJ animationModel = bakedHolder.getAutomatonBaked();

//        IBakedModel cube1 = animationModel.getSplitModelByName("cube");
//        IBakedModel cube2 = animationModel.getSplitModelByName("cube2");
//        IBakedModel cube3 = animationModel.getSplitModelByName("cube3");
//        IBakedModel conn = animationModel.getSplitModelByName("conn");
//        IBakedModel conn2 = animationModel.getSplitModelByName("conn2");

        IBakedModel bottom_arm = animationModel.getSplitModelByName("bearing");
        IBakedModel top_arm = animationModel.getSplitModelByName("bearing_arm");
        IBakedModel arm_base = animationModel.getSplitModelByName("arm_base");
        IBakedModel hand = animationModel.getSplitModelByName("hand");
        IBakedModel rotatingShaft = animationModel.getSplitModelByName("anim_rotation_shaft");
        IBakedModel base = animationModel.getSplitModelByName("base");


        BlockPos newPos = pos.north();
//        Vector3d targetPos = new Vector3d(newPos.getX(), newPos.getY(), newPos.getZ()).add(0.5, 0.5, 0.5);
        Vector3d targetPos = Vector3d.atCenterOf(newPos);
        Vector3d diff = targetPos.subtract(new Vector3d(.5, .5, .5));

        // center to origin
        ms.translate(.5, 0, .5);



        Quaternion fun2 = new Quaternion(0, (float) Math.sin(glfwGetTime()), 0, (float) Math.cos(glfwGetTime()));

        ms.push();
        if (bottom_arm != null && arm_base != null && rotatingShaft != null) {
            float[] angles = computeFabrik(targetPos);

            Vector3d dir = targetPos.subtract(targetPos.add(0, 0, .5).subtract(0, -1, 0));
            Vector3d dirCopy = new Vector3d(dir.x, dir.y, dir.z).add(0, 1.5, 0);

            //SpecialBlockModelRenderer.renderModelCorrectLighting(pMatrixStack, pBuffer, bottom_arm, pBlockEntity, pos, pCombinedOverlay);

            ms.push();
            ms.translate(4.25 / 16d, 17.1036 / 16d, -1.1768 / 16d);
            ms.rotateX((float) (MathHelper.atan2(dirCopy.x, dirCopy.y) * (180/PI)));
            ms.translate(-4.25 / 16d, -17.1036 / 16d, 1.1768 / 16d);
            SpecialBlockModelRenderer.renderModelCorrectLighting(pMatrixStack, pBuffer, bottom_arm, pBlockEntity, pos, pCombinedOverlay);
            ms.pop();

            ms.push();
            ms.translate(dirCopy.x, dirCopy.y, dirCopy.z);
            ms.translate(4.25 / 16d, 24.8817 / 16d, 6.2478 / 16d);
            ms.rotateX((float) (MathHelper.atan2(dir.y, dir.x) * (180/PI)));
            ms.translate(-4.25 / 16d, -24.8817 / 16d, -6.2478 / 16d);
            SpecialBlockModelRenderer.renderModelCorrectLighting(pMatrixStack, pBuffer, top_arm, pBlockEntity, pos, pCombinedOverlay);
            ms.pop();

            ms.translate(dir.x, dir.y, dir.z);
            ms.translate(-0.25 / 16d, 37.6097 / 16d, -6.4094 / 16d);
            ms.rotateX((float) (MathHelper.atan2(targetPos.x, targetPos.y) * (180/PI)));
            ms.translate(0.25 / 16d, -37.6097 / 16d, 6.4094 / 16d);
            SpecialBlockModelRenderer.renderModelCorrectLighting(pMatrixStack, pBuffer, hand, pBlockEntity, pos, pCombinedOverlay);
            ms.translate(-dir.x, -dir.y, -dir.z);

//            SpecialBlockModelRenderer.renderModelCorrectLighting(pMatrixStack, pBuffer, base, pBlockEntity, pos, pCombinedOverlay);
//            ms.translate(-.25 / 16d, 7 / 16d, -1 / 16d);
//            //ms.rotateY((float) MathHelper.lerp(Math.sin(glfwGetTime()), 0, 45));
//            ms.translate(.25 / 16d, -7 / 16d, 1 / 16d);
//            SpecialBlockModelRenderer.renderModelCorrectLighting(pMatrixStack, pBuffer, arm_base, pBlockEntity, pos, pCombinedOverlay);
//            SpecialBlockModelRenderer.renderModelCorrectLighting(pMatrixStack, pBuffer, rotatingShaft, pBlockEntity, pos, pCombinedOverlay);
//            ms.translate(4.25 / 16d, 17.1036 / 16d, -1.1768 / 16d);
//            ms.rotateX(angles[2]);
//            ms.translate(-4.25 / 16d, -17.1036 / 16d, 1.1768 / 16d);
//            SpecialBlockModelRenderer.renderModelCorrectLighting(pMatrixStack, pBuffer, bottom_arm, pBlockEntity, pos, pCombinedOverlay);
//            ms.translate(4.25 / 16d, 24.8817 / 16d, 6.2478 / 16d);
//            ms.rotateX(angles[1]);
//            ms.translate(-4.25 / 16d, -24.8817 / 16d, -6.2478 / 16d);
//            SpecialBlockModelRenderer.renderModelCorrectLighting(pMatrixStack, pBuffer, top_arm, pBlockEntity, pos, pCombinedOverlay);
//            ms.translate(-0.25 / 16d, 37.6097 / 16d, -6.4094 / 16d);
//            ms.rotateX(-angles[0] + angles[1]);
//            ms.translate(0.25 / 16d, -37.6097 / 16d, 6.4094 / 16d);
//            SpecialBlockModelRenderer.renderModelCorrectLighting(pMatrixStack, pBuffer, hand, pBlockEntity, pos, pCombinedOverlay);
        }
        ms.pop();
    }

    private float[] computeFabrik(Vector3d target) {
        float[] angles = new float[3];

        Vector3d dir = target.subtract(0.25 / 16d, -37.6097 / 16d, 6.4094 / 16d);
        angles[0] = (float) (MathHelper.atan2(dir.z, dir.y) * (180/PI));
//
//        Vector3d angle2Vector = new Vector3d((8 * Math.cos(angles[0])) / 16d, 17.25 / 16d, (8 * Math.sin(angles[0])) / 16d);
//
//        Vector3d dir2 = dir.subtract(angle2Vector);
//        angles[1] = (float) (MathHelper.atan2(target.multiply(1, 0, 1).length(), dir2.y * dir.y) * (180/PI));
//
//        Vector3d angle3Vector = new Vector3d((8 * Math.cos(angles[1])) / 16d, 14 / 16d, (8 * Math.sin(angles[1])) / 16d);
//
//        Vector3d dir3 = dir2.subtract(angle3Vector);
//        angles[2] = (float) (MathHelper.atan2(target.multiply(1, 0, 1).length(), dir3.y * dir.y) * (180/PI));
//
//        double len = 6.25;



        return angles;
    }


    private double calcCos(double b, double c, double a) {
        double ret = (b * b + c * c - a * a) / (2 * b * c);
        return ret;
    }


    private void transformShaft(MatrixStack stack, IRenderTypeBuffer pBuffer, IBakedModel model, AssemblyRobot te, int overlay) {
        Quaternion fun2 = new Quaternion(0, (float)Math.sin(glfwGetTime()), 0, (float)Math.cos(glfwGetTime()));
        stack.translate(.1 / 16d, 0, -1 / 16d);
        //stack.mulPose(fun2);
        stack.mulPose(Vector3f.YP.rotationDegrees(0));
        stack.translate(-.1 / 16d, 0,1 / 16d);
        SpecialBlockModelRenderer.renderModelCorrectLighting(stack, pBuffer, model, te, te.getBlockPos(), overlay);
    }
}
//    public Quaternion(Vector3f pVector, float pAngle, boolean pDegrees) {
//        if (pDegrees) {
//            pAngle *= ((float)Math.PI / 180F);
//        }
//
//        float f = sin(pAngle / 2.0F);
//        this.i = pVector.x() * f;
//        this.j = pVector.y() * f;
//        this.k = pVector.z() * f;
//        this.r = cos(pAngle / 2.0F);
//    }

//                double pX = (x - 0.5) * Math.cos(20) - (y - 0.5) * Math.sin(20);
//                double pY = (x - 0.5) * Math.sin(20) - (y - 0.5) * Math.cos(20);
//                double pZ = (z - 0.5) * Math.sin(20) - (z - 0.5) * Math.cos(20);
//                pMatrixStack.popPose();
//                pMatrixStack.pushPose();
//                pMatrixStack.translate(pX,
//                        pY,
//                        pZ);