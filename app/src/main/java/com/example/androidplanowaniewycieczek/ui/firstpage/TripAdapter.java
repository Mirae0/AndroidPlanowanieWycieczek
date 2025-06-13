package com.example.androidplanowaniewycieczek.ui.firstpage;

import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidplanowaniewycieczek.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class TripAdapter extends RecyclerView.Adapter<TripAdapter.ViewHolder> {

    private List<Trip> tripList;

    public TripAdapter(List<Trip> tripList) {
        this.tripList = tripList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView destination, date;
        ConstraintLayout bannerLayout;
        ImageView detailsButton;

        public ViewHolder(View itemView) {
            super(itemView);
            destination = itemView.findViewById(R.id.ranking_trip_destination);
            date = itemView.findViewById(R.id.ranking_trip_date);
            bannerLayout = itemView.findViewById(R.id.bannerbackground);
            detailsButton = itemView.findViewById(R.id.details_button);
        }
    }

    @Override
    public TripAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_trip, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TripAdapter.ViewHolder holder, int position) {
        Trip trip = tripList.get(position);
        holder.destination.setText("Cel: " + trip.getLocationTo());

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(trip.getDurationMillis());
        String formattedDate = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault()).format(calendar.getTime());
        holder.date.setText("Data: " + formattedDate);

        if (trip.imageBitmap != null) {
            Drawable drawable = new BitmapDrawable(holder.itemView.getResources(), trip.imageBitmap);
            holder.bannerLayout.setBackground(drawable);
        } else {
            holder.bannerLayout.setBackgroundResource(R.drawable.banner_backgorund);
        }

        holder.detailsButton.setOnClickListener(v -> {
        });
    }

    @Override
    public int getItemCount() {
        return tripList.size();
    }
}
