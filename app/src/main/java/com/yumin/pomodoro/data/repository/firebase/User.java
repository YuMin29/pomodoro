package com.yumin.pomodoro.data.repository.firebase;

import com.yumin.pomodoro.utils.LogUtil;

public class User {
    private static final String TAG = "[User]";
    private String userName;
    private String userMail;

    public User() {
    }

    public User(String userName, String userMail) {
        LogUtil.logD(TAG,"New User => userName = "+userName+", userMail = "+userMail);
        this.userName = userName;
        this.userMail = userMail;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserMail() {
        return userMail;
    }

    public void setUserMail(String userMail) {
        this.userMail = userMail;
    }
}
