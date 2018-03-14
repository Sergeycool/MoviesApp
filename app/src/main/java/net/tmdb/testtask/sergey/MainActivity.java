package net.tmdb.testtask.sergey;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.Toast;

import net.tmdb.testtask.sergey.adapter.MoviesAdapter;
import net.tmdb.testtask.sergey.adapter.TestAdapter;
import net.tmdb.testtask.sergey.api.Service;
import net.tmdb.testtask.sergey.datacache.DataCacheDbHelper;
import net.tmdb.testtask.sergey.model.Movie;
import net.tmdb.testtask.sergey.model.MoviesResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MoviesAdapter adapter;
    private List<Movie> movieList;
    private SearchView searchView;
    private DataCacheDbHelper cacheDbHelper;
    private String mQuery;
    private SwipeRefreshLayout swipeContainer;
    ProgressDialog pd;
    Movie record;
    int cacheSize = 10 * 1024 * 1024; // 10 MiB


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();

        //For testing the recipe collection sorting alphabetically
        TestAdapter testAdapter = new TestAdapter(LayoutInflater.from(this));
        recyclerView.setAdapter(testAdapter);
        testAdapter.setMovie(movieList);

    }

    public Activity getActivity() {
        Context context = this;
        while (context instanceof ContextWrapper) {
            if (context instanceof Activity) {
                return (Activity) context;
            }
            context = ((ContextWrapper) context).getBaseContext();
        }
        return null;

    }

    private void initViews() {

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        searchView = (SearchView) findViewById(R.id.search);
        movieList = new ArrayList<>();
        adapter = new MoviesAdapter(this, movieList);

        //set parameters for search view
        searchView.setActivated(true);
        searchView.setQueryHint("Type a movie title");
        searchView.onActionViewExpanded();
        searchView.setIconified(false);
        searchView.clearFocus();

        if (getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(this, 5));
        }

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();


        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.main_content);
        swipeContainer.setColorSchemeResources(android.R.color.holo_orange_dark);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initViews();
            }
        });

        if (mQuery != null) new MovieSearcher().start();

    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString("query", mQuery);

    }


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);


        mQuery = savedInstanceState.getString("query");

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

