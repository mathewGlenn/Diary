package com.project.diary.entries;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.project.diary.R;
import com.project.diary.authentication.Login;
import com.project.diary.authentication.Register;
import com.project.diary.databinding.ActivityMainBinding;
import com.project.diary.model.Entry;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class EntriesList extends AppCompatActivity {

    //for the nav_view
    private DrawerLayout drawerLayout;
    //

    private ActivityMainBinding binding;

    RecyclerView entriesList;

    FirebaseFirestore firestore;
    FirestoreRecyclerAdapter<Entry, NoteViewHolder> entryAdapter;
    FirebaseUser user;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        firestore = FirebaseFirestore.getInstance();

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        Query query = firestore.collection("allEntries").document(user.getUid()).collection("userEntries").orderBy("date", Query.Direction.DESCENDING);

        // Query notes > uid > mynotes > allnotes


        FirestoreRecyclerOptions<Entry> allentries = new FirestoreRecyclerOptions.Builder<Entry>()
                .setQuery(query, Entry.class)
                .build();

        entryAdapter = new FirestoreRecyclerAdapter<Entry, NoteViewHolder>(allentries) {
            @Override
            protected void onBindViewHolder(@NonNull NoteViewHolder noteViewHolder, int i, @NonNull Entry entry) {

                SimpleDateFormat dateFormat, timeFormat;

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
                String docID = entryAdapter.getSnapshots().getSnapshot(i).getId();

                // When clicking an Entry from the RecylerView entry list
                noteViewHolder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(v.getContext(), EntryContent.class);
                        //get the title, content, date, etc to view in the EntryContent.class
                        i.putExtra("title", entry.getTitle());
                        i.putExtra("content", entry.getContent());
                        i.putExtra("date", entry.getDate());
                        i.putExtra("entryID", docID);
                        startActivity(i);
                    }
                });
            }

            @NonNull
            @Override
            public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.entry_view, parent, false);

                return new NoteViewHolder(view);
            }
        };


        entriesList = binding.entrieslist;

        entriesList.setLayoutManager(new LinearLayoutManager(this));
        entriesList.setAdapter(entryAdapter);


        binding.addEntry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), AddEntry.class));
            }
        });



        //for the nav
        drawerLayout = binding.draweLayout;


        binding.sideMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!drawerLayout.isDrawerOpen(GravityCompat.START))
                    drawerLayout.openDrawer(GravityCompat.START);
                else
                    drawerLayout.closeDrawer(GravityCompat.END);
            }
        });




        binding.navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.login)
                    startActivity(new Intent(getApplicationContext(), Login.class));
                else if (item.getItemId() == R.id.sync_account)
                    startActivity(new Intent(getApplicationContext(), Register.class));

                return  true;
            }
        });

        // End of onCreate
    }


    public class NoteViewHolder extends RecyclerView.ViewHolder {
        TextView entryTitle, entryContent, entryDate, entryDay, entryMonth;
        View view;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);

            entryTitle = itemView.findViewById(R.id.title);
            entryContent = itemView.findViewById(R.id.content);
            entryDate = itemView.findViewById(R.id.date);
            entryDay = itemView.findViewById(R.id.day);
            entryMonth = itemView.findViewById(R.id.month);

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
