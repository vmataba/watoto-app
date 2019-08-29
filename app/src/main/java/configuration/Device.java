package configuration;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.drawable.GradientDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by victor on 10/13/2017.
 */

public class Device {

    /*For number of Grid columns*/
    public static final int NUM_COL_FOR_TABLET = 2;
    public static final int NUM_COL_FOR_PHONE = 1;
    public static final int NUM_COL_FOR_TAB_LAND_SCAPE = 3;
    public static final int NUM_COL_FOR_PHONE_LAND_SCAPE = 2;

    //For current context
    private Context context;
    //Default Constructor
    public Device () {}
    //Constructor with Context
    public Device (Context context){
        this.context = context;
    }
    //Returns current context
    public Context getContext () {
        return this.context;
    }


    public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }
    //Returns true if device has internet Connection
    public boolean hasInternet () {
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }

    //Checks Screen orientation
    public  boolean isPotrait () {
        return getContext().getResources().getConfiguration().orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
    }


}
