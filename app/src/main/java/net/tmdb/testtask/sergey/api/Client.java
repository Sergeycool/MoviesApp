package net.tmdb.testtask.sergey.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class Client {

    private static final String BASE_URL = "http://api.themoviedb.org/3/";
    private static Retrofit retrofit = null;
    private Service api;

    public static String getBaseUrl() {
        return BASE_URL;
    }

    public static Retrofit getRetrofit() {
        return retrofit;
    }

    public static void setRetrofit(Retrofit retrofit) {
        Client.retrofit = retrofit;
    }

//    public MoviesResponse getClientSearch(String search, int page) throws IOException {
//        return this.api
//                .getSearchedMovies(Service.API_KEY, search, page).execute()
//                .body();
//    }

    public static Retrofit getClient(){

        if (retrofit == null){
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
