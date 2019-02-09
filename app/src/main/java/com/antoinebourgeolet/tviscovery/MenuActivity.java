package com.antoinebourgeolet.tviscovery;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MenuActivity extends AppCompatActivity {
    Button startButton;
    TextView infoTextView;
    LinearLayout menuLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        startButton = findViewById(R.id.startButton);
        menuLayout = findViewById(R.id.menuLayout);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent displayTVShowActivityIntent = new Intent(startButton.getContext(), DisplayTVShowActivity.class);
                startActivity(displayTVShowActivityIntent);
            }
        });

    }

}
