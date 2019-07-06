package com.mansourappdevelopment.androidapp.kidsafe.models;


public class User {
	private String name;
	private String email;
	private String profileImage;
	
	
	public User() {
	
	}
	
	public User(String name, String email) {
		this.name = name;
		this.email = email;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getEmail() {
		return email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getProfileImage() {
		return profileImage;
	}
	
	public void setProfileImage(String profileImage) {
		this.profileImage = profileImage;
	}
	
}
