package com.prova.gui.device;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import com.prova.bluetooth.BluetoothConnection;

public class JoystickView extends SurfaceView implements SurfaceHolder.Callback, View.OnTouchListener{

    ConnectionActivity app;

    public JoystickView(ConnectionActivity app) {
        super(app);
        this.app = app;
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

    public void drawJoystick(float x, float y){
        if (getHolder().getSurface().isValid()){
            Canvas canvas = this.getHolder().lockCanvas();
            Paint colors = new Paint();
            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            colors.setARGB(255, 50 , 50, 50);
            canvas.drawCircle(getWidth()/2,getHeight() / 2,Math.min(getWidth(), getHeight()) / 3,colors);
            colors.setARGB(255, 0, 0, 255);
            canvas.drawCircle(x,y, Math.min(getWidth(), getHeight()) / 5, colors);
            getHolder().unlockCanvasAndPost(canvas);
        }
    }

    public boolean onTouch(View v, MotionEvent e){
        if(v.equals(this)){
            if(e.getAction() != e.ACTION_UP){
                float displacement = (float) Math.sqrt(Math.pow(e.getX() - getWidth()/2, 2) + Math.pow(e.getY() - getHeight()/2, 2));
                if (displacement < (Math.min(getWidth(), getHeight()) / 3))
                    drawJoystick(e.getX(),e.getY());
                else{
                    float ratio =  (Math.min(getWidth(), getHeight()) / 3) / displacement;
                    float constrainedX = getWidth()/2 + (e.getX() - getWidth()/2) * ratio;
                    float constrainedY = getHeight() / 2 + (e.getY() - getHeight() / 2) * ratio;
                    drawJoystick(constrainedX, constrainedY);
                    app.getBluetooth().invia("Joystick");
                }
            }
            else
                drawJoystick(getWidth()/2, getHeight()/2);
        }
        return true;
    }
}
