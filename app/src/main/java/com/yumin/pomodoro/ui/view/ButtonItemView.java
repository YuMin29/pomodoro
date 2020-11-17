package com.yumin.pomodoro.ui.view;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.InputType;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.yumin.pomodoro.R;
import com.yumin.pomodoro.databinding.MissionItemButtonViewBinding;
import com.yumin.pomodoro.databinding.MissionItemNumViewBinding;
import com.yumin.pomodoro.utils.LogUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ButtonItemView extends LinearLayout {
    private static final String TAG = "[ButtonItemView]";
    private MissionItemButtonViewBinding viewBinding;
    private boolean isUseImage = false;
    private boolean isEnabled;

    public ButtonItemView(Context context) {
        super(context);
        inflateView(context);
    }

    public ButtonItemView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        inflateView(context);
    }

    public ButtonItemView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflateView(context);
    }

    private void inflateView(Context context) {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        viewBinding = DataBindingUtil.inflate(inflater, R.layout.mission_item_button_view,this,true);
        viewBinding.itemLinearLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isUseImage) {
                    // switch icon when click button
                    if (isEnabled) {
                        isEnabled = false;
                    } else {
                        isEnabled = true;
                    }
                    setItemContent(isEnabled);
                } else {
                    if (viewBinding.descriptionTextview.getText().toString().equals(getResources().getString(R.string.mission_theme))) {
                        // show color list dialog
                        ColorSelectorDialog dialog = new ColorSelectorDialog(context,
                                getThemeColors(),1);
                        dialog.setClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                switch (v.getId()){
                                    case R.id.buttonOk:
                                        int color = dialog.getSelectColor();
                                        LogUtil.logD(TAG,"choose color = "+color);
                                        setItemContent(color);
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
                    } else if (viewBinding.descriptionTextview.getText().toString().equals(getResources().getString(R.string.mission_operate_day))) {
                        // show calendar
                        Calendar calendar = Calendar.getInstance();
                        DatePickerDialog datePickerDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                Log.d(TAG,"[datePickerDialog][onDateSet]");
                                Calendar chooseDate = Calendar.getInstance();
                                chooseDate.set(year,month,dayOfMonth);
                                Date chooseDateTime = chooseDate.getTime();
                                int compareResult = chooseDateTime.compareTo(Calendar.getInstance().getTime());
                                if (compareResult == 0) {
                                    // equal
                                    setItemContent("TODAY");
                                } else {
                                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
                                    String chooseStr = simpleDateFormat.format(chooseDateTime);
                                    viewBinding.valTextview.setTextSize(26);
                                    setItemContent(chooseStr);
                                }
                            }
                        }, calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH));

                        datePickerDialog.show();
                    } else  if (viewBinding.descriptionTextview.getText().toString().equals(getResources().getString(R.string.mission_repeat))) {{
                        //create dialog to set value
                        String[] repeatOptions = getResources().getStringArray(R.array.repeat_array);
                        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                                .setTitle("請設置"+viewBinding.descriptionTextview.getText())
                                .setItems(repeatOptions, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        setItemContent(repeatOptions[which]);
                                    }
                                });
                        builder.show();
                    }
                }
            }
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

    public void setItemContent(String content1){
        viewBinding.valTextview.setText(content1);
    }

    public void setItemContent(boolean enabled){
        isUseImage = true;
        isEnabled = enabled;
        int imgRsc;

        if (enabled) {
            imgRsc = R.drawable.ic_check_circle_black_24dp;
        } else {
            imgRsc = R.drawable.ic_cancel_black_24dp;
        }

        viewBinding.imageView.setImageResource(imgRsc);
        viewBinding.imageView.setVisibility(VISIBLE);
    }

    public void setItemContent(int color){
        viewBinding.valTextview.setVisibility(View.GONE);
        viewBinding.colorView.setVisibility(VISIBLE);
        viewBinding.colorView.setColorValue(color);
    }

    public void setItemDescription(String string){
        viewBinding.descriptionTextview.setText(string);
    }
}