package local;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;

import story.Story;
import user.User;

/**
 * Created by victor on 11/2/2017.
 */

public class Database extends SQLiteOpenHelper {

    //Testing
    private Context context;

    //Class Constants
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "local_database.db";
    /***Table name for User liked and Viewed stories***/
    private static final String TABLE_STORY_LIKES_AND_VIEWS = "story_likes_and_views";
    /**Table Columns**/
    private static String[] TABLE_STORY_LIKES_AND_VIEWS_COLUMNS = {
            "story_id",
            "user_id",
            "is_liked",
            "is_viewed"
    };

    /**This table is for storing stories for offline display**/
    private static final String TABLE_STORY = "story";
    private static final HashMap<String,String> TABLE_STORY_COLUMNS = getTableStoryColumns() ;


    public Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        /***Create Table TABLE_STORY_LIKES_AND_VIEWS***/
        String table_create_query = null;
        table_create_query = "CREATE TABLE "+TABLE_STORY_LIKES_AND_VIEWS+"(";
        table_create_query += TABLE_STORY_LIKES_AND_VIEWS_COLUMNS[0];
        table_create_query += " INTEGER NOT NULL,";
        table_create_query += TABLE_STORY_LIKES_AND_VIEWS_COLUMNS[1];
        table_create_query += " INTEGER NOT NULL,";
        table_create_query += TABLE_STORY_LIKES_AND_VIEWS_COLUMNS[2];
        table_create_query += " BOOLEAN DEFAULT 0,";
        table_create_query += TABLE_STORY_LIKES_AND_VIEWS_COLUMNS[3];
        table_create_query += " BOOLEAN DEFAULT 0,";
        table_create_query += "PRIMARY KEY("+TABLE_STORY_LIKES_AND_VIEWS_COLUMNS[0]+",";
        table_create_query += TABLE_STORY_LIKES_AND_VIEWS_COLUMNS[1];
        table_create_query += "))";
        db.execSQL(table_create_query);
        /***Create Table TABLE_STORY***/
        String table_story_create_query;
        table_story_create_query = "CREATE TABLE "+TABLE_STORY;
        table_story_create_query += " (";
        table_story_create_query += TABLE_STORY_COLUMNS.get("story_id");
        table_story_create_query += " INTEGER NOT NULL PRIMARY KEY, ";
        table_story_create_query += TABLE_STORY_COLUMNS.get("story_title");
        table_story_create_query += " TEXT NOT NULL, ";
        table_story_create_query += TABLE_STORY_COLUMNS.get("release_date");
        table_story_create_query += " TEXT NOT NULL, ";
        table_story_create_query += TABLE_STORY_COLUMNS.get("num_likes");
        table_story_create_query += " INTEGER NOT NULL, ";
        table_story_create_query += TABLE_STORY_COLUMNS.get("num_views");
        table_story_create_query += " INTEGER NOT NULL, ";
        table_story_create_query += TABLE_STORY_COLUMNS.get("payment_status");
        table_story_create_query += " TEXT NOT NULL ";
        table_story_create_query += ")";
        db.execSQL(table_story_create_query);

