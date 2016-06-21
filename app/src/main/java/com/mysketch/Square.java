package com.mysketch;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class Square extends Shapes {

    private final static String LOGTAG = "Square";

    final static String SHAPE_TYPE = "SQUARE";

    private Paint mPainter = new Paint();

    float height;
    float width;

    public  Square(Context context, String projectName, float x, float y, float strokeWidth, float width, float height){
        super(context, projectName, SHAPE_TYPE, x, y, strokeWidth);


        mPainter.setStyle(Paint.Style.STROKE);
        mPainter.setAntiAlias(true);
        mPainter.setStrokeWidth(strokeWidth);


        this.height = height;
        this.width = width;

        invalidate();
    }

    @Override
    public void onDraw(Canvas canvas){

        canvas.save();

        canvas.setMatrix(matrix);
        canvas.drawLine(x,y,x+ width,y, mPainter);
        canvas.drawLine(x,y,x,y+ height, mPainter);
        canvas.drawLine(x+ width,y+ height,x,y+ height, mPainter);
        canvas.drawLine(x+ width,y+ height,x+ width,y, mPainter);

        canvas.restore();
    }

    @Override
    public void setStrokeWidthandColor(int Color, float Stroke){
        mPainter.setColor(Color);
        mPainter.setStrokeWidth(Stroke);
    }

    @Override
    public boolean Intersects(float x, float y){
        float xMax = this.x + strokeWidth/2;
        float xMin = xMax - strokeWidth;
        float yMax = this.y + strokeWidth/2;
        float yMin = yMax - strokeWidth;

        /*
        boolean[] checkFor = {
                //left
                (xMin <= x && x <= xMax && yMin - this.height <= y && y <= yMax),
                //right
                (xMin + this.width <= x && x <= xMax + this.width && yMin - this.height <= y && y <= yMax),
                //top
                (yMin <= y && y <= yMax && xMin <= x && x <= xMax + this.width),
                //bottom
                (yMin + this.height <= y && y <= yMax + this.height && xMin <= x && x <= xMax + this.width),
        };

        return (checkFor[0] || checkFor[1] || checkFor[2] || checkFor[3])
        */

        return (xMin <= x && x <= xMax && yMin <= y && y <= yMax); //all figure
    }
}
