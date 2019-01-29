package com.mansourappdevelopment.androidapp.kidsafe.utils;

import android.graphics.drawable.Drawable;

public class App {
    private String appName;
    private Drawable appIcon;

    public App(String appName, Drawable appIcon) {
        this.appName = appName;
        this.appIcon = appIcon;
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
}
