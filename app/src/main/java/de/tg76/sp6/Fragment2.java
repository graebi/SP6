package de.tg76.sp6;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class Fragment2 extends Fragment {

    //Declare class
    StoreLocationFragment mStoreLocationFragment;
    RequestLocationFragment mRequestLocationFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_fragment2, container, false);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //Initiate class
        mStoreLocationFragment = new StoreLocationFragment();
        mRequestLocationFragment = new RequestLocationFragment();

        //Adding fragments onto Fragment2
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.add(R.id.store_location, mStoreLocationFragment);
        transaction.add(R.id.request_location, mRequestLocationFragment).commit();

    }
}