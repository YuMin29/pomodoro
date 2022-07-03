package com.yumin.pomodoro.ui.main;

public interface MainActivityNavigator {
    public boolean isNetworkConnected();
    public void showToast(String message);
    public void navigateToLogin();
    public void showLogoutDialog();
    public void refresh();
}
