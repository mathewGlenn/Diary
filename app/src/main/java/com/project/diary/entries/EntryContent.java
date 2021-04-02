package com.project.diary.entries;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
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
import com.project.diary.databinding.ActivityEntryContentBinding;

public class EntryContent extends AppCompatActivity {

    Intent data;
    FirebaseFirestore firestore;
    FirebaseUser user;

    private ActivityEntryContentBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEntryContentBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        firestore = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        data = getIntent();

        binding.entryTitle.setText(data.getStringExtra("title"));
        binding.entryContent.setText(data.getStringExtra("content"));
        binding.txtDate.setText(data.getStringExtra("date"));
        binding.txtTime.setText(data.getStringExtra("time"));

        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        binding.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), EditEntry.class);
                intent.putExtra("title", data.getStringExtra("title"));
                intent.putExtra("content", data.getStringExtra("content"));
                intent.putExtra("date", data.getStringExtra("date"));
                intent.putExtra("time", data.getStringExtra("time"));
                intent.putExtra("entryID",data.getStringExtra("entryID"));
                startActivity(intent);
            }
        });

        binding.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(EntryContent.this);
                alertBuilder.setTitle("Confirm deletion");
                alertBuilder.setMessage("Are you sure to delete this entry? It can not be undone.")
                        .setCancelable(true)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                DocumentReference reference = firestore.collection("allEntries").document(user.getUid()).collection("userEntries").document(data.getStringExtra("entryID"));
                                reference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        onBackPressed();
                                        Toast.makeText(EntryContent.this, "Entry deleted", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(EntryContent.this, "Error deleting entry", Toast.LENGTH_SHORT).show();

                                    }
                                });
                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                alertBuilder.show();
            }
        });




    }
}