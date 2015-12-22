package de.tg76.sp6;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Thorsten Graebner D11123994
 * This class stores user data into a file locally
 */
class UserLocalStore {

    //Static variable for holding user data
    private static final String SP_NAME = "userDetails";
    private final SharedPreferences userLocalDatabase; //allows to store data on the phone

    //Constructor
    //Get context of current state of the SP_NAME file which stores the user details
    public  UserLocalStore(Context context){
        userLocalDatabase = context.getSharedPreferences(SP_NAME,0);
    }

    //Function to update SharedPreference user data
    public void storeUserData(User user){
        SharedPreferences.Editor spEditor = userLocalDatabase.edit();
        spEditor.putInt("customer_id", user.customer_id);
        spEditor.putString("name",user.name);
        spEditor.putString("email",user.email);
        spEditor.putString("username",user.username);
        spEditor.putString("password",user.password);
        spEditor.apply();
    }

    //Function retrieving attributes of user which is stored on the local database
    public  User getLoggedInUser(){
        int customer_id = userLocalDatabase.getInt("customer_id", -1);
        String name = userLocalDatabase. getString("name", "");
        String email = userLocalDatabase.getString("email", "");
        String username = userLocalDatabase.getString("username", "");
        String password = userLocalDatabase.getString("password","");

        //Creating new user and return
        return new User(customer_id,name, email,username,password );
    }

    //Function to set loggedIn if user is logged on to the mobile
    public void setUserLoggedIn(boolean loggedIn){
        SharedPreferences.Editor spEditor = userLocalDatabase.edit();
        spEditor.putBoolean("loggedIn",loggedIn);
        spEditor.apply();
    }

    //Function if user is logged in or out to application
    public boolean getUserLoggedIn(){
        //Set default loggedIn value to false
        return userLocalDatabase.getBoolean("loggedIn", false);
    }
    //Function to clear user data on SharedPreferences
    public void clearUserData(){
        SharedPreferences.Editor spEditor = userLocalDatabase.edit();
        spEditor.clear();
        spEditor.apply();
    }

}
