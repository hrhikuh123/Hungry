package com.example.moranav.hungry.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * this class manage the feedback table in the firebase db
 */

public class FirebaseFeedBack {

    ChildEventListener lisiner = null ;


    public void addFeedBack(FeedBack feed) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(FeedBackSql.FEEDBACK_TABLE);
        Map<String, Object> value = new HashMap<>();
        value.put(FeedBackSql.FEEDBACK_ID, feed.id);
        value.put(FeedBackSql.FEEDBACK_FEEDBACK, feed.feedback);
        value.put(FeedBackSql.FEEDBACK_GRADE, feed.grade);
        value.put(FeedBackSql.FEEDBACK_OWNER_ID, feed.ownerID);
        value.put(FeedBackSql.FEEDBACK_OWNER_NAME, feed.ownerName);
        value.put(FeedBackSql.FEEDBACK_IS_REMOVED, feed.isRemoved);
        value.put(FeedBackSql.FEEDBACK_RESID, feed.resID);
        value.put(FeedBackSql.FEEDBACK_RESNANE, feed.resName);
        value.put(FeedBackSql.FEEDBACK_LAST_UPDATE_DATE,ServerValue.TIMESTAMP );

        myRef.child(feed.id).setValue(value);
    }

    interface GetFeedBackCallback {
        void onComplete(FeedBack feed);

        void onCancel();
    }

    public void getFeedBack(String feedId, final GetFeedBackCallback callback) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(FeedBackSql.FEEDBACK_TABLE);
        myRef.child(feedId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                FeedBack feed = dataSnapshot.getValue(FeedBack.class);
                callback.onComplete(feed);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onCancel();
            }
        });
    }

    interface RegisterFeedBackUpdatesCallback {
        void onFeedBackUpdate(FeedBack res);
    }
    public void registerFeedBacksUpdates(double lastUpdateDate,
                                           final RegisterFeedBackUpdatesCallback callback) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(FeedBackSql.FEEDBACK_TABLE);
        lisiner = myRef.orderByChild(FeedBackSql.FEEDBACK_LAST_UPDATE_DATE).startAt(lastUpdateDate)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        Log.d("TAG","onChildAdded called");
                        FeedBack feed = dataSnapshot.getValue(FeedBack.class);
                        callback.onFeedBackUpdate(feed);
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                        FeedBack feed = dataSnapshot.getValue(FeedBack.class);
                        callback.onFeedBackUpdate(feed);
                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {
                        FeedBack feed = dataSnapshot.getValue(FeedBack.class);
                        callback.onFeedBackUpdate(feed);
                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                        FeedBack feed = dataSnapshot.getValue(FeedBack.class);
                        callback.onFeedBackUpdate(feed);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    public void unregisterFeedBacksUpdates()
    {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(FeedBackSql.FEEDBACK_TABLE);
        if(lisiner !=null)
            myRef.removeEventListener(lisiner);
        lisiner = null ;
    }

}
