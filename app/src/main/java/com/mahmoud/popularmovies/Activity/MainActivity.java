/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mahmoud.popularmovies.Activity;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.mahmoud.popularmovies.Control.DB;
import com.mahmoud.popularmovies.Control.Keys;
import com.mahmoud.popularmovies.Model.MoviesResultModel;
import com.mahmoud.popularmovies.R;

import static com.mahmoud.popularmovies.Activity.MovieDetailsFragment.mCurrentPosition;
import static com.mahmoud.popularmovies.Activity.MoviesFragment.adapter;
import static com.mahmoud.popularmovies.Activity.MoviesFragment.listMovies;
import static com.mahmoud.popularmovies.Activity.MoviesFragment.page;

public class MainActivity extends AppCompatActivity
        implements MoviesFragment.OnHeadlineSelectedListener {

    private Toolbar toolbar;
    public static int MOST_POPULAR = 0;
    public static int TOP_RATED = 1;
    public static int MY_FAVOURITE = 2;
    public static int listType;
    private String TAG = "MainActivity";
    public static boolean isTwoPane = true;
    private boolean isHideMenu = false;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(Keys.API_KEY.equals("")) {
            //pbLoading.setVisibility(View.GONE);
            Toast.makeText(this, getResources().getString(R.string.set_your_api_key_first),
                    Toast.LENGTH_LONG).show();
        }
        else if (savedInstanceState != null){
            restoreInstanceState(savedInstanceState);
        }
        setContentView(R.layout.activity_main);

        if(savedInstanceState != null){
            adapter = null;
            //pbLoading.setVisibility(View.GONE);
            //tvNoMovies.setVisibility(View.GONE);

            MoviesFragment moviesFragment = new MoviesFragment();
            moviesFragment.setAdapter(this);
        }

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.action_most_popular));

        // Check whether the activity is using the layout version with
        // the fragment_container FrameLayout. If so, we must add the first fragment
        if (findViewById(R.id.fragment_container) != null) {

            isTwoPane = false;
            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                return;
            }

            setMoviesFragment();
        }
        else
            isTwoPane = true;
    }

    private void restoreInstanceState(Bundle savedInstanceState) {
        listType = savedInstanceState.getInt("list_type");
        mCurrentPosition = savedInstanceState.getInt("current_position");
        //page = savedInstanceState.getInt("page");
        //preLast = savedInstanceState.getInt("pre_last");
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        savedInstanceState.putInt("list_type", listType);
        savedInstanceState.putInt("current_position", mCurrentPosition);
        //savedInstanceState.putInt("page", page);
        //savedInstanceState.putInt("pre_last", preLast);
        Log.e(TAG, "listType: " + listType);
    }

    private void setMoviesFragment() {
        // Create an instance of ExampleFragment
        MoviesFragment firstFragment = new MoviesFragment();

        // In case this activity was started with special instructions from an Intent,
        // pass the Intent's extras to the fragment as arguments

        // Add the fragment to the 'fragment_container' FrameLayout
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, firstFragment).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(isHideMenu)
            return false;
        else {
            getMenuInflater().inflate(R.menu.menu_main, menu);
            return true;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(Keys.API_KEY.equals("")) {
            //pbLoading.setVisibility(View.GONE);
            Toast.makeText(this, getResources().getString(R.string.set_your_api_key_first),
                    Toast.LENGTH_LONG).show();
        }
        else {
            MoviesFragment moviesFragment = new MoviesFragment();

            switch (item.getItemId()) {
                case R.id.action_most_popular:
                    listType = MOST_POPULAR;
                    getSupportActionBar().setTitle(getResources().getString(R.string.action_most_popular));
                    listMovies = null;
                    adapter = null;
                    page = 1;
                    moviesFragment.getMoviesByMostPopular(this, page);
                    break;

                case R.id.action_top_rated:
                    listType = TOP_RATED;
                    getSupportActionBar().setTitle(getResources().getString(R.string.action_top_rated));
                    listMovies = null;
                    adapter = null;
                    page = 1;
                    moviesFragment.getMoviesByTopRated(this, page);
                    break;

                case R.id.action_my_favourites:
                    listType = MY_FAVOURITE;
                    getSupportActionBar().setTitle(getResources().getString(R.string.action_my_favourites));
                    DB db = new DB(this);
                    moviesFragment.getMyFavourites(this, db.getPo_id(), db.getPo_title(), db.getPo_thumbnail(),
                            db.getPo_release_date(), db.getPo_vote_average(), db.getPo_overview());
                    break;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    public void onArticleSelected(int position) {
        // The user selected the headline of an article from the HeadlinesFragment

        // Capture the article fragment from the activity layout
        MovieDetailsFragment movieDetailsFragment = (MovieDetailsFragment)
                getSupportFragmentManager().findFragmentById(R.id.article_fragment);

        if (movieDetailsFragment != null) {
            // If article frag is available, we're in two-pane layout...
            isTwoPane = true;

            // Call a method in the ArticleFragment to update its content
            movieDetailsFragment.updateMovieDetailsFragment(position, listMovies.get(position));

        } else {
            // If the frag is not available, we're in the one-pane layout and must swap frags...
            isTwoPane = false;
            isHideMenu = true;
            invalidateOptionsMenu();

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setMoviesFragment();
                    isHideMenu = false;
                    invalidateOptionsMenu();
                    getSupportActionBar().setDisplayHomeAsUpEnabled(false);

                    switch (listType) {
                        case 0:
                            getSupportActionBar().setTitle(getResources().getString(
                                    R.string.action_most_popular));
                            break;
                        case 1:
                            getSupportActionBar().setTitle(getResources().getString(
                                    R.string.action_top_rated));
                            break;
                        case 2:
                            getSupportActionBar().setTitle(getResources().getString(
                                    R.string.action_my_favourites));
                            break;
                    }
                }
            });

            MoviesResultModel model = listMovies.get(position);

            // Create fragment and give it an argument for the selected article
            MovieDetailsFragment newFragment = new MovieDetailsFragment();
            Bundle args = new Bundle();
            args.putInt(Keys.INTENT_POSTER_ID, model.getId());
            args.putString(Keys.INTENT_POSTER_ORIGINAL_TITLE, model.getOriginalTitle());
            args.putString(Keys.INTENT_POSTER_THUMBNAIL, model.getPosterPath());
            args.putString(Keys.INTENT_POSTER_RELEASE_DATE, model.getReleaseDate());
            args.putDouble(Keys.INTENT_POSTER_VOTE_AVERAGE, model.getVoteAverage());
            args.putString(Keys.INTENT_POSTER_OVERVIEW, model.getOverview());
            newFragment.setArguments(args);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            // Replace whatever is in the fragment_container view with this fragment,
            // and add the transaction to the back stack so the user can navigate back
            transaction.replace(R.id.fragment_container, newFragment);
            transaction.addToBackStack(null);

            // Commit the transaction
            transaction.commit();

            getSupportActionBar().setTitle(model.getOriginalTitle());
        }
    }
}