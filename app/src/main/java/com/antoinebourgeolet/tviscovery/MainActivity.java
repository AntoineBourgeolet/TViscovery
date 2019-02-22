package com.antoinebourgeolet.tviscovery;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;

public class MainActivity extends AppCompatActivity {

    View mainLayout;

    RelativeLayout chargingLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        initialiseLayouts();
        callWebServiceForTVShowList();

    }

    private void initialiseLayouts() {
        mainLayout = findViewById(R.id.mainLayout);
        chargingLayout = findViewById(R.id.chargingLayout);
    }

    private void callWebServiceForTVShowList() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://www.antoine-bourgeolet.fr/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        WebServiceCallbackInterface callbackInterface = retrofit.create(WebServiceCallbackInterface.class);
        Call<List<TVShow>> getAllTVShow = callbackInterface.getAllTVShow();

        getAllTVShow.enqueue(new Callback<List<TVShow>>() {
            @Override
            public void onResponse(Call<List<TVShow>> call, Response<List<TVShow>> response) {
                Log.w("TViscovery", "ResponseFromALLTVSHOW");
                DatabaseHelper databaseHelper = new DatabaseHelper(mainLayout.getContext());
                SQLiteDatabase database = databaseHelper.getWritableDatabase();
                //databaseHelper.updateListTVShow(response.body(), database);
                databaseHelper.insertListTVShow(response.body(), database);

                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl("https://www.antoine-bourgeolet.fr/api/")
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                WebServiceCallbackInterface callbackInterface = retrofit.create(WebServiceCallbackInterface.class);
                Call<String[]> getAllGenre = callbackInterface.getAllGenre();

                getAllGenre.enqueue(new Callback<String[]>() {
                    @Override
                    public void onResponse(Call<String[]> call, Response<String[]> response) {
                        Log.w("TViscovery", "ResponseFromALLGenre");
                        DatabaseHelper databaseHelper = new DatabaseHelper(mainLayout.getContext());
                        SQLiteDatabase database = databaseHelper.getWritableDatabase();

                        //databaseHelper.updateListGenre(response.body(), database);
                        databaseHelper.insertListGenre(response.body(), database);


                        Genre[] genres = databaseHelper.selectAllGenre(database);

                        Log.d("TViscovery", String.valueOf(genres[0].getGenre()));

                        database.close();

                        Intent displayMenu = new Intent(chargingLayout.getContext(), MenuActivity.class);
                        startActivity(displayMenu);


                    }

                    @Override
                    public void onFailure(Call<String[]> call, Throwable t) {
                        DatabaseHelper databaseHelper = new DatabaseHelper(mainLayout.getContext());
                        SQLiteDatabase database = databaseHelper.getWritableDatabase();
                        if ((databaseHelper.selectAllTVShow(database).length == 0) || (databaseHelper.selectAllGenre(database).length == 0)) {
                            new AlertDialog.Builder(mainLayout.getContext())
                                    .setTitle("Pas de connexion internet")
                                    .setMessage("Une connexion internet est nécessaire pour récupérer les données lors du premier lancement. Voulez-vous réessayer ?")
                                    .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent mainActivityIntent = new Intent(mainLayout.getContext(), MainActivity.class);
                                            startActivity(mainActivityIntent);
                                        }

                                    })

                                    .setNeutralButton("Non", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            finish();
                                        }
                                    })
                                    .show();
                        } else {
                            Toast.makeText(mainLayout.getContext(),
                                    "Impossible de mettre à jour les données. Verifiez votre connexion.",
                                    Toast.LENGTH_LONG).show();
                            Intent displayMenu = new Intent(chargingLayout.getContext(), MenuActivity.class);
                            startActivity(displayMenu);
                        }
                    }
                });
            }

            @Override
            public void onFailure(Call<List<TVShow>> call, Throwable t) {
                DatabaseHelper databaseHelper = new DatabaseHelper(mainLayout.getContext());
                SQLiteDatabase database = databaseHelper.getWritableDatabase();
                if ((databaseHelper.selectAllTVShow(database).length == 0) || (databaseHelper.selectAllGenre(database).length == 0)) {
                    new AlertDialog.Builder(mainLayout.getContext())
                            .setTitle("Pas de connexion internet")
                            .setMessage("Une connexion internet est nécessaire pour récupérer les données lors du premier lancement. Voulez-vous réessayer ?")
                            .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent mainActivityIntent = new Intent(mainLayout.getContext(), MainActivity.class);
                                    startActivity(mainActivityIntent);
                                }

                            })

                            .setNeutralButton("Non", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            })
                            .show();
                } else {
                    Toast.makeText(mainLayout.getContext(),
                            "Impossible de mettre à jour les données. Verifiez votre connexion.",
                            Toast.LENGTH_LONG).show();
                    Intent displayMenu = new Intent(chargingLayout.getContext(), MenuActivity.class);
                    startActivity(displayMenu);
                }
            }
        });
    }

    public interface WebServiceCallbackInterface {
        @GET("tvshow")
        Call<List<TVShow>> getAllTVShow();

        @GET("genre")
        Call<String[]> getAllGenre();
    }
}
