package com.mysketch;

import android.content.Context;
import android.app.Activity;
import android.os.Bundle;
import android.app.*;
import android.os.*;
import android.accessibilityservice.*;
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


import com.example.benjamin.git.MySketch.R;

public class SketchActivity extends Activity {
    RelativeLayout mFrame;
    final String TAG = "SketchActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frame);
        mFrame = (RelativeLayout) findViewById(R.id.frame);

        mFrame.addView(new Sqaure(this, 0,0));


    }


}

