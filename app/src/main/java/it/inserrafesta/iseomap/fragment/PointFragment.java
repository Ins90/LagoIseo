package it.inserrafesta.iseomap.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
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
public class PointFragment extends ListFragment {  //implements SearchView.OnQueryTextListener
    private ListView lv;
    private SimpleArrayAdapter adapter;
    ArrayList<Place> pointList = new ArrayList<Place>();
    //SearchView search_view;


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_point, menu);

       // search_view = (SearchView) menu.findItem(R.id.action_search).getActionView();

    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(R.id.action_filter==id){
            showSingleChoiceDialog(getActivity(), "Filtro punti di balneazione", "Necessaria una connessione internet per usare l'applicazione", true);
        }

        return super.onOptionsItemSelected(item);

    }


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        for(int i=0;i<MapFragment.places.size();i++)
            pointList.add(MapFragment.places.get(i));


    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        /*
        search_view.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String arg0) {

                return true;
            }

            @Override
            public boolean onQueryTextChange(String arg0) {
                adapter.getFilter().filter(arg0);

                return false;
            }
        }); */

        setHasOptionsMenu(true); //necessario per visualizzare i pulsanti nella toolbar

        View rootView = inflater.inflate(R.layout.point_fragment, container, false);
        lv = (ListView)rootView.findViewById(android.R.id.list);
        lv.setDescendantFocusability(ListView.FOCUS_BLOCK_DESCENDANTS);

        //enables filtering for the contents of the given ListView
        lv.setTextFilterEnabled(true);

        EditText myFilter = (EditText) rootView.findViewById(R.id.myFilter);
        myFilter.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s.toString());
            }
        });

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
    //TODO sistemare per i filtri, one choice
    public void showSingleChoiceDialog(Context context, String title, String message, Boolean status) {
        final AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        alert.setTitle(title);
        alert.setIcon(R.drawable.ic_no_connection);
        alert.setMessage(message);
        alert.setCancelable(false);
        alert.setSingleChoiceItems(new String[]{"tutto"}, 0, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                        /* User clicked on a radio button do some stuff */
            }
        });
        alert.setSingleChoiceItems(new String[]{"Eccellente"}, 0, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                        /* User clicked on a radio button do some stuff */
            }
        });
        alert.setNegativeButton("Annulla", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
                System.exit(0);
            }
        });
        alert.show();
    }

/*
    @Override
    public boolean onQueryTextChange(String newText) {
        adapter.getFilter().filter(newText);
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }
*/

}

