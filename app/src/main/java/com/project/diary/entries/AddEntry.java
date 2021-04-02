package com.project.diary.entries;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.project.diary.databinding.ActivityAddEntryBinding;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


public class AddEntry extends AppCompatActivity {

    String date, time;
    FirebaseFirestore firestore;
    FirebaseUser user;
    private ActivityAddEntryBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddEntryBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        firestore = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();


        Calendar calendar;
        SimpleDateFormat dateFormat, timeFormat;

        calendar = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("MM-dd-yyyy");
        timeFormat = new SimpleDateFormat("h:mm a");


        date = dateFormat.format(calendar.getTime());
        time = timeFormat.format(calendar.getTime());


        binding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                String entry_title, entry_content;
                entry_title = binding.entryTitle.getText().toString();
                entry_content = binding.entryContent.getText().toString();


                if (entry_content.isEmpty()) {
                    Toast.makeText(AddEntry.this, "No content", Toast.LENGTH_SHORT).show();
                    return;
                }

                //save note to firestore
                //notes collection >> multiple notes

                binding.progress.setVisibility(View.VISIBLE);

                DocumentReference reference = firestore.collection("allEntries").document(user.getUid()).collection("userEntries").document();
                Map<String, Object> entry = new HashMap<>();
                entry.put("title", entry_title);
                entry.put("content", entry_content);
                entry.put("date", date);
                entry.put("time", time);

                reference.set(entry).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(), "Save successful", Toast.LENGTH_LONG).show();
                        onBackPressed();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Save failed", Toast.LENGTH_LONG).show();
                        binding.progress.setVisibility(View.INVISIBLE);
                    }
                });
            }
        });
    }
}