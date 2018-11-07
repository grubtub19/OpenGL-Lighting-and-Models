import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL4;
import graphicslib3D.Matrix3D;
import graphicslib3D.Vector3D;
import java.nio.FloatBuffer;
import static com.jogamp.opengl.GL.*;

public class Line {
    private float[] posCoords;
    private int[] vbo = new int[1];
    float[] col;
    Matrix3D mMat;

    public Line(GL4 gl, String axis, Vector3D p1, Vector3D p2){
        posCoords = new float[] {(float)p1.getX(), (float)p1.getY(), (float)p1.getZ(), (float)p2.getX(), (float)p2.getY(), (float)p2.getZ()};
        switch(axis) {
            case "x":
                col = new float[] { 1.0f, 0.0f, 0.0f, 1.0f};
                break;
            case "y":
                col = new float[] { 0.0f, 0.0f, 1.0f, 1.0f};
                break;
            case "z":
                col = new float[] { 0.0f, 1.0f, 0.0f, 1.0f};
                break;
        }
        setupArrays(gl);
        mMat = new Matrix3D();
    }

    public void setupArrays(GL4 gl) {
        gl.glGenBuffers(vbo.length, vbo, 0);
        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[0]);
        FloatBuffer pyrBuf = Buffers.newDirectFloatBuffer(posCoords);
        gl.glBufferData(GL_ARRAY_BUFFER, pyrBuf.limit()*4, pyrBuf, GL_STATIC_DRAW);
    }

    public void display(GL4 gl, int rendering_program, Matrix3D pMat, Matrix3D vMat) {
        int m_loc = gl.glGetUniformLocation(rendering_program, "m_matrix");
        int v_loc = gl.glGetUniformLocation(rendering_program, "v_matrix");
        int proj_loc = gl.glGetUniformLocation(rendering_program, "proj_matrix");
        int color_loc = gl.glGetUniformLocation(rendering_program, "colorVec");

        gl.glUniformMatrix4fv(m_loc, 1, false, mMat.getFloatValues(), 0);
        gl.glUniformMatrix4fv(v_loc, 1, false, vMat.getFloatValues(), 0);
        gl.glUniformMatrix4fv(proj_loc, 1, false, pMat.getFloatValues(), 0);
        gl.glUniform4fv(color_loc, 1, Buffers.newDirectFloatBuffer(col));

        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[0]);
        gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
        gl.glEnableVertexAttribArray(0);

        gl.glEnable(GL_DEPTH_TEST);
        gl.glDepthFunc(GL_LEQUAL);
        gl.glLineWidth(2.0f);

        gl.glDrawArrays(GL_LINES, 0, 2);
    }
}
