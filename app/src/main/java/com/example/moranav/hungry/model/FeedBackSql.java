package com.example.moranav.hungry.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.List;


/**
 * this class manage the feedback SQL-DB
 */

public class FeedBackSql {

    public static final String FEEDBACK_TABLE = "feedbacks";
    public static final String FEEDBACK_ID = "id";
    public static final String FEEDBACK_FEEDBACK="feedback";
    public static final String FEEDBACK_GRADE="grade";
    public static final String FEEDBACK_OWNER_ID = "ownerID";
    public static final String FEEDBACK_OWNER_NAME = "ownerName";
    public static final String FEEDBACK_RESID = "resID";
    public static final String FEEDBACK_RESNANE = "resName";
    public static final String FEEDBACK_IS_REMOVED = "isRemoved";
    public static final String FEEDBACK_LAST_UPDATE_DATE = "lastUpdateDate";


    static List<FeedBack> getAllFeedBacksByResID(SQLiteDatabase db,String resID)
    {
        String args[] = { resID} ;
        Cursor cursor = db.query(FEEDBACK_TABLE, null, FEEDBACK_RESID + "=?",args , null, null, null);
        return Utils.getListOfFeedBackFromCursor(cursor);

    }

    static List<FeedBack> getAllFeedBacksByOwnerID(SQLiteDatabase db,String ownerID)
    {
        String args[] = { ownerID} ;
        Cursor cursor = db.query(FEEDBACK_TABLE, null, FEEDBACK_OWNER_ID + "=?",args , null, null, null);
        return Utils.getListOfFeedBackFromCursor(cursor);

    }


    static List<FeedBack> getAllFeedBacks(SQLiteDatabase db) {
        Cursor cursor = db.query(FEEDBACK_TABLE, null, null, null, null, null, null);
        return Utils.getListOfFeedBackFromCursor(cursor);
    }

    static void addFeedBack(SQLiteDatabase db, FeedBack feed) {
        ContentValues values = new ContentValues();
        values.put(FeedBackSql.FEEDBACK_ID, feed.id);
        values.put(FeedBackSql.FEEDBACK_RESNANE, feed.resName);
        values.put(FeedBackSql.FEEDBACK_FEEDBACK,feed.feedback);
        values.put(FeedBackSql.FEEDBACK_GRADE,feed.grade);
        values.put(FeedBackSql.FEEDBACK_RESID,feed.resID);
        values.put(FeedBackSql.FEEDBACK_OWNER_ID,feed.ownerID);
        values.put(FeedBackSql.FEEDBACK_OWNER_NAME,feed.ownerName);
        values.put(FeedBackSql.FEEDBACK_IS_REMOVED,feed.isRemoved);
        values.put(FeedBackSql.FEEDBACK_LAST_UPDATE_DATE, feed.lastUpdateDate);
        db.insert(FEEDBACK_TABLE, FEEDBACK_ID, values);
    }

    static FeedBack getFeedBack(SQLiteDatabase db, String feedId) {

        if(feedId!=null) {
            Cursor cursor = db.query(FEEDBACK_TABLE, null, FEEDBACK_ID + "=?", new String[]{feedId}, null, null, null);
            if (cursor.getCount() != 0) {


                cursor.moveToFirst();

                int idIndex = cursor.getColumnIndex(FEEDBACK_ID);
                int ResnameIndex = cursor.getColumnIndex(FEEDBACK_RESNANE);
                int feedbackIndex =cursor.getColumnIndex(FEEDBACK_FEEDBACK);
                int gradeIndex =cursor.getColumnIndex(FEEDBACK_GRADE);
                int resIDIndex =cursor.getColumnIndex(FEEDBACK_RESID);
                int OwnerIDIndex = cursor.getColumnIndex(FEEDBACK_OWNER_ID);
                int OwnerNameIndex = cursor.getColumnIndex(FEEDBACK_OWNER_NAME);
                int isRemovedIndex = cursor.getColumnIndex(FEEDBACK_IS_REMOVED);
                int lastUpdateIndex = cursor.getColumnIndex(FEEDBACK_LAST_UPDATE_DATE);

                FeedBack feed = new FeedBack();
                feed.id = cursor.getString(idIndex);
                feed.resName = cursor.getString(ResnameIndex);
                feed.feedback = cursor.getString(feedbackIndex);
                feed.grade = cursor.getInt(gradeIndex );
                feed.resID=cursor.getString(resIDIndex);
                feed.ownerID=cursor.getString(OwnerIDIndex);
                feed.ownerName=cursor.getString(OwnerNameIndex);
                feed.isRemoved=cursor.getInt(isRemovedIndex);
                feed.lastUpdateDate = cursor.getDouble(lastUpdateIndex);
                return feed;
            }

        }
        return null;

    }

    static public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + FEEDBACK_TABLE +
                " (" +
                FEEDBACK_ID + " TEXT PRIMARY KEY, " +
                FEEDBACK_RESNANE + " TEXT, " +
                FEEDBACK_FEEDBACK + " TEXT, " +
                FEEDBACK_GRADE + " NUMBER, " +
                FEEDBACK_RESID + " TEXT, " +
                FEEDBACK_OWNER_ID + " TEXT, " +
                FEEDBACK_OWNER_NAME + " TEXT, " +
                FEEDBACK_IS_REMOVED + " NUMBER, " +
                FEEDBACK_LAST_UPDATE_DATE + " DOUBLE); ");
    }

    static public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop " + FEEDBACK_TABLE + ";");
        onCreate(db);
    }
    static public void deleteFeedBack(SQLiteDatabase db, FeedBack res)
    {
        db.delete(FEEDBACK_TABLE,FEEDBACK_ID+"=?",new String[]{res.id});
    }

    static public void updateFeedBack(SQLiteDatabase db, FeedBack feed)
    {
        ContentValues values = new ContentValues();
        values.put(FeedBackSql.FEEDBACK_ID, feed.id);
        values.put(FeedBackSql.FEEDBACK_RESNANE, feed.resName);
        values.put(FeedBackSql.FEEDBACK_FEEDBACK,feed.feedback);
        values.put(FeedBackSql.FEEDBACK_GRADE,feed.grade);
        values.put(FeedBackSql.FEEDBACK_RESID,feed.resID);
        values.put(FeedBackSql.FEEDBACK_OWNER_ID,feed.ownerID);
        values.put(FeedBackSql.FEEDBACK_OWNER_NAME,feed.ownerName);
        values.put(FeedBackSql.FEEDBACK_IS_REMOVED,feed.isRemoved);
        values.put(FeedBackSql.FEEDBACK_LAST_UPDATE_DATE, feed.lastUpdateDate);
        db.update(FEEDBACK_TABLE, values, FEEDBACK_ID+"=?", new String[]{feed.id});

    }
}



