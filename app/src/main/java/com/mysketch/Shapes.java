package com.mysketch;

import android.content.Context;
import android.view.GestureDetector;
import android.view.View;

/**
 * Created by Sean on 17-06-2016.
 */
public class Shapes extends View {


    public Shapes(Context context){
        super(context);
    }

    public boolean Intersects(float x, float y){return false;}

    public void setCoord(float x, float y){}

    public float getXCoord(){return -1.0f;}

    public float getYCoord(){return -1.0f;}

    public void Move(float dx, float dy){}








}
