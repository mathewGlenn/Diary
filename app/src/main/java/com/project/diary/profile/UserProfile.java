package com.project.diary.profile;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.protobuf.StringValue;
import com.project.diary.R;
import com.project.diary.databinding.ActivityMeBinding;
import com.project.diary.entries.EntryImages;
import com.project.diary.entries.FeelingFilteredEntries;
import com.project.diary.entries.TagFilteredEntry;
import com.project.diary.entries.ViewImage;
import com.project.diary.model.Feelings;
import com.project.diary.model.Profile;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class UserProfile extends AppCompatActivity {
    FirebaseStorage storage;
    StorageReference storageReference;
    FirebaseAuth firebaseAuth;
    DocumentReference reference, counterReference;
    FirebaseUser user;
    FirebaseFirestore firestore;
    String profile_name, fave_quote, profile_img_link;
    int happy, crazy, love, sad, sick, angry;
    ActivityMeBinding binding;
    Intent data;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMeBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        data = getIntent();

        int count_all_entries = data.getIntExtra("count_all_entries", 0);
        binding.countAllEntries.setText(String.valueOf(count_all_entries));

        ArrayList<String> allUniqueTags = data.getStringArrayListExtra("unique_tags");
       if (allUniqueTags != null){
           setTag(allUniqueTags);
       }else
           binding.noTagsYet.setVisibility(View.VISIBLE);


        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        user = firebaseAuth.getCurrentUser();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        Query favEntriesReference = firestore.collection("allEntries").document(user.getUid()).collection("userEntries").whereEqualTo("favorite", true);
        favEntriesReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    if (task.getResult() != null) {
                        int count = task.getResult().size();
                        binding.countFavEntries.setText(String.valueOf(count));
                    }
                }
            }
        });

        reference = firestore.collection("allEntries").document(user.getUid()).collection("userProfile").document("profile");

        reference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                if (task.getResult().exists()){
                    Profile profile = task.getResult().toObject(Profile.class);
                    profile_name = profile.getProfile_name();
                    fave_quote = profile.getFav_quote();
                    profile_img_link = profile.getProfile_img_link();
                    binding.profileName.setText( profile_name );
                    binding.favQuote.setText("\" " +fave_quote + " \"");
                    Glide.with(this).load(profile_img_link).into(binding.imgProfilePic);
                }
            }else {
                Log.w("get profile", "error", task.getException());
            }
        });

        counterReference = firestore.collection("allEntries").document(user.getUid()).collection("counters").document("feeling_counters");

        counterReference.get().addOnCompleteListener(task->{
           if (task.isSuccessful()){
               if (task.getResult().exists()){
                   Feelings feelings = task.getResult().toObject(Feelings.class);
                   happy = feelings.getHappy();
                   crazy = feelings.getCrazy();
                   love = feelings.getLove();
                   sad = feelings.getSad();
                   sick = feelings.getSick();
                   angry = feelings.getAngry();

                   int total = happy + crazy + love + sad + sick + angry;
                   double p_happy = Math.round((100*happy) / (double)total);
                   double p_crazy = Math.round((100*crazy) / (double)total);
                   double p_love = Math.round((100*love) / (double)total);
                   double p_sad = Math.round((100*sad) / (double)total);
                   double p_sick = Math.round((100*sick) / (double)total);
                   double p_angry = Math.round((100*angry) / (double)total);

                   happy = (int) p_happy;
                   crazy = (int) p_crazy;
                   love = (int) p_love;
                   sad = (int) p_sad;
                   sick = (int) p_sick;
                   angry = (int) p_angry;

                   String dispHappy = happy+" %", dispCrazy = crazy+" %", dispLove = love+" %",
                           dispSad = sad+" %", dispSick = sick+" %", dispAngry = angry+" %";


                   binding.percentHappy.setText(dispHappy);
                   binding.percentCrazy.setText(dispCrazy);
                   binding.percentLove.setText(dispLove);
                   binding.percentSad.setText(dispSad);
                   binding.percentSick.setText(dispSick);
                   binding.percentAngry.setText(dispAngry);

                   binding.countUniqueTags.setText(total);
               }


           }
        });



    }

    public void showAllImages(View view) {
        startActivity(new Intent(this, EntryImages.class));
    }

    public void editProfile(View view) {
        startActivity(new Intent(this, EditProfile.class));
    }

    public void ViewHappyEntries(View view) {
        Intent i = new Intent(this, FeelingFilteredEntries.class);
        i.putExtra("feeling", "happy");
        startActivity(i);
    }

    public void viewCrazyEntries(View view) {
        Intent i = new Intent(this, FeelingFilteredEntries.class);
        i.putExtra("feeling", "crazy");
        startActivity(i);
    }

    public void viewLoveEntries(View view) {
        Intent i = new Intent(this, FeelingFilteredEntries.class);
        i.putExtra("feeling", "love");
        startActivity(i);
    }

    public void viewSadEntries(View view) {
        Intent i = new Intent(this, FeelingFilteredEntries.class);
        i.putExtra("feeling", "sad");
        startActivity(i);
    }

    public void viewSickEntries(View view) {
        Intent i = new Intent(this, FeelingFilteredEntries.class);
        i.putExtra("feeling", "sick");
        startActivity(i);
    }

    public void viewAngryEntries(View view) {
        Intent i = new Intent(this, FeelingFilteredEntries.class);
        i.putExtra("feeling", "angry");
        startActivity(i);
    }
    private void setTag(final List<String> tagList) {
        final ChipGroup chipGroup = binding.uniqueTagsChipGroup;
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

            chip.setOnClickListener(v -> {
                Intent intent = new Intent(this, TagFilteredEntry.class);
                intent.putExtra("tag_name", tagName);
                startActivity(intent);
            });

            chipGroup.addView(chip);
        }
    }
}