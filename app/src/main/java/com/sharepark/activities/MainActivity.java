package com.sharepark.activities;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.orm.SugarContext;
import com.sharepark.R;
import com.sharepark.fragments.NavigationDrawerFragment;
import com.sharepark.fragments.SharedParkFragment;


public class MainActivity extends AppCompatActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {


    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;



    int drawerItemToGo = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SugarContext.init(getApplicationContext());
        FacebookSdk.sdkInitialize(getApplicationContext());
        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        preferences = this.getSharedPreferences("com.pickapark", Context.MODE_PRIVATE);
        editor = preferences.edit();
        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));


        drawerItemToGo = getIntent().getIntExtra("drawerItemToGo", 0);



        if (drawerItemToGo > 0) {
            NavigationDrawerFragment.callClick(drawerItemToGo);
            drawerItemToGo = 0;
        }

        final LinearLayout about_us = (LinearLayout) findViewById(R.id.about_us);
        about_us.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent about = new Intent(MainActivity.this, AboutUs.class);
                startActivity(about);
            }
        });


    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        selectItem(position);

    }

    public void selectItem(final int position) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        final FragmentTransaction transaction = fragmentManager.beginTransaction();/*
        t
        transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);*/
        switch (position) {
            case 0:
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        transaction.replace(R.id.container, SharedParkFragment.newInstance(position + 1), "SharedParkFragment");
                        transaction.commit();
                    }
                }, 250);
                break;


        }


    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;

        }
    }


    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_main, container, false);
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((MainActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }


    }


    public void logout(View view) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyAlertDialogStyle);

        builder.setMessage("Logout")
                .setTitle("Are you sure?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        logoutnow();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }


    public void logoutnow() {

        Toast.makeText(this, "Logout Successful", Toast.LENGTH_SHORT).show();
        preferences = this.getSharedPreferences("com.pickapark", Context.MODE_PRIVATE);
        editor = preferences.edit();
        editor.clear();
        editor.apply();
        //Clear all database table
        /*if (CarItem.count(CarItem.class) > 0) {
            CarItem.deleteAll(CarItem.class);
        }
        if (ReservationItem.count(ReservationItem.class) > 0) {
            ReservationItem.deleteAll(ReservationItem.class);
        }
        if (ParkingLotItem.count(ParkingLotItem.class) > 0) {
            ParkingLotItem.deleteAll(ParkingLotItem.class);
        }
        if (MarkerSortItem.count(MarkerSortItem.class) > 0) {
            MarkerSortItem.deleteAll(MarkerSortItem.class);
        }
        if (CustomMarker.count(CustomMarker.class) > 0) {
            CustomMarker.deleteAll(CustomMarker.class);
        }
        if (ValetItem.count(ValetItem.class) > 0) {
            ValetItem.deleteAll(ValetItem.class);
        }*/
        LoginManager.getInstance().logOut();
        finish();
        Intent logout = new Intent(this, Login.class);
        logout.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(logout);
        finish();
    }


}





