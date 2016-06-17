package com.mysketch;

import android.app.Activity;
import android.os.Bundle;
import android.view.*;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.example.benjamin.git.MySketch.R;


/**
 * Created by Sean on 16-06-2016.
 */
public class SketchDrawing extends Activity{
    private static final String TAG = "Making of a SurfaceView";

    private RelativeLayout mFrame;
    private GestureDetector mGestureDetector;

    protected void onCreate(Bundle savedInstanceState){


        super.onCreate(savedInstanceState);
        setContentView(R.layout.frame);

        mFrame = (RelativeLayout) findViewById(R.id.frame);
        mFrame.addView(new Square(this,300,100));
        mFrame.addView(new Circle(this,150,400,600));

        Button b1 = (Button) findViewById(R.id.b1);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFrame.removeAllViews();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();




    }
    private void setupGestureDetector(){

    mGestureDetector = new GestureDetector(this,new GestureDetector.SimpleOnGestureListener()) {

        public boolean onSingleTapConfirmed(MotionEvent event){
            int pointind = event.getActionIndex();
            int pointid = event.getPointerId(pointind);


            float x = event.getX(pointid);
            float y = event.getY(pointid);

            boolean selected = false;

            for(int i=0 ; i<mFrame.getChildCount(); i++){

            }
            return true;
        }

    };

    }




    @Override
    protected void onPause() {
        super.onPause();
    }


    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
