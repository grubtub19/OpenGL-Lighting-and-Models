import com.jogamp.opengl.GL4;
import com.jogamp.opengl.glu.GLU;
import com.sun.javafx.geom.transform.GeneralTransform3D;
import graphicslib3D.Matrix3D;
import graphicslib3D.Point3D;
import graphicslib3D.Vector3D;
import graphicslib3D.light.PositionalLight;
import sun.java2d.loops.FillRect;

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
    private int depthMapFBO;
    private int depthCubemap;

    public PosLight(GL4 gl, int mapResolution) {
        light = new PositionalLight();
        shadowWidth = mapResolution;
        shadowHeight = mapResolution;

        int[] frameBufferBuffer = new int[1];
        gl.glGenFramebuffers(1, frameBufferBuffer, 0);
        depthMapFBO = frameBufferBuffer[0];

        int[] depthCubemapBuffer = new int[1];
        gl.glGenTextures(1, depthCubemapBuffer, 0);
        depthCubemap = depthCubemapBuffer[0];

        gl.glBindTexture(GL_TEXTURE_CUBE_MAP, depthCubemap);

        gl.glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        gl.glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        gl.glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        gl.glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        gl.glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_R, GL_CLAMP_TO_EDGE);


        for(int i = 0; i < 6; i++) {
            gl.glTexImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GL_DEPTH_COMPONENT32, shadowWidth, shadowHeight,
                    0, GL_DEPTH_COMPONENT, GL_FLOAT, null);
        }

        gl.glBindFramebuffer(GL_FRAMEBUFFER, depthMapFBO);
        gl.glFramebufferTexture(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, depthCubemap, 0);
        gl.glDrawBuffer(GL_NONE);
        gl.glReadBuffer(GL_NONE);
        int ere = gl.glCheckFramebufferStatus(GL_FRAMEBUFFER);
        if(ere != GL_FRAMEBUFFER_COMPLETE) {
            System.out.println("GL_FRAMEBUFFER is incomplete");
        }
        gl.glBindFramebuffer(GL_FRAMEBUFFER, depthMapFBO);
        //System.out.println("initial depthMapFBO: " + depthMapFBO);
        //System.out.println("initial depthCubemap: " + depthCubemap);
    }

    public int genShadowMap(GL4 gl, int rendering_program, ArrayList<Shape> shapes) {

        float nearPlane = 0.0f;
        float farPlane = 40.0f;
        Matrix3D shadowPMat = Solar.perspective(90f, shadowWidth/shadowHeight, nearPlane, farPlane);

        ArrayList<Matrix3D> shadowTransforms = new ArrayList<>();
        /*
        Matrix3D matrix1 = new Matrix3D();
        matrix1.concatenate(shadowPMat);
        matrix1.concatenate(lookAt(light.getPosition(), light.getPosition().add(new Point3D(1.0f, 0.0f, 0.0f)),
                new Vector3D(0.0f, -1.0f, 0.0f)));
        shadowTransforms.add(matrix1);
        Matrix3D matrix2 = new Matrix3D();
        matrix2.concatenate(shadowPMat);
        matrix2.concatenate(lookAt(light.getPosition(), light.getPosition().add(new Point3D(-1.0f, 0.0f, 0.0f)),
                new Vector3D(0.0f, -1.0f, 0.0f)));
        shadowTransforms.add(matrix2);
        Matrix3D matrix3 = new Matrix3D();
        matrix3.concatenate(shadowPMat);
        matrix3.concatenate(lookAt(light.getPosition(), light.getPosition().add(new Point3D(0.0f, 1.0f, 0.0f)),
                new Vector3D(0.0f, 0.0f, 1.0f)));
        shadowTransforms.add(matrix3);
        Matrix3D matrix4 = new Matrix3D();
        matrix4.concatenate(shadowPMat);
        matrix4.concatenate(lookAt(light.getPosition(), light.getPosition().add(new Point3D(0.0f, -1.0f, 0.0f)),
                new Vector3D(0.0f, 0.0f, -1.0f)));
        shadowTransforms.add(matrix4);
        Matrix3D matrix5 = new Matrix3D();
        matrix5.concatenate(shadowPMat);
        matrix5.concatenate(lookAt(light.getPosition(), light.getPosition().add(new Point3D(0.0f, 0.0f, 1.0f)),
                new Vector3D(0.0f, -1.0f, 0.0f)));
        shadowTransforms.add(matrix5);
        Matrix3D matrix6 = new Matrix3D();
        matrix6.concatenate(shadowPMat);
        matrix6.concatenate(lookAt(light.getPosition(), light.getPosition().add(new Point3D(0.0f, 0.0f, -1.0f)),
                new Vector3D(0.0f, -1.0f, 0.0f)));
        shadowTransforms.add(matrix6);
        */
        Matrix3D matrix1 = new Matrix3D(new float[] { 0,0,-1,0,0,-1,0,0,-1,0,0,0,0,0,0,1 });
        matrix1.translate(-light.getPosition().getX(), -light.getPosition().getY(), -light.getPosition().getZ());
        shadowTransforms.add(matrix1);
        Matrix3D matrix2 = new Matrix3D(new float[] { 1,0,0,0,0,0,-1,0,0,1,0,0,0,0,0,1 });
        matrix2.translate(-light.getPosition().getX(), -light.getPosition().getY(), -light.getPosition().getZ());
        shadowTransforms.add(matrix2);
        Matrix3D matrix3 = new Matrix3D(new float[] { 1,0,0,0,0,-1,0,0,0,0,-1,0,0,0,0,1 });
        matrix3.translate(-light.getPosition().getX(), -light.getPosition().getY(), -light.getPosition().getZ());
        shadowTransforms.add(matrix3);
        Matrix3D matrix4 = new Matrix3D(new float[] { 0,0,1,0,0,-1,0,0,1,0,0,0,0,0,0,1 });
        matrix4.translate(-light.getPosition().getX(), -light.getPosition().getY(), -light.getPosition().getZ());
        shadowTransforms.add(matrix4);
        Matrix3D matrix5 = new Matrix3D(new float[] { 1,0,0,0,0,0,1,0,0,-1,0,0,0,0,0,1 });
        matrix5.translate(-light.getPosition().getX(), -light.getPosition().getY(), -light.getPosition().getZ());
        shadowTransforms.add(matrix5);
        Matrix3D matrix6 = new Matrix3D(new float[] { -1,0,0,0,0,-1,0,0,0,0,1,0,0,0,0,1 });
        matrix6.translate(-light.getPosition().getX(), -light.getPosition().getY(), -light.getPosition().getZ());
        shadowTransforms.add(matrix6);

        // 1. render scene to depth cubemap
        gl.glViewport(0,0, shadowWidth, shadowHeight);
        gl.glBindFramebuffer(GL_FRAMEBUFFER, depthMapFBO);
        System.out.println("build depthMapFBO: " + depthMapFBO);
        gl.glClear(GL_DEPTH_BUFFER_BIT);

        for(int i = 0; i < 6; i++) {
            int shadowMatrix_loc = gl.glGetUniformLocation(rendering_program, "shadowMatrices[" + i + "]");
            System.out.println("shadowTransforms[" + i + "]: \n" + shadowTransforms.get(i).toString());
            gl.glUniformMatrix4fv(shadowMatrix_loc,1, false, shadowTransforms.get(i).getFloatValues(), 0);
        }

        int lightPos_loc = gl.glGetUniformLocation(rendering_program, "lightPos");
        int far_plane_loc = gl.glGetUniformLocation(rendering_program, "far_plane");

        gl.glUniform3f(lightPos_loc, (float) light.getPosition().getX(), (float) light.getPosition().getY(),
                (float) light.getPosition().getZ());
        gl.glUniform1f(far_plane_loc, farPlane);

        for(Shape shape: shapes) {
            shape.display(gl, rendering_program);
        }
        gl.glBindFramebuffer(GL_FRAMEBUFFER, 0);
        return depthCubemap;
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
}
