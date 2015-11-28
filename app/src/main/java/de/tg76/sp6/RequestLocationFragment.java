package de.tg76.sp6;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by adm_toto on 16/11/2015.
 */
public class RequestLocationFragment extends Fragment {

    //Connection time in mill sec before disconnect
    //Connection time in mill sec before disconnect
    private static final int CONNECTION_TIME = 1000*15;
    private static final String SERVER_ADDRESS = "http://ec2-52-17-188-91.eu-west-1.compute.amazonaws.com/";


    //Local variable
    private UserLocalStore userLocalStore;
    int customer_idfk;
    String customer;
    double latitude=0,longitude=0 ;
    String description;

    //UI elements
    private TextView tvLocationdetails;
    private TextView tvDescription;
    private Button btnGetLocation;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
       // return super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.activity_request_location, null);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //Getting access to local store by creating an instance userLocalStore -----------------
        userLocalStore = new UserLocalStore(getActivity());

        tvLocationdetails = (TextView) getActivity().findViewById(R.id.tvLocationdetails);
        tvDescription = (TextView) getActivity().findViewById(R.id.tvDescription);
        btnGetLocation = (Button) getActivity().findViewById(R.id.btnGetLocation);

        // Show location button click listener
        btnGetLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Testing", "RequestLocationFragment onClick");
                displayUserDetails();
                AsyncT asyncT = new AsyncT();
                asyncT.execute();

            }//End onClick
        });
    }//End onActivityCreated

    //Function to display user details on activity_main
    private void displayUserDetails(){
        Log.d("Testing", "StroeLocationFragnemt displayUserDetails function");
        User user = userLocalStore.getLoggedInUser();

        Log.d("Testing", "StroeLocationFragnemt user.customer_id");
        customer_idfk = user.customer_id;

    }//End displayUserDetails


    /* Inner class to get response */
    public class AsyncT extends AsyncTask<Void, Void, Void> {


      //  String nlatitude = String.valueOf(latitude);
      //  String nlongitude = String.valueOf(longitude);
        String ncustomer_idfk = String.valueOf(customer_idfk) ;

        @Override
        //Run in the background when StoreUserDataAsyncTask starts - Accessing the server
        protected Void doInBackground(Void... params) {
            //Data which are send to server
            ArrayList<NameValuePair> dataToSend = new ArrayList<>();
            dataToSend.add(new BasicNameValuePair("customer_idfk", ncustomer_idfk));

            HttpParams httpRequestParams = new BasicHttpParams();
            //Time to wait before the post is executed
            HttpConnectionParams.setConnectionTimeout(httpRequestParams, CONNECTION_TIME);
            //Time to wait to receive anything from server
            HttpConnectionParams.setSoTimeout(httpRequestParams, CONNECTION_TIME);

            //Create client to establish HTTP connection + and make request to server
            HttpClient client = new DefaultHttpClient(httpRequestParams);
            HttpPost post = new HttpPost(SERVER_ADDRESS + "FetchStoredLocation.php");

            try{
                post.setEntity(new UrlEncodedFormEntity(dataToSend));
                HttpResponse httpResponse = client.execute(post);

                //Get the data from the HTTP response
                HttpEntity entity = httpResponse.getEntity();
                //Convert data to string
                String result = EntityUtils.toString(entity);
                //jObject holds the user data
                JSONObject jObject = new JSONObject(result);

                if(jObject.length()==0){
                    Toast.makeText(getActivity(),"No object retrieved from database",Toast.LENGTH_LONG).show();
                }else {
                    longitude = jObject.getDouble("longitude");
                    latitude = jObject.getDouble("latitude");
                    description = jObject.getString("description");

                    //int customer_id = jObject.getInt();
                }

            }catch (Exception e){
                e.printStackTrace();
            }

            return null;
        }
        @Override
        //When AsyncTask is finish
        protected void onPostExecute(Void aVoid) {
            setText();
        }

    }
    public void setText(){
        tvLocationdetails.setText(latitude + ", " + longitude);
        tvDescription.setText(description);
    }
}
