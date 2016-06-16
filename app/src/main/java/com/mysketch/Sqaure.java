package com.mysketch;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import com.example.benjamin.git.MySketch.R;

public class Sqaure extends View {
    private static final int BITMAP_SIZE = 64;
    private static final int REFRESH_RATE = 40;
    private final Paint mPainter = new Paint();
    private Bitmap mBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.sq);
    float x;
    float y;

    public Sqaure(Context context, float x, float y){
        super(context);
        this.x = x;
        this.y = y;
        mPainter.setAntiAlias(true);
        invalidate();
    }
    @Override
    protected synchronized void onDraw(Canvas canvas) {

        // TODO - save the canvas
        canvas.save();

        // TODO - draw the bitmap at its new location
        canvas.drawBitmap(mBitmap, x, y,mPainter);

        // TODO - restore the canvas
        canvas.restore();
    }

}
