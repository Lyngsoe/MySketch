package com.mysketch;

import android.app.Activity;
import android.graphics.Point;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.MotionEventCompat;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.RelativeLayout;

import com.example.benjamin.git.MySketch.R;

import java.util.ArrayList;

public class SketchActivity extends Activity{
    private static final String DEBUG_GESTURE_TAG = "Gestures";
    private static final String ACTIVITY_TAG = "SketchActivity";
    private static final String TOUCH_TAG = "TouchEvents";

    private static final String KEY_PROJECT_NAME = "projectName_key";

    RelativeLayout mFrame;

    String mCurrentProject;
    ArrayList<Shapes> shapesList = new ArrayList<>();

    int mDisplayHeight;
    int mDisplayWidth;
    GestureDetectorCompat gestureListener;
    ScaleGestureDetector mScaleGestureDetector;
    final Handler handler=new Handler();
    float meter;
    PointF screenPos = new PointF(0,0);
    float mScaleFactor = 1;
    boolean isRunning = false;
    float  mLastTouchX;
    float mLastTouchY;
    int mActivePointerId;

    PointF start = new PointF();
    PointF mid = new PointF();
    float oldDist = 1f;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frame);

        //Frame som indeholder alle views
        mFrame = (RelativeLayout) findViewById(R.id.frame);

        //Opsætter display størrelse
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        mDisplayHeight = size.y;
        mDisplayWidth = size.x;

        //definition på meter
        meter = mDisplayHeight/3;

        gestureListener = new GestureDetectorCompat(this, new MyGestureListener());
        mScaleGestureDetector = new ScaleGestureDetector(this, new ScaleListener());

        //Indstiller project der arbejdes med
        mCurrentProject = getIntent().getStringExtra(MainActivity.PROJECT_NAME_KEY);

        //Loader filer gemt under projectet
        loadSavedData();

        //Test
        new Circle(this, mCurrentProject, true, 100,100,200);
        new Square(this, mCurrentProject, true, 100,100,100,100);
        new Square(this, mCurrentProject, true, 50,50,50,50);



        new Thread(new Runnable() {
            @Override
            public void run () {
        handler.post(new Runnable(){
            @Override
            public void run() {
                if (isRunning) {
                    for (int i = 0; i < mFrame.getChildCount(); i++) {
                        View currentView = mFrame.getChildAt(i);
                        ((Shapes) currentView).setDraw(screenPos.x, screenPos.y); //offset position
                        ((Shapes) currentView).setScale(mScaleFactor); //Zoom faktor
                        ((Shapes) currentView).setZoomPoint(mid); //Zoom centrum
                        currentView.invalidate();
                    }
                }

                handler.postDelayed(this, 1); //tid mellem grafikKald
            }
        });}
        }).start();

    }

    @Override
    public void onResume() {
        super.onResume();
        loadSavedData();
        isRunning = true;
    }

    @Override
    public void onPause() {
        saveData();
        isRunning = false;
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        saveData();
        super.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putString(KEY_PROJECT_NAME, mCurrentProject);
        saveData();
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        mCurrentProject = savedInstanceState.getString(KEY_PROJECT_NAME);
        loadSavedData();
        super.onRestoreInstanceState(savedInstanceState);
    }

    //Gemmer alle shapes der arbejdes på i det nuværende projectet
    private void saveData(){
        Log.e("DataManager", shapesList.get(0).getX()+"  "+shapesList.get(1).drawY);
        DataManager.saveAndOverwriteAllShapes(shapesList.toArray(new Shapes[shapesList.size()]));
    }

    //loader alle shapes for det nuværende project.
    private void loadSavedData(){
        shapesList = new ArrayList<>();
        Shapes[] loadShapes = DataManager.loadAllShapes(getApplicationContext(), mCurrentProject, false, false);
        if(loadShapes != null && loadShapes.length>0){
            for(Shapes shape : loadShapes){
                shapesList.add(shape);
                mFrame.addView(shape);
            }
        }
        Log.e("DataManager", shapesList.get(0).getX()+"  "+shapesList.get(1).drawY);
    }

    private void midPoint(PointF point, MotionEvent event) {
        // finder midtpunktet mellem to touches
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }

    public boolean onTouchEvent(MotionEvent event){
        boolean retVal = mScaleGestureDetector.onTouchEvent(event);
        retVal = gestureListener.onTouchEvent(event) || retVal;

        final int action = MotionEventCompat.getActionMasked(event);

        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                Log.i(TOUCH_TAG,"Action down!");
                break;
            }
            case MotionEvent.ACTION_POINTER_DOWN:
                Log.i(TOUCH_TAG,"Action_Pointer down!");
                //zoom centrum
                midPoint(mid, event);
                break;
        }


        return retVal || super.onTouchEvent(event);
    }

    class MyGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if (!mScaleGestureDetector.isInProgress()) {
                Log.i(DEBUG_GESTURE_TAG, "entered onScroll");

                //Rykker skærmen
                screenPos.offset(distanceX, distanceY);


                String pos = (int) screenPos.x + " " + (int) screenPos.y;
                Log.i(DEBUG_GESTURE_TAG, pos);

            }
            return true;
        }

    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            Log.i(DEBUG_GESTURE_TAG,"entered onScale");
            String scale = mScaleFactor+ " ";
            Log.i(DEBUG_GESTURE_TAG,scale);

            //Sætter Scalefactor
            mScaleFactor *= detector.getScaleFactor();
            mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 5.0f));
            return true;
        }
    }
}

