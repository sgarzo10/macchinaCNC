package com.prova.gui.device.view;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import com.prova.gui.device.activity.ConnectionActivity;

@SuppressLint("ViewConstructor")
public class JoystickView extends SurfaceView implements SurfaceHolder.Callback, View.OnTouchListener{

    ConnectionActivity app;
    private float baseRadius;
    private float hatRadius;
    private float centerX;
    private float centerY;
    private float x;
    private float y;
    private  boolean continua;

    @Override
    public float getX() {
        return x;
    }

    @Override
    public float getY() {
        return y;
    }

    public boolean isContinua() {
        return continua;
    }

    public JoystickView(ConnectionActivity app) {
        super(app);
        this.app = app;
        continua = false;
        getHolder().addCallback(this);
        setOnTouchListener(this);
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
        centerX = getWidth() / 2;
        centerY = getHeight() / 2;
        baseRadius = Math.min(getWidth(),getHeight()) / 2.5f;
        hatRadius =  Math.min(getWidth(),getHeight()) / 8;
    }

    public void drawJoystick(float x, float y){
        if (getHolder().getSurface().isValid()){
            Canvas canvas = this.getHolder().lockCanvas();
            Paint colors = new Paint();
            colors.setARGB(255, 251 , 251, 251);
            canvas.drawRect(getLeft(), getTop(), getRight(), getBottom(), colors);
            colors.setARGB(255, 230 , 230, 230);
            canvas.drawCircle(centerX, centerY, baseRadius,colors);
            colors.setARGB(255, 170, 170, 170);
            canvas.drawCircle(x, y, hatRadius, colors);
            getHolder().unlockCanvasAndPost(canvas);
        }
    }

    public boolean onTouch(View v, MotionEvent e){
        if(v.equals(this)){
            continua = false;
            if(e.getAction() != MotionEvent.ACTION_UP){
                float displacement = (float) Math.sqrt(Math.pow(e.getX() - centerX, 2) + Math.pow(e.getY() - centerY, 2));
                if (displacement < baseRadius) {
                    drawJoystick(e.getX(), e.getY());
                    x = e.getX();
                    y = e.getY();
                    continua = true;
                }
                else{
                    float ratio =  baseRadius / displacement;
                    float constrainedX = centerX + (e.getX() - centerX) * ratio;
                    float constrainedY = centerY + (e.getY() - centerY) * ratio;
                    drawJoystick(constrainedX, constrainedY);
                    x = constrainedX;
                    y = constrainedY;
                    continua = true;
                }
                /*Log.i("X",Float.toString(x));
                  Log.i("Y",Float.toString(y));*/
            }
            else
                drawJoystick(getWidth()/2, getHeight()/2);
        }
        return true;
    }
}
