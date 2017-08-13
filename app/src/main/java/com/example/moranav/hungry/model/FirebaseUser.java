package com.example.moranav.hungry.model;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

/**
 * this class manage the user table in the firebase db
 */

public class FirebaseUser {

    public void addUser(User user) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("users");
        Map<String, Object> value = new HashMap<>();
        value.put("id", user.id);
        value.put("name", user.name);
        value.put("phone",user.phone);
        value.put("address", user.address);
        value.put("lastUpdateDate", ServerValue.TIMESTAMP);

        myRef.child(user.id).setValue(value);
    }

    interface GetUserCallback {
        void onComplete(User user);

        void onCancel();
    }

    public void getUser(String userId, final GetUserCallback callback) {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("users");

        myRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                callback.onComplete(user);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onCancel();
            }
        });
    }


}
