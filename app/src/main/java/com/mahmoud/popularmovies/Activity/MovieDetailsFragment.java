package com.mahmoud.popularmovies.Activity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
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
import com.mahmoud.popularmovies.Model.MoviesResultModel;
import com.mahmoud.popularmovies.Model.TrailersResultModel;
import com.mahmoud.popularmovies.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MovieDetailsFragment extends Fragment implements
        AdapterView.OnItemClickListener, View.OnClickListener {

    private String TAG = "MovieDetailsFragment";
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
    private View view;
    public static int mCurrentPosition = 0;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // Save the current article selection in case we need to recreate the fragment
        outState.putInt(ARG_POSITION, mCurrentPosition);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // If activity recreated (such as from screen rotate), restore
        // the previous article selection set by onSaveInstanceState().
        // This is primarily necessary when in the two-pane layout.
        if (savedInstanceState != null) {
            mCurrentPosition = savedInstanceState.getInt(ARG_POSITION);
        }

        view  = inflater.inflate(R.layout.content_movie_details, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onStart() {
        super.onStart();
        init();
        // During startup, check if there are arguments passed to the fragment.
        // onStart is a good place to do this because the layout has already been
        // applied to the fragment at this point so we can safely call the method
        // below that sets the article text.
        Bundle args = getArguments();
        if (args != null) {
            getIntentData();
            checkThisMovieInFavourite(movieId);
            fillUI();
            getTrailers();
        }
        else if (mCurrentPosition != -1 && MoviesFragment.listMovies != null) {
            // Set article based on saved instance state defined during onCreateView
            updateMovieDetailsFragment(mCurrentPosition, MoviesFragment.listMovies.get(mCurrentPosition));
        }
    }

    private void getTrailers() {
        TrailersControl trailersControl = new TrailersControl();
        trailersControl.getTrailers(activity, movieId);
    }

    public void getTrailersCallback(Activity activity, List<TrailersResultModel> list){
        final ViewGroup viewGroup = (ViewGroup) ((ViewGroup) activity
                .findViewById(android.R.id.content)).getChildAt(0);

        if(list != null) {
            listTrailers = new ArrayList<>();
            listTrailers.addAll(list);

            lvTrailers = (ListView) viewGroup.findViewById(R.id.lv_trailers);
            pbLoading = (ProgressBar) viewGroup.findViewById(R.id.pb_loading);

            pbLoading.setVisibility(View.GONE);

            if(lvTrailers != null) {
                TrailersAdapter trailersAdapter = new TrailersAdapter(activity, listTrailers);
                lvTrailers.setAdapter(trailersAdapter);
            }
        }
    }

    private void fillUI() {
        tvReleaseDate.setText(movieReleaseDate);
        tvVoteAverage.setText(movieVoteAverage + "/10");
        tvOverview.setText(movieOverview);

        //getSupportActionBar().setTitle(movieTitle);

        Picasso.with(activity)
                .load(Keys.BASE_IMAGE_URL + "w185" + movieThumbnailPath)
                .resize(Utils.SCREEN_WIDTH / 4, Utils.SCREEN_HEIGHT / 4)
                .into(ivPoster);

    }

    private void init() {
        activity = getActivity();

        /*toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });*/

        ivPoster = (ImageView) view.findViewById(R.id.iv_poster_thumbnail);
        tvReleaseDate = (TextView) view.findViewById(R.id.tv_release_date);
        tvVoteAverage = (TextView) view.findViewById(R.id.tv_vote_average);
        tvOverview = (TextView) view.findViewById(R.id.tv_overview);
        lvTrailers = (ListView) view.findViewById(R.id.lv_trailers);
        pbLoading = (ProgressBar) view.findViewById(R.id.pb_loading);
        btShowReviews = (TextView) view.findViewById(R.id.bt_show_reviews);
        btAddToFavourite = (TextView) view.findViewById(R.id.bt_add_to_favourite);

        lvTrailers.setOnItemClickListener(this);
        btShowReviews.setOnClickListener(this);
        btAddToFavourite.setOnClickListener(this);
    }

    private void getIntentData(){
        Bundle bundle = getArguments();

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

        if(status == 1){
            btAddToFavourite.setText(getResources().getString(R.string.add_to_favourite));

            /*if(isTwoPane){
                *//*MoviesFragment moviesFragment = new MoviesFragment();
                moviesFragment.getMyFavourites(activity, db.getPo_id(), db.getPo_title(),
                        db.getPo_thumbnail(), db.getPo_release_date(), db.getPo_vote_average(),
                        db.getPo_overview());*//*
            }
            else {
                MoviesFragment firstFragment = new MoviesFragment();
                activity.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, firstFragment).commit();
            }*/
        }
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

    public void updateMovieDetailsFragment(int position, MoviesResultModel model) {
        mCurrentPosition = position;

        movieId = model.getId();
        movieTitle =model.getOriginalTitle();
        movieThumbnailPath = model.getPosterPath();
        movieReleaseDate = model.getReleaseDate();
        movieVoteAverage = model.getVoteAverage();
        movieOverview = model.getOverview();

        checkThisMovieInFavourite(movieId);
        fillUI();
        getTrailers();
    }
}
