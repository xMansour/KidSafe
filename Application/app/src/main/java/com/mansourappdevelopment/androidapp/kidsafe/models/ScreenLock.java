package com.mansourappdevelopment.androidapp.kidsafe.models;

public class ScreenLock {
	private int hours;
	private int minutes;
	private int timeInMinutes;
	private int timeInSeconds;
	private boolean locked;
	
	public ScreenLock() {
	}
	
	public ScreenLock(int hours, int minutes, boolean locked) {
		this.hours = hours;
		this.minutes = minutes;
		this.locked = locked;
		this.timeInMinutes = hours * 60 + minutes;
		this.timeInSeconds = hours * 60 * 60 + minutes * 60;
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
	
	public int getTimeInMinutes() {
		return timeInMinutes;
	}
	
	public void setTimeInMinutes(int timeInMinutes) {
		this.timeInMinutes = timeInMinutes;
	}
	
	public int getTimeInSeconds() {
		return timeInSeconds;
	}
	
	public void setTimeInSeconds(int timeInSeconds) {
		this.timeInSeconds = timeInSeconds;
	}
}
