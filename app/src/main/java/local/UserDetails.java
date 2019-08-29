package local;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import java.io.Serializable;
import java.util.HashMap;

import configuration.subscription.Bundle;
import user.Account;
import user.User;

/**
 * Created by victor on 11/1/2017.
 */

public class UserDetails {

    //Key pointing to Current User Account **Public and Class Variable**
    public static final String PREF_USER_ACCOUNT = "user_account";
    public static final String PREF_USER = "user";

    //Other Class Variables
    private String user_account_json_string;
    private String user_json_string;

    //For Class Usages
    private SharedPreferences user_preferences;
    private SharedPreferences.Editor user_pref_editor;
    /*For Activity's Context*/
    private Context context;
    public UserDetails (Context context){
        this.context = context;
        this.user_preferences = getContext().getSharedPreferences(PREF_USER_ACCOUNT,0);
        this.user_pref_editor = getUser_preferences().edit();
        this.user_account_json_string = null;
        this.user_json_string = null;
    }
    public Context getContext () {
        return this.context;
    }
    //Retrieves user_preferences
    public SharedPreferences getUser_preferences (){
        return this.user_preferences;
    }
    //Retrieves editor
    public SharedPreferences.Editor getUser_pref_editor () {
        return this.user_pref_editor;
    }
    /*Stores details of a particular User Account*/
    public void storeUserAccount (Account user_account) {
        SharedPreferences.Editor editor = getUser_pref_editor();
        //Convert user_account into json string
        Gson gson = new Gson();
        //Get Account Details
        HashMap<String,String> params = user_account.getAccountDetails();
        user_account_json_string = gson.toJson(params);
        user_json_string = gson.toJson(user_account.getUser());
        //Save the Strings
        editor.putString(PREF_USER_ACCOUNT,user_account_json_string);
        editor.putString(PREF_USER,user_json_string);
        //Save changes
        editor.commit();
    }
    /*Retrieves User Account*/
    public Account getUserAccount () {
        String saved_account = getUser_preferences().getString(PREF_USER_ACCOUNT,"");
        String saved_user = getUser_preferences().getString(PREF_USER,"");
        Gson gson = new Gson();
        HashMap<String,String>  params = gson.fromJson(saved_account,HashMap.class);
        User user = gson.fromJson(saved_user,User.class);
        Account account = new Account();
        account.setDetails(params);
        account.setUser(user);
        return account;
    }
    /*Updates UserAccount Details*/
    public void updateUserAccount (Account account){
        this.storeUserAccount(account);
    }
    //Checks if User details exist in shared Preferences
    public boolean isEmpty () {
        String saved_account = getUser_preferences().getString(PREF_USER_ACCOUNT,"");
        Gson gson = new Gson();
        HashMap<String,String>  params = gson.fromJson(saved_account,HashMap.class);
        return params == null;
    }
}