//    private void loadJSON() {
//
//        try {
//            if (BuildConfig.THE_MOVIE_DB_API_TOKEN.isEmpty()) {
//                Toast.makeText(getApplicationContext(), "Please obtain API Key firstly from themoviedb.org", Toast.LENGTH_SHORT).show();
//                pd.dismiss();
//                return;
//            }
//            Cache cache = new Cache(getCacheDir(), cacheSize);
//
//            OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                    .cache(cache)
//                    .addInterceptor(new Interceptor() {
//                        @Override
//                        public okhttp3.Response intercept(Interceptor.Chain chain)
//                                throws IOException {
//                            Request request = chain.request();
//                            if (!isNetworkAvailable()) {
//                                int maxStale = 60 * 60 * 24 * 28; // tolerate 4-weeks stale \
//                                request = request
//                                        .newBuilder()
//                                        .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
//                                        .build();
//                            }
//                            return chain.proceed(request);
//                        }
//                    })
//                    .build();
//
//            Retrofit.Builder builder = new Retrofit.Builder()
//                    .baseUrl("http://api.themoviedb.org/3/")
//                    .client(okHttpClient)
//                    .addConverterFactory(GsonConverterFactory.create());
//
//            final Retrofit retrofit = builder.build();
//            Service apiService = retrofit.create(Service.class);
//
//
//            Call<MoviesResponse> call = apiService.getPopularMovies(BuildConfig.THE_MOVIE_DB_API_TOKEN);
//            call.enqueue(new Callback<MoviesResponse>() {
//                @Override
//                public void onResponse(Call<MoviesResponse> call, Response<MoviesResponse> response) {
//                    List<Movie> movies = response.body().getResults();
//                    Collections.sort(movies, Movie.BY_NAME_ALPHABETICAL);
//
//                    recyclerView.setAdapter(new MoviesAdapter(getApplicationContext(), movies));
//                    recyclerView.smoothScrollToPosition(0);
//                    if (swipeContainer.isRefreshing()) {
//                        swipeContainer.setRefreshing(false);
//                    }
//
//                }
//
//                @Override
//                public void onFailure(Call<MoviesResponse> call, Throwable t) {
//                    Log.d("Error", t.getMessage());
//                    Toast.makeText(MainActivity.this, "Error Fetching Data!", Toast.LENGTH_SHORT).show();
//                }
//            });
//        } catch (Exception e) {
//            Log.d("Error", e.getMessage());
//            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
//        }
//    }

    private void cachingData(Movie movies){

            cacheDbHelper = new DataCacheDbHelper(getActivity());

            record = movies;

            Double rate = record.getVoteAverage();

            record.setId(record.getId());
            record.setOriginalTitle(record.getTitle());
            record.setPosterPath(record.getPosterPath());
            record.setVoteAverage(rate);
            record.setOverview(record.getOverview());

            cacheDbHelper.addQuery(record);

    }

    private void loadJSON1() {

        try {
            if (BuildConfig.THE_MOVIE_DB_API_TOKEN.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Please obtain API Key firstly from themoviedb.org", Toast.LENGTH_SHORT).show();
                pd.dismiss();
                return;
            }

            Cache cache = new Cache(getCacheDir(), cacheSize);

            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .cache(cache)
                    .addInterceptor(new Interceptor() {
                        @Override
                        public okhttp3.Response intercept(Interceptor.Chain chain)
                                throws IOException {
                            Request request = chain.request();
                            if (!isNetworkAvailable()) {
                                int maxStale = 60 * 60 * 24 * 28; // tolerate 4-weeks stale \
                                request = request
                                        .newBuilder()
                                        .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
                                        .build();
                            }
                            return chain.proceed(request);
                        }
                    })
                    .build();

            Retrofit.Builder builder = new Retrofit.Builder()
                    .baseUrl("http://api.themoviedb.org/3/")
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create());

            Retrofit retrofit = builder.build();
            Service apiService = retrofit.create(Service.class);
            //Client Client = new Client();
            //Service apiService =
            //Client.getClient().create(Service.class);
            Call<MoviesResponse> call = apiService.getTopRatedMovies(BuildConfig.THE_MOVIE_DB_API_TOKEN);
            call.enqueue(new Callback<MoviesResponse>() {
                @Override
                public void onResponse(Call<MoviesResponse> call, Response<MoviesResponse> response) {
                    List<Movie> movies = response.body().getResults();
                    recyclerView.setAdapter(new MoviesAdapter(getApplicationContext(), movies));
                    recyclerView.smoothScrollToPosition(0);
                    if (swipeContainer.isRefreshing()) {
                        swipeContainer.setRefreshing(false);
                    }
                }

                @Override
                public void onFailure(Call<MoviesResponse> call, Throwable t) {
                    Log.d("Error", t.getMessage());
                    Toast.makeText(MainActivity.this, "Error Fetching Data!", Toast.LENGTH_SHORT).show();

                }
            });
        } catch (Exception e) {
            Log.d("Error", e.getMessage());
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    private void searchTitle(final String query) {

        try {
            if (BuildConfig.THE_MOVIE_DB_API_TOKEN.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Please obtain API Key firstly from themoviedb.org", Toast.LENGTH_SHORT).show();
                pd.dismiss();
                return;
            }


            OkHttpClient client = new OkHttpClient();

            Retrofit.Builder builder = new Retrofit.Builder()
                    .baseUrl("http://api.themoviedb.org/3/")
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create());

            final Retrofit retrofit = builder.build();
            Service apiService = retrofit.create(Service.class);


            Call<MoviesResponse> call = apiService.getSearchMovies(BuildConfig.THE_MOVIE_DB_API_TOKEN, query);
            call.enqueue(new Callback<MoviesResponse>() {
                @Override
                public void onResponse(Call<MoviesResponse> call, Response<MoviesResponse> response) {


                    List<Movie> movies = response.body().getResults();

                    for(int i=0;i<movies.size();i++){
                        cachingData(movies.get(i));
                    }

                    recyclerView.setAdapter(new MoviesAdapter(getApplicationContext(), movies));
                    recyclerView.smoothScrollToPosition(0);
                    if (swipeContainer.isRefreshing()) {
                        swipeContainer.setRefreshing(false);
                    }

                }

                @Override
                public void onFailure(Call<MoviesResponse> call, Throwable t) {
                    Log.d("Error", t.getMessage());
                    Toast.makeText(MainActivity.this, "Error Fetching Data!", Toast.LENGTH_SHORT).show();

                    List<Movie> cacheMov = cacheDbHelper.getAllQueries(mQuery);


                    recyclerView.setAdapter(new MoviesAdapter(getApplicationContext(), cacheMov));
                    recyclerView.smoothScrollToPosition(0);
                    if (swipeContainer.isRefreshing()) {
                        swipeContainer.setRefreshing(false);
                    }


                }
            });
        } catch (NullPointerException e) {
            Log.d("Error", e.getMessage());
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Log.d("Error", e.getMessage());
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }

    }

    private class MovieSearcher extends Thread {

        @Override
        public void run() {


            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    searchTitle(mQuery);

                }
            });


        }
    }


    @Override
    public void onResume() {
        super.onResume();

        if (mQuery == null) loadJSON1();
        if(mQuery != null && (movieList.size() == 0)) new MovieSearcher().start();


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                mQuery = query;
                if (query != null) new MovieSearcher().start();

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

//                    mQuery = newText;
//                    if (newText != null) searchTitle(newText);//new MovieSearcher().start();

                return false;
        }
        });

    }
}
