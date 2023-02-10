package com.ismt.babybuy;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {
    // Database Version
    private static final int DATABASE_VERSION = 1;
    //Database Name
    private static final String DATABASE_NAME = "BabyBuy.db";
    // User table name
    private static final String TABLE_USER = "user";
    //items table name
    private static final String TABLE_ITEMS = "items";
    // User Table Columns names
    private static final String COLUMN_USER_ID = "user_id";
    private static final String COLUMN_USER_NAME = "user_name";
    private static final String COLUMN_USER_EMAIL = "user_email";
    private static final String COLUMN_USER_PASSWORD = "user_password";
    //Items TABLE COLUMN NAMES
    private static final String COLUMN_ITEM_ID = "item_id";
    private static final String COLUMN_ITEM_NAME = "item_name";
    private static final String COLUMN_ITEM_IMAGE = "item_image";
    private static final String COLUMN_ITEM_PRICE = "item_price";
    private static final String COLUMN_ITEM_PURCHASED = "item_purchased";
    private static final String COLUMN_ITEM_DESCRIPTION = "item_description";

    // create USER table sql query
    private final String CREATE_USER_TABLE = "CREATE TABLE " + TABLE_USER + "("
            + COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COLUMN_USER_NAME + " TEXT,"
            + COLUMN_USER_EMAIL + " TEXT," + COLUMN_USER_PASSWORD + " TEXT" + ")";


    //create Items  table sql query
    private final String CREATE_ITEM_TABLE = "CREATE TABLE " + TABLE_ITEMS
            + "(" + COLUMN_ITEM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_ITEM_NAME + " TEXT, "
            + COLUMN_ITEM_IMAGE + " BLOB, "
            + COLUMN_ITEM_PRICE + " FLOAT,"
            + COLUMN_ITEM_PURCHASED + " INTEGER,"
            + COLUMN_ITEM_DESCRIPTION + " TEXT)";
    // drop table sql query
    private final String DROP_USER_TABLE = "DROP TABLE IF EXISTS " + TABLE_USER;

    // drop table ITEM table sql query
    private final String DROP_ITEM_TABLE = "DROP TABLE IF EXISTS " + TABLE_ITEMS;

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_USER_TABLE);
        sqLiteDatabase.execSQL(CREATE_ITEM_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        //Drop User Table if exist
        sqLiteDatabase.execSQL(DROP_USER_TABLE);
        sqLiteDatabase.execSQL(DROP_ITEM_TABLE);

        // Create tables again
        onCreate(sqLiteDatabase);
    }

    public void addUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        //ContentValues: A key/value store that inserts data into a row of a table
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_NAME, user.getName());
        values.put(COLUMN_USER_EMAIL, user.getEmail());
        values.put(COLUMN_USER_PASSWORD, user.getPassword());

        // Inserting Row
        db.insert(TABLE_USER, null, values);
    }

    public Boolean checkUserName(String username) {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("Select * from " + TABLE_USER + " where " + COLUMN_USER_EMAIL + "=?", new String[]{username});
        return cursor.getCount() > 0;
    }

    public Boolean checkUserDetails(String username, String password) {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        String query = "select* from " + TABLE_USER + " where " + COLUMN_USER_EMAIL + "=?" + " AND " + COLUMN_USER_PASSWORD + " = ?";
        Cursor cursor = sqLiteDatabase.rawQuery(query,
                new String[]{username, password});
        return cursor.getCount() > 0;
    }

    public void insertItems(Items items) {
        SQLiteDatabase database = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ITEM_NAME, items.getItemsName());
        values.put(COLUMN_ITEM_IMAGE, items.getImages());
        values.put(COLUMN_ITEM_PRICE, items.getPrice());
        values.put(COLUMN_ITEM_PURCHASED, 0);
        values.put(COLUMN_ITEM_DESCRIPTION, items.getDescription());

        // Inserting Row
        database.insert(TABLE_ITEMS, null, values);

    }

    public Cursor getData(String sql) {
        SQLiteDatabase database = getReadableDatabase();
        return database.rawQuery(sql, null);
    }

    ArrayList<Items> listItems() {
        String sql = "select * from " + TABLE_ITEMS;
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<Items> itemList = new ArrayList<>();
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String name = cursor.getString(1);
                byte[] image = cursor.getBlob(2);
                float price = cursor.getFloat(3);
                int purchased = cursor.getInt(4);
                String description = cursor.getString(5);
                itemList.add(new Items(id, name, image, purchased, price, description));

            }
            while (cursor.moveToNext());
        }
        cursor.close();
        return itemList;
    }

    void updateItems(Items items) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ITEM_NAME, items.getItemsName());
        values.put(COLUMN_ITEM_IMAGE, items.getImages());
        values.put(COLUMN_ITEM_PRICE, items.getPrice());
        values.put(COLUMN_ITEM_DESCRIPTION, items.getDescription());
        db.update(TABLE_ITEMS, values, COLUMN_ITEM_ID + " = ?", new String[]{String.valueOf(items.getId())});
    }

    void markItemAsPurchased(int itemId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ITEM_PURCHASED, 1);
        db.update(TABLE_ITEMS, values, COLUMN_ITEM_ID + " = ?", new String[]{String.valueOf(itemId)});
    }

    void deleteItem(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ITEMS, COLUMN_ITEM_ID + " = ?", new String[]{String.valueOf(id)});
    }

}
