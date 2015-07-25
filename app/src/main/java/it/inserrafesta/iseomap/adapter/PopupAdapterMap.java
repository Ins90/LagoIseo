package it.inserrafesta.iseomap.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.model.Marker;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import it.inserrafesta.iseomap.R;

public class PopupAdapterMap implements InfoWindowAdapter {
    private View popup=null;
    private LayoutInflater inflater=null;
    private Context ctxt=null;

    public PopupAdapterMap(Context ctxt, LayoutInflater inflater) {
        this.ctxt=ctxt;
        this.inflater=inflater;

    }

    @Override
    public View getInfoWindow(Marker marker) {
        return(null);
    }

    @SuppressLint("InflateParams")
    @Override
    public View getInfoContents(Marker marker) {
        if (popup == null) {
            popup=inflater.inflate(R.layout.custom_info_contents, null);
        }

        String str = marker.getTitle();
        final String[] str2 = str.split("_");

        TextView myTitle = (TextView) popup.findViewById(R.id.my_title);


        TextView mysnippet = (TextView) popup.findViewById(R.id.my_snippet);
        TextView myquality = (TextView) popup.findViewById(R.id.qualityWater);
        TextView divietoA = (TextView) popup.findViewById(R.id.divietoAcqua);

        ImageView imageinfo = (ImageView) popup.findViewById(R.id.image_info);
        myTitle.setText(str2[0]);// got first string as title
        mysnippet.setText(marker.getSnippet());

        if (str2[1].equals("1")) {
            divietoA.setText(R.string.prohibition);

        }

        switch (str2[2]) {
            case "1":
                myquality.setText(R.string.qlt_ecc);
                break;
            case "2":
                myquality.setText(R.string.qlt_buo);
                break;
            case "3":
                myquality.setText(R.string.qlt_suf);
                break;
            case "4":
                myquality.setText(R.string.qlt_sca);
                break;
            default:
                break;
        }

        Picasso.with(ctxt)
                .load(str2[3])
                .resize(85*(int) getDensityScale(),85* (int) getDensityScale() )
                .centerCrop().noFade()
                .placeholder(R.drawable.placeholder1)
                .into(imageinfo, new MarkerCallback(marker));
        return(popup);
    }

    private float getDensityScale()
    {
        final DisplayMetrics metrics =
                Resources.getSystem().getDisplayMetrics();
        return metrics.density;
    }

    static class MarkerCallback implements Callback {
        Marker marker=null;

        MarkerCallback(Marker marker) {
            this.marker=marker;
        }

        @Override
        public void onError() {
            Log.e(getClass().getSimpleName(), "Error loading thumbnail!");
        }

        @Override
        public void onSuccess() {
            if (marker != null && marker.isInfoWindowShown()) {
                marker.hideInfoWindow();
                marker.showInfoWindow();            }
        }
    }
}