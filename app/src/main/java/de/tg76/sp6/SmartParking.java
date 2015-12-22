package de.tg76.sp6;

/**
 * Thorsten Graebner D11123994
 * This class creates the tab layout design of the app
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;


public class SmartParking extends AppCompatActivity {
   private UserLocalStore userLocalStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userLocalStore = new UserLocalStore(this);

        setContentView(R.layout.activity_smart_parking);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Create the tabLayout
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("C-Park"));
        tabLayout.addTab(tabLayout.newTab().setText("C-Finder"));
        tabLayout.addTab(tabLayout.newTab().setText("MAP"));
        tabLayout.addTab(tabLayout.newTab().setText("Favorite"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);


        //ViewPager used for swiping the activity
        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        final PagerAdapter adapter = new PagerAdapter
                (getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        Log.d("Testing", "SmartParking onCreateOptionsMenu");//delete this
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        //Logout user function in Menu
        int id = item.getItemId();
        Log.d("Testing", "SmartParking onOptionsItemSelected");//delete this
        userLocalStore.clearUserData();
        userLocalStore.setUserLoggedIn(false);
        startActivity(new Intent(this,Login.class));

        //Original - changed 19/10
       // startActivity(new Intent(this,Login.class))
       return id == R.id.action_settings || super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        Log.d("Testing", "SmartParking onContextItemSelected");//delete this
        switch (item.getItemId()){
            case R.id.action_settings:
                userLocalStore.clearUserData();
                userLocalStore.setUserLoggedIn(false);
                startActivity(new Intent(this, Login.class));
                break;
        }
        return super.onContextItemSelected(item);
    }

}