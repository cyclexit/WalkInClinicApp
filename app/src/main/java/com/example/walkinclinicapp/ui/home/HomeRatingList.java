package com.example.walkinclinicapp.ui.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.walkinclinicapp.R;
import com.iarcuschin.simpleratingbar.SimpleRatingBar;

import java.util.ArrayList;

import Rating.Rate;

public class HomeRatingList extends BaseAdapter {
    // instance
    private ArrayList<Rate> ratings;
    private LayoutInflater inflater;

    public HomeRatingList(LayoutInflater inflater, ArrayList<Rate> ratings){
        this.ratings = ratings;
        this.inflater = inflater;
    }

    @Override
    public int getCount() {
        if (ratings == null){
            return 0;
        }
        return ratings.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = inflater.inflate(R.layout.list_rating,null);
        Rate rate = ratings.get(position);

        SimpleRatingBar ratingBar = (SimpleRatingBar) view.findViewById(R.id.ratingBarHomeList);
        TextView comments = (TextView) view.findViewById(R.id.textViewComments);
        TextView username = (TextView) view.findViewById(R.id.textViewRatingUserName);

        username.setText(rate.getUsername());
        comments.setText(rate.getComment());
        ratingBar.setRating(rate.getRating());
        return view ;
    }
}
