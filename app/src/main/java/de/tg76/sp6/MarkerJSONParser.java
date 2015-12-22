package de.tg76.sp6;

/*
 * Retrieve car park object form JSON array
 * Thorsten Graebner D11123994
 */


import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MarkerJSONParser {

    /** Receives a JSONObject and returns a list */
    public List<HashMap<String,String>> parse(JSONObject jObject){

        Log.d("Testing", "MarkerJSONParser start");
        JSONArray jMarkers = null;
        try {
            /** Retrieves all the elements in the 'markers' array */
            jMarkers = jObject.getJSONArray("markers");
            Log.d("Testing", "Retrieves all the elements in the 'markers' array");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        /** Invoking getMarkers with the array of json object
         * where each json object represent a marker
         */
     //   Log.d("Testing", "getMarkers call");
        return getMarkers(jMarkers);
    }

    private List<HashMap<String, String>> getMarkers(JSONArray jMarkers){
      //  Log.d("Testing", "getMarkers start");
        int markersCount = jMarkers.length();
        List<HashMap<String, String>> markersList = new ArrayList<>();
        HashMap<String, String> marker;

        /** Taking each marker, parses and adds to list object */
        for(int i=0; i<markersCount;i++){
            try {
                /** Call getMarker with marker JSON object to parse the marker */
        //        Log.d("Testing", "Call getMarker");
                marker = getMarker((JSONObject)jMarkers.get(i));
                markersList.add(marker);
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
     //   Log.d("Testing", "getMarkers return");
        return markersList;
    }

    /** Parsing the Marker JSON object */
    private HashMap<String, String> getMarker(JSONObject jMarker){
       // Log.d("Testing", "getMarker start");
        HashMap<String, String> marker = new HashMap<>();
        String lat = "Latitude";
        String lng = "Longitude";
        String desc = "Description";

        try {
        //    Log.d("Testing", "Extracting latitude, if available");
            // Extracting latitude, if available
            if(!jMarker.isNull("Latitude")){
                lat = jMarker.getString("Latitude");
            }

        //    Log.d("Testing", "Extracting longitude, if available");
            // Extracting longitude, if available
            if(!jMarker.isNull("Longitude")){
                lng = jMarker.getString("Longitude");
            }

        //    Log.d("Testing", "Extracting Description, if available");
            // Extracting longitude, if available
            if(!jMarker.isNull("Description")){
                desc = jMarker.getString("Description");
            }

            marker.put("Latitude", lat);
            marker.put("Longitude", lng);
            marker.put("Description", desc);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return marker;
    }
}