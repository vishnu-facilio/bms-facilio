package com.facilio.bmsconsole.context;

import com.facilio.accounts.dto.User;
import com.facilio.bmsconsole.modules.ModuleBaseWithCustomFields;
import org.apache.commons.lang3.StringUtils;

public class LocationContext extends ModuleBaseWithCustomFields {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String name;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	private String street;
	public String getStreet() {
		return street;
	}
	public void setStreet(String street) {
		this.street = street;
	}
	
	private String city;
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	
	private String state;
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	
	private String zip;
	public String getZip() {
		return zip;
	}
	public void setZip(String zip) {
		this.zip = zip;
	}
	
	private String country;
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	
	private double lat = -1;
	public double getLat() {
		return lat;
	}
	public void setLat(double lat) {
		this.lat = lat;
	}
	
	private double lng = -1;
	public double getLng() {
		return lng;
	}
	public void setLng(double lng) {
		this.lng = lng;
	}
	
	private User contact;
	public User getContact() {
		return contact;
	}
	public void setContact(User contact) {
		this.contact = contact;
	}

	private String phone;
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	
	private String faxPhone;
	public String getFaxPhone() {
		return faxPhone;
	}
	public void setFaxPhone(String faxPhone) {
		this.faxPhone = faxPhone;
	}
	
	public boolean isEmpty() {
		if(StringUtils.isEmpty(street) && StringUtils.isEmpty(state) && StringUtils.isEmpty(country)
				&& StringUtils.isEmpty(city) && StringUtils.isEmpty(zip)) {
			return true;
		}
		return false;
	}
}
