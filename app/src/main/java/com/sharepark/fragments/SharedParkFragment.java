/*
Created By: Mark Jansen DS. Calderon
Name: Reservation Fragment
Function: Shows User Reservation
          Grouped by status:
          Pending, Arrived, Out/past

Notes: N/A
*/
package com.sharepark.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.sharepark.R;
import com.sharepark.activities.AddSharedParkingMap;
import com.sharepark.activities.MainActivity;
import com.sharepark.objects.SharedParking;
import com.sharepark.recycler.TabSharedParking;
import com.sharepark.recycler.TabYourSharedParking;
import com.sharepark.util.AppController;
import com.sharepark.util.Constants;
import com.sharepark.util.Endpoints;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SharedParkFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";

    private TabLayout tabLayout;
    private View rootView;
    private ViewPagerAdapter adapter;
    private String pending, out, arrived;

    private ViewPager viewPager;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private LinearLayout loading;
    private LinearLayout no_reservation;

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;


    FloatingActionButton fab;

    public static SharedParkFragment newInstance(int sectionNumber) {
        SharedParkFragment fragment = new SharedParkFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public SharedParkFragment() {
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_sharedparking, container, false);

        preferences = getActivity().getSharedPreferences("com.pickapark", Context.MODE_PRIVATE);

        return rootView;
        
    }


    @Override
    public void onViewCreated(View v, Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);
        MainActivity mAct = (MainActivity) getActivity();
        Toolbar toolBar = (Toolbar) rootView.findViewById(R.id.toolBarReservationFragmentShared);
        mAct.setSupportActionBar(toolBar);
        toolBar.setNavigationIcon(R.drawable.ic_menu);


        loading = (LinearLayout) rootView.findViewById(R.id.loading);
        loading.setVisibility(View.GONE);

        no_reservation = (LinearLayout) rootView.findViewById(R.id.no_reservation);
        no_reservation.setVisibility(View.GONE);

        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayoutShared);
        mSwipeRefreshLayout.setColorSchemeResources(
                R.color.colorPrimaryDark,
                R.color.colorPrimary);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshItems();
            }
        });

