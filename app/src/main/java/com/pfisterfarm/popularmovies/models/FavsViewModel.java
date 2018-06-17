package com.pfisterfarm.popularmovies.models;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import java.util.List;

public class FavsViewModel extends AndroidViewModel {

    private LiveData<List<Movie>> favoriteMovies;

    public FavsViewModel(@NonNull Application application) {
        super(application);
        FavDatabase dbase = FavDatabase.getInstance(this.getApplication());
        favoriteMovies = dbase.movieDao().loadAllFavorites();
    }

    public LiveData<List<Movie>> getFavorites() {
        return favoriteMovies;
    }
}
