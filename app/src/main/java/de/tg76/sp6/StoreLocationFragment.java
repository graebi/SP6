package de.tg76.sp6;

/*
 * Retrieve stored location and description
 * Thorsten Graebner D11123994
 */


import android.annotation.SuppressLint;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.util.ArrayList;

/**
 *
 * Created by adm_toto on 16/11/2015.
 */
public class StoreLocationFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{

    //Show loading bar when server request is executed
    //private final ProgressDialog progressDialog;


    //Connection time in mill sec before disconnect
    private static final int CONNECTION_TIME = 1000*15;
    private static final String SERVER_ADDRESS = "http://ec2-52-17-188-91.eu-west-1.compute.amazonaws.com/";
   // -- troubleshooting, delete
    // LogCat tag
    // private static final String TAG = MainActivity.class.getSimpleName();

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;

    private String description;
    private double latitude=0;
    private double longitude=0 ;
    private int customer_idfk;

    // Google client to interact with Google API
    private GoogleApiClient mGoogleApiClient;

    // boolean flag to toggle periodic location updates
    //private boolean mRequestingLocationUpdates = false;delete

   // private LocationRequest mLocationRequest;delete

    // Location updates intervals in sec
    private static int UPDATE_INTERVAL = 10000; // 10 sec
    private static int FATEST_INTERVAL = 5000; // 5 sec
    private static int DISPLACEMENT = 10; // 10 meters

    // UI elements
    private TextView lblLocation;
    private EditText edDescription;
    private UserLocalStore userLocalStore;


    @SuppressLint("InflateParams")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
       // return super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.activity_store_location, null);
        //Problem synopsis      Avoid passing null as the view root (needed to resolve layout parameters on the inflated layout's root element)

    }//End onCreateView

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        lblLocation = (TextView) getActivity().findViewById(R.id.lblLocation);
        Button btnShowLocation = (Button) getActivity().findViewById(R.id.btnShowLocation);
        edDescription = (EditText)getActivity().findViewById(R.id.edDescription);

        Log.d("Testing", "onActivityCreated - btnShowLocation called");

        // First we need to check availability of play services
        if (checkPlayServices()) {

            // Building the GoogleApi client
            buildGoogleApiClient();
        }

        //Getting access to local store by creating an instance userLocalStore -----------------
        userLocalStore = new UserLocalStore(getActivity());

        // Show location button click listener
        btnShowLocation.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                displayLocation();
                Log.d("Testing", "StoreLocationFragment call displayUserDetails function");
                displayUserDetails();
                AsyncT asyncT = new AsyncT();
                asyncT.execute();
                ((EditText) getActivity().findViewById(R.id.edDescription)).setText("");
            }
        });

    }//End onActivityCreated

    /**
     * Method to display the location on UI
     * */
    private void displayLocation() {

        description = edDescription.getText().toString();
        Location mLastLocation = LocationServices.FusedLocationApi
                .getLastLocation(mGoogleApiClient);

        if (mLastLocation != null) {
             latitude = mLastLocation.getLatitude();
             longitude = mLastLocation.getLongitude();

            //Write co-ordination on screen
            lblLocation.setText(latitude + ", " + longitude);

        } else {

            lblLocation
                    .setText("(Couldn't get the location. Make sure location is enabled on the device)");
        }
    }//End displayLocation


    //Creating google api client object
    private synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
    }//End buildGoogleApiClient


     //Method to verify google play services on the device
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(getActivity());
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, getActivity(),
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(getActivity().getApplicationContext(),
                        "This device is not supported.", Toast.LENGTH_LONG)
                        .show();
                getActivity().finish();
            }
            return false;
        }
        return true;
    }//End checkPlayServices

    @Override
    public void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }//End onStart

    @Override
    public void onResume() {
        super.onResume();

        checkPlayServices();
    }//End onResume

    /**
     * Google api callback methods
     */
    @Override
    public void onConnectionFailed(ConnectionResult result) {
        /*Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = "
                + result.getErrorCode());*/
    }//End onConnectionFailed -- troubleshooting, delete

    @Override
    public void onConnected(Bundle bundle) {

        // Once connected with google api, get the location
        displayLocation();
    }

    @Override
    public void onConnectionSuspended(int arg0) {
        mGoogleApiClient.connect();
    }//End onConnectionSuspended

    //Function to display user details on activity_main
    private void displayUserDetails(){
        Log.d("Testing", "StroeLocationFragnemt displayUserDetails function");
        User user = userLocalStore.getLoggedInUser();

        Log.d("Testing", "StroeLocationFragnemt user.customer_id");
        customer_idfk = user.customer_id;

    }//End displayUserDetails

    /* Inner class to get response */
    public class AsyncT extends AsyncTask<Void, Void, Void> {


        final String nlatitude = String.valueOf(latitude);
        final String nlongitude = String.valueOf(longitude);
        final String ncustomer_idfk = String.valueOf(customer_idfk) ;

            @Override
            //Run in the background when StoreUserDataAsyncTask starts - Accessing the server
            protected Void doInBackground(Void... params) {
                //Data which are send to server
                Log.d("Testing", "StroeLocationFragnemt doInBackground");
                ArrayList<NameValuePair> dataToSend = new ArrayList<>();
                dataToSend.add(new BasicNameValuePair("longitude", nlongitude));
                dataToSend.add(new BasicNameValuePair("latitude", nlatitude));
                dataToSend.add(new BasicNameValuePair("description", description));
                dataToSend.add(new BasicNameValuePair("customer_idfk", ncustomer_idfk));

                HttpParams httpRequestParams = new BasicHttpParams();
                //Time to wait before the post is executed
                HttpConnectionParams.setConnectionTimeout(httpRequestParams, CONNECTION_TIME);
                //Time to wait to receive anything from server
                HttpConnectionParams.setSoTimeout(httpRequestParams, CONNECTION_TIME);

                //Create client to establish HTTP connection + and make request to server
                HttpClient client = new DefaultHttpClient(httpRequestParams);
                HttpPost post = new HttpPost(SERVER_ADDRESS + "StoreCoordinates.php");

                try{
                    post.setEntity(new UrlEncodedFormEntity(dataToSend));
                    client.execute(post);
                }catch (Exception e){
                    e.printStackTrace();
                }
                return null;
            }
            @Override
            //When AsyncTask is finish
            protected void onPostExecute(Void aVoid) {
            }
        }

}//End main

