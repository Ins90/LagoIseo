package it.inserrafesta.iseomap.fragment;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;


import it.inserrafesta.iseomap.Place;
import it.inserrafesta.iseomap.R;

import java.util.ArrayList;

import it.inserrafesta.iseomap.adapter.SimpleArrayAdapter;


public class PointFragment extends ListFragment implements SearchView.OnQueryTextListener {
    private SimpleArrayAdapter adapter;
    ArrayList<Place> pointList = new ArrayList<>();
    public static MenuItem searchItem;
    public static boolean ricercaCreata=false; //necessaria per il tasto indietro, se lo si preme appena aperta l'app
    Menu menuNew;

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menuNew=menu;
        inflater.inflate(R.menu.menu_point, menu);
        searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(this);
        searchView.setIconifiedByDefault(true);
        searchView.setQueryHint(getResources().getString(R.string.search_hint));
        ricercaCreata=true;
        searchView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean queryTextFocused) {
                if (!queryTextFocused) {
                    searchItem.collapseActionView();
                    searchView.setQuery("", false);
                }
            }
        });

        MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                //  if (menuNew != null)
                //      menuNew.findItem(R.id.action_filter).setVisible(true);
                return true;  // Return true to collapse action view
            }

            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                //  if (menuNew != null)
                //     menuNew.findItem(R.id.action_filter).setVisible(false);
                return true;  // Return true to expand action view
            }
        });
    }

    public boolean onOptionsItemSelected(MenuItem item) {
       return super.onOptionsItemSelected(item);
    }

    public void onCreate(Bundle savedInstanceState) {
        boolean singolo=true;
        super.onCreate(savedInstanceState);
        for(int i=0;i<MapFragment.places.size();i++) { //previene l eventualita di un inserimento doppio
            for(int j=0;j<pointList.size();j++) {
                if (MapFragment.places.get(i).getLocalita().equals(pointList.get(j).getLocalita())) {
                    singolo=false;
                }
            }
            if(singolo) pointList.add(MapFragment.places.get(i));
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        setHasOptionsMenu(true); //necessario per visualizzare i pulsanti nella toolbar

        View rootView = inflater.inflate(R.layout.point_fragment, container, false);
        ListView lv = (ListView) rootView.findViewById(android.R.id.list);
        lv.setDescendantFocusability(ListView.FOCUS_BLOCK_DESCENDANTS);

        //enables filtering for the contents of the given ListView
        lv.setTextFilterEnabled(true);
        TextView emptyText = (TextView)rootView.findViewById(android.R.id.empty);
        lv.setEmptyView(emptyText);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d("Adapter", "items.size(): " + pointList.size());
        adapter = new SimpleArrayAdapter(getActivity(), android.R.id.list,
                pointList);
        setListAdapter(adapter);
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        adapter.getFilter().filter(newText);
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }


}

