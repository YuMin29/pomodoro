package com.yumin.pomodoro.utils;

import com.yumin.pomodoro.data.UserMission;

import java.util.Comparator;

public class SortTimeUtil implements Comparator {
    @Override
    public int compare(Object o1, Object o2) {
        UserMission item1 = (UserMission)o1;
        UserMission item2 = (UserMission) o2;

        int flag = String.valueOf(item1.getCreatedTime())
                .compareTo(String.valueOf(item2.getCreatedTime()));
        return flag;
    }
}