//        fab = (FloatingActionButton)rootView.findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();*/
//
//                startActivity(new Intent(getActivity(), AddSharedParkingMap.class));
//            }
//        });


        viewPager = (ViewPager) rootView.findViewById(R.id.viewpagerShared);
        viewPager.setOffscreenPageLimit(3);
        setupViewPager(viewPager);
        getData("load");


    }

    private void refreshItems() {
        // Load items
        // ...
        getData("refresh");
        mSwipeRefreshLayout.setRefreshing(true);
        // Load complete
    }


    @Override
    public void onResume() {
        super.onResume();
        refreshItems();
    }

    private void onItemsLoadComplete() {
        // Update the adapter and notify data set changed
        // ...
        // Stop refresh animation
        mSwipeRefreshLayout.setRefreshing(false);
    }

    public void setupTabIcons() {
        tabLayout.getTabAt(0);



    }


    private void setupViewPager(ViewPager viewPager) {
        adapter = new ViewPagerAdapter(getChildFragmentManager());

        adapter.addFrag(new TabSharedParking(), "All");
        adapter.addFrag(new TabYourSharedParking(), "My Parking");
        //adapter.addFrag(new TabReservationPast(), "Past");

        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }


        });


        tabLayout = (TabLayout) rootView.findViewById(R.id.tabsShared);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();


    }


    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }

        public void setFragmentTiles(int index, String title) {
            mFragmentTitleList.set(index, title);

            Log.e("ARRAY", mFragmentTitleList.toString());
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        ((MainActivity) context).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));
    }


    private void getData(String args0) {
        if (args0.equals("load")) {
            loading.setVisibility(View.VISIBLE);
        }

        JsonObjectRequest jsonRequest = new JsonObjectRequest
                (Request.Method.POST, Constants.web_app + Endpoints.GETALLSHAREDPARKING, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject json) {

                        onItemsLoadComplete();

                        Log.e("link", Constants.web_app + Endpoints.GETALLSHAREDPARKING);
                        Log.e("JSON", json.toString());

                        try {

                            String result, p_name, p_address, p_lat, p_long, p_slot, p_available, p_status,
                                    p_timeStart, p_timeEnd, p_image, firstname, lastname, contact, c_id;
                            int p_id;


                            if(isAdded()){

                                result = json.getString("result");

                                if(result.equals("success")){
                                    Log.e("Result", result);

                                    SharedParking.deleteAll(SharedParking.class);

                                    JSONArray sharedParking = json.getJSONArray("sharedparking");

                                    for( int x = 0 ; x < sharedParking.length() ; x++ ){
                                        p_id = sharedParking.getJSONObject(x).getInt("p_id");
                                        p_name = sharedParking.getJSONObject(x).getString("p_name");
                                        p_address = sharedParking.getJSONObject(x).getString("p_address");
                                        p_lat = sharedParking.getJSONObject(x).getString("p_lat");
                                        p_long = sharedParking.getJSONObject(x).getString("p_long");
                                        p_slot = sharedParking.getJSONObject(x).getString("p_slot");
                                        p_available = sharedParking.getJSONObject(x).getString("p_available");
                                        p_status = sharedParking.getJSONObject(x).getString("p_status");
                                        p_timeStart = sharedParking.getJSONObject(x).getString("p_timeStart");
                                        p_timeEnd = sharedParking.getJSONObject(x).getString("p_timeEnd");
                                        p_image = sharedParking.getJSONObject(x).getString("p_image");
                                        firstname = sharedParking.getJSONObject(x).getString("firstname");
                                        lastname = sharedParking.getJSONObject(x).getString("lastname");
                                        contact = sharedParking.getJSONObject(x).getString("contact");
                                        c_id = sharedParking.getJSONObject(x).getString("c_id");


                                        SharedParking s = new SharedParking(p_id, p_name, p_address, p_lat, p_long, p_slot,
                                                p_available, p_status, p_timeStart, p_timeEnd, p_image,
                                                firstname, lastname, contact, c_id);
                                        s.save();

                                    }
                                    viewPager.setAdapter(adapter);


                                }else{

                                }
                            }
                        } catch (android.database.sqlite.SQLiteException ignored) {

                        } catch (JSONException e) {
                            Log.e("JSONError", e.getMessage());
                            e.printStackTrace();
                            showSnackbar();
                        }catch(Exception e){
                            Log.e("Error", e.getMessage());

                        }


                    }
                }

                        , new Response.ErrorListener()

                {

                    @Override
                    public void onErrorResponse(VolleyError error) {

                        error.printStackTrace();
                        loading.setVisibility(View.GONE);
                        showSnackbar();
                        pending = "";
                        out = "";
                        arrived = "";
                        viewPager = (ViewPager) rootView.findViewById(R.id.viewpagerShared);
                        viewPager.setOffscreenPageLimit(3);
                        setupViewPager(viewPager);

                    }
                }

                );
        AppController.getInstance().

                addToRequestQueue((jsonRequest)

                );
    }


    private void showSnackbar() {

        if (isAdded()) {
            CoordinatorLayout coordinatorLayout = (CoordinatorLayout) rootView.findViewById(R.id.coordinatorLayout);
            Snackbar snackbar = Snackbar
                    .make(coordinatorLayout, "No Internet Connection", Snackbar.LENGTH_INDEFINITE)
                    .setAction("RETRY", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            getData("refresh");
                        }
                    });
            snackbar.setActionTextColor(getResources().getColor(R.color.colorPrimary));
            View snackbarView = snackbar.getView();
            snackbarView.setBackgroundColor(Color.DKGRAY);
            TextView textView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.WHITE);
            snackbar.show();

            pending = "";
            out = "";
            arrived = "";
            viewPager = (ViewPager) rootView.findViewById(R.id.viewpagerShared);
            viewPager.setOffscreenPageLimit(3);
            setupViewPager(viewPager);

        }
    }

}