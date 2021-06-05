package com.yumin.pomodoro.ui.view;

import android.content.Context;
import android.content.DialogInterface;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.InverseBindingListener;
import androidx.databinding.InverseBindingMethod;
import androidx.databinding.InverseBindingMethods;

import com.yumin.pomodoro.BR;
import com.yumin.pomodoro.R;
import com.yumin.pomodoro.databinding.ItemListviewBinding;

@InverseBindingMethods(
        {@InverseBindingMethod(type = ItemListView.class, attribute = "itemListVal", event = "itemListValAttrChanged")})
public class ItemListView extends LinearLayout {
    private ItemListviewBinding mViewBinding;
    private InverseBindingListener mInverseBindingListener;
    private OnRepeatTypeListener mOnRepeatTypeListener = null;
    private static final int REPEAT_NONE = 0;
    private static final int REPEAT_EVERYDAY = 1;
    private static final int REPEAT_DEFINE = 2;

    public ItemListView(Context context) {
        super(context);
        inflateView(context);
    }

    public ItemListView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        inflateView(context);
    }

    public ItemListView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflateView(context);
    }

    private void inflateView(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mViewBinding = DataBindingUtil.inflate(inflater, R.layout.item_listview, this, true);
        mViewBinding.itemLinearLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mViewBinding.descriptionTextview.getText().toString().equals(getResources().getString(R.string.mission_repeat))) {
                    String[] repeatArray = getResources().getStringArray(R.array.repeat_array);
                    String title = context.getString(R.string.dialog_set_title) + mViewBinding.descriptionTextview.getText();
                    AlertDialog.Builder builder = new AlertDialog.Builder(context)
                            .setTitle(title)
                            .setItems(repeatArray, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int index) {
                                    setItemListVal(index);
                                    switch (index) {
                                        case REPEAT_NONE:
                                        case REPEAT_EVERYDAY:
                                            if (mOnRepeatTypeListener != null)
                                                mOnRepeatTypeListener.chooseRepeatNonDefine();
                                            break;
                                        case REPEAT_DEFINE:
                                            if (mOnRepeatTypeListener != null)
                                                mOnRepeatTypeListener.chooseRepeatDefine();
                                            break;
                                    }
                                }
                            });
                    builder.show();
                }
            }
        });
    }

    public void setItemListVal(int val) {
        if (val == -1)
            return;
        String[] repeatArray = getResources().getStringArray(R.array.repeat_array);
        mViewBinding.valTextview.setText(repeatArray[val]);
        if (mInverseBindingListener != null)
            mInverseBindingListener.onChange();
    }

    public int getItemListVal() {
        String text = mViewBinding.valTextview.getText().toString();
        if (text != null) {
            String[] repeatArray = getResources().getStringArray(R.array.repeat_array);
            for (int index = 0; index < repeatArray.length; index++) {
                if (repeatArray[index].equals(mViewBinding.valTextview.getText().toString()))
                    return index;
            }
        }
        return -1;
    }

    public void setItemDescription(String string) {
        mViewBinding.setVariable(BR.itemDescription,string);
    }

    public void setItemListValAttrChanged(InverseBindingListener inverseBindingListener) {
        mInverseBindingListener = inverseBindingListener;
    }

    public interface OnRepeatTypeListener {
        void chooseRepeatDefine();
        void chooseRepeatNonDefine();
    }

    public void setOnRepeatTypeListener(OnRepeatTypeListener listener){
        mOnRepeatTypeListener = listener;
    }
}