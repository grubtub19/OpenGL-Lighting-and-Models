import com.jogamp.opengl.util.FPSAnimator;
import graphicslib3D.*;
import java.nio.*;
import java.util.ArrayList;
import javax.swing.*;
import static com.jogamp.opengl.GL4.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.common.nio.Buffers;

public class Solar extends JFrame implements GLEventListener {
    private GLCanvas myCanvas;
    private int rendering_program;
    private int rendering_program2D;
    private int vao[] = new int[1];
    private Camera camera = new Camera(0,0,6);
    private GLSLUtils util = new GLSLUtils();
    private Shape stars;
    private Shape sun;
    private Shape mercury;
    private Shape marsMoon;
    private Shape venus;
    private Shape earth;
    private Shape moon;
    private Shape mars;
    private Shape jupiter;
    private Shape saturn;
    private Shape uranus;
    private Shape neptune;
    private Shape pluto;
    private Shape penta;
    private ArrayList<Shape> shapes = new ArrayList<>();
    private ArrayList<Line> lines = new ArrayList<>();
    MyListener listen;

    public Solar() {
        setTitle("Solar System");
        setSize(1000, 1000);
        IconHelper.setIconImageFromUrl(this,"http://www.origami-make.org/origami-paper-triangle/Images/origami-paper-triangle.jpg");
        GLProfile profile = GLProfile.get(GLProfile.GL4);
        GLCapabilities capabilities = new GLCapabilities(profile);
        myCanvas = new GLCanvas(capabilities);
        myCanvas.addGLEventListener(this);
        listen = new MyListener();
        myCanvas.addKeyListener(listen);
        getContentPane().add(myCanvas);
        this.setVisible(true);
        FPSAnimator animator = new FPSAnimator(myCanvas, 30);
        animator.start();
    }

    private int createShaderProgram(String d)
    {	GL4 gl = (GL4) GLContext.getCurrentGL();

        String vshaderSource[];
        String fshaderSource[];

        if (d == "2d") {
            vshaderSource = util.readShaderSource("shaders/vertLine.shader");
            fshaderSource = util.readShaderSource("shaders/fragLine.shader");
        } else {
            vshaderSource = util.readShaderSource("shaders/vert.shader");
            fshaderSource = util.readShaderSource("shaders/frag.shader");
        }

        int vShader = gl.glCreateShader(GL_VERTEX_SHADER);
        int fShader = gl.glCreateShader(GL_FRAGMENT_SHADER);

        gl.glShaderSource(vShader, vshaderSource.length, vshaderSource, null, 0);
        gl.glShaderSource(fShader, fshaderSource.length, fshaderSource, null, 0);
        gl.glCompileShader(vShader);
        gl.glCompileShader(fShader);

        int vfprogram = gl.glCreateProgram();
        gl.glAttachShader(vfprogram, vShader);
        gl.glAttachShader(vfprogram, fShader);
        gl.glLinkProgram(vfprogram);
        return vfprogram;
    }

    public static void main(String[] args) {
        new Solar();
    }

    public void init(GLAutoDrawable drawable) {
        GL4 gl = (GL4) GLContext.getCurrentGL();

        rendering_program = createShaderProgram("3d");
        rendering_program2D = createShaderProgram("2d");
        gl.glGenVertexArrays(vao.length, vao, 0);
        gl.glBindVertexArray(vao[0]);

        initShapes(gl);
    }

    public void initShapes(GL4 gl) {
        shapes.add(stars = new Square(gl, 500, "2k_stars.jpg"));
        shapes.add(sun = new Sphere(gl, 2, "8k_sun.jpg"));
        shapes.add(mercury = new Sphere(gl,0.38f, "2k_mercury.jpg"));
        shapes.add(venus = new Sphere(gl,0.95f, "2k_venus_surface.jpg"));
        shapes.add(earth = new Sphere(gl, 1, "earthmap1k.jpg"));
        shapes.add(moon = new Sphere(gl,0.2f, "moon.jpg"));
        shapes.add(mars = new Sphere(gl,0.53f, "2k_mars.jpg"));
        shapes.add(marsMoon = new Sphere(gl, 0.2f, "moon.jpg"));
        shapes.add(jupiter = new Sphere(gl, 11, "2k_jupiter.jpg"));
        shapes.add(saturn = new Sphere(gl, 9f, "2k_saturn.jpg"));
        shapes.add(uranus = new Sphere(gl, 4f, "2k_uranus.jpg"));
        shapes.add(neptune = new Sphere(gl, 3.8f, "2k_neptune.jpg"));
        shapes.add(pluto = new Sphere(gl,0.2f, "moon.jpg"));
        shapes.add(penta = new PentagonalPrism(gl,4, "peter.jpg"));

        sun.setRotY(0.04f);
        mercury.setRotY(0.1f);
        venus.setRotY(-0.1f);
        earth.setRotY(1);
        moon.setRotY(1);
        mars.setRotY(1.4f);
        marsMoon.setRotY(0.5f);
        penta.setRotX(0.5f);
        penta.setRotY(0.5f);
        penta.setRotZ(0.5f);
        jupiter.setRotY(2.1f);
        saturn.setRotY(2.0f);
        uranus.setRotX(-1.6f);
        neptune.setRotY(1.5f);
        pluto.setRotY(2);

        mercury.setOrbit(4, 5, "y");
        venus.setOrbit(7.2f, 1.6f, "y");
        earth.setOrbit(10, 1, "y");
        moon.setOrbit(2f, -10, "y");
        mars.setOrbit(15, 0.5f, "y");
        marsMoon.setOrbit(1.5f, 14, "y");
        penta.setOrbit(4, 8f, "z");
        jupiter.setOrbit(51, 0.08f, "y");
        saturn.setOrbit(95, 0.03f, "y");
        uranus.setOrbit(191, 0.012f, "y");
        neptune.setOrbit(300, 0.006f, "y");
        pluto.setOrbit(394, 0.004f, "y");

        lines.add(new Line(gl, "x", new Vector3D(0,0,0), new Vector3D(8,0,0)));
        lines.add(new Line(gl, "y", new Vector3D(0,0,0), new Vector3D(0,8,0)));
        lines.add(new Line(gl, "z", new Vector3D(0,0,0), new Vector3D(0,0,8)));
    }

