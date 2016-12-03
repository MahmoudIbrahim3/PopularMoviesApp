package com.mahmoud.popularmovies.API;

import com.mahmoud.popularmovies.Model.TrailersModel;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Mahmoud on 10/28/2016.
 */

public interface TrailersAPI {
    @GET("movie/{id}/videos?")
    Call<TrailersModel> getTrailers(@Path("id") Integer id,
                                    @Query("api_key") String APIKey);
}
