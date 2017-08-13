package com.example.moranav.hungry;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;

import com.example.moranav.hungry.model.ModelUser;
import com.example.moranav.hungry.model.User;

/**
 * this class handles the users' authentication using the firebase authentication
 */

public class AuthActivity extends Activity implements SignInFragment.SinginFragmentListener ,RegisterFragment.RegisterFragmentListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        FragmentTransaction tran = getFragmentManager().beginTransaction() ;
        SignInFragment signinFragment = new SignInFragment();
        tran.add(R.id.login_container,signinFragment);
        tran.commit() ;
    }

    @Override
    public void onRegisterClick() {
        FragmentTransaction tran = getFragmentManager().beginTransaction() ;
        RegisterFragment registerFragment = new RegisterFragment();
        tran.replace(R.id.login_container,registerFragment);
        tran.addToBackStack("");
        tran.commit() ;
    }

    @Override
    public void onSignin() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void onRegisterSuccess(User user) {
        ModelUser.instance.addUser(user);
        onSignin();
        finish();
    }
}
