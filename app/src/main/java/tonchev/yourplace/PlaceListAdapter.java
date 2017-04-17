package tonchev.yourplace;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
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
        if(place.getRating()!=null) {
            holder.rating.setText(place.getRating());
            holder.ratingBar.setRating(Float.parseFloat(place.getRating()));
        }

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


        MyViewHolder(View row){
            super(row);
            this.row = row;
            name = (TextView) row.findViewById(R.id.list_name_text);
            rating = (TextView) row.findViewById(R.id.list_rating_text);
            ratingBar = (RatingBar) row.findViewById(R.id.list_rating_bar);

        }
    }
}
