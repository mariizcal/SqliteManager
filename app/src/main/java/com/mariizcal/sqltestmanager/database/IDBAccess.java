package com.mariizcal.sqltestmanager.database;

import android.content.ContentValues;

import java.util.List;

/**
 * Created by mariizcal on 5/21/15.
 */
public interface IDBAccess<T> {
    public long insert(T object);
    public long update(T object, int id);
    public long delete(T object, int id);
    public List<T> read();
    public T find(int id);
    public ContentValues toContentValues(T object, boolean isCreated);
}
