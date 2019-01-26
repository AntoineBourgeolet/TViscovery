package hearthsoftware.fr.coursudevdeux;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "database.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME_PERSONNE = "Personne";

    public static final String DATABASE_CREATE_TABLE_PERSONNE = "CREATE TABLE Personne (id INTEGER PRIMARY KEY AUTOINCREMENT, nom TEXT, prenom TEXT)";


    public DatabaseHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        doDBCheck();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE_TABLE_PERSONNE);
    }

    private void doDBCheck() {
        try {
            File file = new File("data/data/hearthsoftware.fr.coursudevdeux/databases/" + DATABASE_NAME);
            file.delete();
        } catch (Exception e) {
            Log.e("JLE", "La DB n'existe pas");
        }
    }

    public void insertListePersonne(List<Personne> personneList, SQLiteDatabase db) {
        for (int i = 0; personneList.size() - 1 > i; i++) {
            Personne currentPersonne = personneList.get(i);

            //INSERT SQLite
            ContentValues values  = new ContentValues();
            values.put("nom", currentPersonne.getName());
            values.put("prenom", currentPersonne.getFirstName());

            db.insert(TABLE_NAME_PERSONNE, null, values);
        }
    }





    //NE SERA JAMAIS UTILISE POUR CE TP
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


}
