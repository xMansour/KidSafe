package com.mansourappdevelopment.androidapp.kidsafe.models;

public class ScreenLock {
    private int hours;
    private int minutes;
    private boolean locked;

    public ScreenLock() {
    }

    public ScreenLock(int hours, int minutes, boolean locked) {
        this.hours = hours;
        this.minutes = minutes;
        this.locked = locked;
    }

    public int getMinutes() {
        return minutes;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }

    public int getHours() {
        return hours;
    }

    public void setHours(int hours) {
        this.hours = hours;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }
}
