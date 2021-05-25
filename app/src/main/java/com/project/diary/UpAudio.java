package com.project.diary;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Random;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class UpAudio extends AppCompatActivity {
    private Button play, stop, record;
    private MediaRecorder myAudioRecorder;
    private String outputFile;
    private String TAG = "Audio Recorder";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_up_audio);

        play = findViewById(R.id.play);
        stop = findViewById(R.id.stop);
        record = findViewById(R.id.record);
        TextView txt = findViewById(R.id.txt_rec);

        //stop.setEnabled(false);
       // play.setEnabled(false);
        outputFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/recording.3gp";
      //  myAudioRecorder = new MediaRecorder();

        record.setOnClickListener(v -> {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.RECORD_AUDIO },
                        10);
            } else {
                startRecording();
            }

        });

        stop.setOnClickListener(v ->{
            stopRecording();
            txt.setText("Recording Stopped");
        });
    }

    private void startRecording(){
        myAudioRecorder = new MediaRecorder();
        myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);

        myAudioRecorder.setOutputFile(outputFile);
        myAudioRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            myAudioRecorder.prepare();
        } catch (IOException e) {
           Log.e(TAG, "prepare() failed");
        }catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            Toast.makeText(getApplicationContext(), "IllegalStateException called", Toast.LENGTH_LONG).show();
        }
        myAudioRecorder.start();
    }
    private void stopRecording(){
        myAudioRecorder.stop();
        myAudioRecorder.release();
        myAudioRecorder = null;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @NotNull String[] permissions, @NonNull @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 10) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startRecording();
            }else{
                //User denied Permission.
            }
        }
    }
}