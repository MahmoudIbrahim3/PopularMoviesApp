package com.mahmoud.popularmovies.Activity;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mahmoud.popularmovies.Adapter.MoviesAdapter;
import com.mahmoud.popularmovies.Control.DB;
import com.mahmoud.popularmovies.Control.MoviesControl;
import com.mahmoud.popularmovies.Model.MoviesResultModel;
import com.mahmoud.popularmovies.R;

import java.util.ArrayList;
import java.util.List;

import static com.mahmoud.popularmovies.Activity.MainActivity.isTwoPane;
import static com.mahmoud.popularmovies.Activity.MainActivity.listType;

public class MoviesFragment extends Fragment implements AdapterView.OnItemClickListener,
        AbsListView.OnScrollListener {

    private static GridView gvMmovies;
    public static MoviesAdapter adapter;
    private ViewGroup viewGroup;
    public static ArrayList<MoviesResultModel> listMovies  = null;
    private ProgressBar pbLoading;
    private String TAG = "MoviesFragment";
    private Toolbar toolbar;
    private TextView tvNoMovies;
    private DB db;
    private static Activity activity;
    //private int listType;
    public static int page = 1;
    private int preLast = -1;
    private ProgressBar pbFooterLoading;
    private View view;
    static OnHeadlineSelectedListener mCallback;

    // The container Activity must implement this interface so the frag can deliver messages
    public interface OnHeadlineSelectedListener {
        /** Called by HeadlinesFragment when a list item is selected */
        public void onArticleSelected(int position);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view  = inflater.inflate(R.layout.content_movies, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        init();
        getMovies();
        //getIntentData();
    }

    private void getMovies() {
        if (listType == MainActivity.MOST_POPULAR) {
            getMoviesByMostPopular(activity, page);
        }
        else if(listType == MainActivity.TOP_RATED) {
            getMoviesByTopRated(activity, page);
        }
        else if(listType == MainActivity.MY_FAVOURITE) {
            getMyFavourites(activity, db.getPo_id(), db.getPo_title(), db.getPo_thumbnail(),
                    db.getPo_release_date(), db.getPo_vote_average(), db.getPo_overview());
        }
    }

    /*private void getIntentData() {
        Bundle bundle = getArguments();
        if (bundle != null)
            listType = bundle.getInt("list_type");
        else
            listType = MainActivity.MOST_POPULAR;

        Log.e(TAG, "listType: " + listType);

        if (listType == MainActivity.MOST_POPULAR) {
            listMovies = null;
            adapter = null;
            page = 1;
            getMoviesByMostPopular(activity, page);
        }
        else if(listType == MainActivity.TOP_RATED) {
            listMovies = null;
            adapter = null;
            page = 1;
            getMoviesByTopRated(activity, page);
        }
        else if(listType == MainActivity.MY_FAVOURITE) {
                getMyFavourites(activity, db.getPo_id(), db.getPo_title(), db.getPo_thumbnail(),
                        db.getPo_release_date(), db.getPo_vote_average(), db.getPo_overview());
        }
    }*/

    /*@Override
    public void onStart() {
        super.onStart();
        Log.e(TAG, "onStart");
        if(listType == MY_FAVOURITE) {
            getMyFavourites(activity, db.getPo_id(), db.getPo_title(), db.getPo_thumbnail(),
                    db.getPo_vote_average(), db.getPo_release_date(), db.getPo_overview());
        }
    }*/

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy");
    }

    private void init() {
        activity = getActivity();
        /*toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);*/
        
        gvMmovies = (GridView) view.findViewById(R.id.gv_movies);
        pbLoading = (ProgressBar) view.findViewById(R.id.pb_loading);
        pbFooterLoading = (ProgressBar) view.findViewById(R.id.pb_footer_loading);
        tvNoMovies = (TextView) view.findViewById(R.id.tv_no_movies);

        gvMmovies.setOnItemClickListener(this);
        gvMmovies.setOnScrollListener(this);

        listMovies = null;
        adapter = null;
        page = 1;

        db = new DB(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception.
        try {
            mCallback = (OnHeadlineSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

    public void getMoviesByMostPopular(Activity activity, int page) {
        if(listMovies == null) {
            viewGroup = (ViewGroup) ((ViewGroup) activity
                    .findViewById(android.R.id.content)).getChildAt(0);
            pbLoading = (ProgressBar) viewGroup.findViewById(R.id.pb_loading);
            pbLoading.setVisibility(View.VISIBLE);
        }

        MoviesControl moviesControl = new MoviesControl();
        moviesControl.getMovies(activity, "popular", page);
    }

    public void getMoviesByTopRated(Activity activity, int page) {
        if(listMovies == null) {
            viewGroup = (ViewGroup) ((ViewGroup) activity
                    .findViewById(android.R.id.content)).getChildAt(0);
            pbLoading = (ProgressBar) viewGroup.findViewById(R.id.pb_loading);
            pbLoading.setVisibility(View.VISIBLE);
        }

        MoviesControl moviesControl = new MoviesControl();
        moviesControl.getMovies(activity, "top_rated", page);
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

            Log.e(TAG, "isTwoPane: " + isTwoPane);
            if(isTwoPane)
                mCallback.onArticleSelected(MovieDetailsFragment.mCurrentPosition);
        }
        else {
            listMovies.addAll(list);
            setAdapter(activity);
        }
    }

    public void setAdapter(Activity activity) {
        if(adapter == null) {
            adapter = new MoviesAdapter(activity, listMovies);
            gvMmovies.setAdapter(adapter);
        }
        else
            adapter.notifyDataSetChanged();
    }

    public void getMyFavourites(Activity activity, String... params) {
        db = new DB(activity);
        viewGroup = (ViewGroup) ((ViewGroup) activity
                .findViewById(android.R.id.content)).getChildAt(0);
        pbLoading = (ProgressBar) viewGroup.findViewById(R.id.pb_loading);
        gvMmovies = (GridView) viewGroup.findViewById(R.id.gv_movies);

        pbLoading.setVisibility(View.GONE);
        listMovies = null;
        adapter = null;
        listType = MainActivity.MY_FAVOURITE;

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

                if(isTwoPane)
                    mCallback.onArticleSelected(MovieDetailsFragment.mCurrentPosition);
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

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }*/

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);


    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Log.e(TAG, "onItemClick: " + i);
        // Notify the parent activity of selected item
        mCallback.onArticleSelected(i);
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

                    if(MainActivity.listType == MainActivity.MOST_POPULAR)
                        getMoviesByMostPopular(activity, page);
                    else if(MainActivity.listType == MainActivity.TOP_RATED)
                        getMoviesByTopRated(activity, page);
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
