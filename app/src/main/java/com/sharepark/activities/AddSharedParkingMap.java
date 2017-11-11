package com.sharepark.activities;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sharepark.DateHelper;
import com.sharepark.R;
import com.sharepark.util.AppController;
import com.sharepark.util.Constants;
import com.sharepark.util.Endpoints;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AddSharedParkingMap extends AppCompatActivity implements OnMapReadyCallback, TimePickerDialog.OnTimeSetListener {

    private GoogleMap mMap;

    EditText addressShared,sharedName, sharedSlot, sharedTimeStart, sharedTimeEnd;
    Button save;

    private SharedPreferences preferences;
    SharedPreferences.Editor editor;
    String s_timeStart = "6:00:00", s_timeEnd;
    private Toolbar toolbar;
    String id;
    int time_pick;

    double lat, lng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_shared_parking_map);
        toolbar = (Toolbar) findViewById(R.id.toolBarAddSharedParking);
        toolbar.setNavigationIcon(R.drawable.md_nav_back);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
        }
        setSupportActionBar(toolbar);

        preferences = this.getSharedPreferences("com.pickapark", Context.MODE_PRIVATE);
        editor = preferences.edit();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        id = preferences.getString("id", "");



        addressShared = (EditText) findViewById(R.id.addressMap);
        sharedName = (EditText)findViewById(R.id.addSharedParkName);
        sharedSlot = (EditText)findViewById(R.id.addSharedSlot);
        sharedTimeStart = (EditText)findViewById(R.id.addSharedtimeStart);
        sharedTimeEnd = (EditText)findViewById(R.id.addSharedtimeEnd);

        save = (Button) findViewById(R.id.saveShared);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if(!addressShared.getText().toString().equals(null) && !addressShared.getText().toString().equals("")) {
                    String tags = "?name=" + sharedName.getText().toString() +
                            "&address=" + addressShared.getText().toString() +
                            "&lat=" + lat +
                            "&long=" + lng +
                            "&slot=" + sharedSlot.getText().toString() +
                            "&timeStart=" + s_timeStart +
                            "&timeEnd=" + s_timeEnd +
                            "&c_id=" + id;

                    tags = tags.replace(" ", "%20");

                    sendData(tags);
                }else{

                    Toast.makeText(AddSharedParkingMap.this, "Please click the location on the map " + addressShared.getText().toString(), Toast.LENGTH_SHORT).show();

                }


            }
        });


    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;



        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            //Show rationale and request permission.
        }


        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();

        Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
        if (location != null)
        {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 100));

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(location.getLatitude(), location.getLongitude()))
                    .zoom(15)
                    .build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(latLng).title("test"));

                addressShared.setText(getAddress(latLng.latitude, latLng.longitude));

                lat = latLng.latitude;
                lng = latLng.longitude;
            }
        });





    }

    private String getAddress(double latitude, double longitude) {
        StringBuilder result = new StringBuilder();


        try {
            Geocoder geoCoder = new Geocoder(this);
            List<Address> adds = geoCoder.getFromLocation(
                    latitude
                    , longitude
                    , 1);

            if (adds!=null && adds.size()>0) {
                Address add = adds.get(0);
                int max = add.getMaxAddressLineIndex();
                if (max!=-1) {
                    for (int i=0; i<max;i++) {
                        result.append(add.getAddressLine(i));
                        if (i != max-1){
                            result.append(", ");
                        }
                    }
                }
            }
        }
        catch (Exception ex) {
            //Log.w("asd", "geocoding_fromAndroid->"+ex.toString());
            result.append("Error: " + ex.getMessage());
        }

        return result.toString();
    }


    private void sendData(String tags) {
        //http://pickapark.tip.edu.ph/pap/sharedparking/v1/index.php/insertSharedParking?name=asd&address=asd&lat=1&long=1&slot=1&timeStart=1:00:00&timeEnd=10:00:00&c_id=123
        Log.e("link",Constants.web_app + Endpoints.INSERTSHAREDPARKING + tags);

        JsonObjectRequest jsonRequest = new JsonObjectRequest
                (Request.Method.POST, Constants.web_app + Endpoints.INSERTSHAREDPARKING + tags, null, new Response.Listener<JSONObject>() {


                    @Override
                    public void onResponse(JSONObject json) {

                        String result = null;
                        try {
                            result = json.getString("result");

                            if(result.equals("success")){
                                Log.e("INSERT RESULT", "success");
                                finish();

                            }else{
                                Log.e("INSERT RESULT", "failed");

                            }
                        } catch (JSONException e) {
                            Toast.makeText(AddSharedParkingMap.this, "Error: " + e.getMessage() , Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }

                    }
                }

                        , new Response.ErrorListener()

                {



                    @Override
                    public void onErrorResponse(VolleyError error) {

                        error.printStackTrace();


                    }
                }

                );
        AppController.getInstance().

                addToRequestQueue((jsonRequest)

                );
    }


    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int second) {

        String pickedTime = String.format("%02d:%02d:%02d", hourOfDay, minute, 00);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

        Calendar cal = Calendar.getInstance();
        if (time_pick == 1) {
            sharedTimeStart.setText(DateHelper.TO_AM_PM(pickedTime));
            s_timeStart = pickedTime;

            /*try {
                Date checkInTimes = sdf.parse(pickedTime);
                Date checkOutTimes = sdf.parse(pickedTime);
                if (checkOutTimes.before(checkInTimes)) {
                    Toast.makeText(AddSharedParkingMap.this, "You can't reserve lately", Toast.LENGTH_SHORT).show();
                } else {
                    sharedTimeStart.setText(DateHelper.TO_AM_PM(pickedTime));
                    start_time = pickedTime;
                }
            } catch (ParseException | NullPointerException e) {
                e.printStackTrace();
            }*/

        } else if (time_pick == 2) {
            try {
                Date checkInTimes = sdf.parse(s_timeStart);
                Date checkOutTimes = sdf.parse(pickedTime);
                if (checkOutTimes.before(checkInTimes)) {
                    Toast.makeText(AddSharedParkingMap.this, "You can't reserve lately", Toast.LENGTH_SHORT).show();
                } else {
                    sharedTimeEnd.setText(DateHelper.TO_AM_PM(pickedTime));
                    s_timeEnd = pickedTime;
                }
            } catch (ParseException | NullPointerException e) {
                e.printStackTrace();
            }

        }


    }



    public void timeStart(View v) {
        time_pick = 1;
        Calendar now = Calendar.getInstance();
        TimePickerDialog tpd = TimePickerDialog.newInstance(
                AddSharedParkingMap.this,
                now.get(Calendar.HOUR_OF_DAY),
                now.get(Calendar.MINUTE),
                now.get(Calendar.AM_PM),
                false
        );
        /*
        tpd.setMinTime(timeinhours,timeinminutes,00);
        tpd.setMaxTime(closing_hours,closing_minutes,00);*/
        tpd.setStartTime(now.get(Calendar.HOUR_OF_DAY), 00);
        tpd.show(getFragmentManager(), "Timepickerdialog");

    }

    public void timeEnd(View v) {
        time_pick = 2;
        Calendar now = Calendar.getInstance();
        TimePickerDialog tpd = TimePickerDialog.newInstance(
                AddSharedParkingMap.this,
                now.get(Calendar.HOUR_OF_DAY),
                now.get(Calendar.MINUTE),
                now.get(Calendar.AM_PM),
                false
        );
        /*
        tpd.setMinTime(timeinhours,timeinminutes,00);
        tpd.setMaxTime(closing_hours,closing_minutes,00);*/
        tpd.setStartTime(now.get(Calendar.HOUR_OF_DAY), 00);
        tpd.show(getFragmentManager(), "Timepickerdialog");

    }

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
