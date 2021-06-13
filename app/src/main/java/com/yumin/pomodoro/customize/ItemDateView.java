package com.yumin.pomodoro.customize;

import android.app.DatePickerDialog;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.InverseBindingListener;
import androidx.databinding.InverseBindingMethod;
import androidx.databinding.InverseBindingMethods;

import com.yumin.pomodoro.BR;
import com.yumin.pomodoro.R;
import com.yumin.pomodoro.databinding.ItemListviewBinding;
import com.yumin.pomodoro.utils.LogUtil;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@InverseBindingMethods(
        {@InverseBindingMethod(type = ItemDateView.class, attribute = "itemDateVal", event = "itemDateValAttrChanged")})
public class ItemDateView extends LinearLayout {
    private static final String TAG = ItemDateView.class.getSimpleName();
    private ItemListviewBinding mViewBinding;
    private InverseBindingListener mInverseBindingListener;
    private long mOperateDay;

    public ItemDateView(Context context) {
        super(context);
        inflateView(context);
    }

    public ItemDateView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        inflateView(context);
    }

    public ItemDateView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflateView(context);
    }

    private void inflateView(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mViewBinding = DataBindingUtil.inflate(inflater, R.layout.item_listview, this, true);
        mViewBinding.itemLinearLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                DatePickerDialog datePickerDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        Calendar chooseDate = Calendar.getInstance();
                        chooseDate.set(year, month, dayOfMonth);
                        Date chooseDateTime = chooseDate.getTime();
                        if (operateDayListener != null ){
                            operateDayListener.onOperateChanged(chooseDateTime.getTime());
                        }
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

                datePickerDialog.show();
            }
        });
    }

    public void setItemDateVal(long val) {
        mOperateDay = val;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
        Date date = new Date(val);
        LogUtil.logD(TAG,"[setItemDateVal] val = "+simpleDateFormat.format(date));
        int compareResult = date.compareTo(Calendar.getInstance().getTime());

        if (compareResult == 0)
            mViewBinding.valTextview.setText("TODAY");
        else
            mViewBinding.valTextview.setTextSize(26);

        mViewBinding.valTextview.setText(simpleDateFormat.format(date));

        if (mInverseBindingListener != null)
            mInverseBindingListener.onChange();
    }

    public interface OperateDayChanged {
        void onOperateChanged(long time);
    }

    public void updateUI(long val){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
        Date date = new Date(val);
        LogUtil.logD(TAG,"[updateUI] val = "+simpleDateFormat.format(date));
        setItemDateVal(val);
    }

    public long getItemDateVal() {
        return mOperateDay;
    }

    public void setItemDescription(String string) {
        mViewBinding.setVariable(BR.itemDescription,string);
    }

    public void setItemDateValAttrChanged(InverseBindingListener inverseBindingListener) {
        mInverseBindingListener = inverseBindingListener;
    }

    private OperateDayChanged operateDayListener = null;
    public void setOperateDayListener(OperateDayChanged listener) {
        if (operateDayListener == null)
            operateDayListener = listener;
    }
}