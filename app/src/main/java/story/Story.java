package story;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.widget.Toast;

import com.watotoappgmail.watotoapp2.R;

import java.io.Serializable;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

import configuration.app.ThisApp;
import local.Database;
import local.UserDetails;
import user.User;

/**
 * Created by victor on 10/8/2017.
 */

public class Story implements Serializable {

    public static final String PAYMENT_FREE = "FREE";
    public static final String PAYMENT_NON_FREE = "NON FREE";
    public static final boolean STATE_LIKED = true;
    public static final boolean STATE_DISLIKED = false;
    public static final boolean STATE_VIEWED = true;
    public static final boolean STATE_NOT_VIEWED = false;

    private String story_id;
    private String title;
    private String releaseDate;
    private int numViews;
    private int numLikes;
    private String paymentStatus;
    private boolean liked;
    private boolean viewed;
    private String download_url;
    private String icon_url;
    private String original_date;
    private int date_sum;

    //For Local storage
    private Database database;
    private UserDetails user_details;

    /*Empty Constructor*/
    public Story () {
        this.liked = false;
        this.viewed = false;
    }

    public Story(String title, String releaseDate, int numViews, int numLikes, String paymentStatus) {
        this();
        this.title = title;
        this.releaseDate = releaseDate;
        this.numViews = numViews;
        this.numLikes = numLikes;
        this.paymentStatus = paymentStatus;

    }

    public Story (String title, String releaseDate, int numViews, int numLikes, String paymentStatus, String download_url, String icon_url){
        this(title,releaseDate,numViews,numLikes,paymentStatus);
        this.download_url = download_url;
        this.icon_url = icon_url;
    }

    public void setStory_id(String story_id){
        this.story_id = story_id;
    }

    public void setTitle(String title){
        this.title = title;
    }

    public void setReleaseDate(String releaseDate){
        this.releaseDate = releaseDate;
    }

    public void setNumViews(int numViews){
        this.numViews = numViews;
    }

    public void setNumLikes(int numLikes){
        this.numLikes = numLikes;
    }

    public void setPaymentStatus(String paymentStatus){
        this.paymentStatus = paymentStatus;
    }

    public void setDownload_url (String download_url){
        this.download_url = download_url;
    }

    public void setIcon_url(String icon_url){
        this.icon_url = icon_url;
    }

    public void setOriginal_date(String original_date){
        this.original_date = original_date;
    }

    public void setDate_sum(int date_sum){
        this.date_sum = date_sum;
    }

    public String getStory_id (){
        return this.story_id;
    }

    public String getTitle(){
        return this.title;
    }

    public String getReleaseDate() {
        return this.releaseDate;
    }

    public int getNumViews() {
        return this.numViews;
    }

    public int getNumLikes() {
        return this.numLikes;
    }

    public String getPaymentStatus() {
        return this.paymentStatus;
    }

    public String getOriginal_date(){
        return this.original_date;
    }

    public int getDate_sum(){
        return this.date_sum;
    }
/**Called when current User has Liked a story***/
    public void setLiked(Context context){
        database = new Database(context);
        user_details = new UserDetails(context);
        User user = user_details.getUserAccount().getUser();
        this.liked = database.setLiked(this,user);
    }
    /***Called when user dislikes a story***/
    public void setDisliked(Context context){
        database = new Database(context);
        user_details = new UserDetails(context);
        User user = user_details.getUserAccount().getUser();
        this.liked = database.setDisLiked(this,user);
    }
    /***Called to tell if user has liked a particular story***/
    public boolean getLiked(Context context){
        database = new Database(context);
        user_details = new UserDetails(context);
        User user = user_details.getUserAccount().getUser();
        return database.getLikedStatus(this,user);
    }
    /***Called when user has viewed a story***/
    public void setViewed (Context context) {
        if (User.isLoggedIn(context)){
            Database database = new Database(context);
            UserDetails user_details = new UserDetails(context);
            User user = user_details.getUserAccount().getUser();
            this.viewed = database.setViewed(this,user);
        }
    }
    /***Called to tell if user has Viewed a particular story***/
public boolean getViewed (Context context) {
    database = new Database(context);
    user_details = new UserDetails(context);
    User user = user_details.getUserAccount().getUser();
    return database.getViewedStatus(this,user);
}
    public String getDownload_url(){
        return this.download_url;
    }

