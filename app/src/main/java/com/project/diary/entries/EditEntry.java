package com.project.diary.entries;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.project.diary.EmojiDialog;
import com.project.diary.R;
import com.project.diary.databinding.ActivityEditEntryBinding;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class EditEntry extends AppCompatActivity implements EmojiDialog.EmojiDialogListener {
    //for date time picker
    private int dYear, dMonth, dDay, tHour, tMinute;
    FirebaseFirestore firestore;
    Intent data;
    FirebaseUser user;
    private ActivityEditEntryBinding binding;

    ImageButton choose_feeling;
    String userFeeling = "";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditEntryBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        data = getIntent();

        firestore = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        String title = data.getStringExtra("title");
        String content = data.getStringExtra("content");
        String date = data.getStringExtra("dateOnly");
        String time = data.getStringExtra("timeOnly");
        userFeeling = data.getStringExtra("feeling");

        choose_feeling = binding.btnEmoji;

        binding.entryTitle.setText(title);
        binding.entryContent.setText(content);
        binding.txtTime.setText(time);
        binding.txtDate.setText(date);
        checkUserFeeling();

        binding.btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String editTitle = binding.entryTitle.getText().toString();
                String editContent = binding.entryContent.getText().toString();
                String editDate = binding.txtDate.getText().toString();
                String editTime = binding.txtTime.getText().toString();
                String editDateTime = editDate.concat(" " + editTime);

                if (editContent.isEmpty()){
                    Toast.makeText(EditEntry.this, "Entry content is empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                binding.progress.setVisibility(View.VISIBLE);

                //save edited note

                DocumentReference reference = firestore.collection("allEntries").document(user.getUid()).collection("userEntries").document(data.getStringExtra("entryID"));

                Map<String, Object> entry = new HashMap<>();
                entry.put("title", editTitle);
                entry.put("content", editContent);
                entry.put("date", editDateTime);
                entry.put("feeling", userFeeling);

                reference.update(entry).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(EditEntry.this, "Update Successful", Toast.LENGTH_SHORT).show();
                        binding.progress.setVisibility(View.INVISIBLE);
                        //finishing Entry Content and going directly to Entries List
                        setResult(RESULT_OK, new Intent());
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(EditEntry.this, "Failed updating entries", Toast.LENGTH_SHORT).show();
                        binding.progress.setVisibility(View.INVISIBLE);
                    }
                });
            }
        });


        //Change the time
        binding.txtTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                //get current time
                tHour = c.get(Calendar.HOUR_OF_DAY);
                tMinute = c.get(Calendar.MINUTE);

                //Lauch the time picker dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(EditEntry.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String format;
                        if (hourOfDay == 0) {
                            hourOfDay += 12;
                            format = "AM";
                        } else if (hourOfDay == 12) {
                            format = "PM";
                        } else if (hourOfDay > 12) {
                            hourOfDay -= 12;
                            format = "PM";
                        } else {
                            format = "AM";
                        }
                        String mdate = hourOfDay + ":" + minute + " " + format;
                        binding.txtTime.setText(mdate);
                    }
                }, tHour, tMinute, false);
                timePickerDialog.show();
            }
        });

        //change the date
        binding.txtDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //get current date
                final Calendar c = Calendar.getInstance();
                dYear = c.get(Calendar.YEAR);
                dMonth = c.get(Calendar.MONTH);
                dDay = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(EditEntry.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                        //set text
                        //format date


                        SimpleDateFormat originalFormat =  new SimpleDateFormat("dd-MM-yyyy", java.util.Locale.getDefault());
                        SimpleDateFormat dateFormat = new SimpleDateFormat("E, dd MMM yyyy", java.util.Locale.getDefault());

                        String origDate = dayOfMonth + "-" + (monthOfYear+1) + "-" + year;
                        Date formattedDate = null;
                        try {
                            formattedDate = originalFormat.parse(origDate);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        binding.txtDate.setText(dateFormat.format(formattedDate));
                    }
                }, dYear, dMonth, dDay);
                datePickerDialog.show();
            }
        });

        binding.btnEmoji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EmojiDialog emojiDialog = new EmojiDialog();
                emojiDialog.show(getSupportFragmentManager(), "choose emoji on adding entry");
            }
        });
    }


    public void checkUserFeeling(){
        switch (userFeeling) {
            case "happy":
                choose_feeling.setImageResource(R.drawable.ic_happy_svg);
                break;
            case "crazy":
                choose_feeling.setImageResource(R.drawable.ic_crazy_svg);
                break;
            case "love":
                choose_feeling.setImageResource(R.drawable.ic_hert_eyes_svg);
                break;
            case "sad":
                choose_feeling.setImageResource(R.drawable.ic_sad_svg);
                break;
            case "sick":
                choose_feeling.setImageResource(R.drawable.ic_sick_svg);
                break;
            case "angry":
                choose_feeling.setImageResource(R.drawable.ic_angry_svg);
                break;
            default:
                choose_feeling.setImageResource(R.drawable.ic_happy_svg);
                break;
        }
    }

    @Override
    public void applyFeeling(String uFeeling) {
        userFeeling = uFeeling;

        checkUserFeeling();


    }

    @Override
    public void onBackPressed() {
        //Did not saved anything so go back to Entry Content
        setResult(RESULT_CANCELED, new Intent());
        finish();
    }


}