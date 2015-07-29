package it.inserrafesta.iseomap.fragment;

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


import it.inserrafesta.iseomap.Place;
import it.inserrafesta.iseomap.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import it.inserrafesta.iseomap.adapter.SimpleArrayAdapter;


public class PointFragment extends ListFragment implements SearchView.OnQueryTextListener {
    private SimpleArrayAdapter adapter;
    ArrayList<Place> pointList = new ArrayList<>();
    public static MenuItem searchItem;
    public static boolean ricercaCreata=false; //necessaria per il tasto indietro, se lo si preme appena aperta l'app
    Menu menuNew;
    boolean[] saveItemForCancelDialog;
    boolean[] preCheckedItemsLast= new boolean[]{ false, false, false, false, false, false, false, false, false, false, false, false} ;

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
                final TextView tv = (TextView) getActivity().findViewById(R.id.testFilter);
                tv.setVisibility(View.VISIBLE);
                setItemsVisibility(menuNew, searchItem, true);

                return true;  // Return true to collapse action view
            }

            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                preCheckedItemsLast= new boolean[]{ false, false, false, false, false, false, false, false, false, false, false, false} ;
                checkFilterUpdateAdapter();
                final TextView tv = (TextView) getActivity().findViewById(R.id.testFilter);
                tv.setText(Html.fromHtml("<B>" +getActivity().getResources().getString(R.string.noFilter)+"</B>"));
                final TextView contenutoFiltro = (TextView) getActivity().findViewById(R.id.componentFilter);
                tv.setVisibility(View.GONE);
                contenutoFiltro.setText("");
                contenutoFiltro.setVisibility(View.GONE);

                setItemsVisibility(menuNew, searchItem, false); //nascondo l item del filtro quando apro la ricerca

                return true;  // Return true to expand action view
            }
        });
    }

    private void setItemsVisibility(Menu menu, MenuItem exception, boolean visible) {
        for (int i=0; i<menu.size(); ++i) {
            MenuItem item = menu.getItem(i);
            if (item != exception) item.setVisible(visible);
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id) {
            case R.id.action_filter:
                showFilter();
                break;
        }
       return super.onOptionsItemSelected(item);
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


    public void showFilter(){
        //Get widgets reference from XML layout
        final TextView tv = (TextView) getActivity().findViewById(R.id.testFilter);
        final TextView contenutoFiltro = (TextView) getActivity().findViewById(R.id.componentFilter);

        //Initialize a new AlertDialog Builder
        AlertDialog.Builder adb = new AlertDialog.Builder(this.getActivity());

        //Set a title for alert dialog
        adb.setTitle(Html.fromHtml("<B><font color='#727272'>" + getActivity().getResources().getString(R.string.title_filter) + " </font></B>"));;
        //Initialize a new String Array
        String services=getResources().getString(R.string.allServices);
        final String[] serviziNomi=services.split(",");

        //ArrayList to store Alert Dialog selected items index position
        final ArrayList<Integer> selectedItems = new ArrayList<>();


        //Array to store pre checked/selected items
        boolean[] preCheckedItems;
            if (preCheckedItemsLast.length != 0) {
                preCheckedItems = preCheckedItemsLast;
                //ricerco i true, in modo che siano realmente selezionati se l utente non li tocca!!
                saveItemForCancelDialog=preCheckedItemsLast;
                for(int i=0;i<preCheckedItemsLast.length;i++){
                    if(preCheckedItemsLast[i])
                        selectedItems.add(i);
                }
            } else {
                preCheckedItems = new boolean[]{ false, false, false, false, false, false, false, false, false, false, false, false} ;
            }

        //Define the AlertBuilder as a multiple choice items collection.
        adb.setMultiChoiceItems(serviziNomi, preCheckedItems, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {

                //You can update the preCheckedItems array here

                if (isChecked) {
                    //Add the checked item to checked items collection
                    selectedItems.add(which);
                } else if (selectedItems.contains(which)) {
                    selectedItems.removeAll(Collections.singleton(which));
                }
            }
        });

        //Define the AlertDialog positive/ok/yes button
        adb.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //When user click the positive button from alert dialog
                //Set a message to show user at top
                if (selectedItems.size() != 0) {
                    tv.setTypeface(null, Typeface.NORMAL);

                    tv.setText(Html.fromHtml("<B>" + getActivity().getResources().getString(R.string.yesFilter) + "</B>"));
                    contenutoFiltro.setText("");
                    contenutoFiltro.setVisibility(View.VISIBLE);
                    //iterate through ArrayList
                    for (int i = 0; i < selectedItems.size(); i++) {

                        int IndexOfServicesArray = selectedItems.get(i);

                        String selectedService = Arrays.asList(serviziNomi).get(IndexOfServicesArray);
                        //Display the selected services to TextView
                        contenutoFiltro.setText(contenutoFiltro.getText() + " " + selectedService + "; ");
                    }
                } else {
                    tv.setText(Html.fromHtml("<B>" +getActivity().getResources().getString(R.string.noFilter)+"</B>"));
                    contenutoFiltro.setVisibility(View.GONE);
                }
                transformArray(selectedItems);
                checkFilterUpdateAdapter();
            }
        });

        //Define the Neutral/Cancel button in AlertDialog
        adb.setNeutralButton(R.string.neutral_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                preCheckedItemsLast = new boolean[]{false, false, false, false, false, false, false, false, false, false, false, false};
                tv.setText(Html.fromHtml("<B>" + getActivity().getResources().getString(R.string.noFilter) + "</B>"));
                checkFilterUpdateAdapter();
                contenutoFiltro.setVisibility(View.GONE);

            }
        });

        adb.setNegativeButton(R.string.negative_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                preCheckedItemsLast = saveItemForCancelDialog; //TODO non funge

            }
        });
        adb.setCancelable(false);

        //Display the Alert Dialog on app interface
        adb.show();
    }

    public void transformArray(ArrayList<Integer> selectedItems ){
        for(int i=0;i<preCheckedItemsLast.length;i++) {
            preCheckedItemsLast[i]=false;
        }

        //rimuovo i duplicati da i servizi selezionati
        Set<Integer> hs = new HashSet<>();
        hs.addAll(selectedItems);
        selectedItems.clear();
        selectedItems.addAll(hs);

        for(int i=0;i<selectedItems.size();i++){
            preCheckedItemsLast[selectedItems.get(i)]=true;
        }
    }

    public void checkFilterUpdateAdapter(){
        pointList = new ArrayList<>();
        for(int i=0;i<MapFragment.places.size();i++) { //previene l eventualita di un inserimento doppio
            boolean match=true;
            boolean singolo=true;
            for(int j=0;j<pointList.size();j++) {
                if (MapFragment.places.get(i).getLocalita().equals(pointList.get(j).getLocalita())) {
                    singolo=false;
                }
            }

            for(int k=0;k<preCheckedItemsLast.length;k++){
                if(preCheckedItemsLast[k] && !MapFragment.places.get(i).getServiziVectoArray()[k]){ //controllo se esiste almeno un servizio che la località NON ha ma che è utilizzato nel filtro
                    match=false;
                }
            }

            if(singolo && match) pointList.add(MapFragment.places.get(i));
        }

        adapter = new SimpleArrayAdapter(getActivity(), android.R.id.list,
                pointList);
        setListAdapter(adapter);
    }
}

