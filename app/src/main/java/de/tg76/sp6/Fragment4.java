package de.tg76.sp6;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class Fragment4 extends Fragment {

    //Variable declaration
    TextView tvDescription;
    TextView tvName;
    ListView list;

    private UserLocalStore userLocalStore;
    int customer_idfk;


    //Creating ArrayList of type HashMap to store key & value pair
    ArrayList<HashMap<String, String>> oslist = new ArrayList<>();



    //URL to get JSON Array
    private static String url = "http://ec2-52-17-188-91.eu-west-1.compute.amazonaws.com/FetchFavorite.php";


    //JSON Node Names
    public static final String Key_ARRAY = "result";
    public static final String KEY_ID = "favorite_id";
    public static final String KEY_NAME = "carparkname";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_LATITUDE = "latitude";
    public static final String KEY_LONGITUDE = "longitude";


    //Creating JSONArray
    JSONArray capark = null;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_fragment4, container, false);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //Getting access to local store by creating an instance userLocalStore -----------------
        userLocalStore = new UserLocalStore(getActivity());
        User user = userLocalStore.getLoggedInUser();
        customer_idfk = user.customer_id;

        oslist = new ArrayList<>();
        Log.d("Fragment4", "onActivityCreated");

        new JSONParse().execute();

    }

    //Inner Class
    private class JSONParse extends AsyncTask<String, String, JSONObject> {

       String ncustomer_idfk = String.valueOf(customer_idfk) ;

        private ProgressDialog pDialog;

        //Instantiate object jParser from class
        JSONParserFavorite jParser = new JSONParserFavorite();

        //Variable for JSONObject
        private JSONObject json;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //list_view_favorite
            Log.d("Fragment4", "onPreExecute");
            //noinspection ConstantConditions
            tvName = (TextView) getView().findViewById(R.id.tvName);
            tvDescription = (TextView) getView().findViewById(R.id.tvDescription);

            //original place
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Getting Data ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();

        }//End onPreExecute

        @Override
        protected JSONObject doInBackground(String... args) {

            // Getting JSON from URL
            json = jParser.getJSONFromUrl(url, ncustomer_idfk);

            return json;
        }//End doInBackground

        @Override
        protected void onPostExecute(JSONObject json) {

            pDialog.dismiss();
            try {
                // Json objects in array
                capark = json.getJSONArray(Key_ARRAY);

                for (int i = 0; i < capark.length(); i++) {

                    JSONObject c = capark.getJSONObject(i); //Extract Json object from array

                    // Storing JSON item(value) in a String Variable
                    String id = c.getString(KEY_ID);
                    String name = c.getString(KEY_NAME);
                    String description = c.getString(KEY_DESCRIPTION);
                    String longitude = c.getString(KEY_LONGITUDE);
                    String latitude = c.getString(KEY_LATITUDE);


                    // Adding value HashMap key => value == CARPARKNAME => PARNELL
                    HashMap<String, String> map = new HashMap<>();
                    map.put(KEY_ID, id);
                    map.put(KEY_NAME, name);
                    map.put(KEY_DESCRIPTION, description);
                    map.put(KEY_LONGITUDE, longitude);
                    map.put(KEY_LATITUDE, latitude);

                    oslist.add(map);

                    //noinspection ConstantConditions Method invocation 'getView().findViewById(R.id.list)' at line 138 may produce 'java.lang.NullPointerException'
                    list = (ListView) getView().findViewById(R.id.list);

                    ListAdapter adapter = new SimpleAdapter(getActivity(), oslist,
                            R.layout.list_view_favorite_layout,
                            new String[]{KEY_NAME, KEY_DESCRIPTION}, new int[]{
                            R.id.tvName, R.id.tvDescription});
                    list.setAdapter(adapter);
                    list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Toast.makeText(getActivity(), "You Clicked at " + oslist.get(+position).get("CARPARKNAME"), Toast.LENGTH_SHORT).show();
                        }//onItemClick

                    });//setOnItemClickListener

                }//end of for loop

            }//end of try
            catch (JSONException e) {
                e.printStackTrace();
            }

        }//end of onPostExecute

    }//End AsyncTask
}//End listfragment
