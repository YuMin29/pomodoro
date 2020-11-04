package com.yumin.pomodoro.data.model;

import java.util.ArrayList;
import java.util.List;

public class Category {
    enum Priority{
        LOW,
        MEDIUM,
        HIGH
    }

    private String mCategoryName;
    private List<Mission> mMissionList;
    private Priority mPriority = Priority.MEDIUM;

    public Category(String name){
        mCategoryName = name;
    }

    public void addMission(Mission mission){
        if (mMissionList == null)
            mMissionList = new ArrayList<>();
        mMissionList.add(mission);
    }

    public List<Mission> getMissionList() {
        return mMissionList;
    }

    public String getCategoryName(){
        return mCategoryName;
    }
}
