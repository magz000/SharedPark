package com.sharepark.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.sharepark.R;
import com.sharepark.util.AppController;
import com.sharepark.util.Constants;
import com.sharepark.util.Endpoints;

import org.json.JSONException;
import org.json.JSONObject;

public class InquireSharedParking extends AppCompatActivity {

    EditText messageText;

    Button sendMessage;

    private SharedPreferences preferences;
    SharedPreferences.Editor editor;

    String my_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inquire_shared_parking);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        preferences = this.getSharedPreferences("com.pickapark", Context.MODE_PRIVATE);
        editor = preferences.edit();



        messageText = (EditText) findViewById(R.id.messageText);

        sendMessage = (Button) findViewById(R.id.sendMessage);
        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!messageText.getText().toString().equals(null) && !messageText.getText().toString().equals("")) {

                    //http://pickapark.tip.edu.ph/pap/sharedparking/v1/index.php/sendMessage?p_id=1&sender_id=122&receiver_id=115&message=test2

                    String tags = "?p_id=1&sender_id=122&receiver_id=115&message=" + messageText.getText().toString();

                    tags = tags.replace(" ", "%20");

                    sendData(tags);
                }else{

                    Toast.makeText(InquireSharedParking.this, "Please enter a message", Toast.LENGTH_SHORT).show();

                }

            }
        });
    }

    private void sendData(String tags) {

        //http://pickapark.tip.edu.ph/pap/sharedparking/v1/index.php/insertSharedParking?name=asd&address=asd&lat=1&long=1&slot=1&timeStart=1:00:00&timeEnd=10:00:00&c_id=123

        Log.e("link", Constants.web_app + Endpoints.SENDMESSAGE+ tags);

        JsonObjectRequest jsonRequest = new JsonObjectRequest
                (Request.Method.POST, Constants.web_app + Endpoints.SENDMESSAGE + tags, null, new Response.Listener<JSONObject>() {


                    @Override
                    public void onResponse(JSONObject json) {
                        String result = null;
                        try {
                            result = json.getString("result");

                            if(result.equals("success")){
                                Log.e("INSERT RESULT", "success");
                                //finish();

                            }else{
                                Log.e("INSERT RESULT", "failed");

                            }
                        } catch (JSONException e) {
                            Toast.makeText(InquireSharedParking.this, "Error: " + e.getMessage() , Toast.LENGTH_SHORT).show();
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

}
