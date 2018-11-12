import com.jogamp.opengl.GL4;
import graphicslib3D.Matrix3D;

import static com.jogamp.opengl.GL.*;

public class Skybox extends Shape {

    Camera camera;

    public Skybox(GL4 gl, Camera camera, String texture) {
        setupVertices(gl, 1, texture);
        this.camera = camera;
    }

    private void setupVertices(GL4 gl, int size, String texture)
    {
        float oneThird = 1f/3f;
        float twoThirds = 2f/3f;

        float[] positionCoords = new float[0];
        float[] textureCoords = new float[0];
        float[] normalVectors = new float[0];
        float[][] result = addSquare(positionCoords, textureCoords, normalVectors,
                new vec3(-size, -size, size), //front
                new vec3( size, -size, size),
                new vec3( size,  size, size),
                new vec3(-size,  size, size));
        positionCoords = result[0];
        textureCoords = result[1];
        //normalVectors = result[2];
        result = addSquare(positionCoords, textureCoords, normalVectors,
                new vec3( size, -size, size), //left
                new vec3( size, -size, -size),
                new vec3( size,  size, -size),
                new vec3( size,  size,  size));
        positionCoords = result[0];
        textureCoords = result[1];
        //normalVectors = result[2];
        result = addSquare(positionCoords, textureCoords, normalVectors,
                new vec3(-size, -size, -size), //right
                new vec3(-size, -size,  size),
                new vec3(-size,  size,  size),
                new vec3(-size,  size, -size));
        positionCoords = result[0];
        textureCoords = result[1];
        //normalVectors = result[2];
        result = addSquare(positionCoords, textureCoords, normalVectors,
                new vec3( size, -size, -size), //back
                new vec3(-size, -size, -size),
                new vec3(-size,  size, -size),
                new vec3( size,  size, -size));
        positionCoords = result[0];
        textureCoords = result[1];
        //normalVectors = result[2];
        result = addSquare(positionCoords, textureCoords, normalVectors,
                new vec3(-size,  size,  size), //top
                new vec3( size,  size,  size),
                new vec3( size,  size, -size),
                new vec3(-size,  size, -size));
        positionCoords = result[0];
        textureCoords = result[1];
        //normalVectors = result[2];
        result = addSquare(positionCoords, textureCoords, normalVectors,
                new vec3(-size, -size, -size), //bottom
                new vec3( size, -size, -size),
                new vec3( size, -size,  size),
                new vec3(-size, -size,  size));
        positionCoords = result[0];
        textureCoords = result[1];
        //normalVectors = result[2];
        textureCoords = new float[]
                {       .50f, oneThird, .25f, oneThird, .50f, twoThirds,
                        .50f, twoThirds, .25f, oneThird, .25f, twoThirds,

                        .25f, oneThird,  0.0f, oneThird, .25f, twoThirds, //left
                        .25f, twoThirds,  0.0f, oneThird, 0.0f, twoThirds,

                        .75f, oneThird, .50f, oneThird, .75f, twoThirds, // right
                        .75f, twoThirds, .50f, oneThird, .50f, twoThirds,

                        1.0f, oneThird,  .75f, oneThird, 1.0f, twoThirds,
                        1.0f, twoThirds,  .75f, oneThird, .75f, twoThirds, //back

                        .50f, twoThirds,  .25f, twoThirds, .50f, 1.0f, //top
                        .50f, 1.0f,  .25f, twoThirds, .25f, 1.0f,

                        .50f, 0.0f, .25f, 0.0f, .50f, oneThird,
                        .50f, oneThird, .25f, 0.0f, .25f, oneThird
                };
        System.out.println("positionCoords.length: " + positionCoords.length);
        System.out.println("textureCoords.length: " + textureCoords.length);
        System.out.println("normalVectors.length: " + normalVectors.length);
        normalVectors = null;
        setup(gl,positionCoords, textureCoords, normalVectors, texture, Shape.defaultMaterial);
    }

    public void display(GL4 gl, int rendering_program, Matrix3D pMat, Matrix3D vMat) {
        position.setX(camera.position.getX());
        position.setY(camera.position.getY());
        position.setZ(camera.position.getZ());
        update();

        int m_loc = gl.glGetUniformLocation(rendering_program, "m_matrix");
        int v_loc = gl.glGetUniformLocation(rendering_program, "v_matrix");
        int proj_loc = gl.glGetUniformLocation(rendering_program, "proj_matrix");

        gl.glUniformMatrix4fv(m_loc, 1, false, mMat.getFloatValues(), 0);
        gl.glUniformMatrix4fv(v_loc, 1, false, vMat.getFloatValues(), 0);
        gl.glUniformMatrix4fv(proj_loc, 1, false, pMat.getFloatValues(), 0);

        glSettings(gl);
        drawVBOs(gl);
        mMat.setToIdentity();
    }

    public void drawVBOs(GL4 gl) {
        for(int i = 0; i < vbos.size(); i++) {
            //System.out.println("Drawing SKybox group: " + vbos.get(i)[0] + " " + vbos.get(i)[1] + " " + vbos.get(i)[2]);
            gl.glBindBuffer(GL_ARRAY_BUFFER, vbos.get(i)[0]);
            gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
            gl.glEnableVertexAttribArray(0);

            gl.glBindBuffer(GL_ARRAY_BUFFER, vbos.get(i)[1]);
            gl.glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
            gl.glEnableVertexAttribArray(1);

            gl.glActiveTexture(GL_TEXTURE0);
            gl.glBindTexture(GL_TEXTURE_2D, textures.get(i));

            gl.glDrawArrays(GL_TRIANGLES, 0, numVerts.get(i));
        }
    }

    @Override
    protected void glSettings(GL4 gl) {

        gl.glDepthFunc(GL_LEQUAL);
        gl.glEnable(GL_CULL_FACE);
        gl.glFrontFace(GL_CW);
        gl.glDisable(GL_DEPTH_TEST);
    }
}
