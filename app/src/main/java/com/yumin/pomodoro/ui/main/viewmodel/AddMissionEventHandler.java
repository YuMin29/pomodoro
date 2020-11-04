package com.yumin.pomodoro.ui.main.viewmodel;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;


import com.yumin.pomodoro.R;
import com.yumin.pomodoro.ui.main.adapter.BaseAdapter;
import com.yumin.pomodoro.ui.view.MissionItemView;
import com.yumin.pomodoro.utils.LogUtil;

public class AddMissionEventHandler {
    private static final String TAG = "[AddMissionEventHandler]";
    private TextWatcher textWatcher;
    private AddMissionViewModel mAddMissionViewModel;
    public MissionItemView.MissionItemListener missionItemListener;
    private BaseAdapter.OnItemClickListener mOnItemClickListener;
    private Context context;

    public AddMissionEventHandler(AddMissionViewModel addMissionViewModel, Context context) {
        this.context = context;
        mAddMissionViewModel = addMissionViewModel;
        this.textWatcher = getTextWatcherInstance();
        missionItemListener = new MissionItemView.MissionItemListener() {
            @Override
            public void onAddButtonClick(View view, int position) {
                LogUtil.logD(TAG,"[onAddButtonClick] position = "+position);
                mAddMissionViewModel.setMissionTime(1000);
                mAddMissionViewModel.addCountItem(position);
            }

            @Override
            public void onMinusButtonClock(View view, int position) {
                LogUtil.logD(TAG,"[onMinusButtonClock] position = "+position);
                mAddMissionViewModel.minusCountItem(position);
            }
        };

        mOnItemClickListener = new BaseAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                LogUtil.logD(TAG,"[onItemClick] position = "+position);

                if (position < 7) {
                    // new a dialog to set count here
                    final String[] editNum = {""};
                    LayoutInflater layoutInflater = LayoutInflater.from(context);
                    View view1 = layoutInflater.inflate(R.layout.dialog_count,null);
                    final EditText editText = view1.findViewById(R.id.editText);
                    editText.setInputType(EditorInfo.TYPE_CLASS_NUMBER);
                    editText.setText(mAddMissionViewModel.getCountViewList().get(position).getContent());
                    editText.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {

                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            editNum[0] = s.toString();
                        }
                    });
                    AlertDialog alertDialog = new AlertDialog.Builder(context)
                            .setTitle("設置"+mAddMissionViewModel.countViewItemList.getValue().get(position).getDesc())
                            .setView(view1)
                            .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    mAddMissionViewModel.setCountItem(position,editNum[0]);
                                }
                            })
                            .setNegativeButton("cancel", null).show();
                    alertDialog.show();
                } else {

                }
            }

            @Override
            public void onItemLongClick(View view, int position) {
                LogUtil.logD(TAG,"[onItemLongClick] position = "+position);
            }
        };
    }

    public BaseAdapter.OnItemClickListener getOnItemClickListener(){
        return this.mOnItemClickListener;
    }

    public MissionItemView.MissionItemListener getMissionItemListener(){
        return missionItemListener;
    }

    private TextWatcher getTextWatcherInstance() {
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

}
