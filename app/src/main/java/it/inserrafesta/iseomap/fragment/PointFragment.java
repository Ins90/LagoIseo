package it.inserrafesta.iseomap.fragment;

import android.support.v7.app.AlertDialog;
import android.content.DialogInterface;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import it.inserrafesta.iseomap.adapter.SimpleArrayAdapter;


public class PointFragment extends ListFragment implements SearchView.OnQueryTextListener {
    private SimpleArrayAdapter adapter;
    ArrayList<Place> pointList = new ArrayList<>();
    public static MenuItem searchItem;
    public static boolean ricercaCreata=false; //necessaria per il tasto indietro, se lo si preme appena aperta l'app
    Menu menuNew;

    boolean[] preCheckedItemsLast=new boolean[11];
    ArrayList<String> servicesFiltered = new ArrayList<>();


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
        int id = item.getItemId();
        switch(id) {
            case R.id.action_filter:
                showFilter();
                break;
        }
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


    public void showFilter(){
        //Get widgets reference from XML layout
        final TextView tv = (TextView) getActivity().findViewById(R.id.testFilter);

        //Initialize a new AlertDialog Builder
        AlertDialog.Builder adb = new AlertDialog.Builder(this.getActivity());

        //Set a title for alert dialog
        adb.setTitle(R.string.title_filter);

        //Initialize a new String Array
        String services=getResources().getString(R.string.allServices);
        final String[] serviziNomi=services.split(",");

        //ArrayList to store Alert Dialog selected items index position
        final ArrayList<Integer> selectedItems = new ArrayList<Integer>();

        //Array to store pre checked/selected items
        boolean[] preCheckedItems;
        // TODO magari riesci a passare i valori selezionati precedentemente
            if (preCheckedItemsLast.length != 0) {
                preCheckedItems = preCheckedItemsLast;
                //ricerco i true, in modo che siano realmente selezionati se l utente non li tocca!!
                for(int i=0;i<preCheckedItemsLast.length;i++){
                    if(preCheckedItemsLast[i]==true)
                        selectedItems.add(i);
                }
            } else {
                preCheckedItems = new boolean[]{ false, false, false, false, false, false, false, false, false, false, false} ;
            }

        //Define the AlertBuilder as a multiple choice items collection.
 /*
  AlertDialog.builder.setMultiChoiceItems() method
  setMultiChoiceItems(CharSequence[] items, boolean[] checkedItems,
  DialogInterface.OnMultiChoiceClickListener listener)

  First argument to pass an Array of items

  Second argument pass the pre checked/selected items.
  if we don't want to display any pre checked items,
  We can pass the second parameter value as null.

  Third argument set a Click Listener for Multiple Choice.
  */
        adb.setMultiChoiceItems(serviziNomi, preCheckedItems, new DialogInterface.OnMultiChoiceClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked){

                //You can update the preCheckedItems array here
                //In this tutorial i ignored this feature
                if(isChecked)
                {
                    //Add the checked item to checked items collection
                    selectedItems.add(which);
                }
                else if(selectedItems.contains(which))
                {

                    selectedItems.removeAll(Collections.singleton(which));
    /*If the clicked checkbox item is unchecked now
     and it already contains in the selected items collection
     then we remove it from selected items collection*/
                   // selectedItems.remove(which);
                }
            }
        });

        //Define the AlertDialog positive/ok/yes button
        adb.setPositiveButton("Ok", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which){
                //When user click the positive button from alert dialog

                //Set a message to show user at top
                tv.setText("i servizi che vuoi sono...\n");

                //Loop/iterate through ArrayList
                servicesFiltered=new ArrayList<>();
                Log.d("Adapter", "elementi di selected: " + selectedItems.toString());
                transformArray(selectedItems);
                for(int i=0;i<selectedItems.size();i++){
                    //selectedItems ArrayList current item's correspondent
                    //index position of Services Array
                    int IndexOfServicesArray = selectedItems.get(i);

                    //Get the selectedItems array specific index position's
                    //corresponded item from Services array
                    String selectedService = Arrays.asList(serviziNomi).get(IndexOfServicesArray);
                    servicesFiltered.add(selectedService);
                    //Display the selected services to TextView
                    tv.setText(tv.getText() + selectedService + "\n");

                }

                //Write a message for user
                String message = "bravo!";
                //Display the additional message to user on new line
                tv.setText(tv.getText() + "\n\n" + message);

            }
        });

        //Define the Neutral/Cancel button in AlertDialog
        adb.setNeutralButton(R.string.neutral_button, new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which){
                //When user click the neutral/cancel button from alert dialog
            }
        });

        //Display the Alert Dialog on app interface
        adb.show();
    }

    public void transformArray(ArrayList<Integer> selectedItems ){
        Log.d("Adapter", "numero di elementi di selected: " + selectedItems.size());
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
        Log.d("Adapter", "test check element 0: " + preCheckedItemsLast[0]);

    }
}

