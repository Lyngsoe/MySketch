package com.mysketch;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.benjamin.git.MySketch.R;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by Benjamin on 16-06-2016.
 */
public class SetupProject extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Dimensions dimensions = new Dimensions(0,0);
        setContentView(R.layout.setup);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        final int height = displayMetrics.heightPixels;

        getWindow().setLayout((int) (width * .8), (int) (height * .6));

        final EditText editHeight = (EditText) findViewById(R.id.setHeight);
        final EditText editWidth = (EditText) findViewById(R.id.setWidth);


        editHeight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editHeight.setText("");
            }
        });
        editWidth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editWidth.setText("");
            }
        });

        final Button applyWH = (Button) findViewById(R.id.btn_go);
        applyWH.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String h = editHeight.getText().toString();
                String w = editWidth.getText().toString();

                if (!h.equals(null) && !h.equals("") && !w.equals(null) && !w.equals("")) {

                    dimensions.setDimensions(Integer.parseInt(h), Integer.parseInt(w));

                    Intent sketch = new Intent(getBaseContext(), TempSketch.class);
                    Bundle data = new Bundle();
                    ArrayList<Integer> temp = dimensions.getDimensions();
                    data.putIntegerArrayList("Dimensions", temp);
                    sketch.putExtra("Dimensions", data);
                    startActivity(sketch);
                }
            }
        });

    }

    class Dimensions{
        int width;
        int height;

       public Dimensions(int h,int w){
            height = h;
            width = w;
        }
        public void setDimensions(int h, int w){
            height = h;
            width = w;

        }
        public ArrayList<Integer> getDimensions(){
            ArrayList<Integer> dimensions = new ArrayList<Integer>();
            dimensions.add(height);
            dimensions.add(width);
            return dimensions;
        }

    }
}
