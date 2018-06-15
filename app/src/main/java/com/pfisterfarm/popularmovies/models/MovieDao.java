package com.pfisterfarm.popularmovies.models;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface MovieDao {

    @Query("SELECT * FROM favorites ORDER BY title")
    LiveData<List<Movie>> loadAllFavorites();

    @Insert
    void insertFavorite(Movie movie);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateFavorite(Movie movie);

    @Delete
    void deleteFavorite(Movie movie);

}
