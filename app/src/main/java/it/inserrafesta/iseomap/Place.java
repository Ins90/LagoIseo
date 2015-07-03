package it.inserrafesta.iseomap;

import android.util.Log;

import it.inserrafesta.iseomap.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by Nicolo on 09/06/2015.
 */
public class Place {
    private String comune;
    private String localita;
    private String provincia;
    private String title;
    private double lat;
    private double lng;
    private int classificazione; /* 1 eccellente 2 buono 3 sufficiente 4 scarso */
    private int divieto; /* 1 SI 0 NO */

    public Place( String _comune, String _localita, String _provincia,double _lat, double _lng, int _classificazione, int _divieto){
        comune = _comune;
        localita = _localita;
        provincia = _provincia;
        lat = _lat;
        lng = _lng;
        classificazione = _classificazione;
        divieto = _divieto;
    }

    public void makeMaker(GoogleMap mMap){
    int image = 0;
        String s = Integer.toString(divieto);

    if(divieto==1){
        image= R.drawable.divieto;
    }else {
        switch (classificazione) {
            case 1:
                image = R.drawable.eccellente;
                break;
            case 2:
                image = R.drawable.buona;
                break;
            case 3:
                image = R.drawable.sufficiente;
                break;
            case 4:
                image = R.drawable.scarsa;
                break;
            default:
                break;
        }
    }
        title="Localit\u00E0 " + localita+"_"+divieto+"_"+classificazione;
    mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(lat, lng))
                    .title(title)
                    .icon(BitmapDescriptorFactory.fromResource(image))
                    .snippet("Comune: " + comune)
    );

    }


    public double getLng() {
        return lng;
    }

    public double getLat() {
        return lat;
    }

    public String getComune() {
        return comune;
    }

    public String getLocalita() {
        return localita;
    }

    public String getProvincia() {
        return provincia;
    }

    public int getClassificazione() {
        return classificazione;
    }

    public int getDivieto() {
        return divieto;
    }
}
