package com.project.diary.entries;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.project.diary.R;
import com.project.diary.databinding.ActivityTagFilteredEntryBinding;
import com.project.diary.model.Entry;

import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TagFilteredEntry extends AppCompatActivity {
    ActivityTagFilteredEntryBinding binding;

    RecyclerView tagFilteredEntries;
    FirebaseFirestore firestore;
    FirestoreRecyclerAdapter<Entry, NoteViewHolder> entryAdapter;
    FirebaseUser user;
    FirebaseAuth auth;
    List<String> entryTags;
    Intent data;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTagFilteredEntryBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        data = getIntent();
        String tagName = data.getStringExtra("tag_name");

        binding.txtTagName.setText(tagName);


        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        assert tagName != null;
        Query query = firestore.collection("allEntries").document(user.getUid()).collection("userEntries").whereArrayContains("tags", tagName).orderBy("date", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<Entry> allFilteredEntries = new FirestoreRecyclerOptions.Builder<Entry>()
                .setQuery(query, Entry.class)
                .build();

        entryAdapter = new FirestoreRecyclerAdapter<Entry, NoteViewHolder>(allFilteredEntries) {
            @Override
            protected void onBindViewHolder(@NonNull @NotNull TagFilteredEntry.NoteViewHolder noteViewHolder, int i, @NonNull @NotNull Entry entry) {
                String userFeeling = entry.getFeeling();
                entryTags = entry.getTags();

                SimpleDateFormat dateFormat;

                Boolean isFavorite = entry.getFavorite();

                dateFormat = new SimpleDateFormat("E, dd MMM yyyy h:mm a");

                String date = entry.getDate();

                try {

                    Date d_date = dateFormat.parse(date);

                    DateFormat dateOnly = new SimpleDateFormat("dd");
                    DateFormat dayOnly = new SimpleDateFormat("EEE");
                    DateFormat monthOnly = new SimpleDateFormat("MMM");

                    noteViewHolder.entryDate.setText(dateOnly.format(d_date));
                    noteViewHolder.entryDay.setText(dayOnly.format(d_date));
                    noteViewHolder.entryMonth.setText(monthOnly.format(d_date));

                } catch (ParseException e) {
                    e.printStackTrace();
                }

                noteViewHolder.entryTitle.setText(entry.getTitle());
                noteViewHolder.entryContent.setText(entry.getContent());

                //setting feeling emoji
                switch (userFeeling) {
                    case "":
                        noteViewHolder.entryFeeling.setVisibility(View.INVISIBLE);
                        break;
                    case "happy":
                        noteViewHolder.entryFeeling.setImageResource(R.drawable.ic_happy_svg);
                        break;
                    case "crazy":
                        noteViewHolder.entryFeeling.setImageResource(R.drawable.ic_crazy_svg);
                        break;
                    case "love":
                        noteViewHolder.entryFeeling.setImageResource(R.drawable.ic_hert_eyes_svg);
                        break;
                    case "sad":
                        noteViewHolder.entryFeeling.setImageResource(R.drawable.ic_sad_svg);
                        break;
                    case "sick":
                        noteViewHolder.entryFeeling.setImageResource(R.drawable.ic_sick_svg);
                        break;
                    case "angry":
                        noteViewHolder.entryFeeling.setImageResource(R.drawable.ic_angry_svg);
                        break;
                    default:
                        noteViewHolder.entryFeeling.setVisibility(View.INVISIBLE);
                }
                //get id of entry to be used in updating and deleting
                String docID = entryAdapter.getSnapshots().getSnapshot(i).getId();
                ArrayList<String> arrLisTags = new ArrayList<>(entryTags);

                // When clicking an Entry from the RecylerView entry list
                noteViewHolder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(v.getContext(), EntryContent.class);
                        //get the title, content, date, etc to view in the EntryContent.class
                        i.putExtra("title", entry.getTitle());
                        i.putExtra("content", entry.getContent());
                        i.putExtra("date", entry.getDate());
                        i.putExtra("feeling", entry.getFeeling());
                        i.putStringArrayListExtra("tags", arrLisTags);
                        i.putExtra("entryID", docID);
                        i.putExtra("isFavorite", isFavorite);
                        startActivity(i);
                    }
                });
            }

            @NonNull
            @NotNull
            @Override
            public NoteViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.entry_view, parent, false);
                return new NoteViewHolder(view);
            }
            @Override
            public void onDataChanged() {
                if (getItemCount() == 0){
                    binding.lottieNoEntryYet.setVisibility(View.VISIBLE);
                    binding.txtNoFavEntry.setVisibility(View.VISIBLE);
                }else {
                    binding.lottieNoEntryYet.setVisibility(View.INVISIBLE);
                    binding.txtNoFavEntry.setVisibility(View.INVISIBLE);
                }
            }
        };

        tagFilteredEntries = binding.entrieslist;
        tagFilteredEntries.setLayoutManager(new LinearLayoutManager(this));
        tagFilteredEntries.setAdapter(entryAdapter);

        binding.btnBack.setOnClickListener(v -> finish());
    }


    public class NoteViewHolder extends RecyclerView.ViewHolder {
        TextView entryTitle, entryContent, entryDate, entryDay, entryMonth;
        ImageView entryFeeling;
        View view;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);

            entryTitle = itemView.findViewById(R.id.title);
            entryContent = itemView.findViewById(R.id.content);
            entryDate = itemView.findViewById(R.id.date);
            entryDay = itemView.findViewById(R.id.day);
            entryMonth = itemView.findViewById(R.id.month);
            entryFeeling = itemView.findViewById(R.id.imgFeeling);


            view = itemView;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        entryAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        entryAdapter.stopListening();
    }
}