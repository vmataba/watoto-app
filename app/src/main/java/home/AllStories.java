package home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.watotoappgmail.watotoapp2.R;

import java.util.ArrayList;

import configuration.AppBroadCastReceiver;
import configuration.Device;
import configuration.app.ThisApp;
import firebase.Database;
import story.Story;
import story.StoryItemAdapter;

/**
 * Created by victor on 10/10/2017.
 */

public class AllStories extends android.support.v4.app.Fragment{

    public static RecyclerView storyRecycler;
    public static StoryItemAdapter itemAdapter;
    public  static ArrayList<Story> storyArrayList;
    private GridLayoutManager layoutManager;
    public static TextView no_stories_found;
    private SwipeRefreshLayout refreshLayout;
    private View rootView;

    public AllStories(){

    }

    public Context getContext(){
        return this.getActivity();
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.tab_home_all_stories_layout,container,false);
        //Initializing recycler view
        storyRecycler = (RecyclerView) rootView.findViewById(R.id.tab_home_all_stories_recycler_id);
        //No stories found prompt message
        no_stories_found = (TextView) rootView.findViewById(R.id.tab_home_no_stories_found_text_view_id);
        refreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_container);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                display();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshLayout.setRefreshing(false);
                    }
                },5000);
            }
        });
        super.onResume();
        //Show contents
        display();

        //Ready to Go ....
        return rootView;
    }
    //Displays contents on the screen
    public void display () {
        //Create Instance of Class Device
        Device device = new Device(getContext());
        //Set LayoutManager for storyRecycler
        if (Device.isTablet(getContext())){//Tablet
            layoutManager = new GridLayoutManager(getContext(),Device.NUM_COL_FOR_TABLET);
            if (!device.isPotrait()){//Orientation is LandScape
                layoutManager = new GridLayoutManager(getContext(),Device.NUM_COL_FOR_TAB_LAND_SCAPE);
            }
        } else {//Phone
            layoutManager = new GridLayoutManager(getContext(),Device.NUM_COL_FOR_PHONE);
            if (!device.isPotrait()){//LandScape Orientation
                layoutManager = new GridLayoutManager(getContext(),Device.NUM_COL_FOR_PHONE_LAND_SCAPE);
            }

        }
        //Attach LayoutManager to the RecyclerView
        storyRecycler.setLayoutManager(layoutManager);
        storyRecycler.setHasFixedSize(true);
        //Initialize itemAdapter using Default Constructor
        itemAdapter = new StoryItemAdapter();
        //Make storyArrayList Empty
        storyArrayList = new ArrayList<Story>();
        if (AppBroadCastReceiver.isConnected()){
            //Create firebase.Database Instance
            Database database = new Database(getContext());
            //Call showStories function using database instance
            database.showStories(storyRecycler,storyArrayList,itemAdapter,false);
        } else {
            //itemAdapter.showAlert(rootView);
            local.Database local_database = new local.Database(getContext());
            storyArrayList = local_database.getStories();
            // Toast.makeText(getContext(),storyArrayList.size()+"",Toast.LENGTH_LONG).show();
            itemAdapter = new StoryItemAdapter(storyArrayList,getContext());
            storyRecycler.setAdapter(itemAdapter);
        }
    }

    //Displays message using SnackBar
    public void showMessage (View view, String message){
        Snackbar.make(view, message, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }
}
