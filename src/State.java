public class State {
    public enum Position {
        press, down, release, up;
    }

    private Position position;
    public boolean nextIsDown;

    public State() {
        position = Position.up;
    }

    public Position getPosition() {
        return position;
    }

    public boolean isDown() {
        if(position == Position.press || position == Position.down) {
            return true;
        } else {
            return false;
        }
    }

    public void update() {
        if(nextIsDown) {
            if(!isDown()) {
                position = Position.press;
            } else if(position == Position.press) {
                position = Position.down;
            }
        } else {
            if(isDown()) {
                position = Position.release;
            } else if(position == Position.release) {
                position = Position.up;
            }
        }
    }
};