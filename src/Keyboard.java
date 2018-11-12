import java.lang.reflect.Field;

public class Keyboard {
    public State w;
    public State a;
    public State s;
    public State d;
    public State q;
    public State e;
    public State down;
    public State left;
    public State right;
    public State up;
    public State shift;
    public State space;
    public State esc;
    public State lArrow;
    public State rArrow;

    public Keyboard() {
        w = new State();
        a = new State();
        s = new State();
        d = new State();
        q = new State();
        e = new State();
        down = new State();
        left = new State();
        right = new State();
        up = new State();
        shift = new State();
        space = new State();
        esc = new State();
        lArrow = new State();
        rArrow = new State();
    }

    public void update() {
        w.update();
        a.update();
        s.update();
        d.update();
        q.update();
        e.update();
        down.update();
        left.update();
        right.update();
        up.update();
        shift.update();
        space.update();
        esc.update();
        lArrow.update();
        rArrow.update();
    }
}