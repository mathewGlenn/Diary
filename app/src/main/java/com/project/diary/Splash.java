package com.project.diary;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.project.diary.entries.EntriesList;


public class Splash extends AppCompatActivity {
    FirebaseAuth auth;

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        auth = FirebaseAuth.getInstance();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
//Check if the user is logged in
                if (auth.getCurrentUser() != null){
                    startActivity(new Intent(getApplicationContext(), EntriesList.class));
                    finish();
                }else {
                    //create anonymous account
                    auth.signInAnonymously().addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            Toast.makeText(getApplicationContext(), " Logged in with an anonymous account", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(getApplicationContext(), EntriesList.class));
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });

                }

            }
        }, 1000);
    }
}
