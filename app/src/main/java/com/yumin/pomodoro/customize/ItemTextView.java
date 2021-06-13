package com.yumin.pomodoro.customize;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.InputType;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;


import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.InverseBindingListener;
import androidx.databinding.InverseBindingMethod;
import androidx.databinding.InverseBindingMethods;
import androidx.databinding.library.baseAdapters.BR;

import com.yumin.pomodoro.R;
import com.yumin.pomodoro.utils.LogUtil;
import com.yumin.pomodoro.databinding.ItemTextviewBinding;

@InverseBindingMethods({@InverseBindingMethod(type = ItemTextView.class,
        attribute = "itemValue", event = "itemValueAttrChanged")})
public class ItemTextView extends LinearLayout {
    private static final String TAG = ItemTextView.class.getSimpleName();
    private ItemTextviewBinding mViewBinding;
    private int mValue;
    private InverseBindingListener mInverseBindingListener;

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
        mViewBinding = DataBindingUtil.inflate(inflater,R.layout.item_textview,this,true);
        mViewBinding.addNum.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                int val = Integer.parseInt(mViewBinding.numTextview.getText().toString());
                val ++;
                setItemValue(val);
            }
        });
        mViewBinding.minusNum.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                int val = Integer.parseInt(mViewBinding.numTextview.getText().toString());
                if (val > 1)
                    val --;
                setItemValue(val);
            }
        });
        mViewBinding.itemLinearlayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //create dialog to set number
                View view = LayoutInflater.from(context).inflate(R.layout.dialog_count,null);
                EditText editText = view.findViewById(R.id.editText);
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                editText.setText(mViewBinding.numTextview.getText());

                String title = context.getString(R.string.dialog_set_title) + mViewBinding.descriptionTextview.getText();
                AlertDialog.Builder builder = new AlertDialog.Builder(context)
                        .setTitle(title)
                        .setView(view)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                LogUtil.logD(TAG,"[AlertDialog][setText] : "+editText.getText());
                                setItemValue(Integer.valueOf(editText.getText().toString()));
                            }
                        })
                        .setNegativeButton(R.string.cancel,null);
                builder.show();
            }
        });
    }


    public void setItemValue(int val){
        LogUtil.logD(TAG, "[setItemContent] val = "+val);
        mViewBinding.numTextview.setText(String.valueOf(val));
        if (mInverseBindingListener != null)
            mInverseBindingListener.onChange();
        mValue = val;
    }

    public void setItemDescription(String string){
        mViewBinding.setVariable(BR.itemDescription,string);
    }

    public int getItemValue(){
        LogUtil.logD(TAG,"[getItemContent] return = "+ mViewBinding.numTextview.getText().toString());
        if (TextUtils.isEmpty(mViewBinding.numTextview.getText().toString()))
            return 0;
        return Integer.parseInt(mViewBinding.numTextview.getText().toString());
    }

    public void setItemValueAttrChanged(InverseBindingListener inverseBindingListener){
        mInverseBindingListener = inverseBindingListener;
    }
}
