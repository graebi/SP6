package de.tg76.sp6;

/*
 * Store location (GPS), display GPS and navigate to location
 * Thorsten Graebner D11123994
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class Fragment2 extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_fragment2, container, false);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        Log.d("Testing", "Fragment2 onActivityCreated");
        //Initiate class
        StoreLocationFragment mStoreLocationFragment = new StoreLocationFragment();
        RequestLocationFragment mRequestLocationFragment = new RequestLocationFragment();

        Log.d("Testing", "Fragment2 FragmentTransaction");
        //Adding fragments onto Fragment2
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.add(R.id.store_location, mStoreLocationFragment);
        transaction.add(R.id.request_location, mRequestLocationFragment).commit();

    }

    @Override
    public void onDetach() {
        Log.d("Testing", "Fragment2 onDetach");
        super.onDetach();
    }
}