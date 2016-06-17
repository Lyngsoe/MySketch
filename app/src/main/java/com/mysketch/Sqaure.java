package com.mysketch;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.example.benjamin.git.MySketch.R;

public class Sqaure extends View {

    final String TAG = "Square";
    private final Paint mPainter = new Paint();
    float x;
    float y;
    float drawX,drawY;


    public Sqaure(Context context, float x, float y){
        super(context);
        this.x = x;
        this.y = y;

    }
    @Override
    protected synchronized void onDraw(Canvas canvas) {

        // TODO - save the canvas
        canvas.save();
        // TODO - draw at its new location
        canvas.drawCircle(x-drawX,y-drawY,100,mPainter);
        //canvas.drawCircle(x-getTranslationX(),y-getTranslationY(),100,mPainter);

        // TODO - restore the canvas
        canvas.restore();
    }

    public void setDrawX(float drawX) {
        this.drawX = drawX;
    }

    public void setDrawY(float drawY) {
        this.drawY = drawY;
    }
}
