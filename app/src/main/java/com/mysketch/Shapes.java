package com.mysketch;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.view.View;

public abstract class Shapes extends View {

    static float STROKE_WIDTH_STANDARD = 10f;

    protected Matrix matrix;
    protected float x;
    protected float y;
    protected float strokeWidth;

    final String projectName;
    final String shapeType;
    int uniqueID;

    public Shapes(Context context, String projectName, String shapeType, float x, float y, float strokeWidth){
        super(context);
        this.projectName = projectName;
        this.shapeType = shapeType;
        this.x = x;
        this.y = y;
        this.strokeWidth = strokeWidth;
    }

    public void Move(float dx, float dy) {
        this.x -= dx;
        this.y -= dy;
    }

    public void setCoordinates(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float getX(){
        return x;
    }

    public float getY(){
        return y;
    }

    public float getStrokeWidth(){
        return strokeWidth;
    }

    public void setStrokeWidth(float strokeWidth){
        this.strokeWidth = strokeWidth;
    }

    public void setMatrix(Matrix matrix){
        this.matrix = matrix;
    }

    @Override
    public abstract void onDraw(Canvas canvas);

    public abstract boolean Intersects(float x, float y);
}
