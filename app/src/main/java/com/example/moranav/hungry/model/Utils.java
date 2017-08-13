package com.example.moranav.hungry.model;

import android.database.Cursor;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import static com.example.moranav.hungry.model.FeedBackSql.FEEDBACK_FEEDBACK;
import static com.example.moranav.hungry.model.FeedBackSql.FEEDBACK_GRADE;
import static com.example.moranav.hungry.model.FeedBackSql.FEEDBACK_ID;
import static com.example.moranav.hungry.model.FeedBackSql.FEEDBACK_IS_REMOVED;
import static com.example.moranav.hungry.model.FeedBackSql.FEEDBACK_LAST_UPDATE_DATE;
import static com.example.moranav.hungry.model.FeedBackSql.FEEDBACK_OWNER_ID;
import static com.example.moranav.hungry.model.FeedBackSql.FEEDBACK_OWNER_NAME;
import static com.example.moranav.hungry.model.FeedBackSql.FEEDBACK_RESID;
import static com.example.moranav.hungry.model.FeedBackSql.FEEDBACK_RESNANE;
import static com.example.moranav.hungry.model.RestaurantSql.RESTAURANT_ADDRESS;
import static com.example.moranav.hungry.model.RestaurantSql.RESTAURANT_DESCRIPTION;
import static com.example.moranav.hungry.model.RestaurantSql.RESTAURANT_ID;
import static com.example.moranav.hungry.model.RestaurantSql.RESTAURANT_IMAGE_URL;
import static com.example.moranav.hungry.model.RestaurantSql.RESTAURANT_IS_REMOVED;
import static com.example.moranav.hungry.model.RestaurantSql.RESTAURANT_LAST_UPDATE_DATE;
import static com.example.moranav.hungry.model.RestaurantSql.RESTAURANT_LOGO_URL;
import static com.example.moranav.hungry.model.RestaurantSql.RESTAURANT_NAME;
import static com.example.moranav.hungry.model.RestaurantSql.RESTAURANT_OPEN_HOURS;
import static com.example.moranav.hungry.model.RestaurantSql.RESTAURANT_OWNER_ID;
import static com.example.moranav.hungry.model.RestaurantSql.RESTAURANT_PHONE;


public  class Utils {


    static public ModelSql modelSql = new ModelSql(Hungry.getMyContext());

    public static String getTimeValue(){
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        return  df.format(Calendar.getInstance().getTime());
    }

    public static List<Restaurant> getListOfRestarurantFromCursor(Cursor cursor)
    {
        List<Restaurant> list = new LinkedList<Restaurant>();
        if (cursor.moveToFirst()) {
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

            do {
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
                if(res.isRemoved!=1)
                    list.add(res);
            } while (cursor.moveToNext());
        }
        return list;
    }

    public static List<FeedBack> getListOfFeedBackFromCursor(Cursor cursor)
    {
        List<FeedBack> list = new LinkedList<FeedBack>();
        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(FEEDBACK_ID);
            int ResnameIndex = cursor.getColumnIndex(FEEDBACK_RESNANE);
            int feedbackIndex =cursor.getColumnIndex(FEEDBACK_FEEDBACK);
            int gradeIndex =cursor.getColumnIndex(FEEDBACK_GRADE);
            int resIDIndex =cursor.getColumnIndex(FEEDBACK_RESID);
            int OwnerIDIndex = cursor.getColumnIndex(FEEDBACK_OWNER_ID);
            int OwnerNameIndex = cursor.getColumnIndex(FEEDBACK_OWNER_NAME);
            int isRemovedIndex = cursor.getColumnIndex(FEEDBACK_IS_REMOVED);
            int lastUpdateIndex = cursor.getColumnIndex(FEEDBACK_LAST_UPDATE_DATE);

            do {
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
                if(feed.isRemoved!=1)
                    list.add(feed);
            } while (cursor.moveToNext());
        }
        return list;
    }

}
