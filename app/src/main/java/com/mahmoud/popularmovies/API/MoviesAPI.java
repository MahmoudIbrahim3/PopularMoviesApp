package com.mahmoud.popularmovies.API;

import com.mahmoud.popularmovies.Model.MoviesModel;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Mahmoud on 10/28/2016.
 */

public interface MoviesAPI {
    @GET("movie/{sort_type}?")
    Call<MoviesModel> getMovies(@Path("sort_type") String sortType,
                                @Query("api_key") String APIKey,
                                @Query("page") int page);
}
