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

    public float getRotCompX() {
        float num = rotX / getDegrees();
        if (Float.isNaN(num)) {
            return 0;
        }
        else {
            return num;
        }
    }

    public float getRotCompY() {
        float num = rotY / getDegrees();
        if (Float.isNaN(num)) {
            return 0;
        }
        else {
            return num;
        }
    }

    public float getRotCompZ() {
        float num = rotZ / getDegrees();
        if (Float.isNaN(num)) {
            return 0;
        }
        else {
            return num;
        }
    }

    public void setRotX(float x) { rotX = x; }

    public void setRotY(float y) { rotY = y; }

    public void setRotZ(float z) { rotZ = z; }

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
        textures.add(loadTexture(texture).getTextureObject());
        System.out.println(textures.toString());
        System.out.println("texture int value: " + textures.get(textures.size() - 1) + ", vboNum: " + vboNum);
        System.out.println("vbos.get(" + vboNum + "): " + vbos.get(vboNum)[0] + " " + vbos.get(vboNum)[1] + " " + vbos.get(vboNum)[2]);

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

    protected void update() {
        mMat.translate(position.getX(), position.getY(), position.getZ());
        mMat.rotate(rotX, rotY, rotZ);
        mMat.scale(scale.x, scale.y, scale.z);
    }

    protected void glSettings(GL4 gl) {
        //gl.glClear(GL_DEPTH_BUFFER_BIT);
        gl.glEnable(GL_DEPTH_TEST);
        gl.glDepthFunc(GL_LEQUAL);
        gl.glEnable(GL_CULL_FACE);
        gl.glFrontFace(GL_CCW);
    }

    private void addLight(GL4 gl, int rendering_program, Matrix3D vMat, AmbientLight ambient, PositionalLight light) {
        int globalAmbient_loc = gl.glGetUniformLocation(rendering_program, "globalAmbient");
        int light_amb_loc = gl.glGetUniformLocation(rendering_program, "light.ambient");
        int light_diff_loc = gl.glGetUniformLocation(rendering_program, "light.diffuse");
        int light_spec_loc = gl.glGetUniformLocation(rendering_program, "light.specular");
        int light_pos_loc = gl.glGetUniformLocation(rendering_program, "light.position");

        Point3D lightPv = light.getPosition().mult(vMat);
        float[] viewLight = new float[] { (float) lightPv.getX(), (float) lightPv.getY(), (float) lightPv.getZ() };

        gl.glUniform4fv(globalAmbient_loc, 1, ambient.getValues(), 0);
        gl.glUniform4fv(light_amb_loc, 1, light.getAmbient(), 0);
        gl.glUniform4fv(light_diff_loc, 1, light.getDiffuse(), 0);
        gl.glUniform4fv(light_spec_loc, 1, light.getSpecular(), 0);
        gl.glUniform3fv(light_pos_loc, 1, viewLight, 0);
    }

    private void addMatricies(GL4 gl, int rendering_program, Matrix3D pMat, Matrix3D vMat) {
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

    private void addMatricies(GL4 gl, int rendering_program) {
        int model_loc = gl.glGetUniformLocation(rendering_program, "model");

        /*Matrix3D mvp_matrix = new Matrix3D();
        mv_matrix.concatenate(pMat);
        mv_matrix.concatenate(vMat);
        mv_matrix.concatenate(mMat);*/

        gl.glUniformMatrix4fv(model_loc, 1, false, mMat.getFloatValues(), 0);
    }

    public void display(GL4 gl, int rendering_program, Matrix3D pMat, Matrix3D vMat, AmbientLight globalAmbient,
                        PositionalLight light, int depthCubemap, boolean useMaterials) {
        update();
        addMatricies(gl, rendering_program, pMat, vMat);
        addLight(gl, rendering_program, vMat, globalAmbient, light);
        glSettings(gl);

        gl.glActiveTexture(GL_TEXTURE1);
        gl.glBindTexture(GL_TEXTURE_CUBE_MAP, depthCubemap);
        System.out.println("using depthCubemap: " + depthCubemap);

        drawVBOs(gl, rendering_program, useMaterials);
        mMat.setToIdentity();
    }

    public void display(GL4 gl, int rendering_program, Matrix3D pMat, Matrix3D vMat, AmbientLight globalAmbient,
                        PositionalLight light, boolean useMaterials) {
        update();
        addMatricies(gl, rendering_program, pMat, vMat);
        addLight(gl, rendering_program, vMat, globalAmbient, light);
        glSettings(gl);
        drawVBOs(gl, rendering_program, useMaterials);
        mMat.setToIdentity();
    }

    public void display(GL4 gl, int rendering_program, Matrix3D pMat, Matrix3D vMat) {
        update();
        addMatricies(gl, rendering_program, pMat, vMat);
        glSettings(gl);
        drawVBOs(gl, rendering_program, false);
        mMat.setToIdentity();
    }

    public void display(GL4 gl, int rendering_program) {
        update();
        addMatricies(gl, rendering_program);
        //glSettings(gl);
        drawVBOs(gl, rendering_program, false);
        mMat.setToIdentity();
    }

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

    private void getMaterialLocations(GL4 gl, int rendering_program) {
        material_amb_loc = gl.glGetUniformLocation(rendering_program, "material.ambient");
        material_diff_loc = gl.glGetUniformLocation(rendering_program, "material.diffuse");
        material_spec_loc = gl.glGetUniformLocation(rendering_program, "material.specular");
        material_shiny_loc = gl.glGetUniformLocation(rendering_program, "material.shininess");

    }

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