import com.jogamp.opengl.GL4;
import graphicslib3D.Material;

public class Object extends Shape {

    ModelImporter myModel;

    public Object(GL4 gl, String objFilename, String secondFilename, boolean isMtl) {
        fillBuffers(gl, objFilename, secondFilename, isMtl);
    }

    private void fillBuffers(GL4 gl, String objFilename, String secondFilename, boolean isMtl) {
        myModel = new ModelImporter(objFilename, secondFilename, isMtl);
        setupVBO(gl);
    }

    public void setupVBO(GL4 gl) {
        for (ModelGroup group : myModel.groups) {
            float[] vertices = group.getVertices();
            float[] textureCoords = group.getTextureCoordinates();
            float[] normalVectors = group.getNormals();
            Material material = group.material;
            String texture = group.texture;
            System.out.println("group: " + group.groupName + " has texture: " + group.texture);
            setup(gl, vertices, textureCoords, normalVectors, texture, material);
        }
    }
}
