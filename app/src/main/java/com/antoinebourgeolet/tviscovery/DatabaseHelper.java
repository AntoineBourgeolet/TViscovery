package com.antoinebourgeolet.tviscovery;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.util.Log;

import java.io.File;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "TVShowDB.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME_TVSHOW = "TVShow";
    private static final String TABLE_NAME_INFO = "InfoUser";

    public static final String DATABASE_CREATE_TABLE_TVSHOW = "CREATE TABLE TVShow (" +
            "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "name TEXT," +
            "genre TEXT," +
            "image TEXT," +
            "synopsis TEXT," +
            "video TEXT," +
            "platform TEXT," +
            "viewed INTEGER," +
            "liked INTEGER," +
            "disliked INTEGER," +
            "addedToList INTEGER" +
            ")";

    public static final String DATABASE_CREATE_TABLE_INFO = "CREATE TABLE InfoUser (" +
            "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "genre TEXT," +
            "value INTEGER" +
            ")";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE_TABLE_INFO);
        db.execSQL(DATABASE_CREATE_TABLE_TVSHOW);
    }

    private boolean doDBCheck() {
        try {
            File file = new File("data/data/com.antoinebourgeolet.tviscovery/databases/" + DATABASE_NAME);
            Log.e("TViscovery", "La DB n'existe pas");
            file.delete();
            return false;
        } catch (Exception e) {
            Log.e("TViscovery", "La DB existe" + e);
            return true;
        }
    }

    public void insertListTVShow(List<TVShow> TVShowList, SQLiteDatabase db) {
        for (int i = this.selectAllTVShow(db).length; TVShowList.size() > i; i++) {
            TVShow currentTVShow = TVShowList.get(i);

            //INSERT SQLite
            ContentValues values = new ContentValues();
            values.put("name", currentTVShow.getName());
            values.put("genre", currentTVShow.getGenreToString());
            values.put("image", currentTVShow.getImage());
            values.put("synopsis", currentTVShow.getSynopsis());
            values.put("video", currentTVShow.getVideo());
            values.put("platform", currentTVShow.getPlatformToString());
            values.put("viewed", 0);
            values.put("liked", 0);
            values.put("disliked", 0);
            values.put("addedToList", 0);

            db.insert(TABLE_NAME_TVSHOW, null, values);
        }
    }

    public void insertListGenre(String[] genres, SQLiteDatabase db) {
        for (int i = this.selectAllGenre(db).length; genres.length > i; i++) {

            //INSERT SQLite
            ContentValues values = new ContentValues();
            values.put("genre", genres[i]);
            values.put("value", 0);

            db.insert(TABLE_NAME_INFO, null, values);
        }
    }

    public Genre[] selectAllGenre(SQLiteDatabase db) {
        //SELECT * FROM GENRE
        Cursor cursor = db.query(
                TABLE_NAME_INFO, //Argument de la table
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );

        Genre[] genresFromDatabase = new Genre[cursor.getCount()];

        int i = 0;
        while (cursor.moveToNext()) {
            genresFromDatabase[i] = new Genre(
                    cursor.getInt(cursor.getColumnIndexOrThrow("_id")),
                    cursor.getString(cursor.getColumnIndexOrThrow("genre")),
                    cursor.getInt(cursor.getColumnIndexOrThrow("value"))
            );
            i++;
        }

        cursor.close();
        return genresFromDatabase;
    }

    public void updateTVShow(String newValue, int id, String champ, SQLiteDatabase db) {
        //Update SQLite
        ContentValues values = new ContentValues();
        values.put(champ, newValue);
        db.update(TABLE_NAME_TVSHOW, values, "_id = ?", new String[]{String.valueOf(id)});
    }

    public void updateTVShowInt(int newValue, int id, String champ, SQLiteDatabase db) {
        //Update SQLite
        ContentValues values = new ContentValues();
        values.put(champ, newValue);
        Log.w("TViscovery", "Modify " + id + " " + champ);
        int test = db.update(TABLE_NAME_TVSHOW, values, "_id = ?", new String[]{String.valueOf(id)});

    }

    public void updateListTVShow(List<TVShow> tvShows, SQLiteDatabase db) {
        for (int i = 0; this.selectAllTVShow(db).length > i; i++) {
            ContentValues values = new ContentValues();
            TVShow currentTVShow = tvShows.get(i);

            values.put("name", currentTVShow.getName());
            values.put("genre", currentTVShow.getGenreToString());
            values.put("image", currentTVShow.getImage());
            values.put("synopsis", currentTVShow.getSynopsis());
            values.put("video", currentTVShow.getVideo());
            values.put("platform", currentTVShow.getPlatformToString());

            db.update(TABLE_NAME_TVSHOW, values, "_id = ?", new String[]{String.valueOf(i + 1)});
        }
    }

    public void updateListGenre(String[] genres, SQLiteDatabase db) {
        for (int i = 0; this.selectAllGenre(db).length > i; i++) {
            ContentValues values = new ContentValues();

            values.put("genre", genres[i]);

            db.update(TABLE_NAME_INFO, values, "_id = ?", new String[]{String.valueOf(i + 1)});
        }
    }

    public TVShow[] selectAllTVShow(SQLiteDatabase database) {

        //SELECT * FROM Personne
        Cursor cursor = database.query(
                TABLE_NAME_TVSHOW, //Argument de la table
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );

        TVShow[] personnesFromDatabase = new TVShow[cursor.getCount()];

        /*
        On parcourt notre cursor grâce à la fonction moveToNext
        Pour récupérer la valeur d'une colonne, on utilise :
        cursor.getType(cursor.getColumIndexOrThrow("nomColonne"))
         */
        int i = 0;
        while (cursor.moveToNext()) {
            personnesFromDatabase[i] = new TVShow(
                    cursor.getInt(cursor.getColumnIndexOrThrow("_id")),
                    cursor.getString(cursor.getColumnIndexOrThrow("name")),
                    cursor.getString(cursor.getColumnIndexOrThrow("genre")),
                    cursor.getString(cursor.getColumnIndexOrThrow("image")),
                    cursor.getString(cursor.getColumnIndexOrThrow("synopsis")),
                    cursor.getString(cursor.getColumnIndexOrThrow("video")),
                    cursor.getString(cursor.getColumnIndexOrThrow("platform")),
                    cursor.getInt(cursor.getColumnIndexOrThrow("viewed")),
                    cursor.getInt(cursor.getColumnIndexOrThrow("liked")),
                    cursor.getInt(cursor.getColumnIndexOrThrow("disliked")),
                    cursor.getInt(cursor.getColumnIndexOrThrow("addedToList"))
            );
            i++;
        }

        cursor.close();
        return personnesFromDatabase;
    }

    public void updateGenreValue(int value, int id, SQLiteDatabase db) {
        //Update SQLite
        ContentValues values = new ContentValues();
        values.put("value", value);
        db.update(TABLE_NAME_INFO, values, "_id = ?", new String[]{String.valueOf(id)});
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void updateValueFromGenre(int valueToAdd, boolean soustraction, String genre, SQLiteDatabase db) {
        int actualvalue = this.getValueFromGenre(db, genre);
        Log.e("TViscovery", String.valueOf(actualvalue));
        ContentValues values = new ContentValues();
        if (!soustraction)
            values.put("value", actualvalue + valueToAdd);
        else
            values.put("value", actualvalue - valueToAdd);

        db.update(TABLE_NAME_INFO, values, "genre = ?", new String[]{genre});
    }

    private int getValueFromGenre(SQLiteDatabase database, String genre) {
        Cursor cursor = database.query(
                TABLE_NAME_INFO, //Argument de la table
                new String[]{"value"},
                "genre = ?",
                new String[]{genre},
                null,
                null,
                null,
                null
        );

        /*
        On parcourt notre cursor grâce à la fonction moveToNext
        Pour récupérer la valeur d'une colonne, on utilise :
        cursor.getType(cursor.getColumIndexOrThrow("nomColonne"))
         */
        Log.e("TViscovery", "Cursor count" + String.valueOf(cursor.getCount()));
        cursor.moveToNext();
        int value = cursor.getInt(cursor.getColumnIndexOrThrow("value"));

        cursor.close();
        return value;
    }

    public TVShow[] selectAddedTVShow(SQLiteDatabase database) {
        //SELECT * FROM Personne
        Cursor cursor = database.query(
                TABLE_NAME_TVSHOW, //Argument de la table
                null,
                "addedToList = ?",
                new String[]{"1"},
                null,
                null,
                null,
                null
        );

        TVShow[] personnesFromDatabase = new TVShow[cursor.getCount()];

        /*
        On parcourt notre cursor grâce à la fonction moveToNext
        Pour récupérer la valeur d'une colonne, on utilise :
        cursor.getType(cursor.getColumIndexOrThrow("nomColonne"))
         */
        int i = 0;
        while (cursor.moveToNext()) {
            Log.w("TViscovery", "A tv is in add list");
            personnesFromDatabase[i] = new TVShow(
                    cursor.getInt(cursor.getColumnIndexOrThrow("_id")),
                    cursor.getString(cursor.getColumnIndexOrThrow("name")),
                    cursor.getString(cursor.getColumnIndexOrThrow("genre")),
                    cursor.getString(cursor.getColumnIndexOrThrow("image")),
                    cursor.getString(cursor.getColumnIndexOrThrow("synopsis")),
                    cursor.getString(cursor.getColumnIndexOrThrow("video")),
                    cursor.getString(cursor.getColumnIndexOrThrow("platform")),
                    cursor.getInt(cursor.getColumnIndexOrThrow("viewed")),
                    cursor.getInt(cursor.getColumnIndexOrThrow("liked")),
                    cursor.getInt(cursor.getColumnIndexOrThrow("disliked")),
                    cursor.getInt(cursor.getColumnIndexOrThrow("addedToList"))
            );
            i++;
        }

        cursor.close();
        return personnesFromDatabase;
    }

    public void reset(SQLiteDatabase database) {
        database.delete(TABLE_NAME_TVSHOW, null, null);
        database.delete(TABLE_NAME_INFO, null, null);
    }

    public TVShow selectTVShowById(SQLiteDatabase database, int ID) {
        //SELECT * FROM Personne
        Cursor cursor = database.query(
                TABLE_NAME_TVSHOW, //Argument de la table
                null,
                "_id = ?",
                new String[]{String.valueOf(ID)},
                null,
                null,
                null,
                null
        );

        Log.w("TViscovery", "A tv is selected");
        cursor.moveToNext();
        TVShow tvShow = new TVShow(
                cursor.getInt(cursor.getColumnIndexOrThrow("_id")),
                cursor.getString(cursor.getColumnIndexOrThrow("name")),
                cursor.getString(cursor.getColumnIndexOrThrow("genre")),
                cursor.getString(cursor.getColumnIndexOrThrow("image")),
                cursor.getString(cursor.getColumnIndexOrThrow("synopsis")),
                cursor.getString(cursor.getColumnIndexOrThrow("video")),
                cursor.getString(cursor.getColumnIndexOrThrow("platform")),
                cursor.getInt(cursor.getColumnIndexOrThrow("viewed")),
                cursor.getInt(cursor.getColumnIndexOrThrow("liked")),
                cursor.getInt(cursor.getColumnIndexOrThrow("disliked")),
                cursor.getInt(cursor.getColumnIndexOrThrow("addedToList"))
        );

        cursor.close();
        return tvShow;
    }
}
