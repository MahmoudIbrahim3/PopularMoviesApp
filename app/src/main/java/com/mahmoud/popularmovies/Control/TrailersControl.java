package com.mahmoud.popularmovies.Control;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import com.mahmoud.popularmovies.API.TrailersAPI;
import com.mahmoud.popularmovies.Activity.MovieDetailsFragment;
import com.mahmoud.popularmovies.Model.TrailersModel;
import com.mahmoud.popularmovies.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Mahmoud Ibrahim on 3/17/2016.
 */
public class TrailersControl {

    private String TAG = "TrailersControl";
    
    public void getTrailers(final Activity activity, int id){

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Keys.DOMAIN)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        TrailersAPI service = retrofit.create(TrailersAPI.class);
        final Call<TrailersModel> call = service.getTrailers(id, Keys.API_KEY);

        call.enqueue(new Callback<TrailersModel>() {
            @Override
            public void onResponse(Call<TrailersModel> call, Response<TrailersModel> response) {

                Log.e(TAG, "CallRequest: " + call.request().toString());
                Log.e(TAG, "ResponseCode: " + response.code());
                Log.e(TAG, "ResponseMessage: " + response.message());

                if(response.code() == Keys.SUCCESS){
                    MovieDetailsFragment movieDetailsActivity = new MovieDetailsFragment();
                    movieDetailsActivity.getTrailersCallback(activity, response.body().getResults());
                }
            }

            @Override
            public void onFailure(Call<TrailersModel> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.getMessage());
                Toast.makeText(activity, activity.getResources().getString(
                        R.string.internet_connection_error), Toast.LENGTH_SHORT).show();
                MovieDetailsFragment movieDetailsActivity = new MovieDetailsFragment();
                movieDetailsActivity.getTrailersCallback(activity, null);
            }
        });
    }
}
