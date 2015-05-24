package com.mariizcal.sqltestmanager.model;

/**
 * Created by mariizcal on 5/7/15.
 */
public class Usuario extends DBModel{
    private String name;
    private String lastName;
    private int age;
    private float height;
    private boolean enable;

    public Usuario() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }
}
