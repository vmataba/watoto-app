package welcome;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.watotoappgmail.watotoapp2.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import configuration.AppBroadCastReceiver;
import configuration.Device;
import configuration.app.ThisApp;
import user.Child;
import user.User;

/**
 * Created by victor on 10/10/2017.
 */

public class Registration extends android.support.v4.app.Fragment {

    /*For Registration*/
    private Spinner reg_country;
    private Button reg_btn;
    private TextView login_text_view;
    private EditText full_name;
    private EditText phone_number;
    private EditText email_address;
    private Spinner child_age;
    private RadioGroup child_sex;
    private RadioButton child_sex_male;
    private RadioButton child_sex_female;
    private TextView sex_selection_error_text_view;
    private EditText password_new;
    private EditText password_confirm;
    private ArrayList<String> child_ages;
    //For Application User
    private User user;
    private Child child;
    //For Api Call
    private JSONObject json;

    @SuppressLint("ClickableViewAccessibility")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab_registration_layout,container,false);
        //Dropdown List for Countries
        reg_country = (Spinner) rootView.findViewById(R.id.reg_country_id);
        //Get List of all countries in the world
        Locale[] locale = Locale.getAvailableLocales();
        ArrayList<String> countries = new ArrayList<String>();
        String country;
        for( Locale loc : locale ){
            country = loc.getDisplayCountry();
            if( country.length() > 0 && !countries.contains(country) ){
                countries.add( country );
            }
        }

        Collections.sort(countries, String.CASE_INSENSITIVE_ORDER);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),android.R.layout.simple_spinner_item, countries);
        reg_country.setAdapter(adapter);
        //Make Tanzania Default Country
        reg_country.setSelection(adapter.getPosition(User.DEFAULT_COUNTRY));
        //Registration button click event
        reg_btn = (Button) rootView.findViewById(R.id.reg_btn_id);
        //Redirect user into Login Tab
        login_text_view = (TextView)rootView.findViewById(R.id.welcome_tab_login_text_view_id);
        login_text_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StoryItemAdapter.InnerStory innerStory = new StoryItemAdapter.InnerStory(Registration.super.getContext());
                innerStory.getViewPager().setCurrentItem(1);
            }
        });

        //Initialize child_ages
        child_ages = new ArrayList<>();
        child_ages.add("Select child age");
        for (int i=3; i<=14; i++) {
            child_ages.add(i+"");
        }
        child_age = (Spinner) rootView.findViewById(R.id.reg_child_age_id);
        ArrayAdapter<String> child_age_adapter = new ArrayAdapter<String>(getContext(),android.R.layout.simple_spinner_dropdown_item,child_ages);
        //ChildAgeAdapter child_age_adapter = new ChildAgeAdapter(getContext(),child_ages);
        child_age.setAdapter(child_age_adapter);
        /*Other Initializations*/
        full_name = (EditText) rootView.findViewById(R.id.reg_user_name_id);
        //Remove any Error messages when the field is clicked
        full_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                full_name.setError(null);
            }
        });
        phone_number = (EditText) rootView.findViewById(R.id.reg_phone_number_id);
        email_address = (EditText) rootView.findViewById(R.id.reg_email_address_id);

        //child_birth_date_picker = (DatePicker) rootView.findViewById(R.id.reg_child_birth_date_picker_id);
        //btn_date_set = (ImageButton) rootView.findViewById(R.id.reg_date_set_id);
        child_sex = (RadioGroup) rootView.findViewById(R.id.reg_child_sex);
        sex_selection_error_text_view = (TextView)rootView.findViewById(R.id.reg_sex_selection_error_text_view);
        child_sex_male = (RadioButton) rootView.findViewById(R.id.reg_child_sex_male_id);
        child_sex_female = (RadioButton) rootView.findViewById(R.id.reg_child_sex_female_id);
        password_new = (EditText) rootView.findViewById(R.id.reg_user_password_id);
        password_confirm = (EditText) rootView.findViewById(R.id.reg_user_password_confirm_id);

        //Handle registration process
        reg_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!AppBroadCastReceiver.isConnected()){
                    showMessage(v,"OFFLINE");
                    return;
                }
                if (isValid()){//Everything is good to go
                    registerUser();
                }
            }
        });

        //Set Child Birthdate
        /*btn_date_set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setChild_birth_date();
            }
        });*/
        return rootView;
    }

    private HashMap<String,String> getUserDetails() {
        //Create new User
        user = new User();
        user.setName(full_name.getText().toString());
        user.setPhone_number(phone_number.getText().toString());
        if (isEmailSet())
            user.setEmail_address(email_address.getText().toString());
        else
            user.setEmail_address("");
        user.setCountry(reg_country.getSelectedItem().toString());
        user.setPassword(ThisApp.getMd5(password_new.getText().toString()));
        user.setNum_children(1);
        //Create Child
        child = new Child();
        child.setParent(user);
        child.setAge(Integer.parseInt(child_ages.get(child_age.getSelectedItemPosition())));
        String selected_sex = child_sex_male.isChecked()?Child.SEX_MALE:Child.SEX_FEMALE;
        child.setSex(selected_sex);
        HashMap<String, String> params = new HashMap<>();
        params.put("full_name", user.getName().replace(" ","%20")); //e.g Abel John
        params.put("mobile_number", user.getPhone_number()); // e.g 255652605256
        params.put("email_address", user.getEmail_address()); // e.g mtakuja@gmail.com
        params.put("password", user.getPassword());
        params.put("gender", child.getSex()); // e.g Male
        params.put("birth_date", child.getAge()+""); // e.g 10
        params.put("country", user.getCountry().replace(" ","%20")); // e.g. Tanzania
    return params;
    }
    //Registers user and returns true if the process is successfully
    private void registerUser () {
        RegAsyncTask regAsyncTask = new RegAsyncTask();
        regAsyncTask.execute(ThisApp.USER_REG_URL);
    }



    //Checks if User submitted an email
    private boolean isEmailSet () {
        return !TextUtils.isEmpty(email_address.getText().toString());
    }
    //Checks if child sex is set
    private boolean isChildSexSet (){
        return child_sex_male.isChecked() || child_sex_female.isChecked();
    }
    //Checks if password_new and password_confirm match
    private boolean passwordsMatch () {
        return password_new.getText().toString().equals(password_confirm.getText().toString());
    }
    //Checks if phone number is valid. If has 10 digits or 13 if starts with +255
    private boolean isPhoneNumberValid () {
        String phone = phone_number.getText().toString().trim();
        Pattern pattern = Pattern.compile("[0-9]");
        //Check if number has 10 or 13 digits only

        if (phone.charAt(0) == '0'){
            return phone.length() == 10;
        } else if (phone.charAt(0) == '+'){
            return phone.length() == 13;
        } else {
            return false;
        }


    }
    //Checks if Fullname is Valid
    private boolean isFullnameValid (){
        String name = full_name.getText().toString().trim();
        Pattern pattern = Pattern.compile("[^a-zA-Z' ']");
        Matcher matcher = pattern.matcher(name);
        return !matcher.find();
    }

    //Validates all the User Inputs
    private boolean isValid () {
        boolean valid = true;
        //Validate user full name
        if (TextUtils.isEmpty(full_name.getText().toString().trim())){
            full_name.setError(getString(R.string.reg_field_required_message));
            full_name.requestFocus();
            valid = false;
        } else if (!isFullnameValid()){ //Check if name is valid
            full_name.setError("Correct your name");
            full_name.requestFocus();
            valid = false;
        }  else if (TextUtils.isEmpty(phone_number.getText().toString().trim())) {
            phone_number.setError(getString(R.string.reg_field_required_message));
            phone_number.requestFocus();
            valid = false;
        } else if (!isPhoneNumberValid()){//Check if phone number is valid
            phone_number.setError("Correct your phone number");
            phone_number.requestFocus();
            valid = false;
        } else if (isEmailSet()){
            String email= email_address.getText().toString().trim();
            if (!(email.contains("@") && email.contains(".")) || (
                    email.endsWith("@") || email.endsWith(".") ||
                    email.startsWith("@") || email.startsWith("."))){

                email_address.setError(getString(R.string.error_invalid_email));
                email_address.requestFocus();
                valid = false;
            }
        } else if (child_age.getSelectedItemPosition() == 0){
            Toast.makeText(getContext(),"Child age is required",Toast.LENGTH_LONG).show();
            valid = false;
        } else if (!isChildSexSet()){
            sex_selection_error_text_view.setError(getString(R.string.reg_field_required_message));
            child_sex.requestFocus();
            Toast.makeText(getContext(),"Child Gender is required",Toast.LENGTH_LONG).show();
            valid = false;
        } else  if (TextUtils.isEmpty(password_new.getText().toString())){
            password_new.setError(getString(R.string.reg_field_required_message));
            password_new.requestFocus();
            valid = false;
        } else if (TextUtils.isEmpty(password_confirm.getText().toString().trim())){
            password_confirm.setError(getString(R.string.reg_field_required_message));
            password_confirm.requestFocus();
            valid = false;
        } else if (!passwordsMatch()){
            password_confirm.setError(getString(R.string.reg_passwords_mismatch_message));
            password_confirm.requestFocus();
            valid = false;
        }


        //Set no Error if email is not provided
        if (!isEmailSet()){
            email_address.setError(null);
        }
        //Set no Error if Gender is selected
        if (isChildSexSet()){
            sex_selection_error_text_view.setError(null);
        }
        return valid;
    }

    private void clearFields () {
        full_name.setText("");
        phone_number.setText("");
        email_address.setText("");
        child_age.setSelection(0);
        child_sex_male.setSelected(false);
        child_sex_female.setSelected(false);
        password_new.setText("");
        password_confirm.setText("");
    }

    /***Other methods that handle validation during values submission to the fields***/
    private void onFullNameKeyPress () {

    }

    private class RegAsyncTask extends AsyncTask<String, String, String> {

        private ProgressDialog dialog;
        private String finalResult;
        private String json_statement;
        private boolean status;
        //Details
        private String full_name;
        private String phone_number;
        private String email_address = "";
        private String child_gender;
        private String child_birthdate;
        private String country;
        private String password;

        @Override
        protected void onPreExecute() {
            status = false;
            //User details
            HashMap <String,String> details = getUserDetails();
            full_name = details.get("full_name");
            phone_number = details.get("mobile_number");
            email_address = details.get("email_address");
            country = details.get("country");
            password = details.get("password");
            child_gender = details.get("gender");
            child_birthdate = details.get("birth_date");
            //Dialog
            dialog = new ProgressDialog(getContext());
            dialog.setMessage("Creating user account...");
            dialog.show();
        }

        @Override
        protected String doInBackground(String... params) {

                        String url = params[0];
                        url+= "&_fullname="+full_name;
                        url+= "&_mobilenumber="+phone_number;
                        url+= "&_email="+email_address;
                        url+= "&_country="+country;
                        url+= "&_password="+password;
                        url+= "&_gender="+child_gender;
                        url+= "&_age="+child_birthdate;
                        finalResult = new ThisApp().post(2000, 2000, url,"");
                    if (finalResult.contains("Registration is Succesful"))
                        status = true;
            return finalResult;
        }
        @Override
        protected void onPostExecute(String s) {

            json_statement = s;
            try {
                JSONObject jsonObject = new JSONObject(json_statement);
                String message = jsonObject.get("Message").toString();
                Toast.makeText(getContext(),message,Toast.LENGTH_LONG).show();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //status = jsonObject.getBoolean("status");
            if (status) {
                Toast.makeText(getContext(),"Your account has been created!",Toast.LENGTH_LONG).show();
                //Clear all fields
                clearFields();
                //Navigate to Login Tab
                StoryItemAdapter.InnerStory innerStory = new StoryItemAdapter.InnerStory(getActivity());
                innerStory.getViewPager().setCurrentItem(1);

            } else {
                if(dialog.isShowing()) {
                    dialog.dismiss();
                }
               // Toast.makeText(getContext(),"Registration failed",Toast.LENGTH_LONG).show();
            }

            if(dialog.isShowing()) {
                dialog.dismiss();
            }
            super.onPostExecute(s);
        }
    }

    //Sets date to Date's Edit Text From DatePicker Widget
    /*private void setChild_birth_date () {
        //Get Year
        int year = child_birth_date_picker.getYear();
        //Get Month
        int month = child_birth_date_picker.getMonth() + 1;
        //Get day
        int day=  child_birth_date_picker.getDayOfMonth();
        //Create date
        String date = year+"-"+month+"-"+day;
        if (month <= 10)
            date = year+"-0"+month+"-"+day;
        if (day <10 )
            date = year+"-"+month+"-0"+day;
        //Display it on Date EditText
        child_birth_date.setText(date);
        //Remove any error message
        child_birth_date.setError(null);
    }*/

    //Displays message using SnackBar
    public void showMessage (View view, String message){
        Snackbar.make(view, message, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

}
