package com.mysketch;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.view.GestureDetector;
import android.view.SurfaceView;
import android.view.View;

/**
 * Created by Sean on 16-06-2016.
 */
public class Square extends Shapes {


    private Paint paint = new Paint();
    private float h,w;
    private float x,y = 100;
    private float drawX;
    private float drawY;

    public  Square(Context context, float w, float h, float x, float y){
        super(context);
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(4.5f);
        this.h = h;
        this.w = w;

        invalidate();


    }

    public boolean Intersects(float x, float y){

       // if()

        return true;
    }

    public void setCoord(float x, float y){
        this.x = x;
        this.y = y;
    }

    public void setDraw(float x, float y){
        this.drawX=x;
        this.drawY=y;
    }

    public float getXCoord(){
        return drawX;
    }

    public float getYCoord(){
        return drawY;
    }



    @Override
    public void onDraw(Canvas canvas){



        canvas.save();
        canvas.scale(mScaleFactor, mScaleFactor,zoomPoint.x,zoomPoint.y);
        canvas.drawLine(x-drawX,y-drawY,x-drawX+w,y-drawY,paint);
        canvas.drawLine(x-drawX,y-drawY,x-drawX,y-drawY+h,paint);
        canvas.drawLine(x-drawX+w,y-drawY+h,x-drawX,y-drawY+h,paint);
        canvas.drawLine(x-drawX+w,y-drawY+h,x-drawX+w,y-drawY,paint);

        canvas.restore();
    }

    public void clicksOnsquare(){

    }
}
