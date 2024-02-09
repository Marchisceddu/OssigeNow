package com.example.ossigenow;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

public class BookingActivity extends AppCompatActivity {
    // Passaggio dati tra activity
    private Intent result;

    // Elementi UI
    private ImageButton back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        // Inizializza gli elementi UI
        back = findViewById(R.id.indietro);

        // Torna indietro all'activity precedente
        back.setOnClickListener(v -> {
            result = new Intent(BookingActivity.this, HomeActivity.class);
            startActivity(result);
            finish();
        });
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                result = new Intent(BookingActivity.this, HomeActivity.class);
                startActivity(result);
                finish();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);
    }
}