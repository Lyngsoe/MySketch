package com.mysketch;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.example.benjamin.git.MySketch.R;


public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button bt1 = (Button) findViewById(R.id.button);
        bt1.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v){
                Intent NI = new Intent(MainActivity.this,SketchDrawing.class);
                startActivity(NI);
            }
        });

    }

}
