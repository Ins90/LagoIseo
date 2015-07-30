package it.inserrafesta.iseomap;

import android.database.sqlite.SQLiteOpenHelper;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;


public class MySQLiteHelper extends SQLiteOpenHelper {
    public static final String TABLE_PLACE = "localita_tbl";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_ID_ASL = "id_asl";
    public static final String COLUMN_LAT = "lat";
    public static final String COLUMN_LNG = "lng";
    public static final String COLUMN_COMUNE = "comune";
    public static final String COLUMN_INDIRIZZO = "indirizzo";
    public static final String COLUMN_LOCALITA = "localita";
    public static final String COLUMN_PROVINCIA = "provincia";
    public static final String COLUMN_CLAS = "classificazione";
    public static final String COLUMN_DIVIETO = "divieto";
    public static final String COLUMN_IMAGE = "image";
    public static final String COLUMN_FAVORITO = "favorito";
    public static final String COLUMN_SERVIZI = "servizi";
    public static final String COLUMN_MEDICO = "medico";
    public static final String COLUMN_FARMACIA = "farmacia";
    public static final String COLUMN_OSPEDALE = "ospedale";
    public static final String COLUMN_CARABINIERI = "carabinieri";
    public static final String COLUMN_PPROVINCIALE = "provinciale";
    public static final String COLUMN_PLOCALE = "locale";



    public static final String DATABASE_NAME = "place.db";
    public static final int DATABASE_VERSION = 1;

    // database creation SQL statement
    private static final String DATABASE_CREATE = "create table "
            + TABLE_PLACE + "("
            + COLUMN_ID + " integer primary key,"
            + COLUMN_ID_ASL + " text not null,"
            + COLUMN_LAT + " real not null,"
            + COLUMN_LNG + " real not null,"
            + COLUMN_COMUNE + " text not null,"
            + COLUMN_INDIRIZZO + " text,"
            + COLUMN_LOCALITA + " text not null,"
            + COLUMN_PROVINCIA + " text not null,"
            + COLUMN_CLAS + " integer not null,"
            + COLUMN_DIVIETO + " integer not null,"
            + COLUMN_IMAGE + " text not null,"
            + COLUMN_FAVORITO + " integer,"
            + COLUMN_SERVIZI + " text,"
            + COLUMN_MEDICO + " text,"
            + COLUMN_FARMACIA + " text,"
            + COLUMN_OSPEDALE + " text,"
            + COLUMN_CARABINIERI + " text,"
            + COLUMN_PPROVINCIALE + " text,"
            + COLUMN_PLOCALE + " text); ";

    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(MySQLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PLACE);
        onCreate(db);
    }
}
