import com.jogamp.opengl.util.FPSAnimator;
import graphicslib3D.*;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.nio.*;
import java.util.ArrayList;
import javax.swing.*;
import static com.jogamp.opengl.GL4.*;
import static graphicslib3D.light.AmbientLight.getAmbientLight;

import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.common.nio.Buffers;
import graphicslib3D.light.*;

public class Solar extends JFrame implements GLEventListener {
    public static int rendering_programPhong;
    public static int rendering_programBlinn;
    public static int rendering_programBlinnMap;
    public static int rendering_programNormal;
    public static int rendering_programLine;
    public static int rendering_programNoRender;

    private GLCanvas myCanvas;
    private GLSLUtils util = new GLSLUtils();
    private AmbientLight globalAmbient = getAmbientLight();
    private int vao[] = new int[1];
    private Camera camera = new Camera(0,0,0);
    private PosLight posLight;
    private Square bulb;
    private ArrayList<Shape> shapes = new ArrayList<>();
    private ArrayList<Line> lines = new ArrayList<>();
    private Skybox skyBox;
    MyListener listen;
    private long prevTime;
    private long currTime;
    private float timeElapsed;

    public Solar() {
        setTitle("Solar System");
        setSize(1024, 1024);
        IconHelper.setIconImageFromUrl(this,"http://www.origami-make.org/origami-paper-triangle/Images/origami-paper-triangle.jpg");
        GLProfile profile = GLProfile.get(GLProfile.GL4);
        GLCapabilities capabilities = new GLCapabilities(profile);
        myCanvas = new GLCanvas(capabilities);
        myCanvas.addGLEventListener(this);
        try {
            listen = new MyListener(this);
        } catch (AWTException e) {
            System.err.println("Robot constructor failure\n" + e);
        }
        myCanvas.addKeyListener(listen);
        getContentPane().add(myCanvas);
        disableCursor();
        requestFocus();
        this.setVisible(true);
        currTime = System.currentTimeMillis();
        FPSAnimator animator = new FPSAnimator(myCanvas, 30);
        animator.start();
    }

    public static void main(String[] args) {
        new Solar();
    }

    public void init(GLAutoDrawable drawable) {
        GL4 gl = (GL4) GLContext.getCurrentGL();
        gl.glEnable(GL_TEXTURE_CUBE_MAP_SEAMLESS);
        rendering_programBlinn = createShaderProgram("Blinn");
        rendering_programBlinnMap = createShaderProgram("BlinnMap");
        rendering_programPhong = createShaderProgram("Phong");
        rendering_programLine = createShaderProgram("Line");
        rendering_programNormal = createShaderProgram("Sky");
        rendering_programNoRender = createShaderProgram("Shadow");
        gl.glGenVertexArrays(vao.length, vao, 0);
        gl.glBindVertexArray(vao[0]);
        initLights(gl);
        initShapes(gl);
    }

    private void disableCursor() {
        BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(
                cursorImg, new Point(0, 0), "blank cursor");
        getContentPane().setCursor(blankCursor);
    }

    private int createShaderProgram(String d)
    {	GL4 gl = (GL4) GLContext.getCurrentGL();

        String vshaderSource[];
        String fshaderSource[];
        String gshaderSource[];
        vshaderSource = util.readShaderSource("shaders/vert" + d + ".shader");
        fshaderSource = util.readShaderSource("shaders/frag" + d + ".shader");

        int vShader = gl.glCreateShader(GL_VERTEX_SHADER);
        int fShader = gl.glCreateShader(GL_FRAGMENT_SHADER);

        gl.glShaderSource(vShader, vshaderSource.length, vshaderSource, null, 0);
        gl.glShaderSource(fShader, fshaderSource.length, fshaderSource, null, 0);

        gl.glCompileShader(vShader);
        gl.glCompileShader(fShader);

        int vfprogram = gl.glCreateProgram();
        gl.glAttachShader(vfprogram, vShader);
        gl.glAttachShader(vfprogram, fShader);

        if(d == "Shadow") {
            gshaderSource = util.readShaderSource("shaders/geom" + d + ".shader");
            int gShader = gl.glCreateShader(GL_GEOMETRY_SHADER);
            gl.glShaderSource(gShader, gshaderSource.length, gshaderSource, null, 0);
            gl.glCompileShader(gShader);
            gl.glAttachShader(vfprogram, gShader);
        }
        gl.glLinkProgram(vfprogram);
        return vfprogram;
    }

