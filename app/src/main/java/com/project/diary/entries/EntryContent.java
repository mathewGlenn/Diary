package com.project.diary.entries;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.project.diary.R;
import com.project.diary.databinding.ActivityEntryContentBinding;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EntryContent extends AppCompatActivity {

    Boolean entryIsFavorite;

    Intent data;
    FirebaseFirestore firestore;
    FirebaseUser user;

    private ActivityEntryContentBinding binding;

    //if the user go to edit and did not do anything, he will go back to EntryContent
    //if the user edited something, he will go back to EntriesList
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEntryContentBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        firestore = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        data = getIntent();

        String date = data.getStringExtra("date");
        String userFeeling = data.getStringExtra("feeling");
        String imgLink = data.getStringExtra("imgLink");
        String imgName = data.getStringExtra("imgName");
        entryIsFavorite = data.getBooleanExtra("isFavorite", false);

        ArrayList<String> entryTags = data.getStringArrayListExtra("tags");
        setTag(entryTags);
        DateFormat inputFormat = new SimpleDateFormat("E, dd MMM yyyy h:mm a");

        Date d_date = null;
        try {
            d_date = inputFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        DateFormat dateOnly = new SimpleDateFormat("E, dd MMM yyyy");
        DateFormat timeOnly = new SimpleDateFormat("h:mm a");

        binding.entryTitle.setText(data.getStringExtra("title"));
        binding.entryContent.setText(data.getStringExtra("content"));
        binding.txtDate.setText(dateOnly.format(d_date));
        binding.txtTime.setText(timeOnly.format(d_date));

        switch (userFeeling) {
            case "happy":
                binding.imgEmoji.setImageResource(R.drawable.ic_happy_svg);
                break;
            case "crazy":
                binding.imgEmoji.setImageResource(R.drawable.ic_crazy_svg);
                break;
            case "love":
                binding.imgEmoji.setImageResource(R.drawable.ic_hert_eyes_svg);
                break;
            case "sad":
                binding.imgEmoji.setImageResource(R.drawable.ic_sad_svg);
                break;
            case "sick":
                binding.imgEmoji.setImageResource(R.drawable.ic_sick_svg);
                break;
            case "angry":
                binding.imgEmoji.setImageResource(R.drawable.ic_angry_svg);
                break;
            default:
                binding.imgEmoji.setVisibility(View.INVISIBLE);
        }

       //  Glide for displaying image if there is one
        if (imgLink != null){
            binding.img.setVisibility(View.VISIBLE);
            Glide.with(this).load(imgLink).into(binding.img);

            DrawableCompat.setTint(binding.btnBack.getDrawable(), ContextCompat.getColor(getApplicationContext(), R.color.white2));
            DrawableCompat.setTint(binding.btnDelete.getDrawable(), ContextCompat.getColor(getApplicationContext(), R.color.white2));
            DrawableCompat.setTint(binding.btnEdit.getDrawable(), ContextCompat.getColor(getApplicationContext(), R.color.white2));
        }else {
            setMargins(binding.scrollView, 0,90,0,0);
        }

//        if (imgLink != null){
//            binding.loading.setVisibility(View.VISIBLE);
//            Glide.with(this)
//                    .load(imgLink)
//                    .listener(new RequestListener<Drawable>() {
//                        @Override
//                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
//                            binding.lottieLoading.setVisibility(View.INVISIBLE);
//                            binding.loadingText.setText("Failed loading image resource. Check your internet connection.");
//                            return false;
//                        }
//
//                        @Override
//                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
//                            binding.loading.setVisibility(View.GONE);
//                            binding.img.setVisibility(View.VISIBLE);
//                            return false;
//                        }
//                    })
//                    .into(binding.img);
//        }




        binding.btnBack.setOnClickListener(v -> finish());

        binding.btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), EditEntry.class);
            intent.putExtra("title", data.getStringExtra("title"));
            intent.putExtra("content", data.getStringExtra("content"));
            intent.putExtra("dateOnly", binding.txtDate.getText().toString());
            intent.putExtra("timeOnly", binding.txtTime.getText().toString());
            intent.putExtra("feeling", data.getStringExtra("feeling"));
            intent.putStringArrayListExtra("tags", entryTags);
            intent.putExtra("entryID", data.getStringExtra("entryID"));
            intent.putExtra("isFavorite", entryIsFavorite);
            intent.putExtra("imgLink", imgLink);
            intent.putExtra("imgName", imgName);
            startActivityForResult(intent, 1);
        });


        binding.btnDelete.setOnClickListener(v -> {
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(EntryContent.this);
            alertBuilder.setTitle("Confirm deletion");
            alertBuilder.setMessage("Are you sure to delete this entry? It can not be undone.")
                    .setCancelable(true)
                    .setPositiveButton("Yes", (dialog, which) -> {
                        DocumentReference reference = firestore.collection("allEntries").document(user.getUid()).collection("userEntries").document(data.getStringExtra("entryID"));
                        reference.delete().addOnSuccessListener(aVoid -> {
                            onBackPressed();
                            Toast.makeText(EntryContent.this, "Entry deleted", Toast.LENGTH_SHORT).show();
                        }).addOnFailureListener(e -> Toast.makeText(EntryContent.this, "Error deleting entry", Toast.LENGTH_SHORT).show());
                    }).setNegativeButton("No", (dialog, which) -> dialog.cancel());
            alertBuilder.show();
        });


        if (entryIsFavorite) {
            binding.btnFavorite.setImageResource(R.drawable.ic_heart_red_1);
        } else {
            binding.btnFavorite.setVisibility(View.INVISIBLE);
        }

        binding.tfeel.setText(imgLink);

        //end of onCreate

    }

    private void setTag(final List<String> tagList) {
        final ChipGroup chipGroup = binding.chipGroup;
        for (int index = 0; index < tagList.size(); index++) {
            final String tagName = tagList.get(index);
            final Chip chip = new Chip(this);
            int paddingDp = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, 10,
                    getResources().getDisplayMetrics()
            );
            chip.setPadding(paddingDp, paddingDp, paddingDp, paddingDp);
            chip.setText(tagName);
            chip.setChipBackgroundColorResource(R.color.black);
            chip.setTextColor(getResources().getColor(R.color.white));
            chip.setEnsureMinTouchTargetSize(false);

            chipGroup.addView(chip);
        }
    }

    public static void setMargins (View v, int l, int t, int r, int b) {
        if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            p.setMargins(l, t, r, b);
            v.requestLayout();
        }
    }


}