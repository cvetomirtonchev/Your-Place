package tonchev.yourplace;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.ArrayList;

import tonchev.yourplace.modul.Comment;

/**
 * Created by Цветомир on 26.4.2017 г..
 */

public class CommentListAdapter extends RecyclerView.Adapter<CommentListAdapter.MyViewHolder> {
    private Context context;
    private ArrayList<Comment> comments;
    private Comment comment;


    public CommentListAdapter(Context context, ArrayList<Comment> comments) {
        this.context = context;
        this.comments = comments;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater li = LayoutInflater.from(context);
        View row = li.inflate(R.layout.recycler_view_comment, parent, false);
        MyViewHolder vh = new MyViewHolder(row);
        return vh;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        Comment comment = comments.get(position);
        holder.name.setText(comment.getUser() + " :");
        holder.rating.setText(comment.getRating()+"");
        holder.review.setText(comment.getText());
        try {
            holder.ratingBar.setRating((float) Double.parseDouble(comment.getRating()));
        }
        catch(NumberFormatException ex) {
            holder.ratingBar.setRating(0.0f);
            holder.rating.setText("N/A");
        }




    }

    @Override
    public int getItemCount() {

        return comments.size();
    }
    class MyViewHolder extends RecyclerView.ViewHolder{

        View row;
        TextView name;
        TextView rating;
        TextView review;
        RatingBar ratingBar;


        MyViewHolder(View row){
            super(row);
            this.row = row;
            ratingBar = (RatingBar) row.findViewById(R.id.comment_view_ratingBar);
            name = (TextView) row.findViewById(R.id.comment_view_autor);
            rating = (TextView) row.findViewById(R.id.comment_view_rating);
            review = (TextView) row.findViewById(R.id.comment_view_text);


        }
    }
}

