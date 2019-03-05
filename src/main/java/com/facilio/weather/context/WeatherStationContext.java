package com.facilio.weather.context;

public class WeatherStationContext {

	
	private long id = -1;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	
	private String name;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	private Double lat;
	public Double getLat() {
		return lat;
	}
	public void setLat(Double lat) {
		this.lat = lat;
	}
	public void setLat(double lat) {
		this.lat = lat;
	}
	
	private Double lng;
	public Double getLng() {
		return lng;
	}
	public void setLng(Double lng) {
		this.lng = lng;
	}
	public void setLng(double lng) {
		this.lng = lng;
	}
	
	
}
