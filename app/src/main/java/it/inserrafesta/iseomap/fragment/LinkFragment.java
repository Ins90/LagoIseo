package it.inserrafesta.iseomap.fragment;

import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.Fragment;
import android.webkit.WebView;
import android.widget.TextView;

import it.inserrafesta.iseomap.R;

/**
 * Created by Andrea on 19/06/2015.
 */
public class LinkFragment extends Fragment {

    WebView mWebView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.link_fragment, container, false);
    // TODO sistemare dimensione con scalatura immagine lago

        return v;
    }
}


