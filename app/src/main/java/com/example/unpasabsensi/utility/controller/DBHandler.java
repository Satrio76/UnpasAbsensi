package com.example.unpasabsensi.utility.controller;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.unpasabsensi.absensi.model.UserMhs;

import java.util.ArrayList;
import java.util.List;

public class DBHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "Userinfo";
    private static final String TABLE_USER = "User";

    private static final String KEY_ID = "iduser";
    private static final String KEY_NIM = "nim";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_STATUS = "status";
    private static final String KEY_UPLOAD_DATE = "uploaddate";
    private static final String KEY_NAMA = "nama";

    public DBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_USER + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_NIM + " TEXT,"
                + KEY_PASSWORD + " TEXT,"
                + KEY_STATUS + " TEXT,"
                + KEY_UPLOAD_DATE + " TEXT,"
                + KEY_NAMA + " TEXT"+")";

        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        onCreate(db);
    }

    public void addUser(UserMhs user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_ID, user.getId());
        values.put(KEY_NIM, user.getNim());
        values.put(KEY_PASSWORD, user.getPassword());
        values.put(KEY_STATUS, user.getStatus());
        values.put(KEY_UPLOAD_DATE, user.getUpload_date());
        values.put(KEY_NAMA, user.getNama());
        db.insert(TABLE_USER, null, values);
        db.close();
    }

    public UserMhs getUser(int id) {
        UserMhs contact = null;
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.query(TABLE_USER, new String[]{KEY_ID,
                            KEY_NIM, KEY_PASSWORD, KEY_STATUS, KEY_UPLOAD_DATE, KEY_NAMA }, KEY_ID + "=?",
                    new String[]{String.valueOf(id)}, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                cursor.moveToFirst();
                contact = new UserMhs(Integer.parseInt(
                        cursor.getString(0)),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getString(5));
            }
        }catch (Exception ez) {
            ez.printStackTrace();
        }
        return contact;
    }
    public List<UserMhs> getAllUser() {
        List<UserMhs> userList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_USER;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                UserMhs user = new UserMhs();
                user.setId(Integer.parseInt(cursor.getString(0)));
                user.setNim(cursor.getString(1));
                user.setPassword(cursor.getString(2));
                user.setStatus(cursor.getString(3));
                user.setUpload_date(cursor.getString(4));
                user.setNama(cursor.getString(5));
                userList.add(user);
            } while (cursor.moveToNext());
        }
        return userList;
    }
    public int updateUser(UserMhs user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NIM, user.getNim());
        values.put(KEY_PASSWORD, user.getPassword());
        values.put(KEY_STATUS, user.getStatus());
        values.put(KEY_UPLOAD_DATE,user.getUpload_date());
        values.put(KEY_NAMA,user.getNama());
        return db.update(TABLE_USER, values, KEY_ID + " = ?",
                new String[]{String.valueOf(user.getId())});
    }
    public void deleteAll(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_USER,null,null);
        db.execSQL("delete from "+ TABLE_USER);
        db.close();
    }
}
