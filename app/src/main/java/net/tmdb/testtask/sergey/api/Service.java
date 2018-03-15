package net.tmdb.testtask.sergey.api;

import net.tmdb.testtask.sergey.model.MoviesResponse;
import net.tmdb.testtask.sergey.model.TrailerResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;


public interface Service {


    @GET("movie/top_rated")
    Call<MoviesResponse> getTopRatedMovies(@Query("api_key") String apiKey);


    @GET("movie/{movie_id}/videos")
    Call<TrailerResponse> getMovieTrailer(@Path("movie_id") int id, @Query("api_key") String apiKey);


    @GET("search/movie?")
    Call<MoviesResponse> getSearchMovies(@Query("api_key") String apiKey,
                                         @Query("query") String query);

}
