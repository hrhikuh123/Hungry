package com.example.moranav.hungry.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import java.util.LinkedList;
import java.util.List;

/**
 * This class is facade of feedback model functionality
 */

public class ModelFeedBack {

    public static ModelFeedBack instance = new ModelFeedBack();
    public ModelSql modelSql;
    private FirebaseFeedBack firebaseFeedBack;

    private ModelFeedBack() {
        firebaseFeedBack = new FirebaseFeedBack();
        modelSql = Utils.modelSql ;

    }

    private void synchFeedbacksDbAndregisterFeedbackUpdates() {
        //1. get local lastUpdateTade
        SharedPreferences pref = Hungry.getMyContext().getSharedPreferences("TAG", Context.MODE_PRIVATE);
        final double lastUpdateDate = pref.getFloat("FeedBacksLastUpdateDate", 0);
        Log.d("TAG", "lastUpdateDate: " + lastUpdateDate);

        firebaseFeedBack.registerFeedBacksUpdates(lastUpdateDate, new FirebaseFeedBack.RegisterFeedBackUpdatesCallback() {
            @Override
            public void onFeedBackUpdate(FeedBack feed) {
                boolean isDeleted = false ;
                //3. update the local db
                if(FeedBackSql.getFeedBack(modelSql.getReadableDatabase(),feed.id) == null)
                    FeedBackSql.addFeedBack(modelSql.getWritableDatabase(), feed);
                else {
                    if(feed.isRemoved == 1) {
                        FeedBackSql.deleteFeedBack(modelSql.getWritableDatabase(),feed);
                        isDeleted = true ;
                    }
                    else
                        FeedBackSql.updateFeedBack(modelSql.getWritableDatabase(), feed);
                }
                //4. update the lastUpdateTade
                SharedPreferences pref = Hungry.getMyContext().getSharedPreferences("TAG", Context.MODE_PRIVATE);
                final double lastUpdateDate = pref.getFloat("FeedBacksLastUpdateDate", 0);
                if (lastUpdateDate < feed.lastUpdateDate) {
                    SharedPreferences.Editor prefEd = Hungry.getMyContext().getSharedPreferences("TAG",
                            Context.MODE_PRIVATE).edit();
                    prefEd.putFloat("FeedBacksLastUpdateDate", (float) feed.lastUpdateDate);
                    prefEd.commit();
                    Log.d("TAG", "FeedBacksLastUpdateDate: " + feed.lastUpdateDate);
                }

                EventBus.getDefault().post(new UpdateFeedBackEvent(feed,isDeleted));
            }
        });
    }


    public List<FeedBack> getAllFeedBacksByResID(String id) {
        return FeedBackSql.getAllFeedBacksByResID(modelSql.getWritableDatabase(),id);
    }

    public FeedBack getFeedBack(String feedID) {
        return FeedBackSql.getFeedBack(modelSql.getReadableDatabase(),feedID);
    }

    public List<FeedBack> getAllFeedBacks() {
        return FeedBackSql.getAllFeedBacks(modelSql.getReadableDatabase());
    }

    public void addFeedBack(FeedBack f) {
        firebaseFeedBack.addFeedBack(f);
    }

    public void deleteFeedBack(FeedBack f) {
        f.isRemoved  = 1 ;
        firebaseFeedBack.addFeedBack(f);
    }

    public void updateFeedBack(FeedBack feed)
    {
        firebaseFeedBack.addFeedBack(feed);
    }

    public List<FeedBack> getAllFeedBackByOwnerID(String id) {
        List<FeedBack> feeds = FeedBackSql.getAllFeedBacksByOwnerID(modelSql.getWritableDatabase(), id);
        List<Restaurant> restaurants = RestaurantSql.getAllRestaurants(modelSql.getReadableDatabase());
        List<FeedBack> real_feed = new LinkedList<>();
        for (FeedBack f : feeds) {
            for (Restaurant r : restaurants) {
                if (f.resID.equals(r.id)) {
                    real_feed.add(f);
                    break;
                }
            }
        }
        return real_feed;
    }

    public class UpdateFeedBackEvent {
        public final FeedBack feed ;
        public final boolean isDeleted;
        public UpdateFeedBackEvent(FeedBack feed,boolean isDeleted){
            this.feed = feed ;
            this.isDeleted = isDeleted;
        }
    }


    public void RegisterUpdates()
    {
        synchFeedbacksDbAndregisterFeedbackUpdates();
    }

    public void unRegisterUpdates()
    {
        firebaseFeedBack.unregisterFeedBacksUpdates();
    }

}


