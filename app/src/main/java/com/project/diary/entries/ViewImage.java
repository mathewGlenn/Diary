package com.project.diary.entries;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.project.diary.databinding.ActivityViewImageBinding;

public class ViewImage extends AppCompatActivity {
    Intent data;
    String imgLink;
    ActivityViewImageBinding binding;
    boolean showButtons = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding = ActivityViewImageBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

/*        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);*/

        data = getIntent();

        imgLink = data.getStringExtra("imgLink");

        Glide.with(this).load(imgLink).into(binding.imageFull);


        binding.imageFull.setOnClickListener(v -> {
            if (showButtons) {
                binding.actionBar.setVisibility(View.INVISIBLE);
                binding.imgOptions.setVisibility(View.INVISIBLE);
                showButtons = false;
            } else {
                binding.actionBar.setVisibility(View.VISIBLE);
                binding.imgOptions.setVisibility(View.VISIBLE);
                showButtons = true;
            }
        });

        binding.btnBack.setOnClickListener(v->{
            finish();
        });


    }

    @Override
    public void onBackPressed() {
        finish();
    }
}