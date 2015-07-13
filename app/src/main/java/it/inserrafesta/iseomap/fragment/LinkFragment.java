package it.inserrafesta.iseomap.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.Fragment;

import it.inserrafesta.iseomap.R;


public class LinkFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO sistemare dimensione con scalatura immagine lago

        return inflater.inflate(R.layout.link_fragment, container, false);
    }
}


