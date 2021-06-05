package com.yumin.pomodoro.data.repository.firebase;

import com.yumin.pomodoro.utils.LogUtil;

public class User {
    private static final String TAG = "[User]";
    private String mUserName;
    private String mUserMail;

    public User(String userName, String userMail) {
        LogUtil.logD(TAG,"New User => userName = "+userName+", userMail = "+userMail);
        mUserName = userName == null ? "null" : userName;
        mUserMail = userMail == null ? "null" : userMail;
    }

    public String getUserName() {
        return mUserName;
    }

    public void setUserName(String userName) {
        mUserName = userName;
    }

    public String getUserMail() {
        return mUserMail;
    }

    public void setUserMail(String userMail) {
        mUserMail = userMail;
    }
}
