package com.mariizcal.sqltestmanager.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import com.mariizcal.sqltestmanager.model.DBModel;

import java.lang.reflect.Field;


/*
 * Created by mariizcal on 5/7/15.
 */
public class DBConfiguration extends SQLiteOpenHelper implements BaseColumns {

    private static DBConfiguration db;

    private Class[] mClazz;
    private String dbName;
    private Context context;
    private int version;

    private DBConfiguration(Context context, String dbName, int version, Class... clazz) {
        super(context, dbName, null, version);
        this.context = context;
        this.dbName = dbName;
        this.version = version;
        this.mClazz = clazz;
        this.getWritableDatabase().close();
    }

    public static DBConfiguration getInstance() {
        if (db != null)
            return db;
        else
            throw new IllegalStateException("The database is not set up, you need first call" +
                    " DBConfiguration.setUpDataBase() method!");
    }

    public static void setUpDataBase(Context context, String dbName, int version, Class... clazz) {
        if (db == null)
            db = new DBConfiguration(context, dbName, version, clazz);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String[] queries = queriesCreateTables();
        try {
            db.beginTransaction();
            for (String query : queries)
                db.execSQL(query);
            db.setTransactionSuccessful();
        } catch (SQLiteException e) {
            Log.e("SQLiteException", e.getLocalizedMessage());
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    private String[] queriesCreateTables() {
        String[] queriesTables = new String[mClazz.length];

        for (int i = 0; i < mClazz.length; i++) {
            Class clazz = mClazz[i];
            if (DBModel.class.isAssignableFrom(clazz)) {
                String query = "CREATE TABLE " + clazz.getSimpleName() + " ( " + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL";
                Field[] fields = clazz.getDeclaredFields();
                for (Field field : fields) {
                    String type = getSQLiteType(field.getType().getSimpleName());
                    if (!type.equals("none"))
                        query += " , " + field.getName() + " " + type;
                }

                Field[] superFields = clazz.getSuperclass().getDeclaredFields();
                for (Field field : superFields) {
                    String type = getSQLiteType(field.getType().getSimpleName());
                    if (!type.equals("none"))
                        if(!field.getName().equals("_id"))
                        query += " , " + field.getName() + " " + type;
                }

                query += " );";
                queriesTables[i] = query;
            } else
                throw new IllegalStateException(clazz.getName() + " is not extending from DBModel class." +
                        " You can't use it as a table.");
        }

        return queriesTables;
    }

    private String getSQLiteType(String type) {
        switch (type) {
            case "int":
                return "INTEGER";
            case "double":
                return "REAL";
            case "long":
                return "REAL";
            case "boolean":
                return "INTEGER";
            case "String":
                return "TEXT";
            case "short":
                return "REAL";
            case "float":
                return "REAL";
            case "char":
                return "TEXT";
            default:
                return "none";
        }
    }

    public String getDbName() {
        return dbName;
    }

    public Context getContext() {
        return context;
    }

    public int getVersion() {
        return version;
    }
}
