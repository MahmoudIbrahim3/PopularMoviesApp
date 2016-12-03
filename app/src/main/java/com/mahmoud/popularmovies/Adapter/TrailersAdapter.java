package com.mahmoud.popularmovies.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mahmoud.popularmovies.Model.TrailersResultModel;
import com.mahmoud.popularmovies.R;

import java.util.List;

/**
 * Created by Mahmoud on 10/28/2016.
 */

public class TrailersAdapter extends BaseAdapter {

    private final Activity activity;
    private final List<TrailersResultModel> listTrailers;
    private LayoutInflater inflater;

    public TrailersAdapter(Activity activity, List<TrailersResultModel> listTrailers){
        this.activity = activity;
        this.listTrailers = listTrailers;
    }

    @Override
    public int getCount() {
        return listTrailers.size();
    }

    @Override
    public Object getItem(int i) {
        return listTrailers.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(inflater == null)
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        
        if(convertView == null)
            convertView = inflater.inflate(R.layout.item_list_trailers, parent, false);
        
        return fillView(convertView, position);
    }

    private View fillView(View convertView, int position) {

        ImageView ivPlayer = (ImageView) convertView.findViewById(R.id.iv_player);
        TextView tvTrailerTitle = (TextView) convertView.findViewById(R.id.tv_trailer_title);

        tvTrailerTitle.setText(listTrailers.get(position).getName());

        return convertView;
    }
}
