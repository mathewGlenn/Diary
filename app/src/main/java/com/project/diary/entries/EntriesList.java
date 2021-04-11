package com.project.diary.entries;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.project.diary.MainActivity2;
import com.project.diary.R;
import com.project.diary.Splash;
import com.project.diary.authentication.Login;
import com.project.diary.authentication.Register;
import com.project.diary.databinding.ActivityMainBinding;
import com.project.diary.model.Entry;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


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

    List<String> entryTags;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        ///

        ///


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

                String userFeeling = entry.getFeeling();
                entryTags = entry.getTags();

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
                if (item.getItemId() == R.id.login) {
                    if (user.isAnonymous()) {
                        //if the user is using a temporary account and want to login, display a warning that
                        //doing so will delete his data
                        //unless he register his account
                        displayLoginAlert();
                    } else {
                        startActivity(new Intent(getApplicationContext(), Login.class));
                        finish();
                    }
                } else if (item.getItemId() == R.id.sync_account) {
                    if (!user.isAnonymous()) {
                        Toast.makeText(EntriesList.this, "Account already synced", Toast.LENGTH_SHORT).show();
                    } else
                        startActivity(new Intent(getApplicationContext(), Register.class));
                } else if (item.getItemId() == R.id.logout)
                    checkUser();

                return true;
            }
        });


        //favorite entries button
        binding.btnHeart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), FavoriteEntries.class));
            }
        });

        //show profile button
        binding.btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MainActivity2.class));
            }
        });

        //display username and email on navigation header
        View headerView = binding.navView.getHeaderView(0);
        TextView userName = headerView.findViewById(R.id.userDisplayName);
        TextView userEmail = headerView.findViewById(R.id.userDisplayEmail);

        String udEmail, udName;
        if (!user.isAnonymous()) {
            udName = user.getDisplayName();
            udEmail = user.getEmail();
        } else {
            udName = "Temporary account";
            udEmail = "Sync your account to secure your data";
        }

        userName.setText(udName);
        userEmail.setText(udEmail);

        //display user name on top of the app
        //User's Diary
        String diaryOwner = user.getDisplayName();

        if (!diaryOwner.isEmpty()) {
            //get the first word only in user name
            diaryOwner = getFirstWord(diaryOwner);
            String uname_diary = diaryOwner.concat("'s Diary");
            binding.unameDiary.setText(uname_diary);
        }


        // End of onCreate
    }

    //for getting first word in user name to be displayed on top of app
    //User's Diary
    private String getFirstWord(String text) {

        int index = text.indexOf(' ');

        if (index > -1) { // Check if there is more than one word.

            return text.substring(0, index).trim(); // Extract first word.

        } else {

            return text; // Text is the first word itself.
        }
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

    private void checkUser() {
        //check if the user is logged in with email

        if (user.isAnonymous()) {
            displayLogoutAlert();
        } else {


            auth.signOut();
            finish();
            startActivity(new Intent(getApplicationContext(), Splash.class));

        }
    }

    private void displayLogoutAlert() {
        AlertDialog.Builder logoutWarning = new AlertDialog.Builder(this);
        logoutWarning.setTitle("Logout")
                .setMessage("You are using a temporary account. If you logout now all your data will be lost.")
                .setPositiveButton("Sync Account", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(getApplicationContext(), Register.class));
                        finish();
                    }
                }).setNegativeButton("Logout", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if user logout with anonymous account
                //delete  all notes and delete anonymous user

                AlertDialog.Builder builder = new AlertDialog.Builder(EntriesList.this);
                builder.setMessage("Are you sure to logout?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                user.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        startActivity(new Intent(getApplicationContext(), Splash.class));
                                        finish();
                                    }
                                });
                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();

            }
        });
        logoutWarning.show();
    }

    public void displayLoginAlert() {
        AlertDialog.Builder loginWarning = new AlertDialog.Builder(this)
                .setTitle("You are using a temporary account")
                .setMessage("If you login with an existing account, your temporary data will be deleted. Do you want to sync your account?")
                .setPositiveButton("Sync Account", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(getApplicationContext(), Register.class));
                        finish();
                    }
                }).setNegativeButton("It's OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(getApplicationContext(), Login.class));
                        finish();
                    }
                });
        loginWarning.show();

    }

}
