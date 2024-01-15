package hk.edu.polyu.eie3109.animationapplication;

import java.util.Random;

public class Movement {
    public final int X_DIRECTION_RIGHT = 1;
    public final int X_DIRECTION_LEFT = -1;
    public final int Y_DIRECTION_DOWN = 1;
    public final int Y_DIRECTION_UP = -1;

    Random random = new Random();
    private int xSpeed = random.nextInt(5) + 10;
    private int ySpeed = random.nextInt(5) + 10;

    private int xDirection = X_DIRECTION_RIGHT;
    private int yDirection = Y_DIRECTION_DOWN;

    public void setXYSpeed(int xSpeed, int ySpeed) {
        this.xSpeed = xSpeed;
        this.ySpeed = ySpeed;
    }

    public void setDirection(int xDirection, int yDirection) {
        this.xDirection = xDirection;
        this.yDirection = yDirection;
    }

    public void toggleXDirection() {
        if (xDirection == X_DIRECTION_RIGHT) {
            xDirection = X_DIRECTION_LEFT;
        } else {
            xDirection = X_DIRECTION_RIGHT;
        }
    }

    public void toggleYDirection() {
        if (yDirection == Y_DIRECTION_DOWN) {
            yDirection = Y_DIRECTION_UP;
        } else {
            yDirection = Y_DIRECTION_DOWN;
        }
    }

    public int getXSpeed() {
        return xSpeed;
    }

    public int getYSpeed() {
        return ySpeed;
    }

    public int getXDirection() {
        return xDirection;
    }

    public int getYDirection() {
        return yDirection;
    }
}
