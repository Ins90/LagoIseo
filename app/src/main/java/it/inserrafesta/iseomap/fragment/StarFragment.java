package it.inserrafesta.iseomap.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.support.v7.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;

import it.inserrafesta.iseomap.Place;
import it.inserrafesta.iseomap.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import it.inserrafesta.iseomap.adapter.SimpleArrayAdapter;
import it.inserrafesta.iseomap.adapter.SimpleArrayStarAdapter;


public class StarFragment extends ListFragment {
    private SimpleArrayStarAdapter adapter;
    ArrayList<Place> pointList = new ArrayList<>();
    Menu menuNew;
    //SharedPreferences prefstar;
    Context context;


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menuNew=menu;
        inflater.inflate(R.menu.menu_star, menu);
    }



    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        for(int i=0;i<MapFragment.places.size();i++) { //previene l eventualita di un inserimento doppio
            boolean singolo=true;
            for(int j=0;j<pointList.size();j++) {
                if (MapFragment.places.get(i).getLocalita().equals(pointList.get(j).getLocalita())) {
                    singolo=false;
                }
            }

            if(singolo && MapFragment.places.get(i).getFavorite()==1) pointList.add(MapFragment.places.get(i));
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getActivity().getApplicationContext();

       // prefstar = context.getSharedPreferences("StarsPoint", Context.MODE_PRIVATE);

        //setHasOptionsMenu(true); //necessario per visualizzare i pulsanti nella toolbar

        View rootView = inflater.inflate(R.layout.star_fragment, container, false);
        ListView lv = (ListView) rootView.findViewById(android.R.id.list);
        lv.setDescendantFocusability(ListView.FOCUS_BLOCK_DESCENDANTS);

        //enables filtering for the contents of the given ListView

        TextView emptyText = (TextView)rootView.findViewById(android.R.id.empty);
        lv.setEmptyView(emptyText);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        adapter = new SimpleArrayStarAdapter(getActivity(), android.R.id.list,
                pointList);
        setListAdapter(adapter);
    }


    public void checkStarUpdateAdapter(){
        pointList = new ArrayList<>();
        for(int i=0;i<MapFragment.places.size();i++) { //previene l eventualita di un inserimento doppio
            boolean singolo=true;
            for(int j=0;j<pointList.size();j++) {
                if (MapFragment.places.get(i).getLocalita().equals(pointList.get(j).getLocalita())) {
                    singolo=false;
                }
            }

            if(singolo && MapFragment.places.get(i).getFavorite()==1) pointList.add(MapFragment.places.get(i));
        }
            adapter = new SimpleArrayStarAdapter(getActivity(), android.R.id.list,
                    pointList);
            setListAdapter(adapter);
        }
}

