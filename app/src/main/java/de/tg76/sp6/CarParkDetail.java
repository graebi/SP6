package de.tg76.sp6;

/*
 * Thorsten Graebner D11123994
 * Retrieve car park details
 */

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
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
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class CarParkDetail extends AppCompatActivity {

    //Creating ArrayList of type HashMap to store key & value pair
    private ArrayList<HashMap<String, String>> oslist = new ArrayList<>();

    //Connection time in mill sec before disconnect
    private static final int CONNECTION_TIME = 1000 * 15;
    private static final String SERVER_ADDRESS = "http://ec2-52-17-188-91.eu-west-1.compute.amazonaws.com/";

    String cparkID;

    /*  //JSON Node Names
      private static final String Key_ARRAY = "result";
      private static final String KEY_ID = "CARPARK_ID";
      private static final String KEY_NAME = "CARPARKNAME";
      private static final String KEY_STREET = "STREET";
      private static final String KEY_PHONE = "PHONE";
      private static final String KEY_TOTALSPACE = "TOTALSPACE";
      private static final String KEY_SPACES = "SPACE";
      private static final String KEY_RATES = "RATES";
      private static final String KEY_LATITUDE = "LATITUDE";
      private static final String KEY_LONGITUDE = "LONGITUDE";
  */
    private int CARPARK_ID;
    private String CARPARKNAME;
    private double LATITUDE;
    private double LONGITUDE;
    private String SPACE;
    private String TOTALSPACE;
    private String DESCRIPTION;
    private String STREET;
    private String PHONE;
    private String RATES;

    private TextView CarParkNameDetailResult;
    private TextView StreetDetailResult;
    private TextView PhoneDetailResult;
    private TextView TotalSpaceDetailResult;
    private TextView tvRatesResult;
    private TextView tvLatitudeResult;
    private TextView tvLongitudeResult;
    private TextView SpaceDetailResult;


    //Creating JSONArray
    private JSONArray capark = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_park_detail);

        CarParkNameDetailResult = (TextView) findViewById(R.id.CarParkNameDetailResult);
        StreetDetailResult = (TextView) findViewById(R.id.StreetDetailResult);
        PhoneDetailResult = (TextView) findViewById(R.id.PhoneDetailResult);
        TotalSpaceDetailResult = (TextView) findViewById(R.id.TotalSpaceDetailResult);
        tvRatesResult = (TextView) findViewById(R.id.tvRatesResult);
        tvLatitudeResult = (TextView) findViewById(R.id.tvLatitudeResult);
        tvLongitudeResult = (TextView) findViewById(R.id.tvLongitudeResult);
        SpaceDetailResult = (TextView) findViewById(R.id.SpaceDetailResult);

        Button buttonGet = (Button) findViewById(R.id.buttonNavigate);
        buttonGet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //start navigation from google
                navigateTo();

            }//End onClick
        });//End setOnClickListener

        //Initializing JSONArray
        Fragment1 getCarParkID = new Fragment1();
        cparkID = getCarParkID.carparkID;
        AsyncT asyncT = new AsyncT();
        asyncT.execute();

    }//EndonCreate

    /* Inner class to get response */
    public class AsyncT extends AsyncTask<Void, Void, Void> {

        @Override
        //Run in the background when StoreUserDataAsyncTask starts - Accessing the server
        protected Void doInBackground(Void... params) {
            //Data which are send to server
            ArrayList<NameValuePair> dataToSend = new ArrayList<>();
            dataToSend.add(new BasicNameValuePair("CARPARK_ID", cparkID));

            HttpParams httpRequestParams = new BasicHttpParams();
            //Time to wait before the post is executed
            HttpConnectionParams.setConnectionTimeout(httpRequestParams, CONNECTION_TIME);
            //Time to wait to receive anything from server
            HttpConnectionParams.setSoTimeout(httpRequestParams, CONNECTION_TIME);

            //Create client to establish HTTP connection + and make request to server
            HttpClient client = new DefaultHttpClient(httpRequestParams);
            HttpPost post = new HttpPost(SERVER_ADDRESS + "FetchCarParkDetail1.php");

            try {
                post.setEntity(new UrlEncodedFormEntity(dataToSend));
                HttpResponse httpResponse = client.execute(post);

                //Get the data from the HTTP response
                HttpEntity entity = httpResponse.getEntity();
                //Convert data to string
                String result = EntityUtils.toString(entity);
                //jObject holds the user data
                JSONObject jObject = new JSONObject(result);

                if (jObject.length() == 0) {

                    Toast.makeText(CarParkDetail.this, "No object retrieved from database", Toast.LENGTH_LONG).show();
                } else {
                    CARPARK_ID = jObject.getInt("CARPARK_ID");
                    CARPARKNAME = jObject.getString("CARPARKNAME");
                    LATITUDE = jObject.getDouble("LATITUDE");
                    LONGITUDE = jObject.getDouble("LONGITUDE");
                    SPACE = jObject.getString("SPACE");
                    TOTALSPACE = jObject.getString("TOTALSPACE");
                    DESCRIPTION = jObject.getString("DESCRIPTION");
                    STREET = jObject.getString("STREET");
                    PHONE = jObject.getString("PHONE");
                    RATES = jObject.getString("RATES");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        //When AsyncTask is finish
        protected void onPostExecute(Void aVoid) {
            setText();
        }

        //Display co-ordinates on fragment
        private void setText() {
            CarParkNameDetailResult.setText(CARPARKNAME);
            StreetDetailResult.setText(STREET);
            PhoneDetailResult.setText(PHONE);
            TotalSpaceDetailResult.setText(TOTALSPACE);
            tvRatesResult.setText(RATES);
            tvLatitudeResult.setText(Double.toString(LATITUDE));
            tvLongitudeResult.setText(Double.toString(LONGITUDE));
            SpaceDetailResult.setText(SPACE);
        }
    }
    //Navigate to stored location
    private void navigateTo(){
        String url = "http://maps.google.com/maps?f=d&daddr=" + LATITUDE + "," + LONGITUDE + "&dirflg=d&layer=t";
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(url));
        intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
        startActivity(intent);
    }
}
