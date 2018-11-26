import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL4;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;
import com.sun.scenario.effect.light.PointLight;
import graphicslib3D.Material;
import graphicslib3D.Matrix3D;
import graphicslib3D.Point3D;
import graphicslib3D.Vector3D;
import graphicslib3D.light.AmbientLight;
import graphicslib3D.light.Light;
import graphicslib3D.light.PositionalLight;

import java.io.File;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import static com.jogamp.opengl.GL.*;
import static com.jogamp.opengl.GL.GL_TRIANGLES;

public class Shape {
    protected Point3D position;
    protected float rotX, rotY, rotZ;
    protected vec3 scale;
    protected ArrayList<int[]> vbos;
    protected ArrayList<Integer> textures;
    protected ArrayList<Integer> numVerts;
    protected ArrayList<Material> materials;
    protected Matrix3D mMat;

    public static Material defaultMaterial = new Material( new float[] { 1.0f, 1.0f, 1.0f, 1.0f },
            new float[] { 1.0f, 1.0f, 1.0f, 1.0f },
            new float[] { 0.5f, 0.5f, 0.5f, 1.0f },
            new float[] { 1.0f, 1.0f, 1.0f, 1.0f },
           30.0f);
    private int material_amb_loc;
    private int material_diff_loc;
    private int material_spec_loc;
    private int material_shiny_loc;

    public Shape() {
        position = new Point3D(0,0,0);
        rotX = 0;
        rotY = 0;
        rotZ = 0;
        scale = new vec3(1, 1, 1);
        vbos = new ArrayList<>();
        textures = new ArrayList<>();
        numVerts = new ArrayList<>();
        mMat = new Matrix3D();
        materials = new ArrayList<>();

    }

    public float getX() { return (float) position.getX(); }

    public float getY() { return (float) position.getY(); }

    public float getZ() { return (float) position.getZ(); }

    public void setX(float x) { position.setX(x); }

    public void setY(float y) { position.setY(y); }

    public void setZ(float z) { position.setZ(z); }

    public void move(Point3D point) { position = position.add(point); }

    public void move(Vector3D vector) {
        position.setX(position.getX() + vector.getX());
        position.setY(position.getY() + vector.getY());
        position.setZ(position.getZ() + vector.getZ());
    }

    public void setPosition(Point3D point) {
        position.setX(point.getX());
        position.setY(point.getY());
        position.setZ(point.getZ());
    }

    public void scale(float x, float y, float z) {
        scale.x = x;
        scale.y = y;
        scale.z = z;
    }

    public float getDegrees() {
        return Math.max(Math.abs(rotX), Math.max(Math.abs(rotY), Math.abs(rotZ)));
    }

    public void setRotX(float x) { rotX = x; }

    public void setRotY(float y) { rotY = y; }

    public void setRotZ(float z) { rotZ = z; }

    /**
     * Adds a vbo to the vbos array
     * @param gl
     * @param positionCoords
     * @param textureCoords
     * @param normalVectors
     * @param texture
     * @param material
     */
    public void setup(GL4 gl, float[] positionCoords, float[] textureCoords, float[] normalVectors, String texture, Material material) {
        vbos.add(new int[3]);
        int vboNum = vbos.size() - 1;
        gl.glGenBuffers(vbos.get(vboNum).length, vbos.get(vboNum), 0);

        gl.glBindBuffer(GL_ARRAY_BUFFER, vbos.get(vboNum)[0]);
        FloatBuffer pyrBuf = Buffers.newDirectFloatBuffer(positionCoords);
        gl.glBufferData(GL_ARRAY_BUFFER, pyrBuf.limit()*4, pyrBuf, GL_STATIC_DRAW);

        if(textureCoords != null && textureCoords.length != 0) {
            gl.glBindBuffer(GL_ARRAY_BUFFER, vbos.get(vboNum)[1]);
            FloatBuffer texBuf = Buffers.newDirectFloatBuffer(textureCoords);
            gl.glBufferData(GL_ARRAY_BUFFER, texBuf.limit() * 4, texBuf, GL_STATIC_DRAW);
        }

        if(normalVectors != null && normalVectors.length != 0) {
            gl.glBindBuffer(GL_ARRAY_BUFFER, vbos.get(vboNum)[2]);
            FloatBuffer norBuf = Buffers.newDirectFloatBuffer(normalVectors);
            gl.glBufferData(GL_ARRAY_BUFFER, norBuf.limit() * 4, norBuf, GL_STATIC_DRAW);
        }

        numVerts.add(positionCoords.length / 3);
        System.out.println("Texture load: " + texture);
        textures.add(loadTexture(texture).getTextureObject());
        materials.add(material);
    }

