package mod.kerzox.automaton.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.util.math.vector.Vector3f;

import java.util.Vector;

public class MatrixStackWrapper {

    private MatrixStack mStack;

    public MatrixStackWrapper(MatrixStack stack) {
        this.mStack = stack;
    }

    public void toOrigin() {
        translate(0.5, 0.5, 0.5);
    }

    public void toOriginYZero() {
        translate(0.5, 0, 0.5);
    }

    public void translate(Vector3f vector) {
        translate(vector.x(), vector.y(), vector.z());
    }

    public void translate(double x, double y, double z) {
        this.mStack.translate(x, y, z);
    }

    public void rotateX(float degree) {
        this.mStack.mulPose(Vector3f.XP.rotationDegrees(degree));
    }

    public void rotateY(float degree) {
        this.mStack.mulPose(Vector3f.YP.rotationDegrees(degree));
    }

    public void rotateZ(float degree) {
        this.mStack.mulPose(Vector3f.ZP.rotationDegrees(degree));
    }

    public void push() {
        this.mStack.pushPose();
    }

    public void pop() {
        this.mStack.popPose();
    }

    public MatrixStack getStack() {
        return mStack;
    }
}
