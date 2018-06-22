package com.pfisterfarm.popularmovies;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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

import com.pfisterfarm.popularmovies.models.FavDatabase;
import com.pfisterfarm.popularmovies.models.FavsViewModel;
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
  private static final int FAVORITES = 2;

  static ArrayList<Movie> popularMovies;
  static ArrayList<Movie> topRatedMovies;
  static ArrayList<Movie> favMovies;

  tmdbInterface tmdbService = tmdbClient.getClient().create(tmdbInterface.class);

  MovieAdapter popularMovieAdapter, topRatedMovieAdapter, favMovieAdapter;
  GridView gridView;
  Parcelable scrollState;
  public IntentFilter connectIntent;
  public connectBroadcastReceiver connectivityReceiver;

  private FavDatabase mDb;

  static int displayMode;
  static boolean dataLoaded = false;

  private static final String displayModeStr = "displayMode";
  private static final String popularStr = "popular";
  private static final String topRatedStr = "top_rated";
  private static final String scrollPosStr = "scroll_pos";
  private static final String logTag = "POPMOVIES";
  private static final String movieIdStr = "movie_id";
  private static final String favoritesStr = "favs";
  private static final String dataLoadedStr = "dataLoaded";


  @Override
  protected void onCreate(Bundle savedInstanceState) {

    final String MOVIE_KEY = "movie";

    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    gridView = (GridView) findViewById(R.id.movieGrid);

    mDb = FavDatabase.getInstance(getApplicationContext());

    BottomNavigationView bottNav = (BottomNavigationView) findViewById(R.id.bottom_nav);

    // get a broadcast receiver ready to check if data needs to be loaded when
      // network has just become available
    connectIntent = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
    connectivityReceiver = new connectBroadcastReceiver();

     try {
      if ((savedInstanceState == null) ||
              !(((savedInstanceState.containsKey(displayModeStr)) &&
                      (savedInstanceState.containsKey(popularStr)) &&
                      (savedInstanceState.containsKey(topRatedStr))))) {
          // I encountered a strange bug. Once the app loaded and you pressed the back
          // button to return to the launcher screen, then launched the app again,
          // onCreate got called again, savedInstanceState was null, so the app thought
          // it was the first time through and created new ArrayLists, even though all the
          // data was already there which wiped them all out. The data was deleted, but the
          // boolean to check if the data was already loaded was 'true', so no data in the app
          // and no way it was going to be able to load it. The Popular and Top Rated tabs
          // were blank (no crashes though!) and Favorites appeared as normal. It didn't
          // seem to do this if you hit the home button and relaunched.
          if (popularMovies == null) {  // if popularMovies already has data, don't wipe it
              popularMovies = new ArrayList<Movie>();
              displayMode = POPULAR;    // if savedInstanceState and popularMoves are both null
                                        // it's probably safe to assume this is the very first
                                        // time through
          } else {  // enable the button at the bottom instead
              bottNav.getMenu().findItem(R.id.action_popular).setEnabled(true);
          }
          if (topRatedMovies == null) {    // same situation for top rated as for popular movies
              topRatedMovies = new ArrayList<Movie>();
          } else {
              bottNav.getMenu().findItem(R.id.action_top_rated).setEnabled(true);
          }
          if (favMovies == null) {
              favMovies = new ArrayList<Movie>();
          }

          if (helpers.isOnline(getApplicationContext())) {
                loadData();
          } else {
                if (!dataLoaded) {
                    helpers.showOKAlertDialog(this, R.string.no_network_title, R.string.no_network_text);
                }
          }
      } else {
        displayMode = savedInstanceState.getInt(displayModeStr);
        popularMovies = savedInstanceState.getParcelableArrayList(popularStr);
        topRatedMovies = savedInstanceState.getParcelableArrayList(topRatedStr);
        favMovies = savedInstanceState.getParcelableArrayList(favoritesStr);
        scrollState = savedInstanceState.getParcelable(scrollPosStr);
        dataLoaded = savedInstanceState.getBoolean(dataLoadedStr);
        gridView.onRestoreInstanceState(scrollState);
        if (popularMovies.size() > 0 && topRatedMovies.size() > 0) {
            bottNav.getMenu().findItem(R.id.action_popular).setEnabled(true);
            bottNav.getMenu().findItem(R.id.action_top_rated).setEnabled(true);
        }
      }

      popularMovieAdapter = new MovieAdapter(this, popularMovies);
      topRatedMovieAdapter = new MovieAdapter(this, topRatedMovies);
      favMovieAdapter = new MovieAdapter(this, favMovies);


      gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                          @Override
                                          public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                              Intent detailIntent = new Intent(MainActivity.this, DetailActivity.class);
                                              long movieId = 0;
                                              switch (displayMode) {
                                                  case POPULAR:
                                                      detailIntent.putExtra(MOVIE_KEY, popularMovies.get(i));
                                                      movieId = popularMovies.get(i).getId();
                                                      break;
                                                  case TOPRATED:
                                                      detailIntent.putExtra(MOVIE_KEY, topRatedMovies.get(i));
                                                      movieId = topRatedMovies.get(i).getId();
                                                      break;
                                                  case FAVORITES:
                                                      detailIntent.putExtra(MOVIE_KEY, favMovies.get(i));
                                                      movieId = favMovies.get(i).getId();
                                                      break;
                                              }
                                              detailIntent.putExtra(movieIdStr, movieId);
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
                setTitle(getString(R.string.popularMoviesTitle));
              }
              break;
            case R.id.action_top_rated:
              if (displayMode != TOPRATED) {
                displayMode = TOPRATED;
                gridView.setAdapter(topRatedMovieAdapter);
                gridView.invalidate();
                setTitle(getString(R.string.topRatedMoviesTitle));
              }
              break;
            case R.id.action_favorite:
                if (displayMode != FAVORITES) {
                    displayMode = FAVORITES;
                    gridView.setAdapter(favMovieAdapter);
                    gridView.invalidate();
                    setTitle(getString(R.string.favoriteMoviesTitle));
                }
              break;
          }
          return true;
        }
      });

      retrieveFavorites();

      switch (displayMode) {
          case POPULAR:
              setTitle(getString(R.string.popularMoviesTitle));
              gridView.setAdapter(popularMovieAdapter);
              bottNav.getMenu().findItem(R.id.action_popular).setChecked(true);
              break;

          case TOPRATED:
                 setTitle(getString(R.string.topRatedMoviesTitle));
                 gridView.setAdapter(topRatedMovieAdapter);
                 bottNav.getMenu().findItem(R.id.action_top_rated).setChecked(true);
                 break;

           case FAVORITES:
                 setTitle(getString(R.string.favoriteMoviesTitle));
                 gridView.setAdapter(favMovieAdapter);
                 bottNav.getMenu().findItem(R.id.action_favorite).setChecked(true);
                 break;

         }
         gridView.invalidate();    // force update on initial load


     } catch (Exception e) {
      e.printStackTrace();
    }
    }

    private void retrieveFavorites() {

      FavsViewModel viewModel = ViewModelProviders.of(this).get(FavsViewModel.class);
      viewModel.getFavorites().observe(this, new Observer<List<Movie>>() {

          @Override
          public void onChanged(@Nullable List<Movie> movies) {
              favMovies.clear();
              favMovies.addAll(movies);
              if (displayMode == FAVORITES) {
                  gridView.setAdapter(favMovieAdapter);
              }
          }
      });
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
    outState.putBoolean(dataLoadedStr, dataLoaded);
    outState.putParcelableArrayList(popularStr, popularMovies);
    outState.putParcelableArrayList(topRatedStr, topRatedMovies);
    outState.putParcelableArrayList(favoritesStr, favMovies);
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