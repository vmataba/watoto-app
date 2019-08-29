package media;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.watotoappgmail.watotoapp2.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import story.Story;

/**
 * Created by victor on 10/8/2017.
 */

public class StoryAdapter extends RecyclerView.Adapter<StoryAdapter.ViewHolder> {


    public ArrayList<Story> stories;
    private Context context;


    public static class ViewHolder extends RecyclerView.ViewHolder {

        public View view;
        public TextView titleView;
        //public ImageView storyIconView;
        public LinearLayout story_layout;
        public CircleImageView storyIconView;


        public ViewHolder(View view) {
            super(view);
            this.view = view;

            titleView = (TextView) this.view.findViewById(R.id.story_title_id_for_media_activity);
            storyIconView = (CircleImageView) this.view.findViewById(R.id.story_icon_for_media_activity);
            story_layout = (LinearLayout)this.view.findViewById(R.id.single_story_layout_id);

        }
        //Makes Story Title in Blue Texts
        @SuppressLint("ResourceAsColor")
        public void makeBlueTitle (int story_position) {

            titleView.setTextColor(R.color.colorPrimary);
        }
    }

    public StoryAdapter(ArrayList<Story> stories, Context context) {
        this.stories = stories;
        this.context = context;
    }

    //Returns Context of the Current Activity
    public Context getContext() {
        return this.context;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public StoryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View storyView = (View) LayoutInflater.from(parent.getContext()).inflate(R.layout.single_story_view_for_media_activity, parent, false);

        ViewHolder vh = new ViewHolder(storyView);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        //Get Story instance
        final Story story = stories.get(position);
        /*Set Values to the views*/
        //Set title
        holder.titleView.setText(story.getTitle());
        //Set xml background for this Icon
        holder.storyIconView.setBackground(getContext().getResources().getDrawable(R.drawable.media_story_icon_background));
        //Set Icon
        Picasso.with(getContext()).load(story.getIcon_url()).fit().into(holder.storyIconView);
        //SetAction
        holder.story_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // Toast.makeText(getContext(),story.getTitle(),Toast.LENGTH_LONG).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return stories.size();
    }

    public void setFilter(ArrayList<Story> new_story_list){
        stories = new ArrayList<Story>();
        stories.addAll(new_story_list);
        notifyDataSetChanged();
    }
}
