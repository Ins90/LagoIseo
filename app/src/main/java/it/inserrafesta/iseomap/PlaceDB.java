package it.inserrafesta.iseomap;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import it.inserrafesta.iseomap.activity.DetailsActivity;
import it.inserrafesta.iseomap.activity.MainActivity;

public class PlaceDB {
    private SQLiteDatabase database;
    private MySQLiteHelper dbHelper;
    private String[] allColumns = {MySQLiteHelper.COLUMN_ID,
            MySQLiteHelper.COLUMN_LAT,
            MySQLiteHelper.COLUMN_LNG,
            MySQLiteHelper.COLUMN_COMUNE,
            MySQLiteHelper.COLUMN_LOCALITA,
            MySQLiteHelper.COLUMN_PROVINCIA,
            MySQLiteHelper.COLUMN_CLAS,
            MySQLiteHelper.COLUMN_DIVIETO,
            MySQLiteHelper.COLUMN_IMAGE,
            MySQLiteHelper.COLUMN_SERVIZI,
            MySQLiteHelper.COLUMN_MEDICO,
            MySQLiteHelper.COLUMN_FARMACIA,
            MySQLiteHelper.COLUMN_OSPEDALE,
            MySQLiteHelper.COLUMN_CARABINIERI,
            MySQLiteHelper.COLUMN_PPROVINCIALE,
            MySQLiteHelper.COLUMN_PLOCALE};

    public void open() {
        if(dbHelper == null) dbHelper =
                new MySQLiteHelper(MainActivity.getAppContext());
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    // from Object to database
    private ContentValues placeToValues(Place place) {
        ContentValues values = new ContentValues();
        //values.put(MySQLiteHelper.COLUMN_ID, place.getID());
        values.put(MySQLiteHelper.COLUMN_LAT, place.getLat());
        values.put(MySQLiteHelper.COLUMN_LNG, place.getLng());
        values.put(MySQLiteHelper.COLUMN_COMUNE, place.getComune());
        values.put(MySQLiteHelper.COLUMN_LOCALITA, place.getLocalita());
        values.put(MySQLiteHelper.COLUMN_PROVINCIA, place.getProvincia());
        values.put(MySQLiteHelper.COLUMN_CLAS, place.getClassificazione());
        values.put(MySQLiteHelper.COLUMN_DIVIETO, place.getDivieto());
        values.put(MySQLiteHelper.COLUMN_IMAGE, place.getImageUrl());
        values.put(MySQLiteHelper.COLUMN_SERVIZI, place.getServiziStr());
        values.put(MySQLiteHelper.COLUMN_MEDICO, place.getInformazioneIndex(0));
        values.put(MySQLiteHelper.COLUMN_FARMACIA, place.getInformazioneIndex(1));
        values.put(MySQLiteHelper.COLUMN_OSPEDALE, place.getInformazioneIndex(2));
        values.put(MySQLiteHelper.COLUMN_CARABINIERI, place.getInformazioneIndex(3));
        values.put(MySQLiteHelper.COLUMN_PPROVINCIALE, place.getInformazioneIndex(4));
        values.put(MySQLiteHelper.COLUMN_PLOCALE, place.getInformazioneIndex(5));

        return values;
    }

    // from database to Object
    private Place cursorToPlace(Cursor cursor) {
        long id=cursor.getLong(0);
        double lat =cursor.getDouble(1);
        double lng =cursor.getDouble(2);
        String comune = cursor.getString(3);
        String localita = cursor.getString(4);
        String provincia = cursor.getString(5);
        int clas =cursor.getInt(6);
        int divieto =cursor.getInt(7);
        String image = cursor.getString(8);

        String servizi = cursor.getString(9);
        ArrayList<InformazioneUtile> infoVec = new ArrayList<>();

        for(int i=0;i<DetailsActivity.numInfo;i++) {
            String strInfo=cursor.getString(i + 10);
            String[] arrayInfo=strInfo.split(",,");
            InformazioneUtile info = new InformazioneUtile(arrayInfo[0],arrayInfo[1],arrayInfo[2]);

            infoVec.add(info);

        }

       return new Place(id,lat,lng,comune,localita,provincia,clas,divieto,image,servizi,infoVec);
    }

    public Place insertPlace(Place place) {
        long insertId = database.insertOrThrow(MySQLiteHelper.TABLE_PLACE, null,
                placeToValues(place));
        // now read from DB the inserted person and return it

        Cursor cursor = database.query(MySQLiteHelper.TABLE_PLACE, allColumns,
                MySQLiteHelper.COLUMN_ID + " = ?",
                new String[]{"" + insertId}, null, null, null);
        cursor.moveToFirst();
        Place p = cursorToPlace(cursor);
        cursor.close();
        return p;
    }

    public void deletePlace(Place place) {
        long id = place.getID();

        database.delete(MySQLiteHelper.TABLE_PLACE,
                MySQLiteHelper.COLUMN_ID + " = ?",
                new String[]{"" + id});
    }

    public void deleteAllPlace(ArrayList<Place> places) {
        for(int i=0;i< places.size();i++) {
            long id = places.get(i).getID();

            database.delete(MySQLiteHelper.TABLE_PLACE,
                    MySQLiteHelper.COLUMN_ID + " = ?",
                    new String[]{"" + id});
        }
    }

    public void removeAll()
    {
        // db.delete(String tableName, String whereClause, String[] whereArgs);
        // If whereClause is null, it will delete all rows.
        database.delete(MySQLiteHelper.TABLE_PLACE, null, null);
    }
    public ArrayList<Place> getAllPlaces() {
        ArrayList<Place> places = new ArrayList<>();
        Cursor cursor = database.query(MySQLiteHelper.TABLE_PLACE,
                allColumns,null,null,null,null,null);
        cursor.moveToFirst();
        while(!cursor.isAfterLast()) {
            Place place = cursorToPlace(cursor);
            places.add(place);
            cursor.moveToNext();
        }
        cursor.close(); // remember to always close the cursor!
        return places;
    }
}
