package com.sharepark.objects;

import com.orm.SugarRecord;

/**
 * Created by Mark Jansen Calderon on 9/2/2016.
 */
public class Messages extends SugarRecord{

    String mes_id;
    String p_id;
    String sender_id;
    String sender_name;
    String receiver_name;
    String time_sent;
    String seen;
    String time_seen;
    String message;


    public Messages() {
    }

    public Messages(String mes_id, String p_id, String sender_id, String sender_name, String receiver_name, String time_sent, String seen, String time_seen, String message) {
        this.mes_id = mes_id;
        this.p_id = p_id;
        this.sender_id = sender_id;
        this.sender_name = sender_name;
        this.receiver_name = receiver_name;
        this.time_sent = time_sent;
        this.seen = seen;
        this.time_seen = time_seen;
        this.message = message;
    }


    public String getMesId() {
        return mes_id;
    }

    public void setMesId(String mes_id) {
        this.mes_id = mes_id;
    }

    public String getP_id() {
        return p_id;
    }

    public void setP_id(String p_id) {
        this.p_id = p_id;
    }

    public String getSender_id() {
        return sender_id;
    }

    public void setSender_id(String sender_id) {
        this.sender_id = sender_id;
    }

    public String getSender_name() {
        return sender_name;
    }

    public void setSender_name(String sender_name) {
        this.sender_name = sender_name;
    }

    public String getReceiver_name() {
        return receiver_name;
    }

    public void setReceiver_name(String receiver_name) {
        this.receiver_name = receiver_name;
    }

    public String getTime_sent() {
        return time_sent;
    }

    public void setTime_sent(String time_sent) {
        this.time_sent = time_sent;
    }

    public String getSeen() {
        return seen;
    }

    public void setSeen(String seen) {
        this.seen = seen;
    }

    public String getTime_seen() {
        return time_seen;
    }

    public void setTime_seen(String time_seen) {
        this.time_seen = time_seen;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
