package com.project.diary.entries;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
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

import com.facebook.login.LoginManager;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.auth.User;
import com.project.diary.ToggleDarkMode;
import com.project.diary.model.Feelings;
import com.project.diary.model.UniqueTags;
import com.project.diary.profile.UserProfile;
import com.project.diary.R;
import com.project.diary.Splash;
import com.project.diary.app_lock.ManageDiaryLock;
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

import p32929.easypasscodelock.Utils.EasyLock;
import p32929.easypasscodelock.Utils.LockscreenHandler;


public class EntriesList extends LockscreenHandler {

    //for the nav_view
    private DrawerLayout drawerLayout;
    //

    private ActivityMainBinding binding;

    RecyclerView entriesList;

    FirebaseFirestore firestore;
    FirestoreRecyclerAdapter<Entry, NoteViewHolder> entryAdapter;
    FirebaseUser user;
    FirebaseAuth auth;

    List<String> entryTags, allUniqueTags;


    int countAllEntries;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        EasyLock.setBackgroundColor(R.color.background);
        EasyLock.checkPassword(this);


        firestore = FirebaseFirestore.getInstance();

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        Query query = firestore.collection("allEntries").document(user.getUid()).collection("userEntries").orderBy("date", Query.Direction.DESCENDING);


        //check if counter exists. create if not
        DocumentReference counterReference = firestore.collection("allEntries").document(user.getUid()).collection("counters").document("feeling_counters");
        counterReference.get().addOnCompleteListener(task -> {
            if (!task.getResult().exists()) {
                Feelings feelings = new Feelings(0, 0, 0, 0, 0, 0);
                counterReference.set(feelings);
            }

        });

        DocumentReference tagsReference = firestore.collection("allEntries").document(user.getUid()).collection("tags").document("unique_tags");
        tagsReference.get().addOnCompleteListener(task -> {
            if (!task.getResult().exists()) {
                UniqueTags uniqueTags = new UniqueTags();
                tagsReference.set(uniqueTags);
            } else {
                UniqueTags uniqueTags = task.getResult().toObject(UniqueTags.class);
                if (uniqueTags != null) {
                    allUniqueTags = uniqueTags.getUnique_tags();
                }
            }
        });


        // Query notes > uid > mynotes > allnotes


        FirestoreRecyclerOptions<Entry> allentries = new FirestoreRecyclerOptions.Builder<Entry>()
                .setQuery(query, Entry.class)
                .build();


        entryAdapter = new FirestoreRecyclerAdapter<Entry, NoteViewHolder>(allentries) {
            @Override
            protected void onBindViewHolder(@NonNull NoteViewHolder noteViewHolder, int i, @NonNull Entry entry) {

                String userFeeling = entry.getFeeling();
                Boolean isFavorite = entry.getFavorite();

                entryTags = entry.getTags();

                SimpleDateFormat dateFormat;

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

/*                if (entry.getImage_link() == null){
                    noteViewHolder.entryHasImageIndicator.setVisibility(View.INVISIBLE);
                }*/

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
                noteViewHolder.view.setOnClickListener(v -> {
                    Intent i1 = new Intent(v.getContext(), EntryContent.class);
                    //get the title, content, date, etc to view in the EntryContent.class
                    i1.putExtra("title", entry.getTitle());
                    i1.putExtra("content", entry.getContent());
                    i1.putExtra("date", entry.getDate());
                    i1.putExtra("feeling", entry.getFeeling());
                    i1.putStringArrayListExtra("tags", arrLisTags);
                    i1.putExtra("entryID", docID);
                    i1.putExtra("isFavorite", isFavorite);
                    i1.putExtra("imgLink", entry.getImage_link());
                    i1.putExtra("imgName", entry.getImage_name());
                    startActivity(i1);
                });

                countAllEntries = entryAdapter.getItemCount();
            }

            @NonNull
            @Override
            public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.entry_view, parent, false);

