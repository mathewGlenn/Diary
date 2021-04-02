package com.project.diary;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.project.diary.databinding.ActivityMain2Binding;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MainActivity2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        com.project.diary.databinding.ActivityMain2Binding binding = ActivityMain2Binding.inflate(getLayoutInflater());
        View v = binding.getRoot();
        setContentView(v);

        SimpleDateFormat dateTimeFormat;

        Calendar calendar;

        calendar = Calendar.getInstance();

        dateTimeFormat = new SimpleDateFormat("MM-dd-yyyy hh:mm a");

        String date = dateTimeFormat.format(calendar.getTime());



        try {
            Date d_date = dateTimeFormat.parse(date);

            DateFormat dateOnly = new SimpleDateFormat("dd");
            DateFormat dayOnly = new SimpleDateFormat("EEE");
            DateFormat monthOnly = new SimpleDateFormat("MMM");

            DateFormat time  = new SimpleDateFormat("hh: mm a");

            binding.date.setText(dateOnly.format(d_date));
            binding.day.setText(dayOnly.format(d_date));
            binding.month.setText(monthOnly.format(d_date));

            binding.time.setText(time.format(d_date));


        } catch (ParseException e) {
            e.printStackTrace();
        }


    }
}