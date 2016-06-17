package com.mysketch;

import android.content.Context;
import android.app.Activity;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.shapes.Shape;
import android.os.Bundle;
import android.app.*;
import android.os.*;
import android.accessibilityservice.*;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.MotionEventCompat;
import android.util.Log;
import android.view.*;
import android.gesture.*;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.ScrollView;


import com.example.benjamin.git.MySketch.R;

public class SketchActivity extends Activity{
    RelativeLayout mFrame;
    final String TAG = "SketchActivity";
    int mDisplayHeight;
    int mDisplayWidth;
    GestureDetectorCompat gestureListener;
    final Handler handler=new Handler();

    PointF screenPos;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frame);
        mFrame = (RelativeLayout) findViewById(R.id.frame);
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        mDisplayHeight = size.y;
        mDisplayWidth = size.x;
        screenPos = new PointF(0,0);
        gestureListener = new GestureDetectorCompat(this, new MyGestureListener());
        mFrame.addView(new Circle(this,100,100,200));
        mFrame.addView(new Square(this,100,100,100,100));
        mFrame.addView(new Square(this,50,50,50,50));


        new Thread(new Runnable() {
            @Override
            public void run () {
        handler.post(new Runnable(){
            @Override
            public void run() {
                Log.i(TAG,"thread called");
                //float XScreen = screenPos.x;
                //float YScreen = screenPos.y;
                for(int i = 0; i < mFrame.getChildCount();i++){
                    View currentView = mFrame.getChildAt(i);
                    //float XView = currentView.getX();
                    //float YView = currentView.getY();
                    Log.i(TAG,"entered loop");
                //    if(XView <= XScreen+mDisplayWidth && XView >= XScreen){
                  //      if(YView <= YScreen+mDisplayHeight && YView >= YScreen){
                    //        Log.i(TAG,"invalidate called");
                            ((Shapes) currentView).setDraw(screenPos.x,screenPos.y);
                            currentView.invalidate();

                        }


                handler.postDelayed(this,1); // set time here to refresh
            }
        });
            }
        }).start();


    }
    public boolean onTouchEvent(MotionEvent event){
        this.gestureListener.onTouchEvent(event);
        return super.onTouchEvent(event);
    }
    class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
        private static final String DEBUG_TAG = "Gestures";

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
                                float distanceY) {
            Log.i(DEBUG_TAG, "entered onScroll");
            screenPos.offset(distanceX,distanceY);
            String pos = (int) screenPos.x + " " + (int) screenPos.y;
            Log.i(DEBUG_TAG,pos);
            return true;
        }

    }
    public PointF getScreenPos(){
        return screenPos;
    }

}

