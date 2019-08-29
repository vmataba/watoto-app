package com.watotoappgmail.watotoapp2;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import configuration.app.ThisApp;
import firebase.Database;
import local.UserDetails;
import user.Account;
import user.InsuffientAmountException;

public class BundleActivity extends AppCompatActivity {

    /***Useful variables referencing UI Components
     * Definied in the Layout File***/
    private Spinner bundle_spinner;
    private TextView bundle_price_text_view;
    private TextView user_account_balance_text_view;
    private Button bundle_buy_btn;
    /***For User Details***/
    private UserDetails user_details;
    /***For List of Bundles***/
    ArrayList<configuration.subscription.Bundle> bundle_list;
    /**Bundle Id for Selected Bundle**/
    private String selected_bundle_id;


    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bundle);
        /***
         * Working on Tool bar, Customizing it and Displaying
         * Appropriate Messages..
         * ***/
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getString(R.string.bundle_toolbar_message));
        }
        //Initlize varibales
        init();
        //Populate Bundle Spinner
        prepareBundles();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
/**
 * Handling Menu Selection Options ...
 * ****/
        int item_id = item.getItemId();
        switch (item_id) {
            case android.R.id.home://Call HomeActivity
                onBackPressed();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    /**Initializes the global variables**/
    private void init () {
        /***For Screen Widgets***/
        bundle_spinner = (Spinner) findViewById(R.id.bundle_type_spinner_id);
        bundle_price_text_view = (TextView) findViewById(R.id.bundle_price_text_view_id);
        bundle_buy_btn = (Button) findViewById(R.id.bundle_buy_btn_id);
        user_account_balance_text_view = (TextView)findViewById(R.id.bundle_user_account_balcnce_text_view_id);
        bundle_buy_btn = (Button)findViewById(R.id.bundle_buy_btn_id);
        /**Bundle List**/
        bundle_list = new ArrayList<configuration.subscription.Bundle>();
        /***For User Details***/
        user_details = new UserDetails(this);
        //Display User Account Balance as from Firebase
        Database database = new Database(this);
        //Get user_id
        final String user_id = user_details.getUserAccount().getUser().getUser_id();
        database.getUserReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String acc_balance = dataSnapshot.child(user_id).child(Database.USER_ACC_BALANCE).getValue().toString();
                user_account_balance_text_view.setText(Account.format(Double.parseDouble(acc_balance)));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            Toast.makeText(BundleActivity.this,databaseError.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
        //Attempts to Subscription (Button Click Event)
        bundle_buy_btn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("StaticFieldLeak")
            @Override
            public void onClick(final View v) {
                if (isValid()) {
                    //Valid Bundle has been selected Apply Subscription Logic
                    if (canBuy()){
                        //Apply Bundle Buying Logic
                           new AsyncTask<String, String, String>() {
                               //Progress Dialog
                               ProgressDialog dialog;
                               @Override
                               protected void onPreExecute() {
                                   dialog = new ProgressDialog(BundleActivity.this);
                                   dialog.setMessage("Please wait ...");
                                   dialog.show();
                                   super.onPreExecute();
                               }

                               @Override
                            protected String doInBackground(String... strings) {
                                //Get url
                                String url = strings[0];
                                //Get user_id
                                UserDetails details = new UserDetails(BundleActivity.this);
                                String user_id = details.getUserAccount().getUser().getUser_id();
                                //Get bundle_id
                                String bundle_id = selected_bundle_id;
                                //Attach parameters to url
                                url += "&userid=" + user_id;
                                url += "&kifurushi_id="+bundle_id;
                                //Post url
                                return new ThisApp().post(3000,3000,url,"");
                            }

                             @Override
                             protected void onPostExecute(String s) {
                                 //Close Dialog
                                 dialog.dismiss();
                                 if (s.contains("Subscription is Succesful")) {
                                     //Toast.makeText(BundleActivity.this,Account.STATE_SUBSCRIBED,Toast.LENGTH_LONG).show();
                                     showMessage(v,Account.STATE_SUBSCRIBED);
                                     updateUserAccount();
                                     //Go back To Home Screen
                                     onBackPressed();
                                 } else {
                                     //Toast.makeText(BundleActivity.this,"Network Error",Toast.LENGTH_LONG).show();
                                     showMessage(v,"Network Error");
                                 }
                                 super.onPostExecute(s);
                             }
                         }.execute(ThisApp.ACC_SUBSCRIPTION_URL);
                    } else {
                        //Insuffiecient balance
                       // Toast.makeText(BundleActivity.this,"Can not buy",Toast.LENGTH_SHORT).show();
                        showMessage(v,"INSUFFICIENT BALANCE");
                    }

                } else {
                    //No Bundle has been selected, display Error Message
                    //Toast.makeText(BundleActivity.this,"Nothing has been chosen",Toast.LENGTH_SHORT).show();
                    showMessage(v,"TAP TO CHOOSE");
                }
            }
        });

    }
    /**Checks If Customer has set everything needed to buy a bundle**/
    private boolean isValid () {
        return bundle_spinner.getSelectedItemPosition() != 0;
    }
    /**Checks if User Can buy selected Bundle***/
    private boolean canBuy () {
        //Get price of selected bundle
        double selected_price = Double.parseDouble(bundle_price_text_view.getText().toString().replace(",","").replace("/=",""));
        //Get User account balance
        double user_acc_balance = Double.parseDouble(user_account_balance_text_view.getText().toString().replace(",","").replace("/=",""));
        return user_acc_balance >= selected_price;
    }
    /***Populates Bundle Spinner****/
    private void prepareBundles () {
        //This Process runs in background
        @SuppressLint("StaticFieldLeak") AsyncTask<String,String,String> bundle_async = new AsyncTask<String, String, String>() {

            ProgressDialog dialog;
            Database database;

            @Override
            protected void onPreExecute() {
                //Progress Dialog
                dialog = new ProgressDialog(BundleActivity.this);
                dialog.setMessage("Loading ...");
                dialog.show();
                //Bundles List
                bundle_list = new ArrayList<configuration.subscription.Bundle>();
                //Firebase Database
                database = new Database(BundleActivity.this);
            }

            @Override
            protected String doInBackground(String... strings) {

                database.getBundleReference().addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Iterable<DataSnapshot> snapshots = dataSnapshot.getChildren();
                        for (DataSnapshot snapshot:snapshots){
                            //Create bundle Instance
                            configuration.subscription.Bundle bundle = new configuration.subscription.Bundle();
                            bundle.setBundle_id(snapshot.getKey());
//                            Toast.makeText(BundleActivity.this,snapshot.child("name").toString(),Toast.LENGTH_LONG).show();
                            bundle.setBundle_name(snapshot.child(Database.BUNDLE_NAME).getValue().toString());
                            bundle.setBundle_desc(snapshot.child(Database.BUNDLE_DESC).getValue().toString());
                            bundle.setBundle_price(Double.parseDouble(snapshot.child(Database.BUNDLE_PRICE).getValue().toString()));
                            bundle.setNum_days(Integer.parseInt(snapshot.child(Database.BUNDLE_NUM_DAYS).getValue().toString()));
                            //Add bundle in the list
                            bundle_list.add(bundle);
                        }
                        if (bundle_list.size() > 0) {
                            //Close Dialog
                            dialog.dismiss();
                            //Stores Bundle names
                            final ArrayList<String> bundle_names = new ArrayList<String>();
                            //Prompt Message
                            bundle_names.add("--Tap to choose--");
                            final ArrayList<String> bundle_prices = new ArrayList<String>();
                            //Price value if no bundle is selected
                            bundle_prices.add("0/=");
                            for (configuration.subscription.Bundle bundle:bundle_list) {
                                bundle_names.add(bundle.getBundle_name());
                                bundle_prices.add(Account.format(bundle.getBundle_price()));
                            }
                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(BundleActivity.this,android.R.layout.simple_list_item_1,bundle_names);
                            bundle_spinner.setAdapter(adapter);
                            //Handling Click Events
                            bundle_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    //Show price
                                    bundle_price_text_view.setText(bundle_prices.get(position));
                                    if (position != 0){
                                        int pos = position - 1;
                                        //Toast.makeText(BundleActivity.this,bundle_list.get(pos).getBundle_id(),Toast.LENGTH_LONG).show();
                                        selected_bundle_id = bundle_list.get(pos).getBundle_id();
                                    }

                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {

                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(BundleActivity.this,databaseError.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });

                return null;
            }

        }.execute("");
    }
    //Updates Local User Account Details From Firebase
    private void updateUserAccount () {
        //Create Database Instance
        Database database = new Database(this);
        //Get User Id
        final String user_id = user_details.getUserAccount().getUser().getUser_id();
        //Capture Firebase Changes
        database.getUserReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Get Reference to Current User
                DataSnapshot user_snap = dataSnapshot.child(user_id);
                //Get Account Balance
                String balance = user_snap.child(Database.USER_ACC_BALANCE).getValue().toString();
                //Get Bundle Type
                String bundle_type = user_snap.child(Database.USER_BUNDLE_TYPE).getValue().toString();
                //Get Expire date
                String exp_date = user_snap.child(Database.USER_ACC_SUB_EXP_DATE).getValue().toString();
                //Create user Account that reflects the local Account
                Account user_account = user_details.getUserAccount();
                //Modify account balance
                user_account.setBalance(Double.parseDouble(balance));
                //Modify Bundle Type
                user_account.setBundle_type(bundle_type);
                //Modify Expire date
                user_account.setExpire_date(exp_date);
                //Update Local Details
                user_details.updateUserAccount(user_account);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    //Displays message using SnackBar
    public void showMessage (View view, String message){
        Snackbar.make(view, message, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

}
