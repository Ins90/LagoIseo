package it.inserrafesta.iseomap.fragment;

import android.app.Activity;
import android.net.ConnectivityManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import it.inserrafesta.iseomap.R;


public class WaterFragment extends Fragment {

    String url = "http://www.balneazionelagoiseo.it/index.php?option=com_content&view=article&id=7&Itemid=16&lang=it";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.water_fragment, container, false);

/*
        WebView wv = (WebView)v.findViewById(R.id.webView);

        wv.loadUrl(url);
        wv.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        }); */
    return v;
    }




}