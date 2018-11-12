import com.jogamp.opengl.GL4;

public class Pyramid extends Shape {

    public Pyramid(GL4 gl, float height, String texture) {
        genCoords(gl, height, texture);
    }

    public void genCoords(GL4 gl, float height, String texture) {
        float[] positionCoords = new float[]
                {	-height, -height, height, height, -height, height, 0.0f, height, 0.0f,   //front
                        height, -height, height, height, -height, -height, 0.0f, height, 0.0f,   //right
                        height, -height, -height, -height, -height, -height, 0.0f, height, 0.0f, //back
                        -height, -height, -height, -height, -height, height, 0.0f, height, 0.0f, //left
                        -height, -height, -height, height, -height, height, -height, -height, height, //LF
                        height, -height, height, -height, -height, -height, height, -height, -height  //RR
                };
        float[] textureCoords = new float[]
                {	0.0f, 0.0f, 1.0f, 0.0f, 0.5f, 1.0f,
                        0.0f, 0.0f, 1.0f, 0.0f, 0.5f, 1.0f,
                        0.0f, 0.0f, 1.0f, 0.0f, 0.5f, 1.0f,
                        0.0f, 0.0f, 1.0f, 0.0f, 0.5f, 1.0f,
                        0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f,
                        1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f
                };
        float[] normalVectors = null;
        setup(gl, positionCoords, textureCoords, normalVectors, texture, Shape.defaultMaterial);
    }
}
