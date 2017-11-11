package com.sharepark.objects;

import com.orm.SugarRecord;

/**
 * Created by Mark Jansen Calderon on 8/23/2016.
 */
public class SharedParking extends SugarRecord {
    int p_id;
    String p_name;
    String p_address;
    String p_lat;
    String p_long;
    String p_slot;
    String p_available;
    String p_status;
    String p_timeStart;
    String p_timeEnd;
    String p_image;
    String firstname;
    String lastname;
    String contact;
    String c_id;

    public SharedParking(){

    }


    public SharedParking(int p_id, String p_name, String p_address, String p_lat, String p_long, String p_slot, String p_available, String p_status, String p_timeStart, String p_timeEnd, String p_image, String firstname, String lastname, String contact, String c_id) {
        this.p_id = p_id;
        this.p_name = p_name;
        this.p_address = p_address;
        this.p_lat = p_lat;
        this.p_long = p_long;
        this.p_slot = p_slot;
        this.p_available = p_available;
        this.p_status = p_status;
        this.p_timeStart = p_timeStart;
        this.p_timeEnd = p_timeEnd;
        this.p_image = p_image;
        this.firstname = firstname;
        this.lastname = lastname;
        this.contact = contact;
        this.c_id = c_id;
    }

    public String getC_id() {
        return c_id;
    }

    public void setC_id(String c_id) {
        this.c_id = c_id;
    }

    public int getP_id() {
        return p_id;
    }

    public void setP_id(int p_id) {
        this.p_id = p_id;
    }

    public String getP_name() {
        return p_name;
    }

    public void setP_name(String p_name) {
        this.p_name = p_name;
    }

    public String getP_address() {
        return p_address;
    }

    public void setP_address(String p_address) {
        this.p_address = p_address;
    }

    public String getP_lat() {
        return p_lat;
    }

    public void setP_lat(String p_lat) {
        this.p_lat = p_lat;
    }

    public String getP_long() {
        return p_long;
    }

    public void setP_long(String p_long) {
        this.p_long = p_long;
    }

    public String getP_slot() {
        return p_slot;
    }

    public void setP_slot(String p_slot) {
        this.p_slot = p_slot;
    }

    public String getP_available() {
        return p_available;
    }

    public void setP_available(String p_available) {
        this.p_available = p_available;
    }

    public String getP_status() {
        return p_status;
    }

    public void setP_status(String p_status) {
        this.p_status = p_status;
    }

    public String getP_timeStart() {
        return p_timeStart;
    }

    public void setP_timeStart(String p_timeStart) {
        this.p_timeStart = p_timeStart;
    }

    public String getP_timeEnd() {
        return p_timeEnd;
    }

    public void setP_timeEnd(String p_timeEnd) {
        this.p_timeEnd = p_timeEnd;
    }

    public String getP_image() {
        return p_image;
    }

    public void setP_image(String p_image) {
        this.p_image = p_image;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }
}
