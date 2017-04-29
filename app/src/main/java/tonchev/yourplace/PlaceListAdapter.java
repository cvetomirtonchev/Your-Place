package tonchev.yourplace;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import tonchev.yourplace.modul.MyConnectionChecker;
import tonchev.yourplace.modul.Place;

/**
 * Created by Цветомир on 13.4.2017 г..
 */

public class PlaceListAdapter extends RecyclerView.Adapter<PlaceListAdapter.MyViewHolder> {
    private Context context;
    private ArrayList<Place> places;
    tonchev.yourplace.modul.Place temp;
    double[] ll = new double[2];

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
    public void onBindViewHolder(final PlaceListAdapter.MyViewHolder holder, int position) {
        Place place = places.get(position);
        holder.name.setText(place.getName());
        holder.rating.setText(place.getRating());
        holder.distance.setText("Distance: "+place.getDistance() +" / "+place.getDistanceTime());
        holder.adress.setText(place.getAdress());
        holder.isOpen.setText("Open now: " + place.getOpenNow());



        try {
            holder.ratingBar.setRating((float) Double.parseDouble(place.getRating()));
        }
        catch(NumberFormatException ex) {
            holder.ratingBar.setRating(0.0f);
            holder.rating.setText("N/A");
        }
        holder.row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!MyConnectionChecker.haveNetworkConnection(context)) {
                    Toast.makeText(context, "Please check your internet connection", Toast.LENGTH_SHORT).show();
                }
                else {
                    String clickedName = holder.name.getText().toString();
                    for (tonchev.yourplace.modul.Place p : places) {
                        if (clickedName.equals(p.getName())) {
                            temp = p;
                            break;
                        }
                    }
                    Intent intent = new Intent(context, PlaceActivity.class);
                    if (temp.getLatLng() == null) {
                        LatLng ltLg = new LatLng(ll[0], ll[1]);
                        temp.setLatLng(ltLg);
                    }
                    ll[0] = temp.getLatLng().latitude;
                    ll[1] = temp.getLatLng().longitude;
                    temp.setLatLng(null);
                    intent.putExtra("mqsto", temp);
                    intent.putExtra("ID", temp.getId());
                    intent.putExtra("LL", ll);

                    context.startActivity(intent);
                }
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
        TextView isOpen;


        MyViewHolder(View row){
            super(row);
            this.row = row;
            name = (TextView) row.findViewById(R.id.list_name_text);
            rating = (TextView) row.findViewById(R.id.list_rating_text);
            ratingBar = (RatingBar) row.findViewById(R.id.list_rating_bar);
            distance = (TextView) row.findViewById(R.id.list_distance_text);
            adress = (TextView) row.findViewById(R.id.list_adress_text);
            isOpen = (TextView) row.findViewById(R.id.list_open_text);

        }
    }
}
