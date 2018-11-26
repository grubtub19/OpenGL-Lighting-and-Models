import com.jogamp.opengl.GL4;
import graphicslib3D.Material;

public class Model extends Shape {
    boolean useMaterial;
    ModelImporter myModel;

    public Model(GL4 gl, String objFilename, String secondFilename, boolean isMtl, boolean useMaterial) {
        this.useMaterial = useMaterial;
        myModel = new ModelImporter(objFilename, secondFilename, isMtl);
        setupVBO(gl);
    }

    public void setupVBO(GL4 gl) {
        for (ModelGroup group : myModel.groups) {
            float[] vertices = group.getVertices();
            float[] textureCoords = group.getTextureCoordinates();
            float[] normalVectors = group.getNormals();
            Material material;
            if(useMaterial) {
                material = group.material;
            } else {
                material = Shape.defaultMaterial;
            }
            String texture = group.texture;
            System.out.println("group: " + group.groupName + " has texture: " + group.texture);
            setup(gl, vertices, textureCoords, normalVectors, texture, material);
        }
    }
}
