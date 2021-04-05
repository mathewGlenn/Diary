package com.project.diary.entries;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.project.diary.R;
import com.project.diary.databinding.ActivityEntryContentBinding;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EntryContent extends AppCompatActivity {

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
                intent.putExtra("dateOnly", binding.txtDate.getText().toString());
                intent.putExtra("timeOnly", binding.txtTime.getText().toString());
                intent.putExtra("feeling", data.getStringExtra("feeling"));
                intent.putExtra("entryID", data.getStringExtra("entryID"));
                startActivityForResult(intent, 1);
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

        binding.tfeel.setText(data.getStringExtra("feeling"));


    }
}