    public String getIcon_url(){
        return this.icon_url;
    }

    public boolean isLiked () {
        return this.liked;
    }
    /***This Function Will Update Number of Story Views in Server***/
    @SuppressLint("StaticFieldLeak")
    public void updateNumViews (final Context context, final StoryAdapter.ViewHolder holder) {
        final Story story = this;
        new AsyncTask<String, String, String>() {

            @Override
            protected String doInBackground(String... strings) {

                  User user = new UserDetails(context).getUserAccount().getUser();
                  ThisApp app = new ThisApp();
                  String url = strings[0]+"&userid="+user.getUser_id()+"&contentid="+story.getStory_id();
                  return app.post(2000,2000,url,"");

            }
            @Override
            protected void onPostExecute(String s) {
                if (!s.equals(ThisApp.FAILED_PROCESS_MESSAGE)){//Everything is fine
                    //Update number of views
                    int num_old_views = story.getNumViews();
                    int num_new_views = num_old_views + 1;
                    //Update UI
                    story.setNumViews(num_new_views);
                    if (holder != null)
                    holder.numViewsView.setText(story.getNumViews()+"");
                    //update View Icon
                    Drawable views_on_drawable = context.getResources().getDrawable(R.drawable.ic_ear_active);
                   if (holder != null)
                    holder.numViewsView.setCompoundDrawablesWithIntrinsicBounds(null,views_on_drawable,null,null);
                }
            }
        }.execute(ThisApp.STORY_VIEW_URL);
    }

    /***This function update number of story views played by Unregistered Users**/
    /***This Function Will Update Number of Story Views in Server***/
    @SuppressLint("StaticFieldLeak")
    public void updateNumViewsForUnregUsers (final Context context, final StoryAdapter.ViewHolder holder) {
        final Story story = this;
        new AsyncTask<String, String, String>() {

            @Override
            protected String doInBackground(String... strings) {

                    ThisApp app = new ThisApp();
                    String url = strings[0]+"&contentid="+story.getStory_id();
                    return app.post(2000,2000,url,"");

                //return strings[0];
            }
            @Override
            protected void onPostExecute(String s) {
                if (!s.equals(ThisApp.FAILED_PROCESS_MESSAGE)){//Everything is fine
                    //Update number of views
                    int num_old_views = story.getNumViews();
                    int num_new_views = num_old_views + 1;
                    //Update UI
                    story.setNumViews(num_new_views);
                    //if (holder != null)
                       // holder.numViewsView.setText(story.getNumViews()+"");
                    //update View Icon
                    Drawable views_on_drawable = context.getResources().getDrawable(R.drawable.ic_ear_active);
                    if (holder != null)
                        holder.numViewsView.setCompoundDrawablesWithIntrinsicBounds(null,views_on_drawable,null,null);

                }
            }
        }.execute(ThisApp.STORY_VIEW_UNREG_URL);
    }
    //Holds alphabetical values to be added at the end of the figure which is
    //to be formated
    private static final NavigableMap<Long, String> suffixes = new TreeMap<>();
    static {
        suffixes.put(1_000L, "K");
        suffixes.put(1_000_000L, "M");
        suffixes.put(1_000_000_000L, "G");
        suffixes.put(1_000_000_000_000L, "T");
        suffixes.put(1_000_000_000_000_000L, "P");
        suffixes.put(1_000_000_000_000_000_000L, "E");
    }
    //Accepts a figure like 1000 and outputs 1K, 1200 becomes 1.2K or 1000000 becomes 1M
    //and alike.
    public static String format(long value) {
        //Long.MIN_VALUE == -Long.MIN_VALUE so we need an adjustment here
        if (value == Long.MIN_VALUE) return format(Long.MIN_VALUE + 1);
        if (value < 0) return "-" + format(-value);
        if (value < 1000) return Long.toString(value); //deal with easy case

        Map.Entry<Long, String> e = suffixes.floorEntry(value);
        Long divideBy = e.getKey();
        String suffix = e.getValue();

        long truncated = value / (divideBy / 10); //the number part of the output times 10
        boolean hasDecimal = truncated < 100 && (truncated / 10d) != (truncated / 10);
        return hasDecimal ? (truncated / 10d) + suffix : (truncated / 10) + suffix;
    }
}
