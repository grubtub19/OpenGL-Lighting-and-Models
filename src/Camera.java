import graphicslib3D.Matrix3D;
import graphicslib3D.Point3D;
import graphicslib3D.Vector3D;
import java.util.ArrayList;

public class Camera {
    public Point3D position = new Point3D(0,0,0);
    Vector3D forward = new Vector3D(0,0,-1);
    float yaw = 90, pitch = 0;
    boolean needCalc = true;
    float moveSensitivity = 0.002f;
    float rotateSensitivity = 0.03f;
    float speedUpVal = 4;

    public Camera(float x, float y, float z) {
        position.setX(x);
        position.setY(y);
        position.setZ(z);
    }

    public Vector3D toVector3D(Point3D point) {
        return new Vector3D(point.getX(), point.getY(), point.getZ());
    }

    public Matrix3D getRotMatrix() {
        updateForward();
        Vector3D left = forward.cross(new Vector3D(0,1,0)).normalize();
        Vector3D up = left.cross(forward).normalize();
        Matrix3D rotMatrix = new Matrix3D(new double[] {left.getX(), up.getX(), -forward.getX(), 0.0f,
                                        left.getY(), up.getY(), -forward.getY(), 0.0f,
                                        left.getZ(), up.getZ(), -forward.getZ(), 0.0f,
                                        -left.dot(toVector3D(position)), -up.dot(toVector3D(position)), -forward.mult(-1).dot(toVector3D(position)), 1.0f});
        return rotMatrix;
    }

    public float getRotX() {
        return (float) (Math.cos(Math.toRadians(pitch)) * Math.cos(Math.toRadians(yaw)));
    }

    public float getRotY() {
        return (float) (Math.sin(Math.toRadians(pitch)));
    }

    public float getRotZ() {
        return (float) (Math.cos(Math.toRadians(pitch)) * -Math.sin(Math.toRadians(yaw)));
    }

    public void move(Vector3D vector) {
        position.setX(position.getX() + vector.getX());
        position.setY(position.getY() + vector.getY());
        position.setZ(position.getZ() + vector.getZ());
    }

    private void updateForward() {
        if(needCalc) {
            forward.setX(getRotX());
            forward.setY(getRotY());
            forward.setZ(getRotZ());
            forward.normalize();
            needCalc = false;
        }
    }

    public void moveForward(float time) {
        updateForward();
        Vector3D move;
        move = forward.mult(moveSensitivity * time * speedUpVal);
        move.setY(0);
        move(move);
        System.out.println("forward: " + forward.getX() + ", " + forward.getY() + ", " + forward.getZ());
        System.out.println("yaw: " + yaw + ", pitch: " + pitch);
    }

    public void moveBackward(float time) {
        updateForward();
        Vector3D move;
        move = forward.mult(-moveSensitivity * time * speedUpVal);
        move.setY(0);
        move(move);
    }

    public void strafeLeft(float time) {
        updateForward();
        Vector3D left = forward.cross(new Vector3D(0,-1,0)).mult(moveSensitivity * time * speedUpVal);
        move(left);
    }

    public void strafeRight(float time) {
        updateForward();
        Vector3D right = forward.cross(new Vector3D(0,1,0)).mult(moveSensitivity * time * speedUpVal);
        move(right);
    }

    public void strafeUp(float time) {
        updateForward();
        Vector3D up = new Vector3D(0,1,0).mult(moveSensitivity * time * speedUpVal);
        move(up);
    }

    public void strafeDown(float time) {
        updateForward();
        Vector3D up = new Vector3D(0,-1,0).mult(moveSensitivity * time * speedUpVal);
        move(up);
    }

    public void yaw(float amount) {
        yaw -= rotateSensitivity * amount;
        needCalc = true;
    }

    public void yawLeft(float time) {
        yaw += rotateSensitivity * time;
        needCalc = true;
    }

    public void pitchDown(float time) {
        pitch -= rotateSensitivity * time;
        needCalc = true;
    }

    public void pitch(float amount) {
        pitch += rotateSensitivity * amount;
        needCalc = true;
    }

    public void moveCamera(MyListener listener, float time) {
        if(listener.keyboard.shift.isDown()) {
            speedUpVal = 4;
        } else if(listener.keyboard.ctrl.isDown()) {
            speedUpVal = 0.2f;
        } else {
            speedUpVal = 1;
        }
        if(listener.keyboard.w.isDown()) {
            moveForward(time);
        }
        if(listener.keyboard.a.isDown()) {
            strafeLeft(time);
        }
        if(listener.keyboard.s.isDown()) {
            moveBackward(time);
        }
        if(listener.keyboard.d.isDown()) {
            strafeRight(time);
        }
        if(listener.keyboard.q.isDown()) {
            strafeUp(time);
        }
        if(listener.keyboard.e.isDown()) {
            strafeDown(time);
        }

        yaw(listener.mouse.x);

        pitch(-(listener.mouse.y));
    }
}
