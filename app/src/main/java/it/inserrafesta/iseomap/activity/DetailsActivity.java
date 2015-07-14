package it.inserrafesta.iseomap.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.widget.Toolbar;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;

import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.GridLayout;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Vector;

import it.inserrafesta.iseomap.R;
import it.inserrafesta.iseomap.fragment.MapFragment;


public class DetailsActivity extends AppCompatActivity {
    static String[] serviziNomiArray = {"Area picnic", "Parco giochi","Servizi Igienici","Bar","Ristorante","Parcheggio"};
    public static Vector<String> serviziNomi = new Vector<>(Arrays.asList(serviziNomiArray));
    private ArrayList<Boolean> serviziVec;
    private String comune;
    private String localita;
    private String provincia;
    private double lat;
    private double lng;
    private int classificazione; /* 1 eccellente 2 buono 3 sufficiente 4 scarso */
    private int divieto; /* 1 SI 0 NO */
    private String imageUrl;
    private LocationManager locationManager;
    private LocationListener mlocListener;
    private boolean locManDisable=false;
    private Runnable closeLocation;
    private Handler handler= new Handler();
    private float distanza; //in Km
    private String bestProvider;
    SharedPreferences prefs;
    //SharedPreferences prefsLat;
    //SharedPreferences prefsLng;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        Context context = getApplication();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        Bundle extras = getIntent().getExtras();
        localita = extras.getString("localita");
        prefs = context.getSharedPreferences("timeToGps", Context.MODE_PRIVATE);

        initialise();

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Get the location manager
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        //timeToStopGPS=System.currentTimeMillis()/1000;

        int timeRefreshGPS=20*60; // le coordinate gps rimangono valide per i 20 minuti successivi
        Criteria criteria = new Criteria();
        bestProvider = locationManager.getBestProvider(criteria, false);
        Location location = locationManager.getLastKnownLocation(bestProvider);

        /* Se dopo 10 secondi il gps non ha trovato la posizione elimino la richiesta per risparmiare la batteria */
        //final Handler handler = new Handler();

        closeLocation=new Runnable() {
            @Override
            public void run() {
                if(!locManDisable) {
                    locationManager.removeUpdates(mlocListener);
                    //locationManager = null;
                    locManDisable=true;
                    //Toast.makeText(getApplicationContext(), "wwww ",Toast.LENGTH_SHORT ).show();
                    //Do something after 10000ms
                }
            }
        };

        handler.postDelayed(closeLocation, 20000);

        // TODO implementare una variabile in preferences per mantenere le coordinate del gps per 10 minuti

        for (int i = 0; i < MapFragment.places.size(); i++) {
            if (MapFragment.places.get(i).getLocalita().equals(localita)) {
                comune = MapFragment.places.get(i).getComune();
                provincia = MapFragment.places.get(i).getProvincia();
                lat = MapFragment.places.get(i).getLat();
                lng = MapFragment.places.get(i).getLng();
                classificazione = MapFragment.places.get(i).getClassificazione();
                divieto = MapFragment.places.get(i).getDivieto();
                imageUrl = MapFragment.places.get(i).getImageUrl();
                serviziVec = MapFragment.places.get(i).getServiziVec();
                break;
            }
        }

