package com.pixelPower.game;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.util.glu.GLU.*;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

public class Main {
    public static final float DISPLAY_HEIGHT = 600.0f;
    public static final float DISPLAY_WIDTH = 800.0f;
    public static final Logger LOGGER = Logger.getLogger(Main.class.getName());
    public static final float MOUSE_SENS = 0.09f;
    public static final boolean INVERT_Y_AXIS = false;
    
    public Player mainChar;
    public World world;
    public Helper helper;

    static {
        try {
            LOGGER.addHandler(new FileHandler("errors.log", true));
        } catch (IOException ex) {
            LOGGER.log(Level.WARNING, ex.toString(), ex);
        }
    }

    public static void main(String[] args) {
        Main main = null;

        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new DebugWindow().setVisible(false);
            }
        });

        try {
            main = new Main();

            main.create();
            main.run();
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, ex.toString(), ex);
        } finally {
            if (main != null) {
                main.destroy();
            }
        }

        System.exit(0);
    }

    public Main() {
    }

    public void create() throws LWJGLException {
        // Display
        Display.setDisplayMode(new DisplayMode((int) DISPLAY_WIDTH, (int) DISPLAY_HEIGHT));
        Display.setFullscreen(false);
        Display.setTitle("BlockWorld");
        Display.create();

        // Keyboard
        Keyboard.create();

        // Mouse
        Mouse.setGrabbed(true);
        Mouse.create();

        // OpenGL
        initGL();
        resizeGL();
    }

    public void destroy() {
        Mouse.destroy();
        Keyboard.destroy();
        Display.destroy();
    }

    public void initGL() {
        glClearColor(0.5f, 0.75f, 1.0f, 1.0f);
        glLineWidth(2.0f);

        glEnable(GL_CULL_FACE);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_TEXTURE_2D);
        glEnable(GL_FOG);

        glDisable(GL_LIGHTING);

        glDisable(GL_COLOR_MATERIAL);


        glFogi(GL_FOG_MODE, GL_LINEAR);
        glFogf(GL_FOG_DENSITY, 1.0f);
        glHint(GL_FOG_HINT, GL_DONT_CARE);
        glFogf(GL_FOG_START, 300.0f);
        glFogf(GL_FOG_END, 400.0f);

        try {
            Class.forName("com.pixelPower.game.Chunk");
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }

        world = new World(mainChar);
        mainChar = new Player();
        helper = new Helper();

    }

    public void processKeyboard() {
        if (Keyboard.isKeyDown(Keyboard.KEY_W)) {
            mainChar.walkForward();
        }
        
        if (Keyboard.isKeyDown(Keyboard.KEY_S)) {
            mainChar.walkBackwards();
        }
        
        if (Keyboard.isKeyDown(Keyboard.KEY_A)) {
            mainChar.strafeLeft();
        }
        
        if (Keyboard.isKeyDown(Keyboard.KEY_D)) {
            mainChar.strafeRight();
        }
        
        if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
            mainChar.position.y--;
        }
        
        if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
            mainChar.position.y++;
        }
    }

    public void processMouse() {
    }

    public void render() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glLoadIdentity();

        glPushMatrix();
        mainChar.render();
        world.render();

        glBegin(GL_LINES);
        glColor3f(255.0f, 0.0f, 0.0f);
        glVertex3f(0.0f, 0.0f, 0.0f);
        glVertex3f(1000.0f, 0.0f, 0.0f);
        glEnd();

        glBegin(GL_LINES);
        glColor3f(0.0f, 255.0f, 0.0f);
        glVertex3f(0.0f, 0.0f, 0.0f);
        glVertex3f(0.0f, 1000.0f, 0.0f);
        glEnd();

        glBegin(GL_LINES);
        glColor3f(0.0f, 0.0f, 255.0f);
        glVertex3f(0.0f, 0.0f, 0.0f);
        glVertex3f(0.0f, 0.0f, 1000.0f);
        glEnd();

        glPopMatrix();
    }

    public void resizeGL() {
        glViewport(0, 0, (int) DISPLAY_WIDTH, (int) DISPLAY_HEIGHT);

        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        gluPerspective(64.0f, DISPLAY_WIDTH / DISPLAY_HEIGHT, 1f, 1024f);
        glPushMatrix();

        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();
        glPushMatrix();
    }

    public void run() {

        while (!Display.isCloseRequested() && !Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {

            if (Display.isVisible()) {

                processKeyboard();
                processMouse();
                update();
                render();

            } else {

                if (Display.isDirty()) {

                    render();

                }

                try {

                    Thread.sleep(100);

                } catch (InterruptedException ex) {
                }
            }

            Display.update();
            //Display.sync(60);
        }
    }

    public void update() {
        mainChar.yaw(Mouse.getDX() * MOUSE_SENS);

        if (!INVERT_Y_AXIS) {
            mainChar.pitch(-1 * (Mouse.getDY() * MOUSE_SENS));
        } else {
            mainChar.pitch(Mouse.getDY() * MOUSE_SENS);
        }

        helper.frameRendered();
    }
}
