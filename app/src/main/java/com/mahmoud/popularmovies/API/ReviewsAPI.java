package com.mahmoud.popularmovies.API;

import com.mahmoud.popularmovies.Model.ReviewsModel;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Mahmoud on 10/28/2016.
 */

public interface ReviewsAPI {
    @GET("movie/{id}/reviews?")
    Call<ReviewsModel> getReviews(@Path("id") int id,
                                  @Query("api_key") String APIKey,
                                  @Query("page") int page);
}
