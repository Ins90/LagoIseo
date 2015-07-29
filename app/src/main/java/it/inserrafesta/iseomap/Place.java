package it.inserrafesta.iseomap;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Place {

    private long ID;
    private String id_asl;
    private String comune;
    private String indirizzo;
    private String localita;
    private String provincia;
    private double lat;
    private double lng;
    private int classificazione; /* 1 eccellente 2 buono 3 sufficiente 4 scarso */
    private int divieto; /* 1 SI 0 NO */
    private String imageUrl;
    private ArrayList<Boolean> serviziVec=new ArrayList<>();
    private ArrayList<InformazioneUtile> infoVec = new ArrayList<>();
    private String serviziStr;


    public Place(long id, String _id_asl, double _lat, double _lng, String _comune, String _indirizzo, String _localita, String _provincia, int _classificazione, int _divieto, String _imageUrl,String serviziString,ArrayList<InformazioneUtile> _infoVec){

        serviziStr=serviziString;
        infoVec = _infoVec;

        ID=id;
        id_asl=_id_asl;
        comune = _comune;
        indirizzo=_indirizzo;
        localita = _localita;
        provincia = _provincia;
        lat = _lat;
        lng = _lng;
        classificazione = _classificazione;
        divieto = _divieto;
        imageUrl = _imageUrl;
        ArrayList<String> serviziVecTemp = new ArrayList<>();
        serviziVecTemp.addAll(Arrays.asList(serviziString.substring(1, serviziString.length() - 1).split(", ")));
        serviziVec=getBooleanArray(serviziVecTemp);

    }

    private ArrayList<Boolean> getBooleanArray(ArrayList<String> stringArray) {
        ArrayList<Boolean> result = new ArrayList<>();
        for(String stringValue : stringArray) {
                Boolean servizio = false;
                if(stringValue.equals("1"))
                    servizio = true;
            result.add(servizio);
        }
        return result;
    }

    public void makeMaker(GoogleMap mMap){
    int image = 0;

    if(divieto==1){
        image= R.drawable.marker_divieto;
    }else {
        switch (classificazione) {
            case 1:
                image = R.drawable.marker_1;
                break;
            case 2:
                image = R.drawable.marker_2;
                break;
            case 3:
                image = R.drawable.marker_3;
                break;
            case 4:
                image = R.drawable.marker_4;
                break;
            default:
                break;
        }
    }
        String title = "Localit\u00E0 " + localita + "_" + divieto + "_" + classificazione + "_" + imageUrl;

        mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(lat, lng))
                    .title(title)
                    .icon(BitmapDescriptorFactory.fromResource(image))
                    .snippet("Comune: " + comune));  //TODO codificare in string.xml

        //MapFragment.imageStringMapMarker.put(marker,imageUrl);
    }

    public String getServiziStr() {
        return serviziStr;
    }

    public ArrayList<Boolean> getServiziVec() {
        return serviziVec;
    }

    public boolean[] getServiziVectoArray() {
        //Boolean[] serviziArr = new Boolean[serviziVec.size()];
        //serviziArr = serviziVec.toArray(serviziArr);
        return toPrimitiveArray(serviziVec);
    }

    private boolean[] toPrimitiveArray(final List<Boolean> booleanList) {
        final boolean[] primitives = new boolean[booleanList.size()];
        int index = 0;
        for (Boolean object : booleanList) {
            primitives[index++] = object;
        }
        return primitives;
    }

    public String getImageUrl() {
        return imageUrl;
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

    public ArrayList<InformazioneUtile> getInfoVec() {
        return infoVec;
    }

    public String getInformazioneIndex(int indexInfo){
        String strInfo;
        if(infoVec.get(indexInfo).getNome().isEmpty()){
            strInfo="null";
        }else{
            strInfo=infoVec.get(indexInfo).getNome();
        }
        strInfo=strInfo+",,";
        if(infoVec.get(indexInfo).getIndirizzo().isEmpty()){
            strInfo=strInfo+"null";
        }else{
            strInfo=strInfo+infoVec.get(indexInfo).getIndirizzo();
        }
        strInfo=strInfo+",,";

        if(infoVec.get(indexInfo).getTelefono().isEmpty()){
            strInfo=strInfo+"null";
        }else{
            strInfo=strInfo+infoVec.get(indexInfo).getTelefono();
        }

        return strInfo;
    }

    public long getID() {
        return ID;
    }

    public String getId_asl() {
        return id_asl;
    }

    public String getIndirizzo() {
        return indirizzo;
    }

}
