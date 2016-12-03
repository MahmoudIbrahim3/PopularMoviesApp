package com.mahmoud.popularmovies.Activity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mahmoud.popularmovies.Adapter.TrailersAdapter;
import com.mahmoud.popularmovies.Control.DB;
import com.mahmoud.popularmovies.Control.Keys;
import com.mahmoud.popularmovies.Control.TrailersControl;
import com.mahmoud.popularmovies.Control.Utils;
import com.mahmoud.popularmovies.Model.TrailersResultModel;
import com.mahmoud.popularmovies.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MovieDetailsActivity extends AppCompatActivity implements
        AdapterView.OnItemClickListener, View.OnClickListener {

    private String TAG = "MovieDetailsActivity";
    private TextView tvReleaseDate;
    private TextView tvVoteAverage;
    private TextView tvOverview;
    private ImageView ivPoster;
    private int movieId = -1;
    private ListView lvTrailers;
    private ProgressBar pbLoading;
    private TextView btShowReviews;
    private String movieTitle;
    private TextView btAddToFavourite;
    private String movieThumbnailPath;
    private String movieReleaseDate;
    private double movieVoteAverage;
    private String movieOverview;
    private static List<TrailersResultModel> listTrailers = new ArrayList<>();
    private boolean isMovieFavourite = false;
    public final static String ARG_POSITION = "position";
    private Toolbar toolbar;
    private Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        init();
        getIntentData();
        checkThisMovieInFavourite(movieId);
        fillUI();
        getTrailers();
    }

    private void getTrailers() {
        TrailersControl trailersControl = new TrailersControl();
        trailersControl.getTrailers(activity, movieId);
    }

    public void getTrailersCallback(Activity activity, List<TrailersResultModel> list){
        final ViewGroup viewGroup = (ViewGroup) ((ViewGroup) activity
                .findViewById(android.R.id.content)).getChildAt(0);

        listTrailers = new ArrayList<>();
        listTrailers.addAll(list);

        lvTrailers = (ListView) viewGroup.findViewById(R.id.lv_trailers);
        pbLoading = (ProgressBar) viewGroup.findViewById(R.id.pb_loading);

        pbLoading.setVisibility(View.GONE);

        TrailersAdapter trailersAdapter = new TrailersAdapter(activity, listTrailers);
        lvTrailers.setAdapter(trailersAdapter);
    }

    private void fillUI() {
        tvReleaseDate.setText(movieReleaseDate);
        tvVoteAverage.setText(movieVoteAverage + "/10");
        tvOverview.setText(movieOverview);

        getSupportActionBar().setTitle(movieTitle);

        Picasso.with(activity)
                .load(Keys.BASE_IMAGE_URL + "w185" + movieThumbnailPath)
                .resize(Utils.SCREEN_WIDTH / 4, Utils.SCREEN_HEIGHT / 4)
                .into(ivPoster);

    }

    private void init() {
        activity = this;

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        ivPoster = (ImageView) findViewById(R.id.iv_poster_thumbnail);
        tvReleaseDate = (TextView) findViewById(R.id.tv_release_date);
        tvVoteAverage = (TextView) findViewById(R.id.tv_vote_average);
        tvOverview = (TextView) findViewById(R.id.tv_overview);
        lvTrailers = (ListView) findViewById(R.id.lv_trailers);
        pbLoading = (ProgressBar) findViewById(R.id.pb_loading);
        btShowReviews = (TextView) findViewById(R.id.bt_show_reviews);
        btAddToFavourite = (TextView) findViewById(R.id.bt_add_to_favourite);

        lvTrailers.setOnItemClickListener(this);
        btShowReviews.setOnClickListener(this);
        btAddToFavourite.setOnClickListener(this);
    }

    private void getIntentData(){
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        movieId = bundle.getInt(Keys.INTENT_POSTER_ID);
        movieTitle = bundle.getString(Keys.INTENT_POSTER_ORIGINAL_TITLE);
        movieThumbnailPath = bundle.getString(Keys.INTENT_POSTER_THUMBNAIL);
        movieReleaseDate = bundle.getString(Keys.INTENT_POSTER_RELEASE_DATE);
        movieVoteAverage = bundle.getDouble(Keys.INTENT_POSTER_VOTE_AVERAGE);
        movieOverview = bundle.getString(Keys.INTENT_POSTER_OVERVIEW);
    }

    private void checkThisMovieInFavourite(int id) {
        DB db = new DB(activity);
        String sql = "select " + db.getPo_id() + " from " + db.getPOSTER_TABLE() +
                " where " + db.getPo_id() + " = " + id;

        Cursor cursor = db.getDb().rawQuery(sql, null);

        Log.e(TAG, "cursor.getCount(): " + cursor.getCount());
        
        if(cursor.getCount() > 0){
            btAddToFavourite.setText(getResources().getString(R.string.remove_from_favourite));
            isMovieFavourite = true;
        }
        else{
            btAddToFavourite.setText(getResources().getString(R.string.add_to_favourite));
            isMovieFavourite = false;
        }
    }

    private void addToFavourite(){
        Log.e(TAG, "addToFavourite");
        DB db = new DB(activity);

        ContentValues cv = new ContentValues();

        cv.put(db.getPo_id(), movieId);
        cv.put(db.getPo_title(), movieTitle);
        cv.put(db.getPo_thumbnail(), movieThumbnailPath);
        cv.put(db.getPo_release_date(), movieReleaseDate);
        cv.put(db.getPo_vote_average(), movieVoteAverage);
        cv.put(db.getPo_overview(), movieOverview);

        long status = db.getDb().insert(db.getPOSTER_TABLE(), null, cv);
        Log.e(TAG,"status: " + status);

        db.getDb().close();

        btAddToFavourite.setText(getResources().getString(R.string.remove_from_favourite));
        isMovieFavourite = true;
    }

    private void removeFromFavourites() {
        DB db = new DB(activity);
        String whereC = db.getPo_id() + " = ?";
        int status = db.getDb().delete(db.getPOSTER_TABLE(), whereC, new String[]{ "" + movieId});
        Log.e(TAG,"status: " + status);

        if(status == 1)
            activity.finish();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        String url = "https://www.youtube.com/watch?v=" + listTrailers.get(i).getKey();
        Log.e(TAG, "url: " + url);

        Intent intent = new Intent(Intent.ACTION_VIEW,
                Uri.parse(url));
        startActivity(intent);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.bt_show_reviews) {
            Intent intent = new Intent(activity, ReviewsActivity.class);
            intent.putExtra(Keys.INTENT_POSTER_ID, movieId);
            intent.putExtra(Keys.INTENT_POSTER_ORIGINAL_TITLE, movieTitle);
            startActivity(intent);
        }
        else if(view.getId() == R.id.bt_add_to_favourite){
            if(isMovieFavourite)
                removeFromFavourites();
            else
                addToFavourite();
        }
    }
}
