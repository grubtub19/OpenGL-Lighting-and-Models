import javax.swing.*;
import java.awt.*;

public class Mouse {
    public float x;
    public float y;
    //public float xPast;
    //public float yPast;
    public boolean firstFrame = true;
    Robot robot;

    public Mouse() throws AWTException {
        robot = new Robot();
    }

    public void update(JFrame window) {
        Point point = window.getLocation();
        Point root = window.getRootPane().getLocation();
        Point center = new Point(point.x + root.x + window.getWidth() / 2, point.y + root.y + window.getHeight() / 2);
        if (MouseInfo.getPointerInfo() != null) {
            x = (float) MouseInfo.getPointerInfo().getLocation().getX() - center.x;
            y = (float) MouseInfo.getPointerInfo().getLocation().getY() - center.y;
        }
       // x = (float) MouseInfo.getPointerInfo().getLocation().getX();
       // window.getLocation();
        //y = (float) MouseInfo.getPointerInfo().getLocation().getY();
        if(!window.isFocused()) {
            x = 0;
            y = 0;
            firstFrame = true;
        } else if(firstFrame) {
            x = 0;
            y = 0;
            firstFrame = false;
        } else {
            robot.mouseMove(point.x + root.x + window.getWidth() / 2 , point.y + root.y + window.getHeight() / 2);
        }
    }
}