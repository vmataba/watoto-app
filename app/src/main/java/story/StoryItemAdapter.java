package story;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.watotoappgmail.watotoapp2.MediaActivity;
import com.watotoappgmail.watotoapp2.R;

import java.io.Serializable;
import java.util.ArrayList;

import configuration.Device;
import configuration.app.ThisApp;
import firebase.Database;
import local.UserDetails;
import user.Account;
import user.User;

/**
 * Created by victor on 10/9/2017.
 */

public class StoryItemAdapter extends StoryAdapter {

    public StoryItemAdapter(ArrayList<Story> stories, Context context) {
        super(context,stories);
    }

    //Default Constructor
    public StoryItemAdapter () {}

    public static class StoryItemViewHolder extends StoryAdapter.ViewHolder{

        public StoryItemViewHolder(View view) {
            super(view);
        }
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        super.onBindViewHolder(holder, position);

        /****Show Real Look of each Story Item as per Current User****/
        if (User.isLoggedIn(getContext())) {
            showRealLook(stories.get(position), holder,getContext());
        }

        //Create Instance of class Device for checking Internet connection
        final Device device = new Device(getContext());

        /*All clicks are controlled here*/
        //All stuffs associated with btnPlay click
        holder.btnPlay.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("StaticFieldLeak")
            @Override
            public void onClick(View v) {

                if (device.hasInternet()){//Online mode is ON
                    Story story = stories.get(position);
                //Check if User Account exists for this device
                    if (User.isLoggedIn(getContext())){
                        //Get details of user account
                        UserDetails userDetails = new UserDetails(getContext());
                        Account user_account = userDetails.getUserAccount();
                        if (user_account.getSubscriptionStatus() == Account.ACCOUNT_SUBSCRIBED){//User account is subscribed
                            //Send Updates to the Server
                            story.updateNumViews(getContext(),holder);
                            //Make story Viewed
                            story.setViewed(getContext());

                            //Call the MediaActivity
                            Intent intent = new Intent(getContext(), MediaActivity.class);
                            intent.putExtra("story",story);
                            intent.putExtra("regular_list",getRegularList(story));
                            getContext().startActivity(intent);
                        } else {//User account is not subscribed
                            if (story.getPaymentStatus().equals(Story.PAYMENT_FREE)){//Selected story is FREE
                                //Allow user to listen to all other free stories
                                //Call the MediaActivity
                                //Make story Viewed
                                story.setViewed(getContext());
                                Intent intent = new Intent(getContext(), MediaActivity.class);
                                intent.putExtra("story",story);
                                intent.putExtra("regular_list",getFreeList(story));
                                getContext().startActivity(intent);
                            } else {
                                showMessage(v,Account.STATE_UNSUBSCRIBED);
                            }
                        }
                    }

                } else {//OFFLINE Mode is ON
                    showMessage(v,"OFFLINE");
                }

            }
        });

        //All stuffs associated with likeView clicks
        holder.numLikesView.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("StaticFieldLeak")
            @Override
            public void onClick(View v) {

                if (device.hasInternet()){//ONLINE Mode is ON

                    Story story = stories.get(position);//Get Current story Item
                    int old_counts = story.getNumLikes();

                    new AsyncTask<String, String, String>() {

                        Story story;
                        User user;
                        ThisApp app;
                        String like_status;

                        @Override
                        protected void onPreExecute() {
                            user = new UserDetails(getContext()).getUserAccount().getUser();
                            story = stories.get(position);
                            app = new ThisApp();
                            like_status = story.getLiked(getContext())?Database.STORY_DISLIKED:Database.STORY_LIKED;
                        }
                        @Override
                        protected String doInBackground(String... strings) {
                            String url = strings[0];
                            url += "&userid="+user.getUser_id()+"&contentid="+story.getStory_id();
                            url += "&likestatus="+like_status;
                            return app.post(2000,2000,url,"");
                        }

                        @Override
                        protected void onPostExecute(String s) {
                            if (s.equals("Success")){

                            }
                        }
                    }.execute(ThisApp.STORY_LIKE_URL);

                    Drawable top_drawable_like = getContext().getResources().getDrawable(R.drawable.ic_thumb_up_like);
                    Drawable top_drawable_dislike = getContext().getResources().getDrawable(R.drawable.ic_thumb_up_dislike);

                    if (story.getLiked(getContext()) == Story.STATE_LIKED){
                        story.setNumLikes(old_counts - 1);
                        holder.numLikesView.setText(Story.format(story.getNumLikes()));
                        holder.numLikesView.setCompoundDrawablesWithIntrinsicBounds(null, top_drawable_dislike , null, null);
                        story.setDisliked(getContext());
                        //Toast.makeText(getContext(),"Removed from liked stories",Toast.LENGTH_SHORT).show();
                        showMessage(v,"Removed from liked stories");
                    } else {
                        story.setNumLikes(old_counts + 1);
                        holder.numLikesView.setText(Story.format(story.getNumLikes()));
                        holder.numLikesView.setCompoundDrawablesWithIntrinsicBounds(null, top_drawable_like , null, null);
                        story.setLiked(getContext());
                       // Toast.makeText(getContext(),"Added to liked stories",Toast.LENGTH_SHORT).show();
                        showMessage(v,"Added to liked stories");
                    }

                } else {//ONLINE MODE IS OFF
                    showMessage(v,"OFFLINE");
                }

            }
        });
         /*Done controlling clicks*/
    }

    private  ArrayList<Story> getRegularList (Story story){
        ArrayList<Story> regular_list = new ArrayList<Story>();

        //for (Story curr_story:this.getStories()){
          //  if (curr_story.getPaymentStatus() == story.getPaymentStatus()){
            //    regular_list.add(curr_story);
            //}
        //}
        regular_list.addAll(this.getStories());
        regular_list.remove(story);
        regular_list.add(0,story);
        return regular_list;
    }
    //Returns an ArrayList of all Free stories
    private  ArrayList<Story> getFreeList (Story story){
        //Make an empty list
        ArrayList<Story> regular_list = new ArrayList<Story>();
        //Make selected story first in list
        regular_list.add(story);
        //Fetch other Free stories
        for (Story curr_story:this.getStories()){
          if (curr_story.getPaymentStatus() == Story.PAYMENT_FREE && curr_story != story){
            regular_list.add(curr_story);
        }
        }
        return regular_list;
    }
    //Displays message using SnackBar
    public void showMessage (View view, String message){
        Snackbar.make(view, message, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }


}
