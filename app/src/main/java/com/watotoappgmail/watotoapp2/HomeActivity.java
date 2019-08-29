package com.watotoappgmail.watotoapp2;

import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.SearchView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import org.apache.http.impl.client.DefaultTargetAuthenticationHandler;

import java.util.ArrayList;
import java.util.IllegalFormatCodePointException;

import configuration.AppBroadCastReceiver;
import configuration.app.ThisApp;
import firebase.Database;
import home.AllStories;
import home.PopularStories;
import home.Searcher;
import local.UserDetails;
import story.Story;
import story.StoryAdapter;
import story.StoryItemAdapter;
import user.Account;
import user.User;
import welcome.Login;
import welcome.Registration;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    /*For This Activity*/
    private HomeActivity.SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private TabLayout tabLayout;
    private SearchManager searchManager;
    private SearchView searchView;
    private View nav_header;
    /*For User Account*/
    private User user;
    private Account user_account;
    private TextView user_account_name;
    private TextView user_account_number;
    private TextView user_account_balance;
    private TextView user_account_sub_exp_date;
    private TextView user_account_bundle_type;
    private Button user_account_sub_status;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.home_title);
        setSupportActionBar(toolbar);


        mSectionsPagerAdapter = new HomeActivity.SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        tabLayout = (TabLayout) findViewById(R.id.home_tabs_id);
        tabLayout.setupWithViewPager(mViewPager);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();


        init();

        //Update UserAccount Whenever Data changes, only for Registered users
        if (User.isLoggedIn(this)){
            Database database = new Database(this);
            UserDetails details = new UserDetails(this);
            //Get user_id
            String user_id = details.getUserAccount().getUser().getUser_id();
            database.getUserReference().child(user_id).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    init();
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    //No Implementation
                }
            });
        }

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        init();
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        finish();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.home, menu);

        searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.menu_search_id).getActionView();
        searchView.setIconifiedByDefault(false);//Do not expand the View
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        /*All logic about searching for a story*/
        Searcher searcher = new Searcher(this, searchView);
        searcher.setAll_adapter(AllStories.itemAdapter);
        searcher.setAll_no_stories(AllStories.no_stories_found);
        searcher.setPopular_adapter(PopularStories.itemAdapter);
        searcher.setPopular_no_stories(PopularStories.no_stories_text_view);
        searcher.search();


        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Toast.makeText(this, "Development is still in progress", Toast.LENGTH_SHORT).show();
        // return super.onOptionsItemSelected(item);
        return true;
    }

    @Override
    protected void onResume() {
        invalidateOptionsMenu();
        //init();
        super.onResume();

    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id){
            case R.id.home_nav_subscribe:
                //Deals with Bundle subscription
                Intent intent_subscribe = new Intent(this, BundleActivity.class);
                startActivity(intent_subscribe);
                break;
            case R.id.home_nav_about:
                //Deals with Explanations about WatotoApp
                Intent intent_about = new Intent(HomeActivity.this,AboutActivity.class);
                startActivity(intent_about);
                break;
                case R.id.home_nav_contact:
                    //Deals with Displaying contact addresses to the app user
                    Intent intent_contact = new Intent(HomeActivity.this,ContactActivity.class);
                    startActivity(intent_contact);
                    break;
                    case R.id.home_nav_faq:
                    case R.id.home_nav_recharge:
                        //Deals with Displaying List of Frequently Asked Questions and Answers to the user
                        Intent intent_faq = new Intent(HomeActivity.this,FaqActivity.class);
                        startActivity(intent_faq);
                        break;
                    default:
                        //Other features have not been implemented, display a message instead
                        Toast.makeText(this, "Development is still in progress", Toast.LENGTH_SHORT).show();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        private int page_id = 0;

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 1:
                    PopularStories popularStories = new PopularStories();
                    return popularStories;
                case 0:
                    AllStories allStories = new AllStories();
                    page_id = 1;
                    return allStories;
            }
            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 1:
                    return "POPULAR";
                case 0:
                    return "ALL";
            }
            return null;
        }

        //Returns Current page's id
        public int getPageId() {
            return this.page_id;
        }
    }

    //Does component Initialization
    @SuppressLint("SetTextI18n")
    public void init() {

        /*For User Account*/
        //user_account = (Account) getIntent().getSerializableExtra("user_account");
        // user = user_account.getUser();
        UserDetails details = new UserDetails(HomeActivity.this);
        user_account = details.getUserAccount();
        user = user_account.getUser();



       /* FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Initialize nav_header
        nav_header = navigationView.getHeaderView(0);
        //Set Values for an Account
        user_account_name = (TextView) nav_header.findViewById(R.id.user_account_name_id);
        user_account_number = (TextView) nav_header.findViewById(R.id.user_account_number_id);
        user_account_balance = (TextView) nav_header.findViewById(R.id.user_account_balance_id);
        user_account_sub_exp_date = (TextView) nav_header.findViewById(R.id.user_account_expire_date_id);
        user_account_sub_status = (Button) nav_header.findViewById(R.id.user_account_subscription_id);
        user_account_bundle_type = (TextView) nav_header.findViewById(R.id.user_account_bundle_type_id);
        /*Done Initializing ... */
        user_account_name.setText(user.getName());
        user_account_number.setText("Account Number: " + user_account.getAccount_number());
        user_account_balance.setText("Account Balance: " + Account.format(user_account.getBalance()));
        String sub_status = user_account.getBundle_type().equals(Account.STATE_UNSUBSCRIBED) ? Account.STATE_UNSUBSCRIBED : Account.STATE_SUBSCRIBED;
        user_account_sub_status.setText(sub_status);
        //For subscribed account
        if (sub_status.equals(Account.STATE_SUBSCRIBED)) {
            //Change subscription status view background color into Green
            user_account_sub_status.setBackgroundColor(getResources().getColor(R.color.green));
            //View Expire date view
            user_account_sub_exp_date.setVisibility(View.VISIBLE);
            //Set formated date
            user_account_sub_exp_date.setText("Expire date: "+Database.formatDate(user_account.getExpire_date()));
            //View Bundle type
            user_account_bundle_type.setVisibility(View.VISIBLE);
            //Display Bundle type
            user_account_bundle_type.setText("Kifurushi Type: " + user_account.getBundle_type());
        }
        //Check Epire Date
        checkEpireDate();
        //Update Local Details over User Account
        updateUserAccount();
    }

    //Updates Stored User Account Details From Firebase
    private void updateUserAccount() {
        final UserDetails details = new UserDetails(HomeActivity.this);
        //Get Stored User Account
        final Account user_account = details.getUserAccount();
        //Read Updates from Firebase
        final Database database = new Database(HomeActivity.this);
        DatabaseReference user_ref = database.getUserReference().child(user_account.getUser().getUser_id());
        user_ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Read Account Balance
                String account_balance = dataSnapshot.child(Database.USER_ACC_BALANCE).getValue().toString();
                //Read Expire date
                String expire_date = dataSnapshot.child(Database.USER_ACC_SUB_EXP_DATE).getValue().toString();
                //Read Bundle Type
                String bundle_type = dataSnapshot.child(Database.USER_BUNDLE_TYPE).getValue().toString();
                //Attach new Values to the Account Object
                user_account.setBalance(Double.parseDouble(account_balance));
                //Attach Expire date
                user_account.setExpire_date(expire_date);
                //Attach Bundle Type
                user_account.setBundle_type(bundle_type);
                //Update Local User Account
                details.updateUserAccount(user_account);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //No Implementation
            }
        });
    }
    //Cheks Expire date and, Sends Information to the Server along with
    //user id
    @SuppressLint("StaticFieldLeak")
    private void checkEpireDate (){
        new AsyncTask<String, String, String>() {

            @Override
            protected String doInBackground(String... strings) {
                //Store url
                String url = strings[0];
                //Get user_id
                String user_id = new UserDetails(HomeActivity.this).getUserAccount().getUser().getUser_id();
                //Attach user_id to the url
                url += "&userid="+user_id;
                //Make request
                return new ThisApp().post(3000,3000,url,"");
            }

            @Override
            protected void onPostExecute(String s) {
                //Toast.makeText(HomeActivity.this,s,Toast.LENGTH_LONG).show();
                super.onPostExecute(s);
            }
        }.execute(ThisApp.ACC_SUBSCRIBTION_EXP_DATE_CHECK_URL);
    }
}
