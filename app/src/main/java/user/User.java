package user;

import android.content.Context;

import java.io.Serializable;

import local.UserDetails;

/**
 * Created by victor on 10/9/2017.
 * This Class basically references application user who is
 * assumed to be a parent and can have a number of children.
 */

public class User implements Serializable {

    public static final String DEFAULT_COUNTRY = "Tanzania";

    private String user_id;
    private String user_name;
    private String password;

    private String name;
    private String phone_number;
    private String email_address;
    private int num_children;
    private String country;

    //Default Constructor
    public User () {
        this.user_id = null;
        this.name = null;
        this.phone_number = null;
        this.user_name = null;
        this.num_children = 0;
        this.email_address = null;
        this.country = null;
    }

    /*If a user has no email address, we assign it null*/
    public User(String user_id,String name, String phone_number, int num_children, String country){

        this.user_id = user_id;
        this.name = name;
        this.phone_number = phone_number;
        this.user_name = this.phone_number;
        this.num_children = num_children;
        this.email_address = null;
        this.country = country;

    }

    /*If User has email address*/
    public User(String user_id,String name, String phone_number, int num_children, String country, String email_address){
        this(user_id,name,phone_number,num_children,country);
        this.email_address = email_address;
    }

    public String getUser_id () {return this.user_id;}

    public String getName(){
        return this.name;
    }

    public String getPhone_number(){
        return this.phone_number;
    }

    public String getUser_name(){
        return this.user_name;
    }

    public String getPassword(){
        return this.password;
    }

    public String getEmail_address(){
        return this.email_address;
    }

    public int getNum_children () {
        return this.num_children;
    }

    public String getCountry(){
        return this.country;
    }

    public void setUser_id (String user_id){this.user_id = user_id;}

    public void setName(String name){
        this.name = name;
    }

    public void setUser_name(){
        this.user_name = this.getPhone_number();
    }

    public void setPhone_number(String phone_number){
        this.phone_number = phone_number;
        setUser_name();
    }

    public void setPassword(String password){
        this.password = password;
    }

    public void setCountry(String country){
        this.country = country;
    }

    public void setDefaultCountry(){
        this.country = DEFAULT_COUNTRY;
    }

    public void setEmail_address(String email_address){
        this.email_address = email_address;
    }

    public void setNum_children (int num_children){
        this.num_children = num_children;
    }


    public boolean hasEmail () {
        return this.email_address != null;
    }


    /***This function checks if user has successfully Logged in and
     * An account has been created. It Uses values stored in sharedPreferences
     * ***/
    public static boolean isLoggedIn (Context context) {
        UserDetails details = new UserDetails(context);
        return !details.isEmpty();
    }

}
