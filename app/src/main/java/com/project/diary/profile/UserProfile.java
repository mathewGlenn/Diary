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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.protobuf.StringValue;
import com.project.diary.R;
import com.project.diary.databinding.ActivityMeBinding;
import com.project.diary.entries.EntryImages;
import com.project.diary.entries.FeelingFilteredEntries;
import com.project.diary.entries.ViewImage;
import com.project.diary.model.Feelings;
import com.project.diary.model.Profile;

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
        ArrayList<String> allUniqueTags = data.getStringArrayListExtra("unique_tags");
       if (allUniqueTags != null){
           setTag(allUniqueTags);
           String unique_tag_count = String.valueOf(allUniqueTags.size());
           binding.showNumTags.setText(unique_tag_count);
       }else
           binding.noTagsYet.setVisibility(View.VISIBLE);


        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        user = firebaseAuth.getCurrentUser();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
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


                   binding.percentHappy.setText(happy + " %");
                   binding.percentCrazy.setText(crazy + " %");
                   binding.percentLove.setText(love + " %");
                   binding.percentSad.setText(sad + " %");
                   binding.percentSick.setText(sick + " %");
                   binding.percentAngry.setText(angry + " %");
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

            chipGroup.addView(chip);
        }
    }
}