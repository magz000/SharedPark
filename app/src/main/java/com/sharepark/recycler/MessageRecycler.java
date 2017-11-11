package com.sharepark.recycler;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.sharepark.R;
import com.sharepark.adapters.SharedParkingAdapter;
import com.sharepark.objects.Messages;
import com.sharepark.objects.SharedParking;
import com.sharepark.util.AppController;
import com.sharepark.util.Constants;
import com.sharepark.util.Endpoints;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class MessageRecycler extends AppCompatActivity {


    private SharedPreferences preferences;
    SharedPreferences.Editor editor;

    private LinearLayout no_reservation;

    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_recycler);

        preferences = getSharedPreferences("com.pickapark", Context.MODE_PRIVATE);
        editor = preferences.edit();

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_messages);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        /*no_reservation = (LinearLayout) findViewById(R.id.no_reservation);
        no_reservation.setVisibility(View.GONE);*/


        GetOfflineData();
    }

    private void GetOfflineData() {
        if (SharedParking.count(SharedParking.class) > 0) {
            String c_id = preferences.getString("id", "1");
            //  reservationItemList = ReservationItem.findWithQuery(ReservationItem.class, "Select * from reservation_item where status in (? , ?) and restype = ?","No Show", "Out", ReservationsFragment.res_type);
            List<SharedParking> reservationItemList = SharedParking.findWithQuery(SharedParking.class,"select * from messages");
            SharedParkingAdapter adapter = new SharedParkingAdapter(this, reservationItemList);
            mRecyclerView.setAdapter(adapter);
            if (reservationItemList.isEmpty()) {
                no_reservation.setVisibility(View.VISIBLE);
            }
        } else {
            no_reservation.setVisibility(View.VISIBLE);
        }


    }

    private void getData(String tags) {

        //c_id=122&p_id=1


        JsonObjectRequest jsonRequest = new JsonObjectRequest
                (Request.Method.POST, Constants.web_app + Endpoints.GETALLMESSAGE, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject json) {

                        //onItemsLoadComplete();

                        Log.e("link", Constants.web_app + Endpoints.GETALLMESSAGE);
                        Log.e("JSON", json.toString());

                        try {

                            String result, mes_id, p_id, sender_id, sender_name, receiver_id, receiver_name, time_sent,
                                    seen, time_seen, message;



                            result = json.getString("result");

                            if(result.equals("success")){
                                Log.e("Result", result);

                                Messages.deleteAll(Messages.class);

                                JSONArray messages = json.getJSONArray("messages");

                                for( int x = 0 ; x < messages.length() ; x++ ){
                                    mes_id = messages.getJSONObject(x).getString("mes_id");
                                    p_id = messages.getJSONObject(x).getString("p_id");
                                    sender_id = messages.getJSONObject(x).getString("sender_id");
                                    sender_name = messages.getJSONObject(x).getString("sender_name");
                                    receiver_id = messages.getJSONObject(x).getString("receiver_id");
                                    receiver_name = messages.getJSONObject(x).getString("receiver_name");
                                    time_sent = messages.getJSONObject(x).getString("time_sent");
                                    seen = messages.getJSONObject(x).getString("seen");
                                    time_seen = messages.getJSONObject(x).getString("time_seen");
                                    message = messages.getJSONObject(x).getString("message");



                                    Messages m = new Messages(mes_id, p_id, sender_id, sender_name,
                                            receiver_name, time_sent, seen, time_seen, message);
                                    m.save();

                                }



                            }else{

                            }

                        } catch (android.database.sqlite.SQLiteException ignored) {

                        } catch (JSONException e) {
                            Log.e("JSONError", e.getMessage());
                            e.printStackTrace();

                        }catch(Exception e){
                            Log.e("Error", e.getMessage());

                        }


                    }
                }

                        , new Response.ErrorListener()

                {

                    @Override
                    public void onErrorResponse(VolleyError error) {



                    }
                }

                );
        AppController.getInstance().

                addToRequestQueue((jsonRequest)

                );
    }


}
