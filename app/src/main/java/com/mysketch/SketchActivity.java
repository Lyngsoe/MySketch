package com.mysketch;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Point;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.MotionEventCompat;
import android.text.InputType;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.benjamin.git.MySketch.R;

import java.util.ArrayList;

public class SketchActivity extends Activity{
    private static final String DEBUG_GESTURE_TAG = "Gestures";
    private static final String ACTIVITY_TAG = "SketchActivity";
    private static final String TOUCH_TAG = "TouchEvents";

    private static final String KEY_PROJECT_NAME = "projectName_key";
    private static final String KEY_CURRENT_SHAPE = "currentShape_key";
    private static final String KEY_SCALE_FACTOR = "scaleFactor_key";
    private static final String KEY_LAST_TOUCH = "lastTouch_key";
    private static final String KEY_MATRIX = "matrix_key";


    private String mCurrentProject;
    private Shapes mCurrentShape;
    private float mScaleFactor;
    private float lastTouchX;
    private float lastTouchY;
    private Matrix matrix;

    private int mDisplayHeight;
    private int mDisplayWidth;
    private float staticScale;
    private FrameLayout mFrame;
    private ArrayList<Shapes> shapesList = new ArrayList<>();
    private GestureDetectorCompat gestureListener;
    private ScaleGestureDetector mScaleGestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sketch);

        //Frame som indeholder alle views
        mFrame = (FrameLayout) findViewById(R.id.frame);

        //Opsætter display størrelse
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        mDisplayHeight = size.y;
        mDisplayWidth = size.x;

        //definition på staticScale
        staticScale = mDisplayHeight / 3;

        //listeners
        gestureListener = new GestureDetectorCompat(this, new MyGestureListener());
        mScaleGestureDetector = new ScaleGestureDetector(this, new ScaleListener());

        //button
        final FloatingActionButton addShape = (FloatingActionButton) findViewById(R.id.btn_add);
        addShape.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addDialog();

            }
        });
        //first instance of the sketchActivity only
        if (savedInstanceState == null) {
            //opsætter matrix
            matrix = new Matrix();
            matrix.reset();

            //scalesFactor
            mScaleFactor = 1;

            //Indstiller project der arbejdes med
            mCurrentProject = getIntent().getStringExtra(MainActivity.PROJECT_NAME_KEY);

            //Loader filer gemt under projectet
            loadSavedData();

            //Test
            //addCircle();

            addDialog();

        }
    }

    private void addDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setItems(R.array.add_types, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        addCircle();
                        System.out.println("case 0: Circle");
                        //Circle ...
                        break;
                    case 1:
                        addSquare();
                        System.out.println("case 1: Square");
                        //Square
                        break;
                    case 2:
                        //addLine();
                        //Line
                        break;
                }
            }
        });

        builder.show();


    }
    private void addCircle() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("radius of circle in staticScale");
        alertDialog.setMessage("Enter text");


        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        alertDialog.setView(input);

        alertDialog.setPositiveButton("YES",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String inputString = input.getText().toString();
                        float floatin;
                        try {
                            floatin = Float.parseFloat(inputString);
                            if(floatin < 0.0f){
                                throw new NumberFormatException();
                            }
                            float[] newCoords = transformCoordinate(new float[] {mDisplayWidth/2, mDisplayHeight/2});
                            makeNewCircle(newCoords[0], newCoords[1], Shapes.STROKE_WIDTH_STANDARD, floatin * staticScale,true);
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

    public void addSquare(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("sidelengths of square in meters");
        LinearLayout linLay = new LinearLayout(this);
        linLay.setOrientation(LinearLayout.VERTICAL);



        final EditText length = new EditText(this);
        final EditText width = new EditText(this);
        length.setHint("length");
        width.setHint("width");
        //length
        length.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        length.setLayoutParams(lp);

        linLay.addView(length);
        linLay.addView(width);

        //width
        width.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        width.setLayoutParams(lp);



        alertDialog.setPositiveButton("YES",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String lengthString = length.getText().toString();
                        String widthString = width.getText().toString();
                        float floatlen;
                        float floatwid;
                        try {
                            floatlen = Float.parseFloat(lengthString);
                            floatwid = Float.parseFloat(widthString);
                            if(floatlen <= 0.0f || floatwid <= 0.0f){
                                throw new NumberFormatException();
                            }
                            float[] newCoords = transformCoordinate(new float[] {mDisplayWidth/2, mDisplayHeight/2});
                            makeNewSquare(newCoords[0], newCoords[1],Shapes.STROKE_WIDTH_STANDARD,floatlen*staticScale,floatwid*staticScale, true);

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
        alertDialog.setView(linLay);
        alertDialog.show();

    }


    @Override
    public void onResume() {
        super.onResume();
        loadSavedData();
    }

    @Override
    public void onPause() {
        saveData();
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
        savedInstanceState.putInt(KEY_CURRENT_SHAPE, (mCurrentShape != null) ? mCurrentShape.uniqueID : -1);
        savedInstanceState.putFloat(KEY_SCALE_FACTOR, mScaleFactor);
        savedInstanceState.putFloatArray(KEY_LAST_TOUCH, new float[] {lastTouchX, lastTouchY});
        float[] v = new float[9];
        matrix.getValues(v);
        savedInstanceState.putFloatArray(KEY_MATRIX, v);

        saveData();
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        mCurrentProject = savedInstanceState.getString(KEY_PROJECT_NAME);
        int uniqueID = savedInstanceState.getInt(KEY_CURRENT_SHAPE);
        mCurrentShape = (uniqueID != -1) ? DataManager.loafSingleFile(getApplicationContext(), mCurrentProject, uniqueID, false) : null;
        mScaleFactor = savedInstanceState.getFloat(KEY_SCALE_FACTOR);
        float[] touchCoords = savedInstanceState.getFloatArray(KEY_LAST_TOUCH);
        assert touchCoords != null;
        lastTouchX = touchCoords[0];
        lastTouchY = touchCoords[1];
        float[] v = savedInstanceState.getFloatArray(KEY_MATRIX);
        matrix = new Matrix();
        matrix.setValues(v);

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
        Shapes[] loadShapes = DataManager.loadAllShapes(getApplicationContext(), mCurrentProject, false);
        if(loadShapes != null && loadShapes.length>0){
            for(Shapes shape : loadShapes){
                shape.setMatrix(matrix);
                shapesList.add(0, shape);
                mFrame.addView(shape);
            }
        }
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
            .setTitle(getResources().getString(R.string.exit_title))
            .setMessage(getResources().getString(R.string.exit_sub_title))
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int whichButton) {
                    SketchActivity.super.onBackPressed();
                }
            })
            .setNegativeButton(android.R.string.no, null)
            .show();
    }

    public boolean onTouchEvent(MotionEvent event){
        boolean retVal = mScaleGestureDetector.onTouchEvent(event);
        retVal = gestureListener.onTouchEvent(event) || retVal;

        final int action = MotionEventCompat.getActionMasked(event);

        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                Log.i(TOUCH_TAG,"Action down!");
                Shapes temp = getIntersectingShape(event.getX(), event.getY());


                if(temp != null) {
                    if(mCurrentShape != null){
                        mCurrentShape.setStrokeWidthandColor(Shapes.COLOR_STANARD,Shapes.STROKE_WIDTH_STANDARD);
                    }
                    mCurrentShape = temp;
                    mCurrentShape.setStrokeWidthandColor(Shapes.COlOR_SELECTED, Shapes.STROKE_WIDTH_SELECTED);

                } else if(mCurrentShape != null) {
                    mCurrentShape.setStrokeWidthandColor(Shapes.COLOR_STANARD,Shapes.STROKE_WIDTH_STANDARD);
                    mCurrentShape = null;
                }

                for (int i = 0; i < mFrame.getChildCount(); i++) {
                    View currentView = mFrame.getChildAt(i);
                    ((Shapes) currentView).setMatrix(matrix);
                    currentView.invalidate();
                }


                break;
            }

            case MotionEvent.ACTION_POINTER_DOWN:
                Log.i(TOUCH_TAG,"Action_Pointer down!");
                break;
        }

        return retVal || super.onTouchEvent(event);
    }

    public Shapes getIntersectingShape(float x, float y) {

        float[] newCoords = transformCoordinate(new float[] {x,y});

        for(Shapes shape : shapesList){

            if(shape.Intersects(newCoords[0], newCoords[1])){
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
                    mCurrentShape.Move(distanceX / mScaleFactor, distanceY / mScaleFactor);
                }
                else{
                    matrix.postTranslate(-distanceX,-distanceY);
                }

                for (int i = 0; i < mFrame.getChildCount(); i++) {
                    View currentView = mFrame.getChildAt(i);
                    ((Shapes) currentView).setMatrix(matrix);
                    currentView.invalidate();
                }


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

            //Sætter Scalefactor
            float newScale = mScaleFactor * detector.getScaleFactor();

            if(newScale > 0.1f && newScale < 5.0f) {

                mScaleFactor *= detector.getScaleFactor();

                transformationMatrix.postTranslate(-focusX, -focusY);
                transformationMatrix.postScale(detector.getScaleFactor(), detector.getScaleFactor());

                float dx = focusX - lastTouchX;
                float dy = focusY - lastTouchY;

                transformationMatrix.postTranslate(focusX + dx, focusY + dy);

                matrix.postConcat(transformationMatrix);

            }
            lastTouchX = focusX;
            lastTouchY = focusY;


            for (int i = 0; i < mFrame.getChildCount(); i++) {
                View currentView = mFrame.getChildAt(i);
                ((Shapes) currentView).setMatrix(matrix);
                currentView.invalidate();
            }
            return true;
        }
    }

    private float[] transformCoordinate(float[] oldCoords){
        //gets matrix values
        float[] v = new float[9];
        matrix.getValues(v);

        //transformation
        float tx = v[Matrix.MTRANS_X];
        float ty = v[Matrix.MTRANS_Y];

        //calculate new coords
        float[] newCoords = {
                (oldCoords[0] - tx) / mScaleFactor,
                (oldCoords[1] - ty) / mScaleFactor,
        };

        return newCoords;
    }

    private Shapes makeNewSquare(float x, float y, float strokeWidth, float height, float width, boolean addInstance){
        Shapes newSquare = new Square(getApplicationContext(), mCurrentProject, x, y, strokeWidth, height, width);
        return addInstance ? addedInstance(newSquare) : newSquare;
    }

    private Shapes makeNewCircle(float x, float y, float strokeWidth, float radius, boolean addInstance){
        Shapes newCircle = new Circle(getApplicationContext(), mCurrentProject, x, y, strokeWidth, radius);
        return addInstance ? addedInstance(newCircle) : newCircle;
    }

    private Shapes makeNewLine(float x, float y, float strokeWidth, float length, boolean isVertical, boolean addInstance){
        x -= length/2;
        y -= length/2;
        float x2 = (isVertical) ? x : x+length;
        float y2 = (isVertical) ? y+length : y;

        Shapes newLine = new Line(getApplicationContext(), mCurrentProject, x, y, strokeWidth, x2, y2);
        return addInstance ? addedInstance(newLine) : newLine;
    }

    private Shapes addedInstance(Shapes shape){
        shape.uniqueID = DataManager.getUniqueID(mCurrentProject);
        DataManager.saveAndOverwriteSingleShape(shape);
        shape.setMatrix(matrix);
        shapesList.add(0, shape);
        mFrame.addView(shape);

        return shape;
    }
}

