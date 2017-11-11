package com.sharepark.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.sharepark.DateHelper;
import com.sharepark.R;
import com.sharepark.util.AppController;
import com.sharepark.util.Constants;
import com.sharepark.util.Endpoints;

import org.json.JSONException;
import org.json.JSONObject;

public class MyParkingDetails extends AppCompatActivity {

    ImageView imagePreviewShared;
    TextView openingTimeShared, closingTimeShared,sharedAddress, sharedSlots, sharedStatus;
    EditText hiddenOpeningTime, hiddenClosingTime, hiddenAddress, hiddenSlots, hiddenStatus;
    Button updateBtn;
    FloatingActionButton fabResButton;
    private Toolbar toolbar;
    Intent in;
    Bundle resData;

    private CollapsingToolbarLayout collapsingToolbarLayoutProfile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_parking_details);
        toolbar = (Toolbar) findViewById(R.id.toolbarShared);
        toolbar.setNavigationIcon(R.drawable.md_nav_back);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
        }
        setSupportActionBar(toolbar);

        in = getIntent();
        resData = in.getExtras();

        collapsingToolbarLayoutProfile = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout_shared);

        imagePreviewShared = (ImageView)findViewById(R.id.image_preview_shared);
        openingTimeShared = (TextView) findViewById(R.id.opening_time_shared);
        closingTimeShared = (TextView)findViewById(R.id.closing_time_shared);
        sharedAddress = (TextView)findViewById(R.id.shared_address);
        sharedSlots = (TextView)findViewById(R.id.shared_slots);
        sharedStatus = (TextView)findViewById(R.id.shared_status);
        hiddenOpeningTime = (EditText) findViewById(R.id.hidden_opening_time);
        hiddenClosingTime = (EditText)findViewById(R.id.hidden_closing_time);
        hiddenAddress = (EditText)findViewById(R.id.hidden_address);
        hiddenSlots = (EditText)findViewById(R.id.hidden_slots);
        hiddenStatus = (EditText)findViewById(R.id.hidden_status);
        updateBtn = (Button) findViewById(R.id.update_button);

        collapsingToolbarLayoutProfile.setTitle(resData.getString("p_name"));

        Glide.with(this).load("http://pickapark.tip.edu.ph/pap/sharedparking/images/"+resData.getString("p_image"))
                .error(R.drawable.placeholder)
                .into(imagePreviewShared);

        openingTimeShared.setText(DateHelper.TO_AM_PM(resData.getString("p_timeStart")));
        closingTimeShared.setText(DateHelper.TO_AM_PM(resData.getString("p_timeEnd")));
        sharedAddress.setText(resData.getString("p_address"));
        sharedSlots.setText(resData.getString("p_slot"));
        sharedStatus.setText(resData.getString("p_status"));


        final int p_id = resData.getInt("p_id");

        Log.d("tangina", ""+p_id);


        fabResButton = (FloatingActionButton)findViewById(R.id.fabresbutton);
        fabResButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openingTimeShared.setVisibility(View.GONE);
                closingTimeShared.setVisibility(View.GONE);
                sharedAddress.setVisibility(View.GONE);
                sharedSlots.setVisibility(View.GONE);
                sharedStatus.setVisibility(View.GONE);

                hiddenOpeningTime.setText(resData.getString("p_timeStart"));
                hiddenClosingTime.setText(resData.getString("p_timeEnd"));
                hiddenAddress.setText(resData.getString("p_address"));
                hiddenSlots.setText(resData.getString("p_slot"));
                hiddenStatus.setText(resData.getString("p_status"));

                hiddenOpeningTime.setVisibility(View.VISIBLE);
                hiddenClosingTime.setVisibility(View.VISIBLE);
                hiddenAddress.setVisibility(View.VISIBLE);
                hiddenSlots.setVisibility(View.VISIBLE);
                hiddenStatus.setVisibility(View.VISIBLE);
                updateBtn.setVisibility(View.VISIBLE);

                hiddenOpeningTime.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(hiddenOpeningTime, InputMethodManager.SHOW_IMPLICIT);
            }
        });


        final String[] slot_status = getResources().getStringArray(R.array.slot_status);
        hiddenStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MaterialDialog.Builder(MyParkingDetails.this)
                        .title("Status")
                        .titleColor(ContextCompat.getColor(MyParkingDetails.this, R.color.colorPrimary))
                        .theme(Theme.LIGHT)
                        .items(slot_status)
                        .itemsCallback(new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                if (text.equals("Available")) {
                                    hiddenStatus.setText("Available");
                                } else {
                                    hiddenSlots.setText("0");
                                    hiddenStatus.setText("Unavailable");
                                }
                            }
                        }).show();
            }
        });


        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!openingTimeShared.getText().toString().equals(null) && !openingTimeShared.getText().toString().equals("")) {
                    String tags = "?timeStart=" + hiddenOpeningTime.getText().toString() +
                            "&timeEnd=" + hiddenClosingTime.getText().toString() +
                            "&slots=" + hiddenSlots.getText().toString() +
                            "&status=" + hiddenStatus.getText().toString() +
                            "&p_id=" + p_id;

                    //Log.e("p_id", p_id);

                    tags = tags.replace(" ", "%20");

                    sendData(tags);
                }else{
                    Toast.makeText(MyParkingDetails.this, "Please click the location on the map " + openingTimeShared.getText().toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void sendData(String tags) {
        //http://pickapark.tip.edu.ph/pap/sharedparking/v1/index.php/insertSharedParking?name=asd&address=asd&lat=1&long=1&slot=1&timeStart=1:00:00&timeEnd=10:00:00&c_id=123
        Log.e("link", Constants.web_app + Endpoints.UPDATEMYPARKING + tags);

        JsonObjectRequest jsonRequest = new JsonObjectRequest
                (Request.Method.POST, Constants.web_app + Endpoints.UPDATEMYPARKING + tags, null, new Response.Listener<JSONObject>() {


                    @Override
                    public void onResponse(JSONObject json) {

                        String result = null;
                        try {
                            result = json.getString("result");

                            if(result.equals("success")){
                                Log.e("INSERT RESULT", "success");

                                openingTimeShared.setVisibility(View.VISIBLE);
                                closingTimeShared.setVisibility(View.VISIBLE);
                                sharedAddress.setVisibility(View.VISIBLE);
                                sharedSlots.setVisibility(View.VISIBLE);
                                sharedStatus.setVisibility(View.VISIBLE);

                                openingTimeShared.setText(resData.getString("p_timeStart"));
                                closingTimeShared.setText(resData.getString("p_timeEnd"));
                                sharedAddress.setText(resData.getString("p_address"));
                                sharedSlots.setText(resData.getString("p_slot"));
                                sharedStatus.setText(resData.getString("p_status"));

                                hiddenOpeningTime.setVisibility(View.GONE);
                                hiddenClosingTime.setVisibility(View.GONE);
                                hiddenAddress.setVisibility(View.GONE);
                                hiddenSlots.setVisibility(View.GONE);
                                hiddenStatus.setVisibility(View.GONE);
                                updateBtn.setVisibility(View.GONE);

                                Toast.makeText(MyParkingDetails.this, "Succesfull!", Toast.LENGTH_SHORT).show();


                            }else{
                                Log.e("INSERT RESULT", "failed");

                            }
                        } catch (JSONException e) {
                            Toast.makeText(MyParkingDetails.this, "Error: " + e.getMessage() , Toast.LENGTH_SHORT).show();
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
