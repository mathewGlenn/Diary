package com.project.diary.authentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.project.diary.R;
import com.project.diary.databinding.ActivityRegisterBinding;
import com.project.diary.entries.EntriesList;

public class Register extends AppCompatActivity {

    FirebaseAuth auth;
    private ActivityRegisterBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        auth = FirebaseAuth.getInstance();

         binding.btnSync.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 final String full_name = binding.editUsername.getText().toString();
                 String email = binding.editEmail.getText().toString();
                 String password = binding.editPassword.getText().toString();
                 String confirm_pass = binding.editConfirmPassword.getText().toString();

                 if (full_name.isEmpty() || email.isEmpty() || password.isEmpty() || confirm_pass.isEmpty()){
                     Toast.makeText(Register.this, "All fields are required", Toast.LENGTH_SHORT).show();
                     return;
                 }

                 if (!password.equals(confirm_pass)){
                     Toast.makeText(Register.this, "Password do not match", Toast.LENGTH_SHORT).show();

                 }

                 binding.progress.setVisibility(View.VISIBLE);

                 AuthCredential credential = EmailAuthProvider.getCredential(email, password);
                 auth.getCurrentUser().linkWithCredential(credential).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                     @Override
                     public void onSuccess(AuthResult authResult) {
                         Toast.makeText(Register.this, "Account synced successfuly", Toast.LENGTH_SHORT).show();

                         FirebaseUser user = auth.getCurrentUser();
                         UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                                 .setDisplayName(full_name)
                                 .build();
                         user.updateProfile(request);
                         finish();
                         startActivity(new Intent(getApplicationContext(), EntriesList.class));

                     }
                 }).addOnFailureListener(new OnFailureListener() {
                     @Override
                     public void onFailure(@NonNull Exception e) {
                         Toast.makeText(Register.this, "Error linking account", Toast.LENGTH_SHORT).show();
                         binding.progress.setVisibility(View.GONE);
                     }
                 });

             }
         });

         binding.btnClose.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 finish();
             }
         });

    }

    @Override
    public void onBackPressed() {
        finish();
    }
}