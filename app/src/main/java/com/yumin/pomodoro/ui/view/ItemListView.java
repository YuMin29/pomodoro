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
    private static final String TAG = "[ItemListView]";
    private ItemListviewBinding viewBinding;
    private InverseBindingListener inverseBindingListener;
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
        viewBinding = DataBindingUtil.inflate(inflater, R.layout.item_listview, this, true);
        viewBinding.itemLinearLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewBinding.descriptionTextview.getText().toString().equals(getResources().getString(R.string.mission_repeat))) {
                    String[] repeatArray = getResources().getStringArray(R.array.repeat_array);
                    AlertDialog.Builder builder = new AlertDialog.Builder(context)
                            .setTitle("請設置" + viewBinding.descriptionTextview.getText())
                            .setItems(repeatArray, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int index) {
                                    setItemListVal(index);
                                    switch (index) {
                                        case REPEAT_NONE:
                                        case REPEAT_EVERYDAY:
                                            // clear repeat start & end day if exists.
                                            if (onRepeatTypeListener != null)
                                                onRepeatTypeListener.chooseRepeatNonDefine();
                                            break;
                                        case REPEAT_DEFINE:
                                            // set repeat start & end day
                                            if (onRepeatTypeListener != null)
                                                onRepeatTypeListener.chooseRepeatDefine();
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
        // update ui
        String[] repeatArray = getResources().getStringArray(R.array.repeat_array);
        viewBinding.valTextview.setText(repeatArray[val]);
        // notify listener
        if (inverseBindingListener != null)
            inverseBindingListener.onChange();
    }

    public int getItemListVal() {
        String text = viewBinding.valTextview.getText().toString();
        if (text != null) {
            String[] repeatArray = getResources().getStringArray(R.array.repeat_array);
            for (int index = 0; index < repeatArray.length; index++) {
                if (repeatArray[index].equals(viewBinding.valTextview.getText().toString()))
                    return index;
            }
        }
        return -1;
    }

    public void setItemDescription(String string) {
        viewBinding.setVariable(BR.itemDescription,string);
    }

    public void setItemListValAttrChanged(InverseBindingListener inverseBindingListener) {
        this.inverseBindingListener = inverseBindingListener;
    }

    public interface OnRepeatTypeListener {
        public void chooseRepeatDefine();
        public void chooseRepeatNonDefine();
    }

    private OnRepeatTypeListener onRepeatTypeListener = null;

    public void setOnRepeatTypeListener(OnRepeatTypeListener listener){
        this.onRepeatTypeListener = listener;
    }
}