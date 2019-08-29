package com.watotoappgmail.watotoapp2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ExpandableListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import home.faq.ExpandableListAdapter;

public class FaqActivity extends AppCompatActivity {

    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);
        //Set Action bar's title
        getSupportActionBar().setTitle("FAQ");
        //Show back navigation arrow
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        // get the listview
        expListView = (ExpandableListView) findViewById(R.id.faq_main_list_id);

        // preparing list data
        prepareListData();

        listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);

        // setting list adapter
        expListView.setAdapter(listAdapter);
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

    /*
    * Preparing the list data
    */
    private void prepareListData() {
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();

        // Adding child data
        listDataHeader.add("Nawezaje kufanya malipo (Kwa wale wenye namba ya Tigo)");
        //listDataHeader.add("Now Showing");
        //listDataHeader.add("Coming Soon..");

        // Adding child data
        List<String> malipo = new ArrayList<String>();
        malipo.add("Fungua menu ya Tigo Pesa kwa kupiga *150*01#");
        malipo.add("Kisha Changua Na. 4 Kulipia Bili");
        malipo.add("Kisha Chagua Na.3 Weka namba ya kampuni");
        malipo.add("Kisha Namba ya kampuni: 270006 (Jina la Kampuni: Lebena Kids)");
        malipo.add("Kisha Namba ya kumbukumbu ya malipo: TZxxxxxx mfano:TZ100001   ( Kwenye namba ya kumbukumbu, weka namba yako ya Akaunti)");
        malipo.add("Kisha Ingiza kiasi unacholipia, kiwango cha chini ni 1000");
        malipo.add("Kisha Weka namba yako ya Siri (Hakikisha Jina la Kampuni linasomeka Lebena Kids)");
        malipo.add("Baada ya kufanya malipo, tuma meseji utakayotumiwa kwenda Namba: 0652605256  au 0656729975");


        listDataChild.put(listDataHeader.get(0), malipo); // Header, Child data

    }
}
