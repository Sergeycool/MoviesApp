package com.delaroystudios.movieapp.api;

import android.support.v7.util.SortedList;

import com.delaroystudios.movieapp.model.MoviesResponse;
import com.delaroystudios.movieapp.model.TrailerResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;


public interface Service {

    ////////////////////////////////////////////
    @GET("movie/popular")
    Call<MoviesResponse> getPopularMovies(@Query("api_key") String apiKey);
    @GET("movie/top_rated")
    Call<MoviesResponse> getTopRatedMovies(@Query("api_key") String apiKey);
    ////////////////////////////////////////////


    @GET("movie/{movie_id}/videos")
    Call<TrailerResponse> getMovieTrailer(@Path("movie_id") int id, @Query("api_key") String apiKey);


    @GET("search/movie?")
    Call<MoviesResponse> getSearchMovies(@Query("api_key") String apiKey,
                                         @Query("query") String query);

}
