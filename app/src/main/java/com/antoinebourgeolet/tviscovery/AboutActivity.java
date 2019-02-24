package com.antoinebourgeolet.tviscovery;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class AboutActivity extends Activity {
    ImageView backButton;
    Button resetButton;
    TextView versionTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        backButton = findViewById(R.id.backButton);
        resetButton = findViewById(R.id.resetButton);
        versionTextView = findViewById(R.id.versionTextView);


        versionTextView.setText("Version actuel - " + BuildConfig.VERSION_CODE + " - " + BuildConfig.VERSION_NAME);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent displayMenu = new Intent(backButton.getContext(), MenuActivity.class);
                startActivity(displayMenu);
            }
        });

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(resetButton.getContext())
                        .setTitle("ATTENTION")
                        .setMessage("Souhaitez-vous réinitialiser toutes les informations de l'application ? Toutes les données seront perdues !")
                        .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                DatabaseHelper databaseHelper = new DatabaseHelper(resetButton.getContext());
                                SQLiteDatabase database = databaseHelper.getWritableDatabase();
                                databaseHelper.reset(database);
                                Intent mainActivityIntent = new Intent(resetButton.getContext(), MainActivity.class);
                                startActivity(mainActivityIntent);
                            }

                        })

                        .setNeutralButton("Non", null)
                        .show();
            }
        });
    }

}
