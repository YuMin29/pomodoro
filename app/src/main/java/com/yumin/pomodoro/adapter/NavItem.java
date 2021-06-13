package com.yumin.pomodoro.adapter;

public class NavItem {
    String mTitle = "";
    String mSubtitle = "";
    int mIcon;
    boolean mShowDivider;
    boolean mShowSubTitle;

    public NavItem(String title, int icon) {
        mTitle = title;
        mIcon = icon;
    }

    public void setShowDivider(boolean show){
        mShowDivider = show;
    }

    public void setShowSubTitle(boolean show){
        mShowSubTitle = show;
    }

    public void setSubtitle(String subtitle){
        mSubtitle = subtitle;
    }
}
