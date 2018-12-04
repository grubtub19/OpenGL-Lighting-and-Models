import com.jogamp.opengl.util.FPSAnimator;
import graphicslib3D.*;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Scanner;
import javax.swing.*;

import static com.jogamp.opengl.GL4.*;
import static graphicslib3D.light.AmbientLight.getAmbientLight;

import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import graphicslib3D.light.*;

public class Solar extends JFrame implements GLEventListener {
    public static int rendering_programPhong;
    public static int rendering_programBlinn;
    public static int rendering_programBlinnMap;
    public static int rendering_programNormal;
    public static int rendering_programLine;
    public static int rendering_programShadow;

    private GLCanvas myCanvas;
    private GLSLUtils util = new GLSLUtils();
    private AmbientLight globalAmbient = getAmbientLight();
    private int vao[] = new int[1];
    private Camera camera;
    private PosLight posLight;
    private Square bulb;
    private Shape light;
    private ArrayList<Shape> shapes = new ArrayList<>();
    private Shape samurai;
    private Shape maid;
    private Shape peter;
    private Shape planet;
    private Shape eva;
    private Shape patty;
    private Shape ellie;
    private Shape zen;
    private Shape max;
    private Shape splatoon;
    private Shape sonic;
    private Shape lamp;
    private Shape plaza;
    private ArrayList<Line> lines = new ArrayList<>();
    private Skybox skyBox;
    MyListener listen;
    private long prevTime;
    private long currTime;
    private float timeElapsed;
    private boolean spaceToggle = false;
    private boolean fullscreen = false;

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
        FPSAnimator animator = new FPSAnimator(myCanvas, 60);
        animator.start();
    }

    public static void main(String[] args) {
        Scanner reader = new Scanner(System.in);
        System.out.println("READY?");
        reader.next();
        reader.close();
        new Solar();
    }

    public void init(GLAutoDrawable drawable) {
        GL4 gl = (GL4) GLContext.getCurrentGL();
        gl.glEnable(GL_TEXTURE_CUBE_MAP_SEAMLESS);
        gl.glEnable(GL_BLEND);
        gl.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        rendering_programBlinn = createShaderProgram("Blinn");
        rendering_programBlinnMap = createShaderProgram("BlinnMap");
        rendering_programPhong = createShaderProgram("Phong");
        rendering_programLine = createShaderProgram("Line");
        rendering_programNormal = createShaderProgram("Sky");
        rendering_programShadow = createShaderProgram("Shadow");
        gl.glGenVertexArrays(vao.length, vao, 0);
        gl.glBindVertexArray(vao[0]);
        camera = new Camera(0,4.0f,9.0f);
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
            String gshaderSource[];
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

        posLight = new PosLight(gl,2048);
        posLight.light.setAmbient(new float[] { 0.2f, 0.2f, 0.22f, 1.0f });
        posLight.light.setDiffuse(new float[] { 1.0f, 1.0f, 0.9f, 1.0f });
        posLight.light.setSpecular(new float[] { 1.0f, 1.0f, 1.0f, 1.0f });
        posLight.light.setConstantAtt(0.0001f);
        posLight.light.setLinearAtt(0.0f);
        posLight.light.setQuadraticAtt(0.01f);
        posLight.light.setPosition(new Point3D(1.8, 0.5, 1.8));

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
        skyBox = new Skybox(gl, camera, "Models/stars8k.jpg");
        //bulb = new Square(gl, .5f, "stars8k.jpg");
        lamp = new Model(gl, "Models/Light Desk/light_desk.obj", "Models/Light Desk/light_desk.mtl", true, true);
        lamp.scale(0.01f, 0.01f, 0.01f);
        lamp.setPosition(posLight.light.getPosition());
        //light = new Model(gl, "Models/Light/Super Sonic.obj", "Models/Light/Super Sonic.mtl", true, true);
        //shapes.add(new Square(gl, 10, "stars8k.jpg"));
        //shapes.add(new Model(gl,"Models/shuttle/shuttle.obj","Models/shuttle/shuttle.jpg", false));
        //shapes.get(1).scale(10f,10f,10f);


        shapes.add(maid = new Model(gl, "Models/Tsurara/Tsurara.obj", "Models/Tsurara/Tsurara.mtl", true, true));
        maid.move(new Vector3D(2.4,-0.8,-0.6));
        maid.scale(0.1f,0.1f,0.1f);

        shapes.add(peter = new Model(gl, "Models/Peter/peter.obj", "Models/Peter/peter.mtl", true, true));
        peter.scale(1,1,1);
        peter.move(new Vector3D(-3.5, -0.8, 1.6));
        peter.setRotY(90);

        shapes.add(samurai = new Model(gl, "Models/Woman/woman.obj", "Models/Woman/woman.mtl", true, true));
        samurai.move(new Vector3D(-1.2,-0.8,1.6));
        samurai.scale(0.08f, 0.08f, 0.08f);
        //samurai.setRotY(170);

        shapes.add(planet = new Sphere(gl, 4, "Models/2k_jupiter.jpg"));
        planet.move(new Vector3D(0,6.0,0));
        planet.scale(0.1f,0.1f,0.1f);

        shapes.add(eva = new Model(gl, "Models/Eva Unit 03/Unit 03.obj", "Models/Eva Unit 03/Unit 03.mtl", true, true));
        eva.move(new Vector3D(0,-0.8,0));
        eva.scale(0.05f,0.05f,0.05f);

        shapes.add(patty = new Model(gl, "Models/Krabby Patty/krabbypattie01.obj", "Models/Krabby Patty/krabbypattie01.mtl", true, true));
        patty.move(new Vector3D(-0.7,-0.62,-1.7));
        patty.scale(0.1f,0.1f,0.1f);
        patty.setRotZ(-70);

        shapes.add(ellie = new Model(gl, "Models/Ellie/posed2.obj", "Models/Ellie/posed2.mtl", true, true));
        ellie.move(new Vector3D(-1.35, -0.8, -2.25));
        ellie.scale(1,1,1);

        shapes.add(zen = new Model(gl, "Models/Zenyatta/Zenyatta.obj", "Models/Zenyatta/Zenyatta.mtl", true, true));
        zen.scale(1,1,1);
        //zen's position is set below in update()
        zen.setRotY(45);

        shapes.add(max = new Model(gl, "Models/Max/Max.obj", "Models/Max/Max.mtl", true, false));
        max.move(new Vector3D(1.2, -0.8, 2.6));
        max.scale(0.15f, 0.15f, 0.15f);
        max.setRotY(245);

        shapes.add(sonic = new Model(gl, "Models/Sonic/sonicgenesis.obj", "Models/Sonic/sonicgenesis.mtl", true, true));
        sonic.move(new Vector3D(3.4, -0.8, -3.4));
        sonic.scale(0.1f,0.1f,0.1f);
        //sonic.setRotY(180);

        shapes.add(splatoon = new Model(gl, "Models/Splatoon/total.obj", "Models/Splatoon/total.mtl", true, false));
        splatoon.scale(0.01f, 0.01f, 0.01f);
        splatoon.move(new Vector3D(0, -0.85, 0));

        lines.add(new Line(gl, "x", new Vector3D(0,0,0), new Vector3D(1,0,0)));
        lines.add(new Line(gl, "y", new Vector3D(0,0,0), new Vector3D(0,1,0)));
        lines.add(new Line(gl, "z", new Vector3D(0,0,0), new Vector3D(0,0,1)));
    }

    public void dispose(GLAutoDrawable drawable) {}

    private float zenCount = 0.01f;
    private boolean zenUp = true;
    private boolean firstFrame = true;

    /**
     * Updates objects in scene
     */
    public void update() {
        updateTime();
        listen.update();
        doKeyEvents();
        camera.moveCamera(listen, timeElapsed);
        updateModels();
    }

    public void updateTime() {
        if(firstFrame) {
            prevTime = System.currentTimeMillis();
            firstFrame = false;
        } else {
            prevTime = currTime;
        }
        currTime = System.currentTimeMillis();
        timeElapsed = currTime - prevTime;
    }

    public void updateModels() {
        double curve = Math.pow(1 - zenCount, 3) * 0 + 3 * Math.pow(1 - zenCount, 2) * zenCount * 0.005
                + 3 * (1 - zenCount) * Math.pow(zenCount, 2) * 0.095 + Math.pow(zenCount, 3) * 0.1;
        //System.out.println("zenCount: " + zenCount);
        if(zenUp) {
            if(zenCount >= 1) {
                zenUp = false;
            }
            zenCount += timeElapsed / 1000f;
        } else {
            if(zenCount <= 0) {
                zenUp = true;
            }
            zenCount -= timeElapsed / 1000f;
        }

        zen.setPosition(new Point3D(-3.6,-0.8 + curve,-3.6));
        //System.out.println("zen.position: " + zen.position.toString());
        maid.setRotY(maid.rotY + 16 * timeElapsed * .05f);

        //peter.setRotZ(peter.rotZ + 1);
        //peter.setRotY(peter.rotY + 4);

        //samurai.setRotX(samurai.rotX + 2);
        samurai.setRotY(samurai.rotY + 2 * timeElapsed * .05f);

        planet.setRotY(planet.rotY + 2 * timeElapsed * .05f);
        //patty.setRotZ(patty.rotZ + 1);

        //ellie.setRotX(ellie.rotX + 4);
        //ellie.setRotY(ellie.rotY + 2);

        //eva.setRotZ(eva.rotZ + 1);
        eva.setRotY(eva.rotY + 2 * timeElapsed * .05f);
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
        passOne(gl);
        passTwo(gl, pMat, vMat);
    }

    private void passOne(GL4 gl) {
        gl.glUseProgram(rendering_programShadow);
        posLight.genShadowMap(gl, rendering_programShadow, shapes);
        gl.glBindFramebuffer(GL_FRAMEBUFFER, 0);
        gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    /**
     * The render pass
     * @param gl GL4
     * @param pMat projection matrix
     * @param vMat view matrix
     */
    private void passTwo(GL4 gl, Matrix3D pMat, Matrix3D vMat) {
        gl.glViewport(0,0, getWidth(), getHeight());

        gl.glUseProgram(rendering_programNormal);
        skyBox.display(gl, rendering_programNormal, pMat, vMat);

        if(spaceToggle) {
            gl.glUseProgram(rendering_programLine);
            for (Line line : lines) {
                line.display(gl, rendering_programLine, pMat, vMat);
            }
        }
        gl.glUseProgram(rendering_programNormal);
        //bulb.display(gl, rendering_programNormal, pMat, vMat);
        lamp.display(gl, rendering_programNormal, pMat, vMat);

        gl.glUseProgram(rendering_programBlinnMap);
        for(Shape shape: shapes) {
            shape.displayShadow(gl, rendering_programBlinnMap, pMat, vMat, globalAmbient, posLight, true);
        }
    }

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
                    posLight.light.getPosition().getZ() -  0.04f));
            lamp.setPosition(posLight.light.getPosition());
        }
        if(listen.keyboard.down.isDown()) {
            //System.out.println("moving +z");
            posLight.light.setPosition(new Point3D(posLight.light.getPosition().getX(), posLight.light.getPosition().getY(),
                    posLight.light.getPosition().getZ() + 0.04f));
            lamp.setPosition(posLight.light.getPosition());
        }
        if(listen.keyboard.left.isDown()) {
            //System.out.println("moving -x");
            posLight.light.setPosition(new Point3D(posLight.light.getPosition().getX() - 0.04f, posLight.light.getPosition().getY(),
                    posLight.light.getPosition().getZ()));
            lamp.setPosition(posLight.light.getPosition());
        }
        if(listen.keyboard.right.isDown()) {
            //System.out.println("moving +x");
            posLight.light.setPosition(new Point3D(posLight.light.getPosition().getX() + 0.04f, posLight.light.getPosition().getY(),
                    posLight.light.getPosition().getZ()));
            lamp.setPosition(posLight.light.getPosition());
        }
        if(listen.keyboard.rArrow.isDown()) {
            //System.out.println("moving -y");
            posLight.light.setPosition(new Point3D(posLight.light.getPosition().getX(), posLight.light.getPosition().getY() - 0.04f,
                    posLight.light.getPosition().getZ()));
            lamp.setPosition(posLight.light.getPosition());
        }
        if(listen.keyboard.lArrow.isDown()) {
            //System.out.println("moving +y");
            posLight.light.setPosition(new Point3D(posLight.light.getPosition().getX() , posLight.light.getPosition().getY() + 0.04f,
                    posLight.light.getPosition().getZ()));
            lamp.setPosition(posLight.light.getPosition());
        }
        if(listen.keyboard.f11.getPosition().equals(State.Position.press)) {
            if(fullscreen) {
                setExtendedState(JFrame.NORMAL);
                setVisible(true);
                fullscreen = false;
            } else {
                System.out.println("Fullscreen");
                //miniWidth = getWidth();
                //miniHeight = getHeight();
                setExtendedState(JFrame.MAXIMIZED_BOTH);
                //setUndecorated(true);
                setVisible(true);
                fullscreen = true;
            }
        }
    }

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
