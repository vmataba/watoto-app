package welcome;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.watotoappgmail.watotoapp2.MediaActivity;
import com.watotoappgmail.watotoapp2.R;

import java.lang.reflect.Array;
import java.util.ArrayList;

import configuration.AppBroadCastReceiver;
import configuration.app.ThisApp;
import local.UserDetails;
import story.*;
import story.Story;
import user.User;

/**
 * Created by victor on 10/10/2017.
 */

public class StoryItemAdapter extends story.StoryItemAdapter {

    private static boolean is_fired = false;//Checks if NON_FREE Story is selected


    public StoryItemAdapter(ArrayList<Story> stories, Context context) {
        super(stories, context);
    }
    //Default constructor
    public StoryItemAdapter(){}

    public static class StoryItemViewHolder extends story.StoryItemAdapter.StoryItemViewHolder{

        public StoryItemViewHolder(View view) {
            super(view);

        }
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        super.onBindViewHolder(holder, position);

        final Story story = stories.get(position);
        final String story_payment_status = story.getPaymentStatus();

        holder.btnPlay.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("StaticFieldLeak")
            @Override
            public void onClick(View v) {

                if (!AppBroadCastReceiver.isConnected()){
                    showMessage(v,"OFFLINE");
                    return;
                }

                if (story_payment_status.equals(Story.PAYMENT_NON_FREE)){
                    //Navigate to Registration Tab
                    InnerStory innerStory = new InnerStory(StoryItemAdapter.super.getContext());
                    innerStory.getViewPager().setCurrentItem(2);

                } else {

                    story.updateNumViewsForUnregUsers(getContext(),holder);

                    //Update Number of Views
                    /*int num_old_views = story.getNumViews();
                    int num_new_views = num_old_views + 1;
                    story.setNumViews(num_new_views);
                    holder.numViewsView.setText(Story.format(story.getNumViews()));

                    //Update icon
                    Drawable ic_view_active = getContext().getResources().getDrawable(R.drawable.ic_ear_active);
                    holder.numViewsView.setCompoundDrawablesRelativeWithIntrinsicBounds(null,ic_view_active,null,null);*/

                    //Navigate to MediaActivity
                    Intent intent = new Intent(getContext(), MediaActivity.class);
                    intent.putExtra("regular_list",getFreeStories(story));
                    intent.putExtra("story",story);
                    getContext().startActivity(intent);

                    //Send updates to the server


                }
            }
        });

        //Handling Number of likes
        holder.numLikesView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //showMessage(v,"COMPLETE REGISTRATION");
                Toast.makeText(getContext(),"COMPLETE REGISTRATION",Toast.LENGTH_LONG).show();
            }
        });
    }

    public static class InnerStory extends welcome.Story{

        public ViewPager viewPager;

        public InnerStory(){
            super();
        }

        @SuppressLint("ValidFragment")
        public InnerStory(Context context) {
           viewPager = (ViewPager) (((Activity)context).findViewById(R.id.container));
        }

        public ViewPager getViewPager(){
           return this.viewPager;
        }
    }
    //Creates a list of all Free Stories @param selected is a story chosen to play
    private ArrayList<Story> getFreeStories (Story selected) {
        //Create empty list
        ArrayList<Story> free_stories = new ArrayList<>();
        //Fetch all Free Stories
        for (Story story:stories){
            if (story.getPaymentStatus().equals(Story.PAYMENT_FREE)){//story is Free, add it in list
                free_stories.add(story);
            }
        }
        //Remove selected in list
        free_stories.remove(selected);
        //Make in the first in List
        free_stories.add(0,selected);
        return free_stories;
    }
    //Displays message using SnackBar
    public void showMessage (View view, String message){
        Snackbar.make(view, message, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }
}
