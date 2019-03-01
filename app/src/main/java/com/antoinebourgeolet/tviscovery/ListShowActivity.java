package com.antoinebourgeolet.tviscovery;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ListShowActivity extends AppCompatActivity {

    TextView emptyText;
    ListView listView;
    ImageView backButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_show);

        initiateLayouts();
        behaviorViews();
    }
    private void initiateLayouts(){
        listView = findViewById(R.id.listView);
        backButton = findViewById(R.id.backButton);
        emptyText = findViewById(R.id.emptyText);
    }


    @Override
    public void onBackPressed() {
        Intent displayMenuIntent = new Intent(listView.getContext(), MenuActivity.class);
        startActivity(displayMenuIntent);
        finish();
    }

    private void behaviorViews(){
        /*
        On récupére les données passées par l'Intent avec la fonction suivante :
        (Object) getIntent().getExtras().getSerializable("key") avec :
                - Object qui correspond à la classe d'un objet Serializable
                - key qui correspond à la clé fourni par l'expéditeur de l'Intent
         */
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        SQLiteDatabase database = databaseHelper.getWritableDatabase();
        TVShow[] arrayOfTVShow = databaseHelper.selectAddedTVShow(database);


        /*
        Les deux lignes ci-dessous permettent d'instancier un comportement d'affichage à une ListView
        La ligne en commentaire permet d'avoir un comportement de ListView qui est prédéfini
        par Android grâce à android.R.layout.simple_list_item_1
        La deuxième ligne permet de faire notre propre comportement que nous avons défini dans
        la classe PersonneAdapter.
         */

        //ArrayAdapter listViewAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, arrayOfPersonnes);
        TvShowAdapter listViewAdapter = new TvShowAdapter(this,0,arrayOfTVShow);
        /*
        On ajoute notre comportement à notre ListView
         */
        listView.setAdapter(listViewAdapter);
        if(listView.getCount() == 0){
            emptyText.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
        }

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent displayMenu = new Intent(listView.getContext(), MenuActivity.class);
                startActivity(displayMenu);
            }
        });
    }
}
