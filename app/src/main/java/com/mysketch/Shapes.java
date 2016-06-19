package com.mysketch;

import android.content.Context;
import android.graphics.PointF;
import android.view.View;

/**
 * Created by Sean on 17-06-2016.
 */
public abstract class Shapes extends View {
    float mScaleFactor = 1;
    PointF zoomPoint;

    float x;
    float y;
    float drawX;
    float drawY;

    String projectName;
    int uniqueID;
    String shapeType;

    public Shapes(Context context, String projectName, String shapeType, boolean addInstance){
        super(context);
        this.projectName = projectName;
        this.shapeType = shapeType;
        if(addInstance) {
            this.uniqueID = DataManager.getUniqueID(projectName);
        }
    }

    public void setDraw(float x, float y){
        this.drawX=x;
        this.drawY=y;
    }

    public void setCoord(float x, float y){
        this.x = x;
        this.y = y;
    }

    public float getX(){
        return x;
    }

    public float getY(){
        return y;
    }

    public float getXCoord(){
        return drawX;
    }

    public float getYCoord(){
        return drawY;
    }

    public void setScale(float scale){
        mScaleFactor = scale;
    }

    public void setZoomPoint(PointF zoomPoint){
        this.zoomPoint = zoomPoint;
    }

    public abstract boolean Intersects(float x, float y);

    public abstract void Move(float dx, float dy);








}
