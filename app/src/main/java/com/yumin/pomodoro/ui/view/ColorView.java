package com.yumin.pomodoro.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.yumin.pomodoro.utils.LogUtil;

public class ColorView extends View {
    private Context mContext;
    private int mColorValue = Color.TRANSPARENT; //Default value
    private Paint mPaint;
    private Path path;
    private int mStrokeWidth = 8;
    private boolean mSelect = false;

    public ColorView(Context context) {
        super(context);
        initColorView();
        mContext = context;
    }

    public ColorView(Context context, int color) {
        super(context);
        mColorValue = color;
        initColorView();
        mContext = context;
    }

    public ColorView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initColorView();
        mContext = context;
    }


    public ColorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initColorView();
        mContext = context;
    }


    public void initColorView(){
//        mColorValue = Color.BLACK; // Default value. If want to change color use setColor method.
        mPaint = new Paint();
        path = new Path();
        mPaint.setColor(mColorValue);
        mPaint.setStrokeWidth(mStrokeWidth);
        mPaint.setDither(true);
        mPaint.setAntiAlias(true);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        super.onDraw(canvas);
        LogUtil.logD("[ColorView]","[onDraw]");
        float cx = getWidth() / 2.0f;
        float cy = getHeight() / 2.0f;
        float radius = Math.min(getWidth(), getHeight());
        radius = ( radius - 20 ) / 2.0f;
        canvas.drawCircle(cx, cy, radius, mPaint);

        if(mSelect){
            Paint.Style paintStyle = mPaint.getStyle();
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setColor(Color.GRAY);
            path.moveTo(cx - 15, cy);
            path.lineTo(cx, cy + 15 );
            path.lineTo(cx + 15, cy - 15);
            canvas.drawPath(path,mPaint);
            mPaint.setColor(mColorValue);
            mPaint.setStyle(paintStyle);
        }
    }

    public void setColorValue(int color){
        this.mColorValue = color;
        mPaint.setColor(mColorValue);
        invalidate();
    }

    public int getColorValue(){
        return this.mColorValue;
    }

    public void setSelect(boolean select){
        this.mSelect = select;
        invalidate();
    }

    public boolean getSelect(){
        return this.mSelect;
    }
}