package it.inserrafesta.iseomap.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;

import android.support.v7.widget.Toolbar;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;

import android.view.View;
import android.view.WindowManager;

import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Vector;

import it.inserrafesta.iseomap.ExpandableHeightGridView;
import it.inserrafesta.iseomap.adapter.GridViewAdapter;
import it.inserrafesta.iseomap.ServiceItem;
import it.inserrafesta.iseomap.InformazioneUtile;
import it.inserrafesta.iseomap.R;
import it.inserrafesta.iseomap.fragment.MapFragment;

public class DetailsActivity extends AppCompatActivity {

    public static int numInfo = 6;
    private ArrayList<InformazioneUtile> infoVec = new ArrayList<>();
    String[] infoNomiArray;
    static int numServizi=11;
    static String[] serviziNomiArray;
    public static Vector<String> serviziNomi;
    private static ArrayList<Boolean> serviziVec;
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
    double distanza;
    private GridView gridView;
    private GridViewAdapter gridAdapter;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Context context = getApplication();

        String services=getResources().getString(R.string.allServices);
        serviziNomiArray=services.split(",");

        serviziNomi= new Vector<>(Arrays.asList(serviziNomiArray));
        //Log.d("Adapter", "array " + serviziNomiArray[10].toString());
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
                infoVec = MapFragment.places.get(i).getInfoVec();
                break;
            }
        }
        if(!prefLat.getString("prefLat", "0").equals("0")) {
            distanza = getDistance(Double.valueOf(prefLat.getString("prefLat", null)), Double.valueOf(prefLng.getString("prefLng", null)), lat, lng) / 1000;
        }else{
            distanza=0;
        }
        /*
        ** Set Views content
         */
        TextView tvComune = (TextView) findViewById(R.id.tv_comune);
        tvComune.setText(Html.fromHtml("<B>Comune: </B>" + comune + " (" + provincia + ")"));

        TextView distance = (TextView) findViewById(R.id.distance);
        if (distanza !=0) {
            DecimalFormat df = new DecimalFormat("##.##");
            df.setRoundingMode(RoundingMode.DOWN);
            distance.setVisibility(View.VISIBLE);
            distance.setText(Html.fromHtml("<B>Distanza dalla località: </B>" + df.format(distanza) + " Km"));


        }else{
            distance.setVisibility(View.GONE);
        }
        Button btn = (Button) findViewById(R.id.button2);
        btn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                //myClick(v); /* my method to call new intent or activity */
            }
        });

        //Gridview per i servizi
        ExpandableHeightGridView gridView = (ExpandableHeightGridView) findViewById(R.id.gridView);
        gridAdapter = new GridViewAdapter(this, R.layout.grid_item_layout, getData());
        gridView.setAdapter(gridAdapter);
        gridView.setExpanded(true);

        ImageView iv = (ImageView) findViewById(R.id.iv_details_place);
        iv.setScaleType(ImageView.ScaleType.FIT_XY);

        /*
         * Aggiungo le informazioni utili
         */
        String infoServices=getResources().getString(R.string.allInfo);
        infoNomiArray=infoServices.split(",");
        LinearLayout infoContainer = (LinearLayout) findViewById(R.id.info_container);
        for(int i=0;i< numInfo;i++){
            InformazioneUtile info = infoVec.get(i);
            if(info.exists()) {
                String nome = info.getNome();
                String indirizzo = info.getIndirizzo();
                String telefono = info.getTelefono();
                LinearLayout infoRow = new LinearLayout(this);
                infoRow.setOrientation(LinearLayout.VERTICAL);
                TextView titoloTv = new TextView(this);
                TextView nomeTv = new TextView(this);
                TextView telefonoTv = new TextView(this);
                TextView indirizzoTv = new TextView(this);

                titoloTv.setText(Html.fromHtml("<B>" + infoNomiArray[i]+"</B>"));
                //titoloTv.setTextSize(getResources().getDimension(R.dimen.textTitleInfo));

                nomeTv.setText(nome);
                telefonoTv.setText(getResources().getString(R.string.tel)+" "+telefono);
                indirizzoTv.setText(indirizzo);
                infoRow.addView(titoloTv);
                if(!nome.equals(""))
                    infoRow.addView(nomeTv);
                if(!telefono.equals(""))
                    infoRow.addView(telefonoTv);
                if(!indirizzo.equals(""))
                    infoRow.addView(indirizzoTv);
                infoContainer.addView(infoRow);
                titoloTv.setBackgroundColor(Color.parseColor("#3D51B4"));
                titoloTv.setTextColor(Color.WHITE);
                titoloTv.setPadding(30,20,20,20);
                titoloTv.setTextSize(15);
                nomeTv.setPadding(30,10,10,10);
                telefonoTv.setPadding(30,10,10,10);
                indirizzoTv.setPadding(30,10,10,10);
                infoRow.setPadding(0,40,0,0);
            }
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

    // Prepare some dummy data for gridview
    private ArrayList<ServiceItem> getData() {
        final ArrayList<ServiceItem> serviceItems = new ArrayList<>();
        for(int i=0;i<serviziNomi.size();i++) {
            if (serviziVec.get(i)) {
                Resources res = getResources();
                int id = getResources().getIdentifier("servizio_" + (i + 1), "drawable", this.getPackageName());
                //Bitmap bitmap = BitmapFactory.decodeResource(res,id);
                serviceItems.add(new ServiceItem(id,serviziNomi.elementAt(i)));
            }
        }
        return serviceItems;
    }

    private void initialise()
    {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getResources().getString(R.string.localita)+": "+localita);

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

    public static int getNumServizi() {
        return numServizi;
    }

}