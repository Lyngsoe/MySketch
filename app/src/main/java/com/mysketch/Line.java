package com.mysketch;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;

public class Line extends Shapes {

    private final static String LOGTAG = "Line";

    final static String SHAPE_TYPE = "LINE";

    private final Paint mPainter = new Paint();

    float x2;
    float y2;

    public Line(Context context, String projectName, float x, float y, float strokeWidth, float x2, float y2){
        super(context, projectName, SHAPE_TYPE, x, y, strokeWidth);

        mPainter.setStyle(Paint.Style.STROKE);
        mPainter.setStrokeWidth(strokeWidth);


        this.x2 = x2;
        this.y2 = y2;

        invalidate();
    }

    @Override
    public void setStrokeWidthandColor(int Color, float Stroke){
        mPainter.setColor(Color);
        mPainter.setStrokeWidth(Stroke);
    }

    @Override
    public synchronized void onDraw(Canvas canvas) {

        canvas.save();

        canvas.setMatrix(matrix);
        canvas.drawLine(x,y,x2,y2,mPainter);

        canvas.restore();
    }

    @Override
    public boolean Intersects(float px, float py) {
        float dist = (float) ( Math.abs((px-x)*(y2-y)-(py-y)*(x2-x)) / Math.hypot(x2-x, y2-x) );
        return dist <= strokeWidth;
    }
}
