package com.mahmoud.popularmovies.Adapter;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.mahmoud.popularmovies.Control.Keys;
import com.mahmoud.popularmovies.Control.Utils;
import com.mahmoud.popularmovies.Model.MoviesResultModel;
import com.mahmoud.popularmovies.R;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Mahmoud on 10/28/2016.
 */

public class MoviesAdapter extends BaseAdapter {

    private final Activity activity;
    private final List<MoviesResultModel> listMovies;
    private final int orientation1;
    private final int densityDpi;
    private LayoutInflater inflater;

    public MoviesAdapter(Activity activity, List<MoviesResultModel> listMovies){
        this.activity = activity;
        this.listMovies = listMovies;

        Utils.getWindowDimentions(activity);
        orientation1 = activity.getResources().getConfiguration().orientation;

        DisplayMetrics metrics = activity.getResources().getDisplayMetrics();
        densityDpi = (int)(Utils.SCREEN_WIDTH / metrics.density);
    }

    @Override
    public int getCount() {
        return listMovies.size();
    }

    @Override
    public Object getItem(int i) {
        return listMovies.get(i);
    }

    @Override
    public long getItemId(int i) {
        return listMovies.get(i).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(inflater == null)
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if(convertView == null)
            convertView = inflater.inflate(R.layout.item_list_movies, parent, false);

        return fillView(convertView, position);
    }

    private View fillView(View convertView, int position) {

        ImageView ivPoster = (ImageView) convertView.findViewById(R.id.iv_poster);

        if(densityDpi >= 600) {
            Picasso.with(activity)
                    .load(Keys.BASE_IMAGE_URL + "w185" + listMovies.get(position).getPosterPath())
                    .resize(Utils.SCREEN_WIDTH / 4, Utils.SCREEN_HEIGHT / 4)
                    .into(ivPoster);
        }
        else{
            Picasso.with(activity)
                    .load(Keys.BASE_IMAGE_URL + "w185" + listMovies.get(position).getPosterPath())
                    .resize(Utils.SCREEN_WIDTH / 2, Utils.SCREEN_HEIGHT / 2)
                    .into(ivPoster);
        }

        return convertView;
    }
}
