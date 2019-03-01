package com.antoinebourgeolet.tviscovery;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MenuActivity extends AppCompatActivity {
    Button startButton;
    Button myListButton;
    Button aboutButton;
    Button reportButton;
    LinearLayout menuLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        startButton = findViewById(R.id.startButton);
        myListButton = findViewById(R.id.myListButton);
        menuLayout = findViewById(R.id.menuLayout);
        aboutButton = findViewById(R.id.aboutButton);
        reportButton = findViewById(R.id.reportButton);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent displayTVShowActivityIntent = new Intent(startButton.getContext(), DisplayTVShowActivity.class);
                startActivity(displayTVShowActivityIntent);
            }
        });

        myListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent displayListActivityIntent = new Intent(startButton.getContext(), ListShowActivity.class);
                startActivity(displayListActivityIntent);
            }
        });

        reportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("mailto:tvisvovery@gmail.com");
                Intent mailActivity = new Intent(Intent.ACTION_SENDTO, uri);
                mailActivity.putExtra(Intent.EXTRA_SUBJECT,
                        "Rapport de bug");
                startActivity(mailActivity);
            }
        });

        aboutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent displayAboutActivityIntent = new Intent(startButton.getContext(), AboutActivity.class);
                startActivity(displayAboutActivityIntent);
            }
        });



    }


}
