package it.inserrafesta.iseomap.fragment;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;

import it.inserrafesta.iseomap.Place;
import it.inserrafesta.iseomap.R;

import java.util.ArrayList;
import java.util.List;

import it.inserrafesta.iseomap.SimpleArrayAdapter;

/**
 * Created by Andrea on 04-06-2015.
 */
public class PointFragment extends ListFragment {
    private ListView lv;
    private SimpleArrayAdapter adapter;
    List<Place> items = new ArrayList<Place>();


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_point, menu);
    }




    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        for(int i=0;i<MapFragment.places.size();i++)
            items.add(MapFragment.places.get(i));
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        setHasOptionsMenu(true); //necessario per visualizzare i pulsanti nella toolbar

        View rootView = inflater.inflate(R.layout.point_fragment, container, false);
        lv = (ListView)rootView.findViewById(android.R.id.list);
        lv.setDescendantFocusability(ListView.FOCUS_BLOCK_DESCENDANTS);

       return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d("Adapter", "items.size(): " + items.size());
        adapter = new SimpleArrayAdapter(getActivity(), android.R.id.list,
                items);
        setListAdapter(adapter);
    }


}

