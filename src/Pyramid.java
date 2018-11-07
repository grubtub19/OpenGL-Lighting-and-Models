import com.jogamp.opengl.GL4;

public class Pyramid extends Shape {

    public Pyramid(GL4 gl, float height, String texture) {
        genCoords(height);
        setup(gl, texture);
    }

    public void genCoords(float height) {
        posCoords = new float[]
                {	-height, -height, height, height, -height, height, 0.0f, height, 0.0f,   //front
                        height, -height, height, height, -height, -height, 0.0f, height, 0.0f,   //right
                        height, -height, -height, -height, -height, -height, 0.0f, height, 0.0f, //back
                        -height, -height, -height, -height, -height, height, 0.0f, height, 0.0f, //left
                        -height, -height, -height, height, -height, height, -height, -height, height, //LF
                        height, -height, height, -height, -height, -height, height, -height, -height  //RR
                };
        texCoords = new float[]
                {	0.0f, 0.0f, 1.0f, 0.0f, 0.5f, 1.0f,
                        0.0f, 0.0f, 1.0f, 0.0f, 0.5f, 1.0f,
                        0.0f, 0.0f, 1.0f, 0.0f, 0.5f, 1.0f,
                        0.0f, 0.0f, 1.0f, 0.0f, 0.5f, 1.0f,
                        0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f,
                        1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f
                };
    }
}
