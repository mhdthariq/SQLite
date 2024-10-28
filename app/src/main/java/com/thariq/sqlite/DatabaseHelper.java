package com.thariq.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "student.db";
    private static final int DATABASE_VERSION = 2; // Incremented version to trigger onUpgrade
    private static final String TABLE_USERS = "users";
    private static final String TABLE_STUDENTS = "students";
    private static DatabaseHelper instance;

    // Singleton pattern to prevent multiple instances
    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_USERS + " (id INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT, password TEXT)");
        // Include _id column for Cursor compatibility
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_STUDENTS + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, course TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STUDENTS);
        onCreate(db);
    }

    // Insert user
    public boolean insertUser(String username, String password) {
        ContentValues values = new ContentValues();
        values.put("username", username);
        values.put("password", password);
        return insert(TABLE_USERS, values);
    }

    // Check login credentials
    public boolean checkLogin(String username, String password) {
        try (Cursor cursor = getReadableDatabase().rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE username=? AND password=?", new String[]{username, password})) {
            return cursor.getCount() > 0;
        }
    }

    // CRUD for Students
    public boolean insertStudent(String name, String course) {
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("course", course);
        return insert(TABLE_STUDENTS, values);
    }

    public Cursor getAllStudents() {
        return getReadableDatabase().rawQuery("SELECT * FROM " + TABLE_STUDENTS, null);
    }

    public boolean updateStudent(int id, String name, String course) {
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("course", course);
        return update(TABLE_STUDENTS, values, "_id=?", new String[]{String.valueOf(id)});
    }

    public Integer deleteStudent(int id) {
        return delete(TABLE_STUDENTS, "_id=?", new String[]{String.valueOf(id)});
    }

    // Helper methods for CRUD operations
    private boolean insert(String table, ContentValues values) {
        long result = getWritableDatabase().insert(table, null, values);
        return result != -1;
    }

    private boolean update(String table, ContentValues values, String whereClause, String[] whereArgs) {
        int rowsAffected = getWritableDatabase().update(table, values, whereClause, whereArgs);
        return rowsAffected > 0;
    }

    private Integer delete(String table, String whereClause, String[] whereArgs) {
        return getWritableDatabase().delete(table, whereClause, whereArgs);
    }
}