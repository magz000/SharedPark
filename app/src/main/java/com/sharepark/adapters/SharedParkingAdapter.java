package com.sharepark.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sharepark.DateHelper;
import com.sharepark.R;
import com.sharepark.activities.MyParkingDetails;
import com.sharepark.activities.SharedParkingDetails;
import com.sharepark.objects.SharedParking;

import java.util.List;

public class SharedParkingAdapter extends RecyclerView.Adapter<SharedParkingAdapter.SharedParkingListHolder> {


    private List<SharedParking> sharedParkingList;

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private Context context;
    Intent in = new Intent();

    OnItemClickListener clickListener;

    public SharedParkingAdapter(Context context,
                                List<SharedParking> sharedParkingList) {
        this.sharedParkingList = sharedParkingList;
        this.context = context;
    }

    @Override
    public SharedParkingListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sharedparking, parent, false);
        SharedParkingListHolder viewHolder = new SharedParkingListHolder(view);

        return viewHolder;
    }

    public int getItemCount(){
        return sharedParkingList.size();
    }

    @Override
    public void onBindViewHolder(final SharedParkingListHolder holder, final int pos) {

        preferences = context.getSharedPreferences("com.pickapark", Context.MODE_PRIVATE);
        editor = preferences.edit();

        final String id = preferences.getString("id","");

        Glide.with(context).load("http://pickapark.tip.edu.ph/pap/sharedparking/images/"+sharedParkingList.get(pos).getP_image())
                .error(R.drawable.placeholder)
                .into(holder.sharedParkingImage);

        /*holder.sharedParkingName.setText(sharedParkingList.get(pos).getP_name());*/
        holder.sharedParkingAddress.setText(sharedParkingList.get(pos).getP_address());
        holder.sharedParkingStartTime.setText(DateHelper.TO_AM_PM(sharedParkingList.get(pos).getP_timeStart()));
        holder.sharedParkingEndTime.setText(DateHelper.TO_AM_PM(sharedParkingList.get(pos).getP_timeEnd()));
        holder.sharedParkingStatus.setText(sharedParkingList.get(pos).getP_status());
        holder.sharedParkingDistance.setText("1");

        holder.sharedLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle resData = new Bundle();
                resData.putInt("p_id", sharedParkingList.get(pos).getP_id());
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
                resData.putString("c_id", sharedParkingList.get(pos).getC_id());

                if(id.equals(sharedParkingList.get(pos).getC_id())) {
                    in = new Intent(context, MyParkingDetails.class);
                }else{
                    in = new Intent(context, SharedParkingDetails.class);
                }

                in.putExtras(resData);
                context.startActivity(in);


            }
        });


    }




    class SharedParkingListHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView sharedParkingImage;
        TextView sharedParkingName,sharedParkingAddress ,sharedParkingStartTime, sharedParkingEndTime, sharedParkingStatus, sharedParkingDistance;
        LinearLayout sharedLayout;


        public SharedParkingListHolder(View itemView) {
            super(itemView);

            this.sharedLayout = (LinearLayout) itemView.findViewById(R.id.gotoshared);
            this.sharedParkingImage = (ImageView) itemView.findViewById(R.id.sharedParkingImage);
            /*this.sharedParkingName = (TextView) itemView.findViewById(R.id.sharedParkingName);*/
            this.sharedParkingAddress = (TextView) itemView.findViewById(R.id.sharedParkingAddress);
            this.sharedParkingStartTime = (TextView) itemView.findViewById(R.id.sharedParkingStartTime);
            this.sharedParkingEndTime = (TextView) itemView.findViewById(R.id.sharedParkingEndTime);
            this.sharedParkingStatus = (TextView) itemView.findViewById(R.id.sharedParkingStatus);
            this.sharedParkingDistance = (TextView) itemView.findViewById(R.id.sharedParkingDistance);




        }

        @Override
        public void onClick(View v) {
            clickListener.onItemClick(v, getAdapterPosition());

        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);


    }

    public void SetOnItemClickListener(final OnItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;


    }






}