    public void dispose(GLAutoDrawable drawable) {}

    private void gravity(GL4 gl, Matrix3D pMat, Matrix3D vMat, double time) {
        MatrixStack mStack = new MatrixStack(10);

        stars.display(gl, rendering_program, pMat, vMat);

        sun.orbitTopOfStack(mStack, time);
        sun.display(gl, rendering_program, pMat, vMat);

        mercury.orbitTopOfStack(mStack, time);
        mercury.display(gl, rendering_program, pMat, vMat);
        mStack.popMatrix();

        venus.orbitTopOfStack(mStack, time);
        venus.display(gl, rendering_program, pMat, vMat);
        mStack.popMatrix();

        earth.orbitTopOfStack(mStack, time);
        earth.display(gl, rendering_program, pMat, vMat);

        moon.orbitTopOfStack(mStack, time);
        moon.display(gl, rendering_program, pMat, vMat);

        penta.orbitTopOfStack(mStack, time);
        penta.display(gl, rendering_program, pMat, vMat);

        mStack.popMatrix();
        mStack.popMatrix();
        mStack.popMatrix();

        mars.orbitTopOfStack(mStack, time);
        mars.display(gl, rendering_program, pMat, vMat);

        marsMoon.orbitTopOfStack(mStack, time);
        marsMoon.display(gl, rendering_program, pMat, vMat);
        mStack.popMatrix();
        mStack.popMatrix();

        jupiter.orbitTopOfStack(mStack, time);
        jupiter.display(gl, rendering_program, pMat, vMat);
        mStack.popMatrix();

        saturn.orbitTopOfStack(mStack, time);
        saturn.display(gl, rendering_program, pMat, vMat);
        mStack.popMatrix();

        uranus.orbitTopOfStack(mStack, time);
        uranus.display(gl, rendering_program, pMat, vMat);
        mStack.popMatrix();

        neptune.orbitTopOfStack(mStack, time);
        neptune.display(gl, rendering_program, pMat, vMat);
        mStack.popMatrix();

        pluto.orbitTopOfStack(mStack, time);
        pluto.display(gl, rendering_program, pMat, vMat);
    }

    public void display(GLAutoDrawable drawable) {
        GL4 gl = (GL4) GLContext.getCurrentGL();
        gl.glClear(GL_DEPTH_BUFFER_BIT);

        double time = (double)(System.currentTimeMillis())/2000.0;

        float bkg[] = { 0.0f, 0.0f, 0.0f, 1.0f };
        FloatBuffer bkgBuffer = Buffers.newDirectFloatBuffer(bkg);
        gl.glClearBufferfv(GL_COLOR, 0, bkgBuffer);

        float aspect = (float) myCanvas.getWidth() / (float) myCanvas.getHeight();
        Matrix3D pMat = perspective(60.0f, aspect, 0.1f, 1000.0f);

        camera.moveCamera(listen.keyList, time);

        Matrix3D vMat = new Matrix3D();
        vMat.concatenate(camera.getRotMatrix());
        vMat.translate(-camera.position.getX(), -camera.position.getY(), -camera.position.getZ());

        gl.glUseProgram(rendering_program2D);

        checkSpace();

        if(spaceToggle) {
            for (Line line : lines) {
                line.display(gl, rendering_program2D, pMat, vMat);
            }
        }

        gl.glUseProgram(rendering_program);
        gravity(gl, pMat, vMat, time);
    }

    private void checkSpace() {
        System.out.println();
        if(listen.keyList.contains(MyListener.Key.space)) {
            System.out.println("contains space");
            if(spaceWasReleased) {
                System.out.println("space was previously released");
                spaceToggle = !spaceToggle;
                spaceWasReleased = false;
            }
        } else {
            System.out.println("Space is currently released            ");
            spaceWasReleased = true;
        }
    }
    private boolean spaceToggle = false;
    private boolean spaceWasReleased = true;

    public void reshape(GLAutoDrawable drawable, int i, int i1, int i2, int i3) {}

    private Matrix3D perspective(float fovy, float aspect, float n, float f)
    {	float q = 1.0f / ((float) Math.tan(Math.toRadians(0.5f * fovy)));
        float A = q / aspect;
        float B = (n + f) / (n - f);
        float C = (2.0f * n * f) / (n - f);
        Matrix3D r = new Matrix3D();
        r.setElementAt(0,0,A);
        r.setElementAt(1,1,q);
        r.setElementAt(2,2,B);
        r.setElementAt(3,2,-1.0f);
        r.setElementAt(2,3,C);
        r.setElementAt(3,3,0.0f);
        return r;
    }
}
