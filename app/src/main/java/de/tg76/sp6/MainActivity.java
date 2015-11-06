package de.tg76.sp6;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    //Variable to hold values from activity form
    private EditText etName;
    private EditText etEmail;
    private EditText etUsername;
    private UserLocalStore userLocalStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Assign ID to variable
        etName = (EditText) findViewById(R.id.etName);
        etEmail = (EditText) findViewById(R.id.etEmail);
        etUsername = (EditText) findViewById(R.id.etUsername);

        Button bLogout = (Button) findViewById(R.id.bLogout);

        //Creating listener for button bLogout
        bLogout.setOnClickListener(this);

        //Getting access to local store by creating an instance userLocalStore
        userLocalStore = new UserLocalStore(this);
    }

    //Anytime the main activity opens -> verify if user is logged on otherwise start Login.class
    @Override
    protected void onStart(){
        super.onStart();
        //Check if user is logged in => if so start displayUserDetails function otherwise start Login.class activity
        if(authentication()){
            displayUserDetails();
        }else{
            startActivity(new Intent(MainActivity.this,Login.class));
        }
    }

    //Check if user is logged in = true for logged on, false for not logged on
    private boolean authentication(){
        return userLocalStore.getUserLoggedIn();
    }

    //Function to display user details on activity_main
    private void displayUserDetails(){
        User user = userLocalStore.getLoggedInUser();

        etUsername.setText(user.username);
        etName.setText(user.name);
        etEmail.setText(user.email);
    }

    @Override
    public void onClick(View v) {
        //Check ID value
        switch (v.getId()){
            //By click on button logout
            case R.id.bLogout:
                userLocalStore.clearUserData();
                userLocalStore.setUserLoggedIn(false);

                //Original - changed 19/10
                //startActivity(new Intent(this,Login.class));
                //break;

                //New try
                startActivity(new Intent(this,SmartParking.class));
                break;
        }
    }
}
