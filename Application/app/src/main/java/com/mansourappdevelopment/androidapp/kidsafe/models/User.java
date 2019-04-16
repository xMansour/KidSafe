package com.mansourappdevelopment.androidapp.kidsafe.models;

import java.util.ArrayList;
import java.util.HashMap;

public class User {
    private String name;
    private String email;
    private String password;
    private String parentEmail;
    private boolean child;
    private ArrayList<App> apps = new ArrayList<>();
    private Location location;
    private HashMap<String, Message> messages = new HashMap<>();
    private HashMap<String, Call> calls = new HashMap<>();
    private boolean webFilter;


    //TODO:: add user image field
    public User() {

    }

    public User(String name, String email, String password, String parentEmail, boolean child) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.parentEmail = parentEmail;
        this.child = child;
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
        this.child = child;
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
        return child;
    }

    public ArrayList<App> getApps() {
        return apps;
    }

    public Location getLocation() {
        return location;
    }

    public void setApps(ArrayList<App> apps) {
        this.apps = apps;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public HashMap<String, Message> getMessages() {
        return messages;
    }

    public void setMessages(HashMap<String, Message> messages) {
        this.messages = messages;
    }

    public HashMap<String, Call> getCalls() {
        return calls;
    }

    public void setCalls(HashMap<String, Call> calls) {
        this.calls = calls;
    }

    public boolean isWebFilter() {
        return webFilter;
    }

    public void setWebFilter(boolean webFilter) {
        this.webFilter = webFilter;
    }
}
