package story;

import android.app.Application;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.watotoappgmail.watotoapp2.R;

import java.net.UnknownServiceException;
import java.util.ArrayList;

import configuration.Device;
import local.Database;
import local.UserDetails;
import user.User;

/**
 * Created by victor on 10/8/2017.
 */

public class StoryAdapter extends RecyclerView.Adapter<StoryAdapter.ViewHolder>{


    public ArrayList<Story> stories;
    public ArrayList<Story> all_stories;
    private Context context;


    public static class ViewHolder extends RecyclerView.ViewHolder {

       public View view;
        public TextView titleView;
        public ImageView storyIconView;
        public TextView releaseDateView;
        public TextView numViewsView;
        public TextView numLikesView;
        public TextView  paymentStatusView ;
        public ImageButton btnPlay;
        public ProgressBar storyProgressBar;

        public ViewHolder(View view) {
            super(view);
            this.view = view;

            titleView = (TextView) this.view.findViewById(R.id.single_story_title_id);
            storyIconView = (ImageView) this.view.findViewById(R.id.single_story_icon_id);
            releaseDateView = (TextView) this.view.findViewById(R.id.single_story_release_date_id);
            numViewsView = (TextView) this.view.findViewById(R.id.single_story_views_id);
            numLikesView = (TextView) this.view.findViewById(R.id.single_story_likes_id);
            paymentStatusView = (TextView) this.view.findViewById(R.id.single_story_payment_status_id);
            btnPlay = (ImageButton) this.view.findViewById(R.id.single_story_btn_play_id);
            storyProgressBar = (ProgressBar) this.view.findViewById(R.id.single_story_btn_progress_bar_id);

        }
    }

    public StoryAdapter(Context context,ArrayList<Story> stories) {
        this.context = context;
        this.stories = stories;
        this.all_stories = this.stories;

    }
    //Default Constructor
    public StoryAdapter () { all_stories = this.getStories();}
    //Setter Method for Context
    public void setContext (Context context){
        this.context = context;
    }
    //Setter Method for ArrayList<Story>
    public void setStoryArrayList(ArrayList<Story> stories){
        this.stories = stories;
        this.all_stories = stories;
    }
    //Getter Method for Context
    public Context getContext () {
        return this.context;
    }
    //Getter Method for stories
    public ArrayList<Story> getStories () {
        return this.stories;
    }
    public ArrayList<Story> getAll_stories(){return all_stories;}
    // Create new views (invoked by the layout manager)

    @Override
    public StoryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View storyView = (View) LayoutInflater.from(parent.getContext()).inflate(R.layout.single_story_view, parent, false);

        ViewHolder vh = new ViewHolder(storyView);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        //Setting values
        Story story = stories.get(position);
        holder.titleView.setText(story.getTitle());
       // holder.storyIconView.setImageURI(Uri.parse(story.getIcon_url()));
        Picasso.with(this.getContext()).load(story.getIcon_url()).fit().into(holder.storyIconView);
        holder.releaseDateView.setText(story .getReleaseDate());
        holder.numLikesView.setText(Story.format(story.getNumLikes()));
        holder.numViewsView.setText(Story.format(story.getNumViews()));
        holder. paymentStatusView .setText(story.getPaymentStatus());

        //'FREE' Payment status is made green, and 'NON FREE' is left red
        if (holder.paymentStatusView.getText().equals(Story.PAYMENT_FREE)){
            holder.paymentStatusView.setTextColor(Color.GREEN);
        } else {
            holder.paymentStatusView.setTextColor(Color.RED);
        }
        //Set Story Icon for Offline mode
        Device device = new Device(getContext());
        if (!device.hasInternet()){
            holder.storyIconView.setImageResource(R.drawable.offline_icon);
        }
        //Hiding progress bar
        holder.storyProgressBar.setVisibility(View.INVISIBLE);
    }


    @Override
    public int getItemCount() {
        return stories.size();
    }
    //Used to populate stories ArrayList with Search results
    public void setFilter(ArrayList<Story> new_story_list){
        stories = new ArrayList<>();
        stories.addAll(new_story_list);
        notifyDataSetChanged();
    }
    /****Displays StoryItem data accordingly, showing stories liked by current user
    Stories already viewed. It's called Whenever DataItems  from Firebase
     Changes***/
    public  static void showRealLook (Story story,ViewHolder holder, Context context) {
        //Get user user details
        UserDetails user_details = new UserDetails(context);
        User user = user_details.getUserAccount().getUser();
        //Check if this user has liked a ***story***
        Database database = new Database(context);
        boolean has_liked = database.getLikedStatus(story,user);
        boolean has_viewed = database.getViewedStatus(story,user);
        if (has_liked){
            Drawable top_drawable_like = context.getResources().getDrawable(R.drawable.ic_thumb_up_like);
            holder.numLikesView.setCompoundDrawablesWithIntrinsicBounds(null, top_drawable_like , null, null);
        }
        if (has_viewed){
            Drawable views_on_drawable = context.getResources().getDrawable(R.drawable.ic_ear_active);
            holder.numViewsView.setCompoundDrawablesWithIntrinsicBounds(null,views_on_drawable,null,null);
        }
    }

}
