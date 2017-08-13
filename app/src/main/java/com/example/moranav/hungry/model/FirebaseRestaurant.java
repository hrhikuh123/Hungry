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
 * this class manage the restaurant table in the firebase db
 */

public class FirebaseRestaurant {


    ChildEventListener lisiner = null;

    public void addRestaurant(Restaurant res) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(RestaurantSql.RESTAURANT_TABLE);
        Map<String, Object> value = new HashMap<>();
        value.put(RestaurantSql.RESTAURANT_ID, res.id);
        value.put(RestaurantSql.RESTAURANT_NAME, res.name);
        value.put(RestaurantSql.RESTAURANT_DESCRIPTION,res.description);
        value.put(RestaurantSql.RESTAURANT_LOGO_URL,res.logoURL);
        value.put(RestaurantSql.RESTAURANT_OPEN_HOURS,res.openHour);
        value.put(RestaurantSql.RESTAURANT_OWNER_ID,res.ownerID);
        value.put(RestaurantSql.RESTAURANT_PHONE,res.phone);
        value.put(RestaurantSql.RESTAURANT_ADDRESS, res.address);
        value.put(RestaurantSql.RESTAURANT_IS_REMOVED,res.isRemoved);
        value.put(RestaurantSql.RESTAURANT_LAST_UPDATE_DATE, ServerValue.TIMESTAMP);
        value.put(RestaurantSql.RESTAURANT_IMAGE_URL, res.imageURL);

        myRef.child(res.id).setValue(value);
    }

    interface GetRestaurantCallback {
        void onComplete(Restaurant res);

        void onCancel();
    }

    public void getRestaurant(String stId, final GetRestaurantCallback callback) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(RestaurantSql.RESTAURANT_TABLE);
        myRef.child(stId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Restaurant res = dataSnapshot.getValue(Restaurant.class);
                callback.onComplete(res);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onCancel();
            }
        });
    }

    interface RegisterRestaurantUpdatesCallback {
        void onRestaurantUpdate(Restaurant res);
    }
    public void registerRestaurantsUpdates(double lastUpdateDate,
                                           final RegisterRestaurantUpdatesCallback callback) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(RestaurantSql.RESTAURANT_TABLE);
        lisiner = myRef.orderByChild(RestaurantSql.RESTAURANT_LAST_UPDATE_DATE).startAt(lastUpdateDate)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        Log.d("TAG","onChildAdded called");
                        Restaurant restaurant = dataSnapshot.getValue(Restaurant.class);
                        callback.onRestaurantUpdate(restaurant);
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                        Restaurant res = dataSnapshot.getValue(Restaurant.class);
                        callback.onRestaurantUpdate(res);
                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {
                        Restaurant res = dataSnapshot.getValue(Restaurant.class);
                        callback.onRestaurantUpdate(res);
                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                        Restaurant res = dataSnapshot.getValue(Restaurant.class);
                        callback.onRestaurantUpdate(res);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    public void saveImage(Bitmap imageBmp, String name, final ModelResturant.SaveImageListener listener){
        FirebaseStorage storage = FirebaseStorage.getInstance();

        StorageReference imagesRef = storage.getReference().child("images").child(name);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        imageBmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = imagesRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception exception) {
                listener.fail();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                @SuppressWarnings("VisibleForTests") Uri downloadUrl = taskSnapshot.getDownloadUrl();
                listener.complete(downloadUrl.toString());
            }
        });
    }

    public void getImage(String url, final ModelResturant.GetImageListener listener){
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference httpsReference = storage.getReferenceFromUrl(url);
        final long ONE_MEGABYTE = 1024 * 1024;
        httpsReference.getBytes(3* ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap image = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                listener.onSuccess(image);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception exception) {
                Log.d("TAG",exception.getMessage());
                listener.onFail();
            }
        });
    }

    public void unregisterRestaurantsUpdates()
    {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(RestaurantSql.RESTAURANT_TABLE);
        if(lisiner !=null)
            myRef.removeEventListener(lisiner);
        lisiner = null ;
    }

}
