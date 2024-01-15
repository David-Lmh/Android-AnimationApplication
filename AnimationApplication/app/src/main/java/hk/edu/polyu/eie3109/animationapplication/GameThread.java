package hk.edu.polyu.eie3109.animationapplication;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

public class GameThread extends Thread {
    private final SurfaceHolder surfaceHolder;
    private final Panel panel;
    private boolean run = false;

    public GameThread(SurfaceHolder surfaceHolder, Panel panel) {
        this.surfaceHolder = surfaceHolder;
        this.panel = panel;
    }

    public void setRunning(boolean run) {
        this.run = run;
    }

    @Override
    public void run() {
        super.run();
        Canvas c;
        while (run) {
            c = null;
            try {
                c = surfaceHolder.lockCanvas();
                synchronized (surfaceHolder) {
                    panel.updateMovement();
                    panel.onDraw(c);
                }
            } finally {
                if(c != null) {
                    surfaceHolder.unlockCanvasAndPost(c);
                }
            }
        }
    }

    public SurfaceHolder getSurfaceHolder() {
        return surfaceHolder;
    }
}
