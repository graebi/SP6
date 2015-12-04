package de.tg76.sp6;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

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
 * Class is used to push and retrieve data from and to DB
 */
class ServerRequests {

    //Show loading bar when server request is executed
    private final ProgressDialog progressDialog;
    //Connection time in mill sec before disconnect
    private static final int CONNECTION_TIME = 1000*15;
    private static final String SERVER_ADDRESS = "http://ec2-52-17-188-91.eu-west-1.compute.amazonaws.com/";
    int customer_id=0;

    //Constructor
    public ServerRequests(Context context){
        //Instantiate dialog progress
        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Processing");
        progressDialog.setMessage("Please wait...");
    }

    //Storing user to DB
    public void storeUserDataInBackground(User user, GetUserCallback userCallback){
        //Start progress bar
        progressDialog.show();
        new StoreUserDataAsyncTask(user, userCallback).execute();
    }

    //Fetching user from DB
    public void fetchUserDataInBackground(User user, GetUserCallback callback){
        //Start progress bar
        progressDialog.show();
        new fetchUserDataAsyncTask(user, callback).execute();
    }

    //Background task - first void means we are not sending anything to the AsyncTask we use
    //the constructor for that - the second void is how do we want to receive the progress
    //from the AsyncTask - just starting the progressDialog - the third void is what the
    //AsyncTask should return
    public class StoreUserDataAsyncTask extends AsyncTask<Void,Void, Void> {

        //Holds user data
        final User user;
        //Interface GetUserCallback - inform when finishing background task
        final GetUserCallback userCallback;

        //Constructor
        public StoreUserDataAsyncTask(User user, GetUserCallback userCallback){
            this.user = user;
            this.userCallback = userCallback;
        }

        @Override
        //Run in the background when StoreUserDataAsyncTask starts - Accessing the server
        protected Void doInBackground(Void... params) {
            //Data which are send to server
            ArrayList<NameValuePair> dataToSend = new ArrayList<>();
            dataToSend.add(new BasicNameValuePair("name", user.name));
            dataToSend.add(new BasicNameValuePair("username", user.username));
            dataToSend.add(new BasicNameValuePair("password", user.password));
            dataToSend.add(new BasicNameValuePair("email", user.email));

            HttpParams httpRequestParams = new BasicHttpParams();
            //Time to wait before the post is executed
            HttpConnectionParams.setConnectionTimeout(httpRequestParams, CONNECTION_TIME);
            //Time to wait to receive anything from server
            HttpConnectionParams.setSoTimeout(httpRequestParams, CONNECTION_TIME);

            //Create client to establish HTTP connection + and make request to server
            HttpClient client = new DefaultHttpClient(httpRequestParams);
            HttpPost post = new HttpPost(SERVER_ADDRESS + "Register.php");

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
            //Stop progress bar displayed when AsyncTask is finish
            progressDialog.dismiss();
            //Inform activity which started process that process is finished
            userCallback.done(null);
            super.onPostExecute(aVoid);
        }
    }

    //Returns a user in AsyncTask
    public class fetchUserDataAsyncTask extends AsyncTask<Void,Void, User> {
        //Holds user data
        final User user;
        //Interface GetUserCallback
        final GetUserCallback userCallback;

        //Constructor
        public fetchUserDataAsyncTask(User user, GetUserCallback userCallback) {
            this.user = user;
            this.userCallback = userCallback;
        }

        @Override
        protected User doInBackground(Void... params) {
            ArrayList<NameValuePair> dataToSend = new ArrayList<>();
            dataToSend.add(new BasicNameValuePair("username", user.username));
            dataToSend.add(new BasicNameValuePair("password", user.password));

            HttpParams httpRequestParams = new BasicHttpParams();
            //Time to wait before the post is executed
            HttpConnectionParams.setConnectionTimeout(httpRequestParams, CONNECTION_TIME);
            //Time to wait to receive anything from server
            HttpConnectionParams.setSoTimeout(httpRequestParams, CONNECTION_TIME);

            //Create client to establish HTTP connection
            HttpClient client = new DefaultHttpClient(httpRequestParams);
            HttpPost post = new HttpPost(SERVER_ADDRESS + "FetchUserData.php");

            User returnedUser = null;
            try{
                post.setEntity(new UrlEncodedFormEntity(dataToSend));
                //Stores the data fetched from the database
                HttpResponse httpResponse = client.execute(post);

                //Get the data from the HTTP response
                HttpEntity entity = httpResponse.getEntity();
                //Convert data to string
                String result = EntityUtils.toString(entity);
                //jObject holds the user data
                JSONObject jObject = new JSONObject(result);

                if(jObject.length()==0){
                    returnedUser = null;
                }else {
                    int customer_id = jObject.getInt("customer_id");
                    String name = jObject.getString("name");
                    String email = jObject.getString("email");
                    //int customer_id = jObject.getInt();

                    returnedUser = new User(customer_id,name, email, user.username, user.password);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            return returnedUser;
        }

        @Override
        //When AsyncTask is finish - return user
        protected void onPostExecute(User returnedUser) {
            //Stop progress bar displayed when AsyncTask is finish
            progressDialog.dismiss();
            //Send the returned user to the done callback
            userCallback.done(returnedUser);
            super.onPostExecute(returnedUser);
        }
    }
}