    public Texture loadTexture(String textureFileName) {
        Texture tex = null;
        try {
            tex = TextureIO.newTexture(new File(textureFileName), false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tex;
    }

    /**
     * Adds the translation, rotation, and scaling values into the model matrix
     */
    protected void update() {
        mMat.translate(position.getX(), position.getY(), position.getZ());
        mMat.rotate(rotX, rotY, rotZ);
        mMat.scale(scale.x, scale.y, scale.z);
    }

    /**
     * sets the OpenGL settings for when the shape is drawn
     * @param gl
     */
    protected void glSettings(GL4 gl) {
        //gl.glClear(GL_DEPTH_BUFFER_BIT);
        gl.glEnable(GL_DEPTH_TEST);
        gl.glDepthFunc(GL_LEQUAL);
        gl.glEnable(GL_CULL_FACE);
        gl.glFrontFace(GL_CCW);
        gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
    }

    /**
     * Sets the uniform values of the ambient light and the positional light in the shader. The light's position coordinates
     * are made relative to the view matrix.
     * @param gl
     * @param rendering_program
     * @param ambient
     * @param lights
     */
    private void addLight(GL4 gl, int rendering_program, AmbientLight ambient, ArrayList<PosLight> lights, boolean shadows) {
        int globalAmbient_loc = gl.glGetUniformLocation(rendering_program, "globalAmbient");
        gl.glUniform4fv(globalAmbient_loc, 1, ambient.getValues(), 0);

        for(int i = 0; i < lights.size(); i++) {
            lights.get(i).addUniforms(gl, rendering_program, i, shadows);
        }
    }

    /**
     * Sets uniform variables of the matrices used for normal rendering
     * @param gl GL4
     * @param rendering_program
     * @param pMat projection matrix
     * @param vMat view matrix
     */
    private void addMatrices(GL4 gl, int rendering_program, Matrix3D pMat, Matrix3D vMat) {
        int m_loc = gl.glGetUniformLocation(rendering_program, "m_matrix");
        int v_loc = gl.glGetUniformLocation(rendering_program, "v_matrix");
        int proj_loc = gl.glGetUniformLocation(rendering_program, "proj_matrix");
        int norm_loc =  gl.glGetUniformLocation(rendering_program, "norm_matrix");

        Matrix3D mv_matrix = new Matrix3D();
        mv_matrix.concatenate(vMat);
        mv_matrix.concatenate(mMat);

        gl.glUniformMatrix4fv(m_loc, 1, false, mMat.getFloatValues(), 0);
        gl.glUniformMatrix4fv(v_loc, 1, false, vMat.getFloatValues(), 0);
        gl.glUniformMatrix4fv(proj_loc, 1, false, pMat.getFloatValues(), 0);
        gl.glUniformMatrix4fv(norm_loc, 1, false, mv_matrix.inverse().transpose().getFloatValues(), 0);
    }

    /**
     * Sets uniform variables of the matrices for shadow mapping
     * @param gl GL4
     * @param rendering_program
     */
    private void addMatrices(GL4 gl, int rendering_program) {
        int model_loc = gl.glGetUniformLocation(rendering_program, "model");

        /*Matrix3D mvp_matrix = new Matrix3D();
        mv_matrix.concatenate(pMat);
        mv_matrix.concatenate(vMat);
        mv_matrix.concatenate(mMat);*/

        gl.glUniformMatrix4fv(model_loc, 1, false, mMat.getFloatValues(), 0);
    }

    /**
     * Draws the shape with lighting and shadows
     * @param gl
     * @param rendering_program
     * @param pMat
     * @param vMat
     * @param globalAmbient
     * @param lights
     * @param useMaterials
     */
    public void displayShadow(GL4 gl, int rendering_program, Matrix3D pMat, Matrix3D vMat, AmbientLight globalAmbient,
                        ArrayList<PosLight> lights, boolean useMaterials) {
        update();
        glSettings(gl);
        addMatrices(gl, rendering_program, pMat, vMat);
        addLight(gl, rendering_program, globalAmbient, lights, true);
        drawVBOs(gl, rendering_program, useMaterials);
        mMat.setToIdentity();
    }

    /**
     * Draws the shape normally with lighting and WITHOUT shadows
     * @param gl
     * @param rendering_program
     * @param pMat projection matrix
     * @param vMat view matrix
     * @param globalAmbient
     * @param lights
     * @param useMaterials
     */
    public void display(GL4 gl, int rendering_program, Matrix3D pMat, Matrix3D vMat, AmbientLight globalAmbient,
                        ArrayList<PosLight> lights, boolean useMaterials) {
        update();
        addMatrices(gl, rendering_program, pMat, vMat);
        addLight(gl, rendering_program, globalAmbient, lights, false);
        glSettings(gl);
        drawVBOs(gl, rendering_program, useMaterials);
        mMat.setToIdentity();
    }

    /**
     * Draws the shape normally WITHOUT lighting or shadows
     * @param gl
     * @param rendering_program
     * @param pMat
     * @param vMat
     */
    public void display(GL4 gl, int rendering_program, Matrix3D pMat, Matrix3D vMat) {
        update();
        addMatrices(gl, rendering_program, pMat, vMat);
        glSettings(gl);
        drawVBOs(gl, rendering_program, false);
        mMat.setToIdentity();
    }

    /**
     * Draws the shape when shadow mapping
     * @param gl
     * @param rendering_program
     */
    public void display(GL4 gl, int rendering_program) {
        update();
        addMatrices(gl, rendering_program);
        //glSettings(gl);
        drawVBOs(gl);
        mMat.setToIdentity();
    }

    /**
     * Calls glDraw() on every ModelGroup (vbo[])
     * @param gl
     * @param rendering_program
     * @param useMaterials
     */
    public void drawVBOs(GL4 gl, int rendering_program, boolean useMaterials) {
        getMaterialLocations(gl, rendering_program);
        if(!useMaterials) {
            changeMaterial(gl, 0, false);
        }

        for(int i = 0; i < vbos.size(); i++) {
            if(useMaterials) {
                changeMaterial(gl, i, true);
            }

            gl.glBindBuffer(GL_ARRAY_BUFFER, vbos.get(i)[0]);
            gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
            gl.glEnableVertexAttribArray(0);

            gl.glBindBuffer(GL_ARRAY_BUFFER, vbos.get(i)[1]);
            gl.glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
            gl.glEnableVertexAttribArray(1);

            gl.glBindBuffer(GL_ARRAY_BUFFER, vbos.get(i)[2]);
            gl.glVertexAttribPointer(2, 3, GL_FLOAT, false, 0, 0);
            gl.glEnableVertexAttribArray(2);

            gl.glActiveTexture(GL_TEXTURE0);
            gl.glBindTexture(GL_TEXTURE_2D, textures.get(i));

            gl.glDrawArrays(GL_TRIANGLES, 0, numVerts.get(i));
        }
    }

    public void drawVBOs(GL4 gl) {
        for(int i = 0; i < vbos.size(); i++) {
            gl.glBindBuffer(GL_ARRAY_BUFFER, vbos.get(i)[0]);
            gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
            gl.glEnableVertexAttribArray(0);

            gl.glDrawArrays(GL_TRIANGLES, 0, numVerts.get(i));
        }
    }

    /**
     * Retrieves the location values for the shape's "material" uniform variable in the shader
     * @param gl
     * @param rendering_program
     */
    private void getMaterialLocations(GL4 gl, int rendering_program) {
        material_amb_loc = gl.glGetUniformLocation(rendering_program, "material.ambient");
        material_diff_loc = gl.glGetUniformLocation(rendering_program, "material.diffuse");
        material_spec_loc = gl.glGetUniformLocation(rendering_program, "material.specular");
        material_shiny_loc = gl.glGetUniformLocation(rendering_program, "material.shininess");
    }

    /**
     * Sets the uniform values of the shape's "material". If this shape doesnt have any materials, uses a default material
     * @param gl
     * @param i
     * @param useMaterials
     */
    private void changeMaterial(GL4 gl, int i, boolean useMaterials) {
        if(useMaterials) {
            gl.glUniform4fv(material_amb_loc, 1, materials.get(i).getAmbient(), 0);
            gl.glUniform4fv(material_diff_loc, 1, materials.get(i).getDiffuse(), 0);
            gl.glUniform4fv(material_spec_loc, 1, materials.get(i).getSpecular(), 0);
            gl.glUniform1f(material_shiny_loc, materials.get(i).getShininess());
        } else {
            gl.glUniform4fv(material_amb_loc, 1, defaultMaterial.getAmbient(), 0);
            gl.glUniform4fv(material_diff_loc, 1, defaultMaterial.getDiffuse(), 0);
            gl.glUniform4fv(material_spec_loc, 1, defaultMaterial.getSpecular(), 0);
            gl.glUniform1f(material_shiny_loc, defaultMaterial.getShininess());
        }
    }

    private static float[] add(float[] array, vec3 p) {
        float[] temp = new float[array.length + 3];
        System.arraycopy(array, 0, temp, 0, array.length);
        temp[array.length] = p.x;
        temp[array.length + 1] = p.y;
        temp[array.length + 2] = p.z;
        return temp;
    }
    private static float[] add(float[] array, vec2 p) {
        float[] temp = new float[array.length + 2];
        System.arraycopy(array, 0, temp, 0, array.length);
        temp[array.length] = p.x;
        temp[array.length + 1] = p.y;
        return temp;
    }

    private static int[] add(int[] array, int num) {
        int[] temp = new int[array.length + 1];
        System.arraycopy(array, 0, temp, 0, array.length);
        temp[array.length] = num;
        return temp;
    }

    public enum Pos{ topleft, topright, bottomleft, bottomright, topcenter }

    /**
     * Creates a triangle
     * @param p1 first
     * @param p2 second
     * @param p3 third
     */
    protected static float[] addTriPlane(float[] positionCoords, vec3 p1, vec3 p2, vec3 p3) {
        positionCoords = add(positionCoords, p1);
        positionCoords = add(positionCoords, p2);
        positionCoords = add(positionCoords, p3);
        return positionCoords;
    }

    protected static float[] addTriNormal(float[] normalVectors, vec3 p1, vec3 p2, vec3 p3, boolean inverse) {
        Vector3D vector1 = new Vector3D(p1.x - p2.x, p1.y - p2.y, p1.z - p2.z);
        Vector3D vector2 = new Vector3D(p2.x - p3.x, p2.y - p3.y, p2.z - p3.z);
        Vector3D result;
        if (inverse) {
             result = vector2.cross(vector1);
        } else {
            result = vector1.cross(vector2);
        }
        vec3 normal= new vec3((float) result.getZ(), (float) result.getY(), (float) result.getZ());
        return add(normalVectors, normal);

    }

    /**
     * Texture for a triangle with coordinates defined counterclockwise starting from the left-most
     * and then bottom-most coordinate.
     * @param p the position of the middle point where the other points are adjacent.
     */
    protected static float[] addTriTex(float[] textureCoords, Pos p) {
        if(p == Pos.topleft) {
            textureCoords = add(textureCoords, new vec2(0,0));
            textureCoords = add(textureCoords, new vec2(1,1));
            textureCoords = add(textureCoords, new vec2(0,1));
        } else if(p == Pos.topright) {
            textureCoords = add(textureCoords, new vec2(0,1));
            textureCoords = add(textureCoords, new vec2(1,0));
            textureCoords = add(textureCoords, new vec2(1,1));
        } else if(p == Pos.bottomleft) {
            textureCoords = add(textureCoords, new vec2(0,0));
            textureCoords = add(textureCoords, new vec2(1,0));
            textureCoords = add(textureCoords, new vec2(0,1));
        } else if(p == Pos.bottomright) {
            textureCoords = add(textureCoords, new vec2(0,0));
            textureCoords = add(textureCoords, new vec2(1,0));
            textureCoords = add(textureCoords, new vec2(1,1));
        } else if(p == Pos.topcenter) {
            textureCoords = add(textureCoords, new vec2(0,0));
            textureCoords = add(textureCoords, new vec2(1,0));
            textureCoords = add(textureCoords, new vec2(0.5f,1));
        }
        return textureCoords;
    }

    protected static void addTex(Vector3D p1, Vector3D p2, Vector3D p3) {

    }

    protected static void addTriangle(float[] positionCoords, float[] textureCoords, vec3 p1, vec3 p2, vec3 p3, Pos p) {
        addTriPlane(positionCoords, p1, p2, p3);
        addTriTex(textureCoords, p);
    }
    protected static float[][] addSquare(float[] positionCoords, float[] textureCoords, float[] normalVectors, vec3 p1, vec3 p2, vec3 p3, vec3 p4) {
        positionCoords = addTriPlane(positionCoords, p1,p2,p4);
        //System.out.println("positionCoords.length: " + positionCoords.length);
        textureCoords = addTriTex(textureCoords, Pos.bottomleft);
        normalVectors = addTriNormal(normalVectors, p1,p2,p4, true);
        positionCoords = addTriPlane(positionCoords, p4,p2,p3);
        textureCoords = addTriTex(textureCoords, Pos.topright);
        normalVectors = addTriNormal(normalVectors, p1,p2,p3, true);
        return new float[][] {positionCoords, textureCoords, normalVectors};
    }
}