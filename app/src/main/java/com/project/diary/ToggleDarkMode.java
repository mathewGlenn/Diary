package com.project.diary;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.project.diary.databinding.ActivityToggleDarkModeBinding;

public class ToggleDarkMode extends AppCompatActivity {
    static SharedPreferences sharedPreferences;
    static boolean isDarkModeOn;
    static SharedPreferences.Editor editor;
ActivityToggleDarkModeBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityToggleDarkModeBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        isDarkModeOn = sharedPreferences.getBoolean("isDarkModeOn", false);

        if (isDarkModeOn){
            binding.darkmode.setText("Dark Mode is enabled");

        }else {
            binding.darkmode.setText("Dark Mode is disabled");
        }

    }

    public void closeActivity(View view) {
        finish();
    }

    public void changeTheme(View view) {
        if (isDarkModeOn){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            binding.darkmode.setText("Dark Mode is enabled");

            editor.putBoolean("isDarkModeOn", false);
            editor.apply();
        }else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            binding.darkmode.setText("Dark Mode is disabled");

            editor.putBoolean("isDarkModeOn", true);
            editor.apply();
        }
    }
}