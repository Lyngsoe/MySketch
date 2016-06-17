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

    final Button newProject = (Button) findViewById(R.id.btn_new);
        newProject.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent choose = new Intent(getBaseContext(), SketchActivity.class);
                startActivity(choose);
            }
        });
    }
}
