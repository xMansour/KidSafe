package com.mansourappdevelopment.androidapp.kidsafe.utils;

import android.graphics.drawable.Drawable;

public class App {
    private String appName;
    private Drawable appIcon;
    private boolean blocked;

    public App() {
    }

    public App(String appName, boolean blocked) {
        this.appName = appName;
        this.blocked = blocked;
    }

    public App(String appName, Drawable appIcon, boolean blocked) {
        this.appName = appName;
        this.appIcon = appIcon;
        this.blocked = blocked;
    }


    public String getAppName() {
        return appName;
    }

    public Drawable getAppIcon() {
        return appIcon;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public void setAppIcon(Drawable appIcon) {
        this.appIcon = appIcon;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }

    public boolean isBlocked() {
        return blocked;
    }
}
