package firebase;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.google.android.exoplayer2.util.ParsableNalUnitBitArray;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;

import configuration.Device;
import story.Story;
import welcome.StoryItemAdapter;

/**
 * Created by victor on 10/21/2017.
 */

public class Database {

    /*Important Constants for referencing a Story Item*/
    public static final String STORY_REF = "Contents";
    public static final String STORY_URL = "Content_Url";
    public static final String STORY_DATE = "Date";
    public static final String STORY_NUM_LIKES = "Likes";
    public static final String STORY_ICON_URL = "Picture_Url";
    public static final String STORY_TITLE = "Title";
    public static final String STORY_NUM_VIEWS = "Views";
    public static final String STORY_PAYMENT_STATUS = "Free";
    //For story likes
    public static final String STORY_LIKED = "YES";
    public static final String STORY_DISLIKED = "NO";
    /*Constants for Referencing User Details*/
    public static final String USER_REF = "Customers";
    public static final String USER_ACC_BALANCE = "Account Balance";
    public static final String USER_ACC_NUMBER = "Account ID";
    public static final String USER_EMAIL = "Email";
    public static final String USER_FULL_NAME = "Full Name";
    public static final String USER_BUNDLE_TYPE = "Kifurushi Type";
    public static final String USER_MOBILE_NUMBER = "Mobile Number";
    public static final String USER_PASSWORD = "Password";
    public static final String USER_ACC_SUB_EXP_DATE = "Expiry Date";
    /*Constants associated with Bundles (Vifurushi) as per Firebase database*/
    public static final String BUNDLE_REF = "Vifurushi";
    public static final String BUNDLE_NUM_DAYS = "Days";
    public static final String BUNDLE_DESC = "Description";
    public static final String BUNDLE_PRICE = "Fee";
    public static final String BUNDLE_NAME = "name";
    /*Class Variables (Firebase based) */
    private FirebaseDatabase database;
    private DatabaseReference reference;
    /*Other Class Variables*/
    private Context context;
    /*Constructor takes context as an argument*/
    public Database (Context context){
        this.context = context;
        this.database = FirebaseDatabase.getInstance();
        this.reference = this.database.getReference();
    }
    /*Returns current Context*/
    public Context getContext(){
        return this.context;
    }
    /*Return FirebaseDatabase Instance*/
    public FirebaseDatabase getDatabase(){
        return this.database;
    }
    /*Returns DatabaseReference of FirebaseDatabase*/
    public DatabaseReference getDatabaseReference (){
        return this.reference;
    }
    /*Returns Stories reference*/
    public DatabaseReference getStoryReference () {
        return  getDatabaseReference().child(STORY_REF);
    }
    /*Returns Users Reference*/
    public DatabaseReference getUserReference () {
        return getDatabaseReference().child(USER_REF);
    }
    /*Returns Bundle Reference*/
    public DatabaseReference getBundleReference () {
        return getDatabaseReference().child(BUNDLE_REF);
    }
    /*Reads stories from Firebase and populate them in a List
    * It takes Story Reference, ArrayList<story.Story> RecyclerView.Adapter and
    * RecyclerView Instances,
    * @is_popular states weather the list is for popular stories*/
    public void showStories (final RecyclerView story_recycler, final ArrayList<Story> story_list, final story.StoryItemAdapter item_adapter, final boolean is_popular){
        //All stuffs have to be done in background
        @SuppressLint("StaticFieldLeak") AsyncTask<String,String,ArrayList<Story>> asyncTask = new AsyncTask<String, String, ArrayList<Story>>() {

            ProgressDialog dialog;

            @Override
            protected void onPreExecute() {
                //Show dialog when background process is still running
                dialog = new ProgressDialog(getContext());
                dialog.setMessage("Loading ...");
                //Check for Internet connection
                Device device = new Device(getContext());
                if (device.hasInternet())
                dialog.show();
            }

            @Override
            protected ArrayList<Story> doInBackground(String... params) {
                //Listen to Value change event and apply all logic in here
                getStoryReference().addValueEventListener(new ValueEventListener() {
                    @SuppressLint("ClickableViewAccessibility")
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //Get all stories in an Iterable List
                        Iterable<DataSnapshot> stories_snapshot = dataSnapshot.getChildren();
                        //For stories in a Local database
                        local.Database local_database = new local.Database(getContext());
                        //Clear story_list
                        story_list.clear();
                        //Clear TABLE_STORY
                        local_database.clearTableStory();
                        //Iterate through each Story and Collect it's details
                        for (DataSnapshot snapshot:stories_snapshot){
                            //Read story attributes from firebase
                            String story_id = snapshot.getKey();
                            String story_title = snapshot.child(Database.STORY_TITLE).getValue().toString();
                            String story_release_date = snapshot.child(Database.STORY_DATE).getValue().toString();
                            String story_num_views = snapshot.child(Database.STORY_NUM_VIEWS).getValue().toString();
                            String story_num_likes = snapshot.child(Database.STORY_NUM_LIKES).getValue().toString();
                            String payment_status = snapshot.child(Database.STORY_PAYMENT_STATUS).getValue().toString();
                            String story_icon_url = snapshot.child(Database.STORY_ICON_URL).getValue().toString();
                            String story_url = snapshot.child(Database.STORY_URL).getValue().toString();
                            //Assign attribute Values to a story
                            story.Story story = new story.Story();
                            story.setStory_id(story_id);
                            story.setTitle(story_title);
                            story.setReleaseDate(Database.formatDate(story_release_date));
                            story.setOriginal_date(story_release_date);
                            story.setNumViews(Integer.parseInt(story_num_views));
                            story.setNumLikes(Integer.parseInt(story_num_likes));
                            story.setPaymentStatus(payment_status.equalsIgnoreCase("NO")? Story.PAYMENT_NON_FREE: Story.PAYMENT_FREE);
                            story.setIcon_url(story_icon_url);
                            story.setDownload_url(story_url.replace(" ","%20"));
                            story.setDate_sum(getDateSum(story));
                            story_list.add(story);
                            //Add story to a local database
                            local_database.storeStory(story);
                        }
                        //item_adapter has to be created using Default Constructor
                        //Use respective setter functions to set Values for Context and
                        //storyList
                        item_adapter.setContext(getContext());

                        //Check if is_popular is true the reArrange List
                        if (is_popular) {
                            makePopularList(story_list);
                        }
                        else { //Rearrange stories in such a way recent stories are previewed first
                            makeRecentFirst(story_list);
                        }
                        item_adapter.setStoryArrayList(story_list);
                        story_recycler.setAdapter(item_adapter);
                        if (dialog.isShowing() && story_list.size() > 0)
                            dialog.dismiss();
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(getContext(),databaseError.getMessage(),Toast.LENGTH_LONG).show();
                    }
                });
                return null;
            }
        };
        asyncTask.execute("");
    }
    /*Formats Time stamp into Custom date like October 20, 2017*/
    public static String formatDate (String date){
        String year = date.substring(0,4);
        int month = Integer.parseInt(date.substring(5,7));
        String day = date.substring(8,10);
        String text_month = null;
        switch (month){
            case 1:
                text_month = "January";
                break;
            case 2:
                text_month = "February";
                break;
            case 3:
                text_month = "March";
                break;
            case 4:
                text_month = "April";
                break;
            case 5:
                text_month = "May";
                break;
            case 6:
                text_month = "June";
                break;
            case 7:
                text_month = "July";
                break;
            case 8:
                text_month = "August";
                break;
            case 9:
                text_month = "September";
                break;
            case 10:
                text_month = "October";
                break;
            case 11:
                text_month = "November";
                break;
            case 12:
                text_month = "December";
                break;
        }
        String full_date = text_month+", "+day+" "+year;
        return full_date;
    }

    /*Called to Arrange stories in descending order
    * of number of likes, it takes ArrayList<story.Story>
    *     It uses recursion*/
    //Create Empty ArrayList<Story>
    ArrayList<Story> target_list ;
    private void makePopularList (ArrayList<Story> story_list) {
        target_list = new ArrayList<Story>();
        //Make temporary list
        ArrayList<Story> temp_list = new ArrayList<Story>();
        //Copy everything to temp_list
        temp_list.addAll(story_list);
        //Delete everything from story_list
        story_list.clear();
        //Stores story with many likes
        Story target_story = temp_list.get(0);
        //create checker value
        int many_likes = target_story.getNumLikes();
        //Iterate in list and perform relevant actions
        for (Story story:temp_list){
            if (story.getNumLikes() > many_likes) {
                many_likes = story.getNumLikes();//Update many_likes value
                target_story = story;//Update target_story value
            }
        }
        story_list.add(target_story);
        temp_list.remove(target_story);
        story_list.addAll(getNextStory(temp_list));
    }


    //Called to get Next story in popular List
    private ArrayList<Story> getNextStory (ArrayList<Story> story_list) {
        if (story_list.size() > 0){
            Story target_story = story_list.get(0);
            int many_likes = target_story.getNumLikes();
            for (Story story:story_list){
                if (story.getNumLikes() > many_likes) {
                    many_likes = story.getNumLikes();
                    target_story = story;
                }
            }
            story_list.remove(target_story);
            target_list.add(target_story);
            return getNextStory(story_list);
        }
        return target_list;
    }
    //Rearranges stories in descending order, Most recently released will be
    //Shown first, it uses release date
    private void makeRecentFirst (ArrayList<Story> story_list){
        target_list = new ArrayList<Story>();
        //Make temporary list
        ArrayList<Story> temp_list = new ArrayList<Story>();
        //Copy everything to temp_list
        temp_list.addAll(story_list);
        //Delete everything from story_list
        story_list.clear();
        //Stores story with high date_sum
        Story target_story = temp_list.get(0);
        //create checker value
        String release_date = target_story.getOriginal_date();
        //Iterate in list and perform relevant actions
        for (Story story:temp_list){
            if (story.getOriginal_date().compareTo(release_date) > 0) {
                release_date = story.getOriginal_date();//Update many_likes value
                target_story = story;//Update target_story value
            }
        }
        story_list.add(target_story);
        temp_list.remove(target_story);
        story_list.addAll(getNextRecentStory(temp_list));
    }
    //Retrieves List of Recent stories
    private ArrayList<Story> getNextRecentStory (ArrayList<Story> story_list) {
        if (story_list.size() > 0){
            Story target_story = story_list.get(0);
           String release_date = target_story.getOriginal_date();
            for (Story story:story_list){
                if (story.getOriginal_date().compareTo(release_date) > 0) {
                    release_date = story.getOriginal_date();
                    target_story = story;
                }
            }
            story_list.remove(target_story);
            target_list.add(target_story);
            return getNextRecentStory(story_list);
        }
        return target_list;
    }

    //Adds year,month,day,hour,minute and second of story release date
    private int getDateSum (Story story){
        String release_date = story.getOriginal_date();
        int year = Integer.parseInt(release_date.substring(0,4));
        int month = Integer.parseInt(release_date.substring(5,7));
        int day = Integer.parseInt(release_date.substring(8,10));
        int hour = Integer.parseInt(release_date.substring(11,13));
        int minute = Integer.parseInt(release_date.substring(14,16));
        int second = Integer.parseInt(release_date.substring(17,19));
        return year + month + day + hour + minute + second;
    }
}
