package it.inserrafesta.iseomap.fragment;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import it.inserrafesta.iseomap.R;

import java.util.ArrayList;

import it.inserrafesta.iseomap.NowArrayAdapter;

/**
 * Created by Andrea on 04-06-2015.
 */
public class PointFragment extends ListFragment {
    private NowArrayAdapter m_adapter;
    private ListView lv;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.point_fragment, container, false);



        lv = (ListView)v.findViewById(android.R.id.list);

        // ListView lv=lv.getListView();
        return v;

    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ArrayList<String> listContact = new ArrayList<String>();
        listContact.add("ciao");
        listContact.add("ciao2");
        listContact.add("ciao3");


        // GlobalList is a class that holds global variables, arrays etc
        // getMenuCategories returns global arraylist which is initialized in GlobalList class

        m_adapter = new NowArrayAdapter(getActivity(), android.R.id.list, listContact);
        lv.setAdapter(m_adapter);
    }
}