    private void initLights(GL4 gl) {
        float[] red = new float[] { 1.0f, 0.0f, 0.0f, 1.0f };
        float[] white = new float[] { 1.0f, 1.0f, 1.0f, 1.0f };
        globalAmbient.setValues( new float[] { 0.0f, 0.0f, 0.0f, 0.0f } );
       /* DistantLight d = new DistantLight();
        d.setAmbient(red);
        d.setDiffuse(red);
        d.setSpecular(white);
        d.setDirection(new Vector3D(0,0,-1));
        */

        posLight = new PosLight(gl,1024);
        posLight.light.setAmbient(new float[] { 0.1f, 0.1f, 0.1f, 1.0f });
        posLight.light.setDiffuse(new float[] { 1.0f, 1.0f, 1.0f, 1.0f });
        posLight.light.setSpecular(new float[] { 1.0f, 1.0f, 1.0f, 1.0f });
        posLight.light.setConstantAtt(1.0f);
        posLight.light.setLinearAtt(0.1f);
        posLight.light.setQuadraticAtt(0.1f);
        posLight.light.setPosition(new Point3D(0, 0, 0));

        /*
        SpotLight s = new SpotLight();
        s.setAmbient(red);
        s.setDiffuse(red);
        s.setSpecular(white);
        s.setPosition(new Point3D(5,2,-3));
        s.setDirection(new Vector3D(0,0,-1));
        s.setCutoffAngle(45.0f);
        s.setFalloffExponent(2.0f);
        */
    }

    public void initShapes(GL4 gl) {
        skyBox = new Skybox(gl, camera, "Models/alienSkybox.jpg");
        bulb = new Square(gl, .5f, "stars8k.jpg");
        //shapes.add(new Square(gl, 10, "stars8k.jpg"));
        //shapes.add(new Object(gl,"Models/shuttle/shuttle.obj","Models/shuttle/shuttle.jpg", false));
        //shapes.get(1).scale(10f,10f,10f);

        //shapes.add(new Object(gl, "Models/Miku/miku.obj", "Models/Miku/miku.mtl", true));
        //shapes.get(2).move(new Vector3D(6,0,6));
        //shapes.get(2).scale(.1f,.1f,.1f);
        //shapes.get(2).setRotX(30);
        shapes.add(new Object(gl, "Models/Tsurara/Tsurara.obj", "Models/Tsurara/Tsurara.mtl", true));
        shapes.get(0).move(new Vector3D(4,-8,0));

        shapes.add(new Object(gl, "Models/Peter/peter.obj", "Models/Peter/peter.mtl", true));
        shapes.get(1).scale(10,10,10);
        shapes.get(1).move(new Vector3D(-6, -2, 0));
        shapes.add(new Object(gl, "Models/Woman/woman.obj", "Models/Woman/woman.mtl", true));
        shapes.get(2).move(new Vector3D(0,-14,6));

        shapes.add(new Sphere(gl, 4, "Models/2k_jupiter.jpg"));
        shapes.get(3).move(new Vector3D(10,10,10));

        lines.add(new Line(gl, "x", new Vector3D(0,0,0), new Vector3D(8,0,0)));
        lines.add(new Line(gl, "y", new Vector3D(0,0,0), new Vector3D(0,8,0)));
        lines.add(new Line(gl, "z", new Vector3D(0,0,0), new Vector3D(0,0,8)));
    }

    public void dispose(GLAutoDrawable drawable) {}

