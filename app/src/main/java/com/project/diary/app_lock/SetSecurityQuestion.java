package com.project.diary.app_lock;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.project.diary.databinding.ActivitySetSecurityQuestionBinding;

import at.favre.lib.crypto.bcrypt.BCrypt;

public class SetSecurityQuestion extends AppCompatActivity {
    static SharedPreferences sharedPreferences;
    static SharedPreferences.Editor editor;
    EditText editQ, editA;
    private ActivitySetSecurityQuestionBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = ActivitySetSecurityQuestionBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        super.onCreate(savedInstanceState);
        setContentView(view);
        sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        editQ = binding.editQuestion;
        editA = binding.editAnswer;



        binding.btnSave.setOnClickListener(v->{
            String question = editQ.getText().toString();
            String answer = editA.getText().toString();
            if (question.isEmpty() || answer.isEmpty()){
                Toast.makeText(this, "Both fields are required", Toast.LENGTH_SHORT).show();
            }else {

                String bcryptAnswer = BCrypt.withDefaults().hashToString(12, answer.toCharArray());

                editor.putString("Question", question);
                editor.putString("bcryptAnswer", bcryptAnswer);
                editor.apply();
                finish();
            }
        });
    }

    public void closeSetDiaryLock(View view) {
        finish();
    }
}