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

    private Paint paint = new Paint();
    float h,w;

    public  Square(Context context, String projectName, boolean addInstance, float w, float h, float x, float y){
        super(context, projectName, SHAPE_TYPE, addInstance);
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(4.5f);
        this.h = h;
        this.w = w;
        this.x = x;
        this.y = y;

        if(addInstance){
            DataManager.saveAndOverwriteSingleShape(this);
        }

        invalidate();
    }

    public boolean Intersects(float x, float y){
        //TODO
        return false;
    }

    @Override
    public void Move(float dx, float dy) {
        //TODO
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
