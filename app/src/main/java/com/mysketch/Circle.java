package com.mysketch;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

/**
 * Created by Sean on 16-06-2016.
 */
public class Circle extends Shapes {

    final String TAG = "Square";
    private final Paint mPainter = new Paint();
    float x;
    float y;
    float drawX,drawY;
    float radius;


    public Circle(Context context, float x, float y, float radius){
        super(context);
        mPainter.setStyle(Paint.Style.STROKE);
        mPainter.setStrokeWidth(4.5f);
        this.x = x;
        this.y = y;
        this.radius=radius;

    }
    @Override
    protected synchronized void onDraw(Canvas canvas) {

        // TODO - save the canvas
        canvas.save();
        // TODO - draw at its new location
        canvas.scale(mScaleFactor, mScaleFactor,zoomPoint.x,zoomPoint.y);
        canvas.drawCircle(x-drawX,y-drawY,radius,mPainter);
        //canvas.drawCircle(x-getTranslationX(),y-getTranslationY(),100,mPainter);

        // TODO - restore the canvas
        canvas.restore();
    }

    public void setCoord(float x, float y){
        this.x = x;
        this.y = y;
    }

    public void setDraw(float x, float y){
        this.drawX=x;
        this.drawY=y;
    }

}
