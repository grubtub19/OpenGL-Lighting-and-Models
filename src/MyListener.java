import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

public class MyListener implements KeyListener {

    public enum Key { w, a, s, d, q, e, down, left, up, right, shift, space };
    public ArrayList<Key> keyList = new ArrayList<Key>();
    private Camera camera;

    public MyListener() {

    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    private void checkThenAdd(Key key) {
        if (!keyList.contains(key)) {
            keyList.add(key);
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        //System.out.println(e.getKeyCode());
        switch(e.getKeyCode()) {
            case 87:
                checkThenAdd(Key.w);
                break;
            case 65:
                checkThenAdd(Key.a);
                break;
            case 83:
                checkThenAdd(Key.s);
                break;
            case 68:
                checkThenAdd(Key.d);
                break;
            case 81:
                checkThenAdd(Key.q);
                break;
            case 69:
                checkThenAdd(Key.e);
                break;
            case 37:
                checkThenAdd(Key.left);
                break;
            case 38:
                checkThenAdd(Key.up);
                break;
            case 39:
                checkThenAdd(Key.right);
                break;
            case 40:
                checkThenAdd(Key.down);
                break;
            case 16:
                checkThenAdd(Key.shift);
                break;
            case 32:
                checkThenAdd(Key.space);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        switch(e.getKeyCode()) {
            case 87:
                keyList.remove(Key.w);
                break;
            case 65:
                keyList.remove(Key.a);
                break;
            case 83:
                keyList.remove(Key.s);
                break;
            case 68:
                keyList.remove(Key.d);
                break;
            case 81:
                keyList.remove(Key.q);
                break;
            case 69:
                keyList.remove(Key.e);
                break;
            case 37:
                keyList.remove(Key.left);
                break;
            case 38:
                keyList.remove(Key.up);
                break;
            case 39:
                keyList.remove(Key.right);
                break;
            case 40:
                keyList.remove(Key.down);
                break;
            case 16:
                keyList.remove(Key.shift);
                break;
            case 32:
                keyList.remove(Key.space);
        }
    }
}
