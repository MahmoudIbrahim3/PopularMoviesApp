package com.mahmoud.popularmovies.Control;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import com.mahmoud.popularmovies.API.ReviewsAPI;
import com.mahmoud.popularmovies.Activity.ReviewsActivity;
import com.mahmoud.popularmovies.Model.ReviewsModel;
import com.mahmoud.popularmovies.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Mahmoud Ibrahim on 3/17/2016.
 */
public class ReviewsControl {

    private String TAG = "ReviewsControl";
    
    public void getReviews(final Activity activity, int id, int page){

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Keys.DOMAIN)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ReviewsAPI service = retrofit.create(ReviewsAPI.class);
        final Call<ReviewsModel> call = service.getReviews(id, Keys.API_KEY, page);

        call.enqueue(new Callback<ReviewsModel>() {
            @Override
            public void onResponse(Call<ReviewsModel> call, Response<ReviewsModel> response) {

                Log.e(TAG, "CallRequest: " + call.request().toString());
                Log.e(TAG, "ResponseCode: " + response.code());
                Log.e(TAG, "ResponseMessage: " + response.message());

                ReviewsActivity reviewsActivity = new ReviewsActivity();

                if(response.code() == Keys.SUCCESS){
                    reviewsActivity.getReviewsCallBack(activity, response.body().getResults());
                }
                else{
                    reviewsActivity.getReviewsCallBack(activity, null);
                }
            }

            @Override
            public void onFailure(Call<ReviewsModel> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.getMessage());
                Toast.makeText(activity, activity.getResources().getString(
                        R.string.internet_connection_error), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
