package com.example.moranav.hungry.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.List;

/**
 * this class manage the restaurant SQL-DB
 */

public class RestaurantSql {

    public static final String RESTAURANT_TABLE = "restaurants";
    public static final String RESTAURANT_ID = "id";
    public static final String RESTAURANT_NAME="name";
    public static final String RESTAURANT_DESCRIPTION="description";
    public static final String RESTAURANT_PHONE="phone";
    public static final String RESTAURANT_ADDRESS="address";
    public static final String RESTAURANT_OWNER_ID = "ownerID";
    public static final String RESTAURANT_LOGO_URL = "logoURL";
    public static final String RESTAURANT_OPEN_HOURS = "openHour";
    public static final String RESTAURANT_IS_REMOVED = "isRemoved";
    public static final String RESTAURANT_IMAGE_URL = "imageURL";
    public static final String RESTAURANT_LAST_UPDATE_DATE = "lastUpdateDate";


    static List<Restaurant> getAllRestaurantsByOwnerID(SQLiteDatabase db,String ownerID)
    {
        String args[] = { ownerID} ;
        Cursor cursor = db.query(RESTAURANT_TABLE, null, RESTAURANT_OWNER_ID + "=?",args , null, null, null);
        return Utils.getListOfRestarurantFromCursor(cursor);

    }


    static List<Restaurant> getAllRestaurants(SQLiteDatabase db) {
        Cursor cursor = db.query(RESTAURANT_TABLE, null, null, null, null, null, null);
        return Utils.getListOfRestarurantFromCursor(cursor);
    }

    static void addRestaurant(SQLiteDatabase db, Restaurant res) {
        ContentValues values = new ContentValues();
        values.put(RestaurantSql.RESTAURANT_ID, res.id);
        values.put(RestaurantSql.RESTAURANT_NAME, res.name);
        values.put(RestaurantSql.RESTAURANT_DESCRIPTION,res.description);
        values.put(RestaurantSql.RESTAURANT_LOGO_URL,res.logoURL);
        values.put(RestaurantSql.RESTAURANT_OPEN_HOURS,res.openHour);
        values.put(RestaurantSql.RESTAURANT_OWNER_ID,res.ownerID);
        values.put(RestaurantSql.RESTAURANT_PHONE,res.phone);
        values.put(RestaurantSql.RESTAURANT_ADDRESS, res.address);
        values.put(RestaurantSql.RESTAURANT_IS_REMOVED,res.isRemoved);
        values.put(RestaurantSql.RESTAURANT_LAST_UPDATE_DATE, res.lastUpdateDate);
        values.put(RestaurantSql.RESTAURANT_IMAGE_URL, res.imageURL);
        db.insert(RESTAURANT_TABLE, RESTAURANT_ID, values);
    }

    static Restaurant getRestaurant(SQLiteDatabase db, String resId) {

        if(resId!=null) {
            Cursor cursor = db.query(RESTAURANT_TABLE, null, RESTAURANT_ID + "=?", new String[]{resId}, null, null, null);
            if (cursor.getCount() != 0) {


                cursor.moveToFirst();

                int idIndex = cursor.getColumnIndex(RESTAURANT_ID);
                int nameIndex = cursor.getColumnIndex(RESTAURANT_NAME);
                int descriptionIndex =cursor.getColumnIndex(RESTAURANT_DESCRIPTION);
                int phoneIndex =cursor.getColumnIndex(RESTAURANT_PHONE);
                int addressIndex =cursor.getColumnIndex(RESTAURANT_ADDRESS);
                int OwnerIDIndex = cursor.getColumnIndex(RESTAURANT_OWNER_ID);
                int logoURLIndex = cursor.getColumnIndex(RESTAURANT_LOGO_URL);
                int OpenHoursIndex = cursor.getColumnIndex(RESTAURANT_OPEN_HOURS);
                int isRemovedIndex = cursor.getColumnIndex(RESTAURANT_IS_REMOVED);
                int imageUrlIndex = cursor.getColumnIndex(RESTAURANT_IMAGE_URL);
                int lastUpdateIndex = cursor.getColumnIndex(RESTAURANT_LAST_UPDATE_DATE);

                Restaurant res = new Restaurant();
                res.id = cursor.getString(idIndex);
                res.name = cursor.getString(nameIndex);
                res.description = cursor.getString(descriptionIndex);
                res.phone = cursor.getString(phoneIndex );
                res.address=cursor.getString(addressIndex);
                res.ownerID=cursor.getString(OwnerIDIndex);
                res.logoURL=cursor.getString(logoURLIndex);
                res.openHour=cursor.getString(OpenHoursIndex);
                res.isRemoved=cursor.getInt(isRemovedIndex);
                res.imageURL = cursor.getString(imageUrlIndex);
                res.lastUpdateDate = cursor.getDouble(lastUpdateIndex);
                return res;
            }

        }
        return null;

    }

    static public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + RESTAURANT_TABLE +
                " (" +
                RESTAURANT_ID + " TEXT PRIMARY KEY, " +
                RESTAURANT_NAME + " TEXT, " +
                RESTAURANT_DESCRIPTION + " TEXT, " +
                RESTAURANT_PHONE + " TEXT, " +
                RESTAURANT_ADDRESS + " TEXT, " +
                RESTAURANT_OWNER_ID + " TEXT, " +
                RESTAURANT_LOGO_URL + " TEXT, " +
                RESTAURANT_OPEN_HOURS + " TEXT, " +
                RESTAURANT_IS_REMOVED + " NUMBER, " +
                RESTAURANT_IMAGE_URL + " TEXT, " +
                RESTAURANT_LAST_UPDATE_DATE + " DOUBLE); ");
    }

    static public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table " + RESTAURANT_TABLE + ";");
        onCreate(db);
    }
    static public void deleteRestaurant(SQLiteDatabase db, Restaurant res)
    {
        db.delete(RESTAURANT_TABLE,RESTAURANT_ID+"=?",new String[]{res.id});
    }

    static public void updateRestaurant(SQLiteDatabase db, Restaurant res)
    {
        ContentValues values = new ContentValues();
        values.put(RestaurantSql.RESTAURANT_ID, res.id);
        values.put(RestaurantSql.RESTAURANT_NAME, res.name);
        values.put(RestaurantSql.RESTAURANT_DESCRIPTION,res.description);
        values.put(RestaurantSql.RESTAURANT_LOGO_URL,res.logoURL);
        values.put(RestaurantSql.RESTAURANT_OPEN_HOURS,res.openHour);
        values.put(RestaurantSql.RESTAURANT_OWNER_ID,res.ownerID);
        values.put(RestaurantSql.RESTAURANT_PHONE,res.phone);
        values.put(RestaurantSql.RESTAURANT_ADDRESS, res.address);
        values.put(RestaurantSql.RESTAURANT_IS_REMOVED,res.isRemoved);
        values.put(RestaurantSql.RESTAURANT_LAST_UPDATE_DATE, res.lastUpdateDate);
        values.put(RestaurantSql.RESTAURANT_IMAGE_URL, res.imageURL);
        db.update(RESTAURANT_TABLE, values, RESTAURANT_ID+"=?", new String[]{res.id});

    }
}



