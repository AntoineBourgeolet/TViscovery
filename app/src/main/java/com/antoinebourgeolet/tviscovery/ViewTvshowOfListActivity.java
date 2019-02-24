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
    LinearLayout imageLayout;
    LinearLayout buttonLayout;
    LinearLayout genreLayout;
    LinearLayout nameLayout;
    LinearLayout playLayout;

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

        playButton = findViewById(R.id.playButton);

        mainDisplayShowLayout = findViewById(R.id.mainDisplayShowLayout);
        imageLayout = findViewById(R.id.imageLayout);
        buttonLayout = findViewById(R.id.buttonLayout);
        genreLayout = findViewById(R.id.genreLayout);
        nameLayout = findViewById(R.id.nameLayout);
        playLayout = findViewById(R.id.playLayout);
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
}