        /***Done***/
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_STORY_LIKES_AND_VIEWS);
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_STORY);
        onCreate(db);
    }
    //Called When user Likes a story
    public boolean setLiked (Story story, User user){
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TABLE_STORY_LIKES_AND_VIEWS_COLUMNS[0],story.getStory_id());
        values.put(TABLE_STORY_LIKES_AND_VIEWS_COLUMNS[1],user.getUser_id());
        values.put(TABLE_STORY_LIKES_AND_VIEWS_COLUMNS[2],Story.STATE_LIKED+"");
        //values.put(TABLE_STORY_LIKES_AND_VIEWS_COLUMNS[3],true);
        //return database.insert(TABLE_STORY_LIKES_AND_VIEWS,null,values) != -1;
        //Check If Values exist
        String check_query = "SELECT * FROM "+TABLE_STORY_LIKES_AND_VIEWS+" WHERE ";
        check_query += " story_id = "+story.getStory_id()+" AND ";
        check_query += " user_id = "+user.getUser_id();
        //check_query += " is_liked = 1";
        Cursor test_res = database.rawQuery(check_query,null);
        if (test_res.moveToFirst()){//Update
            //Toast.makeText(context,"Updated",Toast.LENGTH_LONG).show();
            String where = "story_id = "+story.getStory_id() + " AND user_id = "+user.getUser_id();
            int res = database.update(TABLE_STORY_LIKES_AND_VIEWS,values,where,null);
            return res !=-1;
        } else {//Insert
            //Toast.makeText(context,"Inserted",Toast.LENGTH_LONG).show();
            return database.insert(TABLE_STORY_LIKES_AND_VIEWS,null,values) != -1;
        }
    }
    //Called When user Dislikes a story
    public boolean setDisLiked (Story story, User user){
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TABLE_STORY_LIKES_AND_VIEWS_COLUMNS[0],story.getStory_id());
        values.put(TABLE_STORY_LIKES_AND_VIEWS_COLUMNS[1],user.getUser_id());
        values.put(TABLE_STORY_LIKES_AND_VIEWS_COLUMNS[2],Story.STATE_DISLIKED);
        //values.put(TABLE_STORY_LIKES_AND_VIEWS_COLUMNS[3],getViewedStatus(story,user));
        //Check Values exist
        String check_query = "SELECT * FROM "+TABLE_STORY_LIKES_AND_VIEWS+" WHERE ";
        check_query += " story_id = "+story.getStory_id()+" AND ";
        check_query += " user_id = "+user.getUser_id();
        Cursor test_res = database.rawQuery(check_query,null);
         //Update
            String where = "story_id = "+story.getStory_id() + " AND user_id = "+user.getUser_id();
            int res = database.update(TABLE_STORY_LIKES_AND_VIEWS,values,where,null);
            return res !=-1;
    }
    //Called When user Views a story at least once
    public boolean setViewed (Story story, User user){
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TABLE_STORY_LIKES_AND_VIEWS_COLUMNS[0],story.getStory_id());
        values.put(TABLE_STORY_LIKES_AND_VIEWS_COLUMNS[1],user.getUser_id());
        values.put(TABLE_STORY_LIKES_AND_VIEWS_COLUMNS[3],Story.STATE_VIEWED+"");
        //values.put(TABLE_STORY_LIKES_AND_VIEWS_COLUMNS[3],true);
        //return database.insert(TABLE_STORY_LIKES_AND_VIEWS,null,values) != -1;
        //Check If Values exist
        String check_query = "SELECT * FROM "+TABLE_STORY_LIKES_AND_VIEWS+" WHERE ";
        check_query += " story_id = "+story.getStory_id()+" AND ";
        check_query += " user_id = "+user.getUser_id();
        //check_query += " is_liked = 1";
        Cursor test_res = database.rawQuery(check_query,null);
        if (test_res.moveToFirst()){//Update
            //Toast.makeText(context,"Updated",Toast.LENGTH_LONG).show();
            String where = "story_id = "+story.getStory_id() + " AND user_id = "+user.getUser_id();
            int res = database.update(TABLE_STORY_LIKES_AND_VIEWS,values,where,null);
            return res !=-1;
        } else {//Insert
            //Toast.makeText(context,"Inserted",Toast.LENGTH_LONG).show();
            return database.insert(TABLE_STORY_LIKES_AND_VIEWS,null,values) != -1;
        }
    }
    //Called to check if user has liked story
    public boolean getLikedStatus (Story story, User user){
        SQLiteDatabase database = this.getReadableDatabase();
        String query = "SELECT is_liked FROM "+TABLE_STORY_LIKES_AND_VIEWS+" WHERE story_id = "+Integer.parseInt(story.getStory_id());
        query += " AND user_id = "+ Integer.parseInt(user.getUser_id())+" LIMIT 1";
        //Toast.makeText(this.context,query,Toast.LENGTH_LONG).show();
        Cursor res = database.rawQuery(query,null);
        if (res.moveToFirst()){
            return Boolean.parseBoolean(res.getString(res.getColumnIndex("is_liked")));
        }
        return false;
    }
    //Called When user Views a story at least once
    public boolean getViewedStatus (Story story, User user){
        SQLiteDatabase database = this.getReadableDatabase();
        String query = "SELECT is_viewed FROM "+TABLE_STORY_LIKES_AND_VIEWS+" WHERE story_id = "+story.getStory_id();
        query += " AND user_id = "+ user.getUser_id() +" LIMIT 1";
        //Toast.makeText(this.context,query,Toast.LENGTH_LONG).show();
        Cursor res = database.rawQuery(query,null);
        boolean is_viewed= false;
        if (res.moveToFirst())
            is_viewed = Boolean.parseBoolean(res.getString(res.getColumnIndex("is_viewed")));
        //Toast.makeText(this.context,is_viewed+"",Toast.LENGTH_LONG).show();
        return is_viewed;
    }

    //Returns column names of TABLE_STORY in HashMap Format
    private static HashMap<String,String> getTableStoryColumns () {
        HashMap<String,String> columns = new HashMap<>();
        columns.put("story_id","story_id");
        columns.put("story_title","story_title");
        columns.put("release_date","release_date");
        columns.put("num_likes","num_likes");
        columns.put("num_views","num_views");
        columns.put("payment_status","payment_status");
        return columns;
    }
    //Inserts details of a single Story in TABLE_STORY
    public void storeStory (Story story){
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TABLE_STORY_COLUMNS.get("story_id"),story.getStory_id());
        values.put(TABLE_STORY_COLUMNS.get("story_title"),story.getTitle());
        values.put(TABLE_STORY_COLUMNS.get("release_date"),story.getReleaseDate());
        values.put(TABLE_STORY_COLUMNS.get("num_likes"),story.getNumLikes());
        values.put(TABLE_STORY_COLUMNS.get("num_views"),story.getNumViews());
        values.put(TABLE_STORY_COLUMNS.get("payment_status"),story.getPaymentStatus());
        long res = database.insert(TABLE_STORY,null,values);
       // Toast.makeText(context,"Added = "+res,Toast.LENGTH_SHORT).show();
    }
    //Returns all stories in an ArrayList
    public ArrayList<Story> getStories () {
        ArrayList<Story> stories = new ArrayList<>();
        SQLiteDatabase database = this.getReadableDatabase();
        String stories_query = "SELECT * FROM "+TABLE_STORY;
        Cursor cursor = database.rawQuery(stories_query,null);



        if (cursor.moveToFirst()){
           // Toast.makeText(context,"Has data",Toast.LENGTH_SHORT).show();
            do {
                Story story = new Story();
                story.setStory_id(cursor.getString(cursor.getColumnIndexOrThrow(TABLE_STORY_COLUMNS.get("story_id"))));
                story.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(TABLE_STORY_COLUMNS.get("story_title"))));
                story.setReleaseDate(cursor.getString(cursor.getColumnIndexOrThrow(TABLE_STORY_COLUMNS.get("release_date"))));
                story.setNumLikes(Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow(TABLE_STORY_COLUMNS.get("num_likes")))));
                story.setNumViews(Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow(TABLE_STORY_COLUMNS.get("num_views")))));
                story.setPaymentStatus(cursor.getString(cursor.getColumnIndexOrThrow(TABLE_STORY_COLUMNS.get("payment_status"))));
                stories.add(story);
            } while (cursor.moveToNext());
        } cursor.close();

        return stories;
    }
    //Clears table TABLE_STORY
    public void clearTableStory () {
        String query = "DELETE  FROM  "+TABLE_STORY;
        SQLiteDatabase database = this.getWritableDatabase();
        database.execSQL(query);
    }

}
