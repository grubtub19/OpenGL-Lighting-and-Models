import com.jogamp.opengl.GL4;
import graphicslib3D.Vector3D;

import java.util.ArrayList;

public class PentagonalPrism extends Shape {

    public PentagonalPrism(GL4 gl, float height, String texture) {
        genCoords(gl, height, texture);

    }

    private ArrayList<Vector3D> verts = new ArrayList<>();
    private int i = 0;
    private int t = 0;
    private float height;

    public void genCoords(GL4 gl, float h, String texture) {
        height = h;
        float inc = 360f / 5f;
        float theta = 18;
        verts = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            verts.add(new Vector3D(Math.cos(Math.toRadians(theta))*height, 0.5f*height, -Math.sin(Math.toRadians(theta))*height));
            theta += inc;
            System.out.println("verts.get(" + i + "): " + verts.get(i));
        }
        theta = 18;
        for (int i = 0; i < 5; i++) {
            verts.add(new Vector3D(Math.cos(Math.toRadians(theta))*height, -0.5f*height, -Math.sin(Math.toRadians(theta))*height));
            theta += inc;
            System.out.println("verts.get(" + (i+5) + "): " + verts.get(i+5));
        }

        float[] positionCoords = new float[144];
        float[] textureCoords = new float[96];
        float[] normalVectors = null;
        doFace(positionCoords, textureCoords, 0);
        doSquare(positionCoords, textureCoords, 0);
        doSquare(positionCoords, textureCoords, 1);
        doSquare(positionCoords, textureCoords, 2);
        doSquare(positionCoords, textureCoords, 3);
        doSquare(positionCoords, textureCoords, 4);
        doFace(positionCoords, textureCoords, 5);
        setup(gl, positionCoords, textureCoords, normalVectors, texture, Shape.defaultMaterial);
    }

    private void doFace(float[] positionCoords, float[] textureCoords, int i) {
        System.out.println("verts.size(): " + verts.size());
        System.out.println("verts.get(0): " + verts.get(0).toString());
        addPoint(positionCoords, verts.get(0 + i));
        addPoint(positionCoords, verts.get(1 + i));
        addPoint(positionCoords, verts.get(2 + i));
        addPoint(positionCoords, verts.get(0 + i));
        addPoint(positionCoords, verts.get(2 + i));
        addPoint(positionCoords, verts.get(3 + i));
        addPoint(positionCoords, verts.get(0 + i));
        addPoint(positionCoords, verts.get(3 + i));
        addPoint(positionCoords, verts.get(4 + i));

        double up = verts.get(0 + i).getZ()/height/2.0f+.5;
        double down = verts.get(4 + i).getZ()/height/2.0f+.5;
        double sideSmall = verts.get(4 + i).getX()/height/2.0f+.5;
        double sideLarge = verts.get(0 + i).getX()/height/2.0f+.5;

        addTexPoint(textureCoords, sideLarge,1-up);
        addTexPoint(textureCoords, 0.5,1);
        addTexPoint(textureCoords, 1 - sideLarge,1-up);
        addTexPoint(textureCoords, sideLarge,1-up);
        addTexPoint(textureCoords, 1 - sideLarge,1-up);
        addTexPoint(textureCoords, 1 - sideSmall, 1-down);
        addTexPoint(textureCoords, sideLarge,1-up);
        addTexPoint(textureCoords, 1 - sideSmall, 1-down);
        addTexPoint(textureCoords, sideSmall, 1-down);
    }

    private void doSquare(float[] positionCoords, float[] textureCoords, int i) {
        int v1, v2, v3, v4;
        if(i < 4) {
            v1 = i + 5;
            v2 = i + 6;
            v3 = i + 1;
            v4 = i;
        } else {
            v1 = i + 5;
            v2 = 5;
            v3 = 0;
            v4 = i;
        }

        addPoint(positionCoords, verts.get(v1));
        addPoint(positionCoords, verts.get(v2));
        addPoint(positionCoords, verts.get(v3));
        addPoint(positionCoords, verts.get(v1));
        addPoint(positionCoords, verts.get(v3));
        addPoint(positionCoords, verts.get(v4));

        addTexPoint(textureCoords, 0,0);
        addTexPoint(textureCoords, 1,0);
        addTexPoint(textureCoords, 1,1);
        addTexPoint(textureCoords, 0,0);
        addTexPoint(textureCoords, 1,1);
        addTexPoint(textureCoords, 0,1);
    }

    private void addPoint(float[] positionCoords, Vector3D point) {
        positionCoords[i] = (float)point.getX();
        positionCoords[i+1] = (float)point.getY();
        positionCoords[i+2] = (float)point.getZ();
        System.out.print("positionCoords[" + i + "]: " + positionCoords[i] + ", ");
        System.out.print("positionCoords[" + (i+1) + "]: " + positionCoords[i+1] + ", ");
        System.out.println("positionCoords[" + (i+2) + "]: " + positionCoords[i+2]);

        i += 3;
    }

    private void addTexPoint(float[] textureCoords, double v1, double v2) {
        textureCoords[t] = (float)v1;
        textureCoords[t+1] = (float)v2;
        System.out.print("textureCoords[" + t + "]: " + textureCoords[t] + ", ");
        System.out.println("textureCoords[" + (t+1) + "]: " + textureCoords[t+1]);
        t += 2;
    }
}
