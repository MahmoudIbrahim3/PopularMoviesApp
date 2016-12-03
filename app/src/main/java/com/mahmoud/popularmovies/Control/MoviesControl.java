package com.mahmoud.popularmovies.Control;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import com.mahmoud.popularmovies.API.MoviesAPI;
import com.mahmoud.popularmovies.Activity.MoviesActivity;
import com.mahmoud.popularmovies.Model.MoviesModel;
import com.mahmoud.popularmovies.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Mahmoud Ibrahim on 3/17/2016.
 */
public class MoviesControl {

    private String TAG = "MoviesControl";
    
    public void getMovies(final Activity activity, String sortType, final int page){

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Keys.DOMAIN)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        MoviesAPI service = retrofit.create(MoviesAPI.class);
        final Call<MoviesModel> call = service.getMovies(sortType, Keys.API_KEY, page);

        call.enqueue(new Callback<MoviesModel>() {
            @Override
            public void onResponse(Call<MoviesModel> call, Response<MoviesModel> response) {

                Log.e(TAG, "CallRequest: " + call.request().toString());
                Log.e(TAG, "ResponseCode: " + response.code());
                Log.e(TAG, "ResponseMessage: " + response.message());

                if(response.code() == Keys.SUCCESS){
                    int page2 = page + 1;
                    MoviesActivity moviesActivity = new MoviesActivity();
                    moviesActivity.showMovies(activity, response.body().getResults(), page2);
                }
            }

            @Override
            public void onFailure(Call<MoviesModel> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.getMessage());
                Toast.makeText(activity, activity.getResources().getString(
                        R.string.internet_connection_error), Toast.LENGTH_SHORT).show();
                MoviesActivity moviesActivity = new MoviesActivity();
                moviesActivity.showMovies(activity, null, page);
            }
        });
    }
}
