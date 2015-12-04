package de.tg76.sp6;

import android.os.AsyncTask;

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
 * Created by adm_toto on 03/12/2015.
 * Asynch task
 */
public class AddDeleteFavorite extends AsyncTask<Void, Void, Void> {

    //Connection time in mill sec before disconnect
    private static final int CONNECTION_TIME = 1000*15;
    private static final String SERVER_ADDRESS1 = "http://ec2-52-17-188-91.eu-west-1.compute.amazonaws.com/StoreFavorite.php";
    private static final String SERVER_ADDRESS2 = "http://ec2-52-17-188-91.eu-west-1.compute.amazonaws.com/DeleteFavorite.php";

    public static boolean favoriteAdd = true;

    @Override
    //Run in the background when StoreUserDataAsyncTask starts - Accessing the server
    protected Void doInBackground(Void... params) {
        //Data which are send to server

        if(favoriteAdd){

            ArrayList<NameValuePair> dataToSend = new ArrayList<>();
            dataToSend.add(new BasicNameValuePair("customer_idfk", Fragment1.ncustomer_idfk));
            dataToSend.add(new BasicNameValuePair("carpark_idfk", Fragment1.carparkID));

            HttpParams httpRequestParams = new BasicHttpParams();
            //Time to wait before the post is executed
            HttpConnectionParams.setConnectionTimeout(httpRequestParams, CONNECTION_TIME);
            //Time to wait to receive anything from server
            HttpConnectionParams.setSoTimeout(httpRequestParams, CONNECTION_TIME);

            //Create client to establish HTTP connection + and make request to server
            HttpClient client = new DefaultHttpClient(httpRequestParams);
            HttpPost post = new HttpPost(SERVER_ADDRESS1);

            try{
                post.setEntity(new UrlEncodedFormEntity(dataToSend));
                client.execute(post);
            }catch (Exception e){
                e.printStackTrace();
            }
        }else {
            ArrayList<NameValuePair> dataToSend = new ArrayList<>();
            dataToSend.add(new BasicNameValuePair("favorite_id", Fragment4.deleteFavorite));

            HttpParams httpRequestParams = new BasicHttpParams();
            //Time to wait before the post is executed
            HttpConnectionParams.setConnectionTimeout(httpRequestParams, CONNECTION_TIME);
            //Time to wait to receive anything from server
            HttpConnectionParams.setSoTimeout(httpRequestParams, CONNECTION_TIME);

            //Create client to establish HTTP connection + and make request to server
            HttpClient client = new DefaultHttpClient(httpRequestParams);
            HttpPost post = new HttpPost(SERVER_ADDRESS2);

            try{
                post.setEntity(new UrlEncodedFormEntity(dataToSend));
                client.execute(post);
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        return null;
    }

    @Override
    //When AsyncTask is finish
    protected void onPostExecute(Void aVoid) {
    }
}