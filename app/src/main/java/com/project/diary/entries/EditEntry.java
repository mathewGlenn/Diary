package com.project.diary.entries;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.project.diary.databinding.ActivityEditEntryBinding;

import java.util.HashMap;
import java.util.Map;

public class EditEntry extends AppCompatActivity {

    FirebaseFirestore firestore;
    Intent data;
    FirebaseUser user;
    private ActivityEditEntryBinding binding;
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
        String date = data.getStringExtra("date");
        String time = data.getStringExtra("time");

        binding.entryTitle.setText(title);
        binding.entryContent.setText(content);
        binding.txtTime.setText(time);
        binding.txtDate.setText(date);

        binding.btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String editTitle = binding.entryTitle.getText().toString();
                String editContent = binding.entryContent.getText().toString();

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

                reference.update(entry).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(EditEntry.this, "Update Successful", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), EntriesList.class));
                        binding.progress.setVisibility(View.INVISIBLE);
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


    }
}