package hk.edu.polyu.eie3109.animationapplication;

import android.graphics.Bitmap;

public class Rectangle {
    private final Bitmap bitmap;
    private final Coordinates coordinates;

    public Rectangle(Bitmap bitmap) {
        this.bitmap = bitmap;
        this.coordinates = new Coordinates(bitmap);
    }

    public Bitmap getGraphic() {
        return bitmap;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }
}
