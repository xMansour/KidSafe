package com.mansourappdevelopment.androidapp.kidsafe.models;

public class Location {
	private double latitude;
	private double longitude;
	private double fenceDiameter;
	private double fenceCenterLatitude;
	private double fenceCenterLongitude;
	private boolean outOfFence;
	private boolean geoFence;
	
	public Location(double latitude, double longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
	}
	
	public Location() {
	}
	
	public Location(double latitude, double longitude, double fenceDiameter, double fenceCenterLatitude, double fenceCenterLongitude, boolean outOfFence, boolean geoFence) {
		this.latitude = latitude;
		this.longitude = longitude;
		this.fenceDiameter = fenceDiameter;
		this.fenceCenterLatitude = fenceCenterLatitude;
		this.fenceCenterLongitude = fenceCenterLongitude;
		this.outOfFence = outOfFence;
		this.geoFence = geoFence;
	}
	
	public double getLatitude() {
		return latitude;
	}
	
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	
	public double getLongitude() {
		return longitude;
	}
	
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	
	public double getFenceDiameter() {
		return fenceDiameter;
	}
	
	public void setFenceDiameter(double fenceDiameter) {
		this.fenceDiameter = fenceDiameter;
	}
	
	public double getFenceCenterLatitude() {
		return fenceCenterLatitude;
	}
	
	public void setFenceCenterLatitude(double fenceCenterLatitude) {
		this.fenceCenterLatitude = fenceCenterLatitude;
	}
	
	public double getFenceCenterLongitude() {
		return fenceCenterLongitude;
	}
	
	public void setFenceCenterLongitude(double fenceCenterLongitude) {
		this.fenceCenterLongitude = fenceCenterLongitude;
	}
	
	public boolean isOutOfFence() {
		return outOfFence;
	}
	
	public void setOutOfFence(boolean outOfFence) {
		this.outOfFence = outOfFence;
	}
	
	public boolean isGeoFence() {
		return geoFence;
	}
	
	public void setGeoFence(boolean geoFence) {
		this.geoFence = geoFence;
	}
}
