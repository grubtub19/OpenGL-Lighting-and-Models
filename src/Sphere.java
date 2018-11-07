import com.jogamp.opengl.GL4;
import graphicslib3D.Vertex3D;

public class Sphere extends Shape {

    public Sphere(GL4 gl, float height, String texture) {
        genCoords(height);
        setup(gl, texture);
    }

    public void genCoords(float h) {
        graphicslib3D.shape.Sphere mySphere = new graphicslib3D.shape.Sphere(24);

        Vertex3D[] vertices = mySphere.getVertices();
        int[] indices = mySphere.getIndices();

        posCoords = new float[indices.length*3];
        texCoords = new float[indices.length*2];

        for (int i=0; i<indices.length; i++)
        {	posCoords[i*3] = (float) (vertices[indices[i]]).getX() * h;
            posCoords[i*3+1] = (float) (vertices[indices[i]]).getY() * h;
            posCoords[i*3+2] = (float) (vertices[indices[i]]).getZ() * h;
            texCoords[i*2] = (float) (vertices[indices[i]]).getS();
            texCoords[i*2+1] = (float) (vertices[indices[i]]).getT();
        }
    }
}
