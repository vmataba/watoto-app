package welcome;

import android.app.Activity;
import android.app.Application;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.watotoappgmail.watotoapp2.HomeActivity;
import com.watotoappgmail.watotoapp2.R;

import java.util.ArrayList;

import configuration.AppBroadCastReceiver;
import configuration.Device;
import configuration.app.ThisApp;
import firebase.Database;
import home.AllStories;


/**
 * Created by victor on 10/10/2017.
 */

public class Story extends android.support.v4.app.Fragment {

    private RecyclerView storyRecycler;
    private Context context;
    private StoryItemAdapter storyAdapter;
    private RecyclerView.LayoutManager layoutManager;
    public ArrayList<story.Story> story_list;
    private FloatingActionButton fab;
    //Default Constructor
    public Story() {

    }

    public Context getContext(){
        return this.getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab_stories_layout,container,false);
        final String message;
        if (AppBroadCastReceiver.isConnected())
            message = "ONLINE";
        else
            message = "OFFLINE";
        //Setting the RecyclerView
        storyRecycler = (RecyclerView) rootView.findViewById(R.id.welcome_stories_recycler_id);
        fab = (FloatingActionButton) rootView. findViewById(R.id.fab);
        reload();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reload();
                //Snackbar.make(view, message, Snackbar.LENGTH_LONG)
                  //      .setAction("Action", null).show();
                Toast.makeText(getContext(),message,Toast.LENGTH_LONG).show();

            }
        });
        return rootView;
    }

    private void reload(){
        if (!AppBroadCastReceiver.isConnected())
            return;
        storyRecycler.setHasFixedSize(true);
        //Layout Manager
        layoutManager = new GridLayoutManager(getContext(),2);
        layoutManager = Device.isTablet(getContext())? new GridLayoutManager(getContext(), Device.NUM_COL_FOR_TABLET):
                new GridLayoutManager(getContext(),Device.NUM_COL_FOR_PHONE);
        storyRecycler.setLayoutManager(layoutManager);
        //Instantiate storyAdapter using default Constructor
        storyAdapter = new StoryItemAdapter();
        //Make story_list Empty
        story_list = new ArrayList<story.Story>();
        //Create Database instance
        Database database = new Database(getContext());
        //Call instance function showStories to set Stories up
        database.showStories(storyRecycler,story_list,storyAdapter,false);

    }
}
