import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class MyListener implements KeyListener {

    public Mouse mouse;
    public Keyboard keyboard;
    private JFrame window;

    public MyListener(JFrame frame) throws AWTException {
        this.window = frame;
        keyboard = new Keyboard();
        mouse = new Mouse();
    }

    public void update() {
        keyboard.update();
        mouse.update(window);
    }

    @Override
    public void keyTyped(KeyEvent e) {
        //System.out.println("Typed: " + e.getKeyChar());
    }

    @Override
    public void keyPressed(KeyEvent e) {
        //System.out.println("Pressed: " + e.getKeyCode());
        switch(e.getKeyCode()) {
            case 87:
                keyboard.w.nextIsDown = true;
                //System.out.println("w.isDown(): " + keyboard.w.isDown());
                break;
            case 65:
                keyboard.a.nextIsDown = true;
                break;
            case 83:
                keyboard.s.nextIsDown = true;
                break;
            case 68:
                keyboard.d.nextIsDown = true;
                break;
            case 81:
                keyboard.q.nextIsDown = true;
                break;
            case 69:
                keyboard.e.nextIsDown = true;
                break;
            case 37:
                keyboard.left.nextIsDown = true;
                break;
            case 38:
                keyboard.up.nextIsDown = true;
                break;
            case 39:
                keyboard.right.nextIsDown = true;
                break;
            case 40:
                keyboard.down.nextIsDown = true;
                break;
            case 16:
                keyboard.shift.nextIsDown = true;
                break;
            case 32:
                keyboard.space.nextIsDown = true;
                break;
            case 27:
                keyboard.esc.nextIsDown = true;
                break;
            case 44:
                keyboard.lArrow.nextIsDown = true;
                break;
            case 46:
                keyboard.rArrow.nextIsDown = true;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        //System.out.println("Released: " + e.getKeyChar());
        switch(e.getKeyCode()) {
            case 87:
                keyboard.w.nextIsDown = false;
                break;
            case 65:
                keyboard.a.nextIsDown = false;
                break;
            case 83:
                keyboard.s.nextIsDown = false;
                break;
            case 68:
                keyboard.d.nextIsDown = false;
                break;
            case 81:
                keyboard.q.nextIsDown = false;
                break;
            case 69:
                keyboard.e.nextIsDown = false;
                break;
            case 37:
                keyboard.left.nextIsDown = false;
                break;
            case 38:
                keyboard.up.nextIsDown = false;
                break;
            case 39:
                keyboard.right.nextIsDown = false;
                break;
            case 40:
                keyboard.down.nextIsDown = false;
                break;
            case 16:
                keyboard.shift.nextIsDown = false;
                break;
            case 32:
                keyboard.space.nextIsDown = false;
                break;
            case 27:
                keyboard.esc.nextIsDown = false;
                break;
            case 44:
                keyboard.lArrow.nextIsDown = false;
                break;
            case 46:
                keyboard.rArrow.nextIsDown = false;
        }
    }
}
