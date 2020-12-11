package com.yumin.pomodoro.ui.view;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
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
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        viewBinding = DataBindingUtil.inflate(inflater, R.layout.item_listview,this,true);
        viewBinding.itemLinearLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewBinding.descriptionTextview.getText().toString().equals(getResources().getString(R.string.mission_operate_day))) {
                    // show calendar
                    Calendar calendar = Calendar.getInstance();
                    DatePickerDialog datePickerDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            LogUtil.logD(TAG, "[datePickerDialog][onDateSet]");
                            Calendar chooseDate = Calendar.getInstance();
                            chooseDate.set(year, month, dayOfMonth);
                            Date chooseDateTime = chooseDate.getTime();
                            int compareResult = chooseDateTime.compareTo(Calendar.getInstance().getTime());
                            if (compareResult == 0) {
                                // equal
                                setItemListVal("TODAY");
                            } else {
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
                                String chooseStr = simpleDateFormat.format(chooseDateTime);
                                viewBinding.valTextview.setTextSize(26);
                                setItemListVal(chooseStr);
                            }
                        }
                    }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

                    datePickerDialog.show();
                } else if (viewBinding.descriptionTextview.getText().toString().equals(getResources().getString(R.string.mission_repeat))) {
                    //create dialog to set value
                    String[] repeatArray = getResources().getStringArray(R.array.repeat_array);

                    AlertDialog.Builder builder = new AlertDialog.Builder(context)
                            .setTitle("請設置" + viewBinding.descriptionTextview.getText())
                            .setItems(repeatArray, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int index) {
                                    setItemListVal(repeatArray[index]);
                                    switch (index){
                                        case REPEAT_DEFINE:
                                            // show a choose dialog
                                            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context)
                                                    .setTitle("choose time")
                                                    .setView(R.layout.dialog_calendar_view)
                                                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {

                                                        }
                                                    })
                                                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {

                                                        }
                                                    });
                                            dialogBuilder.show();
                                            break;
                                    }
                                }
                            });
                    builder.show();
                }
        }});
    }

    public void setItemListVal(String val){
        final String oldString = viewBinding.valTextview.getText().toString();
        if (val == null || val == oldString)
            return;
        // update ui
        viewBinding.valTextview.setText(val);
        // notify listener
        if (inverseBindingListener != null)
            inverseBindingListener.onChange();
    }

    public String getItemListVal(){
        return viewBinding.valTextview.getText().toString();
    }

    public void setItemDescription(String string){
        viewBinding.descriptionTextview.setText(string);
    }

    public void setItemListValAttrChanged(InverseBindingListener inverseBindingListener){
        this.inverseBindingListener = inverseBindingListener;
    }
}