package com.project.diary.entries;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.project.diary.EmojiDialog;
import com.project.diary.R;
import com.project.diary.databinding.ActivityEditEntryBinding;
import com.project.diary.model.Entry;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EditEntry extends AppCompatActivity implements EmojiDialog.EmojiDialogListener {
    //for date time picker
    private int dYear, dMonth, dDay, tHour, tMinute;
    FirebaseFirestore firestore;
    Intent data;
    FirebaseUser user;
    private ActivityEditEntryBinding binding;

    ImageButton choose_feeling, imgFavorite;
    String userFeeling = "";
    Boolean entryIsFavorite;

    List<String> entryTags;
    String[] mTags = {"Happy", "Travel", "Nature", "School"};



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
        entryIsFavorite = data.getBooleanExtra("isFavorite", false);
        entryTags = data.getStringArrayListExtra("tags");

        choose_feeling = binding.btnEmoji;
        imgFavorite = binding.btnFavorite;

        binding.entryTitle.setText(title);
        binding.entryContent.setText(content);
        binding.txtTime.setText(time);
        binding.txtDate.setText(date);
        setTag(entryTags);
        checkUserFeeling();

        binding.btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String editTitle = binding.entryTitle.getText().toString();
                String editContent = binding.entryContent.getText().toString();
                String editDate = binding.txtDate.getText().toString();
                String editTime = binding.txtTime.getText().toString();
                String editDateTime = editDate.concat(" " + editTime);

                if (editContent.isEmpty()) {
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
                entry.put("tags", entryTags);
                entry.put("favorite", entryIsFavorite);


               // Entry entry = new Entry(editTitle, editContent,editDateTime,userFeeling,entryTags,entryIsFavorite);


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


                        SimpleDateFormat originalFormat = new SimpleDateFormat("dd-MM-yyyy", java.util.Locale.getDefault());
                        SimpleDateFormat dateFormat = new SimpleDateFormat("E, dd MMM yyyy", java.util.Locale.getDefault());

                        String origDate = dayOfMonth + "-" + (monthOfYear + 1) + "-" + year;
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

        imgFavorite = binding.btnFavorite;
        if (entryIsFavorite) {
            imgFavorite.setImageResource(R.drawable.ic_baseline_star_on_24);
            imgFavorite.setTag(2);
        } else {
            imgFavorite.setImageResource(R.drawable.ic_baseline_star_off_24);
            imgFavorite.setTag(1);
        }

        binding.btnTags.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddTagDialog();
            }
        });
        //end of onCreate
    }


    public void checkUserFeeling() {
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


    public void isEntryFavorite(View view) {
        if (imgFavorite.getTag().equals(1)) {
            imgFavorite.setImageResource(R.drawable.ic_baseline_star_on_24);
            entryIsFavorite = true;
            imgFavorite.setTag(2);
        } else {
            imgFavorite.setImageResource(R.drawable.ic_baseline_star_off_24);
            entryIsFavorite = false;
            imgFavorite.setTag(1);
        }
    }

    //Show tags in chips
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
                    entryTags.remove(tagName);
                    chipGroup.removeView(chip);
                }
            });

            chipGroup.addView(chip);
        }
    }

    protected void showAddTagDialog(){
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View view = layoutInflater.inflate(R.layout.add_tag_layout, null);
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, mTags);
        final AutoCompleteTextView textViewTag = view.findViewById(R.id.autoCompleteAddTag);
        textViewTag.setAdapter(adapter);

        builder.setCancelable(false)
                .setTitle("Add Tags")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        binding.chipGroup.removeAllViews();
                        entryTags.add(textViewTag.getText().toString());
                        setTag(entryTags);
                    }
                }).setNeutralButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}