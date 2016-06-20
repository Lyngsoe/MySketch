package com.mysketch;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

/**
 * Created by Sean on 16-06-2016.
 */
public class Square extends Shapes {

    final static String SHAPE_TYPE = "SQUARE";

    final static String LOGTAG = "Circle";
    final static float STROKE_WIDTH = 4.5f;

    private Paint paint = new Paint();
    float h,w;

    public  Square(Context context, String projectName, boolean addInstance, float w, float h, float x, float y){
        super(context, projectName, SHAPE_TYPE, addInstance, x, y);
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(STROKE_WIDTH);
        this.h = h;
        this.w = w;

        if(addInstance){
            DataManager.saveAndOverwriteSingleShape(this);
        }

        invalidate();
    }

    public boolean Intersects(float x, float y){
        float xMax = this.x + STROKE_WIDTH/2;
        float xMin = xMax - STROKE_WIDTH;
        float yMax = this.y + STROKE_WIDTH/2;
        float yMin = yMax - STROKE_WIDTH;

        boolean[] checkFor = {
                //left
                (xMin < x && x < xMax && yMin - this.h < y && y < yMax),
                //right
                (xMin + this.w < x && x < xMax + this.w && yMin - this.h < y && y < yMax),
                //top
                (yMin < y && y < yMax && xMin < x && x < xMax + this.w),
                //bottom
                (yMin + this.h < y && y < yMax + this.h && xMin < x && x < xMax + this.w),
        };
        for(Boolean b : checkFor){
            if(b) return true;
        }
        return false;
    }

    @Override
    public void Move(float dx, float dy) {
        this.x+=dx;
        this.y+=dy;
    }

    @Override
    public void onDraw(Canvas canvas){

        canvas.save();

        canvas.setMatrix(m);
        canvas.drawLine(x,y,x+w,y,paint);
        canvas.drawLine(x,y,x,y+h,paint);
        canvas.drawLine(x+w,y+h,x,y+h,paint);
        canvas.drawLine(x+w,y+h,x+w,y,paint);

        canvas.restore();
    }

    @Override
    public void ChangeColorAndStrokeW(int c, float strokeWidth){
        paint.setColor(c);
        paint.setStrokeWidth(strokeWidth);

    }
}
