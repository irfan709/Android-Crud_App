package com.example.usersapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class DbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "users_db";
    private static final int DATABASE_VERSION = 1;

    public DbHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE users(id INTEGER PRIMARY KEY AUTOINCREMENT, email TEXT, username TEXT, password TEXT, image BLOB)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS users");
        onCreate(sqLiteDatabase);
    }
    public boolean registerUserHelper(String email, String username, String pass, byte[] imageData) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("email", email);
        contentValues.put("username", username);
        contentValues.put("password", pass);
        contentValues.put("image", imageData);
        long l =sqLiteDatabase.insert("users", null, contentValues);
        sqLiteDatabase.close();
        if (l > 0) {
            return true;
        }
        else {
            return false;
        }
    }
    public boolean checkUserExists(String email, String username) {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM users WHERE email='"+email+"' AND username='"+username+"'", null);
        if (cursor.moveToFirst()) {
            return true;
        }
        else {
            return false;
        }
    }
    boolean loggedin;
    public boolean loginUserHelper(String email, String pass) {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM users WHERE email='" + email + "' AND password='" + pass + "'", null);
        if (cursor.moveToFirst()) {
            loggedin = true;
        } else {
            loggedin = false;
        }
        cursor.close(); // Close the cursor to release resources
        sqLiteDatabase.close();
        return loggedin;
    }

    public ArrayList<Users> getLoggedInUserDetails(String email) {
        ArrayList<Users> al = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM users WHERE email='" + email + "'", null);
        if (cursor.moveToFirst()) {
            do {
                String email1 = cursor.getString(1);
                String uname = cursor.getString(2);
                String pass = cursor.getString(3);
                byte[] imageData = cursor.getBlob(4);
                Users user = new Users();
                Users.setEmail(email1);
                Users.setName(uname);
                Users.setPassword(pass);
                Users.setImage(imageData);
                al.add(user);
            } while (cursor.moveToNext());
        }
        cursor.close();
        sqLiteDatabase.close();
        return al;
    }


    public boolean updateProfileHelper(String email, String uname, String pass, byte[] image) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("username", uname);
        contentValues.put("password", pass);
        contentValues.put("image", image);
        int i = sqLiteDatabase.update("users", contentValues, "email=?", new String[] {email});
        if (i > 0) {
            return true;
        }
        else {
            return false;
        }
    }
    public boolean deleteUserHelper(String email) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        int i =sqLiteDatabase.delete("users", "email=?", new String[] {email});
        if (i > 0) {
            return true;
        }
        else {
            return false;
        }
    }
}