    private void doKeyEvents() {
        if(listen.keyboard.esc.getPosition() == State.Position.press) {
            System.out.println("exit now");
            System.exit(0);
        }
        if(listen.keyboard.space.getPosition() == State.Position.press) {
            spaceToggle = !spaceToggle;
        }
        if(listen.keyboard.up.isDown()) {
            //System.out.println("moving -z");
            posLight.light.setPosition(new Point3D(posLight.light.getPosition().getX(), posLight.light.getPosition().getY(),
                    posLight.light.getPosition().getZ() -  0.4f));
            bulb.setPosition(posLight.light.getPosition());
        }
        if(listen.keyboard.down.isDown()) {
            //System.out.println("moving +z");
            posLight.light.setPosition(new Point3D(posLight.light.getPosition().getX(), posLight.light.getPosition().getY(),
                    posLight.light.getPosition().getZ() + 0.4f));
            bulb.setPosition(posLight.light.getPosition());
        }
        if(listen.keyboard.left.isDown()) {
            //System.out.println("moving -x");
            posLight.light.setPosition(new Point3D(posLight.light.getPosition().getX() - 0.4f, posLight.light.getPosition().getY(),
                    posLight.light.getPosition().getZ()));
            bulb.setPosition(posLight.light.getPosition());
        }
        if(listen.keyboard.right.isDown()) {
            //System.out.println("moving +x");
            posLight.light.setPosition(new Point3D(posLight.light.getPosition().getX() + 0.4f, posLight.light.getPosition().getY(),
                    posLight.light.getPosition().getZ()));
            bulb.setPosition(posLight.light.getPosition());
        }
        if(listen.keyboard.rArrow.isDown()) {
            //System.out.println("moving -y");
            posLight.light.setPosition(new Point3D(posLight.light.getPosition().getX(), posLight.light.getPosition().getY() - 0.4f,
                    posLight.light.getPosition().getZ()));
            bulb.setPosition(posLight.light.getPosition());
        }
        if(listen.keyboard.lArrow.isDown()) {
            //System.out.println("moving +y");
            posLight.light.setPosition(new Point3D(posLight.light.getPosition().getX() , posLight.light.getPosition().getY() + 0.4f,
                    posLight.light.getPosition().getZ()));
            bulb.setPosition(posLight.light.getPosition());
        }
        //System.out.println("posLight.position: " + posLight.light.getPosition().toString());
        //System.out.println("bulb.position: " + bulb.position.toString());
    }

    public void update() {
        prevTime = currTime;
        currTime = System.currentTimeMillis();
        timeElapsed = currTime - prevTime;
        listen.update();
        doKeyEvents();
        camera.moveCamera(listen, timeElapsed);
    }

    public void display(GLAutoDrawable drawable) {
        update();

        GL4 gl = (GL4) GLContext.getCurrentGL();
        //reset color buffer to all black
        float black[] = { 0.0f, 0.0f, 0.0f, 1.0f };
        gl.glClearBufferfv(GL_COLOR, 0, black, 0);
        gl.glClear(GL_DEPTH_BUFFER_BIT);
        //calc perspective matrix
        float aspect = (float) myCanvas.getWidth() / (float) myCanvas.getHeight();
        Matrix3D pMat = perspective(60.0f, aspect, 0.1f, 1000.0f);
        //calc view matrix
        Matrix3D vMat = new Matrix3D();
        vMat.concatenate(camera.getRotMatrix());

        int depthCubemap = passOne(gl);
        passTwo(gl, pMat, vMat, depthCubemap);
    }

    private int passOne(GL4 gl) {
        //for lights
        gl.glCullFace(GL_FRONT);
        return posLight.genShadowMap(gl, rendering_programNoRender, shapes);


        //gl.glViewport(0,0, shadowWidth, shadowHeight);
    }

    private void passTwo(GL4 gl, Matrix3D pMat, Matrix3D vMat, int depthCubemap) {
        gl.glViewport(0,0, getWidth(), getHeight());
        gl.glClear(GL_COLOR_BUFFER_BIT);
        gl.glClear(GL_DEPTH_BUFFER_BIT);
        gl.glUseProgram(rendering_programNormal);
        skyBox.display(gl, rendering_programNormal, pMat, vMat);

        if(spaceToggle) {
            gl.glUseProgram(rendering_programLine);
            for (Line line : lines) {
                line.display(gl, rendering_programLine, pMat, vMat);
            }
        }
        gl.glUseProgram(rendering_programBlinn);
        bulb.display(gl, rendering_programBlinn, pMat, vMat, globalAmbient, posLight.light, depthCubemap, true);

        gl.glUseProgram(rendering_programBlinnMap);
        for(Shape shape: shapes) {
            shape.display(gl, rendering_programBlinnMap, pMat, vMat, globalAmbient, posLight.light, depthCubemap, true);
        }
    }

    private boolean spaceToggle = false;

    public void reshape(GLAutoDrawable drawable, int i, int i1, int i2, int i3) {}

    public static Matrix3D perspective(float fovy, float aspect, float n, float f)
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
