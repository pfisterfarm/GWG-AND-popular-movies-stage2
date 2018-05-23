package com.pfisterfarm.popularmovies.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;
import android.view.MenuItem;

import com.pfisterfarm.popularmovies.models.Movie;
import com.pfisterfarm.popularmovies.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static java.text.DateFormat.getDateInstance;

public class helpers {

    // isOnline() test taken from
    // https://stackoverflow.com/questions/1560788/how-to-check-internet-access-on-android-inetaddress-never-times-out
    public static boolean isOnline(Context context) {
        ConnectivityManager
                cm = (ConnectivityManager) context.getApplicationContext()
                     .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null
                && activeNetwork.isConnectedOrConnecting();
    }

    // showAlertDialog adapted from
    // https://stackoverflow.com/questions/43513919/android-alert-dialog-with-one-two-and-three-buttons
    public static void showOKAlertDialog(Context context, int titleId, int messageId) {

        // setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getString(titleId));
        builder.setMessage(context.getString(messageId));

        // add a button
        builder.setPositiveButton(R.string.OK_text, null);

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public static void fixDates(ArrayList<Movie> movieArray) {
        /*
            fixDates - convert ISO 8601 dates received from tmdb into more friendly format
        */
        SimpleDateFormat dateParser = new SimpleDateFormat("yyyy-MM-dd",
                java.util.Locale.getDefault());

        for (int i=0; i < movieArray.size(); i++) {
            try {
                Date releaseDateObj = dateParser.parse(movieArray.get(i).getReleaseDate());
                movieArray.get(i).setReleaseDate(getDateInstance(DateFormat.MEDIUM).format(releaseDateObj));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
