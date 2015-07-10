package it.inserrafesta.iseomap.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import it.inserrafesta.iseomap.R;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.squareup.picasso.Picasso;

import android.support.v4.app.Fragment;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Vector;

import it.inserrafesta.iseomap.Place;
import it.inserrafesta.iseomap.activity.DetailsActivity;

public class MapFragment extends Fragment{

    static final LatLng INITIAL_LATLNG = new LatLng(45.733815962451354, 10.05103312432766);
    final int INITIAL_ZOOM = 11;
    MapView mMapView;
    private static GoogleMap googleMap;
    private static CameraUpdate cu;
    public static Vector<Place>  places = new Vector<Place>();
    Bundle bundle;
    TextView mSearchText;
    boolean change=true;
    Context context;
    ArrayList<Place> pointList = new ArrayList<Place>();
   // private float previousZoomLevel = 13; TODO sistemare bloccaggio mappa
  //  private boolean isZooming=false;

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_map, menu);
    }


    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id){
            case R.id.action_home:
                    Toast.makeText(getActivity().getApplicationContext(), "Centramento mappa ...", Toast.LENGTH_SHORT).show();
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(INITIAL_LATLNG, INITIAL_ZOOM));
              //    googleMap.getUiSettings().setScrollGesturesEnabled(false);

                //zoomAnimateLevelToFitMarkers(120);
                break;
            case R.id.action_terrain:
                if(change){
                    item.setIcon(R.drawable.ic_satellite_white_36dp);
                    change=false;
                }else{
                    item.setIcon(R.drawable.ic_street_white_36dp);
                    change=true;
                }

                if(googleMap.getMapType()==GoogleMap.MAP_TYPE_NORMAL){
                    Toast.makeText(context, "Mappa satellitare", Toast.LENGTH_SHORT).show();
                    googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                }else{
                    Toast.makeText(context, "Mappa stradale", Toast.LENGTH_SHORT).show();
                    googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                }
                break;
            case R.id.action_info:
                showLegend();

                break;
        }
        return super.onOptionsItemSelected(item);

    }


    // Context context = getActivity().getApplicationContext();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getActivity().getApplicationContext();
        bundle=getArguments(); //necessario per le preferenze passate dalla mainActivity
        setHasOptionsMenu(true);

        /*
         * Istruzioni magiche per consentire la connessione a internet
         */
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        View v = inflater.inflate(R.layout.map_fragment, container,
                false);
        mMapView = (MapView) v.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume();// needed to get the map to display immediately

        try {
            MapsInitializer.initialize(context);
        } catch (Exception e) {
            e.printStackTrace();
        }

        googleMap = mMapView.getMap();
        try {
            // Loading map
            initializeMap();
            initializeUiSettings();
            initializeMapLocationSettings();
            initializeMapType();

        } catch (Exception e) {
            e.printStackTrace();
        }
      return v;
    }

    //la main activity setta il tipo di mappa -> prova perchè non � efficace! usare switch sulla mappa
    public void initializeMapType() {
        boolean sat=bundle.getBoolean("OptSatellite", true);
        if(sat) {
            googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        }else{
            googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        }
    }

    private void initializeMapLocationSettings() {
        googleMap.setMyLocationEnabled(false);
    }

    private void initializeUiSettings() {
        googleMap.getUiSettings().setCompassEnabled(true);
        googleMap.getUiSettings().setRotateGesturesEnabled(false);
        googleMap.getUiSettings().setTiltGesturesEnabled(true);
        googleMap.getUiSettings().setZoomControlsEnabled(false);
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
       // googleMap.getUiSettings().setScrollGesturesEnabled(false);
    }

    private void initializeMap() {

        /* Qui creo la mia view personalizzata per i marker, sfruttando l xml "custom info contents" */
        if (googleMap != null){
            googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                @Override
                public View getInfoWindow(Marker marker) {
                    return null;
                }

                @Override
                public View getInfoContents(Marker marker) {
                    /* qui assegno tutte le variabili all'xml */

                    //View v = inflater.inflate(R.layout.custom_info_contents, null);
                    //LayoutInflater inflater = LayoutInflater.from(context);
                    View v = getActivity().getLayoutInflater().inflate(R.layout.custom_info_contents, null);

                    String str=marker.getTitle();
                    final String[] str2=str.split("_");

                    TextView myTitle = (TextView) v.findViewById(R.id.my_title);


                    TextView mysnippet= (TextView) v.findViewById(R.id.my_snippet);
                    TextView myquality= (TextView) v.findViewById(R.id.qualityWater);
                    TextView divietoA= (TextView) v.findViewById(R.id.divietoAcqua);

                    ImageView imageinfo= (ImageView) v.findViewById(R.id.image_info);
                    imageinfo.setScaleType(ImageView.ScaleType.FIT_XY);
                    myTitle.setText(str2[0]);// got first string as title
                    mysnippet.setText(marker.getSnippet());

                    int image=0;
                    if(str2[1].equals("1")){
                        image=R.drawable.marker_divieto;
                        divietoA.setText(R.string.prohibition);

                    }

                        switch (str2[2]) {
                            case "1":
                                image = R.drawable.marker3;
                                myquality.setText(R.string.qlt_ecc);
                                break;
                            case "2":
                                image = R.drawable.marker2;
                                myquality.setText(R.string.qlt_buo);

                                break;
                            case "3":
                                image = R.drawable.marker1;
                                myquality.setText(R.string.qlt_suf);

                                break;
                            case "4":
                                image = R.drawable.marker0;
                                myquality.setText(R.string.qlt_sca);

                                break;
                            default:
                                break;
                        }

                    Picasso.with(context)
                            .load(str2[3])
                            .resize(80*(int) getDensityScale(),80* (int) getDensityScale() )
                            .centerCrop()
                            .placeholder(null)
                            .error(R.drawable.placeholder1).into(imageinfo);

                    return v;
               }
           });
        }

        //Move the camera instantly to center of lake Iseo
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(INITIAL_LATLNG, INITIAL_ZOOM));

        /* 1  Creo il JSON dal db, 2 trasformo in un vector di markers, 3 inserisco i marker sulla mappa */
        JSONArray jsonArrayPlaces = getJSONFromDB();
        JSONArrayToVector(jsonArrayPlaces, places);
        putMakers(places, googleMap);

        /* Se lo zoom e troppo basso ritorno allo zoom minimo consentito*/
        googleMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                // Make a web call for the locations
                int minZoom = 11;
                CameraPosition position = googleMap.getCameraPosition();
                System.out.print("Zoom level: "+position.zoom);

              //  if(previousZoomLevel < position.zoom)
              //  {
               //     isZooming = true;
               //     googleMap.getUiSettings().setScrollGesturesEnabled(true);

              //  }
                //String s = Float.toString(position.zoom);
                //Log.d(TAG_LOG,s);
                if (position.zoom < minZoom) {
                    googleMap.animateCamera(CameraUpdateFactory.zoomTo(minZoom));
                }
            }
        });

        googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                // Toast.makeText(getActivity().getBaseContext(), marker.getTitle().split("_")[0].substring(9), Toast.LENGTH_SHORT).show();
                final Intent intent = new Intent(getActivity(), DetailsActivity.class);
                intent.putExtra("localita", marker.getTitle().split("_")[0].substring(9));
                startActivity(intent);
            }
        });

    }

    /*
    * Sposta la visuale della mappa in modo che tutti i markers siano visibili
    */
    public static void zoomAnimateLevelToFitMarkers(int padding) {
        LatLngBounds.Builder b = new LatLngBounds.Builder();
        for(int i=0;i<places.size();i++){
            LatLng ll = new LatLng(places.get(i).getLat(), places.get(i).getLng());
            b.include(ll);
        }

        LatLngBounds bounds = b.build();

        // Change the padding as per needed
        cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        googleMap.animateCamera(cu);
    }

    /*
     * Converte un oggetto JSONArray in Vector<Place>
     */
    private void JSONArrayToVector(JSONArray jsonArray,Vector<Place> places) {
        String s = "";
        for(int i=0;i<jsonArray.length();i++){
            JSONObject json = null;
            try{
                json = jsonArray.getJSONObject(i);
                Vector<Boolean> serviziVec= new Vector<Boolean>();
               for(int j=1;j<=DetailsActivity.serviziNomi.size();j++) {
                   String servizioString = json.getString(Integer.toString(j));
                   Boolean servizio = false;
                   if(servizioString.equals("1"))
                       servizio = true;
                   serviziVec.add(servizio);
                }
                Place p = new Place(json.getString("comune"),json.getString("localita"),
                        json.getString("provincia"),json.getDouble("lat"),json.getDouble("lng"),
                        json.getInt("classificazione"),json.getInt("divieto"),json.getString("image"),serviziVec);
                places.add(p);

            }catch(JSONException e){
                e.printStackTrace();
            }
        }
    }

    /*
     * Ottiene un oggetto JSONArray dal DB remoto
     */
    private JSONArray getJSONFromDB(){
        JSONArray jsa = null;
        String result = "";
        InputStream isr = null;
        try{
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://iseomap.altervista.org/get_loc.php"); //YOUR PHP SCRIPT ADDRESS
            // HttpPost httppost = new HttpPost("http://172.23.193.32/elift-test/myfile.php"); //YOUR PHP SCRIPT ADDRESS
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();
            isr = entity.getContent();
        }
        catch(Exception e){
            Log.e("log_tag", "Error in http connection "+e.toString());
          //  resultView.setText("Couldnt connect to database");
        }
        //convert response to string
        try{
            BufferedReader reader = new BufferedReader(new InputStreamReader(isr,"iso-8859-1"),8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            isr.close();
            result=sb.toString();
        }
        catch(Exception e){
            Log.e("log_tag", "Error  converting result "+e.toString());
        }

        try {
            jsa = new JSONArray(result);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsa;
    }

    /*
     * Aggiunge i makers alla mappa passata costruendoli da places
     */
    private void putMakers(Vector<Place> places,GoogleMap mMap){
        for(int i=0;i<places.size();i++)
            places.get(i).makeMaker(mMap);
    }

    public void showLegend(){

        final Dialog dialog = new Dialog(getActivity(),R.style.Dialog);
        dialog.setContentView(R.layout.popup_custom);
        //ImageView markerImg=(ImageView) v.findViewById(R.id.image_info);
        dialog.setTitle("Legenda");
        float scale=getDensityScale();
        float myWidth = 250;
        float myHeight = 420;
        myWidth=myWidth*scale;
        myHeight=myHeight*scale;
        Log.v("ConvertView", "Width "+myWidth+", Height "+myHeight);

        // set the custom dialog components - text, image and button
        //TextView text = (TextView) dialog.findViewById(R.id.text);
        //text.setText("Android custom dialog example!");
        //ImageView image = (ImageView) dialog.findViewById(R.id.image);
        //image.setImageResource(R.mipmap.ic_icon);

        dialog.getWindow().setLayout((int) myWidth, (int) myHeight);
        dialog.show();
    }

    private final float getDensityScale()
    {
        final DisplayMetrics metrics =
                Resources.getSystem().getDisplayMetrics();
        return metrics.density;
    }

    @Override
    public void onResume() {
        try{
            super.onResume();
            mMapView.onResume();
        }catch(NullPointerException e){
            Log.d("onResume", "NullPointerException: " + e);
        }
    }

    @Override
    public void onDestroy() {
        try{
            super.onDestroy();
            mMapView.onDestroy();
        }catch(NullPointerException e){
            Log.d("onDestroy", "NullPointerException: " + e);
        }
    }

    @Override
    public void onLowMemory() {
        try{
            super.onLowMemory();
            mMapView.onLowMemory();
        }catch(NullPointerException e){
            Log.d("onLowMemory", "NullPointerException: " + e);
        }
    }
}
