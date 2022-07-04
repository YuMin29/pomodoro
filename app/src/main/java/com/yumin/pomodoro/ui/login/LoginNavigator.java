package com.yumin.pomodoro.ui.login;

import android.app.AlertDialog;

public interface LoginNavigator {
    public void navigateToHome();
    public AlertDialog createProgressBarDialog();
    public void showToast(String message);
}
