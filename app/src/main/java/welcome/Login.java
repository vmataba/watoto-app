package welcome;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.watotoappgmail.watotoapp2.HomeActivity;
import com.watotoappgmail.watotoapp2.R;
import com.watotoappgmail.watotoapp2.WelcomeActivity;

import configuration.AppBroadCastReceiver;
import configuration.app.ThisApp;
import firebase.Database;
import local.UserDetails;
import user.Account;
import user.User;

/**
 * Created by victor on 10/10/2017.
 */

public class Login extends android.support.v4.app.Fragment {

    /*Related to UI*/
    private EditText user_name_edit_text;
    private EditText password_edit_text;
    private Button btn_sign_in;
    private TextView create_account_text_view;
    private ProgressBar login_progress_bar;
    /*For Current User*/
    private User user;
    private Account user_account;
    /*For FirebaseDatabase*/
    private Database database;
    /*For user reference*/
    DatabaseReference user_reference;
    /*For Current Context*/
    private Context context;
    /*Default Constructor*/
    public Login(){
    }
    /*Returns context*/
    public Context getContext(){
        return this.getActivity();
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab_login_layout,container,false);

        user_name_edit_text = (EditText) rootView.findViewById(R.id.user_name_id);
        password_edit_text = (EditText) rootView.findViewById(R.id.user_password_id);
        btn_sign_in = (Button)rootView.findViewById(R.id.btn_sign_in_id);
        login_progress_bar = (ProgressBar) rootView.findViewById(R.id.login_progress_bar_id);

        /*Start testing*/
        btn_sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!AppBroadCastReceiver.isConnected()){
                    showMessage(v,"OFFLINE");
                    return;
                }
                login();
            }
        });
        /*End testing*/

        create_account_text_view = (TextView)rootView.findViewById(R.id.welcome_tab_create_account_text_view_id);
        create_account_text_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StoryItemAdapter.InnerStory innerStory = new StoryItemAdapter.InnerStory(Login.super.getContext());
                innerStory.getViewPager().setCurrentItem(2);
            }
        });

        //Initializing database
        database = new Database(getContext());
        //Initializing user_reference
        user_reference = database.getUserReference();
        //Initialize user particulars
        user = new User();
        user_account = new Account();

        return rootView;
    }

    private boolean isUsernameValid () {
        //Submitted username
        final String input_user_name = user_name_edit_text.getText().toString();
        //If username is not supplied
        if (TextUtils.isEmpty(input_user_name)){
            user_name_edit_text.setError(getString(R.string.error_field_required));
            return false;
        }
        return true;
    }

    private boolean isPasswordValid () {
        String input_password = password_edit_text.getText().toString();
        if (TextUtils.isEmpty(input_password)){
            password_edit_text.setError(getString(R.string.error_field_required));
        }
        return true;
    }

    private void login () {
        final String error_message = "Incorrect username or password";
        if (isUsernameValid() && isPasswordValid()) {
            //Checks if Supplied user_name exists
            user_reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Iterable<DataSnapshot> user_snaps = dataSnapshot.getChildren();
                    for (DataSnapshot snapshot:user_snaps){

                        if (snapshot.child(Database.USER_MOBILE_NUMBER).getValue().toString().equals(user_name_edit_text.getText().toString())){
                            /*For user*/
                            user.setUser_id(snapshot.getKey());
                            user.setName(snapshot.child(Database.USER_FULL_NAME).getValue().toString());
                            user.setPhone_number(snapshot.child(Database.USER_MOBILE_NUMBER).getValue().toString());
                            user.setUser_name();
                            user.setPassword(snapshot.child(Database.USER_PASSWORD).getValue().toString());
                            /*For user account*/
                            user_account.setUser(user);
                            user_account.setAccount_number(snapshot.child(Database.USER_ACC_NUMBER).getValue().toString());
                            user_account.setAccount_name(user.getName());
                            user_account.setBalance(Double.parseDouble(snapshot.child(Database.USER_ACC_BALANCE).getValue().toString()));
                            user_account.setExpire_date(snapshot.child(Database.USER_ACC_SUB_EXP_DATE).getValue().toString());
                            user_account.setBundle_type(snapshot.child(Database.USER_BUNDLE_TYPE).getValue().toString());

                            if (ThisApp.getMd5(password_edit_text.getText().toString()).equals(user.getPassword())){//Allow this user to Login
                                //Store Details of this User
                                UserDetails details = new UserDetails(getContext());
                                details.storeUserAccount(user_account);
                                //Done Storing
                                //Create Intent to HomeActivity
                                Intent intent = new Intent(getContext(), HomeActivity.class);
                                intent.putExtra("user_account",user_account);
                                startActivity(intent);
                                ((Activity)getContext()).finish();
                                //ThisApp.checkFirstRun(getContext(),user_account);
                                //Done ...
                            } else {
                                //Toast.makeText(getContext(),"Error Comes for Here",Toast.LENGTH_LONG).show();
                                user_name_edit_text.setError(error_message);
                                password_edit_text.setError(error_message);
                                return;
                            }
                            return;
                        } else {//Supplied Username is incorrect
                            login_progress_bar.setVisibility(View.VISIBLE);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    user_name_edit_text.setError(error_message);
                                    password_edit_text.setError(error_message);
                                    login_progress_bar.setVisibility(View.INVISIBLE);
                                }
                            },5000);
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }
    //Displays message using SnackBar
    public void showMessage (View view, String message){
        Snackbar.make(view, message, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }
}
