package com.sharepark.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sharepark.R;
import com.sharepark.objects.Messages;

import java.util.List;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MessageListHolder> {


    private List<Messages> messageList;

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private Context context;
    Intent in = new Intent();

    OnItemClickListener clickListener;

    public MessagesAdapter(Context context,
                           List<Messages> messageList) {
        this.messageList = messageList;
        this.context = context;
    }

    @Override
    public MessageListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_messages, parent, false);
        MessageListHolder viewHolder = new MessageListHolder(view);

        return viewHolder;
    }



    public int getItemCount(){
        return messageList.size();
    }

    @Override
    public void onBindViewHolder(final MessageListHolder holder, final int pos) {

        preferences = context.getSharedPreferences("com.pickapark", Context.MODE_PRIVATE);
        editor = preferences.edit();

        holder.messageTextView.setText(messageList.get(pos).getMessage());
    }

    class MessageListHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView messageTextView;

        public MessageListHolder(View itemView) {
            super(itemView);
            this.messageTextView = (TextView) itemView.findViewById(R.id.messageTextView);
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
