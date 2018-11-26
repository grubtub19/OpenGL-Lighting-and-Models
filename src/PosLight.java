import com.jogamp.opengl.GL4;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.math.Matrix4;
import com.sun.javafx.geom.transform.GeneralTransform3D;
import graphicslib3D.Matrix3D;
import graphicslib3D.Point3D;
import graphicslib3D.Vector3D;
import graphicslib3D.light.PositionalLight;
import sun.java2d.loops.FillRect;

import java.awt.geom.Point2D;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import static com.jogamp.opengl.GL.*;
import static com.jogamp.opengl.GL.GL_FRAMEBUFFER;
import static com.jogamp.opengl.GL.GL_NONE;
import static com.jogamp.opengl.GL2ES2.GL_DEPTH_COMPONENT;
import static com.jogamp.opengl.GL2ES2.GL_TEXTURE_WRAP_R;

public class PosLight {
    public PositionalLight light;
    private int shadowWidth;
    private int shadowHeight;
    private int nearPlane;
    public int farPlane;
    private Matrix3D pMatrix;
    private ArrayList<Matrix3D> vMatrix;
    private int depthFrameBuf;
    public int depthCubemap;

    public PosLight(GL4 gl, int mapResolution) {
        light = new PositionalLight();
        shadowWidth = mapResolution;
        shadowHeight = mapResolution;
        nearPlane = 0;
        farPlane = 1000;
        initBuffer(gl);
        initCubeMap(gl);
        bindCubeMap(gl);
        setParams(gl);
        bindBuffer(gl);
    }

    public void initBuffer(GL4 gl) {
        int[] temp = new int[1];
        gl.glGenFramebuffers(1, temp,0);
        depthFrameBuf = temp[0];
    }

    public void initCubeMap(GL4 gl) {
        int[] temp = new int[1];
        gl.glGenTextures(1, temp, 0);
        depthCubemap = temp[0];
    }

