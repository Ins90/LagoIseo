package it.inserrafesta.iseomap.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

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

import it.inserrafesta.iseomap.Place;
import it.inserrafesta.iseomap.R;


public class DetailsActivity extends AppCompatActivity {
    private String comune;
    private String localita;
    private String provincia;
    private String title;
    private double lat;
    private double lng;
    private int classificazione; /* 1 eccellente 2 buono 3 sufficiente 4 scarso */
    private int divieto; /* 1 SI 0 NO */

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        Bundle extras = getIntent().getExtras();
        localita = extras.getString("localita");
        initialise();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        JSONArray jsonArray = getJSONFromDB();
        setVariablesFromJSONArray(jsonArray);
        /*
        ** Set Views content
         */
        TextView tvComune = (TextView)findViewById(R.id.tv_comune);
        tvComune.setText("Comune: "+comune+" ("+provincia+")");
        TextView tvLat = (TextView)findViewById(R.id.tv_lat);
        tvLat.setText("Lat: "+lat);
        TextView tvLng = (TextView)findViewById(R.id.tv_lng);
        tvLng.setText("Lng: "+lng);
    }



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
    /*
     * Ottiene un oggetto JSONArray dal DB remoto
     */
    private JSONArray getJSONFromDB(){
        JSONArray jsa = null;
        String result = "";
        InputStream isr = null;
        try{
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://iseomap.altervista.org/get_details_by_loc.php?loc="+localita); //YOUR PHP SCRIPT ADDRESS
            // HttpPost httppost = new HttpPost("http://172.23.193.32/elift-test/myfile.php"); //YOUR PHP SCRIPT ADDRESS
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();
            isr = entity.getContent();
        }
        catch(Exception e){
            Log.e("log_tag", "Error in http connection " + e.toString());
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

    private void setVariablesFromJSONArray(JSONArray jsonArray) {
        JSONObject json = null;
        try {
            json = jsonArray.getJSONObject(0);
            comune = json.getString("comune");
            provincia = json.getString("provincia");
            lat = json.getDouble("lat");
            lng = json.getDouble("lng");
            classificazione = json.getInt("classificazione");
            divieto = json.getInt("divieto");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