        if((System.currentTimeMillis()/1000)-prefs.getLong("timeToGps",0)>timeRefreshGPS) {

            mlocListener = new MyLocationListener();
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 6000, 50, mlocListener);

        }else{
            locManDisable=true;
            //Utilizzo location per creare la distanza!!!
            distanza=getDistance(location.getLatitude(),location.getLongitude(),lat,lng)/1000;
            Toast.makeText( getApplicationContext(),"La distanza dalla tua posizione è: "+distanza +"Km",Toast.LENGTH_SHORT).show();

        }



        /*
        ** Set Views content
         */
        TextView tvComune = (TextView) findViewById(R.id.tv_comune);
        tvComune.setText(Html.fromHtml("<B>Comune: </B>" + comune + " (" + provincia + ")"));
        ImageView iv = (ImageView) findViewById(R.id.iv_details_place);
        iv.setScaleType(ImageView.ScaleType.FIT_XY);
        /*
        ** Aggiungo i servizi
         */

        final float density = context.getResources().getDisplayMetrics().density;
        final GridLayout gridLayout =(GridLayout) findViewById(R.id.GridLayout1);
        int paddingPixel = 130;
        final int paddingDp = (int)(paddingPixel / density);
        Boolean unServizio = false;
        for(int i=0;i<serviziNomi.size();i++){
            if(serviziVec.get(i)) {
                unServizio = true;
                ImageView image = new ImageView(this);
                image.setImageResource(getResources().getIdentifier("servizio_" + (i + 1), "drawable", this.getPackageName()));
                TextView textView = new TextView(this);
                textView.setText(serviziNomi.elementAt(i));
                textView.setMinimumWidth(LinearLayout.LayoutParams.MATCH_PARENT);
                textView.setGravity(Gravity.CENTER);
                LinearLayout linearLayout = new LinearLayout(this);
                linearLayout.setOrientation(LinearLayout.VERTICAL);
                linearLayout.setPadding(paddingDp,paddingDp,paddingDp,paddingDp);
                linearLayout.addView(image);
                linearLayout.addView(textView);
                gridLayout.addView(linearLayout);
            }
        }
        /*
        ** Aggiusto le colonne del Grid Layout a runtime
         */

        if(unServizio){
            ViewTreeObserver vto = gridLayout.getViewTreeObserver();
            vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    gridLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    int maxLLwidth = 0;
                    for (int i = 0; i < (gridLayout).getChildCount(); ++i) {
                        View nextChild = (gridLayout).getChildAt(i);
                        int LLwidth = nextChild.getMeasuredWidth();
                        if (LLwidth > maxLLwidth)
                            maxLLwidth = LLwidth;
                    }
                    int numColonne = (int) Math.ceil(gridLayout.getMeasuredWidth() / maxLLwidth);
                    gridLayout.setColumnCount(numColonne);
                /*    int paddingPixelGL = (gridLayout.getMeasuredWidth()-(maxLLwidth*numColonne))/2;
                   int paddingDpGL = (int)(paddingPixelGL / density);
                    if(((ViewGroup) gridLayout).getChildCount()>2)
                        gridLayout.setPadding(paddingDpGL,0,0,0);
                        */
                }
            });
        }



        /*
        ** Setto l'immagine della localita
         */
        Picasso.with(context)
                .load(imageUrl)
                .placeholder(null)
                .error(R.drawable.placeholder2).into(iv);

        /*
        ** Setto l'immagine e la textview della classificazione
         */
        ImageView ivClassificazione = (ImageView) findViewById(R.id.iv_classificazione);
        ivClassificazione.setImageResource(getResources().getIdentifier("class_" + (classificazione), "drawable", this.getPackageName()));
        TextView tvClassificazione = (TextView) findViewById(R.id.tv_classificazione);
        switch (classificazione){
            case 1:
                tvClassificazione.setText("Eccellente");
                break;
            case 2:
                tvClassificazione.setText("Buona");
                break;
            case 3:
                tvClassificazione.setText("Sufficiente");
                break;
            case 4:
            tvClassificazione.setText("Scarso");
            break;
        }
        if(divieto==1){
            TextView tvDivieto = (TextView) findViewById(R.id.tvDivieto);
            tvDivieto.setText("Divieto Temporaneo");
            ivClassificazione.setImageResource(getResources().getIdentifier("divieto", "drawable", this.getPackageName()));
        }
    }




    /**
     * Create, bind and set up the resources
     */
    private void initialise()
    {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        toolbar.setTitle("Località: "+localita);

        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /**
     * @return the last know best location
     */
    private Location getLastBestLocation() {
        Location locationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        Location locationNet = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        long GPSLocationTime = 0;
        if (null != locationGPS) { GPSLocationTime = locationGPS.getTime(); }

        long NetLocationTime = 0;

        if (null != locationNet) {
            NetLocationTime = locationNet.getTime();
        }

        if ( 0 < GPSLocationTime - NetLocationTime ) {
            return locationGPS;
        }
        else {
            return locationNet;
        }
    }



    @Override
    protected void onStop() {
    if(!locManDisable) {
        locationManager.removeUpdates(mlocListener);
        //locationManager = null;
        locManDisable=true;
        //handler.removeCallbacks(closeLocation);

    }
        super.onStop();
    }

    public static float getDistance(double startLati, double startLongi, double goalLati, double goalLongi){
        float[] resultArray = new float[99];
        Location.distanceBetween(startLati, startLongi, goalLati, goalLongi, resultArray);
        return resultArray[0];
    }

public class MyLocationListener implements LocationListener

{

    @Override

    public void onLocationChanged(Location loc)
    {
        if (loc == null) return;

        //mLastLocationMillis = SystemClock.elapsedRealtime();
        locationManager.removeUpdates(mlocListener);
        //locationManager = null;
        locManDisable=true;
        handler.removeCallbacks(closeLocation);

        /* TODO Inserire istruzioni per inserire in un campo la distanza! */

        distanza=getDistance(loc.getLatitude(),loc.getLongitude(),lat,lng)/1000;
        String Text ="My current location is: " +"Latitud = " + loc.getLatitude() + "Longitud = " + loc.getLongitude() +" distanza= " +distanza +" Km";
        Toast.makeText( getApplicationContext(),Text,Toast.LENGTH_SHORT).show();
        prefs.edit().putLong("timeToGps", System.currentTimeMillis() / 1000).apply();

    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(getApplicationContext(), "Gps Disabled1",Toast.LENGTH_SHORT ).show();
        handler.removeCallbacks(closeLocation);
    }

    @Override
    public void onProviderEnabled(String provider) {
        Toast.makeText(getApplicationContext(),"Gps Enabled1",Toast.LENGTH_SHORT).show();
        handler.postDelayed(closeLocation, 10000);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }
}

}