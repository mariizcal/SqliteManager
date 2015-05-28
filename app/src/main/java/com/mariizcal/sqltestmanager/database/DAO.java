package com.mariizcal.sqltestmanager.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
    private String query;

    private boolean isQueryInitialized = false;
    private boolean isNestedWhereClause = false;

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
        try {
            open();
            mDb.beginTransaction();
            result = mDb.insert(clazz.getSimpleName(), null, toContentValues(object, true));
            mDb.setTransactionSuccessful();
        } catch (SQLiteException e) {
            Log.e("SQLiteException", e.getLocalizedMessage());
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mDb.endTransaction();
            close();
        }
        return result;
    }

    @Override
    public long update(Object object, int id) {
        long result = -1;
        try {
            open();
            mDb.beginTransaction();
            result = mDb.update(clazz.getSimpleName(), toContentValues(object, false), "_id = " + id, null);
            mDb.setTransactionSuccessful();
        } catch (SQLiteException e) {
            Log.e("SQLiteException", e.getLocalizedMessage());
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mDb.endTransaction();
            close();
        }
        return result;
    }

    @Override
    public long delete(Object object, int id) {
        long result = -1;
        try {
            open();
            mDb.beginTransaction();
            result = mDb.delete(clazz.getSimpleName(), "_id = " + id, null);
            mDb.setTransactionSuccessful();
        } catch (SQLiteException e) {
            Log.e("SQLiteException", e.getLocalizedMessage());
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mDb.endTransaction();
            close();
        }
        return result;
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
                for (Field field : fields) {
                    String getMethod = getters(field);
                    if (!getMethod.equals("none")) {
                        Method method = clazz.getMethod(getMethod);
                        cv.put(field.getName(), String.valueOf(method.invoke(object)));
                    }
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

    public DAO getAll() {
        query = "SELECT * FROM " + clazz.getSimpleName() + " ";
        isQueryInitialized = true;
        return this;
    }

    public DAO where(String column, String condition) {
        if (isQueryInitialized) {

            if (isNestedWhereClause)
                query += " AND ";
            else
                query += " WHERE ";

            query += column + " = " + "'" + condition + "'";
            isNestedWhereClause = true;
            return this;
        } else
            throw new IllegalStateException("You have to initialize the query calling the getAll() method!," +
                    " you can't call where method without initialized the query ");
    }

    public List<Object> find() {
        if (isQueryInitialized) {
            ArrayList<Object> items = null;
            query += " ;";
            open();
            mDb.beginTransaction();
            try {
                Cursor cursor = mDb.rawQuery(query, null);
                if (cursor != null) {
                    items = new ArrayList<>();
                    while (cursor.moveToNext()) {
                        Object item = clazz.newInstance();

                        Field [] superFields = clazz.getSuperclass().getDeclaredFields();
                        for (Field field : superFields) {
                            Method method = superSetters(field);
                            if(method != null){
                                method.invoke(item, getObject(field, cursor));
                            }
                        }

                        Field[] fields = clazz.getDeclaredFields();
                        for (Field field : fields) {
                            Method method = setters(field);
                            if(method != null){
                                method.invoke(item, getObject(field, cursor));
                            }
                        }
                        items.add(item);
                    }
                }
                mDb.setTransactionSuccessful();
                restartQuery();
            } catch (SQLiteException e) {
                Log.e("SQLiteException", e.getLocalizedMessage());
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                mDb.endTransaction();
                close();
            }
            return items;
        } else
            throw new IllegalStateException("You have to initialize the query calling the getAll() method!," +
                    " you can't call where method without initialized the query ");
    }

    private void restartQuery() {
        query = null;
        isQueryInitialized = false;
        isNestedWhereClause = false;
    }

    private String getters(Field field) {
        if (isValidSQLiteType(field.getType().getSimpleName())) {
            String methodType = field.getType().getSimpleName().equals("boolean") ? "is" : "get";
            return methodType + field.getName().substring(0, 1).toUpperCase()
                    + field.getName().substring(1);
        } else
            return "none";
    }

    private Method setters(Field field) throws NoSuchMethodException {
        if (isValidSQLiteType(field.getType().getSimpleName())) {
            String methodName = "set" + field.getName().substring(0, 1).toUpperCase()
                    + field.getName().substring(1);
            return clazz.getMethod(methodName, field.getType());
        } else
            return null;
    }

    private Method superSetters(Field field) throws  NoSuchMethodException {
        if (isValidSQLiteType(field.getType().getSimpleName())) {
            String methodName = "set" + field.getName().substring(0, 1).toUpperCase()
                    + field.getName().substring(1);
            return clazz.getSuperclass().getMethod(methodName, field.getType());
        } else
            return null;
    }

    private Object getObject(Field field, Cursor cursor){
        switch (field.getType().getSimpleName()){
            case "int":
                return cursor.getInt(cursor.getColumnIndex(field.getName()));
            case "double":
                return cursor.getDouble(cursor.getColumnIndex(field.getName()));
            case "long":
                return cursor.getLong(cursor.getColumnIndex(field.getName()));
            case "boolean":
                return cursor.getInt(cursor.getColumnIndex(field.getName())) == 1 ? true : false;
            case "String":
                return cursor.getString(cursor.getColumnIndex(field.getName()));
            case "short":
                return cursor.getShort(cursor.getColumnIndex(field.getName()));
            case "float":
                return cursor.getFloat(cursor.getColumnIndex(field.getName()));
            case "char":
                return cursor.getString(cursor.getColumnIndex(field.getName()));
            default:
                return null;
        }
    }

    private boolean isValidSQLiteType(String type) {
        switch (type) {
            case "int":
                return true;
            case "double":
                return true;
            case "long":
                return true;
            case "boolean":
                return true;
            case "String":
                return true;
            case "short":
                return true;
            case "float":
                return true;
            case "char":
                return true;
            default:
                return false;
        }
    }

    private String getCurrentDateWithTime() {
        Calendar cal = Calendar.getInstance();
        Date currentLocalTime = cal.getTime();
        DateFormat date = new SimpleDateFormat("MM/dd/yyyy HH:mm");
        date.setTimeZone(TimeZone.getTimeZone(date.getTimeZone().getID()));
        return date.format(currentLocalTime);
    }
}
