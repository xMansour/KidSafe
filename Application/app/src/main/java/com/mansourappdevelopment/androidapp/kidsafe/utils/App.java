package com.mansourappdevelopment.androidapp.kidsafe.utils;

import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;

//Serializable to send the apps as an intent parameter
public class App implements Parcelable {
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


    protected App(Parcel in) {
        appName = in.readString();
        blocked = in.readByte() != 0;
    }

    public static final Creator<App> CREATOR = new Creator<App>() {
        @Override
        public App createFromParcel(Parcel in) {
            return new App(in);
        }

        @Override
        public App[] newArray(int size) {
            return new App[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(appName);
        dest.writeByte((byte) (blocked ? 1 : 0));
    }
}
