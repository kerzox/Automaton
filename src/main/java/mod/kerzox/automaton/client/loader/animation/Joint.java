package mod.kerzox.automaton.client.loader.animation;

import net.minecraft.util.math.vector.Vector3f;

public class Joint {

    private final Vector3f position;
    private boolean horizontal = false;
    private boolean vertical = false;

    public Joint(Vector3f position, boolean horizontal) {
        this.position = position;
        this.vertical = !horizontal;
        this.horizontal = horizontal;
    }

    public Vector3f getVectorPosition() {
        return position;
    }

    public float x() {
        return this.position.x();
    }

    public float y() {
        return this.position.y();
    }

    public float z() {
        return this.position.z();
    }

    public boolean isHorizontal() {
        return horizontal;
    }

    public boolean isVertical() {
        return vertical;
    }
}
