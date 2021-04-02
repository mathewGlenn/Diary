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

        SimpleDateFormat dateFormat, timeFormat;

        Calendar calendar;

        calendar = Calendar.getInstance();

        dateFormat = new SimpleDateFormat("MM-dd-yyyy");
        timeFormat = new SimpleDateFormat("h:mm a");

        String date = dateFormat.format(calendar.getTime());
        String time = timeFormat.format(calendar.getTime());



        try {
            Date d_date = dateFormat.parse(date);

            DateFormat dateOnly = new SimpleDateFormat("dd");
            DateFormat dayOnly = new SimpleDateFormat("EEE");
            DateFormat monthOnly = new SimpleDateFormat("MMM");

            binding.date.setText(dateOnly.format(d_date));
            binding.day.setText(dayOnly.format(d_date));
            binding.month.setText(monthOnly.format(d_date));

            binding.time.setText(time);


        } catch (ParseException e) {
            e.printStackTrace();
        }


    }
}