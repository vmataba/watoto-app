package user;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.app.ProgressDialog;
import android.graphics.ImageFormat;
import android.os.AsyncTask;
import android.widget.Toast;

import com.watotoappgmail.watotoapp2.BundleActivity;

import java.io.Serializable;
import java.security.PrivateKey;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;

import configuration.app.ThisApp;
import local.UserDetails;

/**
 * Created by victor on 10/9/2017.
 * This Class handles all the User account issues
 */

public class Account extends Application implements Serializable {

    public static final boolean ACCOUNT_SUBSCRIBED = true;
    public static final boolean ACCOUNT_UNSUBSCRIBED = false;
    public static final String STATE_SUBSCRIBED = "SUBSCRIBED";
    public static final String STATE_UNSUBSCRIBED = "NOT SUBSCRIBED";

    private String account_number;
    private String account_name;
    private double balance;
    private boolean is_subscribed = false;
    private String expire_date;
    private String bundle_type;
    private User user;

    public Account () {
        this.account_number = null;
        this.balance = 0.0;
        this.is_subscribed = ACCOUNT_UNSUBSCRIBED;
        this.expire_date = null;
        this.user = null;
        this.account_name = null;
        this.bundle_type = null;
    }

    public Account(String account_number, double balance,  User user,String bundle_type){

        this.account_number = account_number;
        this.balance = balance;
        this.is_subscribed = ACCOUNT_UNSUBSCRIBED;
        this.expire_date = null;
        this.user = user;
        this.account_name = this.user.getName();
        this.bundle_type = bundle_type;

    }


/*This function subscribes the user for a specified period (Months),
* with an amount specified, it returns true if the account has be successfully subscribed*/
    public boolean subscribe(double amount, int period){

        try {

            if (amount > this.getBalance())
                throw new InsuffientAmountException();
            else{
                this.setBalance(this.getBalance() - amount);
                this.is_subscribed = ACCOUNT_SUBSCRIBED;
            }
        } catch (InsuffientAmountException exception){
            Toast.makeText(this,exception.getMessage(),Toast.LENGTH_LONG).show();
        }

        return false;
    }

    //Subscribe
    public void setAccountSubscribed () {
        this.is_subscribed = ACCOUNT_SUBSCRIBED;
    }
    //Unsubscribe
    public void setAccountUnsubscribed () {
        this.is_subscribed = ACCOUNT_UNSUBSCRIBED;
    }

    public boolean unsubscribe(){
        if (this.getSubscriptionStatus() == ACCOUNT_SUBSCRIBED){
            this.is_subscribed = ACCOUNT_UNSUBSCRIBED;
            return true;
        }
        return false;
    }

    //Checks if an account is subscribed, it returns true if subscribed
    //otherwise false

    public boolean getSubscriptionStatus () {
        return !this.getBundle_type().equals(Account.STATE_UNSUBSCRIBED);
    }

    public double getBalance(){
        return this.balance;
    }

    public String getAccount_number(){
        return this.account_number;
    }
    public String getAccount_name(){
        return this.account_name;
    }

    public String getExpire_date(){
        return this.expire_date;
    }

    public String getBundle_type () {return this.bundle_type;}

    public User getUser(){
        return this.user;
    }

    public void setBalance(double balance){
        this.balance = balance;
    }

    public void setAccount_number(String account_number){
        this.account_number = account_number;
    }

    public void setAccount_name(String account_name){
        this.account_name = account_name;
    }

    public void setExpire_date(String expire_date){
        this.expire_date = expire_date;
    }

    public void setBundle_type (String bundle_type) {this.bundle_type = bundle_type;}

    public void setUser(User user){
        this.user = user;
    }

    //Retrieves Account Details in a HashMap
    public HashMap<String,String> getAccountDetails () {
        HashMap<String,String> params = new HashMap<String,String>();
        params.put("user_account_number",this.getAccount_number());
        params.put("user_account_name",this.getAccount_name());
        params.put("user_account_balance",this.getBalance()+"");
        params.put("user_account_exp_date",this.getExpire_date());
        params.put("user_account_bundle_type",this.getBundle_type());
        return params;
    }
    //Sets Account Details from HashMap
    public void setDetails (HashMap<String,String> params) {
           this.account_number = params.get("user_account_number");
           this.account_name = params.get("user_account_name");
           this.balance = Double.parseDouble(params.get("user_account_balance"));
           this.expire_date = params.get("user_account_exp_date");
           this.bundle_type = params.get("user_account_bundle_type");
           this.setUser(null);
    }
    //Formats the supplied figure into thousands example 2200 to 2,200/=
    public static String format (double figure){
        return NumberFormat.getNumberInstance(Locale.US).format(figure)+"/=";
    }
}
