package it.inserrafesta.iseomap;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import it.inserrafesta.iseomap.activity.DetailsActivity;

/**
 * Created by Andrea on 03/07/2015.
 */
public class SimpleArrayAdapter extends ArrayAdapter<Place> {
    private final Context context;
    private final List<Place> objects;

    public SimpleArrayAdapter(Context context, int resource,
                              List<Place> objects) {
        super(context, resource);
        this.context = context;
        this.objects = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder = new ViewHolder();

        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.list_row, parent, false);
            holder.localita = (TextView) row.findViewById(R.id.title_name);
            holder.comune = (TextView) row.findViewById(R.id.description);
            holder.classificazione = (TextView) row.findViewById(R.id.waterclas);

            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        final Place place = objects.get(position);
        if (place != null) {
            holder.comune.setText(place.getComune());
            holder.localita.setText(place.getLocalita());

            if(place.getDivieto()==1){
                holder.classificazione.setText(R.string.prohibition);
                holder.classificazione.setTextColor(Color.parseColor("#D32F2F"));
            }else {
                if (place.getClassificazione() == 1) {
                    holder.classificazione.setText(R.string.water_high);
                } else {
                    if (place.getClassificazione() == 2) {
                        holder.classificazione.setText(R.string.water_good);
                    } else {
                        holder.classificazione.setText(R.string.water_poor);
                    }
                }
            }
            // Log.d("Adapter", "holder.v1.getText(): " + holder.v1.getText());
        }

        final Intent intent = new Intent(context, DetailsActivity.class);

        row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent.putExtra("localita", place.getLocalita());
                context.startActivity(intent);
            }
        });
        return row;
    }

    public static class ViewHolder {
        TextView localita;
        TextView comune;
        TextView classificazione;
        TextView divieto;

    }

    @Override
    public int getCount() {
        return objects.size();
    }

    @Override
    public Place getItem(int position) {
        // TODO Auto-generated method stub
        return objects.get(position);
    }


}