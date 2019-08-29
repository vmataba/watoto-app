package com.watotoappgmail.watotoapp2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.HashMap;

import configuration.app.ThisApp;

public class AboutActivity extends AppCompatActivity {

    private TextView app_version;
    private TextView app_owner;
    private TextView app_owner_website;
    private TextView app_developer;
    private TextView app_developer_email;
    private TextView app_license;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        //Set Action bar's title
        getSupportActionBar().setTitle("About WatotoApp");
        //Show back navigation arrow
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        //Display application details
        app_version = (TextView) findViewById(R.id.app_version_id);
        app_owner = (TextView) findViewById(R.id.app_owner_id);
        app_owner_website = (TextView) findViewById(R.id.app_owner_website_id);
        app_developer = (TextView) findViewById(R.id.app_developer_id);
        app_developer_email = (TextView) findViewById(R.id.app_developer_email_id);
        app_license = (TextView) findViewById(R.id.app_license_id);

        //Get application details
        HashMap<String,String> app_deatils = ThisApp.getAppDetails();
        app_version.setText("Current Version "+ app_deatils.get("app_version"));
        app_owner.setText("Powered by: "+app_deatils.get("app_owner"));
        app_owner_website.setText(app_deatils.get("app_owner_website"));
        app_developer.setText("Developer: "+app_deatils.get("app_developer"));
        app_developer_email.setText(app_deatils.get("app_developer_email"));
        app_license.setText("\u00a9" + app_deatils.get("current_year")+" Allrights reserved");


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Get id of selected menu Item
        int id = item.getItemId();
        switch (id){
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
