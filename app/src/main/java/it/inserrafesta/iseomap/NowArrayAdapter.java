package it.inserrafesta.iseomap;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import it.inserrafesta.iseomap.R;

public class NowArrayAdapter extends ArrayAdapter<String> {

    private ArrayList<String> values;
    private Typeface fontFace;
    Context context;
    public class CustomListItem {
        TextView descText;
    }

    public NowArrayAdapter(Context context, int resource, ArrayList<String> commandsList) {
        super(context, resource, commandsList);
        this.context = context;
        //fontFace = Util.getTypeface(appContext, "fonts/Roboto-Light.ttf");

    }

    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        CustomListItem myListItem;

        String myText = getItem(position);

        if(convertView == null) {
            convertView = mInflater.inflate(R.layout.list_row, null);
            myListItem = new CustomListItem();

            myListItem.descText = (TextView) convertView.findViewById(R.id.title_name);
            //myListItem.descText.setTypeface(fontFace);

            convertView.setTag(myListItem);
        } else {
            myListItem = (CustomListItem) convertView.getTag();
        }

        myListItem.descText.setText(myText);
        //myListItem.descText.setTextSize(14);

        return convertView;
    }


}