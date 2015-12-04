package de.tg76.sp6;

import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

public class Fragment3 extends Fragment {

    //Co-ordinations for Dublin
    private static final double
            Dub_LAT = 53.346953,
            Dub_LNG = -6.264944;

    // private GoogleApiClient mLocationClient; may be remove

    GoogleMap mMap;
    private static final int ERROR_Dialog_REQUEST = 9001;
    //private FragmentActivity myContext; delete

    // private LatLng defaultLatLng = new LatLng(39.233956, -77.484703);
    // private int zoomLevel = 7;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // return inflater.inflate(R.layout.activity_fragment3, container, false);

        Log.d("Testing", "Checkpoint 1");
        //Call function
        if(servicesOK()){
            return inflater.inflate(R.layout.activity_map, container, false);

        }else{
            return inflater.inflate(R.layout.activity_fragment3, container, false);
        }

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        if(initMap()){

            //Call function
            gotoLocation(13);

            Log.d("Testing", "Fragment3 initMap");

            //Set icon my location
            mMap.setMyLocationEnabled(true);

            Log.d("Testing", "Fragment3 RetrieveTask called");
            // Starting locations retrieve task
            new RetrieveTask().execute();

            Toast.makeText(getActivity(), "Ready to map!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity(), "Map not connected!", Toast.LENGTH_SHORT).show();
        }

    }

    //Function to verify connection to GooglePlayService
    public boolean servicesOK(){

        int isAvailable = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity());

        if(isAvailable == ConnectionResult.SUCCESS){
            return true;
        }else if(GooglePlayServicesUtil.isUserRecoverableError(isAvailable)){
            Dialog dialog =
                    GooglePlayServicesUtil.getErrorDialog(isAvailable,getActivity(),ERROR_Dialog_REQUEST);
            dialog.show();
        }else{
            Toast.makeText(getActivity(), "Can't connect to mapping service", Toast.LENGTH_SHORT).show();
        }

        return false;
    }

    //Function to initiate GoogleMaps - return true or false
    private boolean initMap(){
        if(mMap == null){

            FragmentManager obj1 = getChildFragmentManager();
            SupportMapFragment obj2;
            obj2 = (SupportMapFragment) obj1.findFragmentById(R.id.map);
            if(obj2 != null) {
                mMap = obj2.getMap();
            }
        }
        return (mMap != null);
    }

    //Function which shows default location when map is called - Dublin
    private void gotoLocation(float zoom) {
        LatLng latLng = new LatLng(Fragment3.Dub_LAT, Fragment3.Dub_LNG);
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(latLng, zoom);
        mMap.moveCamera(update);
    }


    // Adding marker on GoogleMaps
    private void addMarker(LatLng latlng,String desc) {
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latlng);
        markerOptions.title(desc);
        mMap.addMarker(markerOptions);
    }

    // Background task to retrieve locations from remote mysql server
    private class RetrieveTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            Log.d("Testing", "RetrieveTask start");
            String strUrl = "http://ec2-52-17-188-91.eu-west-1.compute.amazonaws.com/FetchMap.php";
            URL url;
            StringBuilder sb = new StringBuilder();
            try {
                url = new URL(strUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream iStream = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(iStream));
                String line;
                while( (line = reader.readLine()) != null){
                    sb.append(line);
                }
                reader.close();
                iStream.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
            return sb.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            new ParserTask().execute(result);
            Log.d("Testing", "ParserTask called");
        }
    }

    // Background thread to parse the JSON data retrieved from MySQL server
    private class ParserTask extends AsyncTask<String, Void, List<HashMap<String, String>>>{
        @Override
        protected List<HashMap<String,String>> doInBackground(String... params) {
            Log.d("Testing", "MarkerJSONParser called");
            MarkerJSONParser markerParser = new MarkerJSONParser();
            JSONObject json = null;
            try {
                json = new JSONObject(params[0]);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return markerParser.parse(json);
        }

        @Override
        protected void onPostExecute(List<HashMap<String, String>> result) {
            Log.d("Testing", "last");
            for(int i=0; i<result.size();i++){
                HashMap<String, String> marker = result.get(i);
                String desc = marker.get("Description");
                LatLng latlng = new LatLng(Double.parseDouble(marker.get("Latitude")), Double.parseDouble(marker.get("Longitude")));
                addMarker(latlng, desc);
            }
        }
    }



    @Override
    public void onDetach() {
        Log.d("Testing", "Fragment3 onDetach");
        super.onDetach();
    }
}