package com.yumin.pomodoro.customize;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;

import com.yumin.pomodoro.utils.LogUtil;

public class SlideExpandableListView extends ExpandableListView {
    private static final String TAG = SlideExpandableListView.class.getSimpleName();
    private int mScreenWidth;
    private int mDownX;
    private int mDownY;
    private int mDeleteBtnWidth;
    private int mEditBtnWidth;
    private boolean isDeleteShown;
    private ViewGroup mPointChild;
    private LinearLayout.LayoutParams mLayoutParams;
    private boolean mIsChildLayout = false;

    public SlideExpandableListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlideExpandableListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        mScreenWidth = displayMetrics.widthPixels;
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                performActionDown(motionEvent);
                break;
            case MotionEvent.ACTION_MOVE:
                if (mIsChildLayout) {
                    super.onTouchEvent(motionEvent);
                    getParent().requestDisallowInterceptTouchEvent(true); // disable viewpager touch event
                    return performActionMove(motionEvent);
                }
            case MotionEvent.ACTION_UP:
                if (mIsChildLayout) {
                    performActionUp();
                    break;
                }
        }
        return super.onTouchEvent(motionEvent);
    }

    private void performActionDown(MotionEvent ev) {
        if (isDeleteShown) {
            turnToNormal();
        }
        mDownX = (int) ev.getX();
        mDownY = (int) ev.getY();

        mPointChild = (ViewGroup) getChildAt(pointToPosition(mDownX, mDownY)
                - getFirstVisiblePosition());

        if (mPointChild == null || mPointChild.getChildCount() < 3) {
            LogUtil.logD(TAG, "[performActionDown] mPointChild == null");
            mIsChildLayout = false;
            return;
        }
        mIsChildLayout = true;

        if (mPointChild.getChildAt(1) != null)
            mEditBtnWidth = mPointChild.getChildAt(1).getLayoutParams().width;
        LogUtil.logD(TAG, "[performActionDown] mEditBtnWidth = " + mEditBtnWidth);

        if (mPointChild.getChildAt(2) != null)
            mDeleteBtnWidth = mPointChild.getChildAt(2).getLayoutParams().width;
        LogUtil.logD(TAG, "[performActionDown] mDeleteBtnWidth = " + mDeleteBtnWidth);

        mLayoutParams = (LinearLayout.LayoutParams) mPointChild.getChildAt(0)
                .getLayoutParams();
        mLayoutParams.width = mScreenWidth;
        mPointChild.getChildAt(0).setLayoutParams(mLayoutParams);
    }

    private boolean performActionMove(MotionEvent ev) {
        int nowX = (int) ev.getX();
        int nowY = (int) ev.getY();
        if (Math.abs(nowX - mDownX) > Math.abs(nowY - mDownY)) {
            if (nowX < mDownX) {
                int scroll = (nowX - mDownX) / 2;
                if (-scroll >= mEditBtnWidth) {
                    scroll = -mDeleteBtnWidth - mEditBtnWidth;
                }
                if (mPointChild == null)
                    return false;

                mLayoutParams.leftMargin = scroll;
                mPointChild.getChildAt(0).setLayoutParams(mLayoutParams);
            }
            return true;
        }
        return super.onTouchEvent(ev);
    }

    private void performActionUp() {
        if (mPointChild == null)
            return;

        if (-mLayoutParams.leftMargin >= mEditBtnWidth / 2) {
            mLayoutParams.leftMargin = -mDeleteBtnWidth - mEditBtnWidth - 100;
            isDeleteShown = true;
        } else {
            turnToNormal();
        }

        mPointChild.getChildAt(0).setLayoutParams(mLayoutParams);
    }

    public void turnToNormal() {
        LogUtil.logD(TAG, "[turnToNormal]");
        mLayoutParams.leftMargin = 0;
        mPointChild.getChildAt(0).setLayoutParams(mLayoutParams);
        isDeleteShown = false;
    }

    public boolean canClick() {
        return !isDeleteShown;
    }
}
