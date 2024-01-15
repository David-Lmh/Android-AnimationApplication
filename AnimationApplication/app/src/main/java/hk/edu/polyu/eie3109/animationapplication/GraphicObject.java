package hk.edu.polyu.eie3109.animationapplication;

import android.graphics.Bitmap;
import android.view.MotionEvent;

import java.util.ArrayList;

public class GraphicObject {
    private final Bitmap bitmap;
    private final Coordinates coordinates;
    private final Movement movement;

    public GraphicObject(Bitmap bitmap) {
        this.bitmap = bitmap;
        this.coordinates = new Coordinates(bitmap);
        this.movement = new Movement();
    }

    public Bitmap getGraphic() {
        return bitmap;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public Movement getMovement() {
        return movement;
    }
}
