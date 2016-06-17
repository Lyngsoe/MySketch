package com.mysketch;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

/**
 * Created by Sean on 16-06-2016.
 */
public class Circle extends View {


    private Paint paint = new Paint();
    private float R;
    private float posX;
    private float posY;


    public Circle(Context context, float R, float posX, float posY){
        super(context);
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(4.0f);
        this.R = R;
        this.posX=posX;
        this.posY=posY;

        invalidate();


    }


    @Override
    public void onDraw(Canvas canvas){



        canvas.save();
       // canvas.drawLine(0,0,150,150,paint);
        canvas.drawCircle(posX,posY,R,paint);
       // canvas.drawLine(150,0,0,150,paint);
        //canvas.drawLine(200,500,400,500,paint);
       // mcanvas = new Canvas(Bitmap.createBitmap(h,w, Bitmap.Config.ARGB_8888));
        // mcanvas.drawColor(Color.BLACK);



        canvas.restore();
    }

    public void clicksOnsquare(){

    }
}