    private void bindCubeMap(GL4 gl) {
        gl.glBindTexture(GL_TEXTURE_CUBE_MAP, depthCubemap);

        gl.glTexImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_X, 0, GL_DEPTH_COMPONENT, shadowWidth, shadowHeight, 0, GL_DEPTH_COMPONENT, GL_FLOAT, null);
        gl.glTexImage2D(GL_TEXTURE_CUBE_MAP_NEGATIVE_X, 0, GL_DEPTH_COMPONENT, shadowWidth, shadowHeight, 0, GL_DEPTH_COMPONENT, GL_FLOAT, null);
        gl.glTexImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_Y, 0, GL_DEPTH_COMPONENT, shadowWidth, shadowHeight, 0, GL_DEPTH_COMPONENT, GL_FLOAT, null);
        gl.glTexImage2D(GL_TEXTURE_CUBE_MAP_NEGATIVE_Y, 0, GL_DEPTH_COMPONENT, shadowWidth, shadowHeight, 0, GL_DEPTH_COMPONENT, GL_FLOAT, null);
        gl.glTexImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_Z, 0, GL_DEPTH_COMPONENT, shadowWidth, shadowHeight, 0, GL_DEPTH_COMPONENT, GL_FLOAT, null);
        gl.glTexImage2D(GL_TEXTURE_CUBE_MAP_NEGATIVE_Z, 0, GL_DEPTH_COMPONENT, shadowWidth, shadowHeight, 0, GL_DEPTH_COMPONENT, GL_FLOAT, null);
    }

    private void setParams(GL4 gl) {
        gl.glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        gl.glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        gl.glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        gl.glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        gl.glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_R, GL_CLAMP_TO_EDGE);
    }

    private void bindBuffer(GL4 gl) {
        gl.glBindFramebuffer(GL_FRAMEBUFFER, depthFrameBuf);
        gl.glFramebufferTexture(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, depthCubemap, 0);
        gl.glDrawBuffer(GL_NONE);
        gl.glReadBuffer(GL_NONE);
        gl.glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    public void genShadowMap(GL4 gl, int rendering_program, ArrayList<Shape> shapes) {
        pMatrix = Solar.perspective(90f, ((float)shadowWidth)/((float)shadowHeight), nearPlane, farPlane);

        vMatrix = new ArrayList<>();
        Matrix3D xPositive = new Matrix3D();
        xPositive.concatenate(pMatrix);
        xPositive.concatenate(new Matrix3D(new float[] { 0,0,-1,0,0,-1,0,0,-1,0,0,0,0,0,0,1 }));
        xPositive.translate(-light.getPosition().getX(), -light.getPosition().getY(), -light.getPosition().getZ());
        vMatrix.add(xPositive);
        Matrix3D xNegative = new Matrix3D();
        xNegative.concatenate(pMatrix);
        xNegative.concatenate(new Matrix3D(new float[] { 0,0,1,0,0,-1,0,0,1,0,0,0,0,0,0,1 }));
        xNegative.translate(-light.getPosition().getX(), -light.getPosition().getY(), -light.getPosition().getZ());
        vMatrix.add(xNegative);
        Matrix3D yPositive = new Matrix3D();
        yPositive.concatenate(pMatrix);
        yPositive.concatenate(new Matrix3D(new float[] { 1,0,0,0,0,0,-1,0,0,1,0,0,0,0,0,1 }));
        yPositive.translate(-light.getPosition().getX(), -light.getPosition().getY(), -light.getPosition().getZ());
        vMatrix.add(yPositive);
        Matrix3D yNegative = new Matrix3D();
        yNegative.concatenate(pMatrix);
        yNegative.concatenate(new Matrix3D(new float[] { 1,0,0,0,0,0,1,0,0,-1,0,0,0,0,0,1 }));
        yNegative.translate(-light.getPosition().getX(), -light.getPosition().getY(), -light.getPosition().getZ());
        vMatrix.add(yNegative);
        Matrix3D zPositive = new Matrix3D();
        zPositive.concatenate(pMatrix);
        zPositive.concatenate(new Matrix3D(new float[] { 1,0,0,0,0,-1,0,0,0,0,-1,0,0,0,0,1 }));
        zPositive.translate(-light.getPosition().getX(), -light.getPosition().getY(), -light.getPosition().getZ());
        vMatrix.add(zPositive);
        Matrix3D zNegative = new Matrix3D();
        zNegative.concatenate(pMatrix);
        zNegative.concatenate(new Matrix3D(new float[] { -1,0,0,0,0,-1,0,0,0,0,1,0,0,0,0,1 }));
        zNegative.translate(-light.getPosition().getX(), -light.getPosition().getY(), -light.getPosition().getZ());
        vMatrix.add(zNegative);

        gl.glViewport(0, 0, shadowWidth, shadowHeight);
        gl.glBindFramebuffer(GL_FRAMEBUFFER, depthFrameBuf);
        gl.glClear(GL_DEPTH_BUFFER_BIT);

        for(int i = 0; i < 6; i++) {
            int temp = gl.glGetUniformLocation(rendering_program, "viewMatrices[" + i + "]");
            gl.glUniformMatrix4fv(temp, 1, false, vMatrix.get(i).getFloatValues(), 0);
        }

        int lightPos_loc = gl.glGetUniformLocation(rendering_program, "lightPos");
        int far_loc = gl.glGetUniformLocation(rendering_program, "far_plane");
        gl.glUniform3f(lightPos_loc, (float) light.getPosition().getX(), (float) light.getPosition().getY(), (float) light.getPosition().getZ());
        gl.glUniform1f(far_loc, farPlane);

        for(Shape shape: shapes) {
            shape.display(gl, rendering_program);
        }
    }

    private Matrix3D lookAt(Point3D point, Point3D point2, Vector3D realUp) {
        Vector3D vector = new Vector3D(point);
        Vector3D vector2 = new Vector3D(point2);
        new Vector3D(point);
        Vector3D forward = vector2.minus(vector).normalize();
        Vector3D left = forward.cross(realUp).normalize();
        Vector3D up = left.cross(forward).normalize();
        Matrix3D rotMatrix = new Matrix3D(new double[] {left.getX(), up.getX(), -forward.getX(), 0.0f,
                left.getY(), up.getY(), -forward.getY(), 0.0f,
                left.getZ(), up.getZ(), -forward.getZ(), 0.0f,
                -left.dot(vector), -up.dot(vector), -forward.mult(-1).dot(vector), 1.0f});
        return rotMatrix;
    }

    private Matrix3D lookAt(Point3D eye, Point3D at, Point3D up) {
        Vector3D zaxis = new Vector3D(eye.minus(at)).normalize();
        Vector3D xaxis = zaxis.cross(new Vector3D(up)).normalize();
        Vector3D yaxis = xaxis.cross(zaxis);

        zaxis = zaxis.mult(-1);

        Matrix3D matrix = new Matrix3D( new float[] { (float) xaxis.getX(), (float) yaxis.getX(), (float) zaxis.getX(), 0,
                (float) xaxis.getY(), (float) yaxis.getY(), (float) zaxis.getZ(), 0,
                (float) xaxis.getZ(), (float) yaxis.getZ(), (float) zaxis.getZ(), 0,
                (float) -xaxis.dot(new Vector3D(eye)), (float) -yaxis.dot(new Vector3D(eye)), (float) -zaxis.dot(new Vector3D(eye)), 1 });

        return matrix;
    }
}
