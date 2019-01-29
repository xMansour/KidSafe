package com.mansourappdevelopment.androidapp.kidsafe.utils;


public class AppTestClass {
    private String appName;
    private boolean blocked;

    public AppTestClass(String appName, boolean blocked) {
        this.appName = appName;
        this.blocked = blocked;
    }


    public String getAppName() {
        return appName;
    }


    public void setAppName(String appName) {
        this.appName = appName;
    }


    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }

    public boolean isBlocked() {
        return blocked;
    }
}
