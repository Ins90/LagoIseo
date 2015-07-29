package it.inserrafesta.iseomap.fragment;

import it.inserrafesta.iseomap.InformazioneUtile;
import it.inserrafesta.iseomap.PlaceDB;
import it.inserrafesta.iseomap.adapter.PopupAdapterMap;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import it.inserrafesta.iseomap.ConnectionDetector;
import it.inserrafesta.iseomap.R;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

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
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import it.inserrafesta.iseomap.Place;
import it.inserrafesta.iseomap.activity.DetailsActivity;

public class MapFragment extends Fragment implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {
    private PlaceDB dbPlace;

    static final LatLng INITIAL_LATLNG = new LatLng(45.733815962451354, 10.05103312432766);
    final int INITIAL_ZOOM = 11;
    MapView mMapView;
    private static GoogleMap googleMap;
    public static ArrayList<Place>  places = new ArrayList<>();
    Bundle bundle;
    boolean change=true;
    Context context;

    SharedPreferences prefs;
    SharedPreferences prefLat;
    SharedPreferences prefLng;
    SharedPreferences prefGPS;
    private GoogleApiClient mGoogleApiClient;
    private Handler handler= new Handler();
    private boolean locManDisable=false;
    private int timeRefreshGPS;
    private final String TAG = "IseoAcque";
    static String[] serviziNomiArray;
    public static Vector<String> serviziNomi;
    private Handler mHandler = new Handler();
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_map, menu);
    }


    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id){
            case R.id.action_home:
                if((System.currentTimeMillis()/1000)-prefGPS.getLong("timeToGps",0)>timeRefreshGPS) {
                    //Toast.makeText( context,"tempo salvato: "+String.valueOf(System.currentTimeMillis()/1000-prefGPS.getLong("timeToGps",0)) +"tempo",Toast.LENGTH_SHORT).show();
                    mGoogleApiClient.connect();
                }else{
                    mGoogleApiClient.disconnect();
                    locManDisable=true;
                }

                Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.zoomMap), Toast.LENGTH_SHORT).show();
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(INITIAL_LATLNG, INITIAL_ZOOM));
              //    googleMap.getUiSettings().setScrollGesturesEnabled(false);
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
                    Toast.makeText(context, getResources().getString(R.string.map_sat), Toast.LENGTH_SHORT).show();
                    googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                }else{
                    Toast.makeText(context, getResources().getString(R.string.map_str), Toast.LENGTH_SHORT).show();
                    googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                }
                break;
            case R.id.action_info:
                showLegend();

                break;
            case R.id.action_refresh:

                BackgroundTask task = new BackgroundTask(getActivity());
                task.execute();

                break;
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        
        context = getActivity().getApplicationContext();
        bundle=getArguments(); //necessario per le preferenze passate dalla mainActivity
        setHasOptionsMenu(true);
        prefs = context.getSharedPreferences("time", Context.MODE_PRIVATE);

        String services=getResources().getString(R.string.allServices);
        serviziNomiArray=services.split(",");

        serviziNomi= new Vector<>(Arrays.asList(serviziNomiArray));
        Log.d("Adapter", "array " + serviziNomi.size());

        //cerco di ottenere la posizione direttamente appena si apre l app
        prefGPS = context.getSharedPreferences("timeToGps", Context.MODE_PRIVATE);
        prefLat = context.getSharedPreferences("prefLat", Context.MODE_PRIVATE);
        prefLng = context.getSharedPreferences("prefLng", Context.MODE_PRIVATE);

        mGoogleApiClient = new GoogleApiClient.Builder(getActivity().getApplicationContext())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        Runnable closeLocation = new Runnable() {
            @Override
            public void run() {
                if (!locManDisable) {
                    mGoogleApiClient.disconnect();
                    locManDisable = true;
                    // Toast.makeText(getApplicationContext(), "blocco runnable ",Toast.LENGTH_SHORT ).show();
                }
            }
        };

        handler.postDelayed(closeLocation, 20000); //Dopo 20 secondi interrompe la localizzazione
        timeRefreshGPS=60*20; // le coordinate gps rimangono valide per i 20 minuti successivi


        if((System.currentTimeMillis()/1000)-prefGPS.getLong("timeToGps",0)>timeRefreshGPS) {
            //Toast.makeText( context,"tempo salvato: "+String.valueOf(System.currentTimeMillis()/1000-prefGPS.getLong("timeToGps",0)) +"tempo",Toast.LENGTH_SHORT).show();
            mGoogleApiClient.connect();
        }else{
            mGoogleApiClient.disconnect();
            locManDisable=true;
        }

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

            googleMap.setInfoWindowAdapter(new PopupAdapterMap(context,
                    getActivity().getLayoutInflater()));
        }

        //Move the camera instantly to center of lake Iseo
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(INITIAL_LATLNG, INITIAL_ZOOM));

        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getActivity().getBaseContext());
        int timeRefresh =Integer.parseInt(SP.getString("timeRefresh", "12"));
        timeRefresh=timeRefresh*60*60; //il tempo tra un aggiornmaento e l'altro espresso in secondi

        //Toast.makeText(getActivity().getApplicationContext(), "tempo di aggiornamento "+timeRefresh, Toast.LENGTH_SHORT).show();

        if((System.currentTimeMillis()/1000)-prefs.getLong("time",0)>timeRefresh) {
            ConnectionDetector cd = new ConnectionDetector(getActivity().getApplicationContext());
            Boolean isInternetPresent = cd.isConnectingToInternet();

            if (!isInternetPresent) {
               Toast.makeText(getActivity().getApplicationContext(), R.string.noConnection, Toast.LENGTH_SHORT).show();
             }else {
                checkStatusConnection();
            }
        }else {
           // Log.v("dii2iiiiiiiiiiiiiii", String.valueOf(prefs.getLong("time", 0)));

            dbPlace = new PlaceDB();
            dbPlace.open();
            places = dbPlace.getAllPlaces();
            putMakers(places, googleMap);
            Toast.makeText(getActivity().getApplicationContext(), R.string.noUpdate, Toast.LENGTH_SHORT).show();

        }

        /* Se lo zoom e troppo basso ritorno allo zoom minimo consentito*/
        googleMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                // Make a web call for the locations
                int minZoom = 11;
                CameraPosition position = googleMap.getCameraPosition();
                System.out.print("Zoom level: " + position.zoom);

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
     * Aggiunge i makers alla mappa
     */
    private void putMakers(List<Place> places,GoogleMap mMap){
        for(int i=0;i<places.size();i++) {
            //places.get(i).makeMaker(mMap);

            int image = 0;

            if (places.get(i).getDivieto() == 1) {
                image = R.drawable.marker_divieto;
            } else {
                switch (places.get(i).getClassificazione()) {
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

            String title = getActivity().getResources().getString(R.string.localita) +" "+ places.get(i).getLocalita() + "_" + places.get(i).getDivieto() + "_" + places.get(i).getClassificazione() + "_" + places.get(i).getImageUrl();

            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(places.get(i).getLat(), places.get(i).getLng()))
                    .title(title)
                    .icon(BitmapDescriptorFactory.fromResource(image))
                    .snippet(getActivity().getResources().getString(R.string.comune)+ ": " + places.get(i).getComune()));
        }
    }

    public void showLegend(){

        final Dialog dialog = new Dialog(getActivity(),R.style.Dialog);
        dialog.setContentView(R.layout.popup_custom);
        //ImageView markerImg=(ImageView) v.findViewById(R.id.image_info);
        dialog.setTitle(R.string.titleLegend);
        float scale=getDensityScale();
        float myWidth = 250;
        float myHeight = 420;
        myWidth=myWidth*scale;
        myHeight=myHeight*scale;
        //Log.v("ConvertView", "Width " + myWidth + ", Height " + myHeight);

        dialog.getWindow().setLayout((int) myWidth, (int) myHeight);
        dialog.show();
    }

    private float getDensityScale()
    {
        final DisplayMetrics metrics =
                Resources.getSystem().getDisplayMetrics();
        return metrics.density;
    }

    /*
* Converte un oggetto JSONArray in Vector<Place>
*/
    private void JSONArrayToVector(JSONArray jsonArray) {
        for(int i=0;i<jsonArray.length();i++){
            JSONObject json;
            try{
                json = jsonArray.getJSONObject(i);
                ArrayList<String> serviziVec= new ArrayList<>();
                for(int j=1;j<=DetailsActivity.getNumServizi();j++) {
                    String servizioString = json.getString(Integer.toString(j));
                    // Log.v("URddL", servizioString);

                    serviziVec.add(servizioString);
                }
                ArrayList<InformazioneUtile> infoVec = new ArrayList<>();
                for(int infoIndex=1;infoIndex<=DetailsActivity.numInfo;infoIndex++){
                    String nome,telefono,indirizzo;
                    nome = json.getString("nome_"+infoIndex);
                    telefono = json.getString("telefono_"+infoIndex);
                    indirizzo = json.getString("indirizzo_" + infoIndex);
                    InformazioneUtile info = new InformazioneUtile(nome,indirizzo,telefono);
                    infoVec.add(info);
                }
              //  Place p =dbPlace.insertPlace(new Place (json.getLong("ID"),json.getDouble("lat"),json.getDouble("lng"),json.getString("comune"),json.getString("localita"),
              //          json.getString("provincia"),json.getInt("classificazione"),json.getInt("divieto"),json.getString("image"),serviziVec.toString()));
                Place p = new Place (json.getLong("ID"),json.getString("id_asl"),json.getDouble("lat"),json.getDouble("lng"),json.getString("comune"),json.getString("indirizzo"),json.getString("localita"),json.getString("provincia"),json.getInt("classificazione"),json.getInt("divieto"),json.getString("image"),serviziVec.toString(),infoVec);
                dbPlace.insertPlace(p);
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
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
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

    protected void checkStatusConnection(){
     /*check if is present internet connection */
       // ConnectionDetector cd = new ConnectionDetector(getActivity().getApplicationContext());
       // Boolean isInternetPresent = cd.isConnectingToInternet();

        //if (!isInternetPresent) {
         //   Toast.makeText(getActivity().getApplicationContext(), R.string.noConnection, Toast.LENGTH_SHORT).show();
       // }else{
            if(places.size()!=0) {
                dbPlace.removeAll();
                places= new ArrayList<>();
            }
            dbPlace = new PlaceDB();
            dbPlace.open();
            JSONArray jsonArrayPlaces = getJSONFromDB();
            JSONArrayToVector(jsonArrayPlaces);

            googleMap.clear();
            putMakers(places, googleMap);
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    Toast.makeText(getActivity().getApplicationContext(), R.string.updateOK, Toast.LENGTH_SHORT).show();
                }
            }, 1800);
            prefs.edit().putLong("time", System.currentTimeMillis() / 1000).apply();

        //}
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

    @Override
    public void onConnected(Bundle bundle) {
        if((System.currentTimeMillis()/1000)-prefGPS.getLong("timeToGps",0)>timeRefreshGPS) {
            LocationRequest mLocationRequest = LocationRequest.create();
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            // mLocationRequest.setInterval(10000); // Update location every 10 second

            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);
        }else {
            LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "GoogleApiClient connection has been suspend");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(TAG, "GoogleApiClient connection has failed");
    }

    @Override
    public void onLocationChanged(Location location) {
       // Toast.makeText(context, "Location received: " + location.getLatitude(), Toast.LENGTH_SHORT).show();
        prefLat.edit().putString("prefLat", String.valueOf(location.getLatitude())).apply();
        prefLng.edit().putString("prefLng", String.valueOf(location.getLongitude())).apply();
        prefGPS.edit().putLong("timeToGps", System.currentTimeMillis() / 1000).apply();

    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private class BackgroundTask extends AsyncTask<Void, Void, Void> {
        private ProgressDialog dialog;

        public BackgroundTask(Activity activity) {
            dialog = new ProgressDialog(activity);
        }

        @Override
        protected void onPreExecute() {
            dialog.setMessage(getResources().getString(R.string.update));
            dialog.show();
        }

        @Override
        protected void onPostExecute(Void result) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }

        @Override
        protected Void doInBackground(Void... params) {
            ConnectionDetector cd = new ConnectionDetector(getActivity().getApplicationContext());
            Boolean isInternetPresent = cd.isConnectingToInternet();

            if (!isInternetPresent) {
                getActivity().runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        Toast.makeText(getActivity().getApplicationContext(), R.string.noConnection, Toast.LENGTH_SHORT).show();

                    }
                });
            } else {
                try {
                    mHandler.post(new Runnable() {
                        public void run() {
                            checkStatusConnection();
                        }
                    });
                    Thread.sleep(2000);
                } catch (
                        InterruptedException e
                        )
                {
                    e.printStackTrace();
                }
            }
                return null;
            }
            }
}
