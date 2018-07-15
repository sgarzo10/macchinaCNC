package com.prova.gui.device.view;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import com.prova.gui.device.activity.ConnectionActivity;

@SuppressLint("ViewConstructor")
public class QuadratoView extends SurfaceView implements SurfaceHolder.Callback{

    private float radius;
    private float currentX;
    private float currentY;
    private int maxX;
    private int maxY;
    private boolean primo;
    private Bitmap mBitmap;
    private ConnectionActivity app;

    public void setMaxX(int maxX) { this.maxX = maxX; }
    public void setMaxY(int maxY) { this.maxY = maxY; }

    public QuadratoView(ConnectionActivity app) {
        super(app);
        this.app = app;
        maxX = 0;
        maxY = 0;
        primo = true;
        mBitmap = null;
        getHolder().addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (!app.isResume())
            mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config. ARGB_8888);
        else
            app.setResume();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    public void calcolaDimensioni(){
        radius = Math.min(getWidth(),getHeight()) / 240f;
    }

    public void drawPoint(float x, float y){
        if (maxX != 0) {
            x = (x * (getWidth() - 10)) / maxX;
            x = x + 5;
            x = getWidth() - x;
        }
        if (maxY != 0) {
            y = (y * (getHeight() - 12)) / maxY;
            y = y + 6;
            y = getHeight() - y;
        }
        if (getHolder().getSurface().isValid() && x >= 5 && x <= getWidth() - 5 && y >= 6 && y <= getHeight() - 6){
            Paint colors = new Paint();
            currentX = x;
            currentY = y;
            if (mBitmap == null)
                return;
            Canvas canvas = new Canvas(mBitmap);
            if (primo){
                primo = false;
                colors.setARGB(255, 229, 209, 162);
                canvas.drawColor(colors.getColor());
            }
            colors.setARGB(255, 167, 0, 0);
            canvas.drawCircle(x, y, radius, colors);
            canvas = getHolder().lockCanvas(null);
            canvas.drawBitmap(mBitmap, 0, 0, null);
            getHolder().unlockCanvasAndPost(canvas);
        }
    }

    public void pulisci(){
        if (getHolder().getSurface().isValid()) {
            Canvas canvas = new Canvas(mBitmap);
            Paint colors = new Paint();
            colors.setARGB(255, 229, 209, 162);
            canvas.drawColor(colors.getColor());
            colors.setARGB(255, 167, 0, 0);
            canvas.drawCircle(currentX, currentY, radius, colors);
            canvas = getHolder().lockCanvas(null);
            canvas.drawBitmap(mBitmap, 0, 0, null);
            getHolder().unlockCanvasAndPost(canvas);
        }
    }
}
