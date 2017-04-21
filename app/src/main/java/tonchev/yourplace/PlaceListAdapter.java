package tonchev.yourplace;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.ArrayList;

import tonchev.yourplace.modul.Place;

/**
 * Created by Цветомир on 13.4.2017 г..
 */

public class PlaceListAdapter extends RecyclerView.Adapter<PlaceListAdapter.MyViewHolder> {
    private Context context;
    private ArrayList<Place> places;

    public PlaceListAdapter(Context context, ArrayList<Place> places) {
        this.context = context;
        this.places = places;
    }

    @Override
    public PlaceListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater li = LayoutInflater.from(context);
        View row = li.inflate(R.layout.recycler_view_layout, parent, false);
        MyViewHolder vh = new MyViewHolder(row);
        return vh;
    }

    @Override
    public void onBindViewHolder(PlaceListAdapter.MyViewHolder holder, int position) {
        Place place = places.get(position);
        holder.name.setText(place.getName());
        holder.rating.setText(place.getRating());
        holder.distance.setText("Distance: "+place.getDistance() +" / "+place.getDistanceTime());
        holder.adress.setText(place.getAdress());


        try {
            holder.ratingBar.setRating((float) Double.parseDouble(place.getRating()));
        }
        catch(NumberFormatException ex) {
            holder.ratingBar.setRating(0.0f);
            holder.rating.setText("N/A");
        }

        holder.name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,PlaceActivity.class);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return places.size();
    }
    class MyViewHolder extends RecyclerView.ViewHolder{

        View row;
        TextView name;
        TextView rating;
        RatingBar ratingBar;
        TextView distance;
        TextView adress;


        MyViewHolder(View row){
            super(row);
            this.row = row;
            name = (TextView) row.findViewById(R.id.list_name_text);
            rating = (TextView) row.findViewById(R.id.list_rating_text);
            ratingBar = (RatingBar) row.findViewById(R.id.list_rating_bar);
            distance = (TextView) row.findViewById(R.id.list_distance_text);
            adress = (TextView) row.findViewById(R.id.list_adress_text);

        }
    }
}
