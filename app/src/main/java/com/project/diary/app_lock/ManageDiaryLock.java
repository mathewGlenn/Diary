package com.project.diary.app_lock;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;

import com.project.diary.R;
import com.project.diary.databinding.ActivityManageDiaryLockBinding;
import com.project.diary.entries.EntriesList;

import p32929.easypasscodelock.Utils.EasyLock;
import p32929.easypasscodelock.Utils.LockscreenHandler;

public class ManageDiaryLock extends AppCompatActivity {

    ActivityManageDiaryLockBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        binding = ActivityManageDiaryLockBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        super.onCreate(savedInstanceState);
        setContentView(view);

      //  EasyLock.setBackgroundColor(R.color.white);
      //  EasyLock.checkPassword(this);

        binding.switchEnableDiaryLock.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
               //

            }
        });

        binding.cardSetPin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               //
            }
        });
    }

    public void closeSetDiaryLock(View view) {
        super.onBackPressed();
        finish();
    }
}