package com.yumin.pomodoro.ui.home;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.widget.AppCompatButton;
import androidx.databinding.BindingAdapter;

import com.yumin.pomodoro.R;
import com.yumin.pomodoro.utils.BaseBindingAdapter;
import com.yumin.pomodoro.utils.CountView;
import com.yumin.pomodoro.utils.CountViewItem;
import com.yumin.pomodoro.utils.LogUtil;

public class AddMissionEventHandler {
    private static final String TAG = "[AddMissionEventHandler]";
    private TextWatcher textWatcher;
    private AddMissionViewModel mAddMissionViewModel;
    public CountView.CountViewListener countViewListener;
    private BaseBindingAdapter.OnItemClickListener mOnItemClickListener;

    public AddMissionEventHandler(AddMissionViewModel addMissionViewModel) {
        mAddMissionViewModel = addMissionViewModel;
        this.textWatcher = getTextWatcherIns();
        countViewListener = new CountView.CountViewListener() {
            @Override
            public void onAddButtonClick(View view, int position) {
                LogUtil.logD(TAG,"[onAddButtonClick] position = "+position);
                mAddMissionViewModel.setMissionTime(1000);
            }

            @Override
            public void onMinusButtonClock(View view, int position) {
                LogUtil.logD(TAG,"[onMinusButtonClock] position = "+position);
            }
        };

        mOnItemClickListener = new BaseBindingAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                LogUtil.logD(TAG,"[onItemClick] position = "+position);
            }

            @Override
            public void onItemLongClick(View view, int position) {
                LogUtil.logD(TAG,"[onItemLongClick] position = "+position);
            }
        };
    }

    public BaseBindingAdapter.OnItemClickListener getOnItemClickListener(){
        return this.mOnItemClickListener;
    }

    public CountView.CountViewListener getCountViewListener(){
        return countViewListener;
    }

    private TextWatcher getTextWatcherIns() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //do some thing
                LogUtil.logD(TAG, "[beforeTextChanged] S = " + s);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //do some thing
                LogUtil.logD(TAG, "[onTextChanged] S = " + s);
            }

            @Override
            public void afterTextChanged(Editable s) {
                //do some thing
                LogUtil.logD(TAG, "[afterTextChanged] S = " + s.toString());
                mAddMissionViewModel.setMissionTitle(s.toString());
            }
        };
    }

    public TextWatcher getTextWatcher() {
        return textWatcher;
    }

    public void setTextWatcher(TextWatcher textWatcher) {
        this.textWatcher = textWatcher;
    }

    @BindingAdapter("textChangedListener")
    public static void bindTextWatcher(EditText editText, TextWatcher textWatcher) {
        editText.addTextChangedListener(textWatcher);
    }
}
