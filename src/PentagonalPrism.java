import com.jogamp.opengl.GL4;
import graphicslib3D.Vector3D;

import java.util.ArrayList;

public class PentagonalPrism extends Shape {

    public PentagonalPrism(GL4 gl, float height, String texture) {
        genCoords(height);
        setup(gl, texture);
    }

    private ArrayList<Vector3D> verts = new ArrayList<>();
    private int i = 0;
    private int t = 0;
    private float height;

    public void genCoords(float h) {
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

        posCoords = new float[144];
        texCoords = new float[96];

        doFace(0);
        doSquare(0);
        doSquare(1);
        doSquare(2);
        doSquare(3);
        doSquare(4);
        doFace(5);
    }

    private void doFace(int i) {
        System.out.println("verts.size(): " + verts.size());
        System.out.println("verts.get(0): " + verts.get(0).toString());
        addPoint(verts.get(0 + i));
        addPoint(verts.get(1 + i));
        addPoint(verts.get(2 + i));
        addPoint(verts.get(0 + i));
        addPoint(verts.get(2 + i));
        addPoint(verts.get(3 + i));
        addPoint(verts.get(0 + i));
        addPoint(verts.get(3 + i));
        addPoint(verts.get(4 + i));

        double up = verts.get(0 + i).getZ()/height/2.0f+.5;
        double down = verts.get(4 + i).getZ()/height/2.0f+.5;
        double sideSmall = verts.get(4 + i).getX()/height/2.0f+.5;
        double sideLarge = verts.get(0 + i).getX()/height/2.0f+.5;

        addTexPoint(sideLarge,1-up);
        addTexPoint(0.5,1);
        addTexPoint(1 - sideLarge,1-up);
        addTexPoint(sideLarge,1-up);
        addTexPoint(1 - sideLarge,1-up);
        addTexPoint(1 - sideSmall, 1-down);
        addTexPoint(sideLarge,1-up);
        addTexPoint(1 - sideSmall, 1-down);
        addTexPoint(sideSmall, 1-down);
    }

    private void doSquare(int i) {
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

        addPoint(verts.get(v1));
        addPoint(verts.get(v2));
        addPoint(verts.get(v3));
        addPoint(verts.get(v1));
        addPoint(verts.get(v3));
        addPoint(verts.get(v4));

        addTexPoint(0,0);
        addTexPoint(1,0);
        addTexPoint(1,1);
        addTexPoint(0,0);
        addTexPoint(1,1);
        addTexPoint(0,1);
    }

    private void addPoint(Vector3D point) {
        posCoords[i] = (float)point.getX();
        posCoords[i+1] = (float)point.getY();
        posCoords[i+2] = (float)point.getZ();
        System.out.print("posCoords[" + i + "]: " + posCoords[i] + ", ");
        System.out.print("posCoords[" + (i+1) + "]: " + posCoords[i+1] + ", ");
        System.out.println("posCoords[" + (i+2) + "]: " + posCoords[i+2]);

        i += 3;
    }

    private void addTexPoint(double v1, double v2) {
        texCoords[t] = (float)v1;
        texCoords[t+1] = (float)v2;
        System.out.print("texCoords[" + t + "]: " + texCoords[t] + ", ");
        System.out.println("texCoords[" + (t+1) + "]: " + texCoords[t+1]);
        t += 2;
    }
}
