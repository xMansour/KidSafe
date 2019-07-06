package com.mansourappdevelopment.androidapp.kidsafe.models;

import android.os.Parcel;
import android.os.Parcelable;

//Serializable to send the apps as an intent parameter
public class App implements Parcelable {
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
	private String appName;
	private String packageName;
	//private Drawable appIcon;
	private boolean blocked;
	private ScreenLock screenLock;
	
	public App() {
	}

    /*public App(String appName, String packageName, Drawable appIcon, boolean blocked) {
        this.appName = appName;
        this.packageName = packageName;
        this.appIcon = appIcon;
        this.blocked = blocked;
    }*/
	
	
	public App(String appName, String packageName, boolean blocked) {
		this.appName = appName;
		this.packageName = packageName;
		this.blocked = blocked;
	}
	
	protected App(Parcel in) {
		appName = in.readString();
		packageName = in.readString();
		blocked = in.readByte() != 0;
	}
	
	public String getAppName() {
		return appName;
	}
	
	public void setAppName(String appName) {
		this.appName = appName;
	}
	
	public String getPackageName() {
		return packageName;
	}
	
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
	
	public boolean isBlocked() {
		return blocked;
	}
	
	public void setBlocked(boolean blocked) {
		this.blocked = blocked;
	}
	
	public ScreenLock getScreenLock() {
		return screenLock;
	}
	
	public void setScreenLock(ScreenLock screenLock) {
		this.screenLock = screenLock;
	}
	
	
	@Override
	public int describeContents() {
		return 0;
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(appName);
		dest.writeString(packageName);
		dest.writeByte((byte) (blocked ? 1 : 0));
	}
}
