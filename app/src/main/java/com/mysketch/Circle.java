package com.mysketch;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;

public class Circle extends Shapes {

    private final static String LOGTAG = "Circle";

    final static String SHAPE_TYPE = "CIRCLE";

    private final Paint mPainter = new Paint();

    float radius;


    public Circle(Context context, String projectName, float x, float y, float strokeWidth, float radius){
        super(context, projectName, SHAPE_TYPE, x, y, strokeWidth);

        mPainter.setStyle(Paint.Style.STROKE);
        mPainter.setStrokeWidth(strokeWidth);

        this.radius=radius;

        invalidate();
    }

    @Override
    public synchronized void onDraw(Canvas canvas) {

        canvas.save();

        canvas.setMatrix(matrix);
        canvas.drawCircle(x,y,radius,mPainter);

        canvas.restore();
    }
    @Override
    public void setStrokeWidthandColor(int Color, float Stroke){
        mPainter.setColor(Color);
        mPainter.setStrokeWidth(Stroke);
    }

    @Override
    public boolean Intersects(float x, float y) {
        float max = radius + strokeWidth/2;
        float min = radius - strokeWidth/2;
        float dist = (float) Math.hypot(this.x-x,this.y-y);

        //return min <= dist && dist <= max;

        return dist <= max; //all figure

    }
}
