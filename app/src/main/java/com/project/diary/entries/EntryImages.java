package com.project.diary.entries;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.project.diary.R;
import com.project.diary.model.Entry;

public class EntryImages extends AppCompatActivity {
    ImageView img;
    RecyclerView recyclerViewImages;
    boolean linearLayout;

    FirebaseFirestore firestore;
    FirestoreRecyclerAdapter<Entry, EntryImages.NoteViewHolder> imgAdapter;
    FirebaseUser user;
    FirebaseAuth auth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_images);

        SharedPreferences preferences = this.getSharedPreferences("LayoutPref", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        linearLayout = preferences.getBoolean("linearLayout", true);

        recyclerViewImages = findViewById(R.id.all_img_recycler);

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        Query query = firestore.collection("allEntries").document(user.getUid()).collection("userEntries").whereNotEqualTo("image_link", null).orderBy("image_link").orderBy("date", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<Entry> allImages = new FirestoreRecyclerOptions.Builder<Entry>()
                .setQuery(query, Entry.class)
                .build();

        imgAdapter = new FirestoreRecyclerAdapter<Entry, NoteViewHolder>(allImages) {
            @Override
            protected void onBindViewHolder(@NonNull EntryImages.NoteViewHolder noteViewHolder, int i, @NonNull Entry entry) {
                String imgLink = entry.getImage_link();

                Glide.with(EntryImages.this).load(imgLink).into(noteViewHolder.imageView);
                //noteViewHolder.txt.setText(entry.getImage_name());

                noteViewHolder.view.setOnClickListener(v->{
                    Intent intent = new Intent(v.getContext(),ViewImage.class);

                    intent.putExtra("imgLink", imgLink);
                    intent.putExtra("date", entry.getDate());
                    startActivity(intent);

                });

            }

            @NonNull
            @Override
            public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_images_view, parent, false);
                return new NoteViewHolder(view);
            }
        };


        if (linearLayout){
            recyclerViewImages.setLayoutManager(new LinearLayoutManager(this));
        }else {
            recyclerViewImages.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
        }

        ImageButton changeLayout = findViewById(R.id.btn_changeLayout);
        changeLayout.setOnClickListener(v->{
            if (linearLayout){
                editor.putBoolean("linearLayout", false);
                editor.commit();
                changeLayout.setImageResource(R.drawable.ic_linear);
                Intent intent = getIntent();
                finish();
                startActivity(intent);
            }else{
                editor.putBoolean("linearLayout", true);
                editor.commit();
                changeLayout.setImageResource(R.drawable.ic_baseline_grid_on_24);
                Intent intent = getIntent();
                finish();
                startActivity(intent);
            }
        });

        recyclerViewImages.setAdapter(imgAdapter);
    }
    public class NoteViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
       // TextView txt;
        View view;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imgAll);
           // txt = itemView.findViewById(R.id.txtimg);


            view = itemView;
        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        imgAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        imgAdapter.stopListening();
    }
}