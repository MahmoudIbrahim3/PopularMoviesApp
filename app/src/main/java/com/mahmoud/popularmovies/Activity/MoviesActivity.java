package com.mahmoud.popularmovies.Activity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mahmoud.popularmovies.Adapter.MoviesAdapter;
import com.mahmoud.popularmovies.Control.DB;
import com.mahmoud.popularmovies.Control.Keys;
import com.mahmoud.popularmovies.Control.MoviesControl;
import com.mahmoud.popularmovies.Model.MoviesResultModel;
import com.mahmoud.popularmovies.R;

import java.util.ArrayList;
import java.util.List;

public class MoviesActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, AbsListView.OnScrollListener {

    private GridView gvMmovies;
    private static MoviesAdapter adapter;
    private ViewGroup viewGroup;
    private static ArrayList<MoviesResultModel> listMovies  = null;
    private ProgressBar pbLoading;
    private String TAG = "MoviesActivity";
    private Toolbar toolbar;
    private TextView tvNoMovies;
    private DB db;
    private static Activity activity;
    private int MOST_POPULAR = 0;
    private int TOP_RATED = 1;
    private int MY_FAVOURITE = 2;
    private int listType = MOST_POPULAR;
    private static int page = 1;
    private int preLast = -1;
    private ProgressBar pbFooterLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "onCreate");
        setContentView(R.layout.activity_movies);

        init();

        if(savedInstanceState != null)
            restoreInstanceState(savedInstanceState);
        else {
            getMoviesByMostPopular(page);
        }
    }

    private void restoreInstanceState(Bundle savedInstanceState) {
        listType = savedInstanceState.getInt("list_type");
        page = savedInstanceState.getInt("page");
        preLast = savedInstanceState.getInt("pre_last");

        adapter = null;
        pbLoading.setVisibility(View.GONE);
        tvNoMovies.setVisibility(View.GONE);

        setAdapter(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.e(TAG, "onStart");
        if(listType == MY_FAVOURITE) {
            getMyFavourites(this, db.getPo_id(), db.getPo_title(), db.getPo_thumbnail(),
                    db.getPo_vote_average(), db.getPo_release_date(), db.getPo_overview());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy");
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        savedInstanceState.putInt("list_type", listType);
        savedInstanceState.putInt("page", page);
        savedInstanceState.putInt("pre_last", preLast);
    }

    private void init() {
        activity = this;
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        
        gvMmovies = (GridView) findViewById(R.id.gv_movies);
        pbLoading = (ProgressBar) findViewById(R.id.pb_loading);
        pbFooterLoading = (ProgressBar) findViewById(R.id.pb_footer_loading);
        tvNoMovies = (TextView) findViewById(R.id.tv_no_movies);

        gvMmovies.setOnItemClickListener(this);
        gvMmovies.setOnScrollListener(this);

        db = new DB(this);
    }

    private void getMoviesByMostPopular(int page) {
        getSupportActionBar().setTitle(getResources().getString(R.string.action_most_popular));
        MoviesControl moviesControl = new MoviesControl();
        moviesControl.getMovies(this, "popular", page);
    }

    private void getMoviesByTopRated(int page) {
        getSupportActionBar().setTitle(getResources().getString(R.string.action_top_rated));
        MoviesControl moviesControl = new MoviesControl();
        moviesControl.getMovies(this, "top_rated", page);
    }

    public void showMovies(Activity activity, List<MoviesResultModel> list, int page) {
        viewGroup = (ViewGroup) ((ViewGroup) activity
                .findViewById(android.R.id.content)).getChildAt(0);
        gvMmovies = (GridView) viewGroup.findViewById(R.id.gv_movies);
        pbLoading = (ProgressBar) viewGroup.findViewById(R.id.pb_loading);
        tvNoMovies = (TextView) viewGroup.findViewById(R.id.tv_no_movies);
        pbFooterLoading = (ProgressBar) viewGroup.findViewById(R.id.pb_footer_loading);

        gvMmovies.setOnItemClickListener(this);
        pbLoading.setVisibility(View.GONE);
        tvNoMovies.setVisibility(View.GONE);

        this.page = page;
        pbLoading.setVisibility(View.GONE);
        pbFooterLoading.setVisibility(View.GONE);

        if(list == null){
            tvNoMovies.setVisibility(View.VISIBLE);
        }
        else if(listMovies == null) {   //First page.
            listMovies = new ArrayList<>();
            listMovies.addAll(list);
            setAdapter(activity);
        }
        else {
            listMovies.addAll(list);
            setAdapter(activity);
        }
    }

    private void setAdapter(Activity activity) {
        if(adapter == null) {
            adapter = new MoviesAdapter(activity, listMovies);
            gvMmovies.setAdapter(adapter);
        }
        else
            adapter.notifyDataSetChanged();
    }

    private void getMyFavourites(Activity activity, String... params) {
        getSupportActionBar().setTitle(getResources().getString(R.string.action_my_favourites));
        listMovies = null;
        adapter = null;
        listType = MY_FAVOURITE;

        String str="select ";

        for(int i = 0; i<params.length; i++) {
            if(i==params.length-1)
                str+=params[i]+" from " + db.getPOSTER_TABLE();
            else
                str+=params[i]+" , ";
        }

        try {
            Cursor cursor = db.getDb().rawQuery(str, null);
            Log.e(TAG, "cursor_size: " + cursor.getCount());
            listMovies = new ArrayList<>();

            if(cursor.getCount() > 0){
                cursor.moveToFirst();
                do {
                    MoviesResultModel favouritesModel = new MoviesResultModel();

                    favouritesModel.setId(cursor.getInt(cursor.getColumnIndex(db.getPo_id())));
                    favouritesModel.setOriginalTitle(cursor.getString(cursor.getColumnIndex(db.getPo_title())));
                    favouritesModel.setPosterPath(cursor.getString(cursor.getColumnIndex(db.getPo_thumbnail())));
                    favouritesModel.setReleaseDate(cursor.getString(cursor.getColumnIndex(db.getPo_release_date())));
                    favouritesModel.setVoteAverage(Double.parseDouble(cursor.getString(cursor.getColumnIndex(db.getPo_vote_average()))));
                    favouritesModel.setOverview(cursor.getString(cursor.getColumnIndex(db.getPo_overview())));

                    listMovies.add(favouritesModel);
                } while (cursor.moveToNext());

                setAdapter(activity);
            }
            else{
                setAdapter(activity);
                tvNoMovies.setVisibility(View.VISIBLE);
                tvNoMovies.setText(getResources().getString(R.string.no_favourites));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_most_popular:
                listMovies = null;
                adapter = null;
                page = 1;
                getMoviesByMostPopular(page);
                listType = MOST_POPULAR;
                break;

            case R.id.action_top_rated:
                listMovies = null;
                adapter = null;
                page = 1;
                getMoviesByTopRated(page);
                listType = TOP_RATED;
                break;

            case R.id.action_my_favourites:
                getMyFavourites(this, db.getPo_id(), db.getPo_title(), db.getPo_thumbnail(),
                        db.getPo_release_date(), db.getPo_vote_average(), db.getPo_overview());
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        MoviesResultModel model = listMovies.get(i);

        Intent intent = new Intent(activity, MovieDetailsActivity.class);
        intent.putExtra(Keys.INTENT_POSTER_ID, model.getId());
        intent.putExtra(Keys.INTENT_POSTER_ORIGINAL_TITLE, model.getOriginalTitle());
        intent.putExtra(Keys.INTENT_POSTER_THUMBNAIL, model.getPosterPath());
        intent.putExtra(Keys.INTENT_POSTER_RELEASE_DATE, model.getReleaseDate());
        intent.putExtra(Keys.INTENT_POSTER_VOTE_AVERAGE, model.getVoteAverage());
        intent.putExtra(Keys.INTENT_POSTER_OVERVIEW, model.getOverview());
        activity.startActivity(intent);
    }

    @Override
    public void onScrollStateChanged(AbsListView absListView, int i) {

    }

    @Override
    public void onScroll(AbsListView absListView, int firstVisible, int visibleCount,
                         int allItemsCount) {
        if(page != -1){
            final int lastItem = firstVisible + visibleCount;
            if (lastItem == allItemsCount && allItemsCount > 0 && firstVisible != 0) {
                Log.e(TAG, "firstVisible: " + firstVisible);
                Log.e(TAG, "visibleCount: " + visibleCount);
                Log.e(TAG, "allItemsCount: " + allItemsCount);

                pbFooterLoading.setVisibility(View.VISIBLE);

                if (preLast  != lastItem) { // to avoid multiple calls for last item
                    Log.e("Last", "Last");
                    preLast = lastItem;
                    pbFooterLoading.setVisibility(View.VISIBLE);

                    if(listType == MOST_POPULAR)
                        getMoviesByMostPopular(page);
                    else if(listType == TOP_RATED)
                        getMoviesByTopRated(page);
                }
                else
                    Log.e("preLast", "preLast: " + preLast);

            }
            else if(pbFooterLoading.getVisibility() == View.VISIBLE){
                pbFooterLoading.setVisibility(View.GONE);
            }
        }
    }
}
