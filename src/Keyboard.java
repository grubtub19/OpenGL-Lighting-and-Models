import java.lang.reflect.Field;

public class Keyboard {
    public State w = new State();
    public State a = new State();
    public State s = new State();
    public State d = new State();
    public State q = new State();
    public State e = new State();
    public State down = new State();
    public State left = new State();
    public State right = new State();
    public State up = new State();
    public State shift = new State();
    public State space = new State();
    public State esc = new State();
    public State lArrow = new State();
    public State rArrow = new State();
    public State ctrl = new State();
    public State f11 = new State();

    public Keyboard() {}

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
        ctrl.update();
        f11.update();
    }
}