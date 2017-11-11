package com.sharepark.recycler;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.sharepark.R;
import com.sharepark.adapters.SharedParkingAdapter;
import com.sharepark.objects.SharedParking;

import java.util.List;

public class TabYourSharedParking extends Fragment {


    private SharedPreferences preferences;

    private LinearLayout no_reservation;

    private RecyclerView mRecyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.shared_parking, container, false);


        preferences = getActivity().getSharedPreferences("com.pickapark", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view_shared);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        no_reservation = (LinearLayout) rootView.findViewById(R.id.no_reservation);
        no_reservation.setVisibility(View.GONE);


        GetOfflineData();
        return rootView;
    }



    private void GetOfflineData() {
        if (SharedParking.count(SharedParking.class) > 0) {
            //  reservationItemList = ReservationItem.findWithQuery(ReservationItem.class, "Select * from reservation_item where status in (? , ?) and restype = ?","No Show", "Out", ReservationsFragment.res_type);

            String c_id = preferences.getString("id", "1");

            List<SharedParking> reservationItemList = SharedParking.findWithQuery(SharedParking.class,"select * from shared_parking where cid = ?", c_id);
            SharedParkingAdapter adapter = new SharedParkingAdapter(getActivity(), reservationItemList);
            mRecyclerView.setAdapter(adapter);
            if (reservationItemList.isEmpty()) {
                no_reservation.setVisibility(View.VISIBLE);
            }
        } else {
            no_reservation.setVisibility(View.VISIBLE);
        }

    }
}