package com.mysketch;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;

/**
 * Created by Sean on 16-06-2016.
 */
public class Circle extends Shapes {

    final static String SHAPE_TYPE = "CIRCLE";

    final static String LOGTAG = "Circle";
    final static float STROKE_WIDTH = 4.5f;

    private final Paint mPainter = new Paint();
    float radius;


    public Circle(Context context, String projectName, boolean addInstance, float x, float y, float radius){
        super(context, projectName, SHAPE_TYPE, addInstance, x, y);
        mPainter.setStyle(Paint.Style.STROKE);
        mPainter.setStrokeWidth(STROKE_WIDTH);
        this.radius=radius;

        if(addInstance){
            DataManager.saveAndOverwriteSingleShape(this);
        }
    }
    @Override
    protected synchronized void onDraw(Canvas canvas) {

        canvas.save();

        canvas.setMatrix(m);
        canvas.drawCircle(x-drawX,y-drawY,radius,mPainter);
        //canvas.drawCircle(x-getTranslationX(),y-getTranslationY(),100,mPainter);

        canvas.restore();
    }

    @Override
    public boolean Intersects(float x, float y) {
        float max = radius + STROKE_WIDTH/2;
        float dist = (float) Math.sqrt(Math.pow(this.x-x, 2) + Math.pow(this.y-y, 2));
        return dist < max;//- STROKE_WIDTH && dist < max;
    }

    @Override
    public void Move(float dx, float dy) {
        //float[] v = new float[9];
        //m.getValues(v);
        this.x -= dx; //* v[Matrix.MSCALE_X];
        this.y -= dy; //* v[Matrix.MSCALE_Y];
    }

}