                return new NoteViewHolder(view);
            }

            @Override
            public void onDataChanged() {
                if (getItemCount() == 0) {
                    binding.lottieNoEntryYet.setVisibility(View.VISIBLE);
                    binding.txtCreateFirstEntry.setVisibility(View.VISIBLE);
                } else {
                    binding.lottieNoEntryYet.setVisibility(View.INVISIBLE);
                    binding.txtCreateFirstEntry.setVisibility(View.INVISIBLE);
                }
            }
        };


        entriesList = binding.entrieslist;

        entriesList.setLayoutManager(new LinearLayoutManager(this));
        entriesList.setAdapter(entryAdapter);


        binding.addEntry.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddEntry.class);
            intent.putStringArrayListExtra("unique_tags", (ArrayList<String>) allUniqueTags);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });


        //for the nav
        drawerLayout = binding.draweLayout;


        binding.sideMenu.setOnClickListener(v -> {

            if (!drawerLayout.isDrawerOpen(GravityCompat.START))
                drawerLayout.openDrawer(GravityCompat.START);
            else
                drawerLayout.closeDrawer(GravityCompat.END);
        });


        binding.navView.setNavigationItemSelectedListener(item -> {
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
            } else if (item.getItemId() == R.id.logout) {
                checkUser();
            } else if (item.getItemId() == R.id.diary_lock) {
                startActivity(new Intent(getApplicationContext(), ManageDiaryLock.class));
            }else if (item.getItemId() == R.id.darkmode) {
                startActivity(new Intent(getApplicationContext(), ToggleDarkMode.class));
            }

            return true;
        });


        //favorite entries button
        binding.btnHeart.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), FavoriteEntries.class));
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.fade_out);
        });

        //show profile button
        binding.btnProfile.setOnClickListener(v -> {
            Intent intent = new Intent(this, UserProfile.class);
            intent.putStringArrayListExtra("unique_tags", (ArrayList<String>) allUniqueTags);
            intent.putExtra("count_all_entries", countAllEntries);
            startActivity(intent);
            //verridePendingTransition(android.R.anim.slide_in_left, android.R.anim.fade_out);

            // Toast.makeText(EntriesList.this, "Coming soon", Toast.LENGTH_SHORT).show();
        });


        //display user name on top of the app
        //User's Diary
        String diaryOwner = user.getDisplayName();

        if (diaryOwner != null) {
            if (!diaryOwner.isEmpty()) {
                //get the first word only in user name
                diaryOwner = getFirstWord(diaryOwner);
                String uname_diary = diaryOwner.concat("'s Diary");
                binding.unameDiary.setText(uname_diary);
            }

            binding.refreshLayout.setOnRefreshListener(() -> {
                entryAdapter.notifyDataSetChanged();
                binding.refreshLayout.setRefreshing(false);
            });
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
        ImageView entryFeeling, entryHasImageIndicator;
        View view;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);

            entryHasImageIndicator = itemView.findViewById(R.id.entryImgIndicator);
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
            LoginManager.getInstance().logOut();
            auth.signOut();
            finish();
            startActivity(new Intent(getApplicationContext(), Splash.class));

        }
    }

    private void displayLogoutAlert() {
        AlertDialog.Builder logoutWarning = new AlertDialog.Builder(this);
        logoutWarning.setTitle("Logout")
                .setMessage("You are using a temporary account. If you logout now all your data will be lost.")
                .setPositiveButton("Sync Account", (dialog, which) -> {
                    startActivity(new Intent(getApplicationContext(), Register.class));
                    finish();
                }).setNegativeButton("Logout", (dialog, which) -> {
            //if user logout with anonymous account
            //delete  all notes and delete anonymous user

            AlertDialog.Builder builder = new AlertDialog.Builder(EntriesList.this);
            builder.setMessage("Are you sure to logout?")
                    .setPositiveButton("Yes", (dialog1, which1) -> user.delete().addOnSuccessListener(aVoid -> {
                        startActivity(new Intent(getApplicationContext(), Splash.class));
                        finish();
                    })).setNegativeButton("Cancel", (dialog12, which12) -> dialog12.dismiss()).show();

        });
        logoutWarning.show();
    }

    public void displayLoginAlert() {
        AlertDialog.Builder loginWarning = new AlertDialog.Builder(this)
                .setTitle("You are using a temporary account")
                .setMessage("If you login with an existing account, your temporary data will be deleted. Do you want to sync your account?")
                .setPositiveButton("Sync Account", (dialog, which) -> {
                    startActivity(new Intent(getApplicationContext(), Register.class));
                    finish();
                }).setNegativeButton("It's OK", (dialog, which) -> {
                    startActivity(new Intent(getApplicationContext(), Login.class));
                    finish();
                });
        loginWarning.show();

    }

}
