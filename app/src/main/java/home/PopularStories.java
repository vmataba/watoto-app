package home;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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

public class PopularStories extends AllStories implements AppBroadCastReceiver.ConnectivityReceiverListener {


    private Context context;
    public static StoryItemAdapter itemAdapter;
    public static TextView no_stories_text_view;
    public static RecyclerView popular_stories_recycler_view;
    private static ArrayList<Story> story_list;
    private SwipeRefreshLayout refreshLayout;
    public View rootView;

    public PopularStories(){

    }

    public Context getContext(){
        return this.getActivity();
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.tab_home_popular_stories_layout,container,false);

        //Initialization
        popular_stories_recycler_view = (RecyclerView) rootView.findViewById(R.id.tab_home_popular_stories_recycler_id);
        RecyclerView.LayoutManager layoutManager;
        refreshLayout = (SwipeRefreshLayout)rootView.findViewById(R.id.swipe_container);
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
        //Create Instance of Class Device
        Device device = new Device(getContext());
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
        popular_stories_recycler_view.setLayoutManager(layoutManager);

        //render contents
        display();

        //Ready to Go...

        no_stories_text_view = (TextView) rootView.findViewById(R.id.tab_home_no_stories_found_text_view_id);


        return rootView;
    }
    //Called to Render contents
    public void display () {
        Device device = new Device(getContext());
        //Initialize itemAdapter with Default Constructor
        itemAdapter = new StoryItemAdapter();
        //Initialize story_list
        story_list = new ArrayList<Story>();
        if (AppBroadCastReceiver.isConnected()){//ONLINE Mode is ON
            //Create firebase.Database Instance
            Database database = new Database(getContext());
            //Call showStories function
            database.showStories(popular_stories_recycler_view,story_list,itemAdapter,true);
        } else {//OFFLINE Mode is ON
            //Get stories from Local storage
            local.Database local_database = new local.Database(getContext());
            story_list = local_database.getStories();
            itemAdapter = new StoryItemAdapter(story_list,getContext());
            popular_stories_recycler_view.setAdapter(itemAdapter);
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        ThisApp.getInstance().setConnectivityListener(this);
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        super.display();
        display();
        String message;
        if (isConnected)
            message = "ONLINE";
        else
            message = "OFFLINE";
        showMessage(rootView,message);
    }
}
