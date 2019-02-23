package com.antoinebourgeolet.tviscovery;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.Random;

import androidx.appcompat.app.AppCompatActivity;

public class DisplayTVShowActivity extends AppCompatActivity {

    ImageView likeButton;
    ImageView interestedButton;
    ImageView skipButton;
    ImageView dislikeButton;
    ImageView backButton;
    ImageView infoButton;

    TextView nameTextView;
    TextView synopsisTextView;
    TextView genreTextView;

    TextView endTextView;

    ImageView imageView;
    ImageView logoImageView;
    LinearLayout imageLayout;
    LinearLayout buttonLayout;
    LinearLayout genreLayout;
    LinearLayout nameLayout;
    LinearLayout playLayout;

    FloatingActionButton playButton;

    View mainDisplayShowLayout;

    static String howGenerate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_tvshow);

        initialiseLayouts();

        displayATVShow();
    }


    private void initialiseLayouts() {
        likeButton = findViewById(R.id.likeButton);
        interestedButton = findViewById(R.id.interestedButton);
        skipButton = findViewById(R.id.skipButton);
        dislikeButton = findViewById(R.id.dislikeButton);
        backButton = findViewById(R.id.backButton);
        infoButton = findViewById(R.id.infoButton);

        nameTextView = findViewById(R.id.nameTextView);
        synopsisTextView = findViewById(R.id.synopsisTextView);
        genreTextView = findViewById(R.id.genreTextView);
        endTextView = findViewById(R.id.endTextView);
        imageView = findViewById(R.id.imageView);

        playButton = findViewById(R.id.playButton);

        mainDisplayShowLayout = findViewById(R.id.mainDisplayShowLayout);
        logoImageView = findViewById(R.id.logoImageView);
        imageLayout = findViewById(R.id.imageLayout);
        buttonLayout = findViewById(R.id.buttonLayout);
        genreLayout = findViewById(R.id.genreLayout);
        nameLayout = findViewById(R.id.nameLayout);
        playLayout = findViewById(R.id.playLayout);


    }


    private void displayATVShow() {
        final TVShow tvShowSelected = chooseATVShow();
        if (tvShowSelected.getName() != "noMoreTVShow") {
            nameTextView.setText(tvShowSelected.getName());
            Log.d("TViscovery", String.valueOf(tvShowSelected.getSynopsis().length()));
            if (tvShowSelected.getSynopsis().length() > 320) {
                Log.d("TViscovery", "Long synopsis");
                synopsisTextView.setText(String.format("%s...", tvShowSelected.getSynopsis().substring(0, 320)));
            } else {
                Log.d("TViscovery", "Short synopsis");
                synopsisTextView.setText(tvShowSelected.getSynopsis());
            }
            genreTextView.setText(tvShowSelected.getGenreForDisplay());
            imageView.setImageResource(R.drawable.logo_loading);
            new DownloadImageTask(imageView).execute(tvShowSelected.getImage());

            final DatabaseHelper databaseHelper = new DatabaseHelper(mainDisplayShowLayout.getContext());
            final SQLiteDatabase database = databaseHelper.getWritableDatabase();
            likeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (String genre : tvShowSelected.getGenre()) {
                        databaseHelper.updateValueFromGenre(3, false, genre, database);
                    }
                    databaseHelper.updateTVShowInt(1, tvShowSelected.getId(), "viewed", database);
                    databaseHelper.updateTVShowInt(1, tvShowSelected.getId(), "liked", database);
                    displayATVShow();
                }
            });
            dislikeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (String genre : tvShowSelected.getGenre()) {
                        databaseHelper.updateValueFromGenre(3, true, genre, database);
                    }
                    databaseHelper.updateTVShowInt(1, tvShowSelected.getId(), "viewed", database);
                    databaseHelper.updateTVShowInt(1, tvShowSelected.getId(), "disliked", database);
                    displayATVShow();
                }
            });
            interestedButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (String genre : tvShowSelected.getGenre()) {
                        databaseHelper.updateValueFromGenre(1, false, genre, database);
                    }
                    databaseHelper.updateTVShowInt(1, tvShowSelected.getId(), "viewed", database);
                    databaseHelper.updateTVShowInt(1, tvShowSelected.getId(), "addedToList", database);
                    displayATVShow();
                }
            });
            skipButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (String genre : tvShowSelected.getGenre()) {
                        databaseHelper.updateValueFromGenre(1, true, genre, database);
                    }
                    databaseHelper.updateTVShowInt(1, tvShowSelected.getId(), "viewed", database);
                    displayATVShow();
                }
            });

            playButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (tvShowSelected.getVideo().equals("")) {
                        Toast.makeText(playButton.getContext(),
                                "Aucune vidéo disponible pour cette série.",
                                Toast.LENGTH_LONG).show();
                    } else if (!isOnline(playButton.getContext())){
                        Toast.makeText(playButton.getContext(),
                                "Vous devez être connecté à internet.",
                                Toast.LENGTH_LONG).show();
                    }else{
                        Intent displayYoutubeVideo = new Intent(playButton.getContext(), YoutubePlayerActivity.class);
                        displayYoutubeVideo.putExtra("video", tvShowSelected.getVideo());
                        startActivity(displayYoutubeVideo);
                    }
                }
            });

            infoButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    displayInfoGeneration(howGenerate);
                }
            });


        } else {
            nameLayout.setVisibility(View.INVISIBLE);
            buttonLayout.setVisibility(View.INVISIBLE);
            genreTextView.setVisibility(View.INVISIBLE);
            imageLayout.setVisibility(View.INVISIBLE);
            playLayout.setVisibility(View.INVISIBLE);
            synopsisTextView.setVisibility(View.INVISIBLE);
            infoButton.setVisibility(View.INVISIBLE);
            endTextView.setVisibility(View.VISIBLE);
            logoImageView.setVisibility(View.VISIBLE);
        }

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent displayMenu = new Intent(playButton.getContext(), MenuActivity.class);
                startActivity(displayMenu);
            }
        });
    }

    private void displayInfoGeneration(String howGenerate) {
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.custom_toast,
                (ViewGroup) findViewById(R.id.custom_toast_container));

        TextView text = (TextView) layout.findViewById(R.id.text);
        text.setText(howGenerate);

        Toast toast = new Toast(infoButton.getContext());
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setGravity(Gravity.TOP, 0, 150);
        toast.setView(layout);
        toast.show();

    }

    private TVShow chooseATVShow() {
        DatabaseHelper databaseHelper = new DatabaseHelper(mainDisplayShowLayout.getContext());
        SQLiteDatabase database = databaseHelper.getWritableDatabase();
        Genre[] genres = databaseHelper.selectAllGenre(database);
        int genreMaxValue1 = 0;
        String genreMaxValue1Name = "";
        int genreMaxValue2 = 0;
        String genreMaxValue2Name = "";
        int genreMaxValue3 = 0;
        String genreMaxValue3Name = "";


        for (int i = 0; genres.length > i; i++) {
            if (genres[i].getValue() > genreMaxValue1) {
                genreMaxValue3 = genreMaxValue2;
                genreMaxValue3Name = genreMaxValue2Name;
                genreMaxValue2 = genreMaxValue1;
                genreMaxValue2Name = genreMaxValue1Name;
                genreMaxValue1 = genres[i].getValue();
                genreMaxValue1Name = genres[i].getGenre();
            } else if (genres[i].getValue() > genreMaxValue2) {
                genreMaxValue3 = genreMaxValue2;
                genreMaxValue3Name = genreMaxValue2Name;
                genreMaxValue2 = genres[i].getValue();
                genreMaxValue2Name = genres[i].getGenre();
            } else if (genres[i].getValue() > genreMaxValue3) {
                genreMaxValue3 = genres[i].getValue();
                genreMaxValue3Name = genres[i].getGenre();
            }
        }


        Log.w("TViscovery", genreMaxValue1Name + genreMaxValue1 + genreMaxValue2Name + genreMaxValue2 + genreMaxValue3Name + genreMaxValue3);
        String[] bestGenre = new String[3];
        bestGenre[0] = genreMaxValue1Name;
        bestGenre[1] = genreMaxValue2Name;
        bestGenre[2] = genreMaxValue3Name;
        TVShow tvShowSelectionned = null;
        TVShow[] tvShows = databaseHelper.selectAllTVShow(database);

        //Regarde si une serie à les 3 genres correspondants
        for (TVShow tvShow : tvShows) {
            if (tvShow.getGenre().length >= 3 && !tvShow.getViewed()) {
                if (Arrays.asList(tvShow.getGenre()).contains(bestGenre[0]) &&
                        Arrays.asList(tvShow.getGenre()).contains(bestGenre[1]) &&
                        Arrays.asList(tvShow.getGenre()).contains(bestGenre[2])) {
                    Log.d("TViscovery", "3 Best Genre");
                    howGenerate = "Généré grâce à vos 3 genres de série préféré.\n\n "
                            + bestGenre[0] + ", " + bestGenre[1] + " et " + bestGenre[2];
                    tvShowSelectionned = tvShow;
                    break;
                }
            }
        }
        //Sinon Regarde si une serie à les 2 best genre correspondant
        if (tvShowSelectionned == null) {
            for (TVShow tvShow : tvShows) {
                if (tvShow.getGenre().length >= 2 && !tvShow.getViewed()) {
                    if (Arrays.asList(tvShow.getGenre()).contains(bestGenre[0]) &&
                            Arrays.asList(tvShow.getGenre()).contains(bestGenre[1])) {
                        Log.d("TViscovery", "2 Best Genre");
                        howGenerate = "Généré grâce à vos 2 genres de série préféré.\n\n "
                                + bestGenre[0] + " et " + bestGenre[1];
                        tvShowSelectionned = tvShow;
                        break;
                    }
                }
            }
        }

        //Sinon Regarde si une serie à le best genre correspondant
        if (tvShowSelectionned == null) {
            for (TVShow tvShow : tvShows) {
                if (tvShow.getGenre().length >= 1 && !tvShow.getViewed()) {
                    Arrays.asList(tvShow.getGenre()).contains(bestGenre[0]);

                    if (Arrays.asList(tvShow.getGenre()).contains(bestGenre[0])) {
                        Log.d("TViscovery", "Best Genre");
                        howGenerate = "Généré grâce à votre genre de série préféré.\n\n "
                                + bestGenre[0];
                        tvShowSelectionned = tvShow;
                        break;
                    }
                }
            }
        }

        //Sinon Regarde si une serie à la 2eme best genre correspondant
        if (tvShowSelectionned == null) {
            for (TVShow tvShow : tvShows) {
                if (tvShow.getGenre().length >= 1 && !tvShow.getViewed()) {
                    if (Arrays.asList(tvShow.getGenre()).contains(bestGenre[1])) {
                        Log.d("TViscovery", "2nd Best Genre");
                        howGenerate = "Généré grâce à votre second genre de série préféré.\n\n "
                                + bestGenre[1];
                        tvShowSelectionned = tvShow;
                        break;
                    }
                }
            }
        }
        //Sinon Regarde si une serie à la 3eme best genre correspondant
        if (tvShowSelectionned == null) {
            for (TVShow tvShow : tvShows) {
                if (tvShow.getGenre().length >= 1 && !tvShow.getViewed()) {
                    if (Arrays.asList(tvShow.getGenre()).contains(bestGenre[2])) {
                        Log.d("TViscovery", "3rd Best Genre");
                        howGenerate = "Généré grâce à votre troisième genre de série préféré.\n\n "
                                + bestGenre[2];
                        tvShowSelectionned = tvShow;
                        break;
                    }
                }
            }
        }

        //Sinon une aleatoire
        if (tvShowSelectionned == null) {
            int j = tvShows.length;
            do {

                Random r = new Random();
                int i = r.nextInt(tvShows.length);
                if (!tvShows[i].getViewed()) {
                    Log.d("TViscovery", "Alea");
                    howGenerate = "Généré aléatoirement car :\n\n Pas suffisamment d'info sur les séries que vous aimez.\n\n " +
                            "Ou nous n'avons plus de séries correspondant à vos goûts.";
                    tvShowSelectionned = tvShows[i];
                }
                j--;
            } while (tvShowSelectionned == null && j != 0);
            if (j == 0) {
                tvShowSelectionned = new TVShow("noMoreTVShow", new String[]{"noMoreTVShow"}, "noMoreTVShow", "noMoreTVShow", "noMoreTVShow");
            }
        }

        Log.w("TViscovery", tvShowSelectionned.getName());
        return tvShowSelectionned;
    }


    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap bmp = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                bmp = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return bmp;
        }

        protected void onPostExecute(Bitmap result) {
            if (result != null) {
                bmImage.setImageBitmap(result);
            } else {
                bmImage.setImageResource(R.drawable.logo_no_connexion);
            }
        }
    }

    public boolean isOnline(Context context) {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }
}

