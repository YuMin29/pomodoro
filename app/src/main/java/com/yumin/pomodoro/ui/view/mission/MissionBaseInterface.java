package com.yumin.pomodoro.ui.view.mission;

import com.yumin.pomodoro.ui.view.ItemDateView;
import com.yumin.pomodoro.ui.view.ItemListView;

public interface MissionBaseInterface extends ItemListView.OnRepeatTypeListener, ItemDateView.OnOperateDayChanged {
    @Override
    void onOperateChanged(long time);

    @Override
    void chooseRepeatDefine();
}
