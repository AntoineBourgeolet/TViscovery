package com.antoinebourgeolet.tviscovery;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.InputStream;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class TvShowAdapter extends ArrayAdapter<TVShow> {

    public TvShowAdapter(@NonNull Context context, int resource, @NonNull TVShow[] objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull final ViewGroup parent) {
        /*
            On regarde si convertView est null. Si oui, on l'instancie avec la ligne suivante :
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.personne_cell, parent, false);
            Cette ligne sera identique à chaque fois qu'on veut instancier la View.
            Seul R.layout.personne_cell peut changer (il s'agit du layout de notre cellule)
         */
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.tvshow_cell, parent, false);
        }

        final TVShowHolder tvShowHolder = new TVShowHolder();

        tvShowHolder.firstTextView =
                convertView.findViewById(R.id.firstTextView);

        tvShowHolder.imageView =
                convertView.findViewById(R.id.imageView);

        tvShowHolder.likeButton =
                convertView.findViewById(R.id.likeButton);

        tvShowHolder.dislikeButton =
                convertView.findViewById(R.id.dislikeButton);

        tvShowHolder.cellView =
                convertView.findViewById(R.id.cellView);

        final TVShow currentTVShow = getItem(position);
        Log.w("TViscovery", String.valueOf(position));
        assert currentTVShow != null;
        tvShowHolder.firstTextView.setText(currentTVShow.getName());
        new TvShowAdapter.DownloadImageTask(tvShowHolder.imageView).execute(currentTVShow.getImage());

        final DatabaseHelper databaseHelper = new DatabaseHelper(tvShowHolder.firstTextView.getContext());
        final SQLiteDatabase database = databaseHelper.getWritableDatabase();
        final TVShow finalCurrentTVShow = currentTVShow;
        tvShowHolder.likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (String genre : finalCurrentTVShow.getGenre()) {
                    databaseHelper.updateValueFromGenre(3, false, genre, database);
                }
                databaseHelper.updateTVShowInt(1, finalCurrentTVShow.getId(), "viewed", database);
                databaseHelper.updateTVShowInt(1, finalCurrentTVShow.getId(), "liked", database);
                databaseHelper.updateTVShowInt(0, finalCurrentTVShow.getId(), "addedToList", database);
                actionOnButton("Enregistré", tvShowHolder);
            }


        });

        tvShowHolder.dislikeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (String genre : finalCurrentTVShow.getGenre()) {
                    databaseHelper.updateValueFromGenre(3, true, genre, database);
                }
                databaseHelper.updateTVShowInt(1, finalCurrentTVShow.getId(), "viewed", database);
                databaseHelper.updateTVShowInt(1, finalCurrentTVShow.getId(), "disliked", database);
                databaseHelper.updateTVShowInt(0, finalCurrentTVShow.getId(), "addedToList", database);
                actionOnButton("Enregistré", tvShowHolder);
            }
        });
        tvShowHolder.cellView.setLongClickable(true);
        tvShowHolder.cellView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                new AlertDialog.Builder(tvShowHolder.cellView.getContext())
                        .setIcon(R.drawable.round_clear_black_36)
                        .setTitle("Retirer de la liste ?")
                        .setMessage("Attention vous ne retomberez plus dessus !")
                        .setPositiveButton("Oui", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                for (String genre : finalCurrentTVShow.getGenre()) {
                                    databaseHelper.updateValueFromGenre(1, true, genre, database);
                                }
                                databaseHelper.updateTVShowInt(1, finalCurrentTVShow.getId(), "viewed", database);
                                databaseHelper.updateTVShowInt(0, finalCurrentTVShow.getId(), "addedToList", database);
                                actionOnButton("Supprimé", tvShowHolder);

                            }

                        })

                        .setNeutralButton("Non", null)
                        .show();
                return false;
            }
        });


        return convertView;
    }

    private class TVShowHolder {
        public TextView firstTextView;
        public ImageView imageView;
        public ImageView likeButton;
        public ImageView dislikeButton;
        public LinearLayout cellView;
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
            if (result != null){
                bmImage.setImageBitmap(result);
            }
            else {
                bmImage.setImageResource(R.drawable.logo_no_connexion);
            }
        }
    }
    private void actionOnButton(String Message, TVShowHolder tvShowHolder) {
        tvShowHolder.dislikeButton.setVisibility(View.INVISIBLE);
        tvShowHolder.likeButton.setVisibility(View.INVISIBLE);
        tvShowHolder.imageView.setVisibility(View.INVISIBLE);
        int bgColor = tvShowHolder.cellView.getResources().getColor(R.color.gray);
        tvShowHolder.cellView.setBackgroundColor(bgColor);
        int textColor = tvShowHolder.cellView.getResources().getColor(R.color.genre);
        tvShowHolder.firstTextView.setTextColor(textColor);
        tvShowHolder.firstTextView.setTextSize(25);
        tvShowHolder.firstTextView.setText(Message);
    }
}
