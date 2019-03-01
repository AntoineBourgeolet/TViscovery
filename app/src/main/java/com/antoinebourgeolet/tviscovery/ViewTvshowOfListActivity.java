package com.antoinebourgeolet.tviscovery;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.InputStream;

public class ViewTvshowOfListActivity extends Activity {

    ImageView likeButton;
    ImageView skipButton;
    ImageView dislikeButton;
    ImageView backButton;

    TextView nameTextView;
    TextView synopsisTextView;
    TextView genreTextView;

    ImageView imageView;
    ImageView platform1;
    ImageView platform2;
    LinearLayout buttonLayout;
    LinearLayout genreLayout;

    FloatingActionButton playButton;

    View mainDisplayShowLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_tvshow_of_list);
        initialiseLayouts();
        displayATVShow();
    }

    @Override
    public void onBackPressed() {
        Intent displayListActivityIntent = new Intent(likeButton.getContext(), ListShowActivity.class);
        startActivity(displayListActivityIntent);
    }

    private void initialiseLayouts() {
        likeButton = findViewById(R.id.likeButton);
        skipButton = findViewById(R.id.skipButton);
        dislikeButton = findViewById(R.id.dislikeButton);
        backButton = findViewById(R.id.backButton);

        nameTextView = findViewById(R.id.nameTextView);
        synopsisTextView = findViewById(R.id.synopsisTextView);
        genreTextView = findViewById(R.id.genreTextView);
        imageView = findViewById(R.id.imageView);
        platform1 = findViewById(R.id.platform1);
        platform2 = findViewById(R.id.platform2);

        playButton = findViewById(R.id.playButton);

        mainDisplayShowLayout = findViewById(R.id.mainDisplayShowLayout);
        buttonLayout = findViewById(R.id.buttonLayout);
        genreLayout = findViewById(R.id.genreLayout);
    }

    private void displayATVShow() {
        final DatabaseHelper databaseHelper = new DatabaseHelper(mainDisplayShowLayout.getContext());
        final SQLiteDatabase database = databaseHelper.getWritableDatabase();
        final TVShow tvShowSelected = databaseHelper.selectTVShowById(database, (int) getIntent().getExtras().getInt("id"));
        nameTextView.setText(tvShowSelected.getName());
        Log.d("TViscovery", String.valueOf(tvShowSelected.getSynopsis().length()));
        int i = 400;
        if (tvShowSelected.getSynopsis().length() > i) {
            Log.d("TViscovery", "Long synopsis");
            while (!tvShowSelected.getSynopsis().substring(i, i + 1).equals(" ")) {
                i--;
            }
            synopsisTextView.setText(String.format("%s...", tvShowSelected.getSynopsis().substring(0, i)));
        } else {
            Log.d("TViscovery", "Short synopsis");
            synopsisTextView.setText(tvShowSelected.getSynopsis());
        }
        int platformNumber = tvShowSelected.getPlatform().length;
        if(platformNumber >= 1)
        {
            platform1.setVisibility(View.VISIBLE);
            setPlatformImage(tvShowSelected,platform1,0);
        }
        else {
            platform1.setVisibility(View.INVISIBLE);
        }

        if(platformNumber >= 2)
        {
            platform2.setVisibility(View.VISIBLE);
            setPlatformImage(tvShowSelected,platform2,1);

        }
        else {
            platform2.setVisibility(View.INVISIBLE);
        }


        genreTextView.setText(tvShowSelected.getGenreForDisplay());
        imageView.setImageResource(R.drawable.logo_loading);
        new ViewTvshowOfListActivity.DownloadImageTask(imageView).execute(tvShowSelected.getImage());


        likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (String genre : tvShowSelected.getGenre()) {
                    databaseHelper.updateValueFromGenre(3, false, genre, database);
                }
                databaseHelper.updateTVShowInt(1, tvShowSelected.getId(), "viewed", database);
                databaseHelper.updateTVShowInt(1, tvShowSelected.getId(), "liked", database);
                databaseHelper.updateTVShowInt(0, tvShowSelected.getId(), "addedToList", database);
                Toast.makeText(playButton.getContext(),
                        "Changement enregistré.",
                        Toast.LENGTH_LONG).show();
                Intent displayListActivityIntent = new Intent(likeButton.getContext(), ListShowActivity.class);
                startActivity(displayListActivityIntent);

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
                databaseHelper.updateTVShowInt(0, tvShowSelected.getId(), "addedToList", database);
                Toast.makeText(playButton.getContext(),
                        "Changement enregistré.",
                        Toast.LENGTH_LONG).show();
                Intent displayListActivityIntent = new Intent(dislikeButton.getContext(), ListShowActivity.class);
                startActivity(displayListActivityIntent);
            }
        });

        skipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (String genre : tvShowSelected.getGenre()) {
                    databaseHelper.updateValueFromGenre(1, true, genre, database);
                }
                databaseHelper.updateTVShowInt(1, tvShowSelected.getId(), "viewed", database);
                databaseHelper.updateTVShowInt(0, tvShowSelected.getId(), "addedToList", database);
                Toast.makeText(playButton.getContext(),
                        "Changement enregistré.",
                        Toast.LENGTH_LONG).show();
                Intent displayListActivityIntent = new Intent(skipButton.getContext(), ListShowActivity.class);
                startActivity(displayListActivityIntent);
            }
        });

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tvShowSelected.getVideo().equals("")) {
                    Toast.makeText(playButton.getContext(),
                            "Aucune vidéo disponible pour cette série.",
                            Toast.LENGTH_LONG).show();
                } else if (!isOnline(playButton.getContext())) {
                    Toast.makeText(playButton.getContext(),
                            "Vous devez être connecté à internet.",
                            Toast.LENGTH_LONG).show();
                } else {
                    Intent displayYoutubeVideo = new Intent(playButton.getContext(), YoutubePlayerActivity.class);
                    displayYoutubeVideo.putExtra("video", tvShowSelected.getVideo());
                    startActivity(displayYoutubeVideo);
                }
            }
        });


        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent displayListActivityIntent = new Intent(backButton.getContext(), ListShowActivity.class);
                startActivity(displayListActivityIntent);
            }
        });
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

    private void setPlatformImage(TVShow tvShowSelected, ImageView platform, int index){
        switch (tvShowSelected.getPlatform()[index]){
            case "HBO": platform.setImageResource(R.drawable.hbo);
                break;
            case "Netflix": platform.setImageResource(R.drawable.netflix);
                break;
            case "AMC": platform.setImageResource(R.drawable.amc);
                break;
            case "Hulu": platform.setImageResource(R.drawable.hulu);
                break;
            case "OCS": platform.setImageResource(R.drawable.ocs);
                break;
            case "CW": platform.setImageResource(R.drawable.cw);
                break;
            case "History": platform.setImageResource(R.drawable.history);
                break;
            case "Amazon": platform.setImageResource(R.drawable.amazon);
                break;
            case "ShowTime": platform.setImageResource(R.drawable.showtime);
                break;
            case "USA Network": platform.setImageResource(R.drawable.usanetwork);
                break;
            case "Canal": platform.setImageResource(R.drawable.canal);
                break;
            case "Cinemax": platform.setImageResource(R.drawable.cinemax);
                break;
            case "ABC": platform.setImageResource(R.drawable.abc);
                break;
            case "FX": platform.setImageResource(R.drawable.fx);
                break;
            case "MTV": platform.setImageResource(R.drawable.mtv);
                break;
            case "Fox": platform.setImageResource(R.drawable.fox);
                break;
            case "Starz": platform.setImageResource(R.drawable.starz);
                break;
            case "TF1": platform.setImageResource(R.drawable.tf1);
                break;
            case "Warner Bros": platform.setImageResource(R.drawable.warner);
                break;
            case "M6": platform.setImageResource(R.drawable.m6);
                break;
            case "CBS": platform.setImageResource(R.drawable.cbs);
                break;
            case "France 2": platform.setImageResource(R.drawable.france2);
                break;
            case "France 3": platform.setImageResource(R.drawable.france3);
                break;
            case "NBC": platform.setImageResource(R.drawable.nbc);
                break;
            case "ARTE": platform.setImageResource(R.drawable.arte);
                break;
            case "C8": platform.setImageResource(R.drawable.c8);
                break;
            case "Syfy": platform.setImageResource(R.drawable.syfy);
                break;
            default:platform.setVisibility(View.INVISIBLE);
                break;
        }
    }
}


