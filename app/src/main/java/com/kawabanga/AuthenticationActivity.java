package com.kawabanga;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;

import com.kawabanga.model.ModelUser;
import com.kawabanga.model.User;

/**
 * this class handles the users' authentication using the firebase authentication
 */

public class AuthenticationActivity extends Activity implements SignInFragment.LoginFragmentListener, RegisterFragment.RegisterFragmentListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);
        FragmentTransaction tran = getFragmentManager().beginTransaction() ;
        SignInFragment signinFragment = new SignInFragment();
        tran.add(R.id.login_container, signinFragment);
        tran.commit();
    }

    @Override
    public void onRegisterClick() {
        FragmentTransaction tran = getFragmentManager().beginTransaction() ;
        RegisterFragment registerFragment = new RegisterFragment();
        tran.replace(R.id.login_container, registerFragment);
        tran.addToBackStack("");
        tran.commit();
    }

    @Override
    public void onLogin() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onRegisterSuccess(User user) {
        ModelUser.instance.addUser(user);
        onLogin();

    }
}
