package com.watotoappgmail.watotoapp2;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.TransferListener;
import com.google.android.exoplayer2.util.Util;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import dyanamitechetan.vusikview.VusikView;
import local.UserDetails;
import media.StoryAdapter;
import story.Story;
import user.User;

public class MediaActivity extends AppCompatActivity {

    /****Class Variables****/
    private Story story;
    private RecyclerView story_recycler_view;
    private StoryAdapter story_adapter;
    private ArrayList<Story> storyList;
    private ArrayList<Story> regular_list;
    private ProgressBar progressBar;
    /*For ExoPlayer*/
    private Handler main_handler;
    private BandwidthMeter bandwith_meter;
    private SimpleExoPlayer player;
    private SimpleExoPlayerView media_exo_player_view;
    /*Other*/
    private boolean playWhenReady;
    private int currentWindow;
    private long  playbackPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media);
        //ProgressBar Initialization
        progressBar = (ProgressBar)findViewById(R.id.media_progress_bar_id);
        //Initializing story Object brought by an Intent
        story = (Story)getIntent().getExtras().getSerializable("story");
        //Get Regular story list
        regular_list = new ArrayList<Story>();
        //Populate Items in a list
        regular_list.addAll((ArrayList<Story>)getIntent().getSerializableExtra("regular_list"));
        //Changing Toolbar's title to Story's title
        getSupportActionBar().setTitle(story.getTitle());
        //Display back arrow on toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        //Initializing story_recycler_view Object, story_adapter and connecting them up!
        story_recycler_view = (RecyclerView) findViewById(R.id.media_player_recycler_view_id);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        story_recycler_view.setLayoutManager(layoutManager);
        //Keeps story PlayList
        storyList = new ArrayList<Story>();
        //Add First the selected story from Home Screen
        storyList.add(story);

        story_adapter = new StoryAdapter(regular_list,this);
        story_recycler_view.setAdapter(story_adapter);

        //media_vusik_view.start();

        /*ExoPlayer Section*/
        main_handler = new Handler();

        //SimpleExoPlayer exoPlayer = ExoPlayerFactory.newSimpleInstance(MediaActivity.this,selector);
        media_exo_player_view = (SimpleExoPlayerView)findViewById(R.id.media_exo_player_id);


    }

    @Override
    public void onStart() {
        super.onStart();
        if (Util.SDK_INT > 23) {
            initializePlayer();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
       // hideSystemUi();
        if ((Util.SDK_INT <= 23 || player == null)) {
            initializePlayer();
        }
    }

    @SuppressLint("InlinedApi")
    private void hideSystemUi() {
        media_exo_player_view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        );
    }

    @Override
    public void onPause() {
        super.onPause();
        if (Util.SDK_INT <= 23) {
            releasePlayer();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT > 23) {
            releasePlayer();
        }
    }

    //Initializes the Media Player
    private void initializePlayer () {
        //Initializes the Exoplayer Instance
        player = ExoPlayerFactory.newSimpleInstance(new DefaultRenderersFactory(this),
                new DefaultTrackSelector(),new DefaultLoadControl());
        //Attaching player to the View
        media_exo_player_view.setPlayer(player);
        //Allow playback when player is ready
        player.setPlayWhenReady(playWhenReady);
        //Control seeking events
        player.seekTo(currentWindow, playbackPosition);
        //Create Uri of the User selected story from the List
        Uri uri = Uri.parse(story.getDownload_url());
        //Create MediaSource for the selected story
        final MediaSource mediaSource = buildMediaSource(uri);
        //Make an Empty List for rest of stories in the List
        final ArrayList<MediaSource> mediaSources = new ArrayList<MediaSource>();
        //Add uri for the  firstly selected song
       // mediaSources.add(buildMediaSource(uri));
        //Populate the List
        for (Story story:regular_list){
            mediaSources.add(buildMediaSource(Uri.parse(story.getDownload_url())));
        }
        //Convert the List into an array and pass it as an argument
        final ConcatenatingMediaSource concatenatingMediaSource = new ConcatenatingMediaSource(mediaSources.toArray(new MediaSource[mediaSources.size()]));
        //Set things up!..
        player.prepare(concatenatingMediaSource, true, false);
        //Done
        player.addListener(new Player.EventListener() {
            @Override
            public void onTimelineChanged(Timeline timeline, Object manifest) {

            }

            @Override
            public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
                //Get Index of Current Story in PlayList
                int curr_story_index = player.getCurrentPeriodIndex();
                //Get the story
                Story curr_story = regular_list.get(curr_story_index);
                //Change Toolbar's Title
                getSupportActionBar().setTitle(curr_story.getTitle());

                //Send Updates to the server
                if (User.isLoggedIn(MediaActivity.this)){
                    curr_story.updateNumViews(MediaActivity.this,null);
                    //Add curr_story into Viewed stories
                    if (curr_story_index != 0)//First Story in List has already been Viewed
                        curr_story.setViewed(MediaActivity.this);
                } else {
                    if (curr_story_index != 0)
                    curr_story.updateNumViewsForUnregUsers(MediaActivity.this,null);

                }

            }

            @Override
            public void onLoadingChanged(boolean isLoading) {
                if (isLoading){//Show Progress Bar
                    progressBar.setVisibility(View.VISIBLE);
                } else {//Hide Progress Bar
                    progressBar.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

            }

            @Override
            public void onRepeatModeChanged(int repeatMode) {

            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {

            }

            @Override
            public void onPositionDiscontinuity() {

            }

            @Override
            public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

            }
        });
    }

    //Sets Media Source to the Player
    private MediaSource buildMediaSource(Uri uri) {
        return new ExtractorMediaSource(uri,
                new DefaultHttpDataSourceFactory("WatotoApp"),
                new DefaultExtractorsFactory(), null, null);
    }
    //Called to release player
    private void releasePlayer() {
        if (player != null) {
            playbackPosition = player.getCurrentPosition();
            currentWindow = player.getCurrentWindowIndex();
            playWhenReady = player.getPlayWhenReady();
            player.release();
            player = null;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int item_id = item.getItemId();
        if (item_id == android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

}
