package com.sharepark.map_objects;

import com.orm.SugarRecord;

public class CustomMarker extends SugarRecord{

	String cp_id;
	String name;
	String image;
	String address;
	Double latitude;
	Double longitude;
	String type;


	public CustomMarker() {
	}

	public CustomMarker(String cp_id,String name,String image,String address, Double latitude, Double longitude, String type) {
		this.cp_id = cp_id;
		this.name = name;
		this.image = image;
		this.address = address;
		this.latitude = latitude;
		this.longitude = longitude;
		this.type = type;
	}


	public String getCustomMarkerId() {
		return cp_id;
	}

	public void setCustomMarkerId(String cp_id) {
		this.cp_id = cp_id;
	}

	public Double getCustomMarkerLatitude() {
		return latitude;
	}

	public void setCustomMarkerLatitude(Double mLatitude) {
		this.latitude = mLatitude;
	}

	public Double getCustomMarkerLongitude() {
		return longitude;
	}

	public void setCustomMarkerLongitude(Double mLongitude) {
		this.longitude = mLongitude;
	}


	public String getCustomMarkerName() {
		return name;
	}

	public void setCustomMarkerName(String name) {
		this.name = name;
	}

	public String getCustomMarkerImage() {
		return image;
	}

	public void setCustomMarkerImage(String image) {
		this.image = image;
	}

	public String getCustomMarkerAddress() {
		return address;
	}

	public void setCustomMarkerAddress(String address) {
		this.address = address;
	}


	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
