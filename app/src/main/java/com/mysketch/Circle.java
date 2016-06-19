package com.mysketch;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * Created by Sean on 16-06-2016.
 */
public class Circle extends Shapes {

    final static String SHAPE_TYPE = "CIRCLE";

    final static String LOGTAG = "Circle";

    private final Paint mPainter = new Paint();
    float radius;


    public Circle(Context context, String projectName, boolean addInstance, float x, float y, float radius){
        super(context, projectName, SHAPE_TYPE, addInstance);
        mPainter.setStyle(Paint.Style.STROKE);
        mPainter.setStrokeWidth(4.5f);
        this.radius=radius;
        this.x = x;
        this.y = y;

        if(addInstance){
            DataManager.saveAndOverwriteSingleShape(this);
        }
    }
    @Override
    protected synchronized void onDraw(Canvas canvas) {

        canvas.save();

        canvas.scale(mScaleFactor, mScaleFactor,zoomPoint.x,zoomPoint.y);
        canvas.drawCircle(x-drawX,y-drawY,radius,mPainter);
        //canvas.drawCircle(x-getTranslationX(),y-getTranslationY(),100,mPainter);

        canvas.restore();
    }

    @Override
    public boolean Intersects(float x, float y) {
        //TODO
        return false;
    }

    @Override
    public void Move(float dx, float dy) {
        //TODO
    }

}
