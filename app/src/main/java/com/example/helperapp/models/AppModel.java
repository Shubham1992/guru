package com.example.helperapp.models;

import android.graphics.drawable.Drawable;

import java.io.Serializable;

public class AppModel implements Serializable {

    private String id;
    private String name;
    private String packageName;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }
}
