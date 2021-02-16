package com.yumin.pomodoro.data.model;

import com.yumin.pomodoro.data.repository.firebase.UserMission;

import java.util.ArrayList;
import java.util.List;

public class Category {
    public enum Index{
        TODAY,
        COMING
    }

    private String mCategoryName;
    private List<Mission> mMissionList = new ArrayList<>();
    private Index mIndex;

    public Category(String name, Index index){
        mCategoryName = name;
        mIndex = index;
    }

    public void addMission(Mission mission){
        mMissionList.add(mission);
    }

    public void addAllMission(List<UserMission> list){
        mMissionList.addAll(list);
    }

    public List<Mission> getMissionList() {
        return mMissionList;
    }

    public String getCategoryName(){
        return mCategoryName;
    }

    public Index getIndex(){
        return this.mIndex;
    }
}
