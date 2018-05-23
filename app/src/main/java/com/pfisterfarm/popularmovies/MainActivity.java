package com.pfisterfarm.popularmovies;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;

import com.pfisterfarm.popularmovies.models.Movie;
import com.pfisterfarm.popularmovies.models.MovieAdapter;
import com.pfisterfarm.popularmovies.models.Movies;
import com.pfisterfarm.popularmovies.retrofit.tmdbInterface;
import com.pfisterfarm.popularmovies.retrofit.tmdbClient;
import com.pfisterfarm.popularmovies.utils.helpers;

public class MainActivity extends AppCompatActivity {

  private static final String sApiKey = BuildConfig.API_KEY;
  private static final int POPULAR = 0;
  private static final int TOPRATED = 1;

  static ArrayList<Movie> popularMovies;
  static ArrayList<Movie> topRatedMovies;

  tmdbInterface tmdbService = tmdbClient.getClient().create(tmdbInterface.class);

  MovieAdapter popularMovieAdapter, topRatedMovieAdapter;
  GridView gridView;
  Parcelable scrollState;
  public IntentFilter connectIntent;
  public connectBroadcastReceiver connectivityReceiver;

  int displayMode = POPULAR;
  static boolean dataLoaded = false;

  private static final String displayModeStr = "displayMode";
  private static final String popularStr = "popular";
  private static final String topRatedStr = "top_rated";
  private static final String scrollPosStr = "scroll_pos";
  private static final String logTag = "POPMOVIES";


  @Override
  protected void onCreate(Bundle savedInstanceState) {

    final String MOVIE_KEY = "movie";

    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    gridView = (GridView) findViewById(R.id.movieGrid);

    // get a broadcast receiver ready to check if data needs to be loaded when
      // network has just become available
    connectIntent = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
    connectivityReceiver = new connectBroadcastReceiver();

     try {
      if ((savedInstanceState == null) ||
              !(((savedInstanceState.containsKey(displayModeStr)) &&
                      (savedInstanceState.containsKey(popularStr)) &&
                      (savedInstanceState.containsKey(topRatedStr))))) {
          popularMovies = new ArrayList<Movie>();
          topRatedMovies = new ArrayList<Movie>();
          if (helpers.isOnline(getApplicationContext())) {
                loadData();
          } else {
                helpers.showOKAlertDialog(this, R.string.no_network_title, R.string.no_network_text);
          }
      } else {
        displayMode = savedInstanceState.getInt(displayModeStr);
        popularMovies = savedInstanceState.getParcelableArrayList(popularStr);
        topRatedMovies = savedInstanceState.getParcelableArrayList(topRatedStr);
        scrollState = savedInstanceState.getParcelable(scrollPosStr);
        gridView.onRestoreInstanceState(scrollState);
        BottomNavigationView bottNav = (BottomNavigationView) findViewById(R.id.bottom_nav);
        if (popularMovies.size() > 0 && topRatedMovies.size() > 0) {
            bottNav.getMenu().findItem(R.id.action_popular).setEnabled(true);
            bottNav.getMenu().findItem(R.id.action_top_rated).setEnabled(true);
        }
      }

      popularMovieAdapter = new MovieAdapter(this, popularMovies);
      topRatedMovieAdapter = new MovieAdapter(this, topRatedMovies);

      switch (displayMode) {
        case POPULAR:
          gridView.setAdapter(popularMovieAdapter);
          break;
        case TOPRATED:
          gridView.setAdapter(topRatedMovieAdapter);
          break;
      }
      gridView.invalidate();    // force update on initial load

      gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
          Intent detailIntent = new Intent(MainActivity.this, DetailActivity.class);
          switch (displayMode) {
            case POPULAR:
              detailIntent.putExtra(MOVIE_KEY, popularMovies.get(i));
              break;
            case TOPRATED:
              detailIntent.putExtra(MOVIE_KEY, topRatedMovies.get(i));
              break;
          }
          startActivity(detailIntent);
        }
      });

      android.support.design.widget.BottomNavigationView bottomNav = (android.support.design.widget.BottomNavigationView) findViewById(R.id.bottom_nav);

      bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
          switch (item.getItemId()) {
            case R.id.action_popular:
              if (displayMode != POPULAR) {
                displayMode = POPULAR;
                gridView.setAdapter(popularMovieAdapter);
                gridView.invalidate();
              }
              break;
            case R.id.action_top_rated:
              if (displayMode != TOPRATED) {
                displayMode = TOPRATED;
                gridView.setAdapter(topRatedMovieAdapter);
                gridView.invalidate();
              }
              // favorites menu item is a placeholder until Stage 2
            case R.id.action_favorite:
              break;
          }
          return true;
        }
      });

    } catch (Exception e) {
      e.printStackTrace();
    }
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(connectivityReceiver, connectIntent);
        if (!dataLoaded) {
            if (helpers.isOnline(this)) {
                loadData();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(connectivityReceiver);
    }

    private void loadData() {
      if (!dataLoaded) {
          Call<Movies> call = tmdbService.fetchPopularMovies(sApiKey);
          // Use retrofit to fetch the popular movies
          call.enqueue(new Callback<Movies>() {

              @Override
              public void onResponse(Call<Movies> call, retrofit2.Response<Movies> response) {
                  List<Movie> returnList = response.body().getResults();
                  popularMovies.clear();
                  popularMovies.addAll(returnList);
                  helpers.fixDates(popularMovies);
                  BottomNavigationView bottNav = (BottomNavigationView) findViewById(R.id.bottom_nav);
                  bottNav.getMenu().findItem(R.id.action_popular).setEnabled(true);
              }

              @Override
              public void onFailure(Call<Movies> call, Throwable t) {
                  Log.e(logTag, getString(R.string.fetch_popular_failure));
                  t.printStackTrace();
              }
          });

          // Use retrofit to fetch the top rated movies
          call = tmdbService.fetchTopRatedMovies(sApiKey);
          call.enqueue(new Callback<Movies>() {
              @Override
              public void onResponse(Call<Movies> call, retrofit2.Response<Movies> response) {
                  List<Movie> returnList = response.body().getResults();
                  topRatedMovies.clear();
                  topRatedMovies.addAll(returnList);
                  helpers.fixDates(topRatedMovies);
                  BottomNavigationView bottNav = (BottomNavigationView) findViewById(R.id.bottom_nav);
                  bottNav.getMenu().findItem(R.id.action_top_rated).setEnabled(true);
              }

              @Override
              public void onFailure(Call<Movies> call, Throwable t) {
                  Log.e(logTag, getString(R.string.fetch_toprated_failure));
                  t.printStackTrace();
              }
          });
          dataLoaded = true;
      }
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    outState.putInt(displayModeStr, displayMode);
    outState.putParcelableArrayList(popularStr, popularMovies);
    outState.putParcelableArrayList(topRatedStr, topRatedMovies);
    scrollState = gridView.onSaveInstanceState();
    outState.putParcelable(scrollPosStr, scrollState);
    super.onSaveInstanceState(outState);
  }

  public class connectBroadcastReceiver extends BroadcastReceiver {

      @Override
      public void onReceive(Context context, Intent intent) {
          /*

            if we are notified about a connectivity change, see if the
            data has been loaded yet, and if not and we are on the network
            grab the data while you can!

           */
          if (!dataLoaded) {
              if (helpers.isOnline(context)) {
                  loadData();
              }
          }
      }
  }
}