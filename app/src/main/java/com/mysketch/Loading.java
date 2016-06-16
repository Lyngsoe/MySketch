package com.mysketch;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.example.benjamin.git.MySketch.R;


/**
 * Created by Benjamin on 15-06-2016.
 */
public class Loading extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading);
        final ImageView imageView = (ImageView) findViewById(R.id.imageView);

        final Animation animation = AnimationUtils.loadAnimation(getBaseContext(), R.anim.rotate);
        final Animation endAnimation = AnimationUtils.loadAnimation(getBaseContext(), R.anim.abc_fade_out);

        imageView.startAnimation(animation);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                imageView.startAnimation(endAnimation);
                finish();
                Intent mainActivity = new Intent(getBaseContext(), MainActivity.class);
                startActivity(mainActivity);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }
}
