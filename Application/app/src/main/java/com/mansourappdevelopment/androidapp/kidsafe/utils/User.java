package com.mansourappdevelopment.androidapp.kidsafe.utils;

public class User {
    private String name;
    private String email;
    private String password;
    private String parentEmail;
    private boolean isChild;

    public User(String name, String email, String password, String parentEmail, boolean isChild) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.parentEmail = parentEmail;
        this.isChild = isChild;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setParentEmail(String parentEmail) {
        this.parentEmail = parentEmail;
    }

    public void setChild(boolean child) {
        isChild = child;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getParentEmail() {
        return parentEmail;
    }

    public boolean isChild() {
        return isChild;
    }
}
