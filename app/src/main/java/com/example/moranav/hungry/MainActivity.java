package com.example.moranav.hungry;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.moranav.hungry.model.ModelFeedBack;
import com.example.moranav.hungry.model.ModelResturant;
import com.example.moranav.hungry.model.ModelUser;
import com.example.moranav.hungry.model.User;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends Activity  implements ResListFragment.ResListFragmentListener , ResDetailsFragment.ResDetailFragmentListener , FeedListFragment.FeedBackListListener,HomeFragment.HomeFragmentListener{

    public boolean EditPremission = false ;
    public static User user;
    public static FirebaseAuth mAuth ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ModelResturant.instance.RegisterUpdates();
        ModelFeedBack.instance.RegisterUpdates();
        mAuth = FirebaseAuth.getInstance();
        ModelUser.instance.getUser(mAuth.getCurrentUser().getUid(), new ModelUser.GetUserCallback() {
            @Override
            public void onComplete(User user) {
                MainActivity.this.user = user ;
            }

            @Override
            public void onCancel() {

            }
        });
        setContentView(R.layout.activity_main);
        FragmentTransaction tran = getFragmentManager().beginTransaction() ;
        HomeFragment homeFragment = HomeFragment.newInstance();
        tran.add(R.id.main_container,homeFragment);
        tran.commit() ;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main,menu);
        return true ;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.menu_add:
                onAddResClick();
                break;

            case R.id.menu_logout:
                FirebaseAuth fAuth = FirebaseAuth.getInstance();
                fAuth.signOut();
                ModelFeedBack.instance.unRegisterUpdates();
                ModelResturant.instance.unRegisterUpdates();
                Intent intent = new Intent(this, AuthActivity.class);
                startActivity(intent);
                finish();
                break;

            default:
                return super.onOptionsItemSelected(item);
        }

        return true ;
    }

    @Override
    public void onClickResRow(String id) {
        FragmentTransaction tran = getFragmentManager().beginTransaction();
        //does'nt have a permission to edit
        if(!EditPremission) {
            ResDetailsFragment detailFragment = ResDetailsFragment.newInstance(id);
            tran.replace(R.id.main_container, detailFragment);
        }
        //there is a permission to edit
        else
        {
            ResAddFragment resEditFragment = ResAddFragment.newInstance(id);
            tran.replace(R.id.main_container, resEditFragment);
        }
        tran.addToBackStack("");
        tran.commit();
    }

    @Override
    public void onFeedBacksButtonClick(String resID) {
        FragmentTransaction tran = getFragmentManager().beginTransaction() ;
        FeedBackAddFragment feedAddFragment = FeedBackAddFragment.newInstance(null,resID);
        FeedListFragment feedList = FeedListFragment.newInstance(resID,false);
        tran.add(R.id.secondary_container,feedList);
        findViewById(R.id.secondary_container).setVisibility(View.VISIBLE);
        tran.replace(R.id.main_container,feedAddFragment);
        tran.addToBackStack("");
        tran.commit() ;
    }


    @Override
    public void onFeedBackClick(String feedbackId, boolean ifownerID) {
        if(!ifownerID) return ;
        FragmentTransaction tran = getFragmentManager().beginTransaction();
        FeedBackAddFragment feedEditFragment = FeedBackAddFragment.newInstance(feedbackId,null);
        tran.replace(R.id.main_container,feedEditFragment);
        tran.addToBackStack("");
        tran.commit() ;

    }

    @Override
    public void onPersonalAreaClick() {
        FragmentTransaction tran = getFragmentManager().beginTransaction();
        FeedListFragment feedListFragment = FeedListFragment.newInstance(user.id,true);
        tran.replace(R.id.main_container,feedListFragment);
        tran.addToBackStack("");
        tran.commit() ;
    }

    @Override
    public void onResListClick() {
        FragmentTransaction tran = getFragmentManager().beginTransaction() ;
        ResListFragment resListFragment = ResListFragment.newInstance(null);
        tran.replace(R.id.main_container,resListFragment);
        tran.addToBackStack("");
        tran.commit() ;
    }

    @Override
    public void onAddResClick() {
        FragmentTransaction tran = getFragmentManager().beginTransaction() ;
        ResAddFragment resAddFragment = ResAddFragment.newInstance(null);
        tran.replace(R.id.main_container,resAddFragment);
        tran.addToBackStack("");
        tran.commit() ;
    }

    @Override
    public void onEditResClick() {
        EditPremission = true ;
        FragmentTransaction tran = getFragmentManager().beginTransaction() ;
        ResListFragment resListFragment = ResListFragment.newInstance(user.id);
        tran.replace(R.id.main_container,resListFragment);
        tran.addToBackStack("");
        tran.commit() ;
    }
}
