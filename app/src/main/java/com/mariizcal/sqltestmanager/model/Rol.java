package com.mariizcal.sqltestmanager.model;

import java.util.ArrayList;

/**
 * Created by mariizcal on 5/24/15.
 */
public class Rol extends DBModel {
    private int type;
    private boolean enabled;
    private String name;
    private ArrayList<Usuario> usuarios;

    public Rol() {
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Usuario> getUsuarios() {
        return usuarios;
    }

    public void setUsuarios(ArrayList<Usuario> usuarios) {
        this.usuarios = usuarios;
    }
}
