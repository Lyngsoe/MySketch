package com.mysketch;

import android.content.Context;
import android.graphics.PointF;
import android.view.GestureDetector;
import android.view.View;

/**
 * Created by Sean on 17-06-2016.
 */
public class Shapes extends View {
    float mScaleFactor = 1;
    PointF zoomPoint;

    public Shapes(Context context){
        super(context);
    }

    public boolean Intersects(float x, float y){return false;}

    public void setDraw(float x, float y){}

    public float getXCoord(){return -1.0f;}

    public float getYCoord(){return -1.0f;}

    public void Move(float dx, float dy){}

    public void setScale(float scale){mScaleFactor = scale;}

    public void setZoomPoint(PointF zoomPoint){this.zoomPoint = zoomPoint;}






}
