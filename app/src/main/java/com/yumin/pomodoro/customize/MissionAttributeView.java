package com.yumin.pomodoro.customize;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.EditText;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.library.baseAdapters.BR;

import com.yumin.pomodoro.R;
import com.yumin.pomodoro.data.UserMission;
import com.yumin.pomodoro.databinding.LayoutMissionAttributeBinding;

public class MissionAttributeView extends ConstraintLayout {
    private LayoutMissionAttributeBinding layoutMissionAttributeBinding;

    public MissionAttributeView(Context context) {
        this(context,null);
    }

    public MissionAttributeView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MissionAttributeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context){
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layoutMissionAttributeBinding = DataBindingUtil.inflate(inflater, R.layout.layout_mission_attribute,this,true);
    }

    public void setUserMission(UserMission userMission){
        layoutMissionAttributeBinding.setVariable(BR.userMission,userMission);
    }

    public ItemListView getItemRepeat(){
        return layoutMissionAttributeBinding.itemRepeat;
    }

    public ItemDateView getItemOperate(){
        return layoutMissionAttributeBinding.itemOperate;
    }

    public EditText getMissionTitle(){
        return layoutMissionAttributeBinding.missionTitle;
    }
}
