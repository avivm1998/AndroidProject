package com.kawabanga;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.kawabanga.model.ModelPost;
import com.kawabanga.model.ModelUser;
import com.kawabanga.model.User;

public class MainActivity extends Activity { // implements UploadFragment.OnFragmentInteractionListener{
    public static User user;
    public static FirebaseAuth mAuth ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("TAG", "main activity oncreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ModelPost.instance.RegisterUpdates();

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

        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        PostsListFragment postsListFragment = PostsListFragment.newInstance(null);
        fragmentTransaction.replace(R.id.content, postsListFragment);
        fragmentTransaction.addToBackStack("");
        fragmentTransaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.navigation, menu);
        return true ;
    }

    @Override


    public boolean onOptionsItemSelected(MenuItem item) {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();

        switch (item.getItemId()) {
            case R.id.navigation_home:
                PostsListFragment postsListFragment = PostsListFragment.newInstance(null);
                fragmentTransaction.replace(R.id.content, postsListFragment);
                //fragmentTransaction.addToBackStack("");
                fragmentTransaction.commit();
                return true;

            case R.id.navigation_upload:
                UploadFragment uploadFragment = UploadFragment.newInstance("", "");
                fragmentTransaction.replace(R.id.content, uploadFragment);
                fragmentTransaction.commit();
                return true;

            case R.id.navigation_account:
                PostsListFragment userPostListFragment = PostsListFragment.newInstance(user.id);
                fragmentTransaction.replace(R.id.content, userPostListFragment);
                fragmentTransaction.commit();
                return true;

            case R.id.navigation_logout:
                mAuth.signOut();
                ModelPost.instance.unRegisterUpdates();
                Intent intent = new Intent(this, AuthenticationActivity.class);
                startActivity(intent);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
