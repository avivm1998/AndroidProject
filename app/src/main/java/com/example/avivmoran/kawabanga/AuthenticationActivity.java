package com.example.avivmoran.kawabanga;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class AuthenticationActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
