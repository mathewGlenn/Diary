package com.project.diary.profile;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.project.diary.databinding.ActivityEditProfileBinding;

public class EditProfile extends AppCompatActivity {
    ActivityEditProfileBinding binding;

    static SharedPreferences sharedPreferences;
    static SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditProfileBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        binding.btnBack.setOnClickListener(v->finish());
        binding.imgProfilePic.setOnClickListener(v-> Toast.makeText(this, "Coming Soon...", Toast.LENGTH_SHORT).show());
        binding.editName.setOnClickListener(v-> Toast.makeText(this, "Coming Soon...", Toast.LENGTH_SHORT).show());

        binding.saveProfile.setOnClickListener(v->{
            editor.putString("favQuote", binding.editQuote.getText().toString());
            editor.apply();
            finish();
        });

    }

    public void closeActivity(View view) {
        finish();
    }
}