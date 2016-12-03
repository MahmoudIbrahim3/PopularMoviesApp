package com.mahmoud.popularmovies.Activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mahmoud.popularmovies.Adapter.ReviewsAdapter;
import com.mahmoud.popularmovies.Control.Keys;
import com.mahmoud.popularmovies.Control.ReviewsControl;
import com.mahmoud.popularmovies.Model.ReviewsResultModel;
import com.mahmoud.popularmovies.R;

import java.util.ArrayList;
import java.util.List;

public class ReviewsActivity extends AppCompatActivity {

    private static ArrayList<ReviewsResultModel> listReviews  = null;
    private ProgressBar pbLoading;
    private String TAG = "ReviewsActivity";
    private Toolbar toolbar;
    private int movieId;
    private ReviewsAdapter adapter;
    private ListView lvReview;
    private TextView tvNoReviews;
    private String movieTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_reviews);

        init();
        getIntentData();
        getReviews();
    }

    private void init() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        pbLoading = (ProgressBar) findViewById(R.id.pb_loading);
        tvNoReviews = (TextView) findViewById(R.id.tv_no_reviews);
    }

    private void getIntentData(){
        Bundle bundle = getIntent().getExtras();
        movieId = bundle.getInt(Keys.INTENT_POSTER_ID);
        movieTitle = bundle.getString(Keys.INTENT_POSTER_ORIGINAL_TITLE);

        getSupportActionBar().setTitle(getResources().getString(
                R.string.title_activity_show_reviews) + " - " + movieTitle);
    }

    private void getReviews() {
        ReviewsControl reviewsControl = new ReviewsControl();
        reviewsControl.getReviews(this, movieId, 1);
    }

    public void getReviewsCallBack(Activity activity, List<ReviewsResultModel> list) {

        ViewGroup viewGroup = (ViewGroup) ((ViewGroup) activity
                .findViewById(android.R.id.content)).getChildAt(0);
        pbLoading = (ProgressBar) viewGroup.findViewById(R.id.pb_loading);
        lvReview = (ListView) viewGroup.findViewById(R.id.ll_reviews);
        tvNoReviews = (TextView) viewGroup.findViewById(R.id.tv_no_reviews);

        pbLoading.setVisibility(View.GONE);
        if(list != null && list.size() != 0) {
            Log.e(TAG, "list.size: " + list.size());

            tvNoReviews.setVisibility(View.GONE);

            listReviews = new ArrayList<>();
            this.listReviews.addAll(list);

            adapter = new ReviewsAdapter(activity, listReviews);
            lvReview.setAdapter(adapter);
        }
        else{
            Log.e(TAG, "list.size: null");
            tvNoReviews.setVisibility(View.VISIBLE);
        }
    }
}
