package com.example.moranav.hungry.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.util.Log;
import android.webkit.URLUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import static com.example.moranav.hungry.model.ModelFiles.saveImageToFile;

/**
 * This class is facade of restaurant model functionality
 */

public class ModelResturant {

    public static ModelResturant instance = new ModelResturant();
    public ModelSql modelSql;
    private FirebaseRestaurant firebaseRestaurant;

    //constructor
    private ModelResturant() {
        firebaseRestaurant = new FirebaseRestaurant();
        modelSql = Utils.modelSql;
    }

    private void synchResturantsDbAndregisterResturantsUpdates() {
        //1. get local lastUpdateTade
        SharedPreferences pref = Hungry.getMyContext().getSharedPreferences("TAG", Context.MODE_PRIVATE);
        final double lastUpdateDate = pref.getFloat("RestaurantsLastUpdateDate", 0);
        Log.d("TAG", "lastUpdateDate: " + lastUpdateDate);

        firebaseRestaurant.registerRestaurantsUpdates(lastUpdateDate, new FirebaseRestaurant.RegisterRestaurantUpdatesCallback() {
            @Override
            public void onRestaurantUpdate(Restaurant res) {
                boolean isDeleted = false ;
                //3. update the local db
                if(RestaurantSql.getRestaurant(modelSql.getReadableDatabase(),res.id) == null)
                    RestaurantSql.addRestaurant(modelSql.getWritableDatabase(), res);
                else {
                    if(res.isRemoved == 1) {
                        RestaurantSql.deleteRestaurant(modelSql.getWritableDatabase(),res);
                        isDeleted = true ;
                    }
                    else
                        RestaurantSql.updateRestaurant(modelSql.getWritableDatabase(), res);
                }
                //4. update the lastUpdateTade
                SharedPreferences pref = Hungry.getMyContext().getSharedPreferences("TAG", Context.MODE_PRIVATE);
                final double lastUpdateDate = pref.getFloat("RestaurantsLastUpdateDate", 0);
                if (lastUpdateDate < res.lastUpdateDate) {
                    SharedPreferences.Editor prefEd = Hungry.getMyContext().getSharedPreferences("TAG",
                            Context.MODE_PRIVATE).edit();
                    prefEd.putFloat("RestaurantsLastUpdateDate", (float) res.lastUpdateDate);
                    prefEd.commit();
                    Log.d("TAG", "RestaurantsLastUpdateDate: " + res.lastUpdateDate);
                }

                EventBus.getDefault().post(new UpdateRestaurantEvent(res,isDeleted));
            }
        });
    }


    public List<Restaurant> getAllRestaurantsByOwnerID(String id) {
        return RestaurantSql.getAllRestaurantsByOwnerID(modelSql.getReadableDatabase(),id);
    }

    public Restaurant getRestaurant(String resID) {
        return RestaurantSql.getRestaurant(modelSql.getReadableDatabase(),resID);
    }

    public List<Restaurant> getAllRestaurants() {
        return RestaurantSql.getAllRestaurants(modelSql.getReadableDatabase());
    }

    public void addRestaurant(Restaurant r) {
        firebaseRestaurant.addRestaurant(r);
    }

    public void deleteRestaurant(Restaurant res) {
        res.isRemoved  = 1 ;
        firebaseRestaurant.addRestaurant(res);
        List<FeedBack> feeds = ModelFeedBack.instance.getAllFeedBacksByResID(res.id);
        for (FeedBack f:feeds) {
            ModelFeedBack.instance.deleteFeedBack(f);

        }
    }

    public void updateRestaurant(Restaurant res)
    {
        firebaseRestaurant.addRestaurant(res);
    }

    public class UpdateRestaurantEvent {
        public final Restaurant res ;
        public final boolean isDeleted;
        public UpdateRestaurantEvent(Restaurant res,boolean isDeleted){
            this.res = res ;
            this.isDeleted = isDeleted;
        }
    }

    public interface SaveImageListener {
        void complete(String url);
        void fail();
    }

    public void RegisterUpdates()
    {
        synchResturantsDbAndregisterResturantsUpdates();
    }

    public void saveImage(final Bitmap imageBmp, final String name, final SaveImageListener listener) {
        firebaseRestaurant.saveImage(imageBmp, name, new SaveImageListener() {
            @Override
            public void complete(String url) {
                String fileName = URLUtil.guessFileName(url, null, null);
                saveImageToFile(imageBmp,fileName);
                listener.complete(url);
            }

            @Override
            public void fail() {
                listener.fail();
            }
        });


    }

    public interface GetImageListener{
        void onSuccess(Bitmap image);
        void onFail();
    }
    public void getImage(final String url, final GetImageListener listener) {
        //check if image exsist localy
        final String fileName = URLUtil.guessFileName(url, null, null);
        ModelFiles.loadImageFromFileAsynch(fileName, new ModelFiles.LoadImageFromFileAsynch() {
            @Override
            public void onComplete(Bitmap bitmap) {
                if (bitmap != null){
                    Log.d("TAG","getImage from local success " + fileName);
                    listener.onSuccess(bitmap);
                }else {
                    firebaseRestaurant.getImage(url, new GetImageListener() {
                        @Override
                        public void onSuccess(Bitmap image) {
                            String fileName = URLUtil.guessFileName(url, null, null);
                            Log.d("TAG","getImage from FB success " + fileName);
                            saveImageToFile(image,fileName);
                            listener.onSuccess(image);
                        }

                        @Override
                        public void onFail() {
                            Log.d("TAG","getImage from FB fail ");
                            listener.onFail();
                        }
                    });

                }
            }
        });
    }

    public void unRegisterUpdates()
    {
        firebaseRestaurant.unregisterRestaurantsUpdates();
    }
}


