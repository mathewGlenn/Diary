package com.project.diary.entries;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import com.bumptech.glide.Glide;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.project.diary.EmojiDialog;
import com.project.diary.R;
import com.project.diary.databinding.ActivityEditEntryBinding;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class EditEntry extends AppCompatActivity implements EmojiDialog.EmojiDialogListener {
    private Uri filePath;
    private final int PICK_IMAGE_REQUEST = 71;

    // Firebase
    FirebaseStorage storage;
    StorageReference storageReference;
    String imgLink, imgName;
    Drawable oldImage;


    //for date time picker
    private int dYear, dMonth, dDay, tHour, tMinute;
    FirebaseFirestore firestore;
    DocumentReference reference, counterReference;
    Intent data;
    FirebaseUser user;
    private ActivityEditEntryBinding binding;

    ImageButton choose_feeling, imgFavorite;
    String userFeeling = "", newUserFeeling, oldUserFeeling;
    Boolean entryIsFavorite;
    String editTitle, editContent, editDate, editTime, editDateTime;


    List<String> entryTags;
    String[] mTags = {"Happy", "Travel", "Nature", "School"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditEntryBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        // hide view
        binding.headColor.setAlpha(0f);
        //
        binding.scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                int maxDistance = binding.img.getHeight();
                int movement = binding.scrollView.getScrollY();
                float alphaFactor = ((movement * 1.0f)/ (maxDistance - binding.headColor.getHeight()));
                if (movement >= 0 && movement <= maxDistance){
                    binding.headColor.setAlpha(alphaFactor);
                }
            }
        });

        oldImage = binding.img.getDrawable();

        data = getIntent();

        firestore = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        reference = firestore.collection("allEntries").document(user.getUid()).collection("userEntries").document(data.getStringExtra("entryID"));


        String title = data.getStringExtra("title");
        String content = data.getStringExtra("content");
        String date = data.getStringExtra("dateOnly");
        String time = data.getStringExtra("timeOnly");
        userFeeling = data.getStringExtra("feeling");
        oldUserFeeling = data.getStringExtra("feeling");
        newUserFeeling = data.getStringExtra("feeling");
        imgLink = data.getStringExtra("imgLink");
        imgName = data.getStringExtra("imgName");
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

        if (imgLink != null) {
            binding.img.setVisibility(View.VISIBLE);
            Glide.with(this).load(imgLink).into(binding.img);
            setButtonsToWhite();
        } else {
            setMargins(binding.scrollView, 0, 90, 0, 0);
        }



        binding.btnUpdate.setOnClickListener(v -> {
             editTitle = binding.entryTitle.getText().toString();
             editContent = binding.entryContent.getText().toString();
             editDate = binding.txtDate.getText().toString();
             editTime = binding.txtTime.getText().toString();
             editDateTime = editDate.concat(" " + editTime);

            if (editContent.isEmpty()) {
                Toast.makeText(EditEntry.this, "Entry content is empty", Toast.LENGTH_SHORT).show();
                return;
            }

            binding.progress.setVisibility(View.VISIBLE);

            //save edited note


            counterReference = firestore.collection("allEntries").document(user.getUid()).collection("counters").document("feeling_counters");

            //

            if (filePath != null) {

                if (binding.img.getDrawable() != oldImage) {
                    StorageReference newImgRef = storageReference.child("images/users/" + user.getUid() + "/" + imgName);

                    final ProgressDialog progressDialog = new ProgressDialog(this);
                    progressDialog.setTitle("Uploading Image...");
                    progressDialog.show();


                    UploadTask uploadTask = newImgRef.putFile(filePath);
                    uploadTask.addOnProgressListener(snapshot -> {
                        double progress = (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                        progressDialog.setMessage("Uploaded " + (int) progress + "%");
                    }).continueWithTask(task->{
                        if (!task.isSuccessful()){
                            throw task.getException();
                        }
                        return newImgRef.getDownloadUrl();
                    }).addOnCompleteListener(task -> {
                        if (task.isSuccessful()){
                            Uri downloadUri = task.getResult();
                            imgLink = downloadUri.toString();

                            updateEntry();
                        }
                    });
                } else {

                updateEntry();

                }
            }
            else{
                updateEntry();
            }


        });


        //Change the time
        binding.txtTime.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            //get current time
            tHour = c.get(Calendar.HOUR_OF_DAY);
            tMinute = c.get(Calendar.MINUTE);

            //Lauch the time picker dialog
            TimePickerDialog timePickerDialog = new TimePickerDialog(EditEntry.this, (view1, hourOfDay, minute) -> {
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
            }, tHour, tMinute, false);
            timePickerDialog.show();
        });

        //change the date
        binding.txtDate.setOnClickListener(v -> {

            //get current date
            final Calendar c = Calendar.getInstance();
            dYear = c.get(Calendar.YEAR);
            dMonth = c.get(Calendar.MONTH);
            dDay = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(EditEntry.this, (view12, year, monthOfYear, dayOfMonth) -> {

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
            }, dYear, dMonth, dDay);
            datePickerDialog.show();
        });

        binding.btnEmoji.setOnClickListener(v -> {
            EmojiDialog emojiDialog = new EmojiDialog();
            emojiDialog.show(getSupportFragmentManager(), "choose emoji on adding entry");
        });

        imgFavorite = binding.btnFavorite;
        if (entryIsFavorite) {
            imgFavorite.setImageResource(R.drawable.ic_heart_red_1);
            imgFavorite.setTag(2);
        } else {
            imgFavorite.setImageResource(R.drawable.ic_heart_gray_1);
            imgFavorite.setTag(1);
        }

        binding.btnTags.setOnClickListener(v -> showAddTagDialog());


        binding.btnImg.setOnClickListener(v -> {
            chooseImage();
        });
        binding.img.setOnLongClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(EditEntry.this, binding.img);
            popupMenu.getMenuInflater().inflate(R.menu.image_options, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.removeImage) {
                    binding.img.setVisibility(View.GONE);
                    imgLink = null;
                    filePath = null;
                    setMargins(binding.scrollView, 0, 90, 0, 0);
                    setButtonsToBlack();
                } else if (item.getItemId() == R.id.changeImage) {
                    chooseImage();

                }
                return false;
            });
            popupMenu.show();

            return true;
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
        newUserFeeling = uFeeling;

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
            imgFavorite.setImageResource(R.drawable.ic_heart_red_1);
            entryIsFavorite = true;
            imgFavorite.setTag(2);
        } else {
            imgFavorite.setImageResource(R.drawable.ic_heart_gray_1);
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
            chip.setOnCloseIconClickListener(v -> {
                tagList.remove(tagName);
                entryTags.remove(tagName);
                chipGroup.removeView(chip);
            });

            chipGroup.addView(chip);
        }
    }

    protected void showAddTagDialog() {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View view = layoutInflater.inflate(R.layout.add_tag_layout, null);
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, mTags);
        final AutoCompleteTextView textViewTag = view.findViewById(R.id.autoCompleteAddTag);
        textViewTag.setAdapter(adapter);

        builder.setCancelable(false)
                .setTitle("Add Tags")
                .setPositiveButton("OK", (dialog, which) -> {
                    binding.chipGroup.removeAllViews();
                    entryTags.add(textViewTag.getText().toString());
                    setTag(entryTags);
                }).setNeutralButton("cancel", (dialog, which) -> dialog.cancel());
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
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

                setMargins(binding.scrollView, 0, 0, 0, 0);
                setButtonsToWhite();
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

    public static void setMargins(View v, int l, int t, int r, int b) {
        if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            p.setMargins(l, t, r, b);
            v.requestLayout();
        }
    }

    public void setButtonsToWhite() {
        DrawableCompat.setTint(binding.btnImg.getDrawable(), ContextCompat.getColor(getApplicationContext(), R.color.white2));
        DrawableCompat.setTint(binding.btnClose.getDrawable(), ContextCompat.getColor(getApplicationContext(), R.color.white2));
        DrawableCompat.setTint(binding.btnVoiceRec.getDrawable(), ContextCompat.getColor(getApplicationContext(), R.color.white2));
        DrawableCompat.setTint(binding.btnTags.getDrawable(), ContextCompat.getColor(getApplicationContext(), R.color.white2));
        binding.btnUpdate.setBackgroundResource(R.drawable.button_white_bg);
        binding.btnUpdate.setTextColor(getResources().getColor(R.color.black));
    }

    public void setButtonsToBlack() {
        DrawableCompat.setTint(binding.btnImg.getDrawable(), ContextCompat.getColor(getApplicationContext(), R.color.black));
        DrawableCompat.setTint(binding.btnClose.getDrawable(), ContextCompat.getColor(getApplicationContext(), R.color.black));
        DrawableCompat.setTint(binding.btnVoiceRec.getDrawable(), ContextCompat.getColor(getApplicationContext(), R.color.black));
        DrawableCompat.setTint(binding.btnTags.getDrawable(), ContextCompat.getColor(getApplicationContext(), R.color.black));
        binding.btnUpdate.setBackgroundResource(R.drawable.button_black_bg);
        binding.btnUpdate.setTextColor(getResources().getColor(R.color.white));
    }

    public void deleteImage() {

    }
    public void updateEntry(){
        Map<String, Object> entry = new HashMap<>();
        entry.put("title", editTitle);
        entry.put("content", editContent);
        entry.put("date", editDateTime);
        entry.put("feeling", userFeeling);
        entry.put("image_link", imgLink);
        entry.put("tags", entryTags);
        entry.put("favorite", entryIsFavorite);

        reference.update(entry).addOnSuccessListener(aVoid -> {
            Toast.makeText(EditEntry.this, "Update Successful", Toast.LENGTH_SHORT).show();
            binding.progress.setVisibility(View.INVISIBLE);
            //finishing Entry Content and going directly to Entries List
            setResult(RESULT_OK, new Intent());
            finish();
        }).addOnFailureListener(e -> {
            Toast.makeText(EditEntry.this, "Failed updating entries", Toast.LENGTH_SHORT).show();
            binding.progress.setVisibility(View.INVISIBLE);
        });

        updateCounters();
    }

    public void updateCounters(){
        if (!oldUserFeeling.equals(newUserFeeling)){
            counterReference.update(oldUserFeeling, FieldValue.increment(-1));
            counterReference.update(newUserFeeling, FieldValue.increment(1));
        }
    }


}