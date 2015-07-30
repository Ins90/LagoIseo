package it.inserrafesta.iseomap.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;


import it.inserrafesta.iseomap.Place;
import it.inserrafesta.iseomap.R;
import it.inserrafesta.iseomap.activity.DetailsActivity;


public class SimpleArrayStarAdapter extends ArrayAdapter<Place>{
    private final Context context;
    private ArrayList<Place> originalList;
    private ArrayList<Place> pointList;

    public SimpleArrayStarAdapter(Context context, int resource,
                              ArrayList<Place> pointList) {
        super(context, resource,pointList);
        this.context = context;
        this.pointList = new ArrayList<>();
        this.pointList.addAll(pointList);
    }


    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        View row =convertView;
        ViewHolder holder;
        //Log.v("ConvertView", String.valueOf(position));
        if (row == null) {

            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.list_row, null);

            holder = new ViewHolder();
            holder.localita = (TextView) row.findViewById(R.id.title_name);
            holder.comune = (TextView) row.findViewById(R.id.description);
           // holder.classificazione = (TextView) row.findViewById(R.id.waterclas);
            holder.water = (ImageView) row.findViewById(R.id.waterImage);
            holder.imgV = (ImageView) row.findViewById(R.id.point_image);
            holder.imgV.setScaleType(ImageView.ScaleType.FIT_XY);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }
        final Place place = pointList.get(position);

        if (place != null) {
            holder.comune.setText(place.getComune());
            holder.localita.setText(place.getLocalita());

            if(place.getDivieto()==1){
               // holder.classificazione.setText(R.string.prohibition);
                holder.water.setImageResource(R.drawable.divieto);
            }else {
                if (place.getClassificazione() == 1) {
                //    holder.classificazione.setText(R.string.water_high);
                    holder.water.setImageResource(R.drawable.class_1);

                } else {
                    if (place.getClassificazione() == 2) {
                    //    holder.classificazione.setText(R.string.water_good);
                        holder.water.setImageResource(R.drawable.class_2);

                    } else {
                        if (place.getClassificazione() == 3) {
                        //    holder.classificazione.setText(R.string.water_suff);
                            holder.water.setImageResource(R.drawable.class_3);
                        }else {
                        //    holder.classificazione.setText(R.string.water_poor);
                            holder.water.setImageResource(R.drawable.class_4);
                        }
                    }
                }
            }

           // Picasso.with(context).setIndicatorsEnabled(true);
            Picasso.with(context).load(place.getImageUrl())
                    .placeholder(null)
                    .error(R.drawable.placeholder1)
                    .into(holder.imgV);

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
        ImageView imgV;
        ImageView water;

    }

    @Override
    public int getCount() {
        return pointList.size();
    }

    @Override
    public Place getItem(int position) {
        return pointList.get(position);
    }

}