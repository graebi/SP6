package de.tg76.sp6;

/*
 * Fragment1 - Displays live car park data on fragment 1 
 * Thorsten Graebner
 */

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class Fragment1 extends Fragment {

    private static int customer_idfk;
    private static int intCarparkID;
    public static String ncustomer_idfk ;
    public static String carparkID ;



    //Creating ArrayList of type HashMap to store key & value pair
    private ArrayList<HashMap<String, String>> oslist = new ArrayList<>();

    //Connection time in mill sec before disconnect
    private static final int CONNECTION_TIME = 1000*15;
    private static final String SERVER_ADDRESS = "http://ec2-52-17-188-91.eu-west-1.compute.amazonaws.com/";

    //private static String url1 = "http://ec2-52-17-188-91.eu-west-1.compute.amazonaws.com/StoreFavorite.php";

    //JSON Node Names
    private static final String Key_ARRAY = "result";
    private static final String KEY_ID = "CARPARK_ID";
    private static final String KEY_NAME = "CARPARKNAME";
    private static final String KEY_SPACES = "SPACE";

    //Creating JSONArray
    private JSONArray capark = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_fragment1, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //Getting access to local store by creating an instance userLocalStore -----------------
        UserLocalStore userLocalStore = new UserLocalStore(getActivity());
        User user = userLocalStore.getLoggedInUser();
        customer_idfk = user.customer_id;//Get user id

        //Initializing JSONArray
        oslist = new ArrayList<>();
        new JSONParse().execute();

        //noinspection ConstantConditions  Method invocation 'getView().findViewById(R.id.buttonGet)' at line 69 may produce 'java.lang.NullPointerException'
        Button buttonGet = (Button) getView().findViewById(R.id.buttonGet);
        buttonGet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //new JSONParse().execute();
                Intent intent = getActivity().getIntent();
                getActivity().finish();
                startActivity(intent);
            }
        });
    }

    //Inner Class
    private class JSONParse extends AsyncTask<String, String, JSONObject> {

        private ProgressDialog pDialog;

        //Instantiate object jParser from class
        final JSONParser jParser = new JSONParser();

        //Variable for JSONObject
        private JSONObject json;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //noinspection ConstantConditions Method invocation 'getView().findViewById(R.id.id)' at line 92 may produce 'java.lang.NullPointerException'
            //TextView id = (TextView) getView().findViewById(R.id.id); delete
           // TextView name = (TextView) getView().findViewById(R.id.name);delete
            //TextView spaces = (TextView) getView().findViewById(R.id.spaces);delete

            //original place
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Getting Data C-Park...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... args) {
            String url = "http://ec2-52-17-188-91.eu-west-1.compute.amazonaws.com/FetchCarPark.php";
            json = jParser.getJSONFromUrl(url);

            return json;
        }//End doInBackground

        @Override
        protected void onPostExecute(JSONObject json) {

            pDialog.dismiss();
            try {
                // Json objects in array - object 0 = (CARPARK_ID:1, CARPARKNAME:Parnell, SPACE:184)
                capark = json.getJSONArray(Key_ARRAY);

                for (int i = 0; i < capark.length(); i++) {

                    JSONObject c = capark.getJSONObject(i); //Extract Json object from array

                    // Storing JSON item(value) in a String Variable
                    String id = c.getString(KEY_ID);
                    String name = c.getString(KEY_NAME);
                    String spaces = c.getString(KEY_SPACES);

                    // Adding value HashMap key => value == CARPARKNAME => PARNELL
                    HashMap<String, String> map = new HashMap<>();
                    map.put(KEY_ID, id);
                    map.put(KEY_NAME, name);
                    map.put(KEY_SPACES, spaces);

                    oslist.add(map);

                    //noinspection ConstantConditions Method invocation 'getView().findViewById(R.id.list)' at line 138 may produce 'java.lang.NullPointerException'
                    ListView list = (ListView) getView().findViewById(R.id.list);

                    ListAdapter adapter = new SimpleAdapter(getActivity(), oslist,
                            R.layout.list_view_layout,
                            new String[]{KEY_ID, KEY_NAME, KEY_SPACES}, new int[]{
                            R.id.id, R.id.name, R.id.spaces});
                    list.setAdapter(adapter);

                    list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                        @Override
                        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                            intCarparkID = position + 1;
                            carparkID = Integer.toString(intCarparkID);
                            ncustomer_idfk = String.valueOf(customer_idfk);
                            Toast.makeText(getActivity(), "Storing carpark " + oslist.get(+position).get("CARPARKNAME"), Toast.LENGTH_LONG).show();

                            AddDeleteFavorite asyncT = new AddDeleteFavorite();
                            AddDeleteFavorite.favoriteAdd = true;
                            asyncT.execute();

                            return true;
                        }//End onItemLongClick
                    });//End setOnItemLongClickListener

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


}//changing on tabs-working