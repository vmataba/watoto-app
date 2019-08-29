package configuration.app;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.icu.util.GregorianCalendar;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.support.annotation.VisibleForTesting;

import com.watotoappgmail.watotoapp2.BuildConfig;
import com.watotoappgmail.watotoapp2.BundleActivity;
import com.watotoappgmail.watotoapp2.HomeActivity;
import com.watotoappgmail.watotoapp2.WelcomeActivity;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import java.util.Calendar;
import java.util.HashMap;

import configuration.AppBroadCastReceiver;
import firebase.Database;
import local.UserDetails;
import user.Account;
import user.User;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by victor on 10/29/2017.
 */

public class ThisApp extends Application {
    //For Internet Connection checking
    private static ThisApp mInstance;

    //Message displayed when background process has failed
    public static final String FAILED_PROCESS_MESSAGE = "Failed";

    //This is a user registration url, that will be populated with needed user details as Paramters
    public static final String USER_REG_URL = "https://www.watotoapp.com/ms/index.php?r=registrationApi/submit";
    //This is a story View url It's called whenever User Likes a story. it goes with story_id alongside user_id
    public static  final  String STORY_VIEW_URL = "https://watotoapp.com/ms/index.php?r=userViewApi/submit";
    //This is a story view url for non registered users, it only goes with a story id
    public static final String STORY_VIEW_UNREG_URL = "https://www.watotoapp.com/ms/?r=unregisteredUserViewApi/submit";
    //Story Likes and Dislikes url
    public static final String STORY_LIKE_URL = "https://watotoapp.com/ms/index.php?r=userLikeApi/submit";
    //Subscription Url
    public static final String ACC_SUBSCRIPTION_URL = "https://watotoapp.com/ms/index.php?r=subscriptionApi/submit";
    //For checking Expire date
    public static final String ACC_SUBSCRIBTION_EXP_DATE_CHECK_URL = "https://www.watotoapp.com/ms/?r=subscriptionStatus/submit";
    //For checking Subscription status
    public static final String ACC_SUBSCRIPTION_CHECK_URL = "https://www.watotoapp.com/ms/?r=subscriptionStatus/submit";
    /***Other Important Constants that are Used to Check, Application Launching
     * If It is the first time or upgrade time***/
    private static final String PREFS_NAME = "watoto_app_user_level_preference_id";
    private static final String PREF_VERSION_CODE_KEY = "version_code";
    private static final int DOESNT_EXIST = -1;

    public void registerReceiver (Context context) {
        //Create AppBroadCastReceiver Instance
        BroadcastReceiver broadcastReceiver = new AppBroadCastReceiver();
        //IntentFilter Object
        IntentFilter  filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        //filter.addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED);
        context.registerReceiver(broadcastReceiver,filter);
    }
    //Sends parameters Via Url
    public String post(int timeoutConnection, int timeoutSocket, String url, String xml) {
        String finalResponse = FAILED_PROCESS_MESSAGE;
        try {

            HttpParams httpParameters = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
            HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
            HttpClient httpclient = new DefaultHttpClient(httpParameters);
            HttpPost httppost = new HttpPost(url);
            StringEntity entity;
            entity = new StringEntity(xml, "UTF-8");
            httppost.setEntity(entity);
            httppost.addHeader("Accept", "text/xml");
            httppost.addHeader("Content-Type", "text/xml");
            HttpResponse response = httpclient.execute(httppost);
            finalResponse = EntityUtils.toString(response.getEntity());
            return finalResponse;
        } catch (Exception e) {
            e.printStackTrace();
           return e.getMessage();
           // return FAILED_PROCESS_MESSAGE;

        }
    }

    public boolean registerUser (final HashMap<String,String> details, final Context context) {
         boolean final_result = false;
        //Perform registration in background
        @SuppressLint("StaticFieldLeak") AsyncTask regAsync = new AsyncTask<String,String,String>() {
            @Override
            protected String doInBackground(String... strings) {
                String url = strings[0];
                url+= "&_fullname="+details.get("full_name");
                url+= "&_mobilenumber="+details.get("mobile_number");
                url+= "&_email="+details.get("email");
                url+= "&_country="+details.get("country");
                url+= "&_password="+details.get("password");
                url+= "&_gender="+details.get("gender");
                url+= "&_birthdate="+details.get("birth_date");
                if (!post(2000,2000,url,"").equals("FAIL")){
                    //dialog.dismiss();
                    result = true;

                } else {
                    // dialog.dismiss();
                }
                return result+"";
            }


            private ProgressDialog dialog;
            private boolean result = false;
        };
        regAsync.execute("https://watotoapp.com/ms/index.php?r=registrationApi/submit");
        return false;
    }
    //Used to generate hashed password
    public static String getMd5(String md5) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] array = md.digest(md5.getBytes());
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < array.length; ++i) {
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1,3));
            }
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
        }
        return null;
    }

    /***Checks if this Application is run for the first time***/
    public static void checkFirstRun(Context context, Account user_account) {
        final String PREFS_NAME = "MyPrefsFile";
        final String PREF_VERSION_CODE_KEY = "version_code";
        final int DOESNT_EXIST = -1;
        // Get current version code
        int currentVersionCode = BuildConfig.VERSION_CODE;

        // Get saved version code
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        int savedVersionCode = prefs.getInt(PREF_VERSION_CODE_KEY, DOESNT_EXIST);

        // Check for first run or upgrade
        if (currentVersionCode == savedVersionCode) {
            // This is just a normal run
            Intent intent = new Intent(context,HomeActivity.class);
            context.startActivity(intent);
            ((Activity)context).finish();
            return;

        } else if (savedVersionCode == DOESNT_EXIST) {
            // TODO This is a new install (or the user cleared the shared preferences)
            // Or No User Account has been set
            //Intent intent = new Intent(context, HomeActivity.class);
            //context.startActivity(intent);
            //((Activity)context).finish();

        } else if (currentVersionCode > savedVersionCode) {

            // TODO This is an upgrade
        }

        // Update the shared preferences with the current version code
        prefs.edit().putInt(PREF_VERSION_CODE_KEY, currentVersionCode).apply();
    }
    //Only returns true If It is first run, and false otherwise
    public static boolean isFirstRun (Context context) {
        // Get current version code
        int currentVersionCode = BuildConfig.VERSION_CODE;
        // Get saved version code
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        int savedVersionCode = prefs.getInt(PREF_VERSION_CODE_KEY, DOESNT_EXIST);

        return savedVersionCode == DOESNT_EXIST;
    }

    //Returns Current Version of an application
    public static String getCurrentAppVersion () {
        return BuildConfig.VERSION_NAME;
    }

    //Retrieves details of an application
    public static HashMap<String,String> getAppDetails () {
        HashMap<String,String> params = new HashMap<>();
        params.put("app_version",getCurrentAppVersion());
        params.put("app_owner","Lebena Kids Company");
        params.put("app_owner_website","www.watotoapp.com");
        params.put("app_developer","Victor Mataba");
        params.put("app_developer_email","vmataba0@gmail.com");
        params.put("current_year",Calendar.getInstance().get(Calendar.YEAR)+"");
        return params;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }
    public static synchronized ThisApp getInstance() {
        return mInstance;
    }

    public void setConnectivityListener(AppBroadCastReceiver.ConnectivityReceiverListener listener) {
        AppBroadCastReceiver.connectivityReceiverListener = listener;
    }
}
