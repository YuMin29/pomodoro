package com.yumin.pomodoro.ui.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.databinding.BindingAdapter;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.InverseBindingListener;
import androidx.databinding.InverseBindingMethod;
import androidx.databinding.InverseBindingMethods;

import com.yumin.pomodoro.BR;
import com.yumin.pomodoro.R;
import com.yumin.pomodoro.databinding.ItemDialogviewBinding;
import com.yumin.pomodoro.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

@InverseBindingMethods({@InverseBindingMethod(type = ItemDialogView.class,
        attribute = "itemValue", event = "itemValueAttrChanged")})
public class ItemDialogView extends LinearLayout {
    private static final String TAG = "[ItemDialogView]";
    private ItemDialogviewBinding viewBinding;
    private InverseBindingListener inverseBindingListener;
    private int content;

    public ItemDialogView(Context context) {
        super(context);
        inflateView(context);
    }

    public ItemDialogView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        inflateView(context);
    }

    public ItemDialogView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflateView(context);
    }

    private void inflateView(Context context) {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        viewBinding = DataBindingUtil.inflate(inflater, R.layout.item_dialogview,this,true);
        viewBinding.itemLinearLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                        // show color list dialog
                        ColorSelectorDialog dialog = new ColorSelectorDialog(context,
                                getThemeColors(),getCurrentColorPosition());
                        dialog.setClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                switch (v.getId()){
                                    case R.id.buttonOk:
                                        int color = dialog.getSelectColor();
                                        LogUtil.logD(TAG,"choose color = "+color);
                                        setItemValue(color);
                                        dialog.dismiss();
                                        break;

                                    case R.id.buttonCancel:
                                        // Do nothing
                                        dialog.dismiss();
                                        break;
                                }
                            }
                        });
                        dialog.show();

        }});
    }

    private List<ColorViewInfo> getThemeColors() {
        final List<ColorViewInfo> colorList = new ArrayList<ColorViewInfo>();
        // Get color array list from resource. Define in color.xml.
        String[] backgroundAllColors = this.getResources().getStringArray(R.array.backgroundColors);
        for(String colorValue : backgroundAllColors){
            int convertColor = Color.parseColor(colorValue);
            colorList.add(new ColorViewInfo(false,convertColor));
        }
        return colorList;
    }

    public void setItemValue(int color){
        viewBinding.colorView.setColorValue(color);
        if (inverseBindingListener != null)
            inverseBindingListener.onChange();
        this.content = color;
    }

    private int getCurrentColorPosition(){
        String[] backgroundAllColors = this.getResources().getStringArray(R.array.backgroundColors);
        int position = 0;
        for(String colorValue : backgroundAllColors){
            int convertColor = Color.parseColor(colorValue);
            if (convertColor == content)
                return position;
            position++;
        }
        return 0;
    }

    public int getItemValue(){
        return  viewBinding.colorView.getColorValue();
    }

    public void setItemDescription(String string) {
        viewBinding.setVariable(BR.itemDescription,string);
    }

    public void setItemValueAttrChanged(InverseBindingListener inverseBindingListener){
        this.inverseBindingListener = inverseBindingListener;
    }
}