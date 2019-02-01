package com.antoinebourgeolet.tviscovery;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;
import java.util.Random;

import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;

public class MainActivity extends AppCompatActivity {

    View mainLayout;
    Button startButton;
    TextView infoTextView;
    ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mainLayout = findViewById(R.id.mainLayout);
        initialiseLayouts();
        callWebServiceForTVShowList();

    }

    private void initialiseLayouts() {
        startButton = findViewById(R.id.startButton);
        infoTextView = findViewById(R.id.infoTextView);
        progressBar = findViewById(R.id.progressBar);

        String messageInfo = "Veuillez attendre que les infos soit mis à jours";
        infoTextView.setText(messageInfo);
        progressBar.setVisibility(View.VISIBLE);
        startButton.setEnabled(false);
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
                Log.w("TViscovery","ResponseFromALLTVSHOW");
                DatabaseHelper databaseHelper = new DatabaseHelper(mainLayout.getContext());
                SQLiteDatabase database = databaseHelper.getWritableDatabase();
                databaseHelper.updateListTVShow(response.body(), database);
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
                        Log.w("TViscovery","ResponseFromALLGenre");
                        DatabaseHelper databaseHelper = new DatabaseHelper(mainLayout.getContext());
                        SQLiteDatabase database = databaseHelper.getWritableDatabase();

                        databaseHelper.updateListGenre(response.body(), database);
                        databaseHelper.insertListGenre(response.body(), database);


                        Genre[] genres = databaseHelper.selectAllGenre(database);

                        Log.d("TViscovery", String.valueOf(genres[0].getGenre()));

                        String messageInfo = "Mise à jour correctement effectué";
                        infoTextView.setText(messageInfo);
                        progressBar.setVisibility(View.INVISIBLE);
                        startButton.setEnabled(true);

                        startButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent displayTVShowActivityIntent = new Intent(startButton.getContext(), DisplayTVShowActivity.class);
                                startActivity(displayTVShowActivityIntent);

                            }
                        });
                    }

                    @Override
                    public void onFailure(Call<String[]> call, Throwable t) {
                        String messageInfo = "Mise à jour en échec. Verifier votre connexion. Vous pouvez tout de même utiliser l'application normalement";
                        infoTextView.setText(messageInfo);
                        progressBar.setVisibility(View.INVISIBLE);
                        startButton.setEnabled(true);

                        startButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent displayTVShowActivityIntent = new Intent(startButton.getContext(), DisplayTVShowActivity.class);
                                startActivity(displayTVShowActivityIntent);

                            }
                        });
                    }
                });
            }

            @Override
            public void onFailure(Call<List<TVShow>> call, Throwable t) {
                String messageInfo = "Mise à jour en échec. Verifier votre connexion. Vous pouvez tout de même utiliser l'application normalement";
                infoTextView.setText(messageInfo);
                progressBar.setVisibility(View.INVISIBLE);
                startButton.setEnabled(true);

                startButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent displayTVShowActivityIntent = new Intent(startButton.getContext(), DisplayTVShowActivity.class);
                        startActivity(displayTVShowActivityIntent);

                    }
                });
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
