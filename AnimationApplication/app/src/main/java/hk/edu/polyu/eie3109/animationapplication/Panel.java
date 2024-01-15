package hk.edu.polyu.eie3109.animationapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class Panel extends SurfaceView implements SurfaceHolder.Callback {

    private final Bitmap bmp;
    private final Bitmap rec;
    private final GameThread thread;
    private final Rectangle rectangle;

    private final int barHeight = 20;
    private int bmpOriginY = 20;
    private int lastTouchRecX = getResources().getDisplayMetrics().widthPixels / 2;
    private final long startTime = System.currentTimeMillis();
    private final ArrayList<GraphicObject> graphics = new ArrayList<GraphicObject>();

    // Reference: http://hk.javashuo.com/article/p-byipoyat-hn.html
    private static Bitmap getBitmap(Context context, int vectorDrawableId) {
        Bitmap bitmap = null;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            Drawable vectorDrawable = context.getDrawable(vectorDrawableId);
            bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                    vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            vectorDrawable.draw(canvas);
        } else {
            bitmap = BitmapFactory.decodeResource(context.getResources(), vectorDrawableId);
        }
        return bitmap;
    }

    public Panel(Context context) {
        super(context);
        getHolder().addCallback(this);
        bmp = getBitmap(context, R.drawable.ic_github);

        // Create the rectangle for game, coordinates x is in the middle of the screen
        rec = getBitmap(context, R.drawable.ic_rectangle);
        rectangle = new Rectangle(rec);
        rectangle.getCoordinates().setX(getResources().getDisplayMetrics().widthPixels / 2 - rectangle.getGraphic().getWidth()/2);
        rectangle.getCoordinates().setY(2300);

        // automatically create the graphic every 2 seconds
        thread = new GameThread(getHolder(), this);
        setFocusable(true);
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                autoCreateGraphic();
            }
        }, 0, 2000);
    }

    private void autoCreateGraphic() {
        synchronized (graphics) {
            Random random = new Random();
            GraphicObject graphic = new GraphicObject(bmp);

            int bmpW = graphic.getGraphic().getWidth();
            int bmpH = graphic.getGraphic().getHeight();

            // Randomly generate the x and y coordinates of the bmp
            bmpOriginY = random.nextInt(bmpOriginY) + bmpH / 2;
            graphic.getCoordinates().setX(random.nextInt(Math.max(getWidth() - bmpW, bmpW - getWidth())) + bmpW / 2);
            graphic.getCoordinates().setY(bmpOriginY - bmpH / 2);
            graphics.add(graphic);
        }
    }

    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        Coordinates coordinates;
        Paint paint = new Paint();
        int x, y;
        canvas.drawColor(Color.BLACK);

        // Draw the bottom bar with white color and height of 20
        int startColor = Color.YELLOW;
        int endColor = Color.RED;
        @SuppressLint("DrawAllocation") Shader shader = new LinearGradient(0, getHeight() - barHeight, 0, getHeight(), startColor, endColor, Shader.TileMode.CLAMP);
        paint.setShader(shader);
        canvas.drawRect(0, getHeight() - barHeight, getWidth(), getHeight(), paint);

        // Draw the score
        paint.setTextSize(100);
        paint.setTextAlign(Paint.Align.CENTER);
        String score = "Score: " + graphics.size();
        int canvasWidth = getResources().getDisplayMetrics().widthPixels;
        int textX = canvasWidth / 2;
        int textY = 100;
        canvas.drawText(score, textX, textY, paint);

        // Draw the time
        int timeY = 200;
        long Time = Math.round((System.currentTimeMillis() - startTime) / 1000.0);
        canvas.drawText("Time: " + Time + " s", textX, timeY, paint);

        // Draw the rectangle
        canvas.drawBitmap(rec, rectangle.getCoordinates().getX(), rectangle.getCoordinates().getY(), null);

        synchronized (graphics) {
            for (GraphicObject graphic : graphics) {
                coordinates = graphic.getCoordinates();
                x = coordinates.getX();
                y = coordinates.getY();
                canvas.drawBitmap(bmp, x, y, null);
            }
        }
    }

    public void updateMovement() {
        Coordinates coordinates;
        Movement movement;
        int bmpX, bmpY;
        int recX = rectangle.getCoordinates().getX();
        int recY = rectangle.getCoordinates().getY();
        int recW = rectangle.getGraphic().getWidth();
        int recH = rectangle.getGraphic().getHeight();

        // Store the graphics to be removed
        ArrayList<GraphicObject> graphicsToRemove = new ArrayList<GraphicObject>();

        for (GraphicObject graphic: graphics) {
            coordinates = graphic.getCoordinates();
            movement = graphic.getMovement();
            bmpX = (movement.getXDirection() == movement.X_DIRECTION_RIGHT) ? coordinates.getX() + movement.getXSpeed() : coordinates.getX() - movement.getXSpeed();
            bmpY = (movement.getYDirection() == movement.Y_DIRECTION_DOWN) ? coordinates.getY() + movement.getYSpeed() : coordinates.getY() - movement.getYSpeed();

            // Check for collision with the rectangle
            if (bmpX + graphic.getGraphic().getWidth() >= recX && bmpX <= recX + recW &&
                    bmpY + graphic.getGraphic().getHeight() >= recY && bmpY <= recY + recH) {
                movement.toggleYDirection();
                coordinates.setY(recY - graphic.getGraphic().getHeight());

                int bmpMidX = bmpX + graphic.getGraphic().getWidth() / 2;
                if (bmpMidX < recX || bmpMidX > recX + recW) {
                    movement.toggleXDirection();
                }
            } else {
                if (bmpX < 0) {
                    movement.toggleXDirection();
                    coordinates.setX(-bmpX);
                } else if (bmpX + graphic.getGraphic().getWidth() > getWidth()) {
                    movement.toggleXDirection();
                    coordinates.setX(bmpX + getWidth() - (bmpX + graphic.getGraphic().getWidth()));
                } else {
                    coordinates.setX(bmpX);
                }

                if (bmpY < 0) {
                    movement.toggleYDirection();
                    coordinates.setY(-bmpY);
                } else if (bmpY + graphic.getGraphic().getHeight() > getHeight() - barHeight) {
                    graphicsToRemove.add(graphic); // Remove the graphic if it hits the bottom bar
                } else {
                    coordinates.setY(bmpY);
                }
            }
        }
        graphics.removeAll(graphicsToRemove);
    }
    private void showScore() {
        int score = graphics.size();
        Toast.makeText(getContext(), "Score: " + score, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        synchronized (thread.getSurfaceHolder()) {
            int action = event.getAction();
            int x = (int) event.getX();
            int y = (int) event.getY();

            switch (action) {
                // Check if the touch is within the rectangle
                // record the last touch x coordinate
                case MotionEvent.ACTION_DOWN:
                    if (x >= rectangle.getCoordinates().getX() && x <= rectangle.getCoordinates().getX() + rectangle.getGraphic().getWidth() &&
                            y >= rectangle.getCoordinates().getY() && y <= rectangle.getCoordinates().getY() + rectangle.getGraphic().getHeight()) {
                        lastTouchRecX = x;
                    }
                    break;
                // Move the rectangle according to the last touch x coordinate
                case MotionEvent.ACTION_MOVE:
                    if (lastTouchRecX != -1) {
                        int deltaX = x - lastTouchRecX;
                        lastTouchRecX = x;
                        int newX = rectangle.getCoordinates().getX() + deltaX;
                        int screenWidth = getResources().getDisplayMetrics().widthPixels;
                        int rectangleWidth = rectangle.getGraphic().getWidth();

                        // check if the rectangle is out of the screen
                        if (newX < 0) {
                            newX = 0;
                        } else if (newX + rectangleWidth > screenWidth) {
                            newX = screenWidth - rectangleWidth;
                        }
                        rectangle.getCoordinates().setX(newX);
                        invalidate();
                    }
                    break;
                // Reset the last touch x coordinate
                case MotionEvent.ACTION_UP:
                    lastTouchRecX = -1;
                    break;
            }
        }
        synchronized (thread.getSurfaceHolder()) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                int x = (int) event.getX();
                int y = (int) event.getY();

                // Check if the touch is within the bmp
                boolean bmpClicked = false;

                for (int i = graphics.size() - 1; i >= 0; i--) {
                    GraphicObject bmp = graphics.get(i);
                    int left = bmp.getCoordinates().getX();
                    int top = bmp.getCoordinates().getY();
                    int right = left + bmp.getGraphic().getWidth();
                    int bottom = top + bmp.getGraphic().getHeight();

                    if (x >= left && x <= right && y >= top && y <= bottom) {
                        graphics.remove(i);
                        bmpClicked = true;
                        break;
                    }
                }

                // Check if the touch is within the rectangle or lower than the rectangle
                // and the graphic is not clicked
                if (!bmpClicked && y <= rectangle.getCoordinates().getY() - rectangle.getGraphic().getHeight() / 2 - bmp.getHeight() / 2) {
                    GraphicObject bmp = new GraphicObject(this.bmp);
                    int bmpW = bmp.getGraphic().getWidth();
                    int bmpH = bmp.getGraphic().getHeight();
                    bmp.getCoordinates().setX(x - bmpW / 2);
                    bmp.getCoordinates().setY(y - bmpH / 2);
                    graphics.add(bmp);
                }
            }
            return true;
        }
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        thread.setRunning(true);
        thread.start();
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
        boolean retry = true;
        thread.setRunning(false);
        while (retry) {
            try {
                thread.join();
                retry = false;
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
