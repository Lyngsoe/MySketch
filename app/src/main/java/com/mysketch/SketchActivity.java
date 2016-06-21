package com.mysketch;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.MotionEventCompat;
import android.text.Layout;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.benjamin.git.MySketch.R;

import java.util.ArrayList;

public class SketchActivity extends Activity{
    private static final String DEBUG_GESTURE_TAG = "Gestures";
    private static final String ACTIVITY_TAG = "SketchActivity";
    private static final String TOUCH_TAG = "TouchEvents";

    private static final String KEY_PROJECT_NAME = "projectName_key";

    RelativeLayout mFrame;

    Shapes mCurrentShape;
    String mCurrentProject;
    ArrayList<Shapes> shapesList = new ArrayList<>();


    int mDisplayHeight;
    int mDisplayWidth;
    GestureDetectorCompat gestureListener;
    ScaleGestureDetector mScaleGestureDetector;
    float meter;
    PointF screenPos = new PointF(0,0);
    float mScaleFactor = 1;
    boolean isRunning = false;
    float lastTouchX;
    float lastTouchY;
    Matrix m;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frame);

        //Frame som indeholder alle views
        mFrame = (RelativeLayout) findViewById(R.id.frameSketch);

        //Opsætter display størrelse
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        mDisplayHeight = size.y;
        mDisplayWidth = size.x;

        //definition på meter
        meter = mDisplayHeight/3;
        m = new Matrix();
        m.reset();

        gestureListener = new GestureDetectorCompat(this, new MyGestureListener());
        mScaleGestureDetector = new ScaleGestureDetector(this, new ScaleListener());

        //button
        final Button butt = (Button) findViewById(R.id.button);
        butt.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                testDialog();
            }
        });


        //Indstiller project der arbejdes med
        mCurrentProject = getIntent().getStringExtra(MainActivity.PROJECT_NAME_KEY);

        //Loader filer gemt under projectet
        loadSavedData();

        //Test
        testDialog();

    }
    private void testDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("radius of circle in meter");
        alertDialog.setMessage("Enter text");

        final EditText input = new EditText(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        alertDialog.setView(input);

        alertDialog.setPositiveButton("YES",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String inputString = input.getText().toString();
                        float floatin = -1.0f;
                        try {
                            floatin = Float.parseFloat(inputString);
                            if(floatin <= 0.0f){
                                throw new NumberFormatException();
                            }
                            Shapes temp = new Circle(SketchActivity.this, mCurrentProject, true, screenPos.x,  screenPos.y, floatin*meter); //TODO make new stuff be drawn in the middle
                            temp.setMatrix(m);
                            mFrame.addView(temp);
                            shapesList.add(0, temp);
                        }
                        catch(NumberFormatException e) {
                            Toast.makeText(getApplicationContext(), "wrong input", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        alertDialog.setNegativeButton("NO",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        alertDialog.show();
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
        DataManager.saveAndOverwriteAllShapes(shapesList.toArray(new Shapes[shapesList.size()]));
    }

    //loader alle shapes for det nuværende project.
    private void loadSavedData(){
        shapesList = new ArrayList<>();
        mFrame.removeAllViews();
        Shapes[] loadShapes = DataManager.loadAllShapes(getApplicationContext(), mCurrentProject, false, false);
        if(loadShapes != null && loadShapes.length>0){
            for(Shapes shape : loadShapes){
                shape.setMatrix(m);
                shapesList.add(0, shape);
                mFrame.addView(shape);
            }
        }
    }

    public boolean onTouchEvent(MotionEvent event){
        boolean retVal = mScaleGestureDetector.onTouchEvent(event);
        retVal = gestureListener.onTouchEvent(event) || retVal;

        final int action = MotionEventCompat.getActionMasked(event);

        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                Log.i(TOUCH_TAG,"Action down!");

                mCurrentShape = getIntersectingShape(event.getX(), event.getY());
                try {
                    mCurrentShape.ChangeColorAndStrokeW(Color.BLUE, 6f);
                } catch (Exception(mCurrentShape==null));
                    



                break;
            }

            case MotionEvent.ACTION_POINTER_DOWN:
                Log.i(TOUCH_TAG,"Action_Pointer down!");
                break;
        }

        return retVal || super.onTouchEvent(event);
    }

    public Shapes getIntersectingShape(float x, float y) {
        //transforms x and y
        Matrix minv = new Matrix();
        m.invert(minv);
        float[] v = new float[9];
        m.getValues(v);

        //calculations
        float tx = v[Matrix.MTRANS_X];
        float ty = v[Matrix.MTRANS_Y];

        float coordx = (x - tx) / mScaleFactor;
        float coordy = (y - ty) / mScaleFactor;




        Log.i("Hej","Coord x: " + coordx + " Coord y: " +coordy);
        Log.i("Hej","Scale factor: " + mScaleFactor);

        for(Shapes shape : shapesList){

            if(shape.Intersects(coordx, coordy)){
                Log.i("Hej","Shape x: " + shape.x + " Shape y: "+ shape.y);
                return shape;
            }
        }
        return null;
    }

    class MyGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if (!mScaleGestureDetector.isInProgress()) {

                if(mCurrentShape != null){
                    //TODO scale x og y
                    //transforms x and y

                    mCurrentShape.Move(distanceX / mScaleFactor, distanceY / mScaleFactor);
                }
                else{
                    m.postTranslate(-distanceX,-distanceY);
                }

                for (int i = 0; i < mFrame.getChildCount(); i++) {
                    View currentView = mFrame.getChildAt(i);
                    ((Shapes) currentView).setMatrix(m);
                    currentView.invalidate();
                }

                String pos = (int) screenPos.x + " " + (int) screenPos.y;
              //  Log.i(DEBUG_GESTURE_TAG, pos);


            }
            return true;
        }

    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            lastTouchX = detector.getFocusX();
            lastTouchY = detector.getFocusY();
            return true;
        }
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            Matrix transformationMatrix = new Matrix();
            float focusX = detector.getFocusX();
            float focusY = detector.getFocusY();
           // Log.i(DEBUG_GESTURE_TAG,"entered onScale");
            String scale = mScaleFactor+ " ";
            //Log.i(DEBUG_GESTURE_TAG,scale);

            //Sætter Scalefactor
            mScaleFactor *= detector.getScaleFactor();
            mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 5.0f));

            transformationMatrix.postTranslate(-focusX, -focusY);
            transformationMatrix.postScale(detector.getScaleFactor(), detector.getScaleFactor());

            float dx = focusX-lastTouchX;
            float dy = focusY-lastTouchY;

            transformationMatrix.postTranslate(focusX + dx, focusY + dy);
            m.postConcat(transformationMatrix);

            lastTouchX = focusX;
            lastTouchY = focusY;


            for (int i = 0; i < mFrame.getChildCount(); i++) {
                View currentView = mFrame.getChildAt(i);
                ((Shapes) currentView).setMatrix(m);
                currentView.invalidate();
            }
            return true;
        }
    }
}

