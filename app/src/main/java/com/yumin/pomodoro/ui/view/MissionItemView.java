package com.yumin.pomodoro.ui.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.InputType;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;


import androidx.annotation.Nullable;
import androidx.databinding.BindingAdapter;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.InverseBindingAdapter;

import com.yumin.pomodoro.R;
import com.yumin.pomodoro.databinding.MissionItemNumViewBinding;
import com.yumin.pomodoro.utils.LogUtil;

public class MissionItemView extends LinearLayout {
    private static final String TAG = "[MissionItemView]";
    private MissionItemNumViewBinding viewBinding;

    public MissionItemView(Context context) {
        super(context);
        inflateView(context);
    }

    public MissionItemView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        inflateView(context);
    }

    public MissionItemView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflateView(context);
    }

    private void inflateView(Context context) {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        viewBinding = DataBindingUtil.inflate(inflater,R.layout.mission_item_num_view,this,true);
        viewBinding.addNum.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtil.logD(TAG,"[addNum][onClick]");
                int content = Integer.parseInt(viewBinding.numTextview.getText().toString());
                content++;
                viewBinding.numTextview.setText(String.valueOf(content));
            }
        });
        viewBinding.minusNum.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtil.logD(TAG,"[minusNum][onClick]");
                LogUtil.logD(TAG,"[addNum][onClick]");
                int content = Integer.parseInt(viewBinding.numTextview.getText().toString());
                if (content > 0)
                    content--;
                viewBinding.numTextview.setText(String.valueOf(content));
            }
        });
        viewBinding.itemLinearlayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"[linear layout][onClick]");
                //create dialog to set number
                View view = LayoutInflater.from(context).inflate(R.layout.dialog_count,null);
                EditText editText = view.findViewById(R.id.editText);
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                editText.setText(viewBinding.numTextview.getText());

                AlertDialog.Builder builder = new AlertDialog.Builder(context)
                        .setTitle("請設置"+viewBinding.descriptionTextview.getText())
                        .setView(view)
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                LogUtil.logD(TAG,"[AlerDialog][setText] : "+editText.getText());
                                viewBinding.numTextview.setText(editText.getText());
                            }
                        })
                        .setNegativeButton("cancel",null);
                builder.show();
            }
        });
    }


    public void setItemContent(int content1){
        viewBinding.numTextview.setText(String.valueOf(content1));
    }

    public void setItemDescription(String string){
        viewBinding.descriptionTextview.setText(string);
    }

    public int getItemContent(){
        return Integer.valueOf(viewBinding.numTextview.getText().toString());
    }
}
