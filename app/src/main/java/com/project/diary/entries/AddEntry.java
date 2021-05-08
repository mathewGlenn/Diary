package com.project.diary.entries;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.project.diary.EmojiDialog;
import com.project.diary.R;
import com.project.diary.databinding.ActivityAddEntryBinding;
import com.project.diary.model.Entry;

import java.io.IOException;
import java.net.URI;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class AddEntry extends AppCompatActivity implements EmojiDialog.EmojiDialogListener {

    private Uri filePath;
    private final int PICK_IMAGE_REQUEST = 71;

    // Firebase
    FirebaseStorage storage;
    StorageReference storageReference;

    List<String> arrListTags = new ArrayList<>();
    String imgLink;
    String imageName;

    ImageButton imgFavorite;
    ImageButton choose_feeling;
    //for emoji
    String userFeeling = "happy";

    //for date time picker
    private int dYear, dMonth, dDay, tHour, tMinute;
    String[] mTags = {"Happy", "Travel", "Nature", "School"};
    Boolean addEntryAsFavorite = false;

    FirebaseFirestore firestore;
    FirebaseUser user;
    private ActivityAddEntryBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddEntryBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        choose_feeling = binding.btnEmoji;

        firestore = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();


        Calendar calendar;
        SimpleDateFormat dateFormat;

        calendar = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("MMM dd, yyyy  h:mm a", java.util.Locale.getDefault());


        String date = dateFormat.format(calendar.getTime());

        try {
            Date d_date = dateFormat.parse(date);
            DateFormat dateOnly = new SimpleDateFormat("E, dd MMM yyyy", java.util.Locale.getDefault());
            DateFormat timeOnly = new SimpleDateFormat("h:mm a", java.util.Locale.getDefault());

            binding.txtTime.setText(timeOnly.format(d_date));
            binding.txtDate.setText(dateOnly.format(d_date));
        } catch (ParseException e) {
            e.printStackTrace();
        }


        binding.btnSave.setOnClickListener(v -> {

            String entry_title, entry_content, entry_date, entry_time, entry_date_time;

            entry_title = binding.entryTitle.getText().toString();
            entry_content = binding.entryContent.getText().toString();
            entry_date = binding.txtDate.getText().toString();
            entry_time = binding.txtTime.getText().toString();
            entry_date_time = entry_date.concat(" " + entry_time);


            if (entry_content.isEmpty()) {
                Toast.makeText(AddEntry.this, "No content", Toast.LENGTH_SHORT).show();
                return;
            }

            //save note to firestore
            //notes collection >> multiple notes


            binding.lottieLoading.setVisibility(View.VISIBLE);

            DocumentReference reference = firestore.collection("allEntries").document(user.getUid()).collection("userEntries").document();


            //////////////////////

            if (filePath != null) {

                 imageName = UUID.randomUUID().toString();
                StorageReference storReference = storageReference.child("images/users/" + user.getUid() + "/" + imageName);

                final ProgressDialog progressDialog = new ProgressDialog(this);
                progressDialog.setTitle("Uploading Image...");
                progressDialog.show();


                UploadTask uploadTask = storReference.putFile(filePath);

                uploadTask.addOnProgressListener(snapshot -> {
                    double progress = (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                    progressDialog.setMessage("Uploaded " + (int) progress + "%");
                }).continueWithTask(task -> {
                    if (!task.isSuccessful()){
                        throw  task.getException();
                    }
                    return storReference.getDownloadUrl();
                }).addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        Uri downloadUri = task.getResult();
                        imgLink = downloadUri.toString();

                        Entry entry = new Entry(entry_title, entry_content, entry_date_time, userFeeling, imgLink,imageName, arrListTags, addEntryAsFavorite);

                        reference.set(entry).addOnSuccessListener(aVoid -> {
                            Toast.makeText(getApplicationContext(), "Save successful", Toast.LENGTH_LONG).show();
                            finish();
                        }).addOnFailureListener(e -> {
                            Toast.makeText(getApplicationContext(), "Save failed", Toast.LENGTH_LONG).show();
                            binding.lottieLoading.setVisibility(View.INVISIBLE);
                        });
                    }else {
                        Toast.makeText(AddEntry.this, "An error has occured", Toast.LENGTH_SHORT).show();
                    }
                });



            } else {
                Entry entry = new Entry(entry_title, entry_content, entry_date_time, userFeeling, imgLink, imageName, arrListTags, addEntryAsFavorite);
                reference.set(entry).addOnSuccessListener(aVoid -> {
                    Toast.makeText(getApplicationContext(), "Save successful", Toast.LENGTH_LONG).show();
                    finish();
                }).addOnFailureListener(e -> {
                    Toast.makeText(getApplicationContext(), "Save failed", Toast.LENGTH_LONG).show();
                    binding.lottieLoading.setVisibility(View.INVISIBLE);
                });
            }

            ///////////////////////////


            // Use the custom class to get the title, content, etc


            // then save to firestore

        });

        //Change the time
        binding.txtTime.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            //get current time
            tHour = c.get(Calendar.HOUR_OF_DAY);
            tMinute = c.get(Calendar.MINUTE);

            //Lauch the time picker dialog
            TimePickerDialog timePickerDialog = new TimePickerDialog(AddEntry.this, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view1, int hourOfDay, int minute) {
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

                DatePickerDialog datePickerDialog = new DatePickerDialog(AddEntry.this, new DatePickerDialog.OnDateSetListener() {
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

        // Show emoji dialog to change the emoji

        choose_feeling.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EmojiDialog emojiDialog = new EmojiDialog();
                emojiDialog.show(getSupportFragmentManager(), "choose emoji on adding entry");
            }
        });

        // show emoji dialog when the activity starts
        EmojiDialog emojiDialog = new EmojiDialog();
        emojiDialog.show(getSupportFragmentManager(), "choose emoji on adding entry");


        //add tags
        binding.btnTags.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddTagDialog();
            }
        });


        // toggle star on/off if user want to save entry as favorite
        imgFavorite = binding.btnFavorite;
        imgFavorite.setTag(1);

        binding.btnImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();

            }
        });

        binding.img.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                PopupMenu popupMenu = new PopupMenu(AddEntry.this, binding.img);
                popupMenu.getMenuInflater().inflate(R.menu.image_options, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getItemId() == R.id.removeImage){
                            binding.img.setVisibility(View.GONE);
                            filePath = null;
                        }
                        else if (item.getItemId() == R.id.changeImage){
                            chooseImage();
                        }

                        return true;
                    }
                });
                popupMenu.show();
                return  true;
            }
        });

        //end of onCreate
    }

    @Override
    public void applyFeeling(String uFeeling) {
        userFeeling = uFeeling;

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

    protected void showAddTagDialog() {
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
                        binding.chipGroupAddEntry.removeAllViews();
                        arrListTags.add(textViewTag.getText().toString());
                        setTag(arrListTags);
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


    //Show tags in chips
    private void setTag(final List<String> tagList) {
        final ChipGroup chipGroup = findViewById(R.id.chipGroupAddEntry);
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
                    arrListTags.remove(tagName);
                    chipGroup.removeView(chip);
                }
            });

            chipGroup.addView(chip);
        }
    }

    public void isEntryFavorite(View view) {

        if (imgFavorite.getTag().equals(1)) {
            imgFavorite.setImageResource(R.drawable.ic_heart_red_1);
            addEntryAsFavorite = true;
            imgFavorite.setTag(2);
        } else {
            imgFavorite.setImageResource(R.drawable.ic_heart_gray_1);
            addEntryAsFavorite = false;
            imgFavorite.setTag(1);
        }
    }



    // Fire Storage

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                binding.img.setVisibility(View.VISIBLE);
                binding.img.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);

    }
}