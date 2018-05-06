package com.prova.gui.device.utility;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.prova.gui.device.activity.ConnectionActivity;

@SuppressLint("ViewConstructor")
public class QuadratoView extends SurfaceView implements SurfaceHolder.Callback{

    ConnectionActivity app;
    private float radius;
    private float currentX;
    private float currentY;
    private int maxX;
    private int maxY;

    public float getCurrentX() {
        return currentX;
    }
    public float getCurrentY() {
        return currentY;
    }
    public void setMaxX(int maxX) { this.maxX = maxX; }
    public void setMaxY(int maxY) { this.maxY = maxY; }

    public QuadratoView(ConnectionActivity app) {
        super(app);
        this.app = app;
        maxX = 0;
        maxY = 0;
        getHolder().addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    public void calcolaDimensioni(){
        radius = Math.min(getWidth(),getHeight()) / 60f;
        currentX = getWidth() / 2;
        currentY = getHeight() / 2;
    }

    public void drawPoint(float x, float y){
        if (maxX != 0) {
            x = (x * (getWidth() - 10)) / maxX;
            x = x + 5;
        }
        if (maxY != 0) {
            y = (y * (getHeight() - 12)) / maxY;
            y = y + 6;
        }
        if (getHolder().getSurface().isValid()){
            Canvas canvas = this.getHolder().lockCanvas();
            Paint colors = new Paint();
            if (x >= 5 && x <= getWidth() - 4 && y >= 6 && y <= getHeight() - 6) {
                currentX = x;
                currentY = y;
                colors.setARGB(255, 229 , 209, 162);
                canvas.drawRect(getLeft(), getTop(), getRight(), getBottom(), colors);
                colors.setARGB(255, 167, 0, 0);
                canvas.drawCircle(x, y, radius, colors);
            }
            getHolder().unlockCanvasAndPost(canvas);
        }
    }
}
