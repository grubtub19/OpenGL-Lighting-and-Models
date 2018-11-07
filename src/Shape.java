import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL4;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;
import graphicslib3D.Matrix3D;
import graphicslib3D.MatrixStack;
import graphicslib3D.Point3D;
import graphicslib3D.Vector3D;
import java.io.File;
import java.nio.FloatBuffer;
import static com.jogamp.opengl.GL.*;
import static com.jogamp.opengl.GL.GL_TRIANGLES;

public class Shape {
    protected Point3D position = new Point3D(0,0,0);
    protected float rotX = 0, rotY = 0, rotZ = 0;
    protected float[] posCoords, texCoords;
    protected int[] vbo = new int[2];
    protected int texture;
    Matrix3D mMat = new Matrix3D();
    private float orbitXSpeed = 0;
    private float orbitYSpeed = 0;
    private float orbitZSpeed = 0;
    public float orbitDistance = 1;

    public Shape() {
        posCoords = new float[0];
        texCoords = new float[0];
    }

    public float getX() { return (float)position.getX(); }

    public float getY() { return (float)position.getY(); }

    public float getZ() { return (float)position.getZ(); }

    public void setX(float x) { position.setX(x); }

    public void setY(float y) { position.setY(y); }

    public void setZ(float z) { position.setZ(z); }

    public void move(Point3D point) { position = position.add(point); }

