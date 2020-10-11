package com.yumin.pomodoro.utils;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;
import android.widget.ListView;

import androidx.annotation.ColorInt;

import com.yumin.pomodoro.R;

import java.util.Locale;

public class ItemDecoratorBorderListView extends ItemDecorationListView {
    private final Paint paint = new Paint();
    private final int size;

    public ItemDecoratorBorderListView(int size, @ColorInt int color) {
        this.size = size;
        paint.setColor(color);
        paint.setStrokeWidth(size);
        paint.setStyle(Paint.Style.STROKE);
    }

    public static final String TAG = ItemDecoratorBorderListView.class.getSimpleName();

    @Override
    public void onDrawOver(Canvas canvas, ListView parent) {
        super.onDrawOver(canvas, parent);
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);
            if (isHeader(child, parent, i)) {
                for (int j = i + 1; j < childCount; j++) {
                    View childEnd = parent.getChildAt(j);
                    boolean end = isHeader(childEnd, parent, i) || j == childCount - 1;
                    if (end) {
                        LogUtil.logD(TAG, String.format(Locale.ENGLISH, "Draw called i: %d, j: %d", i, j));
                        childEnd = parent.getChildAt(j - 1);
                        if (j == childCount - 1) { childEnd = parent.getChildAt(j); }
                        float top = child.getTop() + child.getTranslationY() + size + child.getPaddingTop();
                        float bottom = childEnd.getBottom() + childEnd.getTranslationY() - size - childEnd
                                .getPaddingBottom();

                        float right = child.getRight() + child.getTranslationX() - size - child.getPaddingRight();
                        float left = child.getLeft() + child.getTranslationX() + size + child.getPaddingLeft();
                        canvas.drawRect(left, top, right, bottom, paint);
                        i = j - 1;
                        break;
                    }
                }
            }
        }
    }

    public boolean isHeader(View child, ListView parent, int position) {
        //You need to set an Id for your layout
        return child.getId() == R.id.header;
    }
}
