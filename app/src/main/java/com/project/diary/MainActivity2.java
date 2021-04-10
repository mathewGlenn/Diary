package com.project.diary;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.project.diary.databinding.ActivityMain2Binding;
import com.project.diary.entries.EntriesList;

import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity2 extends AppCompatActivity  implements EmojiDialog.EmojiDialogListener{

    private ActivityMain2Binding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        binding = ActivityMain2Binding.inflate(getLayoutInflater());
        View v = binding.getRoot();
        setContentView(v);

        SimpleDateFormat dateTimeFormat;

        Calendar calendar;

        calendar = Calendar.getInstance();

        dateTimeFormat = new SimpleDateFormat("MM-dd-yyyy hh:mm a", java.util.Locale.getDefault());

        String date = dateTimeFormat.format(calendar.getTime());



        try {
            Date d_date = dateTimeFormat.parse(date);

            DateFormat dateOnly = new SimpleDateFormat("dd",java.util.Locale.getDefault());
            DateFormat dayOnly = new SimpleDateFormat("EEE", java.util.Locale.getDefault());
            DateFormat monthOnly = new SimpleDateFormat("MMM", java.util.Locale.getDefault());

            DateFormat time  = new SimpleDateFormat("hh: mm a", java.util.Locale.getDefault());

            binding.date.setText(dateOnly.format(d_date));
            binding.day.setText(dayOnly.format(d_date));
            binding.month.setText(monthOnly.format(d_date));

            binding.time.setText(time.format(d_date));


        } catch (ParseException e) {
            e.printStackTrace();
        }


        List<String> tagList = new ArrayList<>();
        tagList.add("Good");
        tagList.add("Funny");
        tagList.add("Sex");
        setTag(tagList);

    }


    public void onChooseFeeling(View view) {
        EmojiDialog emojiDialog = new EmojiDialog();
        emojiDialog.show(getSupportFragmentManager(), "emoji dialog");
    }


    @Override
    public void applyFeeling(String uFeeling) {
        binding.feeling.setText(uFeeling);
    }

    public void gotoMainAc(View view) {
        startActivity(new Intent(this, EntriesList.class));
    }



    private void setTag(final List<String> tagList) {
        final ChipGroup chipGroup = findViewById(R.id.chipGroup);
        for (int index = 0; index < tagList.size(); index++) {
            final String tagName = tagList.get(index);
            final Chip chip = new Chip(this);
            int paddingDp = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, 10,
                    getResources().getDisplayMetrics()
            );
            chip.setPadding(paddingDp, paddingDp, paddingDp, paddingDp);
            chip.setText(tagName);
            chip.setTextColor(getResources().getColor(R.color.white));
            chip.setBackgroundColor(getResources().getColor(R.color.black));
            chip.setCloseIconResource(R.drawable.ic_baseline_close_24);
            chip.setTextAppearance(this, android.R.style.TextAppearance_Small);
            chip.setCloseIconVisible(true);
            //Added click listener on close icon to remove tag from ChipGroup
            chip.setOnCloseIconClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tagList.remove(tagName);
                    chipGroup.removeView(chip);
                }
            });

            chipGroup.addView(chip);
        }
    }
}