package com.project.diary;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.firebase.auth.FirebaseAuth;
import com.project.diary.authentication.Login;
import com.project.diary.entries.EntriesList;


public class Splash extends AppCompatActivity {
    FirebaseAuth auth;

    static SharedPreferences sharedPreferences;
    static boolean isDarkModeOn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE);
        isDarkModeOn = sharedPreferences.getBoolean("isDarkModeOn", false);

        if (isDarkModeOn) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        auth = FirebaseAuth.getInstance();

        Handler handler = new Handler();
        handler.postDelayed(() -> {
            //Check if the user is logged in
            if (auth.getCurrentUser() != null) {
                startActivity(new Intent(getApplicationContext(), EntriesList.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            } else {
                //create anonymous account
                /*auth.signInAnonymously().addOnSuccessListener(authResult -> {
                    Toast.makeText(getApplicationContext(), " Logged in with an anonymous account", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(getApplicationContext(), LockScreen.class));
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    finish();
                }).addOnFailureListener(e -> {
                    Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    finish();
                });*/
                startActivity(new Intent(getApplicationContext(), Login.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            }
        }, 1000);
    }
}
