package com.yumin.pomodoro.ui.view.Navigation;

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
        this.mShowDivider = show;
    }

    public void setShowSubTitle(boolean show){
        this.mShowSubTitle = show;
    }

    public void setSubtitle(String subtitle){
        this.mSubtitle = subtitle;
    }
}
