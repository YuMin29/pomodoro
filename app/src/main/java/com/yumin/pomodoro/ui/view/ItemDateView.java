package com.yumin.pomodoro.ui.view;

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

import com.yumin.pomodoro.R;
import com.yumin.pomodoro.databinding.ItemListviewBinding;
import com.yumin.pomodoro.utils.LogUtil;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@InverseBindingMethods(
        {@InverseBindingMethod(type = ItemDateView.class, attribute = "itemDateVal", event = "itemDateValAttrChanged")})
public class ItemDateView extends LinearLayout {
    private static final String TAG = "[ItemListView]";
    private ItemListviewBinding viewBinding;
    private InverseBindingListener inverseBindingListener;
    private long operateDay;

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
        viewBinding = DataBindingUtil.inflate(inflater, R.layout.item_listview, this, true);
        viewBinding.itemLinearLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // show calendar
                Calendar calendar = Calendar.getInstance();
                DatePickerDialog datePickerDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        // TODO: 2020/12/29 需要新增即時獲得自訂重複區間的日期
                        //  在選擇執行日時 判斷重複區間是否小於執行日
                        //  小於->顯示對話框 消除已選擇的自訂區間->消除
                        //                                  ->返回
                        LogUtil.logD(TAG, "[datePickerDialog][onDateSet]");
                        Calendar chooseDate = Calendar.getInstance();
                        chooseDate.set(year, month, dayOfMonth);
                        Date chooseDateTime = chooseDate.getTime();
                        if (operateDayListener != null ){
                            // TODO: 2020/12/30 需要確認 點選狀態 交給add/edit mission fragment 定奪
                            operateDayListener.onOperateChanged(chooseDateTime.getTime());
                        }
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

                datePickerDialog.show();
            }
        });
    }

    public void setItemDateVal(long val) {
        // long convert to string
        operateDay = val;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
        Date date = new Date(val);
        LogUtil.logD(TAG,"[setItemDateLiveData] val = "+simpleDateFormat.format(date));
        int compareResult = date.compareTo(Calendar.getInstance().getTime());

        if (compareResult == 0) {
            // equal
            viewBinding.valTextview.setText("TODAY");
        } else {
            viewBinding.valTextview.setTextSize(26);
        }
        viewBinding.valTextview.setText(simpleDateFormat.format(date));

        if (inverseBindingListener != null)
            inverseBindingListener.onChange();
    }

    public interface OnOperateDayChanged{
        public void onOperateChanged(long time);
    }

    public void updateUI(long val){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
        Date date = new Date(val);
        LogUtil.logD(TAG,"[updateUI] val = "+simpleDateFormat.format(date));
        setItemDateVal(val);
    }

    public long getItemDateVal() {
        return this.operateDay;
    }

    public void setItemDescription(String string) {
        viewBinding.descriptionTextview.setText(string);
    }

    public void setItemDateValAttrChanged(InverseBindingListener inverseBindingListener) {
        this.inverseBindingListener = inverseBindingListener;
    }

    private OnOperateDayChanged operateDayListener = null;
    public void setOperateDayListener(OnOperateDayChanged listener) {
        if (operateDayListener == null)
            operateDayListener = listener;
    }
}