package com.yumin.pomodoro.ui.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.InputType;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;


import androidx.annotation.Nullable;
import androidx.databinding.BindingAdapter;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.InverseBindingListener;
import androidx.databinding.InverseBindingMethod;
import androidx.databinding.InverseBindingMethods;

import com.yumin.pomodoro.R;
import com.yumin.pomodoro.utils.LogUtil;
import com.yumin.pomodoro.databinding.ItemTextviewBinding;

@InverseBindingMethods({@InverseBindingMethod(type = ItemTextView.class,
        attribute = "itemContent", event = "itemContentAttrChanged")})
public class ItemTextView extends LinearLayout {
    private static final String TAG = "[ItemTextView]";
    private ItemTextviewBinding viewBinding;
    private int content;
    private InverseBindingListener inverseBindingListener;

    public ItemTextView(Context context) {
        super(context);
        inflateView(context);
    }

    public ItemTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        inflateView(context);
    }

    public ItemTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflateView(context);
    }

    private void inflateView(Context context) {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inverseBindingListener = new InverseBindingListener() {
            @Override
            public void onChange() {

            }
        };
        viewBinding = DataBindingUtil.inflate(inflater,R.layout.item_textview,this,true);
        viewBinding.addNum.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtil.logD(TAG,"[addNum][onClick]");
                int content = Integer.parseInt(viewBinding.numTextview.getText().toString());
                content++;
                setItemContent(content);
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
                setItemContent(content);
            }
        });
        viewBinding.itemLinearlayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"[itemLinearlayout][onClick]");
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
                                setItemContent(Integer.valueOf(editText.getText().toString()));
                            }
                        })
                        .setNegativeButton("cancel",null);
                builder.show();
            }
        });
    }


    public void setItemContent(int content){
        LogUtil.logD(TAG,
                "[setItemContent] content = "+content);
        viewBinding.numTextview.setText(String.valueOf(content));
        if (inverseBindingListener != null)
            inverseBindingListener.onChange();
        this.content = content;
    }

    public void setItemDescription(String string){
        LogUtil.logD(TAG,
                "[setItemDescription] string = "+string);
        viewBinding.descriptionTextview.setText(string);
    }

    public int getItemContent(){
        LogUtil.logD(TAG,"[getItemContent] RETURN = "+viewBinding.numTextview.getText().toString());
        if (TextUtils.isEmpty(viewBinding.numTextview.getText().toString()))
            return 0;
        return Integer.parseInt(viewBinding.numTextview.getText().toString());
    }

    public void setItemContentAttrChanged(InverseBindingListener inverseBindingListener){
        this.inverseBindingListener = inverseBindingListener;
    }
}