    public void move(Vector3D vector) {
        position.setX(position.getX() + vector.getX());
        position.setY(position.getY() + vector.getY());
        position.setZ(position.getZ() + vector.getZ());
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

    public void setOrbit(float distance, float speed, String axis) {
        orbitDistance = distance;
        if (axis == "x") {
            orbitXSpeed = speed;
            orbitYSpeed = 0;
            orbitZSpeed = 0;
        }
        else if (axis == "y") {
            orbitYSpeed = speed;
            orbitXSpeed = 0;
            orbitZSpeed = 0;
        }
        else if (axis == "z") {
            orbitZSpeed = speed;
            orbitXSpeed = 0;
            orbitYSpeed = 0;
        }
    }

    public float getOrbitXComp(double amt) {
        //System.out.println("orbitSpeedX: " + orbitXSpeed + ", orbitSpeedY: " + orbitYSpeed + ", orbitSpeedZ: " + orbitZSpeed);
        //System.out.println("orbitX: " + (float)(-Math.sin(amt*orbitZSpeed) + Math.cos(amt*orbitYSpeed)));
        float rtn = 0;
        if(orbitYSpeed != 0) {
            rtn += Math.cos(amt*orbitYSpeed);
        }
        if(orbitZSpeed != 0) {
            rtn -= Math.sin(amt*orbitZSpeed);
        }
        return rtn;
    }

    public float getOrbitYComp(double amt) {
        //System.out.println("orbitY: " + (float)(-Math.sin(amt*orbitXSpeed) + Math.cos(amt*orbitZSpeed)));
        float rtn = 0;
        if(orbitXSpeed != 0) {
            rtn -= Math.sin(amt*orbitXSpeed);
        }
        if(orbitZSpeed != 0) {
            rtn += Math.cos(amt*orbitZSpeed);
        }
        return rtn;
    }

    public float getOrbitZComp(double amt) {
        //System.out.println("orbitZ: " + (float)(-Math.sin(amt*orbitYSpeed) + Math.cos(amt*orbitXSpeed)));
        float rtn = 0;
        if(orbitXSpeed != 0) {
            rtn += Math.cos(amt*orbitXSpeed);
        }
        if(orbitYSpeed != 0) {
            rtn -= Math.sin(amt*orbitYSpeed);
        }
        return rtn;
    }

    public void orbitTopOfStack(MatrixStack mStack, double time) {
        mStack.pushMatrix();
        mStack.translate(getOrbitXComp(time) * orbitDistance, getOrbitYComp(time) * orbitDistance, getOrbitZComp(time) * orbitDistance);
        mStack.pushMatrix();
        //System.out.println((System.currentTimeMillis()/10.0)*getDegrees());
        if (getDegrees() > 0) {
            mStack.rotate((System.currentTimeMillis() / 10.0) * getDegrees(), getRotCompX(), getRotCompY(), getRotCompZ());
        }
        mMat = mStack.peek();
        mStack.popMatrix();
    }

    public void setRotX(float x) { rotX = x; }

    public void setRotY(float y) { rotY = y; }

    public void setRotZ(float z) { rotZ = z; }

    public void setup(GL4 gl, String texture) {
        this.texture = loadTexture(texture).getTextureObject();

        gl.glGenBuffers(vbo.length, vbo, 0);

        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[0]);
        FloatBuffer pyrBuf = Buffers.newDirectFloatBuffer(posCoords);
        gl.glBufferData(GL_ARRAY_BUFFER, pyrBuf.limit()*4, pyrBuf, GL_STATIC_DRAW);

        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[1]);
        FloatBuffer texBuf = Buffers.newDirectFloatBuffer(texCoords);
        gl.glBufferData(GL_ARRAY_BUFFER, texBuf.limit()*4, texBuf, GL_STATIC_DRAW);
    }

    public Texture loadTexture(String textureFileName)
    {	Texture tex = null;
        try { tex = TextureIO.newTexture(new File(textureFileName), false); }
        catch (Exception e) { e.printStackTrace(); }
        return tex;
    }

    private void update() {
        mMat.translate(position.getX(), position.getY(), position.getZ());
        //mMat.rotate(rotX, rotY, rotZ);
    }

    public void display(GL4 gl, int rendering_program, Matrix3D pMat, Matrix3D vMat) {
        update();

        int m_loc = gl.glGetUniformLocation(rendering_program, "m_matrix");
        int v_loc = gl.glGetUniformLocation(rendering_program, "v_matrix");
        int proj_loc = gl.glGetUniformLocation(rendering_program, "proj_matrix");

        gl.glUniformMatrix4fv(m_loc, 1, false, mMat.getFloatValues(), 0);
        gl.glUniformMatrix4fv(v_loc, 1, false, vMat.getFloatValues(), 0);
        gl.glUniformMatrix4fv(proj_loc, 1, false, pMat.getFloatValues(), 0);

        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[0]);
        gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
        gl.glEnableVertexAttribArray(0);

        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[1]);
        gl.glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
        gl.glEnableVertexAttribArray(1);

        gl.glActiveTexture(GL_TEXTURE0);
        gl.glBindTexture(GL_TEXTURE_2D, texture);

        gl.glEnable(GL_DEPTH_TEST);
        gl.glDepthFunc(GL_LEQUAL);

        gl.glDrawArrays(GL_TRIANGLES, 0, posCoords.length/3);
        mMat.setToIdentity();
    }

    private float[] add(float[] array, vec3 p) {
        float[] temp = new float[array.length + 3];
        System.arraycopy(array, 0, temp, 0, array.length);
        temp[array.length] = p.x;
        temp[array.length + 1] = p.y;
        temp[array.length + 2] = p.z;
        return temp;
    }
    private float[] add(float[] array, vec2 p) {
        float[] temp = new float[array.length + 2];
        System.arraycopy(array, 0, temp, 0, array.length);
        temp[array.length] = p.x;
        temp[array.length + 1] = p.y;
        return temp;
    }

    public enum Pos{ topleft, topright, bottomleft, bottomright, topcenter }

    /**
     * Creates a triangle
     * @param p1 first
     * @param p2 second
     * @param p3 third
     */
    protected void addTriPlane(vec3 p1, vec3 p2, vec3 p3) {
        posCoords = add(posCoords, p1);
        posCoords = add(posCoords, p2);
        posCoords = add(posCoords, p3);
    }

    /**
     * Texture for a triangle with coordinates defined counterclockwise starting from the left-most
     * and then bottom-most coordinate.
     * @param p the position of the middle point where the other points are adjacent.
     */
    protected void addTriTex(Pos p) {
        if(p == Pos.topleft) {
            texCoords = add(texCoords, new vec2(0,0));
            texCoords = add(texCoords, new vec2(1,1));
            texCoords = add(texCoords, new vec2(0,1));
        } else if(p == Pos.topright) {
            texCoords = add(texCoords, new vec2(0,1));
            texCoords = add(texCoords, new vec2(1,0));
            texCoords = add(texCoords, new vec2(1,1));
        } else if(p == Pos.bottomleft) {
            texCoords = add(texCoords, new vec2(0,0));
            texCoords = add(texCoords, new vec2(1,0));
            texCoords = add(texCoords, new vec2(0,1));
        } else if(p == Pos.bottomright) {
            texCoords = add(texCoords, new vec2(0,0));
            texCoords = add(texCoords, new vec2(1,0));
            texCoords = add(texCoords, new vec2(1,1));
        } else if(p == Pos.topcenter) {
            texCoords = add(texCoords, new vec2(0,0));
            texCoords = add(texCoords, new vec2(1,0));
            texCoords = add(texCoords, new vec2(0.5f,1));
        }
    }

    protected void addTex(Vector3D p1, Vector3D p2, Vector3D p3) {

    }

    protected void addTriangle(vec3 p1, vec3 p2, vec3 p3, Pos p) {
        addTriPlane(p1, p2, p3);
        addTriTex(p);
    }
    protected void addSquare(vec3 p1, vec3 p2, vec3 p3, vec3 p4) {
        addTriPlane(p1,p2,p4);
        addTriTex(Pos.bottomleft);
        addTriPlane(p4,p2,p3);
        addTriTex(Pos.topright);
    }
}