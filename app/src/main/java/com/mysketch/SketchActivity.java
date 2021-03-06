package com.mysketch;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.widget.LinearLayout;
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

    private static final float MIN_SCALE = 0.1f;
    private static final float MAX_SCALE = 5.0f;
    private static final float STATIC_SCALE = 500f;

    private String mCurrentProject;
    private Shapes mCurrentShape;
    private float mScaleFactor;
    private float lastTouchX;
    private float lastTouchY;
    private Matrix matrix;

    private int mDisplayHeight;
    private int mDisplayWidth;
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

        //listeners
        gestureListener = new GestureDetectorCompat(this, new MyGestureListener());
        mScaleGestureDetector = new ScaleGestureDetector(this, new ScaleListener());

        //button
        final FloatingActionButton addShape = (FloatingActionButton) findViewById(R.id.btn_add);
        addShape.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mCurrentShape == null ){
                    addDialog();
                    return;
                }
                changeShape();
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
        }
    }

    private void changeShape(){
        String shapeType = mCurrentShape.shapeType;
        float x = mCurrentShape.getX();
        float y = mCurrentShape.getY();

        switch(shapeType){
            case Circle.SHAPE_TYPE:{
                changeCircle();
                break;
            }
            case Square.SHAPE_TYPE:{
                changeSquare();
                break;
            }
            case Line.SHAPE_TYPE:{
                changeLine();
                break;
            }
        }

    }

    private void changeCircle(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle(R.string.change_circle_title);
        alertDialog.setMessage(R.string.make_circle_sub_title);


        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        input.setHint("" + ((Circle) mCurrentShape).radius/ STATIC_SCALE);
        alertDialog.setView(input);

        alertDialog.setPositiveButton(R.string.default_enter,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String inputString = input.getText().toString();
                        float floatin;
                        try {
                            floatin = Float.parseFloat(inputString);
                            if(floatin <= 0.0f){
                                throw new NumberFormatException();
                            }

                            ((Circle) mCurrentShape).radius = floatin * STATIC_SCALE;

                            mCurrentShape.invalidate();
                            DataManager.saveAndOverwriteSingleShape(mCurrentShape);
                        }
                        catch(NumberFormatException e) {
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.invalid_input), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        alertDialog.setNegativeButton(R.string.default_cancel,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        alertDialog.show();
    }

    private void changeSquare(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle(R.string.change_square_title);
        alertDialog.setMessage(R.string.make_square_sub_title);
        LinearLayout linLay = new LinearLayout(this);
        linLay.setOrientation(LinearLayout.VERTICAL);



        final EditText height = new EditText(this);
        final EditText width = new EditText(this);
        height.setHint("" + ((Square) mCurrentShape).height/ STATIC_SCALE);
        width.setHint("" + ((Square) mCurrentShape).width/ STATIC_SCALE);
        //height
        height.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        height.setLayoutParams(lp);

        linLay.addView(height);
        linLay.addView(width);

        //width
        width.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        width.setLayoutParams(lp);

        alertDialog.setPositiveButton(R.string.default_enter,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String heightString = height.getText().toString();
                        String widthString = width.getText().toString();
                        float floathei;
                        float floatwid;
                        try {
                            floathei = Float.parseFloat(heightString);
                            floatwid = Float.parseFloat(widthString);
                            if(floathei <= 0.0f || floatwid <= 0.0f){
                                throw new NumberFormatException();
                            }
                            ((Square) mCurrentShape).width = floatwid * STATIC_SCALE;
                            ((Square) mCurrentShape).height = floathei * STATIC_SCALE;

                            mCurrentShape.invalidate();
                            DataManager.saveAndOverwriteSingleShape(mCurrentShape);

                        }
                        catch(NumberFormatException e) {
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.invalid_input), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        alertDialog.setNegativeButton(R.string.default_cancel,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        alertDialog.setView(linLay);
        alertDialog.show();
    }

    private void changeLine() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle(R.string.change_line_title);
        alertDialog.setMessage(R.string.make_line_sub);


        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);

        boolean isVertical = (mCurrentShape.x == ((Line) mCurrentShape).x2);
        String hint = (isVertical) ? "" + Math.abs(mCurrentShape.getY()-((Line) mCurrentShape).y2)/ STATIC_SCALE : "" + Math.abs(mCurrentShape.getX()-((Line) mCurrentShape).x2)/ STATIC_SCALE;
        input.setHint(hint);

        alertDialog.setView(input);

        alertDialog.setPositiveButton(R.string.default_enter,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String inputString = input.getText().toString();
                        float floatin;
                        try {
                            floatin = Float.parseFloat(inputString);
                            if(floatin < 0.0f){
                                throw new NumberFormatException();
                            }
                            float length = floatin * STATIC_SCALE;
                            boolean isVertical = (mCurrentShape.x == ((Line) mCurrentShape).x2);
                            ((Line) mCurrentShape).x2 = (isVertical) ? mCurrentShape.getX() : mCurrentShape.getX()+length;
                            ((Line) mCurrentShape).y2 = (isVertical) ? mCurrentShape.getY()+length : mCurrentShape.getY();

                            mCurrentShape.invalidate();
                            DataManager.saveAndOverwriteSingleShape(mCurrentShape);


                        }
                        catch(NumberFormatException e) {
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.invalid_input), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        alertDialog.setNegativeButton(R.string.default_cancel,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        alertDialog.show();
    }

    private void addDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setItems(R.array.add_types, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        addCircle();
                        //Circle
                        return;
                    case 1:
                        addSquare();
                        //Square
                        return;
                    case 2:
                        addHLine();
                        //HLine
                        return;
                    case 3:
                        addVLine();
                        //VLine
                        return;
                }
            }
        });

        builder.show();


    }
    private void addCircle() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle(R.string.make_circle_title);
        alertDialog.setMessage(R.string.make_circle_sub_title);


        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        input.setHint(R.string.make_circle_hint);
        alertDialog.setView(input);

        alertDialog.setPositiveButton(R.string.default_enter,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String inputString = input.getText().toString();
                        float floatin;
                        try {
                            floatin = Float.parseFloat(inputString);
                            if(floatin <= 0.0f){
                                throw new NumberFormatException();
                            }
                            float[] newCoords = transformCoordinate(new float[] {mDisplayWidth/2, mDisplayHeight/2});
                            makeNewCircle(newCoords[0], newCoords[1], Shapes.STROKE_WIDTH_STANDARD, floatin * STATIC_SCALE,true);
                        }
                        catch(NumberFormatException e) {
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.invalid_input), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        alertDialog.setNegativeButton(R.string.default_cancel,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        alertDialog.show();
    }

    public void addSquare(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle(R.string.make_square_title);
        alertDialog.setMessage(R.string.make_square_sub_title);
        LinearLayout linLay = new LinearLayout(this);
        linLay.setOrientation(LinearLayout.VERTICAL);



        final EditText height = new EditText(this);
        final EditText width = new EditText(this);
        height.setHint(R.string.make_square_hint_height);
        width.setHint(R.string.make_square_hint_width);
        //height
        height.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        height.setLayoutParams(lp);

        linLay.addView(height);
        linLay.addView(width);

        //width
        width.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        width.setLayoutParams(lp);



        alertDialog.setPositiveButton(R.string.default_enter,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String lengthString = height.getText().toString();
                        String widthString = width.getText().toString();
                        float floathei;
                        float floatwid;
                        try {
                            floathei = Float.parseFloat(lengthString);
                            floatwid = Float.parseFloat(widthString);
                            if(floathei <= 0.0f || floatwid <= 0.0f){
                                throw new NumberFormatException();
                            }
                            float[] newCoords = transformCoordinate(new float[] {mDisplayWidth/2, mDisplayHeight/2});
                            makeNewSquare(newCoords[0], newCoords[1],Shapes.STROKE_WIDTH_STANDARD,floatwid* STATIC_SCALE,floathei* STATIC_SCALE, true);

                        }
                        catch(NumberFormatException e) {
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.invalid_input), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        alertDialog.setNegativeButton(R.string.default_cancel,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        alertDialog.setView(linLay);
        alertDialog.show();

    }
    private void addVLine() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle(R.string.make_line_title_vertical);
        alertDialog.setMessage(R.string.make_line_sub);


        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        input.setHint(R.string.make_line_hint);
        alertDialog.setView(input);

        alertDialog.setPositiveButton(R.string.default_enter,
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
                            makeNewLine(newCoords[0], newCoords[1], Shapes.STROKE_WIDTH_STANDARD, floatin * STATIC_SCALE, true, true);
                        }
                        catch(NumberFormatException e) {
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.invalid_input), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        alertDialog.setNegativeButton(R.string.default_cancel,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        alertDialog.show();
    }

    private void addHLine() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle(R.string.name_line_title_horizontal);
        alertDialog.setMessage(R.string.make_line_sub);


        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        input.setHint(R.string.make_line_hint);
        alertDialog.setView(input);

        alertDialog.setPositiveButton(R.string.default_enter,
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
                            makeNewLine(newCoords[0], newCoords[1], Shapes.STROKE_WIDTH_STANDARD, floatin * STATIC_SCALE,false,true);
                        }
                        catch(NumberFormatException e) {
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.invalid_input), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        alertDialog.setNegativeButton(R.string.default_cancel,
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
        mCurrentShape = (uniqueID != -1) ? DataManager.loadSingleFile(getApplicationContext(), mCurrentProject, uniqueID, false) : null;
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
                        mCurrentShape.setStrokeWidthandColor(Shapes.COLOR_STANDARD,Shapes.STROKE_WIDTH_STANDARD);
                    }
                    mCurrentShape = temp;
                    mCurrentShape.setStrokeWidthandColor(Shapes.COlOR_SELECTED, Shapes.STROKE_WIDTH_SELECTED);

                }
                else if(mCurrentShape != null) {
                    mCurrentShape.setStrokeWidthandColor(Shapes.COLOR_STANDARD,Shapes.STROKE_WIDTH_STANDARD);
                    mCurrentShape = null;
                }

                renderAll();

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
                    mCurrentShape.move(distanceX / mScaleFactor, distanceY / mScaleFactor);
                }
                else{
                    matrix.postTranslate(-distanceX,-distanceY);
                }

                renderAll();


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

            float newScale = mScaleFactor * detector.getScaleFactor();

            if(MIN_SCALE < newScale && newScale < MAX_SCALE) {

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

            renderAll();

            return true;
        }
    }

    private void renderAll(){
        for (int i = 0; i < mFrame.getChildCount(); i++) {
            View currentView = mFrame.getChildAt(i);
            ((Shapes) currentView).setMatrix(matrix);
            currentView.invalidate();
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
        Shapes newSquare = new Square(getApplicationContext(), mCurrentProject, x-height/2, y-width/2, strokeWidth, height, width);
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

