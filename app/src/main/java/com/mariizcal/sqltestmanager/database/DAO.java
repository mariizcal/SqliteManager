package com.mariizcal.sqltestmanager.database;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import java.io.InvalidClassException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by mariizcal on 5/24/15.
 */
public abstract class DAO implements IDBAccess<Object> {

    private Class clazz;
    private SQLiteDatabase mDb;

    protected DAO(Class clazz) {
        this.clazz = clazz;
    }

    private void open() {
        mDb = DBConfiguration.getInstance().getWritableDatabase();
    }

    private void close() {
        if (mDb != null)
            mDb.close();
    }

    @Override
    public long insert(Object object) {
        long result = -1;
        open();
        result = mDb.insert(clazz.getSimpleName(), null, toContentValues(object, true));
        close();
        return result;
    }

    @Override
    public long update(Object object, int id) {
        long result = -1;
        open();
        result = mDb.update(clazz.getSimpleName(),toContentValues(object, false), "_id = " + id, null);
        close();
        return result;
    }

    @Override
    public long delete(Object object, int id) {
        return 0;
    }

    @Override
    public List<Object> read() {
        return null;
    }

    @Override
    public Object find(int id) {
        return null;
    }

    @Override
    public ContentValues toContentValues(Object object, boolean isCreated) {
        if (clazz == object.getClass()) {
            ContentValues cv = null;
            try {
                cv = new ContentValues();
                Field[] fields = clazz.getDeclaredFields();
                for (Field field :fields) {
                    String methodType = field.getType().toString().equals("boolean") ? "is" : "get";
                    String getMethod = methodType + field.getName().substring(0, 1).toUpperCase()
                            + field.getName().substring(1);
                    Method method = clazz.getMethod(getMethod);
                    cv.put(field.getName(), String.valueOf(method.invoke(object)));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (isCreated)
                cv.put("createdAt", getCurrentDateWithTime());
            else
                cv.put("updatedAt", getCurrentDateWithTime());
            return cv;
        } else
            throw new IllegalStateException("Checks that have defined the class of the corresponding" +
                    " model in the constructor");
    }

    private String getCurrentDateWithTime() {
        Calendar cal = Calendar.getInstance();
        Date currentLocalTime = cal.getTime();
        DateFormat date = new SimpleDateFormat("MM/dd/yyyy HH:mm");
        date.setTimeZone(TimeZone.getTimeZone(date.getTimeZone().getID()));
        return date.format(currentLocalTime);
    }
}
