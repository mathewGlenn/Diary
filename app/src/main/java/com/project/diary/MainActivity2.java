package com.project.diary;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.project.diary.databinding.ActivityMain2Binding;
import com.project.diary.entries.EntriesList;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

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
}