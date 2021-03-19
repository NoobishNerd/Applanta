package com.example.applanta;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class User implements Serializable {
    private int id;
    private String name;
    private int exp;
    private boolean admin;
    private int friendshipCount;

        public User(int id, String name, int exp, Boolean admin, int friendshipCount) {
            this.id = id;
            this.name = name;
            this.exp = exp;
            this.admin = admin;
            this.friendshipCount = friendshipCount;
        }

    public User(JSONObject userObj) throws JSONException {
        this.id = userObj.getInt("id");
        this.name = userObj.getString("name");
        this.exp = userObj.getInt("exp");
        this.admin = userObj.getBoolean("admin");
        this.friendshipCount = userObj.getInt("friendshipCount");
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getExp() {
        return exp;
    }

    public void setExp(int exp) {
        this.exp = exp;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public int getFriendshipCount() {
        return friendshipCount;
    }

    public void setFriendshipCount(int friendshipCount) {
        this.friendshipCount = friendshipCount;
    }
}
