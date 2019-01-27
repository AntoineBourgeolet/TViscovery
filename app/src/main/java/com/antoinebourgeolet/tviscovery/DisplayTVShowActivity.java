package com.antoinebourgeolet.tviscovery;

import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Random;

public class DisplayTVShowActivity extends AppCompatActivity {

    Button likeButton;
    Button interestedButton;
    Button skipButton;
    Button dislikeButton;

    TextView nameTextView;
    TextView synopsisTextView;
    TextView genreTextView;

    ImageView imageView;

    View mainDisplayShowLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_tvshow);

        initialiseLayouts();

        displayATVShow();
       }

    private void displayATVShow() {
        final TVShow tvShowSelected = chooseATVShow();
        if(tvShowSelected.getName() != "noMoreTVShow"){
            nameTextView.setText(tvShowSelected.getName());
            synopsisTextView.setText(tvShowSelected.getSynopsis());
            genreTextView.setText(tvShowSelected.getGenreToString());

            new DownloadImageTask(imageView).execute(tvShowSelected.getImage());

            final DatabaseHelper databaseHelper = new DatabaseHelper(mainDisplayShowLayout.getContext());
            final SQLiteDatabase database = databaseHelper.getWritableDatabase();
            likeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (String genre:tvShowSelected.getGenre())
                    {
                        databaseHelper.updateValueFromGenre(3,false,genre,database);
                    }
                    databaseHelper.updateTVShowInt(1,tvShowSelected.getId(),"viewed",database);
                    databaseHelper.updateTVShowInt(1,tvShowSelected.getId(),"liked",database);
                    displayATVShow();
                }
            });
            dislikeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (String genre:tvShowSelected.getGenre())
                    {
                        databaseHelper.updateValueFromGenre(3,true,genre,database);
                    }
                    databaseHelper.updateTVShowInt(1,tvShowSelected.getId(),"viewed",database);
                    databaseHelper.updateTVShowInt(1,tvShowSelected.getId(),"disliked",database);
                    displayATVShow();
                }
            });
            interestedButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (String genre:tvShowSelected.getGenre())
                    {
                        databaseHelper.updateValueFromGenre(1,false,genre,database);
                    }
                    databaseHelper.updateTVShowInt(1,tvShowSelected.getId(),"viewed",database);
                    databaseHelper.updateTVShowInt(1,tvShowSelected.getId(),"addedToList",database);
                    displayATVShow();
                }
            });
            skipButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (String genre:tvShowSelected.getGenre())
                    {
                        databaseHelper.updateValueFromGenre(1,true,genre,database);
                    }
                    databaseHelper.updateTVShowInt(1,tvShowSelected.getId(),"viewed",database);
                    displayATVShow();
                }
            });

        }else{
            nameTextView.setText("Fini");
        }
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


        for (int i = 0; genres.length > i; i++){
            if (genres[i].getValue() > genreMaxValue1){
                genreMaxValue3 = genreMaxValue2;
                genreMaxValue3Name = genreMaxValue2Name;
                genreMaxValue2 = genreMaxValue1;
                genreMaxValue2Name = genreMaxValue1Name;
                genreMaxValue1 = genres[i].getValue();
                genreMaxValue1Name = genres[i].getGenre();
            }else if(genres[i].getValue() > genreMaxValue2){
                genreMaxValue3 = genreMaxValue2;
                genreMaxValue3Name = genreMaxValue2Name;
                genreMaxValue2 = genres[i].getValue();
                genreMaxValue2Name = genres[i].getGenre();
            }else if(genres[i].getValue() > genreMaxValue3){
                genreMaxValue3 = genres[i].getValue();
                genreMaxValue3Name = genres[i].getGenre();
            }
        }


        Log.w("TViscovery",genreMaxValue1Name + genreMaxValue1 + genreMaxValue2Name + genreMaxValue2 + genreMaxValue3Name + genreMaxValue3);
        String[] bestGenre = new String[3];
        bestGenre[0] = genreMaxValue1Name;
        bestGenre[1] = genreMaxValue2Name;
        bestGenre[2] = genreMaxValue3Name;
        TVShow tvShowSelectionned = null;
        TVShow[] tvShows = databaseHelper.selectAllTVShow(database);

        //Regarde si une serie à les 3 genres correspondants
        for (TVShow tvShow: tvShows){
            if (tvShow.getGenre().length >= 3 && !tvShow.getViewed()){
                if (Arrays.asList(tvShow.getGenre()).contains(bestGenre[0]) &&
                        Arrays.asList(tvShow.getGenre()).contains(bestGenre[1]) &&
                        Arrays.asList(tvShow.getGenre()).contains(bestGenre[2]))
                {
                    Log.d("TViscovery","3 Best Genre");
                    tvShowSelectionned = tvShow;
                    break;
                }
            }
        }
        //Sinon Regarde si une serie à les 2 best genre correspondant
        if (tvShowSelectionned == null)
        {
            for (TVShow tvShow: tvShows){
                if (tvShow.getGenre().length >= 2 && !tvShow.getViewed()){
                    if (Arrays.asList(tvShow.getGenre()).contains(bestGenre[0]) &&
                            Arrays.asList(tvShow.getGenre()).contains(bestGenre[1]))
                    {
                        Log.d("TViscovery","2 Best Genre");
                        tvShowSelectionned = tvShow;
                        break;
                    }
                }
            }
        }

        //Sinon Regarde si une serie à le best genre correspondant
        if (tvShowSelectionned == null)
        {
            for (TVShow tvShow: tvShows){
                if (tvShow.getGenre().length >= 1 && !tvShow.getViewed()){
                    Arrays.asList(tvShow.getGenre()).contains(bestGenre[0]);

                    if (Arrays.asList(tvShow.getGenre()).contains(bestGenre[0]))
                    {
                        Log.d("TViscovery","Best Genre");
                        tvShowSelectionned = tvShow;
                        break;
                    }
                }
            }
        }

        //Sinon Regarde si une serie à la 2eme best genre correspondant
        if (tvShowSelectionned == null)
        {
            for (TVShow tvShow: tvShows){
                if (tvShow.getGenre().length >= 1 && !tvShow.getViewed()){
                    if (Arrays.asList(tvShow.getGenre()).contains(bestGenre[1]))
                    {
                        Log.d("TViscovery","2nd Best Genre");
                        tvShowSelectionned = tvShow;
                        break;
                    }
                }
            }
        }
        //Sinon Regarde si une serie à la 3eme best genre correspondant
        if (tvShowSelectionned == null)
        {
            for (TVShow tvShow: tvShows){
                if (tvShow.getGenre().length >= 1 && !tvShow.getViewed()){
                    if (Arrays.asList(tvShow.getGenre()).contains(bestGenre[2]))
                    {
                        Log.d("TViscovery","3rd Best Genre");
                        tvShowSelectionned = tvShow;
                        break;
                    }
                }
            }
        }

        //Sinon une aleatoire
        if (tvShowSelectionned == null)
        {
            int j = tvShows.length;
            do{

                Random r = new Random();
                int i = 0 + r.nextInt(tvShows.length - 0);
                if ( !tvShows[i].getViewed()){
                    Log.d("TViscovery","Alea");
                    tvShowSelectionned = tvShows[i];
                }
                j--;
            }while(tvShowSelectionned == null && j != 0 );
            if (j == 0)
            {
                tvShowSelectionned = new TVShow("noMoreTVShow",new String[]{"noMoreTVShow"},"noMoreTVShow","noMoreTVShow","noMoreTVShow");
            }
        }

        Log.w("TViscovery",tvShowSelectionned.getName());
        return tvShowSelectionned;
    }

    private void initialiseLayouts() {
        likeButton = findViewById(R.id.likeButton);
        interestedButton = findViewById(R.id.interestedButton);
        skipButton = findViewById(R.id.skipButton);
        dislikeButton = findViewById(R.id.dislikeButton);

        nameTextView = findViewById(R.id.nameTextView);
        synopsisTextView = findViewById(R.id.synopsisTextView);
        genreTextView = findViewById(R.id.genreTextView);

        imageView = findViewById(R.id.imageView);

        mainDisplayShowLayout = findViewById(R.id.mainDisplayShowLayout);
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
            bmImage.setImageBitmap(result);
        }
    }

}

