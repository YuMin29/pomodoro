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
import android.widget.Switch;


import androidx.lifecycle.MutableLiveData;

import com.yumin.pomodoro.R;
import com.yumin.pomodoro.data.model.AdjustMissionItem;
import com.yumin.pomodoro.data.model.Mission;
import com.yumin.pomodoro.ui.main.adapter.RecyclerViewBaseAdapter;
import com.yumin.pomodoro.ui.view.MissionItemView;
import com.yumin.pomodoro.utils.LogUtil;

import java.util.List;

public class AddMissionEventHandler {
    private static final String TAG = "[AddMissionEventHandler]";
    private TextWatcher textWatcher;
    private AddMissionViewModel mAddMissionViewModel;
    public MissionItemView.MissionItemListener missionItemListener;
    private RecyclerViewBaseAdapter.OnItemClickListener mOnItemClickListener;
    private View.OnClickListener saveButtonListener;
    private View.OnClickListener cancelButtonListener;
    private Context context;
    private List<AdjustMissionItem> mAdjustMissionItems;

    public AddMissionEventHandler(AddMissionViewModel addMissionViewModel, Context context, List<AdjustMissionItem> adjustMissionItems) {
        this.context = context;
        this.mAdjustMissionItems = adjustMissionItems;
        mAddMissionViewModel = addMissionViewModel;
        this.textWatcher = getTextWatcherInstance();
        missionItemListener = new MissionItemView.MissionItemListener() {
            @Override
            public void onAddButtonClick(View view, int position) {
                LogUtil.logD(TAG,"[onAddButtonClick] position = "+position);
                AdjustMissionItem adjustMissionItem = mAdjustMissionItems.get(position);
                String val = adjustMissionItem.getContent();
                switch (adjustMissionItem.getAdjustItem()) {
                    case TIME:
                        int time = Integer.valueOf(val);
                        LogUtil.logD(TAG,"ADD time = "+time);
                        time++;
                        mAddMissionViewModel.setTime(time);
                        break;
                    case LONG_BREAK:
                        int longBreakTime = Integer.valueOf(val);
                        LogUtil.logD(TAG,"ADD longBreakTime = "+longBreakTime);
                        mAddMissionViewModel.setLongBreak(longBreakTime++);
                        break;
                    case SHORT_BREAK:
                        int shortBreakTime = Integer.valueOf(val);
                        mAddMissionViewModel.setShortBreak(shortBreakTime++);
                        break;
                    case GOAL:
                        int goal = Integer.valueOf(val);
                        mAddMissionViewModel.setGoal(goal++);
                        break;
                    case REPEAT:
                        int repeat = Integer.valueOf(val);
                        mAddMissionViewModel.setRepeat(repeat++);
                        break;
                    case OPERATE_DAY:
                        break;
                    case COLOR:
                        mAddMissionViewModel.setColor(Mission.Color.valueOf(val));
                        break;
                    case SOUND:
                        mAddMissionViewModel.setEnableSound(Boolean.valueOf(val));
                        break;
                    case VOLUME:
                        mAddMissionViewModel.setVolume(Mission.Volume.valueOf(val));
                        break;
                    case VIBRATE:
                        mAddMissionViewModel.setEnableVibrate(Boolean.valueOf(val));
                        break;
                    case NOTIFICATION:
                        mAddMissionViewModel.setEnableNotification(Boolean.valueOf(val));
                        break;
                    case SCREEN_ON:
                        mAddMissionViewModel.setKeepScreenOn(Boolean.valueOf(val));
                        break;
                }
            }

            @Override
            public void onMinusButtonClock(View view, int position) {
                LogUtil.logD(TAG,"[onMinusButtonClock] position = "+position);
            }
        };

        mOnItemClickListener = new RecyclerViewBaseAdapter.OnItemClickListener() {
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
//                    editText.setText(mAddMissionViewModel.getCountViewList().get(position).getContent());
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
                            .setTitle("設置"+adjustMissionItems.get(position).getDesc())
                            .setView(view1)
                            .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
//                                    mAddMissionViewModel.setCountItem(position,editNum[0]);
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

        saveButtonListener = v -> {
            LogUtil.logD(TAG,"[saveButtonListener][onClick]");
            mAddMissionViewModel.saveMission();
        };

        cancelButtonListener = v -> LogUtil.logD(TAG,"[cancelButtonListener][onClick]");
    }

    public View.OnClickListener getSaveButtonListener(){
        return this.saveButtonListener;
    }

    public View.OnClickListener getCancelButtonListener(){
        return this.cancelButtonListener;
    }

    public RecyclerViewBaseAdapter.OnItemClickListener getOnItemClickListener(){
        return this.mOnItemClickListener;
    }

    public MissionItemView.MissionItemListener getMissionItemListener(){
        return missionItemListener;
    }

    public void onSaveButtonClick(){
        LogUtil.logD(TAG,"[onSaveButtonClick]");
    }

    public void onCancelButtonClick(){
        LogUtil.logD(TAG,"[onCancelButtonClick]");
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
                mAddMissionViewModel.setMissionName(s.toString());
            }
        };
    }

    public TextWatcher getTextWatcher() {
        return textWatcher;
    }

    public void setAdjustMissionItems(List<AdjustMissionItem> items){
        this.mAdjustMissionItems = items;
    }
}
