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
    private float posX = 100;
    private float posY = 100;
    private GestureDetector mDetector;

    public  Square(Context context, float w, float h){
        super(context);
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(4.0f);
        this.h = h;
        this.w = w;
        invalidate();


    }

    public boolean Intersects(float x, float y){

       // if()

        return true;
    }

    public void setCoord(float x, float y){
        this.posX = x;
        this.posY = y;
    }

    public float getXCoord(){
        return posX;
    }

    public float getYCoord(){
        return posY;
    }



    @Override
    public void onDraw(Canvas canvas){



        canvas.save();
       // canvas.drawLine(0,0,150,150,paint);

        canvas.drawLine(posX,posY,posX+w,posY,paint);
        canvas.drawLine(posX,posY,posX,posY+h,paint);
        canvas.drawLine(posX+w,posY+h,posX,posY+h,paint);
        canvas.drawLine(posX+w,posY+h,posX+w,posY,paint);
       // canvas.drawLine(150,0,0,150,paint);
        //canvas.drawLine(200,500,400,500,paint);
       // mcanvas = new Canvas(Bitmap.createBitmap(h,w, Bitmap.Config.ARGB_8888));
        // mcanvas.drawColor(Color.BLACK);



        canvas.restore();
    }

    public void clicksOnsquare(){

    }
}
