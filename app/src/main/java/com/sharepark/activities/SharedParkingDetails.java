package com.sharepark.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sharepark.DateHelper;
import com.sharepark.R;
import com.sharepark.recycler.MessageRecycler;

public class SharedParkingDetails extends AppCompatActivity {

    ImageView imagePreviewShared;
    TextView openingTimeShared, closingTimeShared,sharedAddress, sharedSlots;
    FloatingActionButton fabResButton;
    private Toolbar toolbar;

    private CollapsingToolbarLayout collapsingToolbarLayoutProfile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shared_parking_details);
        toolbar = (Toolbar) findViewById(R.id.toolbarShared);
        toolbar.setNavigationIcon(R.drawable.md_nav_back);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
        }
        setSupportActionBar(toolbar);

        Intent in = getIntent();
        Bundle resData = in.getExtras();

        collapsingToolbarLayoutProfile = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout_shared);

        imagePreviewShared = (ImageView)findViewById(R.id.image_preview_shared);
        openingTimeShared = (TextView)findViewById(R.id.opening_time_shared);
        closingTimeShared = (TextView)findViewById(R.id.closing_time_shared);
        sharedAddress = (TextView)findViewById(R.id.shared_address);
        sharedSlots = (TextView)findViewById(R.id.shared_slots);

        collapsingToolbarLayoutProfile.setTitle(resData.getString("p_name"));

        Glide.with(this).load("http://pickapark.tip.edu.ph/pap/sharedparking/images/"+resData.getString("p_image"))
                .error(R.drawable.placeholder)
                .into(imagePreviewShared);

        openingTimeShared.setText(DateHelper.TO_AM_PM(resData.getString("p_timeStart")));
        closingTimeShared.setText(DateHelper.TO_AM_PM(resData.getString("p_timeEnd")));
        sharedAddress.setText(resData.getString("p_address"));
        sharedSlots.setText(resData.getString("p_slot"));


        /*resData.putInt("p_id", sharedParkingList.get(pos).getP_id());
        resData.putString("p_name", sharedParkingList.get(pos).getP_name());
        resData.putString("p_address", sharedParkingList.get(pos).getP_address());
        resData.putString("p_lat", sharedParkingList.get(pos).getP_lat());
        resData.putString("p_long", sharedParkingList.get(pos).getP_long());
        resData.putString("p_slot", sharedParkingList.get(pos).getP_slot());
        resData.putString("p_available", sharedParkingList.get(pos).getP_available());
        resData.putString("p_status", sharedParkingList.get(pos).getP_status());
        resData.putString("p_timeStart", sharedParkingList.get(pos).getP_timeStart());
        resData.putString("p_timeEnd", sharedParkingList.get(pos).getP_timeEnd());
        resData.putString("p_image", sharedParkingList.get(pos).getP_image());
        resData.putString("firstname", sharedParkingList.get(pos).getFirstname());
        resData.putString("lastname", sharedParkingList.get(pos).getLastname());
        resData.putString("contact", sharedParkingList.get(pos).getContact());
        resData.putString("c_id", sharedParkingList.get(pos).getC_id());*/



        fabResButton = (FloatingActionButton)findViewById(R.id.fabresbutton);
        fabResButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/

                startActivity(new Intent(SharedParkingDetails.this, MessageRecycler.class));
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case android.R.id.home:
                //NavUtils.navigateUpFromSameTask(this);
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
