package home;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.support.v7.widget.SearchView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import firebase.Database;
import story.Story;
import story.StoryAdapter;
import story.StoryItemAdapter;

/**
 * Created by victor on 10/26/2017.
 */

public class Searcher {

    //For search View
    private SearchView searchView;
    //For AllStories Adapter
    private StoryItemAdapter all_adapter;
    //For PopularStories Adapter
    private StoryItemAdapter popular_adapter;
    //For AllStories zero result message
    private TextView all_no_stories;
    //For PopularStories zero result message
    private TextView popular_no_stories;
    private ArrayList<Story> stories_backup;
    //For Context
    private Context context;

    //Default Constructor
    public Searcher (Context context) {
        this.context = context;
    }
    public Searcher (Context context,SearchView searchView){
        //searchView and searchManager Initializer
        this(context);
        this.searchView = searchView;
    }
    //Setter Method for searchView
    public void setSearchView (SearchView searchView){
        this.searchView = searchView;
    }
    //Setter Method for all_adapter
    public void setAll_adapter(StoryItemAdapter all_adapter){
        this.all_adapter = all_adapter;
        stories_backup = new ArrayList<Story>();
       // stories_backup.addAll(this.all_adapter.getStories());
    }
    //Getter Method for context
    public Context getContext(){
        return this.context;
    }
    //Setter Method for popular_adapter
    public void setPopular_adapter (StoryItemAdapter popular_adapter){
        this.popular_adapter = popular_adapter;
    }
    //Setter Methdd for all_no_stories
    public void setAll_no_stories(TextView all_no_stories){
        this.all_no_stories = all_no_stories;
    }
    //Setter Method for popular_no_stories
    public void setPopular_no_stories(TextView popular_no_stories){
        this.popular_no_stories = popular_no_stories;
    }

    //Getter Method for searchView
    public SearchView getSearchView(){
        return this.searchView;
    }
    //Getter Method for all_adapter
    public StoryItemAdapter getAll_adapter(){
        return this.all_adapter;
    }
    //Getter Method for popular_adapter
    public StoryItemAdapter getPopular_adapter(){
        return this.popular_adapter;
    }
    //Getter Method for all_no_stories
    public TextView getAll_no_stories(){
        return this.all_no_stories;
    }
    //Getter Method for popular_no_stories
    public TextView getPopular_no_stories(){
        return this.popular_no_stories;
    }

    //Performs searching
    public void search (){
        this.getSearchView().setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                //Store key_text in lowercase
                String key_text = newText.toLowerCase();
                //Create an empty List for storing matched stories
                ArrayList<Story> temp_list = new ArrayList<Story>();
                ArrayList<Story> temp_pop = new ArrayList<>();
                //Filter Stories for ALL Tab
                for (Story story:getAll_adapter().all_stories){

                    if (story.getTitle().toLowerCase().contains(key_text)){//Story Matched search key
                        //Add it in temp_list
                        temp_list.add(story);
                    }
                    //restoreState();
                }
                //Filter Stories for POPULAR Tab
                for (Story story:getPopular_adapter().all_stories){

                    if (story.getTitle().toLowerCase().contains(key_text)){//Story Matched search key
                        //Add it in temp_pop
                        temp_pop.add(story);
                    }
                    //restoreState();
                }
                if (newText.equals("")){
                   restoreState();
                } else if (getAll_adapter().stories.size() == 0 || getAll_adapter().getItemCount() == 0){//No story has matched search_key
                    //Toast.makeText(getContext(),temp_list.size()+"",Toast.LENGTH_LONG).show();
                    getAll_no_stories().setVisibility(View.VISIBLE);//Show message in ALL Tab
                    getPopular_no_stories().setVisibility(View.VISIBLE);//Show message in POPULAR Tab
                } else {//Matching exists, Hide no stories found text views
                    getAll_no_stories().setVisibility(View.INVISIBLE);
                    getPopular_no_stories().setVisibility(View.INVISIBLE);
                }
                //Filter stories in ALL Tab
                getAll_adapter().setFilter(temp_list);
                //Filter stories in POPULAR Tab
                getPopular_adapter().setFilter(temp_pop);

                return false;
            }
        });
    }

    //Restores original state of the RecyclerView Adapter
    private void restoreState () {
        getAll_no_stories().setVisibility(View.INVISIBLE);
        getPopular_no_stories().setVisibility(View.INVISIBLE);
        Database database = new Database(getContext());
        database.showStories(AllStories.storyRecycler,getAll_adapter().stories,getAll_adapter(),false);
        database.showStories(PopularStories.popular_stories_recycler_view,getPopular_adapter().stories,getPopular_adapter(),true);
    }
}
