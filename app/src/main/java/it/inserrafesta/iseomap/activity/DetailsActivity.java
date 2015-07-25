package it.inserrafesta.iseomap.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;

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

import com.squareup.picasso.Picasso;

import java.math.RoundingMode;
import java.text.DecimalFormat;
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
    SharedPreferences prefLat;
    SharedPreferences prefLng;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Context context = getApplication();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        Bundle extras = getIntent().getExtras();
        localita = extras.getString("localita");

        prefLat = context.getSharedPreferences("prefLat", Context.MODE_PRIVATE);
        prefLng = context.getSharedPreferences("prefLng", Context.MODE_PRIVATE);

        initialise();

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

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

        double distanza = getDistance(Double.valueOf(prefLat.getString("prefLat", null)), Double.valueOf(prefLng.getString("prefLng", null)), lat, lng) / 1000;

        /*
        ** Set Views content
         */
        TextView tvComune = (TextView) findViewById(R.id.tv_comune);
        tvComune.setText(Html.fromHtml("<B>Comune: </B>" + comune + " (" + provincia + ")"));

        TextView distance = (TextView) findViewById(R.id.distance);
        if (distanza !=0) {
            DecimalFormat df = new DecimalFormat("##.##");
            df.setRoundingMode(RoundingMode.DOWN);
            distance.setText(Html.fromHtml("<B>Distanza dalla località: </B>" + df.format(distanza) + " Km"));
        }

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
                tvClassificazione.setText(R.string.water_high);
                break;
            case 2:
                tvClassificazione.setText(R.string.water_good);
                break;
            case 3:
                tvClassificazione.setText(R.string.water_suff);
                break;
            case 4:
            tvClassificazione.setText(R.string.water_poor);
            break;
        }
        if(divieto==1){
            TextView tvDivieto = (TextView) findViewById(R.id.tvDivieto);
            tvDivieto.setText(R.string.prohibition);
            ivClassificazione.setImageResource(getResources().getIdentifier("divieto", "drawable", this.getPackageName()));
        }
    }

    /**
     * Create, bind and set up the resources
     */
    private void initialise()
    {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        toolbar.setTitle("Località: " + localita);

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

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }


    public static float getDistance(double startLati, double startLongi, double goalLati, double goalLongi){
        float[] resultArray = new float[99];
        Location.distanceBetween(startLati, startLongi, goalLati, goalLongi, resultArray);
        return resultArray[0];
    }
}