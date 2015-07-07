package it.inserrafesta.iseomap.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import it.inserrafesta.iseomap.Place;
import it.inserrafesta.iseomap.R;
import it.inserrafesta.iseomap.fragment.MapFragment;


public class DetailsActivity extends AppCompatActivity {
    private String comune;
    private String localita;
    private String provincia;
    private String title;
    private double lat;
    private double lng;
    private int classificazione; /* 1 eccellente 2 buono 3 sufficiente 4 scarso */
    private int divieto; /* 1 SI 0 NO */
    private String imageUrl;
    private Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        context = getApplication();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        Bundle extras = getIntent().getExtras();
        localita = extras.getString("localita");
        initialise();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);


        //setImage();

        for (int i = 0; i < MapFragment.places.size(); i++) { //previene l eventualita di un inserimento doppio
            if (MapFragment.places.get(i).getLocalita().equals(localita)) {
                comune = MapFragment.places.get(i).getComune();
                provincia = MapFragment.places.get(i).getProvincia();
                lat = MapFragment.places.get(i).getLat();
                lng = MapFragment.places.get(i).getLng();
                classificazione = MapFragment.places.get(i).getClassificazione();
                divieto = MapFragment.places.get(i).getDivieto();
                imageUrl = MapFragment.places.get(i).getImageUrl();
                break;
            }
        }
        /*
        ** Set Views content
         */
        TextView tvComune = (TextView) findViewById(R.id.tv_comune);
        tvComune.setText(Html.fromHtml("<B>Comune: </B>" + comune + " (" + provincia + ")"));
        TextView tvLat = (TextView) findViewById(R.id.tv_lat);
        tvLat.setText(Html.fromHtml("<B>Lat: </B>" + lat));
        TextView tvLng = (TextView) findViewById(R.id.tv_lng);
        tvLng.setText(Html.fromHtml("<B>Lng: </B>" + lng));
        ImageView iv = (ImageView) findViewById(R.id.iv_details_place);
        iv.setScaleType(ImageView.ScaleType.FIT_XY);

        Picasso.with(context)
                .load(imageUrl)
                .placeholder(null)
                .error(R.drawable.placeholder2).into(iv);

    }


/*    private void setImage() {
        ImageView iv = (ImageView)findViewById(R.id.iv_details_place);
        iv.setScaleType(ImageView.ScaleType.FIT_XY);
        URL url = null;
        try {
            url = new URL(imageUrl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Bitmap bmp = null;
        try {
            bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        iv.setImageBitmap(bmp);
    }
*/


    /**
     * Create, bind and set up the resources
     */
    private void initialise()
    {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        toolbar.setTitle(localita);

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
}

