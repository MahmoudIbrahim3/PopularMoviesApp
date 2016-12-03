package com.mahmoud.popularmovies.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mahmoud.popularmovies.Model.ReviewsResultModel;
import com.mahmoud.popularmovies.R;

import java.util.List;

/**
 * Created by Mahmoud on 10/28/2016.
 */

public class ReviewsAdapter extends BaseAdapter {

    private final Activity activity;
    private final List<ReviewsResultModel> listReviews;
    private LayoutInflater inflater;

    public ReviewsAdapter(Activity activity, List<ReviewsResultModel> listReviews){
        this.activity = activity;
        this.listReviews = listReviews;
    }

    @Override
    public int getCount() {
        return listReviews.size();
    }

    @Override
    public Object getItem(int i) {
        return listReviews.get(i);
    }

    @Override
    public long getItemId(int i) {
        return Long.parseLong(listReviews.get(i).getId());
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(inflater == null)
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        
        if(convertView == null)
            convertView = inflater.inflate(R.layout.item_list_review, parent, false);
        
        return fillView(convertView, position);
    }

    private View fillView(View convertView, int position) {

        TextView tvAuthor = (TextView) convertView.findViewById(R.id.tv_author);
        TextView tvReviewContent = (TextView) convertView.findViewById(R.id.tv_review_content);

        tvAuthor.setText(activity.getResources().getString(R.string.reviewed_by) +
                listReviews.get(position).getAuthor());
        tvReviewContent.setText(listReviews.get(position).getContent());

        return convertView;
    }
}
