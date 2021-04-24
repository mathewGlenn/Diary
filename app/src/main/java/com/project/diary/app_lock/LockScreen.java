package com.project.diary.app_lock;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.andrognito.pinlockview.IndicatorDots;
import com.andrognito.pinlockview.PinLockListener;
import com.andrognito.pinlockview.PinLockView;
import com.project.diary.R;
import com.project.diary.entries.EntriesList;

public class LockScreen extends AppCompatActivity {

    private PinLockView pinLockView;
    private IndicatorDots indicatorDots;
    private final static String true_code = "1234";

    Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock_screen);

        pinLockView = findViewById(R.id.pin_lock_view);
        indicatorDots = findViewById(R.id.indicator_dots);

        // attach lock view with dot indicator
        pinLockView.attachIndicatorDots(indicatorDots);

        //set lock code length
        pinLockView.setPinLength(4);

        //set listener for lock code change
        pinLockView.setPinLockListener(new PinLockListener() {
            @Override
            public void onComplete(String pin) {
                // verify password
                if (pin.equals(true_code)){
                    startActivity(new Intent(LockScreen.this, EntriesList.class));
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    finish();
                }else{
                    if (toast != null)
                        toast.cancel();

                    Toast.makeText(LockScreen.this, "Wrong PIN", Toast.LENGTH_SHORT).show();
                    pinLockView.resetPinLockView();

                    toast = Toast.makeText(getApplicationContext(), "Wrong PIN", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }

            @Override
            public void onEmpty() {

            }

            @Override
            public void onPinChange(int pinLength, String intermediatePin) {

            }
        });


    }
    public void reload() {

        Intent intent = getIntent();
        overridePendingTransition(0, 0);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finish();

        overridePendingTransition(0, 0);
        startActivity(intent);
    }
}