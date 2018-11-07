import graphicslib3D.Matrix3D;
import graphicslib3D.Point3D;
import graphicslib3D.Vector3D;
import java.util.ArrayList;

public class Camera {
    Point3D position = new Point3D(0,0,0);
    Vector3D forward = new Vector3D(0,0,-1);
    float pitch = 90, yaw = 0;
    boolean needCalc = true;
    float moveSensitivity = 0.00000000025f;
    float rotateSensitivity = 0.000000003f;
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

    public double getRotX() {
        return Math.cos(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch));
    }

    public double getRotY() {
        return Math.sin(Math.toRadians(yaw));
    }

    public double getRotZ() {
        return Math.cos(Math.toRadians(yaw)) * -Math.sin(Math.toRadians(pitch));
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

    public void moveForward(double time) {
        updateForward();
        Vector3D move;
        move = forward.mult(moveSensitivity * time * speedUpVal);
        move.setY(0);
        move(move);
    }

    public void moveBackward(double time) {
        updateForward();
        Vector3D move;
        move = forward.mult(-moveSensitivity * time * speedUpVal);
        move.setY(0);
        move(move);
    }

    public void strafeLeft(double time) {
        updateForward();
        Vector3D left = forward.cross(new Vector3D(0,-1,0)).mult(moveSensitivity * time * speedUpVal);
        move(left);
    }

    public void strafeRight(double time) {
        updateForward();
        Vector3D right = forward.cross(new Vector3D(0,1,0)).mult(moveSensitivity * time * speedUpVal);
        move(right);
    }

    public void strafeUp(double time) {
        updateForward();
        Vector3D up = new Vector3D(0,1,0).mult(moveSensitivity * time * speedUpVal);
        move(up);
    }

    public void strafeDown(double time) {
        updateForward();
        Vector3D up = new Vector3D(0,-1,0).mult(moveSensitivity * time * speedUpVal);
        move(up);
    }

    public void pitchRight(double time) {
        pitch -= rotateSensitivity *time;
        needCalc = true;
    }

    public void pitchLeft(double time) {
        pitch += rotateSensitivity *time;
        needCalc = true;
    }

    public void yawDown(double time) {
        yaw -= rotateSensitivity *time;
        needCalc = true;
    }

    public void yawUp(double time) {
        yaw += rotateSensitivity *time;
        needCalc = true;
    }

    public void moveCamera(ArrayList<MyListener.Key> keyList, double time) {
        if(keyList.contains(MyListener.Key.shift)) {
            speedUpVal = 4;
        } else {
            speedUpVal = 1;
        }
        for(MyListener.Key key : keyList) {
           // System.out.println(key.name());
            switch(key) {
                case w:
                    moveForward(time);
                    break;
                case a:
                    strafeLeft(time);
                    break;
                case s:
                    moveBackward(time);
                    break;
                case d:
                    strafeRight(time);
                    break;
                case q:
                    strafeUp(time);
                    break;
                case e:
                    strafeDown(time);
                    break;
                case left:
                    pitchLeft(time);
                    break;
                case up:
                    yawUp(time);
                    break;
                case right:
                    pitchRight(time);
                    break;
                case down:
                    yawDown(time);
            }
        }
    }
}
