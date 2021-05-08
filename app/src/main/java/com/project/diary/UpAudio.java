package com.project.diary;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;

public class UpAudio extends AppCompatActivity {
private Button btnRecord;
private TextView recLabel;

private MediaRecorder mediaRecorder;
private String fileName;
private static final String TAG = "record_log";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_up_audio);
        btnRecord = findViewById(R.id.btnRec);
        recLabel = findViewById(R.id.recLabel);
        fileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        fileName += "/recorded_audio.3gp";

    }
}