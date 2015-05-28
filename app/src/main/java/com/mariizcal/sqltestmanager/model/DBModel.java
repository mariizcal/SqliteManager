package com.mariizcal.sqltestmanager.model;

/**
 * Created by mariizcal on 5/23/15.
 */
public class DBModel {
    protected long _id;
    protected String createdAt;
    protected String updatedAt;

    public long get_id() {
        return _id;
    }

    public void set_id(long _id) {
        this._id = _id;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}
