package it.inserrafesta.iseomap.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SearchViewCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
                    Toast.makeText(getActivity().getApplicationContext(), "Mappa satellitare", Toast.LENGTH_SHORT).show();
                    googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                }else{
                    Toast.makeText(getActivity().getApplicationContext(), "Mappa stradale", Toast.LENGTH_SHORT).show();
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
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        googleMap = mMapView.getMap();
        try {
            // Loading map
            initilizeMap();
            initializeUiSettings();
            initializeMapLocationSettings();
            initializeMapType();

        } catch (Exception e) {
            e.printStackTrace();
        }
// TODO sistemare!!
   /*     intent = new Intent(getActivity(), DetailsActivity.class);
        final Button button = (Button) v.findViewById(R.id.detailsButton);

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v2) {
                (getActivity()).startActivity(intent);
            }
        });
*/

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
    }

    private void initilizeMap() {

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
                    ImageView imageinfo= (ImageView) v.findViewById(R.id.image_info);

                    myTitle.setText(str2[0]);// got first string as title
                    mysnippet.setText(marker.getSnippet());

                    int image=0;
                    if(str2[1].equals("1")){
                        image=R.drawable.marker_divieto;
                    }else {
                        switch (str2[2]) {
                            case "1":
                                image = R.drawable.marker3;
                                break;
                            case "2":
                                image = R.drawable.marker2;
                                break;
                            case "3":
                                image = R.drawable.marker1;
                                break;
                            case "4":
                                image = R.drawable.marker0;
                                break;
                            default:
                                break;
                        }
                    }
                  // TODO non funge il pulsante dettagli
                    imageinfo.setImageResource(image);

              /*      final Intent intent = new Intent(getActivity(), DetailsActivity.class);
                    final Button button = (Button) v.findViewById(R.id.detailsButton);

                    button.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            //Toast.makeText(getActivity().getBaseContext(), "Cliccato!!!", Toast.LENGTH_SHORT).show();

                            startActivity(intent);
                        }
                    });
*/
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
                System.out.print(position.zoom);

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

    public void goDetails(){
        startActivity(new Intent(getActivity(), DetailsActivity.class));
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
                Place p = new Place(json.getString("comune"),json.getString("localita"),json.getString("provincia"),json.getDouble("lat"),json.getDouble("lng"),json.getInt("classificazione"),json.getInt("divieto"),json.getString("image"));
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
        // custom dialog
        Context context = getActivity().getApplicationContext();
        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.popup_custom);
        dialog.setTitle("Istruzioni");

        // set the custom dialog components - text, image and button
        //TextView text = (TextView) dialog.findViewById(R.id.text);
        //text.setText("Android custom dialog example!");
        //ImageView image = (ImageView) dialog.findViewById(R.id.image);
        //image.setImageResource(R.mipmap.ic_icon);

        Button dialogButton = (Button) dialog.findViewById(R.id.dialogButtonOK);
        // if button is clicked, close the custom dialog
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
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
