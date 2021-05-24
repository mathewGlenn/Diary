package com.project.diary.authentication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.project.diary.MainActivity2;
import com.project.diary.R;
import com.project.diary.Splash;
import com.project.diary.databinding.ActivityLoginBinding;
import com.project.diary.entries.EntriesList;

import org.jetbrains.annotations.NotNull;

public class Login extends AppCompatActivity {

    private static final String TAG = "FacebookLogin";

    private CallbackManager manager;


    private ActivityLoginBinding binding;

    FirebaseAuth auth;
    FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();


        binding.btnLogin.setOnClickListener(v -> {

            binding.progress.setVisibility(View.INVISIBLE);

            String lEmail = binding.editEmail.getText().toString();
            String lPassword = binding.editPassword.getText().toString();

            if (lEmail.isEmpty() || lPassword.isEmpty()){
                Toast.makeText(Login.this, "All fields are required", Toast.LENGTH_SHORT).show();
                return;
            }

            //signin user
            auth.signInWithEmailAndPassword(lEmail, lPassword).addOnSuccessListener(authResult -> {
                Toast.makeText(Login.this, "Login Successful", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), Splash.class));
                finish();
            }).addOnFailureListener(e -> {
                Toast.makeText(Login.this, "Login Failed", Toast.LENGTH_SHORT).show();
                binding.progress.setVisibility(View.GONE);
            });
        });

        binding.btnClose.setOnClickListener(v -> finish());

        //Facebook Login
        manager = CallbackManager.Factory.create();
        LoginButton loginButton = binding.loginButton;
        loginButton.setPermissions("email", "public_profile");
        loginButton.registerCallback(manager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError:" + error);
                Toast.makeText(Login.this, "error", Toast.LENGTH_SHORT).show();
            }
        });

    }

/*    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser != null) {
            Log.d(TAG, "Currently Signed in:" + currentUser.getEmail());
            //Toast.makeText(this, "currently logged in", Toast.LENGTH_SHORT).show();
            finish();
            startActivity(new Intent(this, EntriesList.class));
        }
    }*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        manager.onActivityResult(requestCode, resultCode, data);
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        auth.signInWithCredential(credential).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                //sign in success, ui will update with the signed in user's information
                Log.d(TAG, "signInWithCredential:success");
                FirebaseUser user = auth.getCurrentUser();
                Toast.makeText(Login.this, "Authentication Succeeded", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, EntriesList.class));
                finish();
            } else {
                //if signin fails a message will display to the user
                Log.d(TAG, "signInCredential:failure", task.getException());
                Toast.makeText(Login.this, "Authentication failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    public void continueAsGuest(View view) {
        auth.signInAnonymously().addOnSuccessListener(authResult -> {
            Toast.makeText(this, "Signed in anonymously", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getApplicationContext(), EntriesList.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                Toast.makeText(Login.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void createAccount(View view) {
        finish();
        startActivity(new Intent(this, Register.class));
    }
}