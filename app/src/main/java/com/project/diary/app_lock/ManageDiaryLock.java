package com.project.diary.app_lock;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;

import com.project.diary.R;
import com.project.diary.databinding.ActivityManageDiaryLockBinding;
import com.project.diary.entries.EntriesList;

import p32929.easypasscodelock.Utils.EasyLock;
import p32929.easypasscodelock.Utils.LockscreenHandler;

public class ManageDiaryLock extends LockscreenHandler {
    static SharedPreferences sharedPreferences;
    static boolean isPasswordEnabled;
    static SharedPreferences.Editor editor;
    ActivityManageDiaryLockBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        binding = ActivityManageDiaryLockBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        super.onCreate(savedInstanceState);
        setContentView(view);

        sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        isPasswordEnabled = sharedPreferences.getBoolean("isPasswordEnabled", false);

        if (isPasswordEnabled) {
            binding.switchEnableDiaryLock.setChecked(true);
        }
        //  EasyLock.setBackgroundColor(R.color.white);
        //  EasyLock.checkPassword(this);

        binding.switchEnableDiaryLock.setOnClickListener(v -> {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(ManageDiaryLock.this);

            if (binding.switchEnableDiaryLock.isChecked()) {
                alertDialog.setMessage("Enable lockscreen diary?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                EasyLock.setBackgroundColor(R.color.background);
                                EasyLock.setPassword(ManageDiaryLock.this, ManageDiaryLock.class);
                                finish();
                                editor.putBoolean("isPasswordEnabled", true);
                                editor.apply();
                                binding.switchEnableDiaryLock.setChecked(true);
                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        binding.switchEnableDiaryLock.setChecked(false);
                    }
                });
                AlertDialog alert = alertDialog.create();
                alert.show();

            } else {
                alertDialog.setMessage("You are about to disable your diary's lock screen")
                        .setCancelable(false)
                        .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                EasyLock.setBackgroundColor(R.color.background);
                                EasyLock.disablePassword(ManageDiaryLock.this, ManageDiaryLock.class);
                                finish();
                                editor.putBoolean("isPasswordEnabled", false);
                                editor.apply();
                                binding.switchEnableDiaryLock.setChecked(false);
                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        binding.switchEnableDiaryLock.setChecked(true);
                    }
                });
                AlertDialog alert = alertDialog.create();
                alert.show();
            }
        });


    }

    public void closeSetDiaryLock(View view) {
        super.onBackPressed();
        finish();
    }

    public void changePin(View view) {
        EasyLock.setBackgroundColor(R.color.background);
        EasyLock.changePassword(ManageDiaryLock.this, ManageDiaryLock.class);
        finish();
    }
}