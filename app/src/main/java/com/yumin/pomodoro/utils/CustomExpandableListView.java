package com.yumin.pomodoro.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.ExpandableListView;

import java.util.ArrayList;
import java.util.List;

public class CustomExpandableListView extends ExpandableListView {
    private List<ItemDecorationListView> itemDecorations = new ArrayList<>(1);
    public CustomExpandableListView(Context context) {
        super(context);
    }

    public CustomExpandableListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void addItemDecoration(ItemDecorationListView item){
        itemDecorations.add(item);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        final int count = itemDecorations.size();
        for (int i = 0; i < count; i++) {
            itemDecorations.get(i).onDrawOver(canvas, this);
        }
    }
}
