package com.example.helperapp.models;

import java.io.Serializable;

public class AppModel implements Serializable {

    private String id;
    private String name;
    private String packageName;
    private String icon;
    private String description;

    public AppModel(String name, String packageName, String icon, String description) {
        this.name = name;
        this.packageName = packageName;
        this.icon = icon;
        this.description = description;
    }

    public AppModel() {

    }

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

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
