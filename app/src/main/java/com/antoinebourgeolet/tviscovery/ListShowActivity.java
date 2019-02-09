package com.antoinebourgeolet.tviscovery;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.app.Activity;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

public class ListShowActivity extends AppCompatActivity {

    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_show);

        initiateLayouts();
        behaviorViews();
    }
    private void initiateLayouts(){
        listView = findViewById(R.id.listView);
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
    }
}
