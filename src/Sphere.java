import com.jogamp.opengl.GL4;
import graphicslib3D.Vertex3D;

public class Sphere extends Shape {

    public Sphere(GL4 gl, float height, String texture) {
        genCoords(gl, height, texture);
    }

    public void genCoords(GL4 gl, float h, String texture) {
        graphicslib3D.shape.Sphere mySphere = new graphicslib3D.shape.Sphere(24);

        Vertex3D[] vertices = mySphere.getVertices();
        int[] indices = mySphere.getIndices();

        float[] positionCoords = new float[indices.length*3];
        float[] textureCoords = new float[indices.length*2];
        float[] normalVectors = new float[indices.length*3];
        for (int i=0; i<indices.length; i++)
        {	positionCoords[i*3] = (float) (vertices[indices[i]]).getX() * h;
            positionCoords[i*3+1] = (float) (vertices[indices[i]]).getY() * h;
            positionCoords[i*3+2] = (float) (vertices[indices[i]]).getZ() * h;
            textureCoords[i*2] = (float) (vertices[indices[i]]).getS();
            textureCoords[i*2+1] = (float) (vertices[indices[i]]).getT();
            normalVectors[i*3] = (float) (vertices[indices[i]]).getNormalX();
            normalVectors[i*3+1] = (float) (vertices[indices[i]]).getNormalY();
            normalVectors[i*3+2] = (float) (vertices[indices[i]]).getNormalZ();
        }
        setup(gl, positionCoords, textureCoords, normalVectors, texture, Shape.defaultMaterial);
    }
}
