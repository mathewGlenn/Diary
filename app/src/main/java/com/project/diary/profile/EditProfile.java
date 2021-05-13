package com.project.diary.profile;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.project.diary.R;
import com.project.diary.databinding.ActivityEditProfileBinding;
import com.project.diary.model.Entry;
import com.project.diary.model.Profile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EditProfile extends AppCompatActivity {
    ActivityEditProfileBinding binding;
    private Uri filePath;
    private final int PICK_IMAGE_REQUEST = 71;

    FirebaseStorage storage;
    StorageReference storageReference;
    FirebaseAuth firebaseAuth;
    CollectionReference reference;
    FirebaseUser user;
    FirebaseFirestore firestore;
    String imgLink;
    String profile_name, fave_quote, profile_img_link;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditProfileBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        user = firebaseAuth.getCurrentUser();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        reference = firestore.collection("allEntries").document(user.getUid()).collection("userProfile");


        binding.imgProfilePic.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
        });

        binding.saveProfile.setOnClickListener(v -> {
            profile_name = binding.editName.getText().toString();
            fave_quote = binding.editQuote.getText().toString();


            String imageName = UUID.randomUUID().toString();

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
            }).addOnCompleteListener(task->{
                if (task.isSuccessful()){
                    Uri downloadUri = task.getResult();
                    imgLink = downloadUri.toString();

                    reference.document("profile").delete();

                    Profile profile = new Profile(profile_name, imgLink, fave_quote);

                    reference.document("profile").set(profile);

                    finish();

                }else {
                    reference.document("profile").delete();

                    Profile profile = new Profile(profile_name, imgLink, fave_quote);

                    reference.document("profile").set(profile);
                    finish();
                }
            });
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                binding.imgProfilePic.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void uploadImage(View view) {

        FirebaseUser user = firebaseAuth.getCurrentUser();
        String userID = user.getUid();

        if (filePath != null) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();


            StorageReference reference = storageReference.child("images/users/" + userID + "/" + UUID.randomUUID().toString());
            reference.putFile(filePath).addOnSuccessListener(taskSnapshot -> {
                progressDialog.dismiss();
                Toast.makeText(this, "Uploaded", Toast.LENGTH_SHORT).show();

            })
                    .addOnFailureListener(e -> {
                        progressDialog.dismiss();
                        Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show();
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                    double progress = (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                    progressDialog.setMessage("Uploaded " + (int) progress + "%");
                }
            });
        }
    }
}